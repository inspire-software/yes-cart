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

package org.yes.cart.service.order.impl.handler;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.Assert.*;

/* User: Igor Azarny iazarny@yahoo.com
* Date: 09-May-2011
* Time: 14:12:54
*/
public class CancelOrderWithRefundOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private CustomerOrderService orderService;
    private OrderEventHandler pendingHandler;
    private SkuWarehouseService skuWarehouseService;
    private OrderEventHandler handler;

    @Before
    public void setUp()  {
        orderService = (CustomerOrderService) ctx().getBean("customerOrderService");
        skuWarehouseService = (SkuWarehouseService) ctx().getBean("skuWarehouseService");
        pendingHandler = (OrderEventHandler) ctx().getBean("pendingOrderEventHandler");
        handler = (OrderEventHandler) ctx().getBean("cancelOrderWithRefundOrderEventHandler");
        super.setUp();
    }

    /**
     * Test on order , that not completed.
     */
    @Test
    public void testHandle0() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        CustomerOrder customerOrder = orderService.createFromCart(getStdCard(customer.getEmail()), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());
        customerOrder.setPgLabel("testPaymentGatewayLabel");
        orderService.update(customerOrder);
        assertTrue(pendingHandler.handle(new OrderEventImpl("", customerOrder, null, Collections.EMPTY_MAP)));
        SkuWarehouse skuWarehouse = skuWarehouseService.getById(31);
        skuWarehouse.setReserved(new BigDecimal("0.00"));
        skuWarehouse.setQuantity(new BigDecimal("0.00"));
        skuWarehouseService.update(skuWarehouse);
        skuWarehouse = skuWarehouseService.getById(30);
        skuWarehouse.setReserved(new BigDecimal("0.00"));
        skuWarehouse.setQuantity(new BigDecimal("7.00"));
        skuWarehouseService.update(skuWarehouse);
        assertTrue(handler.handle(new OrderEventImpl("", customerOrder, null, null)));
        skuWarehouse = skuWarehouseService.getById(31);
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getReserved().setScale(Constants.DEFAULT_SCALE));
        assertEquals(new BigDecimal("1.00"), skuWarehouse.getQuantity().setScale(Constants.DEFAULT_SCALE));
        skuWarehouse = skuWarehouseService.getById(30);
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getReserved().setScale(Constants.DEFAULT_SCALE));
        assertEquals(new BigDecimal("9.00"), skuWarehouse.getQuantity().setScale(Constants.DEFAULT_SCALE));
        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }

    /**
     * Test on order , that not completed.
     */
    @Test
    public void testHandle1() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        CustomerOrder customerOrder = orderService.createFromCart(getStdCard(customer.getEmail()), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());
        customerOrder.setPgLabel("testPaymentGatewayLabel");
        orderService.update(customerOrder);
        assertTrue(pendingHandler.handle(new OrderEventImpl("", customerOrder, null, Collections.EMPTY_MAP)));
        customerOrder.getDelivery().iterator().next().setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);
        orderService.update(customerOrder);
        customerOrder = orderService.getById(customerOrder.getCustomerorderId());
        assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED,
                customerOrder.getDelivery().iterator().next().getDeliveryStatus());
        SkuWarehouse skuWarehouse = skuWarehouseService.getById(31);
        skuWarehouse.setReserved(new BigDecimal("0.00"));
        skuWarehouse.setQuantity(new BigDecimal("0.00"));
        skuWarehouseService.update(skuWarehouse);
        skuWarehouse = skuWarehouseService.getById(30);
        skuWarehouse.setReserved(new BigDecimal("0.00"));
        skuWarehouse.setQuantity(new BigDecimal("7.00"));
        skuWarehouseService.update(skuWarehouse);
        try {
            handler.handle(new OrderEventImpl("", customerOrder, null, null));
            fail("Unable to handle cancellation for delivery 130830233414-2-0 with status ds.shipped");
        } catch (OrderException oe) {
            // ok
        }
        skuWarehouse = skuWarehouseService.getById(31);
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getReserved().setScale(Constants.DEFAULT_SCALE));
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getQuantity().setScale(Constants.DEFAULT_SCALE));
        skuWarehouse = skuWarehouseService.getById(30);
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getReserved().setScale(Constants.DEFAULT_SCALE));
        assertEquals(new BigDecimal("7.00"), skuWarehouse.getQuantity().setScale(Constants.DEFAULT_SCALE));
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }
}
