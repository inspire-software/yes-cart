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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.util.ShopCodeContext;

import java.text.MessageFormat;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class AbstractOrderEventHandlerImpl implements OrderEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    /**
     * Get transition target.
     * @param orderEvent event
     * @return transition target id.
     */
    protected abstract String getTransitionTarget(final OrderEvent orderEvent);

    protected void handleInternal(final OrderEvent orderEvent) {
        if (LOG.isInfoEnabled()) {
            LOG.info(
                    MessageFormat.format(
                            "Order {0} transition from {1} to {2} state",
                            orderEvent.getCustomerOrder().getOrdernum(),
                            orderEvent.getCustomerOrder().getOrderStatus(),
                            getTransitionTarget(orderEvent)
                    )
            );
        }
        orderEvent.getCustomerOrder().setOrderStatus(getTransitionTarget(orderEvent));
    }



}
