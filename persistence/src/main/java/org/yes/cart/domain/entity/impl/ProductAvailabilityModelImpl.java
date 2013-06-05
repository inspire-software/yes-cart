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

package org.yes.cart.domain.entity.impl;

import org.yes.cart.domain.entity.Product;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.domain.entity.ProductAvailabilityModel;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 13-04-06
 * Time: 1:13 PM
 */
public class ProductAvailabilityModelImpl implements ProductAvailabilityModel {

    public static final BigDecimal PERPETUAL = new BigDecimal(Integer.MAX_VALUE);

    private final boolean available;
    private final boolean inStock;
    private final boolean perpetual;
    private final BigDecimal availableToSellQuantity;

    public ProductAvailabilityModelImpl(final int availability, final BigDecimal inventoryQty) {
        perpetual = availability == Product.AVAILABILITY_ALWAYS;
        inStock = !perpetual && MoneyUtils.isFirstBiggerThanSecond(inventoryQty, BigDecimal.ZERO);
        available = inStock || (availability != Product.AVAILABILITY_STANDARD);
        availableToSellQuantity = perpetual ? PERPETUAL : MoneyUtils.notNull(inventoryQty);
    }

    /** {@inheritDoc} */
    public boolean isAvailable() {
        return available;
    }

    /** {@inheritDoc} */
    public boolean isInStock() {
        return inStock;
    }

    /** {@inheritDoc} */
    public boolean isPerpetual() {
        return perpetual;
    }

    /** {@inheritDoc} */
    public BigDecimal getAvailableToSellQuantity() {
        return availableToSellQuantity;
    }
}
