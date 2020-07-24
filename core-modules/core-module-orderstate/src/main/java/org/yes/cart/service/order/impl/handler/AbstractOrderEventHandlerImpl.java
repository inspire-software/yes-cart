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

package org.yes.cart.service.order.impl.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class AbstractOrderEventHandlerImpl extends AbstractEventHandlerImpl implements OrderEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractOrderEventHandlerImpl.class);

    /**
     * Get transition target.
     *
     * @param orderEvent    event
     * @return transition   target id.
     */
    protected abstract String getTransitionTarget(final OrderEvent orderEvent);

    /**
     * Default handling to transition to target state.
     *
     * @param orderEvent    event
     */
    protected void handleInternal(final OrderEvent orderEvent) {

        transition(orderEvent, orderEvent.getCustomerOrder(), getTransitionTarget(orderEvent));

    }

    /**
     * Determine if "forceProcessing" flag is enabled in runtime variables, so that PG operation can be
     * forced for manual overrides.
     *
     * @param orderEvent    order event
     *
     * @return true if enabled as Boolean.TRUE runtime parameter
     */
    protected boolean isForceProcessing(final OrderEvent orderEvent) {

        final Object forceProcessing = orderEvent.getRuntimeParams().get("forceProcessing");
        return forceProcessing instanceof Boolean && (Boolean) forceProcessing;

    }

}
