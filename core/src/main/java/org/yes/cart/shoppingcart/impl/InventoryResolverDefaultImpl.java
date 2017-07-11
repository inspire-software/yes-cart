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

import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.shoppingcart.InventoryResolver;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 11/07/2017
 * Time: 14:21
 */
public class InventoryResolverDefaultImpl implements InventoryResolver {

    private final SkuWarehouseService skuWarehouseService;

    public InventoryResolverDefaultImpl(final SkuWarehouseService skuWarehouseService) {
        this.skuWarehouseService = skuWarehouseService;
    }

    @Override
    public BigDecimal reservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal reserveQty) {
        return skuWarehouseService.reservation(warehouse, productSkuCode, reserveQty);
    }

    @Override
    public BigDecimal reservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal reserveQty, final boolean allowBackorder) {
        return skuWarehouseService.reservation(warehouse, productSkuCode, reserveQty, allowBackorder);
    }

    @Override
    public BigDecimal voidReservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal voidQty) {
        return skuWarehouseService.voidReservation(warehouse, productSkuCode, voidQty);
    }

    @Override
    public BigDecimal debit(final Warehouse warehouse, final String productSkuCode, final BigDecimal debitQty) {
        return skuWarehouseService.debit(warehouse, productSkuCode, debitQty);
    }

    @Override
    public BigDecimal credit(final Warehouse warehouse, final String productSkuCode, final BigDecimal addQty) {
        return skuWarehouseService.credit(warehouse, productSkuCode, addQty);
    }

    @Override
    public SkuWarehouse findByWarehouseSku(final Warehouse warehouse, final String productSkuCode) {
        return skuWarehouseService.findByWarehouseSku(warehouse, productSkuCode);
    }
}
