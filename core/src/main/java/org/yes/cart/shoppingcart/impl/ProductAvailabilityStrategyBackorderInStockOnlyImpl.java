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
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.shoppingcart.InventoryResolver;
import org.yes.cart.shoppingcart.ProductAvailabilityStrategy;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 18/08/2019
 * Time: 11:36
 */
public class ProductAvailabilityStrategyBackorderInStockOnlyImpl extends ProductAvailabilityStrategyDefaultImpl
        implements ProductAvailabilityStrategy, Configuration {

    public ProductAvailabilityStrategyBackorderInStockOnlyImpl(final WarehouseService warehouseService,
                                                               final InventoryResolver inventoryResolver) {
        super(warehouseService, inventoryResolver);
    }

    @Override
    protected boolean isAvailableNow(final int availability,
                                     final boolean enabled,
                                     final LocalDateTime from,
                                     final LocalDateTime to,
                                     final LocalDateTime releaseDate,
                                     final Map<String, BigDecimal> qty) {

        final boolean available = super.isAvailableNow(availability, enabled, from, to, releaseDate, qty);

        if (available && availability == SkuWarehouse.AVAILABILITY_BACKORDER) {

            for (final BigDecimal qtyItem : qty.values()) {
                if (MoneyUtils.isPositive(qtyItem)) {
                    return true; // in stock on at least one inventory record
                }
            }

            return false; // no stock - force unavailable flag

        }

        return available;

    }
}
