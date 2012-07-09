package org.yes.cart.domain.entity;

import java.math.BigDecimal;


/**
 * Currency exchange rate in particular store.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ShopExchangeRate extends Auditable {

    /**
     */
    long getShopexchangerateId();

    void setShopexchangerateId(long shopexchangerateId);

    /**
     */
    String getFromCurrency();

    void setFromCurrency(String fromCurrency);

    /**
     */
    String getToCurrency();

    void setToCurrency(String toCurrency);

    /**
     */
    Shop getShop();

    void setShop(Shop shop);

    BigDecimal getRate();

    void setRate(BigDecimal rate);

}


