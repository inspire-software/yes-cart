/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.bridge.support.ShopCategoryRelationshipSupport;

import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 13:40
 */
public class ShopCategoryRelationshipSupportCachedImpl implements ShopCategoryRelationshipSupport {

    private final ShopCategoryRelationshipSupport support;

    public ShopCategoryRelationshipSupportCachedImpl(final ShopCategoryRelationshipSupport support) {
        this.support = support;
    }

    /** {@inheritDoc} */
    @Cacheable(value = "shopService-allShops")
    public List<Shop> getAll() {
        return support.getAll();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopCategoriesIds"/*, key ="shop.getShopId()"*/)
    public Set<Long> getShopCategoriesIds(final long shopId) {
        return support.getShopCategoriesIds(shopId);
    }

    /**
     * {@inheritDoc} Just to cache
     */
    @Cacheable(value = "categoryService-byId")
    public Category getCategoryById(final long categoryId) {
        return support.getCategoryById(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-categoryParentsIds"/*, key ="shop.getShopId()"*/)
    public Set<Long> getCategoryParentsIds(final long categoryId) {
        return support.getCategoryParentsIds(categoryId);
    }
}
