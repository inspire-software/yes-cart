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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Shop;

import java.math.BigDecimal;

/**
 * Currency exchange rate service used for price determitation in case when
 * prices not defined for requested currency but requested currency supported.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ExchangeRateService {

    /**
     * Get the exchange rate for given shop.
     *
     * @param shop                shop
     * @param defaultCurrencyCode default currency
     * @param targetCurrencyCode  target currency
     * @return exchange rate if found, otherwise is null
     */
    BigDecimal getExchangeRate(Shop shop,
                               String defaultCurrencyCode,
                               String targetCurrencyCode);

}
