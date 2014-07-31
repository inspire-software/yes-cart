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

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;
import org.yes.cart.util.ShopCodeContext;

/**
 * Cancel new order transition with funds return.
 * This class responsible for two things:
 * 1. perform saving of the original funds capture,
 * 2. performing immediate refund if possible
 * <p/>
 * This transition is performed automatically in the event that external
 * form processing ends up with an out of stock order thus failing the whole
 * chain.
 * <p/>
 * <p/>
 * User: Denis Pavlov
 */
public class CancelNewOrderWithRefundOrderEventHandlerImpl extends AbstractOrderEventHandlerImpl implements OrderEventHandler {

    private final PaymentProcessorFactory paymentProcessorFactory;

    /**
     * Construct cancel transition.
     *
     * @param paymentProcessorFactory to funds return
     */
    public CancelNewOrderWithRefundOrderEventHandlerImpl(
            final PaymentProcessorFactory paymentProcessorFactory) {

        this.paymentProcessorFactory = paymentProcessorFactory;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handle(final OrderEvent orderEvent) throws OrderException {
        synchronized (OrderEventHandler.syncMonitor) {
            final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(orderEvent.getCustomerOrder().getPgLabel());
            final CustomerOrder order = orderEvent.getCustomerOrder();

            boolean handled = true;
            if (paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().isOnlineGateway()) {
                if (Payment.PAYMENT_STATUS_OK.equals(paymentProcessor.authorize(orderEvent.getCustomerOrder(), orderEvent.getParams()))) {
                    //payment was ok, but we are out of stock
                    if (!Payment.PAYMENT_STATUS_OK.equals(paymentProcessor.cancelOrder(order))) {
                        /**
                         * Administrative notification will be send via email. See appropriate aspect
                         */
                        ShopCodeContext.getLog(this).error("Can not cancel order, because of error on payment gateway.");
                        handled = false;
                    }

                } //else payment failed, but we have not reserved anything and we cancelled
            } // else we have offline payment, so no money yet

            handleInternal(orderEvent);

            return handled;
        }
    }


    /**
     * {@inheritDoc}
     */
    protected String getTransitionTarget(final OrderEvent orderEvent) {
        return CustomerOrder.ORDER_STATUS_CANCELLED;
    }

}
