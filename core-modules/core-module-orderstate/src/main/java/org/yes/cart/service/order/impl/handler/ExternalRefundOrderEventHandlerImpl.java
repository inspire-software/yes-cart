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
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.PGDisabledException;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;
import org.yes.cart.util.log.Markers;

/**
 * External refund event is triggered by callback from payment gateway, whcih could happen
 * if refund is made outside of YC (ay from PG user panel)
 * <p/>
 * <p/>
 * User: Denis Pavlov
 */
public class ExternalRefundOrderEventHandlerImpl extends AbstractOrderEventHandlerImpl implements OrderEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalRefundOrderEventHandlerImpl.class);

    private final PaymentProcessorFactory paymentProcessorFactory;

    /**
     * Construct cancel transition.
     *
     * @param paymentProcessorFactory to funds return
     */
    public ExternalRefundOrderEventHandlerImpl(final PaymentProcessorFactory paymentProcessorFactory) {

        this.paymentProcessorFactory = paymentProcessorFactory;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handle(final OrderEvent orderEvent) throws OrderException {
        synchronized (OrderEventHandler.syncMonitor) {

            final CustomerOrder order = orderEvent.getCustomerOrder();

            final Shop pgShop = order.getShop().getMaster() != null ? order.getShop().getMaster() : order.getShop();
            final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(order.getPgLabel(), pgShop.getCode());
            if (!paymentProcessor.isPaymentGatewayEnabled()) {
                throw new PGDisabledException("PG " + order.getPgLabel() + " is disabled in " + order.getShop().getCode(), order.getPgLabel());
            }

            if (paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().isOnlineGateway()) {
                final String state = paymentProcessor.refundNotification(orderEvent.getCustomerOrder(), orderEvent.getParams());
                if (Payment.PAYMENT_STATUS_OK.equals(state)) {
                    LOG.info(Markers.alert(), "Received refund notification for order {}", orderEvent.getCustomerOrder().getOrdernum());
                }
            } // else we have offline payment, so callbacks are not possible

            return true;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTransitionTarget(final OrderEvent orderEvent) {
        return orderEvent.getCustomerOrder().getOrderStatus();  // No change
    }

}
