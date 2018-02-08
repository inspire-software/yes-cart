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

package org.yes.cart.util;

import org.yes.cart.constants.Constants;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.shoppingcart.Total;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utils class to simplify some work with BigDecimals.
 * <p/>
 * User: dogma
 * Date: Jan 15, 2011
 * Time: 11:35:42 PM
 */
public final class MoneyUtils {

    public static final BigDecimal ZERO = new BigDecimal("0.00").setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP);
    public static final BigDecimal ONE = new BigDecimal("1.00").setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP);
    public static final BigDecimal HUNDRED = new BigDecimal("100.00").setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP);

    private MoneyUtils() {
        // prevent instantiation
    }

    /**
     * Get max value from given values.
     *
     * @param first    first value
     * @param second   second value
     *
     * @return max value.
     */
    public static BigDecimal max(final BigDecimal first, final BigDecimal second) {
        if (isFirstBiggerThanSecond(notNull(first), notNull(second))) {
            return notNull(first);
        }
        return notNull(second);
    }

    /**
     * Get value.
     *
     * @param firstAndSecond    first and second
     *
     * @return second if not null, first if second is null.
     */
    public static BigDecimal secondOrFirst(final Pair<BigDecimal, BigDecimal> firstAndSecond) {
        return firstAndSecond.getSecond() == null ? firstAndSecond.getFirst() : firstAndSecond.getSecond();
    }

    /**
     * Get minimal, but greater than 0 value from given values.
     *
     * @param firstAndSecond pair value
     *
     * @return min value.
     */
    public static BigDecimal minPositive(final Pair<BigDecimal, BigDecimal> firstAndSecond) {
        return minPositive(firstAndSecond.getSecond(), firstAndSecond.getFirst());
    }

    /**
     * Get minimal, but greater than 0 value from given values.
     *
     * @param first    first given
     * @param second   second value
     *
     * @return min value.
     */
    public static BigDecimal minPositive(final BigDecimal first, final BigDecimal second) {
        if ( first == null || first.compareTo(BigDecimal.ZERO) == 0) {
            return notNull(second);
        } else if ( second == null || second.compareTo(BigDecimal.ZERO) == 0) {
            return notNull(first);
        } else  if (isFirstBiggerThanSecond(notNull(first), notNull(second))) {
            return notNull(second);
        }
        return notNull(first);
    }

    /**
     * Non null.
     *
     * @param value value to check
     *
     * @return value if it not null, otherwise BigDecimal.ZERO
     */
    public static BigDecimal notNull(final BigDecimal value) {
        return notNull(value, null);
    }

    /**
     * Return #ifNull value if null.
     *
     * @param value  value to check
     * @param ifNull value to return if value to check is null
     *
     * @return value or ifNull if value is null. if ifNull is null returns BigDecimal.ZERO.
     */
    public static BigDecimal notNull(final BigDecimal value, final BigDecimal ifNull) {
        if (value == null) {
            if (ifNull == null) {
                return BigDecimal.ZERO;
            }
            return ifNull;
        }
        return value;
    }

    /**
     * Check if positive.
     *
     * @param value  value
     *
     * @return true if value is greater than zero (null safe)
     */
    public static boolean isPositive(final BigDecimal value) {

        return value != null && value.compareTo(BigDecimal.ZERO) > 0;

    }

    /**
     * Check if first is bigger than second.
     *
     * @param first  value
     * @param second value
     *
     * @return true if first is greater than second (null safe)
     */
    public static boolean isFirstBiggerThanSecond(final BigDecimal first, final BigDecimal second) {

        return first != null && (second == null || first.compareTo(second) > 0);

    }

    /**
     * Check if first bigger or equal than second.
     *
     * @param first  value
     * @param second value
     *
     * @return true if first is greater than or equal to second (null safe)
     */
    public static boolean isFirstBiggerThanOrEqualToSecond(final BigDecimal first, final BigDecimal second) {

        return first != null && (second == null || first.compareTo(second) >= 0);

    }

    /**
     * Check if first equal to second.
     *
     * @param first  value
     * @param second value
     *
     * @return true if first is equal to second (null safe)
     */
    public static boolean isFirstEqualToSecond(final BigDecimal first, final BigDecimal second) {

        return first != null && second != null && first.compareTo(second) == 0;

    }

    /**
     * Check if first equal to second at scale.
     *
     * @param first  value
     * @param second value
     * @param scale  scale
     *
     * @return true if first is equal to second (null safe)
     */
    public static boolean isFirstEqualToSecond(final BigDecimal first, final BigDecimal second, final int scale) {

        return first != null && second != null &&
                first.setScale(scale, RoundingMode.HALF_UP).compareTo(second.setScale(scale, RoundingMode.HALF_UP)) == 0;

    }

    /**
     * Get discount as percentage. E.g. original 100.00, discounted 80.0 - the result will be 20 (%)
     * This is not an exact discount calculation but rather one used for UI and hence it rounds to the
     * nearest percent value. E.g. original 100.00, discounted 80.99 - the result will be 19 (%)
     *
     * @param original original price
     * @param discounted discounted price
     *
     * @return discount in percent
     */
    public static BigDecimal getDiscountDisplayValue(final BigDecimal original, final BigDecimal discounted) {

        if (original == null || discounted == null) {
            return BigDecimal.ZERO;
        }
        return original.subtract(discounted).multiply(HUNDRED)
                .divide(original, RoundingMode.FLOOR).setScale(0, RoundingMode.HALF_EVEN);

    }



    /**
     * Create money object from amount and tax configuration.
     *
     * @param money to calculate tax.
     * @param taxRate tax rate.
     * @param taxIncluded tax is included in price.
     *
     * @return tax.
     */
    public static Money getMoney(final BigDecimal money, final BigDecimal taxRate, final boolean taxIncluded) {
        if (money == null) {
            return new Money(Total.ZERO, Total.ZERO, Total.ZERO, Total.ZERO, taxRate, taxIncluded);
        }

        final BigDecimal tax = getTaxAmount(money, taxRate, taxIncluded);

        if (taxIncluded) {

            return new Money(money, money.subtract(tax), money, tax, taxRate, taxIncluded);
        }

        return new Money(money, money, money.add(tax), tax, taxRate, taxIncluded);
    }




    /**
     * Calculate tax for given money.
     *
     * @param money to calculate tax.
     * @param taxRate tax rate.
     * @param taxIncluded tax is included in price.
     *
     * @return tax.
     */
    public static BigDecimal getTaxAmount(final BigDecimal money, final BigDecimal taxRate, final boolean taxIncluded) {
        if (money == null || taxRate == null) {
            return Total.ZERO;
        }
        if (taxIncluded) {
            // vat = item * vatRate / (vat + 100). Round CEILING to make sure we are not underpaying tax
            return money.multiply(taxRate)
                    .divide(taxRate.add(HUNDRED), Constants.MONEY_SCALE, BigDecimal.ROUND_CEILING);
        }
        // tax = item * taxRate / 100. Round CEILING to make sure we are not underpaying tax
        return money.multiply(taxRate).divide(HUNDRED, Constants.MONEY_SCALE, BigDecimal.ROUND_CEILING);
    }



    /**
     * Determine NET (without tax) money amount.
     *
     * @param money to calculate tax.
     * @param taxRate tax rate.
     * @param taxIncluded tax is included in price.
     *
     * @return tax.
     */
    public static BigDecimal getNetAmount(final BigDecimal money, final BigDecimal taxRate, final boolean taxIncluded) {
        if (money == null) {
            return Total.ZERO;
        }
        if (taxIncluded) {
            return money.subtract(getTaxAmount(money, taxRate, taxIncluded));
        }
        // taxes excluded so this is net
        return money;
    }


    /**
     * Determine GROSS (with tax) money amount.
     *
     * @param money to calculate tax.
     * @param taxRate tax rate.
     * @param taxIncluded tax is included in price.
     *
     * @return tax.
     */
    public static BigDecimal getGrossAmount(final BigDecimal money, final BigDecimal taxRate, final boolean taxIncluded) {
        if (money == null) {
            return Total.ZERO;
        }
        if (taxIncluded) {
            // taxes included so this is gross
            return money;
        }
        return money.add(getTaxAmount(money, taxRate, taxIncluded));
    }

    public static class Money {

        private final BigDecimal money;
        private final BigDecimal net;
        private final BigDecimal gross;
        private final BigDecimal tax;
        private final BigDecimal rate;
        private final boolean included;

        public Money(final BigDecimal money,
                     final BigDecimal net,
                     final BigDecimal gross,
                     final BigDecimal tax,
                     final BigDecimal rate,
                     final boolean included) {
            this.money = money;
            this.net = net;
            this.gross = gross;
            this.tax = tax;
            this.rate = rate;
            this.included = included;
        }

        public BigDecimal getMoney() {
            return money;
        }

        public BigDecimal getNet() {
            return net;
        }

        public BigDecimal getGross() {
            return gross;
        }

        public BigDecimal getTax() {
            return tax;
        }

        public BigDecimal getRate() {
            return rate;
        }

        public boolean isIncluded() {
            return included;
        }

    }



}
