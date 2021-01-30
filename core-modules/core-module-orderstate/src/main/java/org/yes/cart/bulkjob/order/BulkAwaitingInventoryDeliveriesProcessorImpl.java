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

package org.yes.cart.bulkjob.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkjob.cron.AbstractCronJobProcessorImpl;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.Job;
import org.yes.cart.domain.entity.JobDefinition;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.impl.JobStatusListenerWithLoggerImpl;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.OrderItemAllocationException;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.utils.DateUtils;
import org.yes.cart.utils.log.Markers;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Processor that scrolls though all order deliveries that are waiting for
 * pre oder and back order items and updates those deliveries if the inventory
 * is not available.
 *
 * Last time this job runs is stored in system preferences: JOB_DELWAITINV_LR
 * So that next run we only scan inventory that has changed since last job run.
 *
 * User: denispavlov
 * Date: 07/11/2013
 * Time: 15:42
 */
public class BulkAwaitingInventoryDeliveriesProcessorImpl extends AbstractCronJobProcessorImpl
        implements BulkAwaitingInventoryDeliveriesProcessorInternal, JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(BulkAwaitingInventoryDeliveriesProcessorImpl.class);

    private CustomerOrderService customerOrderService;
    private OrderStateManager orderStateManager;

    private final JobStatusListener listener = new JobStatusListenerWithLoggerImpl(new JobStatusListenerImpl(), LOG);

    /** {@inheritDoc} */
    @Override
    public JobStatus getStatus(final String token) {
        return listener.getLatestStatus();
    }

    /** {@inheritDoc} */
    @Override
    public Pair<JobStatus, Instant> processInternal(final Map<String, Object> context, final Job job, final JobDefinition definition) {

        listener.reset();

        listener.notifyInfo("Check orders awaiting allocation start date");

        final int allocWaiting = processAwaitingOrders(null,
                CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT,
                OrderStateManager.EVT_PROCESS_ALLOCATION);

        listener.count("Allocated", allocWaiting);
        listener.notifyInfo("Transitioned {} deliveries awaiting allocation", allocWaiting);

        listener.notifyInfo("Check orders awaiting preorder start date");

        final int dateWaiting = processAwaitingOrders(null,
                CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT,
                OrderStateManager.EVT_DELIVERY_ALLOWED_TIMEOUT);


        listener.count("Released", dateWaiting);
        listener.notifyInfo("Transitioned {} deliveries awaiting preorder start date", dateWaiting);

        listener.notifyInfo("Check orders awaiting inventory");

        final int inventoryWaiting = processAwaitingOrders(null,
                    CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT,
                    OrderStateManager.EVT_DELIVERY_ALLOWED_QUANTITY);

        listener.count("Reserved", dateWaiting);
        listener.notifyInfo("Transitioned {} deliveries awaiting inventory", inventoryWaiting);

        listener.notifyCompleted();

        return new Pair<>(listener.getLatestStatus(), null);
    }

    /**
     * Get deliveries for given order and delivery state and try to push into processing.
     *
     *
     * @param productSkus          SKU's for which inventory changes since the last run
     * @param status               status of delivery
     * @param event                transition event
     *
     * @return quantity of processed deliveries
     */
    int processAwaitingOrders(final List<String> productSkus,
                              final String status,
                              final String event) {

        int cnt = 0;

        final List<Long> awaitingDeliveries = customerOrderService.findAwaitingDeliveriesIds(
                productSkus,
                status,
                Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS, CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED));


        final int total = awaitingDeliveries.size();

        try {
            for (final Long deliveryId : awaitingDeliveries) {

                try {
                    // We want to isolate delivery updates, since we want to process others if one fails
                    proxy().processDeliveryEvent(event, deliveryId);
                    cnt++;
                    listener.notifyPing("Processed " + cnt + " of " + total + " awaiting deliveries for " + status + " using event " + event);

                } catch (final OrderItemAllocationException oiaexp) {
                    // Ensure that long-term OOS items do not trigger notification on every cycle
                    proxy().processDeliveryMarkOutOfStockNotification(deliveryId);

                } catch (OrderException oexp) {

                    listener.notifyWarning("Cannot process delivery {}, caused: {}", deliveryId, oexp.getMessage());

                } catch (Exception exp) {

                    listener.notifyError("Awaiting delivery processor failed for: {}", deliveryId);
                    LOG.error(Markers.alert(), "Awaiting delivery processor failed for: " + deliveryId, exp);

                }

            }
        } catch (Exception exp){
            LOG.error(exp.getMessage(), exp);
        }

        return cnt;
    }

    /** {@inheritDoc} */
    @Override
    public void processDeliveryEvent(final String event, final long deliveryId) throws OrderException {

        final CustomerOrderDelivery delivery = customerOrderService.findDelivery(deliveryId);

        if (delivery != null && orderStateManager.fireTransition(
                    new OrderEventImpl(event, delivery.getCustomerOrder(), delivery))) {

            customerOrderService.update(delivery.getCustomerOrder());
            listener.notifyInfo("Updated customer order {} delivery {}, event {}", delivery.getCustomerOrder().getOrdernum(), delivery.getDeliveryNum(), event);

        }
    }

    /** {@inheritDoc} */
    @Override
    public void processDeliveryMarkOutOfStockNotification(final long deliveryId) {

        final CustomerOrderDelivery delivery = customerOrderService.findDelivery(deliveryId);
        if (delivery != null) {

            final String key = "OUT_OF_STOCK_NOTIFICATION:" + delivery.getDeliveryNum();

            delivery.getCustomerOrder().putValue(key, DateUtils.formatSDT(), null);
            customerOrderService.update(delivery.getCustomerOrder());
            listener.notifyWarning("Marked customer order {} delivery {} ... OOS notification sent", delivery.getCustomerOrder().getOrdernum(), delivery.getDeliveryNum());

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


    /**
     * Spring IoC.
     *
     * @param customerOrderService service
     */
    public void setCustomerOrderService(final CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
    }


    /**
     * Spring IoC.
     *
     * @param orderStateManager service
     */
    public void setOrderStateManager(final OrderStateManager orderStateManager) {
        this.orderStateManager = orderStateManager;
    }
}
