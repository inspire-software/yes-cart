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

package org.yes.cart.service.federation.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.service.dto.ManagementService;
import org.yes.cart.service.federation.ShopFederationStrategy;

import java.util.*;

/**
 * User: denispavlov
 * Date: 16/09/2014
 * Time: 14:32
 */
public class ShopFederationStrategyImpl implements ShopFederationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(ShopFederationStrategyImpl.class);

    private static final AdminContext EMPTY = new AdminContext(
            false,
            Collections.emptySet(),
            Collections.emptySet(),
            Collections.emptyMap(),
            Collections.emptySet(),
            Collections.emptySet(),
            Collections.emptySet(),
            Collections.emptySet()
    );

    private final ManagementService managementService;

    private final Cache USER_ACCESS_CACHE_ADMIN;

    public ShopFederationStrategyImpl(final ManagementService managementService,
                                      final CacheManager cacheManager) {
        this.managementService = managementService;
        USER_ACCESS_CACHE_ADMIN = cacheManager.getCache("shopFederationStrategy-admin");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCurrentUserSystemAdmin() {
        return getCurrent().isAdmin();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCurrentUser(final String role) {
        return getCurrent().getRoles().contains(role);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShopAccessibleByCurrentManager(final String shopCode) {
        final AdminContext ctx = getCurrent();
        if (ctx.isAdmin()) {
            return true;
        }
        return ctx.getShopCodes().contains(shopCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShopAccessibleByCurrentManager(final Long shopId) {
        final AdminContext ctx = getCurrent();
        if (ctx.isAdmin()) {
            return true;
        }
        return ctx.getShopIds().contains(shopId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupplierCatalogAccessibleByCurrentManager(final String catalogCode) {
        final AdminContext ctx = getCurrent();
        if (ctx.isAdmin()) {
            return true;
        }
        return ctx.getSupplierCodes().contains(catalogCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> getAccessibleShopIdsByCurrentManager() {
        return getCurrent().getShopIds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAccessibleShopCodesByCurrentManager() {
        return getCurrent().getShopCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAccessibleSupplierCatalogCodesByCurrentManager() {
        return getCurrent().getSupplierCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAccessibleCategoryCatalogCodesByCurrentManager() {
        return getCurrent().getCatalogCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> getAccessibleCatalogIdsByCurrentManager() {
        return getCurrent().getCatalogTopIds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmployeeManageableByCurrentManager(final String employeeId) {

        final AdminContext employee = getFromCache(employeeId);
        final Map<Long, Long> employeeShopIds = employee.getShopWithMasterIds();

        final AdminContext current = getCurrent();
        final Set<Long> currentShopIds = current.getShopIds();

        for (final Map.Entry<Long, Long> shopAndMaster : employeeShopIds.entrySet()) {
            if (currentShopIds.contains(shopAndMaster.getKey())) {
                /*
                    If this manager has access to top level shop to which employee has access to
                    OR this manager also has access to master shop of this employees sub.
                    This means: master shop managers can manage sub manager but sub manager cannot
                    manage master shop managers (or other sub managers).
                 */
                if (shopAndMaster.getValue() == null || currentShopIds.contains(shopAndMaster.getValue())) {
                    return true;
                }
            }
        }
        return false;

    }

    private AdminContext getCurrent() {
        if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) {
            return EMPTY;
        }
        final String currentManager = SecurityContextHolder.getContext().getAuthentication().getName();
        return getFromCache(currentManager);
    }

    private AdminContext getFromCache(final String manager) {
        AdminContext admin = getValueWrapper(USER_ACCESS_CACHE_ADMIN.get(manager));
        if (admin == null) {
            admin = loadAdminContext(manager);
            if (admin != EMPTY) {
                USER_ACCESS_CACHE_ADMIN.put(manager, admin);
            }
        }
        return admin;
    }

    private AdminContext loadAdminContext(final String manager) {
        try {
            final Set<String> roles = new TreeSet<>();
            managementService.getAssignedManagerRoles(manager).forEach(role -> roles.add(role.getCode()));
            final boolean admin = roles.contains("ROLE_SMADMIN");

            final Set<Long> shopIds = new TreeSet<>();
            final Map<Long, Long> shopWithMasterIds = new HashMap<>();
            final Set<String> shopCodes = new TreeSet<>();
            managementService.getAssignedManagerShops(manager, true).forEach(shop -> {
                shopIds.add(shop.getShopId());
                shopWithMasterIds.put(shop.getShopId(), shop.getMasterId());
                shopCodes.add(shop.getCode());
            });

            final Set<String> supplierCodes = new TreeSet<>(managementService.getAssignedManagerSupplierCatalogs(manager));

            final Set<String> catalogCodes = new TreeSet<>(managementService.getAssignedManagerCategoryCatalogs(manager));

            final Set<Long> catalogTopIds = new TreeSet<>(managementService.getAssignedManagerCatalogHierarchy(manager));

            return new AdminContext(
                    admin,
                    roles,
                    shopIds,
                    shopWithMasterIds,
                    shopCodes,
                    supplierCodes,
                    catalogCodes,
                    catalogTopIds
            );

        } catch (Exception exp) {
            LOG.error("Failed to load manager federation context " + exp.getMessage(), exp);
            return EMPTY;
        }
    }

    private <T> T getValueWrapper(final Cache.ValueWrapper wrapper) {
        if (wrapper != null) {
            return (T) wrapper.get();
        }
        return null;
    }

    private static class AdminContext {

        private final boolean admin;
        private final Set<String> roles;
        private final Set<Long> shopIds;
        private final Map<Long, Long> shopWithMasterIds;
        private final Set<String> shopCodes;
        private final Set<String> supplierCodes;
        private final Set<String> catalogCodes;
        private final Set<Long> catalogTopIds;

        private AdminContext(final boolean admin,
                             final Set<String> roles,
                             final Set<Long> shopIds,
                             final Map<Long, Long> shopWithMasterIds,
                             final Set<String> shopCodes,
                             final Set<String> supplierCodes,
                             final Set<String> catalogCodes,
                             final Set<Long> catalogTopIds) {
            this.admin = admin;
            this.roles = Collections.unmodifiableSet(roles);
            this.shopIds = Collections.unmodifiableSet(shopIds);
            this.shopWithMasterIds = Collections.unmodifiableMap(shopWithMasterIds);
            this.shopCodes = Collections.unmodifiableSet(shopCodes);
            this.supplierCodes = Collections.unmodifiableSet(supplierCodes);
            this.catalogCodes = Collections.unmodifiableSet(catalogCodes);
            this.catalogTopIds = Collections.unmodifiableSet(catalogTopIds);
        }

        public boolean isAdmin() {
            return admin;
        }

        public Set<String> getRoles() {
            return roles;
        }

        public Set<Long> getShopIds() {
            return shopIds;
        }

        public Map<Long, Long> getShopWithMasterIds() {
            return shopWithMasterIds;
        }

        public Set<String> getShopCodes() {
            return shopCodes;
        }

        public Set<String> getSupplierCodes() {
            return supplierCodes;
        }

        public Set<String> getCatalogCodes() {
            return catalogCodes;
        }

        public Set<Long> getCatalogTopIds() {
            return catalogTopIds;
        }
    }

}
