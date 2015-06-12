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

import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;

/**
 * Put delivery into allocation waiting state. This is used to break the continuous flow
 * from payment to allocation, since there are just too many things that can go wrong
 * and we may loose some state.
 */
public class ProcessAllocationWaitOrderEventHandlerImpl implements OrderEventHandler {

    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) {
        synchronized (OrderEventHandler.syncMonitor) {
            orderEvent.getCustomerOrderDelivery().setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            return true;
        }
    }

}
