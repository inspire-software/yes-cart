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

package org.yes.cart.web.support.service;

import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-10-25
 * Time: 6:55 PM
 */
public interface CustomerServiceFacade {

    /**
     * Check if this email is already registered.
     *
     * @param email email
     *
     * @return true if there is already an account with this email
     */
    boolean isCustomerRegistered(String email);

    /**
     * Register new account for given email in given shop.
     *
     * @param registrationShop shop where registration takes place
     * @param email            customer email
     * @param registrationData registration data
     *
     * @return password to login user
     */
    String registerCustomer(Shop registrationShop,
                            String email,
                            Map<String, Object> registrationData);

    /**
     * Find customer by email.
     *
     * @param email email
     *
     * @return customer object or null
     */
    Customer getCustomerByEmail(String email);

    /**
     * Reset password to given user and send generated password via email.
     *
     * @param shop     shop to render email
     * @param customer customer to create
     */
    void resetPassword(Shop shop, Customer customer);

    /**
     * List of custom attributes eligible for profile edit form.
     * CPOINT - This will be available to the shoppers to edit as they please, so
     * need to restrict attributes that should be hidden from shoppers.
     *
     * @param customer customer
     *
     * @return list of eligible attributes
     */
    List<? extends AttrValue> getCustomerRegistrationAttributes(Customer customer);

    /**
     * Update customer entry.
     *
     * @param customer customer
     */
    void updateCustomer(Customer customer);

    /**
     * Authenticate customer using username and password.
     *
     * @param username username
     * @param password raw password
     *
     * @return true if both username and password match
     */
    boolean authenticate(final String username, final String password);


}
