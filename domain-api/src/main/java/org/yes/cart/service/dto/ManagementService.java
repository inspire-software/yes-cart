package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.ManagerDTO;
import org.yes.cart.domain.dto.RoleDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * User managment service allow
 * to manage users, roles and
 * relationы between them.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ManagementService {

    /**
     * Add new user.
     *
     * @param userId    user email
     * @param firstName first name
     * @param lastName  last name
     * @throws java.io.UnsupportedEncodingException
     *          in case of bad encoding
     * @throws java.security.NoSuchAlgorithmException
     *          in case of bad algoritm
     */
    void addUser(String userId, String firstName, String lastName)
            throws NoSuchAlgorithmException, UnsupportedEncodingException;

    /**
     * Get the list of managers by given filtring citeria.
     *
     * @param emailFilter     optional email filter
     * @param firstNameFilter optional first name filter
     * @param lastNameFilter  optional last name filter
     * @return list of managers dto that match given criteria
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration error
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case if some problems with reflection
     */
    List<ManagerDTO> getManagers(final String emailFilter,
                                        final String firstNameFilter,
                                        final String lastNameFilter)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get the assigned to manager roles
     *
     * @param userId user email
     * @return list of assigned roles
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration error
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case if some problems with reflection
     */
    List<RoleDTO> getAssignedManagerRoles(String userId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get the available to manager roles. Mean all roles minus assigned.
     *
     * @param userId user email
     * @return list of available roles
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration error
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case if some problems with reflection
     */
    List<RoleDTO> getAvailableManagerRoles(String userId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Update user names by given user id.
     *
     * @param userId    user email
     * @param firstName first name
     * @param lastName  last name
     */
    void updateUser(String userId, String firstName, String lastName);

    /**
     * Reset password to given user and send generated password via email.
     *
     * @param userId user email
     */
    void resetPassword(String userId);

    /**
     * Delete user by given user id.
     * All {@link org.yes.cart.domain.entity.ManagerRole} related to this user will be deleted.
     *
     * @param userId given user id
     */
    void deleteUser(String userId);

    /**
     * Get all roles.
     *
     * @return List of all roles.
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of entity to dto mapping errors
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of caonfig errors
     */
    List<RoleDTO> getRolesList() throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Add new role.
     *
     * @param role       role
     * @param decription role description
     */
    void addRole(String role, String decription);

    /**
     * Update role description.
     *
     * @param role       given role
     * @param decription descrition
     */
    void updateRole(String role, String decription);

    /**
     * Delete role. All {@link org.yes.cart.domain.entity.ManagerRole} related to this role will be deleted.
     *
     * @param role role
     */
    void deleteRole(String role);

    /**
     * Grant given role to user
     *
     * @param userId user id
     * @param role   role
     */
    void grantRole(String userId, String role);

    /**
     * Revoke role from user.
     *
     * @param userId user id
     * @param role   role
     */
    void revokeRole(String userId, String role);

}
