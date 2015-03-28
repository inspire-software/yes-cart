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

package org.yes.cart.domain.entity.bridge.support.impl;

import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopCategory;
import org.yes.cart.domain.entity.bridge.support.ShopCategoryRelationshipSupport;

import java.util.*;

/**
 * Extra logic to determine relationship between categories and shops.
 *
 * User: denispavlov
 * Date: 13-10-01
 * Time: 1:55 PM
 */
public class ShopCategoryRelationshipSupportImpl implements ShopCategoryRelationshipSupport {

    private final GenericDAO<Shop, Long> shopDao;
    private final GenericDAO<Category, Long> categoryDao;

    public ShopCategoryRelationshipSupportImpl(final GenericDAO<Shop, Long> shopDao,
                                               final GenericDAO<Category, Long> categoryDao) {
        this.shopDao = shopDao;
        this.categoryDao = categoryDao;
    }

    /** {@inheritDoc} */
    @Cacheable(value = "shopService-allShops")
    public List<Shop> getAll() {
        return this.shopDao.findAll();
    }

    private Set<Category> getShopCategories(final long shopId) {

        final Set<Category> result = new HashSet<Category>();

        for (ShopCategory shopCategory : shopDao.findById(shopId).getShopCategory()) {

            loadChildCategoriesRecursiveInternal(result, shopCategory.getCategory().getCategoryId());

        }

        return result;
    }

    private void loadChildCategoriesRecursiveInternal(final Set<Category> result, final Long categoryId) {

        result.add(categoryDao.findById(categoryId));

        final List<Category> categories = categoryDao.findByNamedQueryCached(
                "CATEGORIES.BY.PARENTID.WITHOUT.DATE.FILTERING",
                categoryId
        );

        result.addAll(categories);

        for (Category subCategory : categories) {

            loadChildCategoriesRecursiveInternal(result, subCategory.getCategoryId());

        }

    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopCategoriesIds"/*, key ="shop.getShopId()"*/)
    public Set<Long> getShopCategoriesIds(final long shopId) {
        return transform(getShopCategories(shopId));
    }

    public Set<Long> transform(final Collection<Category> categories) {
        final Set<Long> result = new LinkedHashSet<Long>(categories.size());
        for (Category category : categories) {
            result.add(category.getCategoryId());
        }
        return result;
    }

}
