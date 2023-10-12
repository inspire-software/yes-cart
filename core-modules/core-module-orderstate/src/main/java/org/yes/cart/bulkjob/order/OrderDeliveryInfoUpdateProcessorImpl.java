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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkjob.cron.AbstractCronJobProcessorImpl;
import org.yes.cart.domain.entity.CustomerOrder;
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
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.service.order.impl.handler.delivery.OrderDeliveryStatusUpdate;

import java.time.Instant;
import java.util.*;

/**
 * Generic processor for auto delivery info updates. This processor relies on data feed providers that
 * implement {@link Iterator} interface and iterate over OrderDeliveryStatusUpdate object. Each delivery
 * status update object represents an update on one of more order lines. These updates are then fed into
 * the order state machine to update the order information and transition deliveries (and possibly order
 * itself) to next stages of processing.
 *
 * User: denispavlov
 * Date: 17/02/2017
 * Time: 18:04
 */
public class OrderDeliveryInfoUpdateProcessorImpl extends AbstractCronJobProcessorImpl
        implements OrderDeliveryInfoUpdateProcessorInternal, JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(OrderDeliveryInfoUpdateProcessorImpl.class);

    private CustomerOrderService customerOrderService;
    private OrderStateManager orderStateManager;

    private List<Iterator<OrderDeliveryStatusUpdate>> dataFeeds = new ArrayList<>();

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

        for (final Iterator<OrderDeliveryStatusUpdate> dataFeed : dataFeeds) {

            try {

                while (dataFeed.hasNext()) {

                    final OrderDeliveryStatusUpdate update = dataFeed.next();

                    try {

                        proxy().processDeliveryUpdate(update);

                        listener.notifyInfo("Processed delivery update for order {}", update.getOrderNumber());
                        listener.count("delivery updates");

                    } catch (Exception exp) {

                        listener.notifyError("Delivery update processor failed for: {}", exp, dataFeed.getClass(), update);

                    }

                }

            } catch (Exception fexp) {

                listener.notifyError("Unable to process feed {}, caused by: {}", fexp, dataFeed.getClass(), fexp.getMessage());

            }

        }

        listener.notifyCompleted();

        return new Pair<>(listener.getLatestStatus(), null);

    }


    /** {@inheritDoc} */
    @Override
    public void processDeliveryUpdate(final OrderDeliveryStatusUpdate update) throws OrderException {

        if (update != null) {

            final CustomerOrder order = resolveOrder(update);

            if (order != null && this.orderStateManager.fireTransition(
                    new OrderEventImpl(OrderStateManager.EVT_DELIVERY_UPDATE, order, null, Collections.singletonMap("update", update)))) {

                this.customerOrderService.update(order);

            }

        }

    }

    CustomerOrder resolveOrder(final OrderDeliveryStatusUpdate update) {
        if (StringUtils.isNotBlank(update.getOrderNumber())) {
            return this.customerOrderService.findByReference(update.getOrderNumber());
        } else if (StringUtils.isNotBlank(update.getDeliveryNumber())) {
            return this.customerOrderService.findByDeliveryReference(update.getDeliveryNumber());
        }
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public void registerDataFeed(final Iterator<OrderDeliveryStatusUpdate> dataFeed) {
        this.dataFeeds.add(dataFeed);
    }

    private OrderDeliveryInfoUpdateProcessorInternal proxy;

    OrderDeliveryInfoUpdateProcessorInternal proxy() {
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
    public OrderDeliveryInfoUpdateProcessorInternal getSelfProxy() {
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
