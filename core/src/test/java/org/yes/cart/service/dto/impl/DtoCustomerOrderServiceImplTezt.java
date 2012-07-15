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

package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.dto.DtoCustomerOrderService;
import org.yes.cart.shoppingcart.ShoppingCart;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCustomerOrderServiceImplTezt extends BaseCoreDBTestCase {

    private DtoCustomerOrderService dtoService;
    private CustomerOrderService customerOrderService;

    @Before
    public void setUp() throws Exception {
        dtoService = (DtoCustomerOrderService) ctx().getBean(ServiceSpringKeys.DTO_CUSTOMER_ORDER_SERVICE);
        customerOrderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
    }

    @Test(expected = UnableToCreateInstanceException.class)
    public void testCreate() throws Exception {
        dtoService.create(null);
    }

    @Test
    public void testUpdate() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart();
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
        long pk = order.getCustomerorderId();
        assertNotNull(dtoService.getById(pk));
    }
}
