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

package org.yes.cart.search.query.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 * 
 */
public class SearchUtil {

    /**
     * Tokenize search phraze and clean from empty strings.
     *
     * @param phraze optional phraze
     * @param charThreshold character threshold. e.g. if set to 3 will skip words less than 3 characters
     *
     * @return list of tokens, that found in phraze.
     */
    public static List<String> splitForSearch(final String phraze, final int charThreshold) {
        if (phraze != null) {
            String [] token = StringUtils.splitPreserveAllTokens(phraze, "| ;");
            List<String> words = new ArrayList<String>(token.length);
            for (final String aToken : token) {
                if (StringUtils.isNotBlank(aToken)) {
                    String clean = aToken.trim();
                    if (clean.length() >= charThreshold) {
                        // remove tailing punctuation
                        int pos = clean.length() - 1;
                        for (; pos >= 0; pos--) {
                            char last = clean.charAt(pos);
                            if (last != ',' && last != '.' && last != '!' && last != '?' && last != ')' && last != '}' && last != ']') {
                                break;
                            }
                        }
                        if (pos < clean.length() - 1) {
                            clean = clean.substring(0, pos + 1);
                        }
                        // remove leading punctuation
                        pos = 0;
                        for (; pos < clean.length(); pos++) {
                            char first = clean.charAt(pos);
                            if (first != '(' && first != '{' && first != '[') {
                                break;
                            }
                        }
                        if (pos > 0) {
                            clean = clean.substring(pos);
                        }

                        if (clean.length() >= charThreshold) {
                            // look for words separates by dash
                            pos = clean.indexOf('-');
                            if (pos >= charThreshold &&
                                    /* more than just dashed min words */ clean.length() > charThreshold * 2 + 1 &&
                                    /* full words */ pos != 0 && (pos < clean.length() - charThreshold) &&
                                    /* doubke dash */ clean.charAt(pos + 1) != '-') {
                                words.add(clean.substring(0, pos));
                                words.add(clean.substring(pos + 1, clean.length()));
                            } else {
                                words.add(clean);
                            }
                        }
                    }
                }
            }
            return words;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Create index value for given shop currency and price.
     *
     * @param shopId shop id
     * @param currency currency code
     *
     * @return field name that has format facet_price_shopid_currency.
     */
    public static String priceFacetName(final long shopId, final String currency) {
        return "facet_price_" + shopId + "_" + currency;
    }

    /**
     * Convert price to long value.
     *
     * @param price price to convert to long
     *
     * @return price padded using {@link Constants#MONEY_SCALE}
     */
    public static long priceToLong(final BigDecimal price) {
        return price.movePointRight(Constants.MONEY_SCALE).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
    }

    /**
     * Decimal to string. Pads the numbers since FT searches numbers as strings.
     *
     * @param value      decimal to pad
     * @param scale      scale to preserve
     *
     * @return padded big decimal
     */
    public static Long valToLong(final String value, final int scale) {
        try {
            final BigDecimal decimal = new BigDecimal(value);
            return decimal.movePointRight(scale).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
        } catch (Exception exp) {
            // NaN
        }
        return null;
    }

    /**
     * Decimal to string. Pads the numbers since FT searches numbers as strings.
     *
     * @param value      decimal to pad
     * @param scale      scale to preserve
     *
     * @return padded big decimal
     */
    public static String longToVal(final String value, final int scale) {
        try {
            final BigDecimal decimal = new BigDecimal(value);
            return decimal.movePointLeft(scale).stripTrailingZeros().toPlainString();
        } catch (Exception exp) {
            // NaN
        }
        return "0";
    }


}
