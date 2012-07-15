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
