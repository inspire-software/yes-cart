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

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestCustomerOrderServiceImpl extends BaseCoreDBTestCase {

    private CustomerOrderService customerOrderService;


    @Before
    public void setUp() {
        customerOrderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        super.setUp();
    }

    @Test
    public void testCreateAndDelete() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart();
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
        long pk = order.getCustomerorderId();
        assertTrue(pk > 0);
        customerOrderService.delete(order);
        assertNull(customerOrderService.findById(pk));
    }

    @Test
    public void testFindByGuid() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart();
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
        long pk = order.getCustomerorderId();
        assertTrue(pk > 0);
        CustomerOrder order2 = customerOrderService.findByGuid(shoppingCart.getGuid());
        assertNotNull(order2);
        customerOrderService.delete(order);
        assertNull(customerOrderService.findById(pk));
        assertNull(customerOrderService.findByGuid(shoppingCart.getGuid()));
        assertNull(customerOrderService.findByGuid(null));
    }

    @Test
    public void testGetCustomerOrders() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart();
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
        assertTrue(order.getCustomerorderId() > 0);
        assertEquals(1, customerOrderService.findCustomerOrders(customer, null).size());
    }

    @Test
    public void testPersistReassembledOrder1() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart();
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
        assertEquals(4, order.getDelivery().size());
    }

    @Test
    public void testPersistReassembledOrder2() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart();
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, true);
        assertEquals(2, order.getDelivery().size());
    }

    @Test
    public void testPersistReassembledOrder3() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart2(customer.getEmail());
        assertFalse(customerOrderService.isOrderMultipleDeliveriesAllowed(shoppingCart));
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, true);
        assertEquals(2, order.getDelivery().size());
    }

    /**
     * @return cart with one digital available product.
     */
    protected ShoppingCart getShoppingCart2(final String customerEmail) {
        return getShoppingCart2(getEmptyCart(customerEmail));
    }

    private ShoppingCart getShoppingCart2(final ShoppingCart shoppingCart) {

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        // this digital product available
        Map<String, String> param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST9");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart,
                (Map) param);

        param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST5");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "200.00");
        commands.execute(shoppingCart,
                (Map) param);

        return shoppingCart;
    }

}
