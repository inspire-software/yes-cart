package org.yes.cart.domain.entity;

import java.util.Date;
import java.util.Set;

/**
 * Shop discount rule.
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ShopDiscount extends Auditable {

    /**
     */
    long getShopDiscountId();

    void setShopDiscountId(long shopDiscountId);

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
    Date getAvailablefrom();

    void setAvailablefrom(Date availablefrom);

    /**
     */
    Date getAvailabletill();

    void setAvailabletill(Date availabletill);

    /**
     */
    Shop getShop();

    void setShop(Shop shop);

    /**
     */
    Set<ShopDiscountRule> getShopDiscountRules();

    void setShopDiscountRules(Set<ShopDiscountRule> shopDiscountRules);

}


