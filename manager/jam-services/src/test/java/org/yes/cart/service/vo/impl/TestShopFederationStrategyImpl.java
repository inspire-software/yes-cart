package org.yes.cart.service.vo.impl;

import org.yes.cart.service.federation.ShopFederationStrategy;

import java.util.Set;

/**
 * User: denispavlov
 * Date: 31/08/2017
 * Time: 15:32
 */
public class TestShopFederationStrategyImpl implements ShopFederationStrategy {

    @Override
    public boolean isCurrentUserSystemAdmin() {
        return true;
    }

    @Override
    public boolean isCurrentUser(final String role) {
        return true;
    }

    @Override
    public boolean isShopAccessibleByCurrentManager(final String shopCode) {
        return false;
    }

    @Override
    public boolean isShopAccessibleByCurrentManager(final Long shopId) {
        return true;
    }

    @Override
    public boolean isSupplierCatalogAccessibleByCurrentManager(final String catalogCode) {
        return true;
    }

    @Override
    public Set<Long> getAccessibleShopIdsByCurrentManager() {
        return null;
    }

    @Override
    public Set<String> getAccessibleShopCodesByCurrentManager() {
        return null;
    }

    @Override
    public Set<String> getAccessibleSupplierCatalogCodesByCurrentManager() {
        return null;
    }

    @Override
    public Set<String> getAccessibleCategoryCatalogCodesByCurrentManager() {
        return null;
    }

    @Override
    public Set<Long> getAccessibleCatalogIdsByCurrentManager() {
        return null;
    }

    @Override
    public boolean isEmployeeManageableByCurrentManager(final String employeeId) {
        return true;
    }
}
