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

package org.yes.cart.web.support.service;

import org.yes.cart.domain.misc.Pair;

import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 4:47:19 PM
 */
public interface CurrencySymbolService {

    /**
     * Get the currency symbol(s) in html unicode format for given currency code.
     *
     * @param currencyCode currency code
     * @return currency symbol(s)
     */
    String getCurrencySymbol(String currencyCode);

    /**
     * Get currency to display.
     * @param curensiesListString  given comma separated string of currency codes
     * @return list of currency code - currency label
     */
    List<Pair<String, String>> getCurrencyToDisplayAsList(String curensiesListString);

    /**
     * Get currency to display.
     * @param curensiesListString  given comma separated string of currency codes
     * @return map of currency code - currency label
     */
    Map<String, String> getCurrencyToDisplayAsMap(String curensiesListString);

}
