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

package org.yes.cart.service.domain.impl;

import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductAvailabilityModel;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.entity.impl.ProductAvailabilityModelImpl;
import org.yes.cart.service.domain.ProductAvailabilityStrategy;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.util.DomainApiUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-04-06
 * Time: 1:20 PM
 */
public class ProductAvailabilityStrategyImpl implements ProductAvailabilityStrategy {

    private final WarehouseService warehouseService;
    private final SkuWarehouseService skuWarehouseService;

    public ProductAvailabilityStrategyImpl(final WarehouseService warehouseService,
                                           final SkuWarehouseService skuWarehouseService) {
        this.warehouseService = warehouseService;
        this.skuWarehouseService = skuWarehouseService;
    }

    /** {@inheritDoc} */
    public ProductAvailabilityModel getAvailabilityModel(final long shopId, final Product product) {

        final List<Warehouse> warehouses = warehouseService.getByShopId(shopId, false);
        final Map<String, BigDecimal> qty = skuWarehouseService.getProductAvailableToSellQuantity(product.getProductId(), warehouses);
        final boolean availableNow = isAvailableNow(product.getAvailability(), product.getAvailablefrom(), product.getAvailableto());

        return new ProductAvailabilityModelImpl(
                product.getDefaultSku().getCode(),
                product.getAvailability(),
                availableNow,
                qty);
    }

    /** {@inheritDoc} */
    public ProductAvailabilityModel getAvailabilityModel(final long shopId, final ProductSearchResultDTO product) {

        final Map<String, BigDecimal> skuInventory = product.getQtyOnWarehouse(shopId);
        final boolean availableNow = isAvailableNow(product.getAvailability(), product.getAvailablefrom(), product.getAvailableto());

        return new ProductAvailabilityModelImpl(
                product.getDefaultSkuCode(),
                product.getAvailability(),
                availableNow,
                skuInventory);
    }

    /** {@inheritDoc} */
    public ProductAvailabilityModel getAvailabilityModel(final long shopId, final ProductSku sku) {

        final Product product = sku.getProduct();
        final List<Warehouse> warehouses = warehouseService.getByShopId(shopId, false);
        final Map<String, BigDecimal> qty = skuWarehouseService.getProductSkuAvailableToSellQuantity(sku.getCode(), warehouses);
        final boolean availableNow = isAvailableNow(product.getAvailability(), product.getAvailablefrom(), product.getAvailableto());

        return new ProductAvailabilityModelImpl(
                sku.getCode(),
                product.getAvailability(),
                availableNow,
                qty);
    }

    /** {@inheritDoc} */
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

    private boolean isAvailableNow(final int availability, final Date from, final Date to) {
        final Date now = new Date();
        if (availability == Product.AVAILABILITY_PREORDER) {
            return DomainApiUtils.isObjectAvailableNow(true, null, to, now);
        }
        return DomainApiUtils.isObjectAvailableNow(true, from, to, now);
    }

}
