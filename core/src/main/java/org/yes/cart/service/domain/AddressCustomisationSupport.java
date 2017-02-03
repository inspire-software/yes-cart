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

import org.yes.cart.domain.entity.*;

import java.util.List;

/**
 * User: denispavlov
 * Date: 31/01/2017
 * Time: 12:20
 */
public interface AddressCustomisationSupport {

    /**
     * Get address form attributes to be displayed for current customer.
     *
     * @param customer customer
     * @param shop shop
     * @param addressType address type
     *
     * @return attributes that describe suitable address form
     */
    List<AttrValue> getShopCustomerAddressAttributes(Customer customer, Shop shop, String addressType);

    /**
     * Find all countries.
     *
     * @param shopCode shop code
     * @param addressType address type
     *
     * @return all countries
     */
    List<Country> getAllCountries(String shopCode, String addressType);

    /**
     * Find by country code.
     *
     * @param countryCode country code.
     * @return list of states , that belong to given country.
     */
    List<State> getStatesByCountry(String countryCode);

    /**
     * Format address as specified by shop address formatting.
     *
     * @param address address to format
     * @param shop shop
     * @param customer customer
     * @param lang language
     *
     * @return string representation of this address
     */
    String formatAddressFor(Address address, Shop shop, Customer customer, String lang);

}
