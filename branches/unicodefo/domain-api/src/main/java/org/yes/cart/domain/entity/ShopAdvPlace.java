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
    long getShopadvplaceId();

    void setShopadvplaceId(long shopadvplaceId);

    /**
     */
    String getName();

    void setName(String name);

    /**
     */
    String getDescription();

    void setDescription(String description);

    /**
     */
    Shop getShop();

    void setShop(Shop shop);

    /**
     */
    Set<ShopAdvPlaceRule> getShopAdvPlaceRules();

    void setShopAdvPlaceRules(Set<ShopAdvPlaceRule> shopAdvPlaceRules);

}


