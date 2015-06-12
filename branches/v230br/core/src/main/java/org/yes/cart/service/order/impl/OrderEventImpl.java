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

package org.yes.cart.service.order.impl;

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.service.order.OrderEvent;

import java.util.Collections;
import java.util.HashMap;
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
    private final String customerOrderOriginalStatus;
    private final CustomerOrderDelivery customerOrderDelivery;
    private final String customerOrderDeliveryOriginalStatus;
    private final Map params;
    private final Map runtimeParams = new HashMap();

    private final OrderEvent parent;

    /**
     * Construct order event.
     *
     * @param eventId trigger name
     * @param customerOrder order
     */
    public OrderEventImpl(final String eventId,
                          final CustomerOrder customerOrder) {
        this(null, eventId, customerOrder, null, null);
    }

    /**
     * Construct order event.
     *
     * @param parent parent event
     * @param eventId trigger name
     * @param customerOrder order
     */
    public OrderEventImpl(final OrderEvent parent,
                          final String eventId,
                          final CustomerOrder customerOrder) {
        this(parent, eventId, customerOrder, null, null);
    }

    /**
     * Construct order event.
     *
     * @param eventId trigger name
     * @param customerOrder order
     * @param customerOrderDelivery optional delivery
     */
    public OrderEventImpl(final String eventId,
                          final CustomerOrder customerOrder,
                          final CustomerOrderDelivery customerOrderDelivery) {
        this(null, eventId, customerOrder, customerOrderDelivery, null);
    }

    /**
     * Construct order event.
     *
     * @param parent parent event
     * @param eventId trigger name
     * @param customerOrder order
     * @param customerOrderDelivery optional delivery
     */
    public OrderEventImpl(final OrderEvent parent,
                          final String eventId,
                          final CustomerOrder customerOrder,
                          final CustomerOrderDelivery customerOrderDelivery) {
        this(parent, eventId, customerOrder, customerOrderDelivery, null);
    }

    /**
     * Construct order event.
     *
     * @param eventId trigger name
     * @param customerOrder order
     * @param customerOrderDelivery optional delivery
     * @param params optional params
     */
    public OrderEventImpl(final String eventId,
                          final CustomerOrder customerOrder,
                          final CustomerOrderDelivery customerOrderDelivery,
                          final Map params) {
        this(null, eventId, customerOrder, customerOrderDelivery, params);
    }

    /**
     * Construct order event.
     *
     * @param parent parent event
     * @param eventId trigger name
     * @param customerOrder order
     * @param customerOrderDelivery optional delivery
     * @param params optional params
     */
    public OrderEventImpl(final OrderEvent parent,
                          final String eventId,
                          final CustomerOrder customerOrder,
                          final CustomerOrderDelivery customerOrderDelivery,
                          final Map params) {
        this.parent = parent;
        this.eventId = eventId;
        this.customerOrder = customerOrder;
        this.customerOrderOriginalStatus = customerOrder != null ? customerOrder.getOrderStatus() : "-";
        this.customerOrderDelivery = customerOrderDelivery;
        this.customerOrderDeliveryOriginalStatus = customerOrderDelivery != null ? customerOrderDelivery.getDeliveryStatus() : "-";
        this.params = params == null ? Collections.emptyMap() : Collections.unmodifiableMap(params);
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

    /** {@inheritDoc}*/
    public Map getRuntimeParams() {
        return runtimeParams;
    }

    @Override
    public String toString() {
        final String event = "OrderEventImpl{"
                + eventId
                + ", order=" +  (customerOrder == null ? "" : customerOrder.getOrdernum())
                + "@" + (customerOrder == null ? "-" : customerOrderOriginalStatus + "-" + customerOrder.getOrderStatus() )
                + (customerOrderDelivery==null?"":", delivery=" + customerOrderDelivery.getDeliveryNum() + "@" + customerOrderDeliveryOriginalStatus + "-" + customerOrderDelivery.getDeliveryStatus()) +
                '}';
        if (parent != null) {
            return parent.toString() + " -> " + event;
        }
        return event;
    }
}
