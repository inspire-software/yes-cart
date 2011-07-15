package org.yes.cart.domain.entity;

/**
 * Shop settings.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ShopSettings extends Auditable {

    /**
     */
    long getShopSettingsId();

    void setShopSettingsId(long shopSettingsId);


    /**
     */
    Shop getShop();

    void setShop(Shop shop);

    /**
     */
    String getVal();

    void setVal(String val);

}


