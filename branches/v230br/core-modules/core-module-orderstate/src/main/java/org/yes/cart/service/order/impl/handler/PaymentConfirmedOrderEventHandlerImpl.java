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

import org.springframework.context.ApplicationContextAware;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentConfirmedOrderEventHandlerImpl extends PaymentOkOrderEventHandlerImpl implements OrderEventHandler, ApplicationContextAware {


    private final PaymentProcessorFactory paymentProcessorFactory;

    public PaymentConfirmedOrderEventHandlerImpl(final PaymentProcessorFactory paymentProcessorFactory) {
        this.paymentProcessorFactory = paymentProcessorFactory;
    }



    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) throws OrderException {

        final CustomerOrder order = orderEvent.getCustomerOrder();
        final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(order.getPgLabel(), order.getShop().getCode());

        if (!paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().isOnlineGateway()) {

            final String result = paymentProcessor.authorize(orderEvent.getCustomerOrder(), orderEvent.getParams());
            if (Payment.PAYMENT_STATUS_OK.equals(result)) {
                return super.handle(orderEvent);
            }

        }

        return false;
    }

}
