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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.domain.entity.*;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.*;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;
import org.yes.cart.util.DomainApiUtils;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Initial {@link CustomerOrder#ORDER_STATUS_PENDING} state.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PendingOrderEventHandlerImpl extends AbstractOrderEventHandlerImpl implements OrderEventHandler, ApplicationContextAware {

    private final PaymentProcessorFactory paymentProcessorFactory;
    private OrderStateManager orderStateManager = null;
    private ApplicationContext applicationContext;
    private final WarehouseService warehouseService;
    private final SkuWarehouseService skuWarehouseService;
    private final ProductService productService;

    /**
     * Construct transition handler.
     *
     * @param paymentProcessorFactory to perform authorize operation
     * @param warehouseService        warehouse service
     * @param skuWarehouseService     sku on warehouse service to change quantity
     * @param productService          product service
     */
    public PendingOrderEventHandlerImpl(final PaymentProcessorFactory paymentProcessorFactory,
                                        final WarehouseService warehouseService,
                                        final SkuWarehouseService skuWarehouseService,
                                        final ProductService productService) {
        this.paymentProcessorFactory = paymentProcessorFactory;
        this.warehouseService = warehouseService;
        this.skuWarehouseService = skuWarehouseService;
        this.productService = productService;
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private synchronized OrderStateManager getOrderStateManager() {
        if (orderStateManager == null) {
            orderStateManager = (OrderStateManager) applicationContext.getBean("orderStateManager");
        }
        return orderStateManager;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) throws OrderException {
        synchronized (OrderEventHandler.syncMonitor) {

            for (CustomerOrderDelivery customerOrderDelivery : orderEvent.getCustomerOrder().getDelivery()) {
                reserveQuantity(customerOrderDelivery);
            }
            handleInternal(orderEvent);

            final CustomerOrder order = orderEvent.getCustomerOrder();

            final Shop pgShop = order.getShop().getMaster() != null ? order.getShop().getMaster() : order.getShop();
            final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(order.getPgLabel(), pgShop.getCode());
            if (!paymentProcessor.isPaymentGatewayEnabled()) {
                throw new PGDisabledException("PG " + order.getPgLabel() + " is disabled in " + order.getShop().getCode(), order.getPgLabel());
            }

            if (paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().isOnlineGateway()) {
                final String result = paymentProcessor.authorize(orderEvent.getCustomerOrder(), orderEvent.getParams());
                if (Payment.PAYMENT_STATUS_OK.equals(result)) {
                    //payment was ok, so quantity on warehouses will be decreased
                    getOrderStateManager().fireTransition(new OrderEventImpl(orderEvent, OrderStateManager.EVT_PAYMENT_OK, orderEvent.getCustomerOrder()));
                } else if (Payment.PAYMENT_STATUS_PROCESSING.equals(result)) {
                    // Payment is still processing
                    getOrderStateManager().fireTransition(new OrderEventImpl(orderEvent, OrderStateManager.EVT_PAYMENT_PROCESSING, orderEvent.getCustomerOrder()));
                } else {
                    //in case of bad payment reserved product quantity will be returned from reservation
                    getOrderStateManager().fireTransition(new OrderEventImpl(orderEvent, OrderStateManager.EVT_CANCEL, orderEvent.getCustomerOrder()));
                }
            } else {
                // wait for confirmation about payment
                getOrderStateManager().fireTransition(new OrderEventImpl(orderEvent, OrderStateManager.EVT_PAYMENT_OFFLINE, orderEvent.getCustomerOrder()));
            }

            return true;
        }
    }

    /**
     * Allocate sku quantity on warehouses, that belong to shop, where order was made.
     *
     * @param orderDelivery reserve for this delivery
     * @throws OrderItemAllocationException in case if can not allocate quantity for each sku
     */
    void reserveQuantity(final CustomerOrderDelivery orderDelivery) throws OrderItemAllocationException {

        if (!CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP.equals(orderDelivery.getDeliveryGroup())) {

            final Collection<CustomerOrderDeliveryDet> deliveryDetails = orderDelivery.getDetail();

            final Map<String, Warehouse> warehouseByCode = warehouseService.getByShopIdMapped(
                    orderDelivery.getCustomerOrder().getShop().getShopId(), false);

            final Date now = now();

            for (CustomerOrderDeliveryDet det : deliveryDetails) {

                final Product product = productService.getProductBySkuCode(det.getProductSkuCode());
                // there may not be this product anymore potentially, so it can be null
                // Null products are treated as AVAILABILITY_STANDARD
                if (product == null || Product.AVAILABILITY_ALWAYS != product.getAvailability()) {

                    final Warehouse selected = warehouseByCode.get(det.getSupplierCode());

                    if (selected == null) {
                        throw new OrderItemAllocationException(
                                "N/A",
                                BigDecimal.ZERO,
                                "PendingOrderEventHandlerImpl. Warehouse '" + det.getSupplierCode() +
                                        "' is not found for " + orderDelivery.getDeliveryNum() + ":" + det.getProductSkuCode());
                    }


                    final String skuCode = det.getProductSkuCode();
                    final BigDecimal toReserve = det.getQty();

                    final int availability;
                    final Date availableFrom;
                    final Date availableTo;
                    if (product != null) {
                        availability = product.getAvailability();
                        availableFrom = product.getAvailablefrom();
                        availableTo = product.getAvailableto();
                    } else { // default behaviour for SKU not in PIM
                        availability = Product.AVAILABILITY_STANDARD;
                        availableFrom = null;
                        availableTo = null;
                    }

                    final boolean preorder = availability == Product.AVAILABILITY_PREORDER && !DomainApiUtils.isObjectAvailableNow(true, availableFrom, availableTo, now) && DomainApiUtils.isObjectAvailableNow(true, null, availableTo, now);
                    final boolean backorder = availability == Product.AVAILABILITY_BACKORDER;

                    final BigDecimal rem = skuWarehouseService.reservation(selected, skuCode, toReserve, backorder || preorder);

                    if (MoneyUtils.isFirstBiggerThanSecond(rem, BigDecimal.ZERO)) {
                        /**
                         * For reservation we always must have stock items with inventory supported availability
                         */
                        throw new OrderItemAllocationException(
                                skuCode,
                                toReserve,
                                "PendingOrderEventHandlerImpl. Can not allocate total qty = " + det.getQty()
                                        + " for sku = " + skuCode
                                        + " in delivery " + orderDelivery.getDeliveryNum());
                    }

                }
            }
        }
        orderDelivery.setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

    }

    Date now() {
        return new Date();
    }

    /**
     * {@inheritDoc}
     */
    protected String getTransitionTarget(final OrderEvent orderEvent) {
        return CustomerOrder.ORDER_STATUS_PENDING;
    }


}