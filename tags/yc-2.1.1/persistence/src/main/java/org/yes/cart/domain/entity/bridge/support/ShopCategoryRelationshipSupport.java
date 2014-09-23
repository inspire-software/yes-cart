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

package org.yes.cart.domain.entity.bridge.support;

import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;

import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 13-10-01
 * Time: 1:52 PM
 */
public interface ShopCategoryRelationshipSupport {

    /**
     * Get all entities.
     *
     * @return list of all entities
     */
    List<Shop> getAll();

    /**
     * Get all categories including child categories, that belong to given shop.
     *
     * @param shop given shop
     * @return linear representation of category tree
     */
    Set<Long> getShopCategoriesIds(Shop shop);

    /**
     * Get all categories including child categories, that belong to given shop.
     *
     * @param shop given shop
     * @return linear representation of category tree
     */
    Set<Category> getShopCategories(Shop shop);

}
