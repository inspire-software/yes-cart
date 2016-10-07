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

import org.yes.cart.domain.vo.VoRole;

import java.util.List;

/**
 * User: Denis Lozenko
 * Date 8/22/2016.
 */
public interface VoRoleService {

    /**
     * Get all vo in the system, filtered according to rights
     * @return all roles
     * @throws Exception
     */
    List<VoRole> getAllRoles() throws Exception;

    /**
     * Create new vo
     * @param vo vo
     * @return persistent version
     * @throws Exception
     */
    VoRole createRole(VoRole vo)  throws Exception;

    /**
     * Update vo
     * @param vo vo
     * @return persistent version
     * @throws Exception
     */
    VoRole updateRole(VoRole vo)  throws Exception;


    /**
     * Remove vo.
     *
     * @param role role code
     * @throws Exception
     */
    void removeRole(String role) throws Exception;
}
