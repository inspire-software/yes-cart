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
 * Cancel order transition with funds return.
 * This class responsible for two things:
 * 1. perform refund to customers card,
 * 2. credit quantity on warehouse, that belongs to shop.
 * For quantity credit always will be used first warehouse
 * <p/>
 * Funds return can be performed via 2 types of operations:
 * 1. Void capture - in case if capture was not settled,
 * usually up to 24 hours after capture, but depends from payment gateway
 * 2. Credit - in case if funds was settled
 * <p/>
 * This transition can be performed by operator/call center request
 * when after funds capture. No sense to give cancel
 * possibility to shopper.
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CancelOrderWithRefundOrderEventHandlerImpl extends CancelOrderEventHandlerImpl implements OrderEventHandler {

    private final PaymentProcessorFactory paymentProcessorFactory;


    /**
     * Construct cancel transition.
     *
     * @param paymentProcessorFactory to funds return
     * @param warehouseService        to locate warehouse, that belong to shop where order was created
     * @param skuWarehouseService     to credit quantity on warehouse
     * @param productService          product service
     */
    public CancelOrderWithRefundOrderEventHandlerImpl(
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

            if (paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().isOnlineGateway()) {

                // We need to attempt to cancel first as this may throw an exception, then we should not make any payment refunds
                creditQuantity(orderEvent.getCustomerOrder());

                final String resultCancel = paymentProcessor.cancelOrder(order, Collections.emptyMap());
                if (!Payment.PAYMENT_STATUS_OK.equals(resultCancel)) {
                    orderEvent.getRuntimeParams().put("cancelFailed", Boolean.TRUE);
                }
            } else {
                // offline PG should always work
                final String resultCancel = paymentProcessor.cancelOrder(order, Collections.emptyMap());
                if (!Payment.PAYMENT_STATUS_OK.equals(resultCancel)) {
                    return false;
                }
                creditQuantity(orderEvent.getCustomerOrder());

            }

            handleInternal(orderEvent);
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTransitionTarget(final OrderEvent orderEvent) {
        if (CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED.equals(orderEvent.getCustomerOrder().getOrderStatus()) ||
                CustomerOrder.ORDER_STATUS_COMPLETED.equals(orderEvent.getCustomerOrder().getOrderStatus())) {
            if (orderEvent.getRuntimeParams().containsKey("cancelFailed")) {
                return CustomerOrder.ORDER_STATUS_RETURNED_WAITING_PAYMENT;
            }
            return CustomerOrder.ORDER_STATUS_RETURNED;
        }
        if (orderEvent.getRuntimeParams().containsKey("cancelFailed")) {
            return CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT;
        }
        return CustomerOrder.ORDER_STATUS_CANCELLED;
    }

    @Override
    protected boolean isNeedReturn(final String deliveryStatus) {
        // Allow to return shipped items
        return CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED.equals(deliveryStatus);
    }
}
