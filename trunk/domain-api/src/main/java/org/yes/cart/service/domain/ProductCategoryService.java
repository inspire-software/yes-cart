/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
     * Find single product-caterory entity by category and product id.
     * @param categoryId  category id
     * @param productId   product id
     * @return {@link ProductCategory} if found, otherwise null
     */
    ProductCategory findByCategoryIdProductId( long categoryId,  long productId);

     /**
     * Unlink product from all categories.
     *
     * @param productId  given product id
     */
    void removeByProductIds(long productId);


    /**
     * Get the next rank for product during product assignment.
     * Default step is 50.
     *
     * @param categoryId category id
     * @return rank.
     */
    int getNextRank(long categoryId);
}
