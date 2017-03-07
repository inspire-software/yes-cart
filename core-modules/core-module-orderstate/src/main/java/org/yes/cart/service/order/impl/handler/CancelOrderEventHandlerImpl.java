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
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CancelOrderEventHandlerImpl extends AbstractOrderEventHandlerImpl implements OrderEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CancelOrderEventHandlerImpl.class);

    private final WarehouseService warehouseService;
    private final SkuWarehouseService skuWarehouseService;
    private final ProductService productService;


    /**
     * @param warehouseService    to locate warehouse, that belong to shop where order was created
     * @param skuWarehouseService to credit quantity on warehouse
     * @param productService      product service
     */
    public CancelOrderEventHandlerImpl(
            final WarehouseService warehouseService,
            final SkuWarehouseService skuWarehouseService,
            final ProductService productService) {
        this.warehouseService = warehouseService;
        this.skuWarehouseService = skuWarehouseService;
        this.productService = productService;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) throws OrderException  {
        synchronized (OrderEventHandler.syncMonitor) {
            creditQuantity(orderEvent.getCustomerOrder());
            handleInternal(orderEvent);
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String getTransitionTarget(final OrderEvent orderEvent) {
        return CustomerOrder.ORDER_STATUS_CANCELLED;
    }


    protected void creditQuantity(final CustomerOrder order) throws OrderException  {
        final Collection<CustomerOrderDelivery> deliveries = order.getDelivery();
        for (CustomerOrderDelivery delivery : deliveries) {
            if (!this.isAlreadyCancelled(delivery.getDeliveryStatus())) {
                creditQuantity(delivery);
            }
        }
    }

    private void creditQuantity(final CustomerOrderDelivery delivery) throws OrderException {

        final String newStatus;

        final boolean voidReservation = isNeedVoidReservation(delivery.getDeliveryStatus());
        final boolean voidCredit = isNeedCredit(delivery.getDeliveryStatus());

        if (voidReservation) {
            // Voiding reservation, so that we do not prevent customers from ordering available stock
            newStatus = CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION;
        } else if (voidCredit) {
            // Allocation already happened so we deallocate back (items not yet left warehouse)
            newStatus = CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_DEALLOCATED;
        } else if (isFulfillment(delivery.getDeliveryStatus())) {
            // No reservation yet was made so just setting the status
            newStatus = CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT;
        } else if (isNeedReturn(delivery.getDeliveryStatus())) {
            // Returned items from customers. No deallocation here since we cannot blindly put the
            // quantity back. Item could have been returned due to being faulty and may not be sellable
            // anymore. Updating stock in this case should be manual after the returned items are checked
            newStatus = CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RETURNED;
        } else {
            throw new OrderException("Unable to handle cancellation for delivery " + delivery.getDeliveryNum() + " with status " + delivery.getDeliveryStatus());
        }

        if (voidCredit || voidReservation) {

            final Map<String, Warehouse> warehouseByCode = warehouseService.getByShopIdMapped(
                    delivery.getCustomerOrder().getShop().getShopId(), false);

            for (CustomerOrderDeliveryDet det : delivery.getDetail()) {

                final String skuCode = det.getProductSkuCode();
                final BigDecimal toCredit = det.getQty();

                final Product product = productService.getProductBySkuCode(det.getProductSkuCode());

                if (product == null || Product.AVAILABILITY_ALWAYS != product.getAvailability()) {

                    final Warehouse selected = warehouseByCode.get(det.getSupplierCode());

                    if (selected == null) {
                        LOG.warn(
                                "Warehouse is not found for delivery detail {}:{}",
                                delivery.getDeliveryNum(), det.getProductSkuCode()
                        );
                    } else {

                        if (voidReservation) {
                            // this delivery was not completed, so can just void reservation
                            final BigDecimal rem = skuWarehouseService.voidReservation(selected, skuCode, toCredit);
                            if (MoneyUtils.isFirstBiggerThanSecond(rem, BigDecimal.ZERO)) {
                                LOG.warn(
                                        "Could not void all reservation {}:{}",
                                        delivery.getDeliveryNum(), det.getProductSkuCode()
                                );
                            }
                        } else if (voidCredit) {
                            // this delivery is completed, so need to credit qty
                            final BigDecimal rem = skuWarehouseService.credit(selected, skuCode, toCredit);
                            if (MoneyUtils.isFirstBiggerThanSecond(rem, BigDecimal.ZERO)) {
                                LOG.warn(
                                        "Could not credit all reservation {}:{}",
                                        delivery.getDeliveryNum(), det.getProductSkuCode()
                                );
                            }
                        }

                    }
                }

            }
        }
        delivery.setDeliveryStatus(newStatus);
    }

    protected boolean isAlreadyCancelled(final String deliveryStatus) {
        return CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION.equals(deliveryStatus)
                || CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT.equals(deliveryStatus)
                || CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_DEALLOCATED.equals(deliveryStatus)
                || CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RETURNED.equals(deliveryStatus);
    }

    protected boolean isFulfillment(final String deliveryStatus) {
        return CustomerOrderDelivery.DELIVERY_STATUS_ON_FULLFILMENT.equals(deliveryStatus);
    }

    protected boolean isNeedVoidReservation(final String deliveryStatus) {
        return CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED.equals(deliveryStatus)
                || CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT.equals(deliveryStatus)
                || CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT.equals(deliveryStatus)
                || CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT.equals(deliveryStatus);
    }

    protected boolean isNeedCredit(final String deliveryStatus) {
        return CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED.equals(deliveryStatus)
                || CustomerOrderDelivery.DELIVERY_STATUS_PACKING.equals(deliveryStatus)
                || CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY.equals(deliveryStatus)
                || CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT.equals(deliveryStatus);

    }

    protected boolean isNeedReturn(final String deliveryStatus) {
        return false; // This is basic cancel with cannot perform refunds
    }

}

