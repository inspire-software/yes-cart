package org.yes.cart.domain.entity;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 * <p/>
 * Need to use this rank instead of Category@rank in future version.
 */
public interface ShopCategory extends Auditable {

    /**
     * Primary key.
     *
     * @return pk value.
     */
    long getShopCategoryId();

    /**
     * Set pk value
     *
     * @param shopCategoryId pk value.
     */
    void setShopCategoryId(long shopCategoryId);

    /**
     * Get order.
     *
     * @return category rank in shop.
     */
    int getRank();

    /**
     * Set category rank.
     *
     * @param rank rank
     */
    void setRank(int rank);

    /**
     * Get shop.
     *
     * @return {@link Shop}
     */
    Shop getShop();

    /**
     * Set {@link Shop}
     *
     * @param shop {@link Shop}
     */
    void setShop(Shop shop);

    /**
     * Get category.
     *
     * @return category.
     */
    Category getCategory();

    /**
     * Set category.
     *
     * @param category category
     */
    void setCategory(Category category);
}
