package org.yes.cart.domain.entity;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface AttrValueShop extends AttrValue {

    /**
     * Get the shop.
     *
     * @return {@link Shop}
     */
    Shop getShop();

    /**
     * Set {@link Shop}
     *
     * @param shop {@link Shop} to set.
     */
    void setShop(Shop shop);

}
