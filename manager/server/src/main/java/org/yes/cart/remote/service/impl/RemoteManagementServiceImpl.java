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

import org.yes.cart.domain.dto.ManagerDTO;
import org.yes.cart.domain.dto.RoleDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteManagementService;
import org.yes.cart.service.dto.ManagementService;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteManagementServiceImpl implements RemoteManagementService {

    private final ManagementService managementService;

    /**
     * Construct remote user managment service.
     *
     * @param managementService
     */
    public RemoteManagementServiceImpl(final ManagementService managementService) {
        this.managementService = managementService;
    }


    /**
     * {@inheritDoc}
     */
    public void addUser(final String userId, final String firstName, final String lastName)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        managementService.addUser(userId, firstName, lastName);
    }

    /**
     * {@inheritDoc}
     */
    public List<ManagerDTO> getManagers(final String emailFilter, final String firstNameFilter, final String lastNameFilter)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return managementService.getManagers(emailFilter, firstNameFilter, lastNameFilter);
    }

    /**
     * {@inheritDoc}
     */
    public List<RoleDTO> getAssignedManagerRoles(String userId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return managementService.getAssignedManagerRoles(userId);
    }

    /**
     * {@inheritDoc}
     */
    public List<RoleDTO> getAvailableManagerRoles(String userId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return managementService.getAvailableManagerRoles(userId);
    }

    /**
     * {@inheritDoc}
     */
    public void updateUser(final String userId, final String firstName, final String lastName) {
        managementService.updateUser(userId, firstName, lastName);
    }

    /**
     * {@inheritDoc}
     */
    public void resetPassword(final String userId) {
        managementService.resetPassword(userId);
    }

    /**
     * {@inheritDoc}
     */
    public void deleteUser(final String userId) {
        managementService.deleteUser(userId);
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
    public void addRole(final String role, final String decription) {
        managementService.addRole(role, decription);
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
        managementService.grantRole(userId, role);
    }

    /**
     * {@inheritDoc}
     */
    public void revokeRole(final String userId, final String role) {
        managementService.revokeRole(userId, role);
    }

}
