package org.yes.cart.domain.entity;


/**
 * Product Category relation.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */

public interface ProductCategory extends Auditable {

    /**
     * Get pk.
     *
     * @return pk
     */
    long getProductCategoryId();

    /**
     * set pk.
     *
     * @param productCategoryId pk
     */
    void setProductCategoryId(long productCategoryId);

    /**
     * Get product.
     *
     * @return {@link Product}
     */
    Product getProduct();

    /**
     * Set {@link Product}
     *
     * @param product {@link Product}
     */
    void setProduct(Product product);

    /**
     * Get {@link Category}
     *
     * @return {@link Category}
     */
    Category getCategory();

    /**
     * Set {@link Category}
     *
     * @param category {@link Category}
     */
    void setCategory(Category category);

    /**
     * Get the order of product in category.
     *
     * @return order of product in category.
     */
    int getRank();

    /**
     * Set order of product in category.
     *
     * @param rank order of product in category.
     */
    void setRank(int rank);

}


