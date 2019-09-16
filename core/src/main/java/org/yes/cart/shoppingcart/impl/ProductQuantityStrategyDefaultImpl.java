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

package org.yes.cart.shoppingcart.impl;

import org.yes.cart.config.Configuration;
import org.yes.cart.config.ConfigurationContext;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.QuantityModelImpl;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.shoppingcart.InventoryResolver;
import org.yes.cart.shoppingcart.ProductQuantityStrategy;
import org.yes.cart.utils.TimeContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * User: denispavlov
 * Date: 25/10/2014
 * Time: 21:05
 */
public class ProductQuantityStrategyDefaultImpl implements ProductQuantityStrategy, Configuration {

    private final WarehouseService warehouseService;
    private final InventoryResolver inventoryResolver;

    private ConfigurationContext cfgContext;

    public ProductQuantityStrategyDefaultImpl(final WarehouseService warehouseService,
                                              final InventoryResolver inventoryResolver) {
        this.warehouseService = warehouseService;
        this.inventoryResolver = inventoryResolver;
    }

    /** {@inheritDoc} */
    @Override
    public QuantityModel getQuantityModel(final long shopId,
                                          final BigDecimal cartQty,
                                          final Product product,
                                          final String supplier) {

        final Map<String, Warehouse> warehouses = warehouseService.getByShopIdMapped(shopId, false);
        final Warehouse warehouse = warehouses.get(supplier);
        final Map<String, BigDecimal> qty = new HashMap<>();

        final SortedMap<Integer, SkuWarehouse> rankedInventory = new TreeMap<>();
        for (final ProductSku sku : product.getSku()) {

            final SkuWarehouse inventory = inventoryResolver.findByWarehouseSku(warehouse, sku.getCode());
            if (inventory != null) {
                rankedInventory.put(sku.getRank(), inventory);
                qty.put(inventory.getSkuCode(), inventory.getAvailableToSell());
            }

        }

        if (!rankedInventory.isEmpty()) {
            final LocalDateTime now = now();
            for (final SkuWarehouse first : rankedInventory.values()) {

                final boolean availableNow = first.isAvailable(now);
                if (availableNow) {
                    return new QuantityModelImpl(
                            supplier,
                            first.getSkuCode(),
                            first.getMinOrderQuantity(),
                            first.getMaxOrderQuantity(),
                            first.getStepOrderQuantity(),
                            cartQty
                    );
                }
            }
        }

        return new QuantityModelImpl(
                supplier,
                product.getCode(),
                null,
                null,
                null,
                cartQty
        );
    }

    /** {@inheritDoc} */
    @Override
    public QuantityModel getQuantityModel(final long shopId,
                                          final BigDecimal cartQty,
                                          final ProductSku productSku,
                                          final String supplier) {

        return getQuantityModel(shopId, cartQty, productSku.getCode(), supplier);

    }


    /** {@inheritDoc} */
    @Override
    public QuantityModel getQuantityModel(final long shopId,
                                          final BigDecimal cartQty,
                                          final ProductSearchResultDTO product) {

        return new QuantityModelImpl(
                product.getFulfilmentCentreCode(),
                product.getDefaultSkuCode(),
                product.getMinOrderQuantity(),
                product.getMaxOrderQuantity(),
                product.getStepOrderQuantity(),
                cartQty
        );

    }


    /** {@inheritDoc} */
    @Override
    public QuantityModel getQuantityModel(final long shopId,
                                          final BigDecimal cartQty,
                                          final ProductSkuSearchResultDTO sku) {

        return new QuantityModelImpl(
                sku.getFulfilmentCentreCode(),
                sku.getCode(),
                sku.getMinOrderQuantity(),
                sku.getMaxOrderQuantity(),
                sku.getStepOrderQuantity(),
                cartQty
        );

    }


    @Override
    public QuantityModel getQuantityModel(final long shopId,
                                          final BigDecimal cartQty,
                                          final String sku,
                                          final String supplier) {

        final Map<String, Warehouse> warehouses = warehouseService.getByShopIdMapped(shopId, false);
        final Warehouse warehouse = warehouses.get(supplier);

        if (warehouse != null) {
            final SkuWarehouse inventory = inventoryResolver.findByWarehouseSku(warehouse, sku);
            if (inventory != null) {

                return new QuantityModelImpl(
                        supplier,
                        sku,
                        inventory.getMinOrderQuantity(),
                        inventory.getMaxOrderQuantity(),
                        inventory.getStepOrderQuantity(),
                        cartQty
                );

            }
        }

        return new QuantityModelImpl(
                supplier,
                sku,
                null,
                null,
                null,
                cartQty
        );
    }

    /** {@inheritDoc} */
    @Override
    public QuantityModel getQuantityModel(final long shopId,
                                          final BigDecimal cartQty,
                                          final String sku,
                                          final BigDecimal min,
                                          final BigDecimal max,
                                          final BigDecimal step,
                                          final String supplier) {

        return new QuantityModelImpl(
                supplier,
                sku,
                min,
                max,
                step,
                cartQty
        );

    }

    private LocalDateTime now() {
        return TimeContext.getLocalDateTime();
    }


    /** {@inheritDoc} */
    @Override
    public ConfigurationContext getCfgContext() {
        return cfgContext;
    }

    public void setCfgContext(final ConfigurationContext cfgContext) {
        this.cfgContext = cfgContext;
    }

}
