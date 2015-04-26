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

import org.yes.cart.domain.entity.AttrValueCustomer;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface CustomerService extends GenericService<Customer> {

    /**
     * Find customer by given search criteria. Search will be performed using like operation.
     *
     *
     * @param email      optional email
     * @param firstname  optional first name
     * @param lastname   optional last name
     * @param middlename optional middle name
     * @param tag        optional tag
     * @return list of persons, that match search criteria or empty list if nobody found or null if no search criteria provided.
     */
    List<Customer> findCustomer(String email, String firstname, String lastname, String middlename, String tag);

    /**
     * Get customer by email.
     *
     * @param email email
     *
     * @return {@link Customer} or null if customer not found
     */
    Customer getCustomerByEmail(String email);

    /**
     * Get customer by auth token.
     *
     * @param token auth token
     *
     * @return {@link Customer} or null if customer not found
     */
    Customer getCustomerByToken(String token);

    /**
     * Get customer by public key exact match.
     *
     * @param publicKey public key
     * @param lastName last name
     *
     * @return {@link Customer} or null if customer not found
     */
    Customer getCustomerByPublicKey(String publicKey, String lastName);

    /**
     * Get customer shops by email.
     *
     * @param email email
     * @return List of {@link Shop} or null if customer not found
     */
    List<Shop> getCustomerShopsByEmail(String email);

    /**
     * Get name as specified by shop name formatting.
     *
     * @param customer customer
     * @param shop shop
     *
     * @return name
     */
    String formatNameFor(Customer customer, Shop shop);

    /**
     * Check is customer already registered.
     *
     * @param email email to check
     * @return true in case if email unique.
     */
    boolean isCustomerExists(String email);

    /**
     * Check is provided password for customer valid.
     *
     * @param email    email to check
     * @param password password
     * @return true in case if email unique.
     */
    boolean isPasswordValid(String email, String password);

    /**
     * Reset password to given user and send generated password via email.
     *
     * @param customer customer to create
     * @param shop     shop to assign
     * @param authToken authentication token for password reset
     */
    void resetPassword(Customer customer, Shop shop, String authToken);


    /**
     * Create customer and assign it to particular shop
     *
     * @param customer customer to create
     * @param shop     shop to assign
     * @return customer instance
     */
    Customer create(Customer customer, Shop shop);


    /**
     * Update customer and assign it to particular shop
     *
     * @param email customer to update
     * @param shopCode shop to assign
     * @return customer instance
     */
    Customer update(String email, String shopCode);


    /**
     * Get sorted by attribute rank collection of customer attributes.
     * Not all customers attributes can be filled out new attributes can
     * be added, so the result list contains filled values and
     * possible values to fill.
     *
     * @param customer customer
     * @return sorted by attribute.
     */
    List<AttrValueCustomer> getRankedAttributeValues(Customer customer);

    /**
     * Add new attribute to customer. If attribute already exists, his value will be changed.
     * This method not perform any actions to persist changes.  Blank value will not be added
     * @param customer given customer
     * @param attributeCode given attribute code
     * @param attributeValue given attribute value
     */
    void addAttribute(Customer customer, String attributeCode, String attributeValue);


}
