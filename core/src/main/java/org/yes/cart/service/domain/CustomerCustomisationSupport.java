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

import org.yes.cart.domain.entity.AttrValueCustomer;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;

import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 31/01/2017
 * Time: 12:39
 */
public interface CustomerCustomisationSupport {

    /**
     * Return guest customer type.
     *
     * @param shop shop
     *
     * @return guest type
     */
    String getGuestCustomerType(Shop shop);

    /**
     * Return customer types.
     *
     * @param shop shop
     * @param includeGuest include guest type
     *
     * @return types
     */
    Set<String> getCustomerTypes(final Shop shop, final boolean includeGuest);

    /**
     * List of supported customer types.
     *
     * @param shop shop
     *
     * @return supported customer types
     */
    List<Pair<String, I18NModel>> getSupportedCustomerTypes(Shop shop);

    /**
     * Check to see if guest checkout is supported.
     *
     * @param shop shop
     *
     * @return flag to determine if gues checkout is supported
     */
    boolean isGuestCheckoutSupported(Shop shop);

    /**
     * Check to see if customer type is supported.
     *
     * @param shop shop
     * @param customerType type code
     *
     * @return flag to determine if gues checkout is supported
     */
    boolean isCustomerTypeSupported(Shop shop, String customerType);

    /**
     * Get all supported customer attributes.
     *
     * @param shop shop
     * @param customerType customer type {@link Customer#getCustomerType()}
     *
     * @return list of attribute codes.
     */
    List<String> getSupportedRegistrationFormAttributesAsList(Shop shop, String customerType);

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
    List<AttrValueCustomer> getRegistrationAttributes(Shop shop, String customerType);


    /**
     * Get all supported customer attributes.
     *
     * @param shop shop
     * @param customerType customer type {@link Customer#getCustomerType()}
     *
     * @return list of attribute codes.
     */
    List<String> getSupportedProfileFormAttributesAsList(Shop shop, String customerType);


    /**
     * Get all supported customer attributes.
     *
     * @param shop shop
     * @param customerType customer type {@link Customer#getCustomerType()}
     *
     * @return list of attribute codes.
     */
    List<String> getSupportedProfileFormReadOnlyAttributesAsList(Shop shop, String customerType);


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
    List<Pair<AttrValueCustomer, Boolean>> getProfileAttributes(Shop shop, Customer customer);

}
