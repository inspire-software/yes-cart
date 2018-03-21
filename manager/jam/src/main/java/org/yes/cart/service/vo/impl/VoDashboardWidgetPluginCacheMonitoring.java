/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.service.vo.impl;

import org.yes.cart.bulkjob.impl.BulkJobAutoContextImpl;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.dto.impl.CacheInfoDTO;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoDashboardWidget;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.domain.vo.VoManagerRole;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.cluster.ClusterService;
import org.yes.cart.service.vo.VoDashboardWidgetPlugin;
import org.yes.cart.service.vo.VoDashboardWidgetService;
import org.yes.cart.service.async.AsyncContextFactory;

import java.util.*;

/**
 * User: denispavlov
 * Date: 24/10/2016
 * Time: 08:31
 */
public class VoDashboardWidgetPluginCacheMonitoring implements VoDashboardWidgetPlugin {

    private List<String> roles = Collections.emptyList();

    private ClusterService clusterService;
    private AsyncContextFactory asyncContextFactory;


    @Override
    public boolean applicable(final VoManager manager) {
        if (manager.getManagerShops().size() > 0) {
            for (final VoManagerRole role : manager.getManagerRoles()) {
                if (roles.contains(role.getCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public VoDashboardWidget getWidget(final VoManager manager) {

        final VoDashboardWidget widget = new VoDashboardWidget();
        widget.setWidgetId("CacheOverview");

        boolean hasHotCaches = false;
        final List<MutablePair<String, Integer>> data = new ArrayList<>();
        try {
            final Map<String, Object> param = new HashMap<>();
            param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
            Map<String, List<CacheInfoDTO>> caches = clusterService.getCacheInfo(createCtx(param));
            for (final String node : caches.keySet()) {

                int counter = 0;

                for (final CacheInfoDTO cache : caches.get(node)) {
                    // if we have max size setting and we are more than 75%, report as hot cache
                    if (cache.getInMemorySizeMax() > 0 &&
                            cache.getCacheSize() > (cache.getInMemorySizeMax() - cache.getInMemorySizeMax() / 4)) {
                        counter++;
                        hasHotCaches = true;
                    }
                }

                data.add(new MutablePair<>(node, counter));

            }

        } catch (Exception exp) {
            // ignore

        }

        final Map<String, Object> wData = new HashMap<>();
        wData.put("hasHotCaches", hasHotCaches);
        wData.put("caches", data);
        widget.setData(wData);

        return widget;
    }

    /**
     * Spring IoC.
     *
     * @param roles roles for accessing this widget
     */
    public void setRoles(final List<String> roles) {
        this.roles = roles;
    }

    /**
     * Spring IoC.
     *
     * @param clusterService cluster
     */
    public void setClusterService(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    /**
     * Spring IoC.
     *
     * @param dashboardWidgetService dashboard service
     */
    public void setDashboardWidgetService(VoDashboardWidgetService dashboardWidgetService) {
        dashboardWidgetService.registerWidgetPlugin(this);
    }

    /**
     * Spring IoC.
     *
     * @param asyncContextFactory async context factory
     */
    public void setAsyncContextFactory(AsyncContextFactory asyncContextFactory) {
        this.asyncContextFactory = asyncContextFactory;
    }

    private AsyncContext createCtx(final Map<String, Object> param) {
        try {
            // This is manual access via Admin
            return asyncContextFactory.getInstance(param);
        } catch (IllegalStateException exp) {
            // This is auto access with thread local
            return new BulkJobAutoContextImpl(param);
        }
    }
}
