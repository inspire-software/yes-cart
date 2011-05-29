package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Unique;

import java.io.Serializable;


/**
 * Product - category relation interface.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ProductCategoryDTO extends Unique {

    /**
     * Get pk.
     *
     * @return pk
     */
    public long getProductCategoryId();

    /**
     * set pk.
     *
     * @param productCategoryId pk
     */
    public void setProductCategoryId(long productCategoryId);

    /**
     * Get product.
     *
     * @return product pk
     */
    public long getProductId();

    /**
     * Set product's pk
     *
     * @param productId product pk
     */
    public void setProductId(long productId);

    /**
     * Get category pk
     *
     * @return category pk
     */
    public long getCategoryId();

    /**
     * Set category pk.
     *
     * @param categoryId category pk
     */
    public void setCategoryId(long categoryId);

    /**
     * Get the order of product in category.
     *
     * @return order of product in category.
     */
    public int getRank();

    /**
     * Set order of product in category.
     *
     * @param rank order of product in category.
     */
    public void setRank(int rank);

    /**
     * Get category name.
     *
     * @return category name
     */
    public String getCategoryName();

    /**
     * Set category name.
     *
     * @param categoryName category name.
     */
    public void setCategoryName(final String categoryName);


}
