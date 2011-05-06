package org.yes.cart.domain.entity;

import java.util.Date;
import java.util.Set;

/**
 * Shop discount rule.
 *
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ShopDiscount extends Auditable {

    /**
     */
    public long getShopDiscountId();

    public void setShopDiscountId(long shopDiscountId);

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
    public Date getAvailablefrom();

    public void setAvailablefrom(Date availablefrom);

    /**
     */
    public Date getAvailabletill();

    public void setAvailabletill(Date availabletill);

    /**
     */
    public Shop getShop();

    public void setShop(Shop shop);

    /**
     */
    public Set<ShopDiscountRule> getShopDiscountRules();

    public void setShopDiscountRules(Set<ShopDiscountRule> shopDiscountRules);

}


