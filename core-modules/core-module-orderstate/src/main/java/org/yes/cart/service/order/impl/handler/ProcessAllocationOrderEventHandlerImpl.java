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
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.OrderItemAllocationException;
import org.yes.cart.shoppingcart.InventoryResolver;
import org.yes.cart.utils.MoneyUtils;
import org.yes.cart.utils.log.Markers;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProcessAllocationOrderEventHandlerImpl implements OrderEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessAllocationOrderEventHandlerImpl.class);

    private final WarehouseService warehouseService;

    private final InventoryResolver inventoryResolver;

    private final ProductService productService;

    /**
     * Construct transition.
     *
     * @param warehouseService    warehouse service
     * @param inventoryResolver sku on warehouse service to change quantity
     * @param productService      product service
     */
    public ProcessAllocationOrderEventHandlerImpl(final WarehouseService warehouseService,
                                                  final InventoryResolver inventoryResolver,
                                                  final ProductService productService) {
        this.warehouseService = warehouseService;
        this.inventoryResolver = inventoryResolver;
        this.productService = productService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handle(final OrderEvent orderEvent) throws OrderItemAllocationException {
        synchronized (OrderEventHandler.syncMonitor) {
            allocateQuantity(orderEvent.getCustomerOrderDelivery());
            return true;
        }
    }


    /**
     * Get warehouse service.
     *
     * @return {@link WarehouseService}
     */
    protected WarehouseService getWarehouseService() {
        return warehouseService;
    }

    /**
     * Get sku warehouse service.
     *
     * @return {@link SkuWarehouseService}
     */
    protected InventoryResolver getInventoryResolver() {
        return inventoryResolver;
    }



    /**
     * Allocate sku quantity on warehouses, that belong to shop, where order was made.
     *
     * @param orderDelivery reserve for this delivery
     * @throws OrderItemAllocationException in case if can not allocate quantity for each sku
     */
    void allocateQuantity(final CustomerOrderDelivery orderDelivery) throws OrderItemAllocationException {

        if (!CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP.equals(orderDelivery.getDeliveryGroup())) {

            final Collection<CustomerOrderDeliveryDet> deliveryDetails = orderDelivery.getDetail();

            final Map<String, Warehouse> warehouseByCode = warehouseService.getByShopIdMapped(
                    orderDelivery.getCustomerOrder().getShop().getShopId(), false);

            for (CustomerOrderDeliveryDet det : deliveryDetails) {

                final String skuCode = det.getProductSkuCode();
                final BigDecimal toAllocate = det.getQty();
                final Warehouse selected = warehouseByCode.get(det.getSupplierCode());

                if (selected == null) {
                    LOG.error(
                            Markers.alert(),
                            "Warehouse is not found for delivery detail {}:{}",
                            orderDelivery.getDeliveryNum(), det.getProductSkuCode()
                    );

                    /*
                     * For allocation we always must have stock items with inventory supported availability
                     */
                    throw new OrderItemAllocationException(
                            skuCode,
                            toAllocate,
                            "ProcessAllocationOrderEventHandlerImpl. Warehouse not found. Total qty = " + det.getQty()
                                    + " for sku = " + skuCode
                                    + " in delivery " + orderDelivery.getDeliveryNum());
                }

                final BigDecimal rem = inventoryResolver.debit(selected, skuCode, toAllocate);

                if (MoneyUtils.isPositive(rem)) {
                    /*
                     * For allocation we always must have stock items with inventory supported availability
                     */
                    throw new OrderItemAllocationException(
                            skuCode,
                            toAllocate,
                            "ProcessAllocationOrderEventHandlerImpl. Insufficient total qty = " + det.getQty()
                                    + " for sku = " + skuCode
                                    + " in delivery " + orderDelivery.getDeliveryNum());
                }

                // Void reservation on quantity debited
                inventoryResolver.voidReservation(selected, skuCode, toAllocate);

            }
        }

        orderDelivery.setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED);

    }




}
