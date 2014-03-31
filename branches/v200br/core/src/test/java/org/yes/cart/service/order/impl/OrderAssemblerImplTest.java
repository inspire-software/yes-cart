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

package org.yes.cart.service.order.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderAssembler;
import org.yes.cart.shoppingcart.ShoppingCart;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class OrderAssemblerImplTest extends BaseCoreDBTestCase {

    private OrderAssembler orderAssembler;
    private CustomerOrderService customerOrderService;

    @Before
    public void setUp()  {
        orderAssembler = (OrderAssembler) ctx().getBean(ServiceSpringKeys.ORDER_ASSEMBLER);
        customerOrderService =  ctx().getBean("customerOrderService", CustomerOrderService.class);
        super.setUp();
    }

    @Test
    public void testAssembleCustomerOrder() {
        Customer customer = createCustomer();
        ShoppingCart shoppingCart = getShoppingCart2(customer.getEmail());
        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        assertNotNull(customerOrder);
        customerOrder =  customerOrderService.create(customerOrder);
        assertNotNull(customerOrder);
        assertNotNull(customerOrder.getBillingAddress());
        assertEquals("By default billing and shipping addresses the same",
                customerOrder.getBillingAddress(),
                customerOrder.getShippingAddress());
        assertTrue("By Default billing address is shipping address ",
                customerOrder.getBillingAddress().contains("shipping addr"));
        assertEquals("Order must be in ORDER_STATUS_NONE state",
                CustomerOrder.ORDER_STATUS_NONE,
                customerOrder.getOrderStatus());
        assertNotNull("Order num must be set", customerOrder.getOrdernum());
        assertTrue("Order in pending state must not have delivery", customerOrder.getDelivery().isEmpty());
        assertEquals("Shopping cart guid and order guid are equals",
                shoppingCart.getGuid(),
                customerOrder.getGuid());
        assertEquals(8, customerOrder.getOrderDetail().size());
        assertFalse(customerOrder.isMultipleShipmentOption());
    }
}
