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

package org.yes.cart.web.support.service;

import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.shoppingcart.ShoppingCart;

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
     * @param shop shop
     * @param email email
     *
     * @return true if there is already an account with this email
     */
    boolean isCustomerRegistered(Shop shop, String email);

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
     * Register new account for given email in given shop.
     *
     * @param registrationShop shop where registration takes place
     * @param email            customer email
     * @param registrationData registration data
     *
     * @return guest user email hash
     */
    String registerGuest(Shop registrationShop,
                         String email,
                         Map<String, Object> registrationData);

    /**
     * Register given email in given shop for newsletter list.
     *
     * @param registrationShop shop where registration takes place
     * @param email            customer email
     * @param registrationData registration data
     *
     * @return email
     */
    String registerNewsletter(Shop registrationShop,
                              String email,
                              Map<String, Object> registrationData);

    /**
     * Register request via email in given shop.
     *
     * @param registrationShop shop where registration takes place
     * @param email            customer email
     * @param registrationData registration data
     *
     * @return email
     */
    String registerEmailRequest(Shop registrationShop,
                                String email,
                                Map<String, Object> registrationData);

    /**
     * Find customer by email.
     *
     * @param shop shop
     * @param email email
     *
     * @return customer object or null
     */
    Customer getCustomerByEmail(Shop shop, String email);

    /**
     * Find guest customer by cart.
     *
     * @param shop shop
     * @param cart shopping cart
     *
     * @return customer object or null
     */
    Customer getGuestByCart(Shop shop, ShoppingCart cart);

    /**
     * Get customer object for current checkout process. This could be either guest or registered customer.
     *
     * @param shop shop
     * @param cart shopping cart
     *
     * @return customer object or null
     */
    Customer getCheckoutCustomer(Shop shop, ShoppingCart cart);

    /**
     * Find customer wish list by email.
     *
     * @param shop shop
     * @param type wish list items type (optional)
     * @param email customer email
     * @param visibility visibility (optional)
     * @param tags tags (optional)
     *
     * @return wish list for customer that contains items of specified type with specified tags
     */
    List<CustomerWishList> getCustomerWishListByEmail(Shop shop, String type, String email, String visibility, String... tags);

    /**
     * Reset password to given user and send generated password via email.
     *
     * @param shop     shop to render email
     * @param customer customer to create
     */
    void resetPassword(Shop shop, Customer customer);

    /**
     * List of supported customer types.
     *
     * @param shop shop
     *
     * @return supported customer types
     */
    List<Pair<String, I18NModel>> getShopSupportedCustomerTypes(Shop shop);

    /**
     * Check to see if guest checkout is supported.
     *
     * @param shop shop
     *
     * @return flag to determine if gues checkout is supported
     */
    boolean isShopGuestCheckoutSupported(Shop shop);

    /**
     * Check to see if customer type is supported.
     *
     * @param shop shop
     * @param customerType type code
     *
     * @return flag to determine if gues checkout is supported
     */
    boolean isShopCustomerTypeSupported(Shop shop, String customerType);

    /**
     * List of custom attributes eligible for profile edit form.
     * CPOINT - This will be available to the shoppers to edit as they please, so
     * need to restrict attributes that should be hidden from shoppers.
     *
     * @param shop shop
     * @param customerType customer type {@link Customer#getCustomerType()}
     *
     * @return list of eligible attributes
     */
    List<AttrValueCustomer> getShopRegistrationAttributes(Shop shop, String customerType);


    /**
     * List of custom attributes eligible for profile edit form.
     * CPOINT - This will be available to the shoppers to edit as they please, so
     * need to restrict attributes that should be hidden from shoppers.
     *
     *
     * @param shop shop
     * @param customer customer
     *
     * @return list of eligible attributes (pair: 1) attribute, 2) read only flag)
     */
    List<Pair<AttrValueCustomer, Boolean>> getCustomerProfileAttributes(Shop shop, Customer customer);

    /**
     * Update customer entry.
     *
     * @param shop shop
     * @param customer customer
     */
    void updateCustomer(Shop shop, Customer customer);

    /**
     * Update customer entry.
     *
     * @param profileShop current shop where profile is being updated
     * @param customer customer
     * @param values attribute values
     */
    void updateCustomerAttributes(Shop profileShop, Customer customer, Map<String, String> values);


    /**
     * Get customer public key information. Default format is PUBLICKEY-LASTNAME.
     *
     * @param customer customer
     *
     * @return customer public key
     */
    String getCustomerPublicKey(final Customer customer);

    /**
     * Find customer by public key as generated by {@link #getCustomerPublicKey(Customer)}.
     *
     * @param publicKey public key
     *
     * @return customer object or null
     */
    Customer getCustomerByPublicKey(String publicKey);


}
