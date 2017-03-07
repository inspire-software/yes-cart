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
import org.yes.cart.service.order.OrderItemAllocationException;
import org.yes.cart.util.ShopCodeContext;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DeliveryAllowedByInventoryOrderEventHandlerImpl extends ProcessAllocationOrderEventHandlerImpl
        implements OrderEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryAllowedByInventoryOrderEventHandlerImpl.class);

    private final ProductService productService;

    /**
     * Construct transition
     *
     * @param warehouseService    warehouse service
     * @param skuWarehouseService sku on warehouse service to change quantity
     * @param productService      product service
     */
    public DeliveryAllowedByInventoryOrderEventHandlerImpl(final WarehouseService warehouseService,
                                                           final SkuWarehouseService skuWarehouseService,
                                                           final ProductService productService) {
        super(warehouseService, skuWarehouseService, productService);
        this.productService = productService;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) throws OrderItemAllocationException {
        synchronized (OrderEventHandler.syncMonitor) {

            final CustomerOrderDelivery orderDelivery = orderEvent.getCustomerOrderDelivery();

            if (!CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP.equals(orderDelivery.getDeliveryGroup())) {

                final Map<String, Warehouse> warehouseByCode = getWarehouseService().getByShopIdMapped(
                        orderEvent.getCustomerOrder().getShop().getShopId(), false);

                // If this delivery is physical then try inventory
                for (CustomerOrderDeliveryDet det : orderDelivery.getDetail()) {

                    final Product product = productService.getProductBySkuCode(det.getProductSkuCode());
                    // there may not be this product anymore potentially, so it can be null
                    // Null products are treated as AVAILABILITY_STANDARD
                    if (product == null || Product.AVAILABILITY_ALWAYS != product.getAvailability()) {

                        final Warehouse selected = warehouseByCode.get(det.getSupplierCode());

                        if (selected == null) {
                            LOG.warn(
                                    "Warehouse is not found for delivery detail {}:{}",
                                    orderDelivery.getDeliveryNum(), det.getProductSkuCode()
                            );
                            return false; // warehouse is disabled or not available
                        }

                        final SkuWarehouse stock = getSkuWarehouseService().findByWarehouseSku(
                                selected,
                                det.getProductSkuCode()
                        );
                        if (stock == null || !stock.isAvailableToAllocate(det.getQty())) {
                            LOG.info(
                                    "Not enough stock for delivery detail {}:{}",
                                    orderDelivery.getDeliveryNum(), det.getProductSkuCode()
                            );
                            return false; //inventory is less than we can give for this order
                        }
                    }
                }
            }
            return super.handle(orderEvent);
        }
    }


}
