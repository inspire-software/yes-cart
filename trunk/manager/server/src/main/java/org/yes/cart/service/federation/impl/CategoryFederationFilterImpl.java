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

package org.yes.cart.service.federation.impl;

import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.dto.CustomerOrderDTO;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.dto.DtoCustomerOrderService;
import org.yes.cart.service.federation.FederationFilter;
import org.yes.cart.service.federation.ShopFederationStrategy;

import java.util.*;

/**
 * User: denispavlov
 * Date: 16/09/2014
 * Time: 14:27
 */
public class CategoryFederationFilterImpl implements FederationFilter {

    private final ShopFederationStrategy shopFederationStrategy;
    private final ShopService shopService;

    public CategoryFederationFilterImpl(final ShopFederationStrategy shopFederationStrategy,
                                        final ShopService shopService) {
        this.shopFederationStrategy = shopFederationStrategy;
        this.shopService = shopService;
    }

    /**
     * {@inheritDoc}
     */
    public void applyFederationFilter(final Collection list, final Class objectType) {

        final Set<Long> manageableShopIds = shopFederationStrategy.getAccessibleShopIdsByCurrentManager();
        final Set<Long> manageableCategoryIds = new HashSet<Long>();
        for (final Long shopId : manageableShopIds) {
            manageableCategoryIds.addAll(shopService.getShopAllCategoriesIds(shopId));
        }

        final Iterator<CategoryDTO> categoryIt = list.iterator();
        while (categoryIt.hasNext()) {
            final CategoryDTO category = categoryIt.next();
            if (!manageableCategoryIds.contains(category.getCategoryId())) {
                categoryIt.remove();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isManageable(final Object object, final Class objectType) {

        final Set<Long> manageableShopIds = shopFederationStrategy.getAccessibleShopIdsByCurrentManager();
        final Set<Long> manageableCategoryIds = new HashSet<Long>();
        for (final Long shopId : manageableShopIds) {
            manageableCategoryIds.addAll(shopService.getShopAllCategoriesIds(shopId));
        }

        return manageableCategoryIds.contains(object);
    }

}
