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
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;
import org.yes.cart.util.ShopCodeContext;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ShipmentCompleteOrderEventHandlerImpl implements OrderEventHandler {

    private final PaymentProcessorFactory paymentProcessorFactory;

    /**
     * Construct shipment complete transition hanler.
     *
     * @param paymentProcessorFactory to get the payment processor
     */
    public ShipmentCompleteOrderEventHandlerImpl(final PaymentProcessorFactory paymentProcessorFactory) {
        this.paymentProcessorFactory = paymentProcessorFactory;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) {
        synchronized (OrderEventHandler.syncMonitor) {

            final Logger log = ShopCodeContext.getLog(this);

            final CustomerOrder order = orderEvent.getCustomerOrder();

            final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(order.getPgLabel(), order.getShop().getCode());

            final boolean fundCaptured = Payment.PAYMENT_STATUS_OK.equals(
                    paymentProcessor.shipmentComplete(orderEvent.getCustomerOrder(), orderEvent.getCustomerOrderDelivery().getDeliveryNum())
            );
            if (fundCaptured) {
                if (log.isInfoEnabled()) {
                    log.info("Funds captured for delivery {}", orderEvent.getCustomerOrderDelivery().getDeliveryNum());
                }
                orderEvent.getCustomerOrderDelivery().setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);
                for (CustomerOrderDelivery delivery : orderEvent.getCustomerOrder().getDelivery()) {
                    if (!CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED.equals(delivery.getDeliveryStatus())) {
                        orderEvent.getCustomerOrder().setOrderStatus(CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED);
                        return true;
                    }
                }
                orderEvent.getCustomerOrder().setOrderStatus(CustomerOrder.ORDER_STATUS_COMPLETED);
                if (log.isInfoEnabled()) {
                    log.info("Order {} completed ", orderEvent.getCustomerOrder().getOrdernum());
                }
                return true;
            }
            if (log.isErrorEnabled()) {
                log.error("Funds not captured for delivery {}", orderEvent.getCustomerOrderDelivery().getDeliveryNum());
            }
            return false;
        }
    }


}
