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

package org.yes.cart.domain.query.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.PriceNavigation;

import java.math.BigDecimal;

/**
 * PriceNavigation responsible for compose/decompose url parameter to  java objects. 
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 10-May-2011
 * Time: 09:48:51
 */
public class PriceNavigationImpl implements PriceNavigation {

    /**
     * {@inheritDoc}
     */
    public Pair<String, Pair<BigDecimal, BigDecimal>> decomposePriceRequestParams(final String val) {
        final String[] currencyPriceBorders = StringUtils.split(val, Constants.RANGE_NAVIGATION_DELIMITER);
        final Pair<BigDecimal, BigDecimal> priceBorders =
                new Pair<BigDecimal, BigDecimal>(
                        convertToDecimal(currencyPriceBorders[1]),
                        convertToDecimal(currencyPriceBorders[2]));
        return new Pair<String, Pair<BigDecimal, BigDecimal>>(
                currencyPriceBorders[0],
                priceBorders
        );
    }

    private BigDecimal convertToDecimal(String currencyPriceBorder) {
        try {
            return new BigDecimal(currencyPriceBorder);
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }

    }

    /**
     * {@inheritDoc}
     */
    public String composePriceRequestParams(final String currency, final BigDecimal lowBorder, final BigDecimal highBorder) {
        return composePriceRequestParams(currency, lowBorder, highBorder, Constants.RANGE_NAVIGATION_DELIMITER, Constants.RANGE_NAVIGATION_DELIMITER);
    }


    /**
     * {@inheritDoc}
     */
    public String composePriceRequestParams(final String currency,
                                            final BigDecimal lowBorder,
                                            final BigDecimal highBorder,
                                            final String currencyDelimiter,
                                            final String priceDelimiter) {

        StringBuilder builder = new StringBuilder();
        builder.append(currency);
        builder.append(currencyDelimiter);
        builder.append(lowBorder.toPlainString());
        builder.append(priceDelimiter);
        builder.append(highBorder.toPlainString());
        return builder.toString();
    }

}
