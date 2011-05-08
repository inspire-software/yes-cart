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
     * @return {@link Product}
     */
    public Product getProduct();

    /**
     * Set {@link Product}
     *
     * @param product {@link Product}
     */
    public void setProduct(Product product);

    /**
     * Get {@link Category}
     *
     * @return {@link Category}
     */
    public Category getCategory();

    /**
     * Set {@link Category}
     *
     * @param category {@link Category}
     */
    public void setCategory(Category category);

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

}


