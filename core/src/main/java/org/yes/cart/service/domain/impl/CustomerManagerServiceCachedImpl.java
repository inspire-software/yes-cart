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

import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CustomerManagerService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 26/10/2019
 * Time: 10:24
 */
public class CustomerManagerServiceCachedImpl implements CustomerManagerService {

    private final CustomerManagerService customerManagerService;

    public CustomerManagerServiceCachedImpl(final CustomerManagerService customerManagerService) {
        this.customerManagerService = customerManagerService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCustomerManagerLoginEnabled(final Shop shop) {
        return customerManagerService.isCustomerManagerLoginEnabled(shop);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "customerService-customerByEmail", condition = "#shop != null", key = "#email + #shop.code + '!m'")
    public Customer getCustomerByEmail(final String email, final Shop shop) {
        return customerManagerService.getCustomerByEmail(email, shop);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isManagerExists(final String email, final Shop shop) {
        return customerManagerService.isManagerExists(email, shop);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPasswordValid(final String email, final Shop shop, final String password) {
        return customerManagerService.isPasswordValid(email, shop, password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Shop> getCustomerShops(final Customer customer) {
        return customerManagerService.getCustomerShops(customer);
    }

}
