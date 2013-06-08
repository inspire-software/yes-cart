/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
