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

import org.yes.cart.domain.entity.ProductQuantityModel;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * User: denispavlov
 * Date: 25/10/2014
 * Time: 11:55
 */
public class ProductQuantityModelImpl implements ProductQuantityModel {

    private static final BigDecimal MAX = new BigDecimal(Integer.MAX_VALUE);

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

    public ProductQuantityModelImpl(final BigDecimal min,
                                    final BigDecimal max,
                                    final BigDecimal step,
                                    final BigDecimal cartQty) {

        this.hasStep = MoneyUtils.isFirstBiggerThanSecond(step, BigDecimal.ZERO);
        this.step = (this.hasStep ? step : BigDecimal.ONE).stripTrailingZeros(); // Stepping ultimately defines the scale for quantity

        this.hasMin = MoneyUtils.isFirstBiggerThanSecond(min, BigDecimal.ZERO);
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
    public boolean canOrderLess() {
        return canOrderLess;
    }

    /** {@inheritDoc} */
    public boolean canOrderMore() {
        return canOrderMore;
    }

    /** {@inheritDoc} */
    public boolean hasMin() {
        return hasMin;
    }

    /** {@inheritDoc} */
    public boolean hasMax() {
        return hasMax;
    }

    /** {@inheritDoc} */
    public boolean hasStep() {
        return hasStep;
    }

    /** {@inheritDoc} */
    public boolean hasMinMaxStep() {
        return hasMin || hasStep || hasMax;
    }

    /** {@inheritDoc} */
    public BigDecimal getMin() {
        return min;
    }

    /** {@inheritDoc} */
    public BigDecimal getMinOrder() {
        return minOrder;
    }

    /** {@inheritDoc} */
    public BigDecimal getMax() {
        return max;
    }

    /** {@inheritDoc} */
    public BigDecimal getMaxOrder() {
        return maxOrder;
    }

    /** {@inheritDoc} */
    public BigDecimal getStep() {
        return step;
    }

    /** {@inheritDoc} */
    public BigDecimal getCartQty() {
        return cartQty;
    }

    /** {@inheritDoc} */
    public BigDecimal getValidAddQty(final BigDecimal addQty) {
        final BigDecimal toAdd;
        if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(this.minOrder, addQty)) {
            toAdd = this.minOrder; // do not allow qty less than min
        } else {
            toAdd = addQty;
        }
        final BigDecimal rem = cartQty.add(toAdd).setScale(step.scale(), RoundingMode.HALF_UP); // trial add

        if (MoneyUtils.isFirstBiggerThanSecond(rem, max)) { // check we are not over max
            return max.subtract(cartQty).setScale(step.scale());
        }

        final BigDecimal multiplier = rem.subtract(min).divide(step, 0, RoundingMode.FLOOR);
        return min.add(step.multiply(multiplier)).subtract(cartQty).setScale(step.scale(), RoundingMode.HALF_UP);
    }

    /** {@inheritDoc} */
    public BigDecimal getValidRemoveQty(final BigDecimal remQty) {
        final BigDecimal toRemove;
        if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(this.step, remQty)) {
            toRemove = this.step;
        } else {
            toRemove = remQty;
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
    public BigDecimal getValidSetQty(final BigDecimal qty) {
        if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(min, qty)) {
            return min;
        }
        if (MoneyUtils.isFirstBiggerThanSecond(qty, max)) {
            return max;
        }

        final BigDecimal margin = qty.subtract(min);
        final BigDecimal multiplier = margin.divide(step, 0, RoundingMode.CEILING);
        return min.add(step.multiply(multiplier)).setScale(step.scale(), RoundingMode.HALF_UP);
    }
}
