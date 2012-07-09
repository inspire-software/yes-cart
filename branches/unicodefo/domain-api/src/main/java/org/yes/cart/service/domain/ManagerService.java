package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Manager;
import org.yes.cart.domain.entity.Shop;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ManagerService extends GenericService<Manager> {


    /**
     * Reset password to given user and send generated password via email.
     *
     * @param manager to reset password
     */
    void resetPassword(Manager manager);

    /**
     * Create manager and assing it to manage particular shop.
     *
     * @param manager to reset password
     * @param shop    shop to assing
     * @return customer instance
     */
    Manager create(final Manager manager, final Shop shop);

    /**
     * Find manager by email using like operation
     *
     * @param email filter
     * @return list of found managers
     */
    List<Manager> findByEmail(String email);


}
