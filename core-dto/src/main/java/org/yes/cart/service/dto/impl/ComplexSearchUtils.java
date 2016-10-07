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

package org.yes.cart.service.dto.impl;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.util.StringUtils;
import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * User: denispavlov
 * Date: 01/09/2016
 * Time: 09:24
 */
class ComplexSearchUtils {

    private ComplexSearchUtils() {
        // no instance
    }

    /**
     * Check if filter is a special search with prefix
     *
     * Format: [prefix].+
     *
     * E.g. #tag, ?phrase where # and ? are prefix characters
     *
     * @param filter non empty filter
     * @param binarySortedPrefixes prefixes
     *
     * @return prefix and number
     */
    public static Pair<String, String> checkSpecialSearch(String filter, char[] binarySortedPrefixes) {
        if (filter.length() > 1 && Arrays.binarySearch(binarySortedPrefixes, filter.charAt(0)) >= 0) {
            return new Pair<>(filter.substring(0, 1), filter.substring(1));
        }
        return null;
    }

    /**
     * Check if filter is a number search with prefix
     *
     * Format: [prefix]\d+
     *
     * E.g. -1, +1.01, #333 where -,+ and # are prefix characters
     *
     * @param filter non empty filter
     * @param binarySortedPrefixes prefixes
     * @param precision precision
     *
     * @return prefix and number
     */
    public static Pair<String, BigDecimal> checkNumericSearch(String filter, char[] binarySortedPrefixes, int precision) {
        if (filter.length() > 1 && Arrays.binarySearch(binarySortedPrefixes, filter.charAt(0)) >= 0) {
            try {
                final BigDecimal qty = new BigDecimal(filter.substring(1)).setScale(precision, BigDecimal.ROUND_CEILING);
                if (qty.signum() == -1) {
                    return null;
                }
                return new Pair<>(filter.substring(0, 1), qty);
            } catch (Exception exp) {
                // do nothing
                return null;
            }
        }
        return null;
    }

    /**
     * Check if filter is a date range search:
     *
     * Format is: [YYYY[-MM[-DD]]]<[YYYY[-MM[-DD]]]
     *
     * Each date part is optional, so following is valid:
     * Before: <YYYY-MM-DD, <YYYY-MM, <YYYY
     * AFter: YYYY-MM-DD<, YYYY-MM<, YYYY<
     *
     * @param filter non empty filter
     *
     * @return from-to pair
     */
    public static Pair<Date, Date> checkDateRangeSearch(String filter) {
        if (filter.length() > 4 && filter.contains("<")) {

            final String[] fromAndTo = org.apache.commons.lang.StringUtils.splitPreserveAllTokens(filter, '<');
            final Date from = fromAndTo.length > 0 ? stringToDate(fromAndTo[0]) : null;
            final Date to = fromAndTo.length > 1 ? stringToDate(fromAndTo[1]) : null;

            if (from != null || to != null) {
                return new Pair<>(from, to);
            }

        }
        return null;
    }


    private static Date stringToDate(String string) {
        if (StringUtils.hasLength(string)) {
            try {
                final String[] dateParts = org.apache.commons.lang.StringUtils.split(string, '-');
                final Calendar cal = Calendar.getInstance();
                if (dateParts.length > 0) {
                    int year = NumberUtils.toInt(dateParts[0]);
                    if (year < 1900 || year > 2199) {
                        return null;
                    }
                    cal.set(Calendar.YEAR, year);
                }
                if (dateParts.length > 1) {
                    int mth = NumberUtils.toInt(dateParts[1], -1) - 1;
                    if (mth < 0 || mth > 11) {
                        return null;
                    }
                    cal.set(Calendar.MONTH, mth);
                } else {
                    cal.set(Calendar.MONTH, 0);
                }
                if (dateParts.length > 2) {
                    int dt = NumberUtils.toInt(dateParts[2], -1);
                    if (dt < 0 || dt > 31) {
                        return null;
                    }
                    cal.set(Calendar.DATE, dt);
                } else {
                    cal.set(Calendar.DATE, 1);
                }
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal.getTime();
            } catch (Exception exp) {
                // do nothing
                return null;
            }
        }
        return null;
    }


}
