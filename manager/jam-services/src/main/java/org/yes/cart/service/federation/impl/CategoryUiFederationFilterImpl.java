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

import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.federation.FederationFilter;
import org.yes.cart.service.federation.ShopFederationStrategy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 16/09/2014
 * Time: 14:27
 */
public class CategoryUiFederationFilterImpl implements FederationFilter {

    private final ShopFederationStrategy shopFederationStrategy;
    private final ShopService shopService;
    private final CategoryService categoryService;

    public CategoryUiFederationFilterImpl(final ShopFederationStrategy shopFederationStrategy,
                                          final ShopService shopService,
                                          final CategoryService categoryService) {
        this.shopFederationStrategy = shopFederationStrategy;
        this.shopService = shopService;
        this.categoryService = categoryService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyFederationFilter(final Collection list, final Class objectType) {

        final Set<Long> manageableCategoryIds = getManageableCategoryIds();

        list.removeIf(category -> !manageableCategoryIds.contains(((CategoryDTO) category).getCategoryId()));
    }


    Set<Long> getManageableCategoryIds() {
        final Set<Long> manageableCategoryIds;
        final Set<String> guids = shopFederationStrategy.getAccessibleCategoryCatalogCodesByCurrentManager();
        if (guids.isEmpty()) {
            final Set<Long> manageableShopIds = shopFederationStrategy.getAccessibleShopIdsByCurrentManager();
            manageableCategoryIds = shopService.getShopsCategoriesIds(manageableShopIds);
        } else {
            manageableCategoryIds = categoryService.getAllCategoryIds(guids);
        }
        return manageableCategoryIds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isManageable(final Object object, final Class objectType) {

        final Set<Long> manageableCategoryIds = getManageableCategoryIds();

        return manageableCategoryIds.contains(object);
    }

}
