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

package org.yes.cart.bulkjob.order;

import org.slf4j.Logger;
import org.yes.cart.bulkjob.cron.AbstractLastRunDependentProcessorImpl;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.RuntimeAttributeService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.util.ShopCodeContext;

import java.util.*;

/**
 * Processor that scrolls though all order deliveries that are waiting for
 * pre oder and back order items and updates those deliveries if the inventory
 * is not available.
 *
 * Last time this job runs is stored in system preferences: JOB_DEL_WAITING_INV_LAST_RUN
 * So that next run we only scan inventory that has changed since last job run.
 *
 * User: denispavlov
 * Date: 07/11/2013
 * Time: 15:42
 */
public class BulkAwaitingInventoryDeliveriesProcessorImpl extends AbstractLastRunDependentProcessorImpl
        implements BulkAwaitingInventoryDeliveriesProcessorInternal {

    private static final String LAST_RUN_PREF = "JOB_DEL_WAITING_INV_LAST_RUN";

    private final CustomerOrderService customerOrderService;
    private final OrderStateManager orderStateManager;
    private final SkuWarehouseService skuWarehouseService;

    public BulkAwaitingInventoryDeliveriesProcessorImpl(final CustomerOrderService customerOrderService,
                                                        final OrderStateManager orderStateManager,
                                                        final SkuWarehouseService skuWarehouseService,
                                                        final SystemService systemService,
                                                        final RuntimeAttributeService runtimeAttributeService) {
        super(systemService, runtimeAttributeService);
        this.customerOrderService = customerOrderService;
        this.orderStateManager = orderStateManager;
        this.skuWarehouseService = skuWarehouseService;
    }


    /** {@inheritDoc} */
    @Override
    protected String getLastRunPreferenceAttributeName() {
        return LAST_RUN_PREF;
    }

    /** {@inheritDoc} */
    @Override
    protected void doRun(final Date lastRun) {

        final Logger log = ShopCodeContext.getLog(this);

        final long start = System.currentTimeMillis();

        log.info("Check orders awaiting allocation start date");

        final int allocWaiting = processAwaitingOrders(log, null,
                CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT,
                OrderStateManager.EVT_PROCESS_ALLOCATION);

        log.info("Transitioned {} deliveries awaiting allocation", allocWaiting);

        log.info("Check orders awaiting preorder start date");

        final int dateWaiting = processAwaitingOrders(log, null,
                CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT,
                OrderStateManager.EVT_DELIVERY_ALLOWED_TIMEOUT);


        log.info("Transitioned {} deliveries awaiting preorder start date", dateWaiting);

        final List<String> productSkus = skuWarehouseService.findProductSkuForWhichInventoryChangedAfter(lastRun);

        if (productSkus != null && !productSkus.isEmpty()) {

            log.info("Inventory changed for {} SKU", productSkus.size());

            final int batch = 100;
            int skustart = 0;

            while (true) {

                final List<String> waitingSkus = buildSkuBatch(productSkus, skustart, batch);
                if (waitingSkus.isEmpty()) {
                    break;
                }

                log.info("Inventory changed for {} preorder/backorder SKUs: {}", waitingSkus.size(), waitingSkus);

                final int inventoryWaiting = processAwaitingOrders(log, productSkus,
                        CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT,
                        OrderStateManager.EVT_DELIVERY_ALLOWED_QUANTITY);

                log.info("Transitioned {} deliveries awaiting inventory", inventoryWaiting);

                skustart += batch; // next batch

            }

        }

        final long finish = System.currentTimeMillis();

        final long ms = (finish - start);

        log.info("Check orders awaiting preorder start date ... completed in {}s", (ms > 0 ? ms / 1000 : 0));

    }

    /*
        Need to batch the SKU inventory updates so that we do not create query with 000's of sku in SQL 'in' clause
        This could happen if the inventory update will have huge amount of updates, so the recommendation is to do
        inventory import incrementally
     */
    List<String> buildSkuBatch(final List<String> original, final int start, final int batch) {

        if (start >= original.size()) {
            return Collections.emptyList();
        }

        if (start + batch >= original.size()) {
            return original.subList(start, original.size());
        }

        return original.subList(start, start + batch);

    }

    /**
     * Get deliveries for given order and delivery state and try to push into processing.
     *
     *
     * @param productSkus          SKU's for which inventory changes since the last run
     * @param status               status of delivery
     * @param event                what event to look for
     *
     * @return quantity of processed deliveries
     */
    int processAwaitingOrders(final Logger log,
                              final List<String> productSkus,
                              final String status,
                              final String event) {

        int cnt = 0;

        final List<Long> awaitingDeliveries = customerOrderService.findAwaitingDeliveriesIds(
                productSkus,
                status,
                Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS, CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED));


        try {
            for (final Long deliveryId : awaitingDeliveries) {

                try {
                    // We want to isolate delivery updates, since we want to process others if one fails
                    proxy().processDeliveryEvent(event, deliveryId);

                } catch (OrderException oexp) {

                    log.warn("Cannot process delivery " + deliveryId, oexp);

                } catch (Exception exp) {

                    log.error("Cannot process delivery " + deliveryId, exp);

                }

            }
        } catch (Exception exp){
            log.error(exp.getMessage(), exp);
        }

        return cnt;
    }

    /** {@inheritDoc} */
    @Override
    public void processDeliveryEvent(final String event, final long deliveryId) throws OrderException {

        final Logger log = ShopCodeContext.getLog(this);

        final CustomerOrderDelivery delivery = customerOrderService.findDelivery(deliveryId);

        if (delivery != null && orderStateManager.fireTransition(
                    new OrderEventImpl(event, delivery.getCustomerOrder(), delivery))) {

                customerOrderService.update(delivery.getCustomerOrder());
                log.info("Updated customer order {} delivery {}", delivery.getCustomerOrder().getOrdernum(), delivery.getDeliveryNum());

        }
    }

    private BulkAwaitingInventoryDeliveriesProcessorInternal proxy;

    BulkAwaitingInventoryDeliveriesProcessorInternal proxy() {
        if (proxy == null) {
            proxy = getSelfProxy();
        }
        return proxy;
    }

    /**
     * Spring IoC.
     *
     * @return self proxy
     */
    public BulkAwaitingInventoryDeliveriesProcessorInternal getSelfProxy() {
        return null;
    }


}
