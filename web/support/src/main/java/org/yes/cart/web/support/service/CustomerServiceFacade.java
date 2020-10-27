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

package org.yes.cart.web.support.service;

import org.yes.cart.domain.entity.AttrValueWithAttribute;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.domain.entity.Shop;
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
     * Check if this login is already registered.
     *
     * @param shop shop
     * @param login login
     *
     * @return true if there is already an account with this login
     */
    boolean isCustomerRegistered(Shop shop, String login);

    /**
     * Register new account for given data in given shop.
     *
     * @param registrationShop shop where registration takes place
     * @param registrationData registration data
     *
     * @return password to login user
     */
    RegistrationResult registerCustomer(Shop registrationShop,
                                        Map<String, Object> registrationData);

    /**
     * Register new account for given data in given shop.
     *
     * @param registrationShop shop where registration takes place
     * @param registrationData registration data
     *
     * @return guest login ID
     */
    RegistrationResult registerGuest(Shop registrationShop,
                                     Map<String, Object> registrationData);

    /**
     * Register given data in given shop for newsletter list.
     *
     * @param registrationShop shop where registration takes place
     * @param registrationData registration data
     *
     * @return email
     */
    RegistrationResult registerNewsletter(Shop registrationShop,
                                          Map<String, Object> registrationData);


    /**
     * Notify customer that managed list was created.
     *
     * @param shop             shop where managed list was created
     * @param listData         list data
     *
     * @return email
     */
    String notifyManagedListCreated(Shop shop,
                                    Map<String, Object> listData);

    /**
     * Notify manager that managed list was rejected.
     *
     * @param shop             shop where managed list was created
     * @param listData         list data
     *
     * @return email
     */
    String notifyManagedListRejected(Shop shop,
                                     Map<String, Object> listData);


    /**
     * Contact us request in given shop.
     *
     * @param registrationShop shop where registration takes place
     * @param registrationData registration data
     *
     * @return sign up login ID
     */
    String contactUsEmailRequest(Shop registrationShop,
                                 Map<String, Object> registrationData);

    /**
     * Find customer by login.
     *
     * @param shop shop
     * @param login login
     *
     * @return customer object or null
     */
    Customer getCustomerByLogin(Shop shop, String login);


    /**
     * Get customer by login.
     *
     * @param login login
     *
     * @return {@link Customer} or null if customer not found
     */
    Customer findCustomerByLogin(Shop shop, String login, boolean includeDisabled);


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
     * Find customer wish list by login.
     *
     * @param shop shop
     * @param type wish list items type (optional)
     * @param login customer login
     * @param visibility visibility (optional)
     * @param tags tags (optional)
     *
     * @return wish list for customer that contains items of specified type with specified tags
     */
    List<CustomerWishList> getCustomerWishList(Shop shop, String type, String login, String visibility, String... tags);

    /**
     * Reset password to given user and send generated password via email.
     *
     * @param shop     shop to render email
     * @param customer customer to create
     */
    void resetPassword(Shop shop, Customer customer);

    /**
     * Initiate account removal process and send email.
     *
     * @param shop     shop to render email
     * @param customer customer to create
     */
    void deleteAccount(Shop shop, Customer customer);

    /**
     * Activate account for a particular shop
     *
     * @param shop     shop to assign
     * @param customer customer to create
     */
    void updateActivate(Shop shop, Customer customer);


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
     * @return flag to determine if guest checkout is supported
     */
    boolean isShopGuestCheckoutSupported(Shop shop);

    /**
     * Check to see if customer type is supported.
     *
     * @param shop shop
     * @param customerType type code
     *
     * @return flag to determine if guest checkout is supported
     */
    boolean isShopCustomerTypeSupported(Shop shop, String customerType);

    /**
     * Special setting for shop specific email validation.
     * This is a synonym for {@link #getShopRegistrationAttributes(Shop, String)}
     * where customerType is "EMAIL".
     *
     * @param shop shop
     *
     * @return email attribute setting or null
     */
    AttrValueWithAttribute getShopEmailAttribute(Shop shop);

    /**
     * List of custom attributes eligible for profile edit form.
     *
     * Depends on the Shop configuration for customer type specific profile settings.
     * See:
     * {@link org.yes.cart.constants.AttributeNamesKeys.Shop#CUSTOMER_REGISTRATION_ATTRIBUTES_PREFIX}
     *
     * @param shop shop
     * @param customerType customer type {@link Customer#getCustomerType()}
     *
     * @return list of eligible attributes
     */
    List<AttrValueWithAttribute> getShopRegistrationAttributes(Shop shop, String customerType);

    /**
     * List of custom attributes eligible for profile edit form.
     *
     * Depends on the Shop configuration for customer type specific profile settings.
     * See:
     * {@link org.yes.cart.constants.AttributeNamesKeys.Shop#CUSTOMER_REGISTRATION_ATTRIBUTES_PREFIX}
     *
     * @param shop shop
     * @param customerType customer type {@link Customer#getCustomerType()}
     * @param force do not perform supports check if force is true
     *
     * @return list of eligible attributes
     */
    List<AttrValueWithAttribute> getShopRegistrationAttributes(Shop shop, String customerType, boolean force);


    /**
     * List of custom attributes eligible for profile edit form.
     *
     * Depends on the Shop configuration for customer type specific profile settings.
     * See:
     * {@link org.yes.cart.constants.AttributeNamesKeys.Shop#CUSTOMER_PROFILE_ATTRIBUTES_READONLY_PREFIX}
     * {@link org.yes.cart.constants.AttributeNamesKeys.Shop#CUSTOMER_PROFILE_ATTRIBUTES_VISIBLE_PREFIX}
     *
     * @param shop shop
     * @param customer customer
     *
     * @return list of eligible attributes (pair: 1) attribute, 2) read only flag)
     */
    List<Pair<AttrValueWithAttribute, Boolean>> getCustomerProfileAttributes(Shop shop, Customer customer);

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
     * @param profileShop current shop where profile is held
     * @param customer customer
     *
     * @return customer public key
     */
    String getCustomerPublicKey(Shop profileShop, Customer customer);

    /**
     * Find customer by public key as generated by {@link #getCustomerPublicKey(Shop, Customer)}.
     *
     * @param profileShop current shop where profile is held
     * @param publicKey public key
     *
     * @return customer object or null
     */
    Customer getCustomerByPublicKey(Shop profileShop, String publicKey);

    /**
     * Get customer by auth token.
     *
     * @param profileShop current shop where profile is held
     * @param token auth token
     *
     * @return {@link Customer} or null if customer not found
     */
    Customer getCustomerByToken(Shop profileShop, String token);


    /**
     * Registration result.
     */
    interface RegistrationResult {

        /**
         * @return true if it is an attempt at duplicate registration
         */
        boolean isDuplicate();

        /**
         * @return true if registration is successful (returns fals if duplicate)
         */
        boolean isSuccess();

        /**
         * @return newly created customer object
         */
        Customer getCustomer();

        /**
         * @return raw password for auto login the first time after the registration
         */
        String getRawPassword();

        /**
         * @return error code if unsuccessful
         */
        String getErrorCode();

    }


}
