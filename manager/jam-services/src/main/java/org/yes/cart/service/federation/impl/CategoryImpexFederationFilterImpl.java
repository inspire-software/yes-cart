/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.service.federation.impl;

import org.springframework.cache.CacheManager;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.federation.FederationFilter;
import org.yes.cart.service.federation.ShopFederationStrategy;

import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 16/09/2014
 * Time: 14:27
 */
public class CategoryImpexFederationFilterImpl extends CacheableImpexFederationFacadeImpl implements FederationFilter {

    private final ShopService shopService;
    private final CategoryService categoryService;

    public CategoryImpexFederationFilterImpl(final ShopFederationStrategy shopFederationStrategy,
                                             final List<String> roles,
                                             final ShopService shopService,
                                             final CategoryService categoryService,
                                             final CacheManager cacheManager,
                                             final List<String> cachesToFlushForTransientEntities) {
        super(shopFederationStrategy, roles, cacheManager, cachesToFlushForTransientEntities);
        this.shopService = shopService;
        this.categoryService = categoryService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isManageable(final Object object, final Class objectType) {

        if (!hasAccessRole()) {
            return false;
        }

        final Set<Long> manageableCategoryIds;
        final Set<String> guids = shopFederationStrategy.getAccessibleCategoryCatalogCodesByCurrentManager();
        if (guids.isEmpty()) {
            final Set<Long> manageableShopIds = shopFederationStrategy.getAccessibleShopIdsByCurrentManager();
            manageableCategoryIds = shopService.getShopsCategoriesIds(manageableShopIds);
        } else {
            manageableCategoryIds = categoryService.getAllCategoryIds(guids);
        }

        final Category cat = (Category) object;
        final Long catId = cat.getCategoryId();
        final Long parentCatId = cat.getParentId();

        final boolean isNew = isTransientEntity(cat);

        // Must have access to parent and if persistent the to category as well (e.g. if changing parent during import)
        final boolean accessible = (isNew && manageableCategoryIds.contains(parentCatId)) ||
                (!isNew && manageableCategoryIds.contains(catId));

        // Must flush caches since there may be other categories that are children to currently added one
        flushCachesIfTransient(cat);

        return accessible;

    }

}
