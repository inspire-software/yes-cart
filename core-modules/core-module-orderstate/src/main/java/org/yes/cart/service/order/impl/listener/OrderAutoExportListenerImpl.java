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

package org.yes.cart.service.order.impl.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderStateAfterTransitionListener;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 20/02/2017
 * Time: 11:26
 */
public class OrderAutoExportListenerImpl implements OrderStateAfterTransitionListener {

    private static final Logger LOG = LoggerFactory.getLogger(OrderAutoExportListenerImpl.class);

    private String orderEligibilityType;
    private String deliveryEligibilityType;
    private Set<String> validOrderStates;
    private Set<String> validDeliveryStates;

    /** {@inheritDoc} */
    @Override
    public boolean onEvent(final OrderEvent orderEvent) {

        final Boolean handled = (Boolean) orderEvent.getRuntimeParams().get("handled");
        if (handled != null && handled && isValidOrderState(orderEvent.getCustomerOrder())) {

            final CustomerOrder order = orderEvent.getCustomerOrder();

            final Collection<CustomerOrderDelivery> deliveries = orderEvent.getCustomerOrderDelivery() != null ?
                    Collections.singleton(orderEvent.getCustomerOrderDelivery()) : order.getDelivery();

            for (final CustomerOrderDelivery delivery : deliveries) {

                if (isValidDeliveryState(delivery)) {

                    if (LOG.isInfoEnabled()) {
                        LOG.info("OrderEvent {} is eligible for auto export {}/{}",
                                new Object[]{orderEvent, orderEligibilityType, deliveryEligibilityType});
                    }

                    order.setEligibleForExport(orderEligibilityType);
                    delivery.setEligibleForExport(deliveryEligibilityType);

                }

            }

        }
        return false;

    }

    protected boolean isValidOrderState(final CustomerOrder customerOrder) {

        return !customerOrder.isBlockExport() &&
                (validOrderStates == null || validOrderStates.contains(customerOrder.getOrderStatus()));

    }

    protected boolean isValidDeliveryState(final CustomerOrderDelivery customerOrderDelivery) {

        return !customerOrderDelivery.isBlockExport() &&
                (validDeliveryStates == null || validDeliveryStates.contains(customerOrderDelivery.getDeliveryStatus()));

    }

    /**
     * Order export eligibility type if state is allowed.
     *
     * @param orderEligibilityType type
     */
    public void setOrderEligibilityType(final String orderEligibilityType) {
        this.orderEligibilityType = orderEligibilityType;
    }

    /**
     * Delivery export eligibility type if state is allowed.
     *
     * @param deliveryEligibilityType type
     */
    public void setDeliveryEligibilityType(final String deliveryEligibilityType) {
        this.deliveryEligibilityType = deliveryEligibilityType;
    }

    /**
     * Set of valid order states.
     *
     * @param validOrderStates valid states
     */
    public void setValidOrderStates(final Set<String> validOrderStates) {
        this.validOrderStates = validOrderStates;
    }

    /**
     * Set of valida delivery states.
     *
     * @param validDeliveryStates valid states
     */
    public void setValidDeliveryStates(final Set<String> validDeliveryStates) {
        this.validDeliveryStates = validDeliveryStates;
    }
}
