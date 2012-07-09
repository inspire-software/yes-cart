package org.yes.cart.domain.entity;

/**
 * Roles
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Role {

    String getCode();

    void setCode(String code);

    String getDescription();

    void setDescription(String description);

    long getRoleId();

    void setRoleId(long roleId);

}
