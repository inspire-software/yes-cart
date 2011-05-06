package org.yes.cart.domain.entity;

/**
 * TODO nice to have scheduler, that perform old wish list items clean up
 *
 * Shopper wish list item. Also reponsible to notification sheduling.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CustomerWishList extends Auditable {

    public static final String SIMPLE_WISH_ITEM = "W";
    
    public static final String REMIND_WHEN_WILL_BE_AVAILABLE = "A";

    public static final String REMIND_WHEN_PRICE_CHANGED = "P";

    public static final String REMIND_WHEN_WILL_BE_IN_PROMO = "R";

    /**
     * Primary key value.
     * @return key value.
     */
    public long getCustomerwishlistId();

    /**
     * Set key value
     * @param customerwishlistId value to set.
     */
    public void setCustomerwishlistId(long customerwishlistId);

    /**
     * Product sku
     * @return {@link ProductSku}
     */
    public ProductSku getSkus();

    /**
     * Set {@link ProductSku}
     * @param skus Product Sku
     */
    public void setSkus(ProductSku skus);

    /**
     * Get customer
     * @return {@link Customer}
     */
    Customer getCustomer();

    /**
     * Set customer
     * @param customer customer to set
     */
    void setCustomer(Customer customer);

    /**
     * Get type of wsih list item.
     * @return type of wsih list item
     */
    String getWlType();

    /**
     * Set type of wsih list item
     * @param wlType type of wsih list item to set.
     */
    void setWlType(final String wlType);


}


