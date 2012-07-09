package org.yes.cart.domain.entity;

/**
 * Relation between manages and his roles
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ManagerRole extends Auditable {

    String getEmail();

    void setEmail(String email);

    String getCode();

    void setCode(String code);

    long getManagerRoleId();

    void setManagerRoleId(long managerRoleId);
}
