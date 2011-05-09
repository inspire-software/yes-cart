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
