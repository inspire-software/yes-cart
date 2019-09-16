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

import org.yes.cart.domain.entity.QuantityModel;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * User: denispavlov
 * Date: 25/10/2014
 * Time: 11:55
 */
public class QuantityModelImpl implements QuantityModel {

    private static final BigDecimal MAX = new BigDecimal(Integer.MAX_VALUE);

    private final String supplier;
    private final boolean canOrderLess;
    private final boolean canOrderMore;
    private final boolean hasMin;
    private final boolean hasMax;
    private final boolean hasStep;
    private final BigDecimal min;
    private final BigDecimal minOrder;
    private final BigDecimal max;
    private final BigDecimal maxOrder;
    private final BigDecimal step;
    private final BigDecimal cartQty;
    private final String defaultSku;

    public QuantityModelImpl(final String supplier,
                             final String defaultSku,
                             final BigDecimal min,
                             final BigDecimal max,
                             final BigDecimal step,
                             final BigDecimal cartQty) {

        this.supplier = supplier;
        this.defaultSku = defaultSku;

        this.hasStep = MoneyUtils.isPositive(step);
        this.step = (this.hasStep ? step : BigDecimal.ONE).stripTrailingZeros(); // Stepping ultimately defines the scale for quantity

        this.hasMin = MoneyUtils.isPositive(min);
        this.min = (this.hasMin ? min : BigDecimal.ONE).setScale(this.step.scale(), RoundingMode.HALF_UP);

        this.hasMax = max != null && MoneyUtils.isFirstBiggerThanSecond(MAX, max);
        this.max = (this.hasMax ? max : MAX).setScale(this.step.scale(), RoundingMode.HALF_UP);

        this.cartQty = (cartQty != null ? cartQty : BigDecimal.ZERO).setScale(this.step.scale(), RoundingMode.HALF_UP);

        this.canOrderLess = MoneyUtils.isFirstBiggerThanSecond(this.cartQty, this.min);
        this.canOrderMore = MoneyUtils.isFirstBiggerThanSecond(this.max, this.cartQty);

        if (this.canOrderLess) {
            if (this.canOrderMore) {
                this.minOrder = this.step;
            } else {
                this.minOrder = BigDecimal.ZERO;
            }
        } else {
            if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(BigDecimal.ZERO, this.cartQty)) {
                this.minOrder = this.min; // when no items in can start with min
            } else if (MoneyUtils.isFirstEqualToSecond(this.min, this.cartQty)) {
                this.minOrder = this.step; // if at min add one step
            } else { // if not yet at min add amount to get to min
                this.minOrder = this.min.subtract(this.cartQty).setScale(this.step.scale(), RoundingMode.CEILING);
            }
        }

        if (this.canOrderMore) {
            this.maxOrder = this.max.subtract(this.cartQty).setScale(this.step.scale(), RoundingMode.FLOOR);
        } else {
            this.maxOrder = BigDecimal.ZERO;
        }

    }

    /** {@inheritDoc} */
    @Override
    public String getSupplier() {
        return supplier;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canOrderLess() {
        return canOrderLess;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canOrderMore() {
        return canOrderMore;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasMin() {
        return hasMin;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasMax() {
        return hasMax;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasStep() {
        return hasStep;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasMinMaxStep() {
        return hasMin || hasStep || hasMax;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getMin() {
        return min;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getMinOrder() {
        return minOrder;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getMax() {
        return max;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getMaxOrder() {
        return maxOrder;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getStep() {
        return step;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getCartQty() {
        return cartQty;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getValidAddQty(final BigDecimal addQty) {
        final BigDecimal toAdd;
        if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(this.minOrder, addQty)) {
            toAdd = this.minOrder; // do not allow qty less than min
        } else if (addQty != null) {
            toAdd = addQty;
        } else {
            toAdd = BigDecimal.ONE;
        }
        final BigDecimal rem = cartQty.add(toAdd).setScale(step.scale(), RoundingMode.HALF_UP); // trial add

        if (MoneyUtils.isFirstBiggerThanSecond(rem, max)) { // check we are not over max
            return max.subtract(cartQty).setScale(step.scale(), RoundingMode.FLOOR);
        }

        final BigDecimal multiplier = rem.subtract(min).divide(step, 0, RoundingMode.FLOOR);
        return min.add(step.multiply(multiplier)).subtract(cartQty).setScale(step.scale(), RoundingMode.HALF_UP);
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getValidRemoveQty(final BigDecimal remQty) {
        final BigDecimal toRemove;
        if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(this.step, remQty)) {
            toRemove = this.step;
        } else if (remQty != null) {
            toRemove = remQty;
        } else {
            toRemove = BigDecimal.ONE;
        }
        final BigDecimal rem = cartQty.subtract(toRemove);
        if (MoneyUtils.isFirstBiggerThanSecond(min, rem)) {
            return cartQty;
        }

        final BigDecimal margin = rem.subtract(min);
        final BigDecimal multiplier = margin.divide(step, 0, RoundingMode.CEILING);
        return cartQty.subtract(min.add(step.multiply(multiplier))).setScale(step.scale(), RoundingMode.HALF_UP);
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getValidSetQty(final BigDecimal qty) {
        if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(min, qty)) {
            return min;
        }
        if (MoneyUtils.isFirstBiggerThanSecond(qty, max)) {
            return max;
        }

        final BigDecimal margin = (qty != null ? qty : BigDecimal.ONE).subtract(min);
        final BigDecimal multiplier = margin.divide(step, 0, RoundingMode.CEILING);
        return min.add(step.multiply(multiplier)).setScale(step.scale(), RoundingMode.HALF_UP);
    }

    /** {@inheritDoc} */
    @Override
    public String getDefaultSkuCode() {
        return defaultSku;
    }
}
