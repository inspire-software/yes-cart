package org.yes.cart.domain.dto;

import java.io.Serializable;

/**
 *
 * Shop Category DTO.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ShopCategoryDTO extends Serializable {

    /**
     * Primary key.
     * @return pk value.
     */
    long getShopCategoryId();

    /**
     * Set pk value
     * @param shopCategoryId pk value.
     */
    void setShopCategoryId(long shopCategoryId);

    /**
     * Get order.
     * @return category rank in shop.
     */
    int getRank();

    /**
     * Set category rank.
     * @param rank rank
     */
    void setRank(int rank);

    /**
     * Get shop.
     * @return shop id
     */
    long getShopId();

    /**
     * Set shop id
     * @param shopId shop id
     */
    void setShopId(long shopId);

    /**
     * Get category.
     * @return category.
     */
    long getCategoryId();

    /**
     * Set category.
     * @param categoryId category id
     */
    void setCategoryId(long categoryId);


}
