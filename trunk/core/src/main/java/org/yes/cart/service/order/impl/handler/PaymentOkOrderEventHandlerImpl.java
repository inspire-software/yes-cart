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

package org.yes.cart.service.order.impl.handler;

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
import org.yes.cart.util.ShopCodeContext;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Perform separate processing of order deliveries.
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentOkOrderEventHandlerImpl extends AbstractOrderEventHandlerImpl implements OrderEventHandler, ApplicationContextAware {

    private OrderStateManager orderStateManager = null;
    private ApplicationContext applicationContext;

    private static final Map<String, String> GROUP_TRIGGER_MAP = new HashMap<String, String>() {{

        put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, OrderStateManager.EVT_PROCESS_ALLOCATION);
        put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, OrderStateManager.EVT_PROCESS_TIME_WAIT);
        put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, OrderStateManager.EVT_PROCESS_INVENTORY_WAIT);
        put(CustomerOrderDelivery.ELECTONIC_DELIVERY_GROUP, OrderStateManager.EVT_SHIPMENT_COMPLETE);
        put(CustomerOrderDelivery.MIX_DELIVERY_GROUP, OrderStateManager.EVT_PROCESS_TIME_WAIT);

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
            for (CustomerOrderDelivery delivery : order.getDelivery()) {
                final String eventId = GROUP_TRIGGER_MAP.get(delivery.getDeliveryGroup());
                ShopCodeContext.getLog().info(MessageFormat.format("Delivery {0} for order {1} event {2}",
                        delivery.getDeliveryNum(), order.getOrdernum(), eventId));
                final OrderEvent deliveryEvent = new OrderEventImpl(eventId, order, delivery);
                getOrderStateManager().fireTransition(deliveryEvent);
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
