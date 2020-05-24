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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.dto.ProductDTO;
import org.yes.cart.service.dto.DtoProductService;
import org.yes.cart.service.federation.FederationFilter;
import org.yes.cart.service.federation.ShopFederationStrategy;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 16/09/2014
 * Time: 14:27
 */
public class ProductUiFederationFilterImpl implements FederationFilter {

    private final ShopFederationStrategy shopFederationStrategy;
    private final DtoProductService dtoProductService;

    public ProductUiFederationFilterImpl(final ShopFederationStrategy shopFederationStrategy,
                                         final DtoProductService dtoProductService) {
        this.shopFederationStrategy = shopFederationStrategy;
        this.dtoProductService = dtoProductService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyFederationFilter(final Collection list, final Class objectType) {

        final Set<String> suppliers = shopFederationStrategy.getAccessibleSupplierCatalogCodesByCurrentManager();

        final Iterator<ProductDTO> productIt = list.iterator();
        while (productIt.hasNext()) {
            final ProductDTO product = productIt.next();
            if (StringUtils.isNotBlank(product.getSupplierCatalogCode()) &&
                    !suppliers.contains(product.getSupplierCatalogCode())) {
                productIt.remove();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isManageable(final Object object, final Class objectType) {

        try {
            final ProductDTO product = dtoProductService.getById((Long) object);
            if (StringUtils.isBlank(product.getSupplierCatalogCode())) {
                return true;
            }
            final Set<String> suppliers = shopFederationStrategy.getAccessibleSupplierCatalogCodesByCurrentManager();
            return suppliers.contains(product.getSupplierCatalogCode());

        } catch (Exception e) {
            // ok
        }
        return false;
    }

}
