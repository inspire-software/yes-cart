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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.config.Configuration;
import org.yes.cart.config.ConfigurationContext;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.ProductAvailabilityModelImpl;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.shoppingcart.InventoryResolver;
import org.yes.cart.shoppingcart.ProductAvailabilityStrategy;
import org.yes.cart.utils.DomainApiUtils;
import org.yes.cart.utils.TimeContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * User: denispavlov
 * Date: 13-04-06
 * Time: 1:20 PM
 */
public class ProductAvailabilityStrategyDefaultImpl implements ProductAvailabilityStrategy, Configuration {

    private final WarehouseService warehouseService;
    private final InventoryResolver inventoryResolver;

    private ConfigurationContext cfgContext;

    public ProductAvailabilityStrategyDefaultImpl(final WarehouseService warehouseService,
                                                  final InventoryResolver inventoryResolver) {
        this.warehouseService = warehouseService;
        this.inventoryResolver = inventoryResolver;
    }

    /** {@inheritDoc} */
    @Override
    public ProductAvailabilityModel getAvailabilityModel(final long shopId,
                                                         final Product product,
                                                         final String supplier) {

        final Map<String, BigDecimal> qty = new HashMap<>();
        final Map<String, Warehouse> warehouses = warehouseService.getByShopIdMapped(shopId, false);

        if (!warehouses.isEmpty()) {

            final Warehouse warehouse = StringUtils.isBlank(supplier) ? warehouses.values().iterator().next() : warehouses.get(supplier);

            final SortedMap<Integer, SkuWarehouse> rankedInventory = new TreeMap<>();
            for (final ProductSku sku : product.getSku()) {

                final SkuWarehouse inventory = inventoryResolver.findByWarehouseSku(warehouse, sku.getCode());
                if (inventory != null) {
                    rankedInventory.put(sku.getRank(), inventory);
                    qty.put(inventory.getSkuCode(), inventory.getAvailableToSell());
                }

            }

            if (!rankedInventory.isEmpty()) {
                for (final SkuWarehouse first : rankedInventory.values()) {

                    final boolean availableNow = isAvailableNow(
                            first.getAvailability(),
                            !first.isDisabled(),
                            first.getAvailablefrom(),
                            first.getAvailableto(),
                            first.getReleaseDate(),
                            qty
                    );

                    return new ProductAvailabilityModelImpl(
                            warehouse.getCode(),
                            first.getSkuCode(),
                            first.getAvailability(),
                            availableNow,
                            first.getReleaseDate(),
                            qty,
                            first.getRestockDate(),
                            first.getRestockNote()
                    );
                }
            }
        }

        return new ProductAvailabilityModelImpl(
                supplier,
                null,
                SkuWarehouse.AVAILABILITY_NA,
                false,
                null,
                qty,
                null,
                null
        );
    }

    /** {@inheritDoc} */
    @Override
    public ProductAvailabilityModel getAvailabilityModel(final long shopId,
                                                         final ProductSearchResultDTO product) {

        final Map<String, BigDecimal> skuInventory = product.getQtyOnWarehouse(shopId);
        final boolean availableNow = isAvailableNow(
                product.getAvailability(),
                true,
                product.getAvailablefrom(),
                product.getAvailableto(),
                product.getReleaseDate(),
                skuInventory
        );

        return new ProductAvailabilityModelImpl(
                product.getFulfilmentCentreCode(),
                product.getDefaultSkuCode(),
                product.getAvailability(),
                availableNow,
                product.getReleaseDate(),
                skuInventory,
                product.getRestockDate(),
                product.getRestockNotes()
        );
    }

    /** {@inheritDoc} */
    @Override
    public ProductAvailabilityModel getAvailabilityModel(final long shopId,
                                                         final ProductSku sku,
                                                         final String supplier) {

        return getAvailabilityModel(shopId, sku.getCode(), supplier);

    }

    /** {@inheritDoc} */
    @Override
    public ProductAvailabilityModel getAvailabilityModel(final long shopId,
                                                         final ProductSkuSearchResultDTO sku) {

        final BigDecimal inventory = sku.getQtyOnWarehouse(shopId);
        final Map<String, BigDecimal> skuInventory = Collections.singletonMap(sku.getCode(), inventory);
        final boolean availableNow = isAvailableNow(
                sku.getAvailability(),
                true,
                sku.getAvailablefrom(),
                sku.getAvailableto(),
                sku.getReleaseDate(),
                skuInventory
        );

        return new ProductAvailabilityModelImpl(
                sku.getFulfilmentCentreCode(),
                sku.getCode(),
                sku.getAvailability(),
                availableNow,
                sku.getReleaseDate(),
                skuInventory,
                sku.getRestockDate(),
                sku.getRestockNotes()
        );
    }

    /** {@inheritDoc} */
    @Override
    public ProductAvailabilityModel getAvailabilityModel(final long shopId,
                                                         final String skuCode,
                                                         final String supplier) {

        final Map<String, Warehouse> warehouses = warehouseService.getByShopIdMapped(shopId, false);
        final Warehouse warehouse = warehouses.get(supplier);
        int availability = SkuWarehouse.AVAILABILITY_NA;
        boolean availableNow = false;
        LocalDateTime releaseDate = null;
        final Map<String, BigDecimal> qty = new HashMap<>();
        LocalDateTime restockDate = null;
        I18NModel restockNotes = null;

        if (warehouse != null) {
            final SkuWarehouse inventory = inventoryResolver.findByWarehouseSku(warehouse, skuCode);
            if (inventory != null) {
                availability = inventory.getAvailability();
                qty.put(skuCode, inventory.getAvailableToSell());
                availableNow = isAvailableNow(
                        availability,
                        !inventory.isDisabled(),
                        inventory.getAvailablefrom(),
                        inventory.getAvailableto(),
                        inventory.getReleaseDate(),
                        qty
                );
                releaseDate = inventory.getReleaseDate();
                restockDate = inventory.getRestockDate();
                restockNotes = inventory.getRestockNote();
            }
        }

        return new ProductAvailabilityModelImpl(
                supplier,
                skuCode,
                availability,
                availableNow,
                releaseDate,
                qty,
                restockDate,
                restockNotes
        );
    }

    /**
     * Hook to override availability flag in the model.
     *
     * @param availability type of availability
     * @param enabled      enabled flag
     * @param from         start time active
     * @param to           end time active
     * @param releaseDate  release date
     * @param qty          quantity on stock map
     *
     * @return true if this product to be considered available
     */
    protected boolean isAvailableNow(final int availability,
                                     final boolean enabled,
                                     final LocalDateTime from,
                                     final LocalDateTime to,
                                     final LocalDateTime releaseDate,
                                     final Map<String, BigDecimal> qty) {

        return DomainApiUtils.isObjectAvailableNow(enabled, from, to, now());

    }

    /**
     * @return current time
     */
    protected LocalDateTime now() {
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
