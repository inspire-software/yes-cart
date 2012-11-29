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

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.service.order.impl.handler.PendingOrderEventHandlerImpl;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.impl.AddSkuToCartEventCommandImpl;
import org.yes.cart.shoppingcart.impl.SetCarrierSlaCartCommandImpl;
import org.yes.cart.shoppingcart.impl.SetSkuQuantityToCartEventCommandImpl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestCustomerOrderServiceImpl extends BaseCoreDBTestCase {

    private CustomerOrderService customerOrderService;

    private PendingOrderEventHandlerImpl handler;


    @Before
    public void setUp() throws Exception {
        customerOrderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        handler = (PendingOrderEventHandlerImpl) ctx().getBean("pendingOrderEventHandler");
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
        assertNull(customerOrderService.getById(pk));
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
        assertNull(customerOrderService.getById(pk));
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
        ShoppingCart shoppingCart = getShoppingCart2(getTestName());
        assertFalse(customerOrderService.isOrderCanHasMultipleDeliveries(shoppingCart));
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, true);
        assertEquals(2, order.getDelivery().size());
    }

    @Test
    public void testOrderAmountCalculation() throws Exception {
        String prefix = UUID.randomUUID().toString();
        Customer customer = createCustomer(prefix);
        assertFalse(customer.getAddress().isEmpty());
        assertNotNull(customer.getDefaultAddress(Address.ADDR_TYPE_BILLING));
        assertNotNull(customer.getDefaultAddress(Address.ADDR_TYPE_SHIPING));

        ShoppingCart shoppingCart = getEmptyCart(getTestName() + prefix);

        assertEquals(getTestName() + prefix + "jd@domain.com", shoppingCart.getCustomerEmail());
        assertEquals(customer.getEmail(), shoppingCart.getCustomerEmail());

        //one delivery 16.77 usd
        new SetCarrierSlaCartCommandImpl(null, singletonMap(SetCarrierSlaCartCommandImpl.CMD_KEY, "3"))
                .execute(shoppingCart);

        new AddSkuToCartEventCommandImpl(ctx(), singletonMap(AddSkuToCartEventCommandImpl.CMD_KEY, "CC_TEST1"))
                .execute(shoppingCart);
        new AddSkuToCartEventCommandImpl(ctx(), singletonMap(AddSkuToCartEventCommandImpl.CMD_KEY, "CC_TEST1"))
                .execute(shoppingCart);
        new AddSkuToCartEventCommandImpl(ctx(), singletonMap(AddSkuToCartEventCommandImpl.CMD_KEY, "CC_TEST1"))
                .execute(shoppingCart);
        // 3 x 180  usd
        new AddSkuToCartEventCommandImpl(ctx(), singletonMap(AddSkuToCartEventCommandImpl.CMD_KEY, "CC_TEST3"))
                .execute(shoppingCart);
        new AddSkuToCartEventCommandImpl(ctx(), singletonMap(AddSkuToCartEventCommandImpl.CMD_KEY, "CC_TEST3"))
                .execute(shoppingCart);
        //2 x 7.99  usd

        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, true);

        assertEquals(CustomerOrder.ORDER_STATUS_NONE, order.getOrderStatus());
        order.setPgLabel("testPaymentGatewayLabel");
        order = customerOrderService.update(order);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        order,
                        null,
                        Collections.EMPTY_MAP)));

        BigDecimal amount = customerOrderService.getOrderAmount(order.getOrdernum());

        assertTrue("payment must be 16.77 + 3 * 190.01 + 2 * 70.99 = 728.78, but was  " + amount
                , new BigDecimal("728.78").equals(amount));


        assertEquals(1, order.getDelivery().size());
    }

    /**
     * @return cart with one digital available product.
     */
    protected ShoppingCart getShoppingCart2(final String prefix) {
        ShoppingCart shoppingCart = getEmptyCart(prefix);
        // this digital product available
        Map<String, String> param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST9");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "1.00");
        new SetSkuQuantityToCartEventCommandImpl(ctx(), param)
                .execute(shoppingCart);
        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST5");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "200.00");
        new SetSkuQuantityToCartEventCommandImpl(ctx(), param)
                .execute(shoppingCart);
        return shoppingCart;
    }
}
