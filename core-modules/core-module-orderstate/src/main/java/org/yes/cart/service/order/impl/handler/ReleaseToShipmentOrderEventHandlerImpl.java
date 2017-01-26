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
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.PGDisabledException;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ReleaseToShipmentOrderEventHandlerImpl implements OrderEventHandler {

    private final PaymentProcessorFactory paymentProcessorFactory;

    /**
     * Construct to shipment transition handler.
     *
     * @param paymentProcessorFactory to get the payment processor
     */
    public ReleaseToShipmentOrderEventHandlerImpl(final PaymentProcessorFactory paymentProcessorFactory) {
        this.paymentProcessorFactory = paymentProcessorFactory;
    }


    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) throws OrderException {
        synchronized (OrderEventHandler.syncMonitor) {

            final CustomerOrder order = orderEvent.getCustomerOrder();
            final CustomerOrderDelivery delivery = orderEvent.getCustomerOrderDelivery();

            final Shop pgShop = order.getShop().getMaster() != null ? order.getShop().getMaster() : order.getShop();
            final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(order.getPgLabel(), pgShop.getCode());
            if (!paymentProcessor.isPaymentGatewayEnabled()) {
                throw new PGDisabledException("PG " + order.getPgLabel() + " is disabled in " + order.getShop().getCode(), order.getPgLabel());
            }

            if (paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().isSupportAuthorize()) {
                // this is payment on shipping/delivery

                if (paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().isOnlineGateway()) {

                    // need to capture before shipping
                    if (Payment.PAYMENT_STATUS_OK.equals(paymentProcessor.shipmentComplete(order, delivery.getDeliveryNum(), orderEvent.getParams()))) {

                        // payment was ok so continue
                        delivery.setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

                    } else {

                        // payment was not ok so we mark the order as waiting payment and we do NOT proceed to shipping
                        delivery.setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT);

                    }

                } else {

                    // this is offline PG, so CAPTURE will happen on delivery (i.e. next phase)
                    delivery.setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS_WAITING_PAYMENT);

                }

            } else {

                // this is pre-paid, so we proceed to shipping in progress
                // Electronic also proceed to shipping in progress since shipped status is when it is downloaded
                delivery.setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

            }

            return true;
        }
    }

}
