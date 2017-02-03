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

import java.util.List;

/**
 * User: denispavlov
 * Date: 13-10-15
 * Time: 12:22 AM
 */
public interface AddressBookFacade {

    /**
     * Returns true if customer identified by given email address
     * has at least one address in the address book.
     *
     * @param email customer email
     * @param customerShop shop
     *
     * @return true if at least one address exists
     */
    boolean customerHasAtLeastOneAddress(String email, Shop customerShop);


    /**
     * Get addresses applicable for given shop.
     *
     * @param customer customer
     * @param customerShop shop
     * @param addressType address type
     * @return list of applicable addresses
     */
    List<Address> getAddresses(Customer customer, Shop customerShop, String addressType);

    /**
     * Get existing address or create new instance object.
     *
     * @param customer customer of the address
     * @param customerShop shop
     * @param addrId address PK
     * @param addressType type of address
     * @return address instance
     */
    Address getAddress(Customer customer, Shop customerShop, String addrId, String addressType);


    /**
     * Get existing address default address.
     *
     * @param customer customer of the address
     * @param customerShop shop
     * @param addressType type of address
     * @return address instance
     */
    Address getDefaultAddress(Customer customer, Shop customerShop, String addressType);


    /**
     * Create a copy of the existing address.
     *
     * @param customer customer of the address
     * @param customerShop shop
     * @param addrId address PK
     * @param addressType type of address
     * @return new address instance with copy of original data (or null if not found)
     */
    Address copyAddress(Customer customer, Shop customerShop, String addrId, String addressType);

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
     * Create or update address object.
     *
     * @param address address
     * @param customerShop shop
     */
    void createOrUpdate(Address address, Shop customerShop);

    /**
     * Removes address and resets default address.
     *
     * @param address address to remove
     * @param customerShop shop
     */
    void remove(Address address, Shop customerShop);

    /**
     * Set given address as default inside address type group.
     *
     * @param address instance to update
     * @param customerShop shop
     *
     * @return persisted instance of address.
     */
    Address useAsDefault(Address address, Shop customerShop);

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
