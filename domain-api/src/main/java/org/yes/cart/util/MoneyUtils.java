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

package org.yes.cart.util;

import org.yes.cart.constants.Constants;

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

    public static final BigDecimal ZERO = new BigDecimal("0.00").setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
    public static final BigDecimal ONE = new BigDecimal("1.00").setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
    public static final BigDecimal HUNDRED = new BigDecimal("100.00").setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);

    private MoneyUtils() {
        // prevent instantiation
    }

    /**
     * Get max value from given values.
     * @param first    first given
     * @param second   second value
     * @return max value.
     */
    public static BigDecimal max(final BigDecimal first, final BigDecimal second) {
        if (isFirstBiggerThanSecond(
                notNull(first),
                notNull(second))) {
            return notNull(first);
        }
        return notNull(second);
    }

    /**
     * Get minimal, but greater than 0 value from given values.
     * @param first    first given
     * @param second   second value
     * @return max value.
     */
    public static BigDecimal minPositive(final BigDecimal first, final BigDecimal second) {
        if ( first == null || notNull(first).equals(BigDecimal.ZERO) ) {
            return notNull(second);
        } else if ( second == null || notNull(second).equals(BigDecimal.ZERO) ) {
            return notNull(first);
        } else  if (isFirstBiggerThanSecond(
                notNull(first),
                notNull(second))) {
            return notNull(second);
        }
        return notNull(first);
    }

    /**
     *
     * @param value value to check
     * @return value if it not null, otherwise BigDecimal.ZERO
     */
    public static BigDecimal notNull(final BigDecimal value) {
        return notNull(value, null);
    }

    /**
     * @param value  value to check
     * @param ifNull value to return if value to check is null
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
     * @param first  value
     * @param second value
     * @return true if first is greater than second (null safe)
     */
    public static boolean isFirstBiggerThanSecond(final BigDecimal first, final BigDecimal second) {

        if (first == null && second == null) {
            return false;
        } else if (second == null) {
            return true;
        } else if (first == null) {
            return false;
        }
        return first.compareTo(second) > 0;

    }

    /**
     * @param first  value
     * @param second value
     * @return true if first is greater than or equal to second (null safe)
     */
    public static boolean isFirstBiggerThanOrEqualToSecond(final BigDecimal first, final BigDecimal second) {

        if (first == null && second == null) {
            return false;
        } else if (second == null) {
            return true;
        } else if (first == null) {
            return false;
        }
        return first.compareTo(second) >= 0;

    }

    /**
     * @param first  value
     * @param second value
     * @return true if first is equal to second (null safe)
     */
    public static boolean isFirstEqualToSecond(final BigDecimal first, final BigDecimal second) {

        if (first == null && second == null) {
            return false;
        } else if (second == null) {
            return false;
        } else if (first == null) {
            return false;
        }
        return first.compareTo(second) == 0;

    }

    /**
     * @param first  value
     * @param second value
     * @param scale  scale
     * @return true if first is equal to second (null safe)
     */
    public static boolean isFirstEqualToSecond(final BigDecimal first, final BigDecimal second, final int scale) {

        if (first == null && second == null) {
            return false;
        } else if (second == null) {
            return false;
        } else if (first == null) {
            return false;
        }
        return first.setScale(scale).compareTo(second.setScale(scale)) == 0;

    }

    /**
     * Get discount as percentage. E.g. original 100.00, discounted 80.0 - the result will be 20 (%)
     * This is not an exact discount calculation but rather one used for UI and hence it rounds to the
     * nearest percent value. E.g. original 100.00, discounted 80.99 - the result will be 19 (%)
     *
     * @param original original price
     * @param discounted discounted price
     * @return discount in percent
     */
    public static BigDecimal getDiscountDisplayValue(final BigDecimal original, final BigDecimal discounted) {

        if (original == null || discounted == null) {
            return BigDecimal.ZERO;
        }
        return original.subtract(discounted).multiply(HUNDRED)
                .divide(original, RoundingMode.FLOOR).setScale(0, RoundingMode.HALF_EVEN);

    }


}
