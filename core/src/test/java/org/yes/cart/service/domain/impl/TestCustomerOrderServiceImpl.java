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
public class TestCustomerOrderServiceImpl extends BaseCoreDBTestCase {

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

        final Set<Long> shopAll = null;
        final Set<Long> shop10 = Collections.singleton(10L);

        List<CustomerOrder> list;
        int count;

        final Map<String, List> filterNone = null;

        count = customerOrderService.findCustomerOrderCount(shopAll, filterNone);
        assertTrue(count > 0);
        list = customerOrderService.findCustomerOrder(0, 1, "ordernum", false, shopAll, filterNone);
        assertFalse(list.isEmpty());

        count = customerOrderService.findCustomerOrderCount(shop10, filterNone);
        assertTrue(count > 0);
        list = customerOrderService.findCustomerOrder(0, 1, "ordernum", false, shop10, filterNone);
        assertFalse(list.isEmpty());


        final Map<String, List> filterAny = new HashMap<>();
        SearchContext.JoinMode.OR.setMode(filterAny);
        filterAny.put("email", Collections.singletonList("190323"));
        filterAny.put("ordernum", Collections.singletonList("190323"));
        filterAny.put("lastname", Collections.singletonList("190323"));

        count = customerOrderService.findCustomerOrderCount(shopAll, filterAny);
        assertEquals(2, count);
        list = customerOrderService.findCustomerOrder(0, 1, "ordernum", false, shopAll, filterAny);
        assertEquals(1, list.size());
        list = customerOrderService.findCustomerOrder(1, 1, "ordernum", false, shopAll, filterAny);
        assertEquals(1, list.size());


        final Map<String, List> filterSpecific = new HashMap<>();
        filterSpecific.put("email", Collections.singletonList("reg@test.com"));
        filterSpecific.put("ordernum", Collections.singletonList("190323063753-2"));

        count = customerOrderService.findCustomerOrderCount(shopAll, filterSpecific);
        assertEquals(1, count);
        list = customerOrderService.findCustomerOrder(0, 1, "ordernum", false, shopAll, filterSpecific);
        assertEquals(1, list.size());

        final Map<String, List> filterNoMatch = Collections.singletonMap("ordernum", Collections.singletonList("ZZZZZZZ"));

        count = customerOrderService.findCustomerOrderCount(shopAll, filterNoMatch);
        assertEquals(0, count);
        list = customerOrderService.findCustomerOrder(0, 1, "ordernum", false, shopAll, filterNoMatch);
        assertEquals(0, list.size());

        final Map<String, List> filterStatusSpecific = Collections.singletonMap("orderStatus", Collections.singletonList("os.in.progress"));

        count = customerOrderService.findCustomerOrderCount(shop10, filterStatusSpecific);
        assertTrue(count > 0);
        list = customerOrderService.findCustomerOrder(0, 1, "ordernum", false, shop10, filterStatusSpecific);
        assertFalse(list.isEmpty());

        final Map<String, List> filterStatusSpecificNoMatch = Collections.singletonMap("orderStatus", Collections.singletonList("zzzzzz"));

        count = customerOrderService.findCustomerOrderCount(shop10, filterStatusSpecificNoMatch);
        assertEquals(0, count);
        list = customerOrderService.findCustomerOrder(0, 1, "ordernum", false, shop10, filterStatusSpecificNoMatch);
        assertTrue(list.isEmpty());

        final Map<String, List> filterOr = new HashMap<>();
        SearchContext.JoinMode.OR.setMode(filterOr);
        filterOr.put("email", Collections.singletonList("reg@test.com"));
        filterOr.put("ordernum", Collections.singletonList("190323063746-1"));

        count = customerOrderService.findCustomerOrderCount(shop10, filterOr);
        assertEquals(2, count);
        list = customerOrderService.findCustomerOrder(0, 2, "ordernum", false, shop10, filterOr);
        assertEquals(2, list.size());

        final Map<String, List> filterCreated = new HashMap<>();
        filterCreated.put("email", Collections.singletonList("reg@test.com"));
        filterCreated.put("orderTimestamp", Arrays.asList(
                SearchContext.MatchMode.GE.toParam(DateUtils.ldtParseSDT("2019-03-01")),
                SearchContext.MatchMode.LT.toParam(DateUtils.ldtParseSDT("2019-04-01"))
                )
        );

        count = customerOrderService.findCustomerOrderCount(shop10, filterCreated);
        assertEquals(2, count);
        list = customerOrderService.findCustomerOrder(0, 2, "ordernum", false, shop10, filterCreated);
        assertEquals(2, list.size());

    }


}
