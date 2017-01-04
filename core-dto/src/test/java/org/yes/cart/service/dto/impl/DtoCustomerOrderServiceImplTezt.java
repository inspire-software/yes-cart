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

package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.DtoServiceSpringKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDTO;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDetailDTO;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.dto.DtoCustomerOrderService;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
        assertEquals(5, deliveryNumsSet.size());
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
        assertEquals(5, deliveryNumsSet.size());


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
            assertNull(det.getDeliveryStatusLabel());
            assertNull(det.getDeliveryNum());
        }

        assertEquals(6, details.size());

    }


}
