/*
 * Copyright 2009 Inspire-Software.com
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

import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.vo.VoDashboardWidget;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.domain.vo.VoManagerRole;
import org.yes.cart.domain.vo.VoManagerShop;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.vo.VoDashboardWidgetPlugin;
import org.yes.cart.utils.DateUtils;
import org.yes.cart.utils.TimeContext;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * User: denispavlov
 * Date: 23/09/2016
 * Time: 19:38
 */
public class VoDashboardWidgetPluginOrdersInShops extends AbstractVoDashboardWidgetPluginImpl implements VoDashboardWidgetPlugin {

    private List<String> roles = Collections.emptyList();

    private final CustomerOrderService customerOrderService;
    private final ShopService shopService;

    public VoDashboardWidgetPluginOrdersInShops(final CustomerOrderService customerOrderService,
                                                final ShopService shopService,
                                                final AttributeService attributeService,
                                                final String widgetName) {
        super(attributeService, widgetName);
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
    protected void processWidgetData(final VoManager manager, final VoDashboardWidget widget, final Attribute config) {

        final Set<Long> shops = new HashSet<>();
        for (final VoManagerShop shop : manager.getManagerShops()) {
            shops.add(shop.getShopId());
            final Set<Long> subs = this.shopService.getAllShopsAndSubs().get(shop.getShopId());
            if (subs != null) {
                shops.addAll(subs);
            }
        }

        final ZonedDateTime today = TimeContext.getZonedDateTime();

        final String criteria = " where e.shop.shopId in (?1) and e.createdTimestamp >= ?2 and e.orderStatus  not in (?3)";

        final int ordersToday = this.customerOrderService.findCountByCriteria(
                criteria, shops, DateUtils.zdtAtStartOfDay(today).toInstant(),
                Arrays.asList(CustomerOrder.ORDER_STATUS_NONE, CustomerOrder.QUOTE_STATUS_NONE)
        );

        final int ordersWeek = this.customerOrderService.findCountByCriteria(
                criteria, shops, DateUtils.zdtAtStartOfWeek(today).toInstant(),
                Arrays.asList(CustomerOrder.ORDER_STATUS_NONE, CustomerOrder.QUOTE_STATUS_NONE)
        );

        final int ordersMonth = this.customerOrderService.findCountByCriteria(
                criteria, shops, DateUtils.zdtAtStartOfMonth(today).toInstant(),
                Arrays.asList(CustomerOrder.ORDER_STATUS_NONE, CustomerOrder.QUOTE_STATUS_NONE)
        );

        final String waiting = " where e.shop.shopId in (?1) and e.orderStatus in (?2)";

        final int ordersWaiting = this.customerOrderService.findCountByCriteria(
                waiting, shops, Arrays.asList(CustomerOrder.ORDER_STATUS_WAITING, CustomerOrder.ORDER_STATUS_APPROVE)
        );

        final int allocWaiting = this.customerOrderService.findAwaitingDeliveriesCount(shops,
                CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT,
                Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS, CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED));

        final int dateWaiting = this.customerOrderService.findAwaitingDeliveriesCount(shops,
                CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT,
                Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS, CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED));

        final int inventoryWaiting = this.customerOrderService.findAwaitingDeliveriesCount(shops,
                CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT,
                Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS, CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED));


        final Map<String, Integer> data = new HashMap<>();
        data.put("ordersToday", ordersToday);
        data.put("ordersThisWeek", ordersWeek);
        data.put("ordersThisMonth", ordersMonth);
        data.put("ordersWaiting", ordersWaiting);
        data.put("deliveriesWaitingAllocation", allocWaiting);
        data.put("deliveriesWaitingDate", dateWaiting);
        data.put("deliveriesWaitingInventory", inventoryWaiting);

        widget.setData(data);

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
