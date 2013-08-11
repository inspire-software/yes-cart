/*
 * Copyright 2013 Igor Azarnyi, Denys Pavlov
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
package org.yes.cart.service.order.impl;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.slf4j.Logger;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.util.ShopCodeContext;

import java.util.Date;
import java.util.List;

/**
 * Simple quartz job, which will try to get orders, which are awaiting for beginning of the sales.
 */
public class PreOrderJobImpl extends QuartzJobBean implements StatefulJob {

    final Logger log = ShopCodeContext.getLog(this);

    protected void executeInternal(final JobExecutionContext context) throws JobExecutionException {

        CustomerOrderService customerOrderService = (CustomerOrderService) context.getMergedJobDataMap().get("customerOrderService");
        OrderStateManager orderStateManager = (OrderStateManager) context.get("orderStateManager");

        log.info("Check for awaiting orders");

        processAwaitingOrders(
                CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT,
                OrderStateManager.EVT_DELIVERY_ALLOWED_TIMEOUT,
                customerOrderService,
                orderStateManager);

        processAwaitingOrders(
                CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT,
                OrderStateManager.EVT_DELIVERY_ALLOWED_QUANTITY,
                customerOrderService,
                orderStateManager);


    }

    /**
     * Get deliveries for given order and delivery state and try to push into porcessing.
     *
     * @param status               status of delivery
     * @param event                what event to fore
     * @param customerOrderService customer order service
     * @param orderStateManager    order state manager
     * @return quantity of processed deliveries
     */
    int processAwaitingOrders(final String status,
                              final String event,
                              final CustomerOrderService customerOrderService,
                              final OrderStateManager orderStateManager) {

        int cnt = 0;

        final List<CustomerOrderDelivery> waitForDate = customerOrderService.findAwaitingDeliveries(
                null,
                status,
                CustomerOrder.ORDER_STATUS_IN_PROGRESS);


        for (CustomerOrderDelivery delivery : waitForDate) {
            try {
                if (orderStateManager.fireTransition(
                        new OrderEventImpl(event, delivery.getCustomerOrder(), delivery)
                )) {

                    customerOrderService.update(delivery.getCustomerOrder());
                    cnt++;

                }


            } catch (OrderException e) {
                log.error("Cannot process delivery " + delivery.getDeliveryNum());
            }

        }

        return cnt;
    }

}
