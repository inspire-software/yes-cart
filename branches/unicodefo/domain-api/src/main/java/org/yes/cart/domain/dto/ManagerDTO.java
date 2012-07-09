package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

/**
 * Shop manager DTO.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ManagerDTO extends Identifiable {

    /**
     * Primapy key.
     *
     * @return pk value.
     */
    long getManagerId();

    /**
     * Set pk.
     *
     * @param managerId pk value.
     */
    void setManagerId(long managerId);

    /**
     * Get the login mane, that equals to email.
     *
     * @return user email.
     */
    String getEmail();

    /**
     * Set email.
     *
     * @param email email.
     */
    void setEmail(String email);


    /**
     * Get first name.
     *
     * @return first name.
     */
    String getFirstName();

    /**
     * Set first name
     *
     * @param firstName new first name.
     */
    void setFirstName(String firstName);


    /**
     * Get last name
     *
     * @return last name.
     */
    String getLastName();

    /**
     * Set last name
     *
     * @param lastName lat name.
     */
    void setLastName(String lastName);

}
