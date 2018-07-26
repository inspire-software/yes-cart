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

import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.CustomerRemoveService;
import org.yes.cart.service.domain.CustomerService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 24/07/2018
 * Time: 19:05
 */
public class CustomerRemoveServiceImpl implements CustomerRemoveService {

    private final CustomerService customerService;
    private final CustomerOrderService customerOrderService;

    public CustomerRemoveServiceImpl(final CustomerService customerService,
                                     final CustomerOrderService customerOrderService) {
        this.customerService = customerService;
        this.customerOrderService = customerOrderService;
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAccount(final Customer customer, final Shop shop, final String token) {

        if (token != null) {
            this.deleteAccount(customer);
        } else {
            this.customerService.update(customer); // update auth token from aspect
        }

    }

    /** {@inheritDoc} */
    @Override
    public void deleteAccount(final Customer customer) {

        final List<CustomerOrder> orders = this.customerOrderService.findByCriteria(" where e.customer = ?1", customer);
        for (final CustomerOrder order : orders) {
            this.customerOrderService.delete(order);
        }
        this.customerService.delete(this.customerService.findById(customer.getCustomerId()));

    }
    
}
