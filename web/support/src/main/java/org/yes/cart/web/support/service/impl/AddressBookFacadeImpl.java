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

package org.yes.cart.web.support.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.AddressCustomisationSupport;
import org.yes.cart.service.domain.AddressService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.web.support.service.AddressBookFacade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13-10-15
 * Time: 12:31 AM
 */
public class AddressBookFacadeImpl implements AddressBookFacade {

    private final CustomerService customerService;
    private final AddressService addressService;
    private final AddressCustomisationSupport addressCustomisationSupport;

    public AddressBookFacadeImpl(final CustomerService customerService,
                                 final AddressService addressService,
                                 final AddressCustomisationSupport addressCustomisationSupport) {
        this.customerService = customerService;
        this.addressService = addressService;
        this.addressCustomisationSupport = addressCustomisationSupport;
    }

    /** {@inheritDoc} */
    @Override
    public boolean customerHasAtLeastOneAddress(final String email, final Shop customerShop) {

        if (StringUtils.isNotBlank(email)) {

            final Customer customer = customerService.getCustomerByEmail(email, customerShop);
            if (customer != null) {
                return
                        !getAddresses(customer, customerShop, Address.ADDR_TYPE_BILLING).isEmpty() ||
                        !getAddresses(customer, customerShop, Address.ADDR_TYPE_SHIPPING).isEmpty();
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean customerHasAtLeastOneAddress(final String email, final Shop customerShop, final String type) {

        if (StringUtils.isNotBlank(email)) {

            final Customer customer = customerService.getCustomerByEmail(email, customerShop);
            if (customer != null) {
                return !getAddresses(customer, customerShop, type).isEmpty();
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public List<Address> getAddresses(final Customer customer, final Shop customerShop, final String addressType) {

        final List<Address> allowed = new ArrayList<>();
        if (customer != null) {
            final Shop configShop = customerShop.getMaster() != null ? customerShop.getMaster() : customerShop;
            final Collection<Address> allAvailable = getAddressbook(customerShop, customer, addressType);
            final List<String> allowedCountries = Address.ADDR_TYPE_BILLING.equals(addressType) ?
                    configShop.getSupportedBillingCountriesAsList() : configShop.getSupportedShippingCountriesAsList();

            for (final Address address : allAvailable) {
                if (allowedCountries.contains(address.getCountryCode())) {
                    allowed.add(address);
                }
            }
        }
        return allowed;
    }

    /** {@inheritDoc} */
    @Override
    public Address getAddress(final Customer customer, final Shop customerShop, final String addrId, final String addressType) {
        long pk;
        try {
            pk = NumberUtils.toLong(addrId);
        } catch (NumberFormatException nfe) {
            pk = 0;
        }
        Address rez = null;
        for (Address addr : getAddressbook(customerShop, customer)) {
            if (addr.getAddressId() == pk) {
                rez = addr;
                break;
            }
        }
        if (rez == null) {
            rez = customerService.getGenericDao().getEntityFactory().getByIface(Address.class);
            rez.setCustomer(customer);
            rez.setAddressType(Address.ADDR_TYPE_BILLING.equals(addressType) ? Address.ADDR_TYPE_BILLING : Address.ADDR_TYPE_SHIPPING);
            // customer.getAddress().add(rez); Must do this when we create address only!

            final AttrValueCustomer attrValue = customer.getAttributeByCode(AttributeNamesKeys.Customer.CUSTOMER_PHONE);
            rez.setSalutation(customer.getSalutation());
            rez.setFirstname(customer.getFirstname());
            rez.setMiddlename(customer.getMiddlename());
            rez.setLastname(customer.getLastname());
            rez.setCompanyName1(customer.getCompanyName1());
            rez.setCompanyName2(customer.getCompanyName2());
            rez.setCompanyDepartment(customer.getCompanyDepartment());
            rez.setEmail1(customer.getContactEmail());
            rez.setPhone1(attrValue == null ? StringUtils.EMPTY : attrValue.getVal());
        }
        return rez;
    }

    /**
     * Get existing address default address.
     *
     * @param customer customer of the address
     * @param customerShop shop
     * @param addressType type of address
     * @return address instance
     */
    @Override
    public Address getDefaultAddress(final Customer customer, final Shop customerShop, final String addressType) {
        if (customer == null || customerShop == null) {
            return null;
        }
        return customer.getDefaultAddress(addressType);
    }

    /**
     * Get customer address book for this shop
     *
     * @param customerShop shop
     * @param customer customer
     *
     * @return addresses
     */
    protected Collection<Address> getAddressbook(final Shop customerShop, final Customer customer) {
        return customer != null ? customer.getAddress() : Collections.emptyList();
    }

    /**
     * Get customer address book for this shop
     *
     * @param customerShop shop
     * @param customer customer
     *
     * @return addresses
     */
    protected Collection<Address> getAddressbook(final Shop customerShop, final Customer customer, final String type) {
        return customer != null ? customer.getAddresses(type) : Collections.emptyList();
    }


    /** {@inheritDoc} */
    @Override
    public Address copyAddress(final Customer customer, final Shop customerShop, final String addrId, final String addressType) {

        final Address original = getAddress(customer, customerShop, addrId, addressType);

        if (original.getAddressId() > 0L) {

            Address rez = customerService.getGenericDao().getEntityFactory().getByIface(Address.class);
            rez.setCustomer(customer);
            // customer.getAddress().add(rez); Must do this when we create address only!

            copyAddressDetails(original, rez);
            rez.setAddressType(addressType);

            return rez;

        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void copyAddressDetails(final Address from, final Address to) {

        to.setName(from.getName());
        to.setAddressType(from.getAddressType());
        to.setSalutation(from.getSalutation());
        to.setFirstname(from.getFirstname());
        to.setMiddlename(from.getMiddlename());
        to.setLastname(from.getLastname());
        to.setPhone1(from.getPhone1());
        to.setPhone2(from.getPhone2());
        to.setMobile1(from.getMobile1());
        to.setMobile2(from.getMobile2());
        to.setEmail1(from.getEmail1());
        to.setEmail2(from.getEmail2());
        to.setCity(from.getCity());
        to.setPostcode(from.getPostcode());
        to.setAddrline1(from.getAddrline1());
        to.setAddrline2(from.getAddrline2());
        to.setCountryCode(from.getCountryCode());
        to.setStateCode(from.getStateCode());
        to.setCompanyName1(from.getCompanyName1());
        to.setCompanyName2(from.getCompanyName2());
        to.setCompanyDepartment(from.getCompanyDepartment());
        to.setCustom0(from.getCustom0());
        to.setCustom1(from.getCustom1());
        to.setCustom2(from.getCustom2());
        to.setCustom3(from.getCustom3());
        to.setCustom4(from.getCustom4());
        to.setCustom5(from.getCustom5());
        to.setCustom6(from.getCustom6());
        to.setCustom7(from.getCustom7());
        to.setCustom8(from.getCustom8());
        to.setCustom9(from.getCustom9());

    }

    /** {@inheritDoc} */
    @Override
    public List<AttrValueWithAttribute> getShopCustomerAddressAttributes(final Customer customer, final Shop shop, final String addressType) {

        return addressCustomisationSupport.getShopCustomerAddressAttributes(customer, shop, addressType);

    }

    /** {@inheritDoc} */
    @Override
    public List<Country> getAllCountries(final String shopCode, final String addressType) {

        return addressCustomisationSupport.getAllCountries(shopCode, addressType);

    }

    /** {@inheritDoc} */
    @Override
    public List<State> getStatesByCountry(final String countryCode) {

        return addressCustomisationSupport.getStatesByCountry(countryCode);

    }

    /** {@inheritDoc} */
    @Override
    public void createOrUpdate(final Address address, final Shop customerShop) {
        if (!customerShop.isB2BAddressBookActive() &&
                customerShop.isSfAddressBookEnabled(address.getCustomer().getCustomerType()) &&
                    (!Address.ADDR_TYPE_BILLING.equals(address.getAddressType()) ||
                            customerShop.isSfAddressBookBillingEnabled(address.getCustomer().getCustomerType()))) {
            if (address.getAddressId() == 0) {
                // Need to add address to customer only just before the creation
                address.getCustomer().getAddress().add(address);
                addressService.create(address);
            } else {
                addressService.update(address);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void remove(Address address, final Shop customerShop) {

        if (!customerShop.isB2BAddressBookActive() &&
                customerShop.isSfAddressBookEnabled(address.getCustomer().getCustomerType()) &&
                (!Address.ADDR_TYPE_BILLING.equals(address.getAddressType()) ||
                        customerShop.isSfAddressBookBillingEnabled(address.getCustomer().getCustomerType()))) {
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
    }

    /** {@inheritDoc} */
    @Override
    public Address useAsDefault(Address address, final Shop customerShop) {
        if (!customerShop.isB2BAddressBookActive()) {
            return addressService.updateSetDefault(address);
        }
        return address;
    }

    /** {@inheritDoc} */
    @Override
    public String formatAddressFor(final Address address, final Shop shop, final Customer customer, final String lang) {

        return addressCustomisationSupport.formatAddressFor(address, shop, customer, lang);

    }
}
