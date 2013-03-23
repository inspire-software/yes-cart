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
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;
import org.yes.cart.util.ShopCodeContext;

/**
 * Cancel order transition with funds return.
 * This class responsible for two things:
 * 1. perform fund retuns to cutomers card,
 * 2. credit quantity on warehouse , that belong to shop.
 * For quantity credit alway will be used first warehouse
 * <p/>
 * Funds return can be performed via 2 types of operations:
 * 1. Void capture - in case if capture was not settled,
 * usually up to 24 hours after capture, but depends from payment gateway
 * 2. Credit - in case if funds was settled
 * <p/>
 * This transition can be pefrormed by operator/call center request
 * when after funds capture. No sence to give cancel
 * posibility to shopper.
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CancelOrderWithRefundOrderEventHandlerImpl extends CancelOrderEventHandlerImpl implements OrderEventHandler {

    private final PaymentProcessorFactory paymentProcessorFactory;


    /**
     * Constracu cancel transition.
     *
     * @param paymentProcessorFactory to funds return
     * @param warehouseService        to locate warehouse, that belong to shop where order was created
     * @param skuWarehouseService     to credit quantity on warehouse
     */
    public CancelOrderWithRefundOrderEventHandlerImpl(
            final PaymentProcessorFactory paymentProcessorFactory,
            final WarehouseService warehouseService,
            final SkuWarehouseService skuWarehouseService) {

        super(warehouseService, skuWarehouseService);
        this.paymentProcessorFactory = paymentProcessorFactory;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handle(final OrderEvent orderEvent) {
        synchronized (OrderEventHandler.syncMonitor) {
            final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(orderEvent.getCustomerOrder().getPgLabel());
            final CustomerOrder order = orderEvent.getCustomerOrder();
            if (Payment.PAYMENT_STATUS_OK.equals(paymentProcessor.cancelOrder(order))) {

                return super.handle(orderEvent);
            }
            /**
             * Administrative notification will be send via email. See appropriate aspect
             */
            ShopCodeContext.getLog(this).error("Can not cancel order, because of error on payment gateway.");
            return false;
        }
    }


}
