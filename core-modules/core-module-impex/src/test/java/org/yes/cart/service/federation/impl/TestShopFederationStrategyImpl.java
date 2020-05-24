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

import org.yes.cart.service.federation.ShopFederationStrategy;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 16/09/2014
 * Time: 14:32
 */
public class TestShopFederationStrategyImpl implements ShopFederationStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCurrentUserSystemAdmin() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCurrentUser(final String role) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShopAccessibleByCurrentManager(final String shopCode) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShopAccessibleByCurrentManager(final Long shopId) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupplierCatalogAccessibleByCurrentManager(final String catalogCode) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> getAccessibleShopIdsByCurrentManager() {
        return new HashSet<>(Arrays.asList(10L, 20L, 30L, 40L, 50L, 60L));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAccessibleShopCodesByCurrentManager() {
        return new HashSet<>(Arrays.asList("SHOIP1", "SHOIP2", "SHOIP3", "SHOIP4", "SHOIP5", "JEWEL_SHOP"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAccessibleSupplierCatalogCodesByCurrentManager() {
        return new HashSet<>(Arrays.asList("CAT001", "CAT002", "CAT003"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAccessibleCategoryCatalogCodesByCurrentManager() {
        return Collections.emptySet();
    }

    @Override
    public Set<Long> getAccessibleCatalogIdsByCurrentManager() {
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmployeeManageableByCurrentManager(final String employeeId) {
        return true;
    }
}
