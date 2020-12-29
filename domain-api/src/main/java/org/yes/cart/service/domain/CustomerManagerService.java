/*
 * Copyright 2009 Inspire-Software.com
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

import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;

import java.util.List;

/**
 * User: denispavlov
 * Date: 26/10/2019
 * Time: 10:07
 */
public interface CustomerManagerService {

    /**
     * Check if manager login is enabled for given shop.
     *
     * @param shop shop
     *
     * @return true if enabled
     */
    boolean isCustomerManagerLoginEnabled(Shop shop);

    /**
     * Get customer by login.
     *
     * @param login login
     *
     * @return {@link Customer} or null if customer not found
     */
    Customer getCustomerByLogin(String login, Shop shop);

    /**
     * Check is customer already registered.
     *
     * @param email email to check
     * @param shop shop
     *
     * @return true in case if email unique.
     */
    boolean isManagerExists(String email, Shop shop);

    /**
     * Check is provided password for customer valid.
     *
     * @param email    email to check
     * @param shop     shop
     * @param password password
     * @return true in case if email unique.
     */
    boolean isPasswordValid(String email, Shop shop, String password);

    /**
     * Get customer shops by email.
     *
     * @param customer customer
     *
     * @return List of {@link Shop} or null if customer not found
     */
    List<Shop> getCustomerShops(Customer customer);

}
