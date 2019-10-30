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

package org.yes.cart.shoppingcart.impl;

import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CustomerManagerService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.shoppingcart.CustomerResolver;

import java.util.List;

/**
 * User: denispavlov
 * Date: 26/10/2019
 * Time: 12:10
 */
public class CustomerResolverDefaultImpl implements CustomerResolver {

    private final CustomerService customerService;
    private final CustomerManagerService customerManagerService;

    public CustomerResolverDefaultImpl(final CustomerService customerService,
                                       final CustomerManagerService customerManagerService) {
        this.customerService = customerService;
        this.customerManagerService = customerManagerService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isManagerLoginEnabled(final Shop shop) {
        return customerManagerService.isCustomerManagerLoginEnabled(shop);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer getCustomerByEmail(final String email, final Shop shop) {
        final Customer customer = customerService.getCustomerByEmail(email, shop);
        if (customer != null) {
            return customer;
        }

        if (customerManagerService.isCustomerManagerLoginEnabled(shop)) {

            return customerManagerService.getCustomerByEmail(email, shop);

        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Shop> getCustomerShops(final Customer customer) {
        if (AttributeNamesKeys.Cart.CUSTOMER_TYPE_MANAGER.equals(customer.getCustomerType())) {
            return customerManagerService.getCustomerShops(customer);
        }
        return customerService.getCustomerShops(customer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatNameFor(final Customer customer, final Shop shop) {
        return customerService.formatNameFor(customer, shop);
    }

    @Override
    public boolean authenticate(final String email, final Shop shop, final String password) {

        final boolean customerInShop = customerService.isCustomerExists(email, shop);

        return (customerInShop && customerService.isPasswordValid(email, shop, password)) ||
                (!customerInShop && customerManagerService.isManagerExists(email, shop) &&
                    customerManagerService.isPasswordValid(email, shop, password));

    }
}
