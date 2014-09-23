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

package org.yes.cart.domain.entity;

import java.math.BigDecimal;


/**
 * Currency exchange rate in particular store.
 * We are not holding the history of echange rates.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ShopExchangeRate extends Auditable {

    /**
     * Get primary key
     */
    long getShopexchangerateId();

    /**
     * Set primary key
     * @param shopexchangerateId  set pk
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
     * @param toCurrency  target currency
     */
    void setToCurrency(String toCurrency);

    /**
     * Get shop.
     */
    Shop getShop();

    /**
     * Get shop.
     * @param shop shop
     */
    void setShop(Shop shop);

    /**
     * Get exchange rate between two currencies.
     * @return    rate
     */
    BigDecimal getRate();

    /**
     * Set exchange rate.
     * @param rate  exchange rate to use.
     */
    void setRate(BigDecimal rate);

}


