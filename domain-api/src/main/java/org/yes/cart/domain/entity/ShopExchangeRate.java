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
    public long getShopexchangerateId();

    public void setShopexchangerateId(long shopexchangerateId);

    /**
     */
    public String getFromCurrency();

    public void setFromCurrency(String fromCurrency);

    /**
     */
    public String getToCurrency();

    public void setToCurrency(String toCurrency);

    /**
     */
    public Shop getShop();

    public void setShop(Shop shop);

    BigDecimal getRate();

    void setRate(BigDecimal rate);

}


