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
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestCustomerOrderServiceImpl extends BaseCoreDBTestCase {

    private CustomerOrderService customerOrderService;
    private ProductSkuService productSkuService;

    private OrderEventHandler handler;


    @Before
    public void setUp() {
        customerOrderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        productSkuService = (ProductSkuService) ctx().getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);
        handler = (OrderEventHandler) ctx().getBean("pendingOrderEventHandler");
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

        ShoppingCart shoppingCart = getEmptyCartByPrefix(getTestName() + prefix);

        assertEquals(getTestName() + prefix + "jd@domain.com", shoppingCart.getCustomerEmail());
        assertEquals(customer.getEmail(), shoppingCart.getCustomerEmail());

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        //one delivery 16.77 usd
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETCARRIERSLA, "3"));


        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST1"));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST1"));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST1"));
        // 3 x 180  usd

        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST3"));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST3"));
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
                , new BigDecimal("728.78").compareTo(amount) == 0);


        assertEquals(1, order.getDelivery().size());
    }

    /**
     * @return cart with one digital available product.
     */
    protected ShoppingCart getShoppingCart2ByPrefix(final String prefix) {
        return getShoppingCart2(getEmptyCartByPrefix(prefix));
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





    @Test
    public void testFindDeliveryAwaitingForInventory()      throws Exception {
        final Customer customer = createCustomer();
        final ShoppingCart shoppingCart = getShoppingCartWithPreorderItems(getTestName(), 1);

        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, order.getOrderStatus());
        order.setPgLabel("testPaymentGatewayLabel");
        customerOrderService.update(order);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        order,
                        null,
                        Collections.EMPTY_MAP)));
        customerOrderService.update(order);
        order = customerOrderService.findByGuid(shoppingCart.getGuid());
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery : order.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT, delivery.getDeliveryStatus());
        }

        List<CustomerOrderDelivery> rez = customerOrderService.findAwaitingDeliveries(
                Arrays.asList(productSkuService.findById(15330L).getCode()), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT,
                Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS));
        assertEquals("Expect one order with preorder sku id = 15330", 1, rez.size());

        rez = customerOrderService.findAwaitingDeliveries(
                Arrays.asList(productSkuService.findById(15340L).getCode()), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT,
                Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS));
        assertEquals("Expect one order with preorder sku id = 15340", 1, rez.size());

        rez = customerOrderService.findAwaitingDeliveries(
                Arrays.asList(productSkuService.findById(15129L).getCode()), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT,
                Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS));
        assertEquals("Not expected orders waiting for inventory sku id = 15129" ,0, rez.size());

        rez = customerOrderService.findAwaitingDeliveries(null, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT,
                Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS));
        assertEquals("Total two orders wait for inventory", 2, rez.size());



    }

}
