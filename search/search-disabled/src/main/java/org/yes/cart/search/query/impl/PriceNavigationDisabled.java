/*
 * Copyright 2009 Inspire-Software.com
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

import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.PriceNavigation;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 25/03/2018
 * Time: 12:13
 */
public class PriceNavigationDisabled implements PriceNavigation {

    @Override
    public Pair<String, Pair<BigDecimal, BigDecimal>> decomposePriceRequestParams(final String val) {
        return new Pair<>("disabled", new Pair<>(BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Override
    public String composePriceRequestParams(final String currency, final BigDecimal lowBorder, final BigDecimal highBorder) {
        return "disabled";
    }

    @Override
    public String composePriceRequestParams(final String currency, final BigDecimal lowBorder, final BigDecimal highBorder, final String currencyDelimiter, final String priceDelimiter) {
        return "disabled";
    }
}
