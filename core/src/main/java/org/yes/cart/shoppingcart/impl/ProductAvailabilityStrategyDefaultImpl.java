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
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductAvailabilityModel;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.entity.impl.ProductAvailabilityModelImpl;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.shoppingcart.ProductAvailabilityStrategy;
import org.yes.cart.utils.DomainApiUtils;
import org.yes.cart.utils.TimeContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-04-06
 * Time: 1:20 PM
 */
public class ProductAvailabilityStrategyDefaultImpl implements ProductAvailabilityStrategy, Configuration {

    private final WarehouseService warehouseService;
    private final SkuWarehouseService skuWarehouseService;

    private ConfigurationContext cfgContext;

    public ProductAvailabilityStrategyDefaultImpl(final WarehouseService warehouseService,
                                                  final SkuWarehouseService skuWarehouseService) {
        this.warehouseService = warehouseService;
        this.skuWarehouseService = skuWarehouseService;
    }

    /** {@inheritDoc} */
    @Override
    public ProductAvailabilityModel getAvailabilityModel(final long shopId, final Product product) {

        final List<Warehouse> warehouses = warehouseService.getByShopId(shopId, false);
        final Map<String, BigDecimal> qty = skuWarehouseService.getProductAvailableToSellQuantity(product.getProductId(), warehouses);
        final boolean availableNow = isAvailableNow(
                product.getAvailability(),
                !product.isDisabled(),
                product.getAvailablefrom(),
                product.getAvailableto(),
                qty
        );

        return new ProductAvailabilityModelImpl(
                product.getDefaultSku().getCode(),
                product.getAvailability(),
                availableNow,
                qty);
    }

    /** {@inheritDoc} */
    @Override
    public ProductAvailabilityModel getAvailabilityModel(final long shopId, final ProductSearchResultDTO product) {

        final Map<String, BigDecimal> skuInventory = product.getQtyOnWarehouse(shopId);
        final boolean availableNow = isAvailableNow(
                product.getAvailability(),
                true,
                product.getAvailablefrom(),
                product.getAvailableto(),
                skuInventory
        );

        return new ProductAvailabilityModelImpl(
                product.getDefaultSkuCode(),
                product.getAvailability(),
                availableNow,
                skuInventory);
    }

    /** {@inheritDoc} */
    @Override
    public ProductAvailabilityModel getAvailabilityModel(final long shopId, final ProductSku sku) {

        final Product product = sku.getProduct();
        final List<Warehouse> warehouses = warehouseService.getByShopId(shopId, false);
        final Map<String, BigDecimal> qty = skuWarehouseService.getProductSkuAvailableToSellQuantity(sku.getCode(), warehouses);
        final boolean availableNow = isAvailableNow(
                product.getAvailability(),
                !product.isDisabled(),
                product.getAvailablefrom(),
                product.getAvailableto(),
                qty
        );

        return new ProductAvailabilityModelImpl(
                sku.getCode(),
                product.getAvailability(),
                availableNow,
                qty);
    }

    /** {@inheritDoc} */
    @Override
    public ProductAvailabilityModel getAvailabilityModel(final long shopId, final String skuCode) {

        final List<Warehouse> warehouses = warehouseService.getByShopId(shopId, false);
        final Map<String, BigDecimal> qty = skuWarehouseService.getProductSkuAvailableToSellQuantity(skuCode, warehouses);
        final boolean availableNow = true;

        return new ProductAvailabilityModelImpl(
                skuCode,
                Product.AVAILABILITY_STANDARD,
                availableNow,
                qty);
    }

    /**
     * Hook to override availability flag in the model.
     *
     * @param availability type of availability
     * @param enabled      enabled flag
     * @param from         start time active
     * @param to           end time active
     * @param qty          quantity on stock map
     *
     * @return true if this product to be considered available
     */
    protected boolean isAvailableNow(final int availability,
                                     final boolean enabled,
                                     final LocalDateTime from,
                                     final LocalDateTime to,
                                     final Map<String, BigDecimal> qty) {
        final LocalDateTime now = now();
        if (availability == Product.AVAILABILITY_PREORDER) {
            return DomainApiUtils.isObjectAvailableNow(enabled, null, to, now);
        }
        return DomainApiUtils.isObjectAvailableNow(enabled, from, to, now);
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
