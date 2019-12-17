package org.yes.cart.bulkcommon.service.support.auth.impl;

import org.yes.cart.service.federation.FederationFacade;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 20/10/2018
 * Time: 13:16
 */
public class SystemAdminFederationFacade implements FederationFacade {

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
        return true;
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
    public boolean isCategoryCatalogAccessibleByCurrentManager(final String catalogCode) {
        return true;
    }

    @Override
    public Set<Long> getAccessibleShopIdsByCurrentManager() {
        return null;
    }

    @Override
    public Set<String> getAccessibleShopCodesByCurrentManager() {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getAccessibleSupplierCatalogCodesByCurrentManager() {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getAccessibleCategoryCatalogCodesByCurrentManager() {
        return Collections.emptySet();
    }

    @Override
    public Set<Long> getAccessibleCatalogIdsByCurrentManager() {
        return Collections.emptySet();
    }

    @Override
    public void applyFederationFilter(final Collection list, final Class objectType) {

    }

    @Override
    public boolean isManageable(final Object object, final Class objectType) {
        return true;
    }
}
