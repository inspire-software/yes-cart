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
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderStateAfterTransitionListener;
import org.yes.cart.util.ShopCodeContext;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 20/02/2017
 * Time: 11:26
 */
public class PaymentConfirmedOrderAutoExportListenerImpl implements OrderStateAfterTransitionListener {

    public static final String ELIGIBILITY_TYPE = "INITPAID";

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

                    final Logger log = ShopCodeContext.getLog(this);
                    log.debug("OrderEvent {} is eligible for auto export", orderEvent);

                    order.setEligibleForExport(ELIGIBILITY_TYPE);
                    delivery.setEligibleForExport(ELIGIBILITY_TYPE);

                }

            }

        }
        return false;

    }

    private static final Set<String> VALID = new HashSet<String>() {{
        add(CustomerOrder.ORDER_STATUS_IN_PROGRESS);
        add(CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED);
        add(CustomerOrder.ORDER_STATUS_COMPLETED);
    }};

    protected boolean isValidOrderState(final CustomerOrder customerOrder) {

        return !customerOrder.isBlockExport() && VALID.contains(customerOrder.getOrderStatus());

    }

    protected boolean isValidDeliveryState(final CustomerOrderDelivery customerOrderDelivery) {

        return !customerOrderDelivery.isBlockExport();

    }

}
