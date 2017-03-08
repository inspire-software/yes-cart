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

import org.hibernate.criterion.Restrictions;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.vo.VoDashboardWidget;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.domain.vo.VoManagerRole;
import org.yes.cart.domain.vo.VoManagerShop;
import org.yes.cart.service.cluster.ClusterService;
import org.yes.cart.service.domain.MailService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.vo.VoDashboardWidgetPlugin;
import org.yes.cart.service.vo.VoDashboardWidgetService;
import org.yes.cart.web.service.ws.client.AsyncContextFactory;

import java.util.*;

/**
 * User: denispavlov
 * Date: 15/02/2017
 * Time: 11:24
 */
public class VoDashboardWidgetPluginAlerts implements VoDashboardWidgetPlugin {

    private final List<String> roles = Arrays.asList("ROLE_SMADMIN", "ROLE_SMSHOPADMIN");

    private final ShopService shopService;
    private final MailService mailService;

    private ClusterService clusterService;
    private AsyncContextFactory asyncContextFactory;

    public VoDashboardWidgetPluginAlerts(final ShopService shopService,
                                         final MailService mailService) {
        this.shopService = shopService;
        this.mailService = mailService;
    }

    @Override
    public boolean applicable(final VoManager manager) {
        if (manager.getManagerShops().size() > 0) {
            for (final VoManagerRole role : manager.getManagerRoles()) {
                if (roles.contains(role.getCode())) {
                    return manager.getManagerShops().size() > 0;
                }
            }
        }
        return false;
    }

    @Override
    public VoDashboardWidget getWidget(final VoManager manager) {

        final Set<String> shops = new HashSet<>();
        for (final VoManagerShop shop : manager.getManagerShops()) {
            shops.add(this.shopService.getById(shop.getShopId()).getCode());
            final Set<Long> subs = this.shopService.getAllShopsAndSubs().get(shop.getShopId());
            if (subs != null) {
                shops.add(this.shopService.getById(shop.getShopId()).getCode());
            }
            for (final VoManagerRole role : manager.getManagerRoles()) {
                if ("ROLE_SMADMIN".equals(role.getCode())) {
                    shops.add("DEFAULT");
                }
            }
        }

        final VoDashboardWidget widget = new VoDashboardWidget();
        widget.setWidgetId("Alerts");

        final List<MutablePair<String, Integer>> data = new ArrayList<>();

        // Mail alert
        final int mailCount = this.mailService.findCountByCriteria(Restrictions.in("shopCode", shops));
        if (mailCount > 0) {
            data.add(MutablePair.of("mailQueue", mailCount));
        }

        try {
            final List<Pair<String, String>> alerts = this.clusterService.getAlerts(this.asyncContextFactory.getInstance());
            for (final Pair<String, String> alert : alerts) {
                if (shops.contains(alert.getSecond())) {
                    data.add(MutablePair.of(alert.getFirst(), 1));
                }
            }
        } catch (Exception exp) {
            // Do nothing
        }

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
     * @param asyncContextFactory async context factory
     */
    public void setAsyncContextFactory(AsyncContextFactory asyncContextFactory) {
        this.asyncContextFactory = asyncContextFactory;
    }

}
