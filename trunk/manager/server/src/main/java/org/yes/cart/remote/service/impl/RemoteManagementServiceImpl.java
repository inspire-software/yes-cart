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

package org.yes.cart.remote.service.impl;

import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.ManagerDTO;
import org.yes.cart.domain.dto.RoleDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteManagementService;
import org.yes.cart.service.dto.ManagementService;
import org.yes.cart.service.federation.FederationFacade;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteManagementServiceImpl implements RemoteManagementService {

    private final ManagementService managementService;
    private final FederationFacade federationFacade;

    /**
     * Construct remote user management service.
     *
     * @param managementService
     * @param federationFacade
     */
    public RemoteManagementServiceImpl(final ManagementService managementService,
                                       final FederationFacade federationFacade) {
        this.managementService = managementService;
        this.federationFacade = federationFacade;
    }


    /**
     * {@inheritDoc}
     */
    public void addUser(final String userId, final String firstName, final String lastName, final String shopCode)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {

        if (federationFacade.isShopAccessibleByCurrentManager(shopCode)) {
            managementService.addUser(userId, firstName, lastName, shopCode);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<ManagerDTO> getManagers(final String emailFilter, final String firstNameFilter, final String lastNameFilter)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<ManagerDTO> managers = managementService.getManagers(emailFilter, firstNameFilter, lastNameFilter);
        federationFacade.applyFederationFilter(managers, ManagerDTO.class);
        return managers;
    }

    /**
     * {@inheritDoc}
     */
    public List<RoleDTO> getAssignedManagerRoles(String userId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(userId, ManagerDTO.class)) {
            return managementService.getAssignedManagerRoles(userId);
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public List<RoleDTO> getAvailableManagerRoles(String userId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(userId, ManagerDTO.class)) {
            return managementService.getAvailableManagerRoles(userId);
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public void updateUser(final String userId, final String firstName, final String lastName) {
        if (federationFacade.isManageable(userId, ManagerDTO.class)) {
            managementService.updateUser(userId, firstName, lastName);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void resetPassword(final String userId) {
        if (federationFacade.isManageable(userId, ManagerDTO.class)) {
            managementService.resetPassword(userId);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteUser(final String userId) {
        if (federationFacade.isManageable(userId, ManagerDTO.class)) {
            managementService.deleteUser(userId);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }


    /**
     * {@inheritDoc}
     */
    public List<RoleDTO> getRolesList()
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return managementService.getRolesList();
    }


    /**
     * {@inheritDoc}
     */
    public void addRole(final String role, final String description) {
        managementService.addRole(role, description);
    }

    /**
     * {@inheritDoc}
     */
    public void updateRole(String role, String description) {
        managementService.updateRole(role, description);
    }

    /**
     * {@inheritDoc}
     */
    public void deleteRole(final String role) {
        managementService.deleteRole(role);
    }

    /**
     * {@inheritDoc}
     */
    public void grantRole(final String userId, final String role) {
        if ("ROLE_SMADMIN".equals(role) && !federationFacade.isCurrentUserSystemAdmin()) {
            throw new AccessDeniedException("ACCESS DENIED");
        }
        if (federationFacade.isManageable(userId, ManagerDTO.class)) {
            managementService.grantRole(userId, role);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void revokeRole(final String userId, final String role) {
        if ("ROLE_SMADMIN".equals(role) && !federationFacade.isCurrentUserSystemAdmin()) {
            throw new AccessDeniedException("ACCESS DENIED");
        }
        if (federationFacade.isManageable(userId, ManagerDTO.class)) {
            managementService.revokeRole(userId, role);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<ShopDTO> getAssignedManagerShops(final String userId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(userId, ManagerDTO.class)) {
            final List<ShopDTO> assigned = managementService.getAssignedManagerShops(userId);

            if (!federationFacade.isCurrentUserSystemAdmin()) { // restrict other managers
                final Set<Long> currentAssignedIds = federationFacade.getAccessibleShopIdsByCurrentManager();
                final Iterator<ShopDTO> availableIt = assigned.iterator();
                while (availableIt.hasNext()) {
                    final ShopDTO shop = availableIt.next();
                    if (!currentAssignedIds.contains(shop.getShopId())) {
                        availableIt.remove();
                    }
                }
            }
            return assigned;
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public List<ShopDTO> getAvailableManagerShops(final String userId) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        if (federationFacade.isManageable(userId, ManagerDTO.class)) {
            final List<ShopDTO> available = managementService.getAvailableManagerShops(userId);

            if (!federationFacade.isCurrentUserSystemAdmin()) { // restrict other managers
                final Set<Long> currentAssignedIds = federationFacade.getAccessibleShopIdsByCurrentManager();
                final Iterator<ShopDTO> availableIt = available.iterator();
                while (availableIt.hasNext()) {
                    final ShopDTO shop = availableIt.next();
                    if (!currentAssignedIds.contains(shop.getShopId())) {
                        availableIt.remove();
                    }
                }
            }
            return available;
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public void grantShop(final String userId, final String shopCode) {

        if (federationFacade.isManageable(userId, ManagerDTO.class)
                && federationFacade.isShopAccessibleByCurrentManager(shopCode)) {
            managementService.grantShop(userId, shopCode);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }

    }

    /**
     * {@inheritDoc}
     */
    public void revokeShop(final String userId, final String shopCode) {
        if (federationFacade.isManageable(userId, ManagerDTO.class)
                && federationFacade.isShopAccessibleByCurrentManager(shopCode)) {
            managementService.revokeShop(userId, shopCode);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }

}
