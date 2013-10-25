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

package org.yes.cart.web.support.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.AttrValueCustomer;
import org.yes.cart.domain.entity.Country;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.State;
import org.yes.cart.service.domain.AddressService;
import org.yes.cart.service.domain.CountryService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.StateService;
import org.yes.cart.web.support.service.AddressBookFacade;

import java.util.List;

/**
 * User: denispavlov
 * Date: 13-10-15
 * Time: 12:31 AM
 */
public class AddressBookFacadeImpl implements AddressBookFacade {

    private final CustomerService customerService;
    private final AddressService addressService;
    private final CountryService countryService;
    private final StateService stateService;

    public AddressBookFacadeImpl(final CustomerService customerService,
                                 final AddressService addressService,
                                 final CountryService countryService,
                                 final StateService stateService) {
        this.customerService = customerService;
        this.addressService = addressService;
        this.countryService = countryService;
        this.stateService = stateService;
    }

    /** {@inheritDoc} */
    @Cacheable(value = "web.addressBookFacade-customerHasAtLeastOneAddress")
    public boolean customerHasAtLeastOneAddress(final String email) {

        if (StringUtils.isNotBlank(email)) {
            return addressService.customerHasAtLeastOneAddress(email);
        }
        return false;
    }

    /** {@inheritDoc} */
    public Address getAddress(final Customer customer, final String addrId, final String addressType) {
        long pk;
        try {
            pk = NumberUtils.createLong(addrId);
        } catch (NumberFormatException nfe) {
            pk = 0;
        }
        Address rez = null;
        for (Address addr : customer.getAddress()) {
            if (addr.getAddressId() == pk) {
                rez = addr;
                break;
            }
        }
        if (rez == null) {
            rez = customerService.getGenericDao().getEntityFactory().getByIface(Address.class);
            rez.setCustomer(customer);
            rez.setAddressType(addressType);
            customer.getAddress().add(rez);

            final AttrValueCustomer attrValue = customer.getAttributeByCode(AttributeNamesKeys.CUSTOMER_PHONE);
            rez.setFirstname(customer.getFirstname());
            rez.setLastname(customer.getLastname());
            rez.setPhoneList(attrValue == null ? StringUtils.EMPTY : attrValue.getVal());
        }
        return rez;
    }

    /** {@inheritDoc} */
    @Cacheable(value = "web.addressBookFacade-findAllCountries")
    public List<Country> findAllCountries(final String shopCode) {
        return countryService.findAll();
    }

    /** {@inheritDoc} */
    @Cacheable(value = "web.addressBookFacade-findStatesByCountry")
    public List<State> findStatesByCountry(final String countryCode) {
        return stateService.findByCountry(countryCode);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
        "web.addressBookFacade-customerHasAtLeastOneAddress"
    }, allEntries = true)
    public void createOrUpdate(final Address address) {
        if (address.getAddressId() == 0) {
            addressService.create(address);
        } else {
            addressService.update(address);
        }
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "web.addressBookFacade-customerHasAtLeastOneAddress"
    }, allEntries = true)
    public void remove(Address address) {

        final boolean isDefault = address.isDefaultAddress();
        addressService.delete(address);

        if (isDefault) {
            // set new default address in case if default address was deleted
            final List<Address> restOfAddresses = addressService.getAddressesByCustomerId(
                    address.getCustomer().getCustomerId(),
                    address.getAddressType());
            if (!restOfAddresses.isEmpty()) {
                addressService.updateSetDefault(restOfAddresses.get(0));
            }
        }

    }

    /** {@inheritDoc} */
    public Address useAsDefault(Address address) {
        return addressService.updateSetDefault(address);
    }

}
