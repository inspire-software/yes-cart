/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.service.order.impl.handler.delivery.OrderDeliveryStatusUpdate;
import org.yes.cart.util.log.Markers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
public class OrderDeliveryInfoUpdateProcessorImpl implements OrderDeliveryInfoUpdateProcessorInternal {

    private static final Logger LOG = LoggerFactory.getLogger(OrderDeliveryInfoUpdateProcessorImpl.class);

    private final CustomerOrderService customerOrderService;
    private final OrderStateManager orderStateManager;

    private List<Iterator<OrderDeliveryStatusUpdate>> dataFeeds = new ArrayList<Iterator<OrderDeliveryStatusUpdate>>();

    public OrderDeliveryInfoUpdateProcessorImpl(final CustomerOrderService customerOrderService,
                                                final OrderStateManager orderStateManager) {
        this.customerOrderService = customerOrderService;
        this.orderStateManager = orderStateManager;
    }


    @Override
    public void run() {


        LOG.info("Process delivery info updates");

        for (final Iterator<OrderDeliveryStatusUpdate> dataFeed : dataFeeds) {

            while (dataFeed.hasNext()) {

                final OrderDeliveryStatusUpdate update = dataFeed.next();

                try {

                    proxy().processDeliveryUpdate(update);

                } catch (Exception exp) {

                    LOG.error(Markers.alert(), "Delivery update processor failed for: " + update, exp);

                }

            }

        }

        LOG.info("Process delivery info updates ... completed");

    }


    /** {@inheritDoc} */
    @Override
    public void processDeliveryUpdate(final OrderDeliveryStatusUpdate update) throws OrderException {

        if (update != null) {

            final CustomerOrder order = this.customerOrderService.findByReference(update.getOrderNumber());

            if (order != null && this.orderStateManager.fireTransition(
                    new OrderEventImpl(OrderStateManager.EVT_DELIVERY_UPDATE, order, null, Collections.singletonMap("update", update)))) {

                this.customerOrderService.update(order);

            }

        }

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


}
