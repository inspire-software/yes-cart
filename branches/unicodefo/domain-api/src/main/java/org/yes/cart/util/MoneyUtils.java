package org.yes.cart.util;

import java.math.BigDecimal;

/**
 * Utils class to simplify some work with BigDecimals.
 * <p/>
 * User: dogma
 * Date: Jan 15, 2011
 * Time: 11:35:42 PM
 */
public final class MoneyUtils {

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
     * @return value or ifNull if value is null. if ifNull is null retirns BigDecimal.ZERO.
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


}
