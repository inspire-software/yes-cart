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
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.dao.ResultsIteratorCallback;
import org.yes.cart.domain.entity.AttrValueCustomer;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CustomerService;

import java.time.Instant;
import java.util.List;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 16:34
 */
public class CustomerServiceCachedImpl implements CustomerService {

    private final CustomerService customerService;

    public CustomerServiceCachedImpl(final CustomerService customerService) {
        this.customerService = customerService;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "customerService-customerByEmail", condition = "#shop != null", key = "#email + #shop.code")
    public Customer getCustomerByEmail(final String email, final Shop shop) {
        return customerService.getCustomerByEmail(email, shop);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer getCustomerByToken(final String token) {
        return customerService.getCustomerByToken(token);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer getCustomerByPublicKey(final String publicKey, final String lastName) {
        return customerService.getCustomerByPublicKey(publicKey, lastName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Shop> getCustomerShops(final Customer customer) {
        return customerService.getCustomerShops(customer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatNameFor(final Customer customer, final Shop shop) {
        return customerService.formatNameFor(customer, shop);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> findCustomer(final String email,
                                       final String firstname,
                                       final String lastname,
                                       final String middlename,
                                       final String tag,
                                       final String customerType,
                                       final String pricingPolicy) {
        return customerService.findCustomer(email, firstname, lastname, middlename, tag, customerType, pricingPolicy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCustomerExists(final String email, final Shop shop) {
        return customerService.isCustomerExists(email, shop);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPasswordValid(final String email, final Shop shop, final String password) {
        return customerService.isPasswordValid(email, shop, password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AttrValueCustomer> getRankedAttributeValues(final Customer customer) {
        return customerService.getRankedAttributeValues(customer);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = false, condition = "#shop != null", key = "#customer.email + #shop.code")
    public void resetPassword(final Customer customer, final Shop shop, final String authToken) {
        customerService.resetPassword(customer, shop, authToken);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = false, condition = "#shop != null", key = "#customer.email + #shop.code")
    public Customer create(final Customer customer, final Shop shop) {
        return customerService.create(customer, shop);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = false, condition = "#shop != null", key = "#customer.email + #shop.code")
    public Customer updateActivate(final Customer customer, final Shop shop, final boolean soft) {
        return customerService.updateActivate(customer, shop, soft);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = false, condition = "#shop != null", key = "#customer.email + #shop.code")
    public Customer updateDeactivate(final Customer customer, final Shop shop, final boolean soft) {
        return customerService.updateDeactivate(customer, shop, soft);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer create(final Customer instance) {
        return customerService.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = false, key = "#email + #shopCode")
    public Customer update(final String email, final String shopCode) {
        return customerService.update(email, shopCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = true)
    public Customer update(final Customer instance) {
        return customerService.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = true)
    public void delete(final Customer instance) {
        customerService.delete(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultsIterator<Customer> findGuestsBefore(final Instant date) {
        return customerService.findGuestsBefore(date);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> findAll() {
        return customerService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<Customer> callback) {
        customerService.findAllIterator(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer findById(final long pk) {
        return customerService.findById(pk);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> findByCriteria(final String eCriteria, final Object... parameters) {
        return customerService.findByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return customerService.findCountByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return customerService.findSingleByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericDAO<Customer, Long> getGenericDao() {
        return customerService.getGenericDao();
    }
}
