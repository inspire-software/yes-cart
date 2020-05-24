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

import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.federation.FederationFilter;
import org.yes.cart.service.federation.ShopFederationStrategy;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 16/09/2014
 * Time: 14:19
 */
public class UiFederationFacadeImpl implements FederationFacade {

    private final ShopFederationStrategy shopFederationStrategy;
    private final Map<Class, FederationFilter> filters;

    public UiFederationFacadeImpl(final ShopFederationStrategy shopFederationStrategy,
                                  final Map<Class, FederationFilter> filters) {
        this.shopFederationStrategy = shopFederationStrategy;
        this.filters = filters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCurrentUserSystemAdmin() {
        return shopFederationStrategy.isCurrentUserSystemAdmin();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCurrentUser(final String role) {
        return shopFederationStrategy.isCurrentUser(role);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShopAccessibleByCurrentManager(final String shopCode) {
        return shopFederationStrategy.isShopAccessibleByCurrentManager(shopCode);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShopAccessibleByCurrentManager(final Long shopId) {
        return shopFederationStrategy.isShopAccessibleByCurrentManager(shopId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupplierCatalogAccessibleByCurrentManager(final String catalogCode) {
        return shopFederationStrategy.isSupplierCatalogAccessibleByCurrentManager(catalogCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> getAccessibleShopIdsByCurrentManager() {
        return shopFederationStrategy.getAccessibleShopIdsByCurrentManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAccessibleShopCodesByCurrentManager() {
        return shopFederationStrategy.getAccessibleShopCodesByCurrentManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAccessibleSupplierCatalogCodesByCurrentManager() {
        return shopFederationStrategy.getAccessibleSupplierCatalogCodesByCurrentManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAccessibleCategoryCatalogCodesByCurrentManager() {
        return shopFederationStrategy.getAccessibleCategoryCatalogCodesByCurrentManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> getAccessibleCatalogIdsByCurrentManager() {
        return shopFederationStrategy.getAccessibleCatalogIdsByCurrentManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyFederationFilter(final Collection list, final Class objectType) {
        if (shopFederationStrategy.isCurrentUserSystemAdmin()) {
            return;
        }
        final FederationFilter filter = filters.get(objectType);
        if (filter == null) {
            throw new IllegalArgumentException("Unknown object type: " + objectType);
        }
        filter.applyFederationFilter(list, objectType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isManageable(final Object object, final Class objectType) {
        if (shopFederationStrategy.isCurrentUserSystemAdmin()) {
            return true;
        }
        final FederationFilter filter = filters.get(objectType);
        return filter != null && filter.isManageable(object, objectType);
    }

}
