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

package org.yes.cart.service.domain.impl;

import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopExchangeRate;
import org.yes.cart.service.domain.ExchangeRateService;

import java.math.BigDecimal;

/**
 * Local exchange rate service implementation.
 * <p/>
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ExchangeRateServiceImpl implements ExchangeRateService {

    /**
     * {@inheritDoc}
     */
    public BigDecimal getExchangeRate(final Shop shop,
                                      final String defaultCurrencyCode,
                                      final String targetCurrencyCode) {
        for (ShopExchangeRate rate : shop.getExchangerates()) {
            if (rate.getFromCurrency().equals(defaultCurrencyCode) && rate.getToCurrency().equals(targetCurrencyCode)) {
                return rate.getRate();
            }
        }
        return null;
    }
}
