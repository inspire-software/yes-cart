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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;


/**
 * Product - category relation interface.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ProductCategoryDTO extends Identifiable {

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
     * @return product pk
     */
    long getProductId();

    /**
     * Set product's pk
     *
     * @param productId product pk
     */
    void setProductId(long productId);

    /**
     * Get category pk
     *
     * @return category pk
     */
    long getCategoryId();

    /**
     * Set category pk.
     *
     * @param categoryId category pk
     */
    void setCategoryId(long categoryId);

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

    /**
     * Get category name.
     *
     * @return category name
     */
    String getCategoryName();

    /**
     * Set category name.
     *
     * @param categoryName category name.
     */
    void setCategoryName(final String categoryName);


}
