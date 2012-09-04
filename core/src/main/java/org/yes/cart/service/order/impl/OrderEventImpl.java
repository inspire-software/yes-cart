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

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.service.order.OrderEvent;

import java.util.Map;

/**
 *
 * Event with id and his context. One of the context element is order.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class OrderEventImpl  implements OrderEvent {

    private final String eventId;
    private final CustomerOrder customerOrder;
    private final CustomerOrderDelivery customerOrderDelivery;
    private final Map params;

    /**
     * Construct order event.
     * @param eventId trigger name
     * @param customerOrder order
     */
    public OrderEventImpl(final String eventId,
                          final CustomerOrder customerOrder) {
        this(eventId, customerOrder, null, null);

    }

    /**
     * Construct order event.
     * @param eventId trigger name
     * @param customerOrder order
     * @param customerOrderDelivery optional delivery
     */
    public OrderEventImpl(final String eventId,
                          final CustomerOrder customerOrder,
                          final CustomerOrderDelivery customerOrderDelivery) {
        this(eventId, customerOrder, customerOrderDelivery, null);
    }

    /**
     * Construct order event.
     * @param eventId trigger name
     * @param customerOrder order
     * @param customerOrderDelivery optional delivery
     * @param params optional params
     */
    public OrderEventImpl(final String eventId,
                          final CustomerOrder customerOrder,
                          final CustomerOrderDelivery customerOrderDelivery,
                          final Map params) {
        this.eventId = eventId;
        this.customerOrder = customerOrder;
        this.customerOrderDelivery = customerOrderDelivery;
        this.params = params;
    }

    /** {@inheritDoc}*/
    public String getEventId() {
        return eventId;
    }

    /** {@inheritDoc}*/
    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    /** {@inheritDoc}*/
    public CustomerOrderDelivery getCustomerOrderDelivery() {
        return customerOrderDelivery;
    }

    /** {@inheritDoc}*/
    public Map getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "OrderEventImpl{"
                + "eventId='" + eventId + '\''
                + ", customerOrder=" +  (customerOrder == null ? "" : customerOrder.getOrdernum())
                + " order status " + (customerOrder == null ? "" : customerOrder.getOrderStatus() )
                + (customerOrderDelivery==null?"":", customerOrderDelivery=" + customerOrderDelivery.getDeliveryNum() + " delivery status = " + customerOrderDelivery.getDeliveryStatus()) +
                '}';
    }
}
