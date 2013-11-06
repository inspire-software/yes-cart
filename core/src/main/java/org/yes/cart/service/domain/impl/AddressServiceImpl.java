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

package org.yes.cart.service.domain.impl;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.service.domain.AddressService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AddressServiceImpl extends BaseGenericServiceImpl<Address> implements AddressService {

    /**
     * Construct service.
     * @param genericDao dao  to use. 
     */
    public AddressServiceImpl(final GenericDAO<Address, Long> genericDao) {
        super(genericDao);
    }

    /** {@inheritDoc} */
    public boolean customerHasAtLeastOneAddress(String email) {
        final List pks = (List) getGenericDao().findQueryObjectByNamedQuery("ADDRESSES.BY.CUSTOMER.EMAIL", email);
        return CollectionUtils.isNotEmpty(pks);
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
    }, allEntries = false, key = "#instance.customer.email")
    public Address create(final Address instance) {
        setDefault(instance);
        return super.create(instance);
    }


    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = false, key = "#instance.customer.email")
    public Address updateSetDefault(final Address instance) {
        setDefault(instance);
        return super.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = false, key = "#instance.customer.email")
    public Address update(final Address instance) {
        return super.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = false, key = "#instance.customer.email")
    public void delete(final Address instance) {
        super.delete(instance);
    }

    private void setDefault(final Address instance) {
        getGenericDao().executeUpdate(
                "ADDRESSES.RESET.DEFAULT",
                instance.getCustomer().getCustomerId(), instance.getAddressType(), false
        );
        instance.setDefaultAddress(true);
    }


}
