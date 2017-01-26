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
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.vo.VoDashboardWidget;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.domain.vo.VoManagerShop;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.vo.VoDashboardWidgetPlugin;

import java.util.*;

/**
 * User: denispavlov
 * Date: 23/09/2016
 * Time: 19:38
 */
public class VoDashboardWidgetPluginOrdersInShops implements VoDashboardWidgetPlugin {

    private final CustomerOrderService customerOrderService;
    private final ShopService shopService;

    public VoDashboardWidgetPluginOrdersInShops(final CustomerOrderService customerOrderService,
                                                final ShopService shopService) {
        this.customerOrderService = customerOrderService;
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

        final int ordersToday = this.customerOrderService.findCountByCriteria(
                Restrictions.in("shop.shopId", shops),
                Restrictions.ge("createdTimestamp", today.getTime()),
                Restrictions.ne("orderStatus", CustomerOrder.ORDER_STATUS_NONE)
        );

        if (today.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            today.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            if (date < today.get(Calendar.DATE)) {
                // monday moved us forward, so need to subtract one week
                today.add(Calendar.DAY_OF_YEAR, -7);
            }
        }

        final int ordersWeek = this.customerOrderService.findCountByCriteria(
                Restrictions.in("shop.shopId", shops),
                Restrictions.ge("createdTimestamp", today.getTime()),
                Restrictions.ne("orderStatus", CustomerOrder.ORDER_STATUS_NONE)
        );

        if (today.get(Calendar.DATE) > date) {
            // We moved one month back when going to start of the week
            today.set(Calendar.DATE, 1);
            today.add(Calendar.MONTH, 1);
        } else {
            today.set(Calendar.DATE, 1);
        }

        final int ordersMonth = this.customerOrderService.findCountByCriteria(
                Restrictions.in("shop.shopId", shops),
                Restrictions.ge("createdTimestamp", today.getTime()),
                Restrictions.ne("orderStatus", CustomerOrder.ORDER_STATUS_NONE)
        );


        final int ordersWaiting = this.customerOrderService.findCountByCriteria(
                Restrictions.in("shop.shopId", shops),
                Restrictions.eq("orderStatus", CustomerOrder.ORDER_STATUS_WAITING)
        );


        final VoDashboardWidget widget = new VoDashboardWidget();

        widget.setWidgetId("OrdersInShop");

        final Map<String, Integer> data = new HashMap<>();
        data.put("ordersToday", ordersToday);
        data.put("ordersThisWeek", ordersWeek);
        data.put("ordersThisMonth", ordersMonth);
        data.put("ordersWaiting", ordersWaiting);

        widget.setData(data);

        return widget;
    }
}
