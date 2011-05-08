package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.ProductCategory;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ProductCategoryService extends GenericService<ProductCategory> {

    /**
     * Delete product from given category
     *
     * @param categoryId given category id
     * @param productId  given product id
     */
    void removeByCategoryProductIds(long categoryId, long productId);


    /**
     * Get the next rank for product during product assignment.
     * Default step is 50.
     *
     * @param categoryId category id
     * @return rank.
     */
    int getNextRank(long categoryId);
}
