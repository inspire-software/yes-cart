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

import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Country;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.State;

import java.util.List;

/**
 * User: denispavlov
 * Date: 13-10-15
 * Time: 12:22 AM
 */
public interface AddressBookFacade {

    /**
     * Get existing address or create new instance object.
     *
     * @param customer customer of the address
     * @param addrId address PK
     * @param addressType type of address
     *
     * @return address instance
     */
    Address getAddress(Customer customer, String addrId, String addressType);

    /**
     * Create or update address object.
     *
     * @param address address
     */
    void createOrUpdate(Address address);

    /**
     * Find all countries.
     *
     * @return all countries
     */
    List<Country> findAllCountries(String shopCode);

    /**
     * Find by country code.
     *
     * @param countryCode country code.
     * @return list of states , that belong to given country.
     */
    List<State> findStatesByCountry(String countryCode);

}
