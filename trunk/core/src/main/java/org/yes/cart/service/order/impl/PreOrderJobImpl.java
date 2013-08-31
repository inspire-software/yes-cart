/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.util.ShopCodeContext;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Simple quartz job, which will try to get orders, which are awaiting for beginning of the sales.
 */
public class PreOrderJobImpl extends QuartzJobBean implements StatefulJob {

    final Logger log = ShopCodeContext.getLog(this);

    protected void executeInternal(final JobExecutionContext context) throws JobExecutionException {

        final CustomerOrderService customerOrderService = (CustomerOrderService) context.getMergedJobDataMap().get("customerOrderService");
        final OrderStateManager orderStateManager = (OrderStateManager) context.getMergedJobDataMap().get("orderStateManager");
        final SkuWarehouseService skuWarehouseService = (SkuWarehouseService)  context.getMergedJobDataMap().get("skuWarehouseService");

        final Date now = new Date();
        final Date lastRun = (Date) context.getJobDetail().getJobDataMap().get("lastRun");


        log.info("Check orders awaiting preorder start date");

        final int dateWaiting = processAwaitingOrders(
                null,
                CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT,
                OrderStateManager.EVT_DELIVERY_ALLOWED_TIMEOUT,
                customerOrderService,
                orderStateManager);

        log.info("Transitioned {} deliveries awaiting preorder start date", dateWaiting);

        final List<Long> productSkus = skuWarehouseService.findProductSkuForWhichInventoryChangedAfter(lastRun);

        if (productSkus != null && !productSkus.isEmpty()) {
            log.info("Check for awaiting orders for SKUs {}", productSkus);

            final int inventoryWaiting = processAwaitingOrders(
                    productSkus,
                    CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT,
                    OrderStateManager.EVT_DELIVERY_ALLOWED_QUANTITY,
                    customerOrderService,
                    orderStateManager);

            log.info("Transitioned {} deliveries awaiting inventory", inventoryWaiting);

        }

        context.getJobDetail().getJobDataMap().put("lastRun", now);

    }

    /**
     * Get deliveries for given order and delivery state and try to push into processing.
     *
     *
     * @param productSkus          SKU's for which inventory changes since the last run
     * @param status               status of delivery
     * @param event                what event to fore
     * @param customerOrderService customer order service
     * @param orderStateManager    order state manager
     * @return quantity of processed deliveries
     */
    int processAwaitingOrders(final List<Long> productSkus,
                              final String status,
                              final String event,
                              final CustomerOrderService customerOrderService,
                              final OrderStateManager orderStateManager) {

        int cnt = 0;

        final List<CustomerOrderDelivery> waitForDate = customerOrderService.findAwaitingDeliveries(
                productSkus,
                status,
                Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS, CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED));


        for (CustomerOrderDelivery delivery : waitForDate) {
            try {
                if (orderStateManager.fireTransition(
                        new OrderEventImpl(event, delivery.getCustomerOrder(), delivery)
                )) {

                    customerOrderService.update(delivery.getCustomerOrder());
                    log.info("Updated customer order {} delivery {}", delivery.getCustomerOrder().getOrdernum(), delivery.getDeliveryNum());
                    cnt++;

                }


            } catch (OrderException e) {
                log.error("Cannot process delivery " + delivery.getDeliveryNum());
            }

        }

        return cnt;
    }

}
