package org.yes.cart.domain.entity;

/**
 * Shop settings.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ShopSettings extends Auditable {

    /**
     */
    public long getShopSettingsId();

    public void setShopSettingsId(long shopSettingsId);

    /**
     */
    public Settings getSetting();

    public void setSetting(Settings setting);

    /**
     */
    public Shop getShop();

    public void setShop(Shop shop);

    /**
     */
    public String getVal();

    public void setVal(String val);

}


