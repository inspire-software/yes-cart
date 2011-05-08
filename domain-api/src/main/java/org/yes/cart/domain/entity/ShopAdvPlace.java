package org.yes.cart.domain.entity;

import java.util.Set;

/**
 * Named advertising place in the shop .
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ShopAdvPlace extends Auditable {

    /**
     */
    public long getShopadvplaceId();

    public void setShopadvplaceId(long shopadvplaceId);

    /**
     */
    public String getName();

    public void setName(String name);

    /**
     */
    public String getDescription();

    public void setDescription(String description);

    /**
     */
    public Shop getShop();

    public void setShop(Shop shop);

    /**
     */
    public Set<ShopAdvPlaceRule> getShopAdvPlaceRules();

    public void setShopAdvPlaceRules(Set<ShopAdvPlaceRule> shopAdvPlaceRules);

}


