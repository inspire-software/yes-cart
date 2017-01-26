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

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.PGDisabledException;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;

/**
 * Handle pending refunds for cancelled and returned orders.
 * <p/>
 * User: Denis Pavlov
 * Date: 27-Apr-2015
 * Time: 14:12:54
 */
public class RefundProcessedOrderEventHandlerImpl extends AbstractOrderEventHandlerImpl implements OrderEventHandler {

    private final PaymentProcessorFactory paymentProcessorFactory;

    /**
     * Construct transition handler.
     *
     * @param paymentProcessorFactory to perform authorize operation
     */
    public RefundProcessedOrderEventHandlerImpl(final PaymentProcessorFactory paymentProcessorFactory) {
        this.paymentProcessorFactory = paymentProcessorFactory;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) throws OrderException {
        synchronized (OrderEventHandler.syncMonitor) {

            final CustomerOrder order = orderEvent.getCustomerOrder();

            final Shop pgShop = order.getShop().getMaster() != null ? order.getShop().getMaster() : order.getShop();
            final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(order.getPgLabel(), pgShop.getCode());
            if (!paymentProcessor.isPaymentGatewayEnabled()) {
                throw new PGDisabledException("PG " + order.getPgLabel() + " is disabled in " + order.getShop().getCode(), order.getPgLabel());
            }

            boolean handled = false;
            if (paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().isOnlineGateway()) {
                final String result = paymentProcessor.cancelOrder(orderEvent.getCustomerOrder(), orderEvent.getParams());
                if (Payment.PAYMENT_STATUS_OK.equals(result)) {
                    //payment was ok, proceed
                    handleInternal(orderEvent);
                    handled = true;
                }
            }

            return handled;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String getTransitionTarget(final OrderEvent orderEvent) {
        if (CustomerOrder.ORDER_STATUS_RETURNED_WAITING_PAYMENT.equals(orderEvent.getCustomerOrder().getOrderStatus())) {
            return CustomerOrder.ORDER_STATUS_RETURNED;
        }
        return CustomerOrder.ORDER_STATUS_CANCELLED;
    }


}