/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo;

import org.yes.cart.domain.vo.VoLicenseAgreement;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.domain.vo.VoManagerInfo;

import java.util.List;

/**
 * User: denispavlov
 * Date: 26/07/2016
 * Time: 08:58
 */
public interface VoManagementService {

    /**
     * Get information about current user
     * @return manager vo
     * @throws Exception errors
     */
    VoManager getMyself() throws Exception;

    /**
     * Get license for current user
     * @return license vo
     * @throws Exception errors
     */
    VoLicenseAgreement getMyAgreement() throws Exception;

    /**
     * Accept license for current user.
     * @return license vo
     * @throws Exception errors
     */
    VoLicenseAgreement acceptMyAgreement() throws Exception;

    /**
     * Get all vo in the system, filtered according to rights
     * @return all managers
     * @throws Exception errors
     */
    List<VoManagerInfo> getManagers() throws Exception;

    /**
     * Get vo by email.
     *
     * @param email email
     * @return vo
     * @throws Exception errors
     */
    VoManager getByEmail(String email) throws Exception;

    /**
     * Create new vo
     * @param voManager vo
     * @return persistent version
     * @throws Exception errors
     */
    VoManager createManager(VoManager voManager) throws Exception;

    /**
     * Update vo
     * @param voManager vo
     * @return persistent version
     * @throws Exception errors
     */
    VoManager updateManager(VoManager voManager) throws Exception;

    /**
     * Remove vo.
     * @param managerEmail manager email
     * @throws Exception errors
     */
    void deleteManager(String managerEmail) throws Exception;

    /**
     * Reset password to given vo.
     * @param email manager email
     * @throws Exception errors
     */
    void resetPassword(String email) throws Exception;

    /**
     * Update the manager disabled flag.
     *
     * @param manager manager user name
     * @param disabled true if manager account is disabled
     * @throws Exception errors
     */
    void updateDisabledFlag(String manager, boolean disabled) throws Exception;

}
