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

package org.yes.cart.domain.entity.impl;

import org.yes.cart.domain.entity.ProductAvailabilityModel;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * User: denispavlov
 * Date: 13-04-06
 * Time: 1:13 PM
 */
public class ProductAvailabilityModelImpl implements ProductAvailabilityModel {

    private static final BigDecimal PERPETUAL = new BigDecimal(Integer.MAX_VALUE);

    private static final SortedSet<String> NO_SKU = new TreeSet<>();

    private final String supplier;
    private final boolean available;
    private final int availability;
    private boolean inStock;
    private final boolean perpetual;
    private final Map<String, BigDecimal> availableToSellQuantity;
    private final SortedSet<String> skuCodes;
    private final String defaultSku;
    private final LocalDateTime releaseDate;
    private String firstAvailableSku = null;

    public ProductAvailabilityModelImpl(final String supplier,
                                        final String defaultSku,
                                        final int availability,
                                        final boolean availableNow,
                                        final LocalDateTime releaseDate,
                                        final Map<String, BigDecimal> inventoryQty) {
        this.supplier = supplier;
        this.defaultSku = defaultSku;
        this.releaseDate = releaseDate;

        this.availability = availability;

        perpetual = availability == SkuWarehouse.AVAILABILITY_ALWAYS;
        boolean notForSale = availability == SkuWarehouse.AVAILABILITY_SHOWROOM;

        if (perpetual) {
            inStock = false; // perpetual is used exactly for purpose of not needing to have stock
        }

        if (inventoryQty != null) {
            inStock = false;
            availableToSellQuantity = new HashMap<>(inventoryQty);
            skuCodes = new TreeSet<>(inventoryQty.keySet());
            if (!perpetual) {
                for (final BigDecimal qty : inventoryQty.values()) {
                    if (MoneyUtils.isPositive(qty)) {
                        inStock = true;
                        break;
                    }
                }
            }
        } else {
            // This would probably mean that this product is not available at all
            availableToSellQuantity = Collections.emptyMap();
            skuCodes = NO_SKU;
            inStock = false;
        }

        available = availableNow && !notForSale && (inStock || (availability != SkuWarehouse.AVAILABILITY_STANDARD));
    }

    /** {@inheritDoc} */
    @Override
    public String getSupplier() {
        return supplier;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAvailable() {
        return available;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInStock() {
        return inStock;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPerpetual() {
        return perpetual;
    }

    /** {@inheritDoc} */
    @Override
    public int getAvailability() {
        return availability;
    }

    /** {@inheritDoc} */
    @Override
    public String getDefaultSkuCode() {
        return defaultSku;
    }

    /** {@inheritDoc} */
    @Override
    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    /** {@inheritDoc} */
    @Override
    public String getFirstAvailableSkuCode() {
        if (firstAvailableSku == null) {
            firstAvailableSku = determineFirstAvailableSkuCode(defaultSku, skuCodes);
        }
        return firstAvailableSku;
    }

    private String determineFirstAvailableSkuCode(final String defaultSku, final SortedSet<String> skuCodes) {
        final BigDecimal defValue = getAvailableToSellQuantity(defaultSku);
        if (MoneyUtils.isPositive(defValue)) {
            return defaultSku;
        }
        for (final String skuCode : skuCodes) {
            final BigDecimal value = getAvailableToSellQuantity(skuCode);
            if (MoneyUtils.isPositive(value)) {
                return skuCode;
            }
        }
        return defaultSku;
    }


    /** {@inheritDoc} */
    @Override
    public SortedSet<String> getSkuCodes() {
        return skuCodes;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getAvailableToSellQuantity(final String skuCode) {
        if (perpetual) {
            return PERPETUAL;
        }
        return MoneyUtils.notNull(availableToSellQuantity.get(skuCode));
    }

}
