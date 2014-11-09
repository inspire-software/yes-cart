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

package org.yes.cart.shoppingcart;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 04/11/2014
 * Time: 17:39
 */
public interface TaxProvider {

    interface Tax {

        /**
         * Get tax rate.
         *
         * @return tax rate (0-99)
         */
        BigDecimal getRate();

        /**
         * Get exclusive flag.
         *
         * Inclusive model example:
         * Price:       $1.00
         * Tax:         20%
         * Net price:   $0.83
         * Gross price: $1.00
         *
         * Exclusive model example:
         * Price:       $1.00
         * Tax:         20%
         * Net price:   $1.00
         * Gross price: $1.20
         *
         * @return true exclusive of price, false inclusive of price.
         */
        boolean isExcluded();

        /**
         * Get tax code.
         *
         * @return tax code
         */
        String getCode();

    }

    /**
     * Determine tax for a given item.
     *
     * @param shopCode shop code
     * @param currency currency
     * @param countryCode country code
     * @param stateCode state code
     * @param itemCode product SKU or shipping SLA guid
     *
     * @return tax rate
     */
    Tax determineTax(String shopCode, String currency, String countryCode, String stateCode, String itemCode);

}
