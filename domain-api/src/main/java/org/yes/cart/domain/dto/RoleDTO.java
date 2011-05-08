package org.yes.cart.domain.dto;

import java.io.Serializable;

/**
 * Role DTO.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface RoleDTO extends Serializable {

    /**
     * Get role
     *
     * @return role
     */
    String getCode();

    /**
     * Set role
     *
     * @param code role
     */
    void setCode(String code);

    /**
     * Get role desription
     *
     * @return role description
     */
    String getDescription();

    /**
     * Set role description
     *
     * @param description descrition
     */
    void setDescription(String description);


}
