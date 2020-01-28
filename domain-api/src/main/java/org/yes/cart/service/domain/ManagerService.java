/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Manager;
import org.yes.cart.domain.entity.Shop;

import java.util.List;
import java.util.Map;

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
     * Create manager and assign it to manage particular shop.
     *
     * @param manager to reset password
     * @param shop    shop to assign
     * @param roles   roles to assign
     *
     * @return customer instance
     */
    Manager create(Manager manager, Shop shop, String ... roles);

    /**
     * Find manager by email exact
     *
     * @param email filter
     * @return list of found managers
     */
    Manager getByEmail(String email);

    /**
     * Find manager by email exact
     *
     * @param email filter
     * @return list of found managers
     */
    Manager findByEmail(String email);

    /**
     * Find manager by email using like operation
     *
     * @param email filter
     * @return list of found managers
     */
    List<Manager> findByEmailPartial(String email);



    /**
     * Find managers by given search criteria. Search will be performed using like operation.
     *
     * @param start             start
     * @param offset            page size
     * @param sort              optional sort property
     * @param sortDescending    optional sort property direction
     * @param filter            optional filters (e.g. firstname, lastname)
     *
     * @return list of warehouses, that match search criteria or empty list if nobody found or null if no search criteria provided.
     */
    List<Manager> findManagers(int start,
                               int offset,
                               String sort,
                               boolean sortDescending,
                               Map<String, List> filter);

    /**
     * Find managers by given search criteria. Search will be performed using like operation.
     *
     * @param filter            optional filters (e.g. firstname, lastname)
     *
     * @return count
     */
    int findManagerCount(Map<String, List> filter);


}
