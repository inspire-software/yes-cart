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
import org.yes.cart.cluster.node.Node;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.vo.VoDashboardWidget;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.domain.vo.VoManagerRole;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.cluster.ClusterService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.vo.VoDashboardWidgetPlugin;
import org.yes.cart.service.vo.VoDashboardWidgetService;
import org.yes.cart.web.service.ws.client.AsyncContextFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 24/10/2016
 * Time: 18:01
 */
public class VoDashboardWidgetPluginReindex implements VoDashboardWidgetPlugin {


    private final List<String> roles = Arrays.asList("ROLE_SMADMIN", "ROLE_SMSHOPADMIN");

    private ClusterService clusterService;
    private AsyncContextFactory asyncContextFactory;

    private ProductService productService;


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
        widget.setWidgetId("ReindexOverview");

        final Map<String, Object> data = new HashMap<>();
        try {

            final Map<String, Object> param = new HashMap<String, Object>();
            param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_TIMEOUT_MS);
            final List<Node> cluster = clusterService.getClusterInfo(createCtx(param));

            final StringBuilder ftNodes = new StringBuilder();
            for (final Node node : cluster) {

                if (!node.isFtIndexDisabled()) {
                    if (ftNodes.length() > 0) {
                        ftNodes.append(", ");
                    }
                    ftNodes.append(node.getId());
                }

            }

            if (ftNodes.length() > 0) {
                data.put("ftNodes", ftNodes.toString());
            } else {
                data.put("ftNodes", "-");
            }

        } catch (Exception exp) {
            // ignore
            data.put("ftNodes", "-");
        }

        final Pair<Integer, Integer> totalAndActive = this.productService.getProductQtyAll();

        data.put("productCountTotal", totalAndActive.getFirst());
        data.put("productCountActive", totalAndActive.getSecond());

        widget.setData(data);

        return widget;
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
     * @param productService product service
     */
    public void setProductService(final ProductService productService) {
        this.productService = productService;
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
