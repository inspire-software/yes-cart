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

import org.hibernate.criterion.Criterion;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.entity.AttrValueCustomer;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CustomerService;

import java.util.Date;
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
    @Cacheable(value = "customerService-customerByEmail", condition = "#shop != null", key = "#email + #shop.code")
    public Customer getCustomerByEmail(final String email, final Shop shop) {
        return customerService.getCustomerByEmail(email, shop);
    }

    /**
     * {@inheritDoc}
     */
    public Customer getCustomerByToken(final String token) {
        return customerService.getCustomerByToken(token);
    }

    /**
     * {@inheritDoc}
     */
    public Customer getCustomerByPublicKey(final String publicKey, final String lastName) {
        return customerService.getCustomerByPublicKey(publicKey, lastName);
    }

    /**
     * {@inheritDoc}
     */
    public List<Shop> getCustomerShops(final Customer customer) {
        return customerService.getCustomerShops(customer);
    }

    /**
     * {@inheritDoc}
     */
    public String formatNameFor(final Customer customer, final Shop shop) {
        return customerService.formatNameFor(customer, shop);
    }

    /**
     * {@inheritDoc}
     */
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
    public boolean isCustomerExists(final String email, final Shop shop) {
        return customerService.isCustomerExists(email, shop);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPasswordValid(final String email, final Shop shop, final String password) {
        return customerService.isPasswordValid(email, shop, password);
    }



    /**
     * Add new attribute to customer. If attribute already exists, his value will be changed.
     * This method not perform any actions to persist changes.
     *
     * @param customer       given customer
     * @param attributeCode  given attribute code
     * @param attributeValue given attribute value
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = true)
    public void addAttribute(final Customer customer, final String attributeCode, final String attributeValue) {
        customerService.addAttribute(customer, attributeCode, attributeValue);
    }

    /**
     * {@inheritDoc}
     */
    public List<AttrValueCustomer> getRankedAttributeValues(final Customer customer) {
        return customerService.getRankedAttributeValues(customer);
    }


    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = false, condition = "#shop != null", key = "#customer.email + #shop.code")
    public void resetPassword(final Customer customer, final Shop shop, final String authToken) {
        customerService.resetPassword(customer, shop, authToken);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = false, condition = "#shop != null", key = "#customer.email + #shop.code")
    public Customer create(final Customer customer, final Shop shop) {
        return customerService.create(customer, shop);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = false, condition = "#shop != null", key = "#customer.email + #shop.code")
    public Customer updateActivate(final Customer customer, final Shop shop, final boolean soft) {
        return customerService.updateActivate(customer, shop, soft);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = false, condition = "#shop != null", key = "#customer.email + #shop.code")
    public Customer updateDeactivate(final Customer customer, final Shop shop, final boolean soft) {
        return customerService.updateDeactivate(customer, shop, soft);
    }

    /**
     * {@inheritDoc}
     */
    public Customer create(final Customer instance) {
        return customerService.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = false, key = "#email + #shopCode")
    public Customer update(final String email, final String shopCode) {
        return customerService.update(email, shopCode);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = true)
    public Customer update(final Customer instance) {
        return customerService.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "customerService-customerByEmail"
    }, allEntries = true)
    public void delete(final Customer instance) {
        customerService.delete(instance);
    }

    /**
     * {@inheritDoc}
     */
    public ResultsIterator<Customer> findGuestsBefore(final Date date) {
        return customerService.findGuestsBefore(date);
    }

    /**
     * {@inheritDoc}
     */
    public List<Customer> findAll() {
        return customerService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public Customer findById(final long pk) {
        return customerService.findById(pk);
    }

    /**
     * {@inheritDoc}
     */
    public List<Customer> findByCriteria(final Criterion... criterion) {
        return customerService.findByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public int findCountByCriteria(final Criterion... criterion) {
        return customerService.findCountByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public int findCountByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return customerService.findCountByCriteria(criteriaTuner, criterion);
    }

    /**
     * {@inheritDoc}
     */
    public Customer findSingleByCriteria(final Criterion... criterion) {
        return customerService.findSingleByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public GenericDAO<Customer, Long> getGenericDao() {
        return customerService.getGenericDao();
    }
}
