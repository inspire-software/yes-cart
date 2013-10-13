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

import org.yes.cart.domain.entity.Address;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface AddressService extends GenericService<Address> {

    /**
     * Get customer addresses.
     *
     * @param customerId customer id
     * @return list of addresses
     */
    List<Address> getAddressesByCustomerId(long customerId);

    /**
     * Get customer addresses filtered by requested type .
     *
     * @param customerId  customer id
     * @param addressType address type
     * @return list of addresses
     */
    List<Address> getAddressesByCustomerId(long customerId, String addressType);


    /**
     * Set given address as default inside address type group.
     *
     * @param instance instance to update
     * @return persisted instance of address.
     */
    Address updateSetDefault(Address instance);


}
