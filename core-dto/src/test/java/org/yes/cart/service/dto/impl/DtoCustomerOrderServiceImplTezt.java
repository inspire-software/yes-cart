/*
 * Copyright 2009 Inspire-Software.com
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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.DtoServiceSpringKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.CustomerOrderDTO;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDTO;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDetailDTO;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.dto.DtoCustomerOrderService;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCustomerOrderServiceImplTezt extends BaseCoreDBTestCase {

    private DtoCustomerOrderService dtoService;
    private CustomerOrderService customerOrderService;

    private GenericDAO<Object, Long> genericDao;


    @Before
    public void setUp() {
        dtoService = (DtoCustomerOrderService) ctx().getBean(DtoServiceSpringKeys.DTO_CUSTOMER_ORDER_SERVICE);
        customerOrderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        genericDao = (GenericDAO<Object, Long>) ctx().getBean("genericDao");
        super.setUp();
    }

    @Test(expected = UnableToCreateInstanceException.class)
    public void testCreate() throws Exception {
        dtoService.create(null);
    }


    @Test
    public void testRetreiveCreatedOrder() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart(true);
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart);
        long pk = order.getCustomerorderId();
        assertNotNull(dtoService.getById(pk));
    }

    @Test
    public void testFindDeliveryByOrderNumber() throws Exception {

        final Customer customer = createCustomer();
        final ShoppingCart shoppingCart = getShoppingCart(true);
        final CustomerOrder order = customerOrderService.createFromCart(shoppingCart);
        final String orderNum = order.getOrdernum();
        final List<CustomerOrderDeliveryDTO> shipments = dtoService.findDeliveryByOrderNumber(orderNum);
        assertTrue("Cant get deliveries for order num " + orderNum , !shipments.isEmpty());
        final Set deliveryNumsSet = new HashSet();
        for (CustomerOrderDeliveryDTO dto : shipments) {
            assertTrue("At lest one item in shipment must be present", !dto.getDetail().isEmpty());
            deliveryNumsSet.add(dto.getDeliveryNum());
        }
        assertEquals(4, deliveryNumsSet.size());
    }

    @Test
    public void testFindDeliveryDetails()      throws Exception {
        final Customer customer = createCustomer();
        final ShoppingCart shoppingCart = getShoppingCart(true);
        final CustomerOrder order = customerOrderService.createFromCart(shoppingCart);
        final String orderNum = order.getOrdernum();
        final List<CustomerOrderDeliveryDetailDTO> details = dtoService.findDeliveryDetailsByOrderNumber(orderNum);
        assertTrue(!details.isEmpty());
        final Set deliveryNumsSet = new HashSet();
        for (CustomerOrderDeliveryDetailDTO det : details) {
            assertEquals("ds.fullfillment", det.getDeliveryStatusLabel());
            deliveryNumsSet.add(det.getDeliveryNum());
        }
        assertEquals(4, deliveryNumsSet.size());


    }

    @Test
    public void testFindDeliveryDetailsNoDeliveries() throws Exception {
        final Customer customer = createCustomer();
        final ShoppingCart shoppingCart = getShoppingCart(true);
        final CustomerOrder order = customerOrderService.createFromCart(shoppingCart);

        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus status) {
                final CustomerOrder persistent = customerOrderService.findById(order.getCustomerorderId());
                final Iterator<CustomerOrderDelivery> itDel = persistent.getDelivery().iterator();
                while (itDel.hasNext()) {

                    final CustomerOrderDelivery del = itDel.next();
                    genericDao.delete(del);
                    itDel.remove();
                }
            }
        });

        final String orderNum = order.getOrdernum();
        final List<CustomerOrderDeliveryDetailDTO> details = dtoService.findDeliveryDetailsByOrderNumber(orderNum);

        assertTrue(!details.isEmpty());

        for(CustomerOrderDeliveryDetailDTO det :details) {
            assertEquals("os.none", det.getDeliveryStatusLabel());
            assertNull(det.getDeliveryNum());
        }

        assertEquals(6, details.size());

    }


    @Test
    public void testFindOrders() throws Exception {
        final Customer customer = createCustomer();
        final ShoppingCart shoppingCart = getShoppingCart(true);
        final CustomerOrder order = customerOrderService.createFromCart(shoppingCart);

        SearchContext ctx;
        SearchResult<CustomerOrderDTO> rez;


        // by PK
        ctx = createSearchContext( 0, 10,
                "filter", "*" + order.getCustomerorderId()
        );
        rez = dtoService.findOrders(ctx);
        assertEquals(1, rez.getTotal());
        assertEquals(1, rez.getItems().size());
        assertEquals(order.getCustomerorderId(), rez.getItems().get(0).getCustomerorderId());

        // by reference
        ctx = createSearchContext( 0, 10,
                "filter", "#" + order.getOrdernum()
        );
        rez = dtoService.findOrders(ctx);
        assertEquals(1, rez.getTotal());
        assertEquals(1, rez.getItems().size());
        assertEquals(order.getOrdernum(), rez.getItems().get(0).getOrdernum());

        // by customer
        ctx = createSearchContext( 0, 10,
                "filter", "?" + order.getFirstname()
        );
        rez = dtoService.findOrders(ctx);
        assertEquals(1, rez.getTotal());
        assertEquals(1, rez.getItems().size());
        assertEquals(order.getFirstname(), rez.getItems().get(0).getFirstname());

        // by address
        ctx = createSearchContext( 0, 10,
                "filter", "@" + order.getBillingAddress()
        );
        rez = dtoService.findOrders(ctx);
        assertTrue(rez.getTotal() > 1);
        assertTrue(rez.getItems().size() > 1);
        assertEquals(order.getBillingAddress(), rez.getItems().get(0).getBillingAddress());

        // by shop
        ctx = createSearchContext( 0, 10,
                "filter", "^" + order.getShop().getCode()
        );
        rez = dtoService.findOrders(ctx);
        assertTrue(rez.getTotal() > 1);
        assertTrue(rez.getItems().size() > 1);
        assertEquals(order.getShop().getShopId(), rez.getItems().get(0).getShopId());

        // basic
        ctx = createSearchContext( 0, 10,
                "filter", order.getOrdernum()
        );
        rez = dtoService.findOrders(ctx);
        assertEquals(1, rez.getTotal());
        assertEquals(1, rez.getItems().size());
        assertEquals(order.getOrdernum(), rez.getItems().get(0).getOrdernum());

        // all
        ctx = createSearchContext( 0, 10);
        rez = dtoService.findOrders(ctx);
        assertTrue(rez.getTotal() > 1);
        assertTrue(rez.getItems().size() > 1);

        // time
        ctx = createSearchContext( 0, 10,
                "filter", "2019-03<2019-04"
        );
        rez = dtoService.findOrders(ctx);
        assertEquals(2, rez.getTotal());
        assertEquals(2, rez.getItems().size());

        // dry run for reservation check query
        ctx = createSearchContext( 0, 10,
                "filter", "!reservationcheck"
        );
        rez = dtoService.findOrders(ctx);
        assertEquals(0, rez.getTotal());

        // with status
        ctx = createSearchContext( 0, 10,
                "statuses", Collections.singletonList("os.in.progress")
        );
        rez = dtoService.findOrders(ctx);
        assertTrue(rez.getTotal() > 0);

        ctx = createSearchContext( 0, 10,
                "statuses", Collections.singletonList("invalid")
        );
        rez = dtoService.findOrders(ctx);
        assertTrue(rez.getTotal() == 0);


    }


}
