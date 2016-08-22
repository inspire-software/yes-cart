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
