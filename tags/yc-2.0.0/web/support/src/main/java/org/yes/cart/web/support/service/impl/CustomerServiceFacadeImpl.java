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

import org.apache.commons.collections.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueCustomer;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.PassPhrazeGenerator;
import org.yes.cart.web.support.service.CustomerServiceFacade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-10-25
 * Time: 7:03 PM
 */
public class CustomerServiceFacadeImpl implements CustomerServiceFacade {

    private final CustomerService customerService;
    private final AttributeService attributeService;
    private final PassPhrazeGenerator phrazeGenerator;

    public CustomerServiceFacadeImpl(final CustomerService customerService,
                                     final AttributeService attributeService,
                                     final PassPhrazeGenerator phrazeGenerator) {
        this.customerService = customerService;
        this.attributeService = attributeService;
        this.phrazeGenerator = phrazeGenerator;
    }

    /** {@inheritDoc} */
    public boolean isCustomerRegistered(String email) {
        return customerService.isCustomerExists(email);
    }

    /** {@inheritDoc} */
    public Customer getCustomerByEmail(final String email) {
        return customerService.getCustomerByEmail(email);
    }

    /** {@inheritDoc} */
    public void resetPassword(final Shop shop, final Customer customer) {
        customerService.resetPassword(customer, shop);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "web.addressBookFacade-customerHasAtLeastOneAddress"
    }, key = "#email")
    public String registerCustomer(Shop registrationShop, String email, Map<String, Object> registrationData) {

        final String password = phrazeGenerator.getNextPassPhrase();

        final Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);

        customer.setEmail(email);
        customer.setFirstname((String) registrationData.get("firstname"));
        customer.setLastname((String) registrationData.get("lastname"));
        customer.setPassword(password); // aspect will create hash

        final AttrValueCustomer attrValueCustomer = customerService.getGenericDao().getEntityFactory().getByIface(AttrValueCustomer.class);
        attrValueCustomer.setCustomer(customer);
        attrValueCustomer.setVal((String) registrationData.get("phone"));
        attrValueCustomer.setAttribute(attributeService.findByAttributeCode(AttributeNamesKeys.CUSTOMER_PHONE));

        customer.getAttributes().add(attrValueCustomer);

        customerService.create(customer, registrationShop);

        return password;
    }

    /** {@inheritDoc} */
    public List<? extends AttrValue> getCustomerRegistrationAttributes(final Customer customer) {

        final List<? extends AttrValue> attrValueCollection = customerService.getRankedAttributeValues(customer);
        if (CollectionUtils.isEmpty(attrValueCollection)) {
            return Collections.emptyList();
        }

        return new ArrayList<AttrValue>(attrValueCollection);  // CPOINT - possibly need to filter some out
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "web.addressBookFacade-customerHasAtLeastOneAddress"
    }, key = "#customer.email")
    public void updateCustomer(final Customer customer) {
        customerService.update(customer);
    }

    /** {@inheritDoc} */
    public boolean authenticate(final String username, final String password) {
        return customerService.isCustomerExists(username) &&
                customerService.isPasswordValid(username, password);
    }
}
