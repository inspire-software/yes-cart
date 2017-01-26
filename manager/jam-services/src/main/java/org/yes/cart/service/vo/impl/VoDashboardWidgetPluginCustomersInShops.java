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

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.DistinctRootEntityResultTransformer;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.domain.vo.VoDashboardWidget;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.domain.vo.VoManagerShop;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.vo.VoDashboardWidgetPlugin;

import java.util.*;

/**
 * User: denispavlov
 * Date: 25/09/2016
 * Time: 18:14
 */
public class VoDashboardWidgetPluginCustomersInShops implements VoDashboardWidgetPlugin {

    private final CustomerService customerService;
    private final ShopService shopService;

    public VoDashboardWidgetPluginCustomersInShops(final CustomerService customerService,
                                                   final ShopService shopService) {
        this.customerService = customerService;
        this.shopService = shopService;
    }

    @Override
    public boolean applicable(final VoManager manager) {
        return manager.getManagerShops().size() > 0;
    }

    @Override
    public VoDashboardWidget getWidget(final VoManager manager) {

        final Set<Long> shops = new HashSet<>();
        for (final VoManagerShop shop : manager.getManagerShops()) {
            shops.add(shop.getShopId());
            final Set<Long> subs = this.shopService.getAllShopsAndSubs().get(shop.getShopId());
            if (subs != null) {
                shops.addAll(subs);
            }
        }

        final Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        final int date = today.get(Calendar.DATE);

        final CriteriaTuner tuner = new CriteriaTuner() {
            @Override
            public void tune(final Criteria crit) {
                crit.createAlias("shops", "cshop");
                crit.setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE);
            }
        };

        final int ordersToday = this.customerService.findCountByCriteria(
                tuner,
                Restrictions.in("cshop.shop.shopId", shops),
                Restrictions.ge("createdTimestamp", today.getTime())
        );

        if (today.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            today.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            if (date < today.get(Calendar.DATE)) {
                // monday moved us forward, so need to subtract one week
                today.add(Calendar.DAY_OF_YEAR, -7);
            }
        }

        final int ordersWeek = this.customerService.findCountByCriteria(
                tuner,
                Restrictions.in("cshop.shop.shopId", shops),
                Restrictions.ge("createdTimestamp", today.getTime())
        );

        if (today.get(Calendar.DATE) > date) {
            // We moved one month back when going to start of the week
            today.set(Calendar.DATE, 1);
            today.add(Calendar.MONTH, 1);
        } else {
            today.set(Calendar.DATE, 1);
        }

        final int ordersMonth = this.customerService.findCountByCriteria(
                tuner,
                Restrictions.in("cshop.shop.shopId", shops),
                Restrictions.ge("createdTimestamp", today.getTime())
        );


        final VoDashboardWidget widget = new VoDashboardWidget();

        widget.setWidgetId("CustomersInShop");

        final Map<String, Integer> data = new HashMap<>();
        data.put("customersToday", ordersToday);
        data.put("customersThisWeek", ordersWeek);
        data.put("customersThisMonth", ordersMonth);

        widget.setData(data);

        return widget;
    }
}
