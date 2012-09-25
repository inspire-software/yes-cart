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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

import java.math.BigDecimal;

/**
 *
 * Dto for currencies exchange rate in shop.
 *
 * User: iazarny@yahoo.com
 * Date: 9/22/12
 * Time: 1:06 PM
 */
public interface ShopExchangeRateDTO extends Identifiable {

    /**
     * Get primary key
     */
    long getShopexchangerateId();

    /**
     * Set primary key
     * @param shopexchangerateId
     */
    void setShopexchangerateId(long shopexchangerateId);

    /**
     * From default currency
     */
    String getFromCurrency();

    /**
     * Set from default currency
     * @param fromCurrency   default currency
     */
    void setFromCurrency(String fromCurrency);

    /**
     * To currency.
     */
    String getToCurrency();

    /**
     * Set to currency.
     * @param toCurrency
     */
    void setToCurrency(String toCurrency);

    /**
     * @return {@link org.yes.cart.domain.entity.Shop}
     */
    long getShopId();

    /**
     * Get shop id.
     * @param shopId
     */
    void setShopId(long shopId);

    /**
     * Get exchange rate between two currencies.
     * @return
     */
    BigDecimal getRate();

    /**
     * Set exchange rate.
     * @param rate  exchange rate to use.
     */
    void setRate(BigDecimal rate);

}
