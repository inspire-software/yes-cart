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

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.vo.VoDashboardWidget;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.domain.vo.VoManagerRole;
import org.yes.cart.domain.vo.VoManagerShop;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.vo.VoDashboardWidgetPlugin;
import org.yes.cart.util.DateUtils;
import org.yes.cart.util.TimeContext;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * User: denispavlov
 * Date: 23/09/2016
 * Time: 19:38
 */
public class VoDashboardWidgetPluginOrdersInShops implements VoDashboardWidgetPlugin {

    private List<String> roles = Collections.emptyList();

    private final CustomerOrderService customerOrderService;
    private final ShopService shopService;

    public VoDashboardWidgetPluginOrdersInShops(final CustomerOrderService customerOrderService,
                                                final ShopService shopService) {
        this.customerOrderService = customerOrderService;
        this.shopService = shopService;
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

        final Set<Long> shops = new HashSet<>();
        for (final VoManagerShop shop : manager.getManagerShops()) {
            shops.add(shop.getShopId());
            final Set<Long> subs = this.shopService.getAllShopsAndSubs().get(shop.getShopId());
            if (subs != null) {
                shops.addAll(subs);
            }
        }

        final ZonedDateTime today = TimeContext.getZonedDateTime();

        final String criteria = " where e.shop.shopId in (?1) and e.createdTimestamp >= ?2 and e.orderStatus <> ?3";

        final int ordersToday = this.customerOrderService.findCountByCriteria(
                criteria, shops, DateUtils.zdtAtStartOfDay(today).toInstant(), CustomerOrder.ORDER_STATUS_NONE
        );

        final int ordersWeek = this.customerOrderService.findCountByCriteria(
                criteria, shops, DateUtils.zdtAtStartOfWeek(today).toInstant(), CustomerOrder.ORDER_STATUS_NONE
        );

        final int ordersMonth = this.customerOrderService.findCountByCriteria(
                criteria, shops, DateUtils.zdtAtStartOfMonth(today).toInstant(), CustomerOrder.ORDER_STATUS_NONE
        );

        final String waiting = " where e.shop.shopId in (?1) and e.orderStatus in (?2)";

        final int ordersWaiting = this.customerOrderService.findCountByCriteria(
                waiting, shops, Arrays.asList(CustomerOrder.ORDER_STATUS_WAITING, CustomerOrder.ORDER_STATUS_APPROVE)
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

    /**
     * Spring IoC.
     *
     * @param roles roles for accessing this widget
     */
    public void setRoles(final List<String> roles) {
        this.roles = roles;
    }

}
