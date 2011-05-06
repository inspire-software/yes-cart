package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Shop;

import java.math.BigDecimal;

/**
 *
 * Currency exchange rate service used for price determitation in case when
 * prices not defined for requested currency but requested currency supported.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ExchangeRateService {

    /**
     * Get the exchange rate for given shop.
     * @param shop shop
     * @param defaultCurrencyCode default currency
     * @param targetCurrencyCode target currency
     * @return exchange rate if found, otherwise is null
     */
    BigDecimal getExchangeRate(Shop shop,
                        String defaultCurrencyCode,
                        String targetCurrencyCode);

}
