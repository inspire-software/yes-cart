package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Address;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface AddressService  extends GenericService<Address>{

    /**
     * Get customer addresses.
     * @param customerId customer id
     * @return list of addresses
     */
    List<Address> getAddressesByCustomerId(long customerId);

    /**
     * Get customer addresses filtered by requested type .
     * @param customerId customer id
     * @param addressType address type
     * @return list of addresses
     */
    List<Address> getAddressesByCustomerId(long customerId, String addressType);


    /**
     * Set given address as default inside address type group.
     * @param instance instance to update
     * @return persisted instance of address.
     */
    Address updateSetDefault(Address instance);


}
