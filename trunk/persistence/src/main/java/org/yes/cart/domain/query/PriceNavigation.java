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

package org.yes.cart.domain.query;

import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 10-May-2011
 * Time: 09:48:01
 */
public interface PriceNavigation {
    /**
     * Expected value in following format CUR-LOW-HIGH format
     *
     * @param val value rfom request
     * @return filled price tier node.
     */
    Pair<String, Pair<BigDecimal, BigDecimal>> decomposePriceRequestParams(String val);

    /**
     * Compose price tier into string representation.
     *
     * @param currency   currency
     * @param lowBorder  low price
     * @param highBorder high price
     * @return string representation in CUR-LOW-HIGH format
     */
    String composePriceRequestParams(String currency, BigDecimal lowBorder, BigDecimal highBorder);

    /**
     * Compose price tier into string representation.
     *
     * @param currency           currency
     * @param lowBorder          low price
     * @param highBorder         high price
     * @param currencyDelimitter delimitter between currency and prices
     * @param priceDelimitter    price delimitter
     * @return string representation in CUR-LOW-HIGH format
     */
    String composePriceRequestParams(String currency,
                                     BigDecimal lowBorder,
                                     BigDecimal highBorder,
                                     String currencyDelimitter,
                                     String priceDelimitter);
}
