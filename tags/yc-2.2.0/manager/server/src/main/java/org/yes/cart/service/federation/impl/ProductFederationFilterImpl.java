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

import org.yes.cart.domain.dto.ProductCategoryDTO;
import org.yes.cart.domain.dto.ProductDTO;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.dto.DtoProductService;
import org.yes.cart.service.federation.FederationFilter;
import org.yes.cart.service.federation.ShopFederationStrategy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 16/09/2014
 * Time: 14:27
 */
public class ProductFederationFilterImpl implements FederationFilter {

    private final ShopFederationStrategy shopFederationStrategy;
    private final ShopService shopService;
    private final DtoProductService dtoProductService;

    public ProductFederationFilterImpl(final ShopFederationStrategy shopFederationStrategy,
                                       final ShopService shopService,
                                       final DtoProductService dtoProductService) {
        this.shopFederationStrategy = shopFederationStrategy;
        this.shopService = shopService;
        this.dtoProductService = dtoProductService;
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

        final Iterator<ProductDTO> productIt = list.iterator();
        while (productIt.hasNext()) {
            final ProductDTO product = productIt.next();
            boolean hasManageableCategory = false;
            for (final ProductCategoryDTO pcat : product.getProductCategoryDTOs()) {
                if (manageableCategoryIds.contains(pcat.getCategoryId())) {
                    hasManageableCategory = true;
                    break;
                }
            }
            if (!hasManageableCategory) {
                productIt.remove();
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


        try {
            final ProductDTO product = dtoProductService.getById((Long) object);
            for (final ProductCategoryDTO pcat : product.getProductCategoryDTOs()) {
                if (manageableCategoryIds.contains(pcat.getCategoryId())) {
                    return true;
                }
            }

        } catch (Exception e) {
            // ok
        }
        return false;
    }

}
