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
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.utils.DateUtils;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerOrderServiceImplTest extends BaseCoreDBTestCase {

    private CustomerOrderService customerOrderService;


    @Override
    @Before
    public void setUp() {
        customerOrderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        super.setUp();
    }

    @Test
    public void testCreateAndDelete() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart(true);
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart);
        long pk = order.getCustomerorderId();
        assertTrue(pk > 0);
        customerOrderService.delete(order);
        assertNull(customerOrderService.findById(pk));
    }

    @Test
    public void testFindByGuid() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart(true);
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart);
        long pk = order.getCustomerorderId();
        assertTrue(pk > 0);
        CustomerOrder order2 = customerOrderService.findByReference(shoppingCart.getGuid());
        assertNotNull(order2);
        customerOrderService.delete(order);
        assertNull(customerOrderService.findById(pk));
        assertNull(customerOrderService.findByReference(shoppingCart.getGuid()));
        assertNull(customerOrderService.findByReference(null));
    }

    @Test
    public void testGetCustomerOrders() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart(true);
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart);
        assertTrue(order.getCustomerorderId() > 0);
        assertEquals(1, customerOrderService.findCustomerOrders(customer, null).size());
    }

    @Test
    public void testPersistReassembledOrder1() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart(true);
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart);
        assertEquals(4, order.getDelivery().size());
    }

    @Test
    public void testPersistReassembledOrder2() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart(false);
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart);
        assertEquals(2, order.getDelivery().size());
    }

    @Test
    public void testPersistReassembledOrder3() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart2(customer.getEmail(), false);
        final Map<String, Boolean> allowed = customerOrderService.isOrderMultipleDeliveriesAllowed(shoppingCart);
        assertFalse(allowed.values().iterator().next());
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart);
        assertEquals(2, order.getDelivery().size());
    }

    /**
     * @return cart with one digital available product.
     */
    @Override
    protected ShoppingCart getShoppingCart2(final String customerEmail, final boolean multi) {
        final ShoppingCart cart = getShoppingCart2(getEmptyCart(customerEmail));
        prepareMultiDeliveriesAndRecalculate(cart, multi);
        return cart;
    }

    private ShoppingCart getShoppingCart2(final ShoppingCart shoppingCart) {

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        // this digital product available
        Map<String, String> param = new HashMap<>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST9");
        param.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        param.put(ShoppingCartCommand.CMD_P_QTY, "1.00");
        commands.execute(shoppingCart,
                (Map) param);

        param = new HashMap<>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST5");
        param.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        param.put(ShoppingCartCommand.CMD_P_QTY, "200.00");
        commands.execute(shoppingCart,
                (Map) param);

        return shoppingCart;
    }


    @Test
    public void testFindCustomerOrder() {

        final List<Long> shop10 = Collections.singletonList(10L);

        List<CustomerOrder> list;
        int count;

        final Map<String, List> filterNone = null;
        count = customerOrderService.findOrderCount(filterNone);
        assertTrue(count > 0);
        list = customerOrderService.findOrders(0, 1, "ordernum", false, filterNone);
        assertFalse(list.isEmpty());

        final Map<String, List> filterNoneShop10 = Collections.singletonMap("shopIds", shop10);
        count = customerOrderService.findOrderCount(filterNoneShop10);
        assertTrue(count > 0);
        list = customerOrderService.findOrders(0, 1, "ordernum", false, filterNoneShop10);
        assertFalse(list.isEmpty());


        final Map<String, List> filterAny = new HashMap<>();
        SearchContext.JoinMode.OR.setMode(filterAny);
        filterAny.put("email", Collections.singletonList("190323"));
        filterAny.put("ordernum", Collections.singletonList("190323"));
        filterAny.put("lastname", Collections.singletonList("190323"));

        count = customerOrderService.findOrderCount(filterAny);
        assertEquals(2, count);
        list = customerOrderService.findOrders(0, 1, "ordernum", false, filterAny);
        assertEquals(1, list.size());


        final Map<String, List> filterAnyShop10 = new HashMap<>();
        SearchContext.JoinMode.OR.setMode(filterAnyShop10);
        filterAnyShop10.put("email", Collections.singletonList("190323"));
        filterAnyShop10.put("ordernum", Collections.singletonList("190323"));
        filterAnyShop10.put("lastname", Collections.singletonList("190323"));
        filterAnyShop10.put("shopIds", shop10);
        list = customerOrderService.findOrders(1, 1, "ordernum", false, filterAnyShop10);
        assertEquals(1, list.size());


        final Map<String, List> filterSpecific = new HashMap<>();
        filterSpecific.put("email", Collections.singletonList("reg@test.com"));
        filterSpecific.put("ordernum", Collections.singletonList("190323063753-2"));

        count = customerOrderService.findOrderCount(filterSpecific);
        assertEquals(1, count);
        list = customerOrderService.findOrders(0, 1, "ordernum", false, filterSpecific);
        assertEquals(1, list.size());

        final Map<String, List> filterNoMatch = Collections.singletonMap("ordernum", Collections.singletonList("ZZZZZZZ"));

        count = customerOrderService.findOrderCount(filterNoMatch);
        assertEquals(0, count);
        list = customerOrderService.findOrders(0, 1, "ordernum", false, filterNoMatch);
        assertEquals(0, list.size());

        final Map<String, List> filterStatusSpecific = new HashMap<>();
        filterStatusSpecific.put("orderStatus", Collections.singletonList("os.in.progress"));
        filterStatusSpecific.put("shopIds", shop10);

        count = customerOrderService.findOrderCount(filterStatusSpecific);
        assertTrue(count > 0);
        list = customerOrderService.findOrders(0, 1, "ordernum", false, filterStatusSpecific);
        assertFalse(list.isEmpty());

        final Map<String, List> filterStatusSpecificNoMatch = new HashMap<>();
        filterStatusSpecificNoMatch.put("orderStatus", Collections.singletonList("zzzzzz"));
        filterStatusSpecificNoMatch.put("shopIds", shop10);

        count = customerOrderService.findOrderCount(filterStatusSpecificNoMatch);
        assertEquals(0, count);
        list = customerOrderService.findOrders(0, 1, "ordernum", false, filterStatusSpecificNoMatch);
        assertTrue(list.isEmpty());

        final Map<String, List> filterOr = new HashMap<>();
        SearchContext.JoinMode.OR.setMode(filterOr);
        filterOr.put("email", Collections.singletonList("reg@test.com"));
        filterOr.put("ordernum", Collections.singletonList("190323063746-1"));
        filterOr.put("shopIds", shop10);

        count = customerOrderService.findOrderCount(filterOr);
        assertEquals(2, count);
        list = customerOrderService.findOrders(0, 2, "ordernum", false, filterOr);
        assertEquals(2, list.size());

        final Map<String, List> filterCreated = new HashMap<>();
        filterCreated.put("email", Collections.singletonList("reg@test.com"));
        filterCreated.put("orderTimestamp", Arrays.asList(
                SearchContext.MatchMode.GE.toParam(DateUtils.ldtParseSDT("2019-03-01")),
                SearchContext.MatchMode.LT.toParam(DateUtils.ldtParseSDT("2019-04-01"))
                )
        );
        filterCreated.put("shopIds", shop10);

        count = customerOrderService.findOrderCount(filterCreated);
        assertEquals(2, count);
        list = customerOrderService.findOrders(0, 2, "ordernum", false, filterCreated);
        assertEquals(2, list.size());

    }


}
