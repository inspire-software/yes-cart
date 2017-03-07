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

package org.yes.cart.service.order.impl.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.text.MessageFormat;
import java.util.*;

/**
 * Perform separate processing of order deliveries.
 * <p/>
 * Delivery types and corresponding events are defined by LinkedHashMap, so the priority is preserved.
 * The order is: 1: D1 standard, 2: D2 pre order, 3: D3 backorder, 4: D5 mixed, 5: D4 electronic
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentOkOrderEventHandlerImpl extends AbstractOrderEventHandlerImpl implements OrderEventHandler, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentOkOrderEventHandlerImpl.class);

    private OrderStateManager orderStateManager = null;
    private ApplicationContext applicationContext;

    private static final Map<String, String> GROUP_TRIGGER_MAP = new LinkedHashMap<String, String>() {{
        // Need to use LinkedHashMap since it preserves the order of entries when iterating over the map
        put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, OrderStateManager.EVT_PROCESS_ALLOCATION_WAIT);
        put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, OrderStateManager.EVT_PROCESS_TIME_WAIT);
        put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, OrderStateManager.EVT_PROCESS_INVENTORY_WAIT);
        put(CustomerOrderDelivery.MIX_DELIVERY_GROUP, OrderStateManager.EVT_PROCESS_TIME_WAIT);
        put(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, OrderStateManager.EVT_RELEASE_TO_SHIPMENT);
    }};


    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private synchronized OrderStateManager getOrderStateManager() {
        if (orderStateManager == null) {
            orderStateManager = (OrderStateManager) applicationContext.getBean("orderStateManager");
        }
        return orderStateManager;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) throws OrderException {
        synchronized (OrderEventHandler.syncMonitor) {
            handleInternal(orderEvent);
            CustomerOrder order = orderEvent.getCustomerOrder();

            final Set<CustomerOrderDelivery> allDeliveriesToConsider = new HashSet<CustomerOrderDelivery>(order.getDelivery());

            // go through map entries (since it is linked hash map order is preserved)
            for (final Map.Entry<String, String> deliveryGroupEvent : GROUP_TRIGGER_MAP.entrySet()) {

                final Iterator<CustomerOrderDelivery> allIt = allDeliveriesToConsider.iterator();
                while (allIt.hasNext()) {
                    // for all deliveries to consider
                    final CustomerOrderDelivery delivery = allIt.next();

                    if (deliveryGroupEvent.getKey().equals(delivery.getDeliveryGroup())) {

                        final String eventId = deliveryGroupEvent.getValue();

                        if (LOG.isInfoEnabled()) {
                            LOG.info(MessageFormat.format("Delivery {0} for order {1} event {2}",
                                    delivery.getDeliveryNum(), order.getOrdernum(), eventId));
                        }
                        final OrderEvent deliveryEvent = new OrderEventImpl(orderEvent, eventId, order, delivery);
                        getOrderStateManager().fireTransition(deliveryEvent);

                        allIt.remove(); // remove this entry as it is processed
                    }

                }
            }
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String getTransitionTarget(final OrderEvent orderEvent) {
        return CustomerOrder.ORDER_STATUS_IN_PROGRESS;
    }


}
