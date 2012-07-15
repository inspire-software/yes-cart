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

import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CancelOrderEventHandlerImpl extends AbstractOrderEventHandlerImpl implements OrderEventHandler {


    private final WarehouseService warehouseService;
    private final SkuWarehouseService skuWarehouseService;


    /**
     * @param warehouseService    to locate warehouse, that belong to shop where order was created
     * @param skuWarehouseService to credit quantity on warehouse
     */
    public CancelOrderEventHandlerImpl(
            final WarehouseService warehouseService,
            final SkuWarehouseService skuWarehouseService) {
        this.warehouseService = warehouseService;
        this.skuWarehouseService = skuWarehouseService;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) {
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


    protected void creditQuantity(final CustomerOrder order) {
        final Collection<CustomerOrderDelivery> deliveries = order.getDelivery();
        for (CustomerOrderDelivery delivery : deliveries) {
            creditQuantity(delivery);
        }
    }

    private void creditQuantity(final CustomerOrderDelivery delivery) {

        final List<Warehouse> warehouses = warehouseService.findByShopId(
                delivery.getCustomerOrder().getShop().getShopId());

        String newStatus = null;
        if (isNeedVoidReservation(delivery.getDeliveryStatus())) {
            newStatus = CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION;
        } else if (isNeedCredit(delivery.getDeliveryStatus())) {
            newStatus = CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_DEALLOCATED;

        }

        for (CustomerOrderDeliveryDet det : delivery.getDetail()) {
            final ProductSku productSku = det.getSku();
            BigDecimal toCredit = det.getQty();
            for (Warehouse wh : warehouses) {
                if (isNeedVoidReservation(delivery.getDeliveryStatus())) {
                    // this delivery was not completed, so can just void reservation
                    toCredit = skuWarehouseService.voidReservation(wh, productSku, toCredit);
                } else if (isNeedCredit(delivery.getDeliveryStatus())) {
                    // this delivery is completed, so need to credit qty

                    toCredit = skuWarehouseService.credit(wh, productSku, toCredit);
                }
                if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), toCredit.setScale(Constants.DEFAULT_SCALE))) {
                    break;
                }
            }

        }
        delivery.setDeliveryStatus(newStatus);
    }

    private boolean isNeedVoidReservation(final String deliveryStatus) {
        return CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED.equals(deliveryStatus);
    }

    private boolean isNeedCredit(final String deliveryStatus) {
        return
                CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED.equals(deliveryStatus)
                        || CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED.equals(deliveryStatus)
                        || CustomerOrderDelivery.DELIVERY_STATUS_PACKING.equals(deliveryStatus)
                        || CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY.equals(deliveryStatus)
                        || CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS.equals(deliveryStatus)
                        || CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED.equals(deliveryStatus);

    }

}

