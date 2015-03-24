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
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.OrderItemAllocationException;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProcessAllocationOrderEventHandlerImpl implements OrderEventHandler {

    private final WarehouseService warehouseService;

    private final SkuWarehouseService skuWarehouseService;

    private final ProductService productService;

    /**
     * Construct transition.
     *
     * @param warehouseService    warehouse service
     * @param skuWarehouseService sku on warehouse service to change quantity
     * @param productService      product service
     */
    public ProcessAllocationOrderEventHandlerImpl(final WarehouseService warehouseService,
                                                  final SkuWarehouseService skuWarehouseService,
                                                  final ProductService productService) {
        this.warehouseService = warehouseService;
        this.skuWarehouseService = skuWarehouseService;
        this.productService = productService;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) throws OrderItemAllocationException {
        synchronized (OrderEventHandler.syncMonitor) {
            reserveQuantity(orderEvent.getCustomerOrderDelivery());
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
    protected SkuWarehouseService getSkuWarehouseService() {
        return skuWarehouseService;
    }



    /**
     * Allocate sku quantity on warehouses, that belong to shop, where order was made.
     *
     * @param orderDelivery reserve for this delivery
     * @throws OrderItemAllocationException in case if can not allocate quantity for each sku
     */
    void reserveQuantity(final CustomerOrderDelivery orderDelivery) throws OrderItemAllocationException {


        final Collection<CustomerOrderDeliveryDet> deliveryDetails = orderDelivery.getDetail();

        final List<Warehouse> warehouses = warehouseService.getByShopId(
                orderDelivery.getCustomerOrder().getShop().getShopId());

        for (CustomerOrderDeliveryDet det : deliveryDetails) {

            String skuCode = det.getProductSkuCode();
            BigDecimal toAllocate = det.getQty();

            for (Warehouse warehouse : warehouses) {

                skuWarehouseService.voidReservation(warehouse, skuCode, toAllocate);

                toAllocate = skuWarehouseService.debit(warehouse, skuCode, toAllocate);

                if (MoneyUtils.isFirstEqualToSecond(toAllocate, BigDecimal.ZERO, Constants.DEFAULT_SCALE)) {

                    break; // quantity allocated
                }
                
            }
            if (MoneyUtils.isFirstBiggerThanSecond(toAllocate, BigDecimal.ZERO)) {

                final Product product = productService.getProductBySkuCode(det.getProductSkuCode());

                if (product == null || Product.AVAILABILITY_STANDARD == product.getAvailability()) {

                    /**
                     * Availability.AVAILABILITY_BACKORDER -  can get more stock
                     * Availability.AVAILABILITY_PREORDER - can pre-order from manufacturer
                     * Availability.AVAILABILITY_ALWAYS - always
                     */
                    throw new OrderItemAllocationException(
                        skuCode,
                        toAllocate,
                        "ProcessAllocationOrderEventHandlerImpl. Can not allocate total qty = " + det.getQty()
                                + " for sku = " + skuCode
                                + " in delivery " + orderDelivery.getDeliveryNum());
                }
            }
        }

        orderDelivery.setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED);

    }




}
