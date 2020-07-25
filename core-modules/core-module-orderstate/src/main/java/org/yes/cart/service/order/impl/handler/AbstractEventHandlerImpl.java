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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.i18n.I18NModels;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.utils.DateUtils;

/**
 * Date: 24-Jul-2020
 * Time: 14:12:54
 */
public abstract class AbstractEventHandlerImpl implements OrderEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractEventHandlerImpl.class);

    private static final Pair<Boolean, String> NO_STRING_VALUE = new Pair<>(false, null);

    /**
     * Transition order to next state.
     *
     * @param orderEvent    order event
     * @param order         order
     * @param targetState   target state for the delivery
     */
    protected void transition(final OrderEvent orderEvent,
                              final CustomerOrder order,
                              final String targetState) {

        final String currentState = order.getOrderStatus();

        LOG.info("Order {} transition from {} to {} state",
                order.getOrdernum(),
                currentState,
                targetState
        );

        addAuditRecord(orderEvent, order, order.getOrdernum(), currentState, targetState);
        addClientRecord(orderEvent, order, "", targetState);

        order.setOrderStatus(targetState);
    }


    /**
     * Transition delivery to next state.
     *
     * @param orderEvent    order event
     * @param order         order
     * @param delivery      delivery
     * @param targetState   target state for the delivery
     */
    protected void transition(final OrderEvent orderEvent,
                              final CustomerOrder order,
                              final CustomerOrderDelivery delivery,
                              final String targetState) {

        final String currentState = delivery.getDeliveryStatus();

        LOG.info("Delivery {} transition from {} to {} state",
                delivery.getDeliveryNum(),
                currentState,
                targetState
        );

        addAuditRecord(orderEvent, order, delivery.getDeliveryNum(), currentState, targetState);
        addClientRecord(orderEvent, order, "_" + delivery.getDeliveryNum(), targetState);

        delivery.setDeliveryStatus(targetState);
    }


    private void addAuditRecord(final OrderEvent orderEvent, final CustomerOrder order, final String reference, final String currentState, final String nextState) {

        final Pair<Boolean, String> manager = determineStringValue(orderEvent, AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_USER);
        final Pair<Boolean, String> auditMessage = determineStringValue(orderEvent, AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_AUDIT_MESSAGE);
        final Pair<Boolean, String> clientMessage = determineStringValue(orderEvent, AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_CLIENT_MESSAGE);

        final StringBuilder transitionTrail = new StringBuilder()
                .append(currentState).append(" -> ").append(nextState);
        if (manager.getFirst()) {
            transitionTrail.append(", ").append(manager.getSecond());
        }
        if (auditMessage.getFirst()) {
            transitionTrail.append(", ").append(auditMessage.getSecond());
        }
        if (clientMessage.getFirst()) {
            transitionTrail.append(", ").append(clientMessage.getSecond());
        }

        order.putValue(
                "ST " + reference + ": " + DateUtils.formatSDT(),
                transitionTrail.toString(),
                I18NModels.AUDITEXPORT
        );

    }

    private void addClientRecord(final OrderEvent orderEvent, final CustomerOrder order, final String referenceSuffix, final String nextState) {

        final Pair<Boolean, String> clientMessage = determineStringValue(orderEvent, AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_CLIENT_MESSAGE);

        if (clientMessage.getFirst()) {
            order.putValue(
                    nextState.toUpperCase().replace('.', '_') + referenceSuffix,
                    clientMessage.getSecond(),
                    I18NModels.AUDITEXPORT
            );
        }
    }

    private Pair<Boolean, String> determineStringValue(final OrderEvent orderEvent, final String valueKey) {

        final Object valueObj = orderEvent.getParams().get(valueKey);
        if (valueObj instanceof String && StringUtils.isNotEmpty((String) valueObj)) {
            return new Pair<>(true, (String) valueObj);
        }

        return NO_STRING_VALUE;

    }


}
