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

package org.yes.cart.service.domain.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.AddressService;
import org.yes.cart.service.order.OrderAddressFormatter;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AddressServiceImpl extends BaseGenericServiceImpl<Address> implements AddressService {

    private final OrderAddressFormatter orderAddressFormatter;

    /**
     * Construct service.
     * @param genericDao dao  to use.
     * @param orderAddressFormatter address formatter
     */
    public AddressServiceImpl(final GenericDAO<Address, Long> genericDao,
                              final OrderAddressFormatter orderAddressFormatter) {
        super(genericDao);
        this.orderAddressFormatter = orderAddressFormatter;
    }

    /** {@inheritDoc} */
    public List<Address> getAddressesByCustomerId(final long customerId) {
        return getGenericDao().findByNamedQuery("ADDRESSES.BY.CUSTOMER", customerId);
    }

    /** {@inheritDoc} */
    public List<Address> getAddressesByCustomerId(final long customerId, final String addressType) {
        return getGenericDao().findByNamedQuery("ADDRESSES.BY.CUSTOMER.TYPE", customerId, addressType);
    }


    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = true)
    public Address create(final Address instance) {
        setDefault(instance);
        return super.create(instance);
    }


    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = true)
    public Address updateSetDefault(final Address instance) {
        setDefault(instance);
        return super.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = true)
    public Address update(final Address instance) {
        return super.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = true)
    public void delete(final Address instance) {
        super.delete(instance);
    }

    private void setDefault(final Address instance) {
        if (instance.getCustomer() != null) {
            getGenericDao().executeUpdate(
                    "ADDRESSES.RESET.DEFAULT",
                    instance.getCustomer().getCustomerId(), instance.getAddressType(), false
            );
            instance.setDefaultAddress(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String formatAddressFor(final Address address, final Shop shop, final Customer customer, final String lang) {

        final String customerType = customer != null ? customer.getCustomerType() : null;

        if (shop != null && address != null) {
            final String addressType = address.getAddressType();
            final String format = shop.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType(address.getCountryCode(), lang, customerType, addressType);
            return orderAddressFormatter.formatAddress(address, format);
        }
        return orderAddressFormatter.formatAddress(address);

    }
}
