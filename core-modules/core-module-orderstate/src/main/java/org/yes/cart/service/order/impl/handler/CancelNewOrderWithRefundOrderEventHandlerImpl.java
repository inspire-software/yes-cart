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
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.PGDisabledException;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;

import java.util.Collections;

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
public class CancelNewOrderWithRefundOrderEventHandlerImpl extends CancelOrderEventHandlerImpl implements OrderEventHandler {

    private final PaymentProcessorFactory paymentProcessorFactory;

    /**
     * Construct cancel transition.
     *
     * @param paymentProcessorFactory to funds return
     * @param warehouseService        to locate warehouse, that belong to shop where order was created
     * @param skuWarehouseService     to credit quantity on warehouse
     * @param productService          product service
     */
    public CancelNewOrderWithRefundOrderEventHandlerImpl(
            final PaymentProcessorFactory paymentProcessorFactory,
            final WarehouseService warehouseService,
            final SkuWarehouseService skuWarehouseService,
            final ProductService productService) {

        super(warehouseService, skuWarehouseService, productService);
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

            if (paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().isExternalFormProcessing()) {
                final String result = paymentProcessor.authorize(orderEvent.getCustomerOrder(), orderEvent.getParams());
                if (Payment.PAYMENT_STATUS_OK.equals(result) || Payment.PAYMENT_STATUS_PROCESSING.equals(result)) {
                    //payment was ok, but we are out of stock
                    final String resultCancel = paymentProcessor.cancelOrder(order, Collections.emptyMap());
                    if (!Payment.PAYMENT_STATUS_OK.equals(resultCancel)) {
                        orderEvent.getRuntimeParams().put("cancelFailed", Boolean.TRUE);
                    }

                } //else payment failed, but we have not reserved anything and we cancelled
            } // else we have offline payment, so no money yet

            return super.handle(orderEvent);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTransitionTarget(final OrderEvent orderEvent) {
        if (orderEvent.getRuntimeParams().containsKey("cancelFailed")) {
            return CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT;
        }
        return CustomerOrder.ORDER_STATUS_CANCELLED;
    }

}
