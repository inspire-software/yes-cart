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
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DeliveryAllowedByTimeoutOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private CustomerOrderService orderService;
    private OrderEventHandler handler;

    @Before
    public void setUp()  {
        handler = (OrderEventHandler) ctx().getBean("deliveryAllowedByTimeoutOrderEventHandler");
        orderService = (CustomerOrderService) ctx().getBean("customerOrderService");
        super.setUp();
    }

    @Test
    public void testHandle() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        CustomerOrder customerOrder = orderService.createFromCart(getStdCard(ctx(), customer.getEmail()), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());
        CustomerOrderDelivery delivery = customerOrder.getDelivery().iterator().next();
        Calendar calendar = Calendar.getInstance();
        //set allowed time in the future
        calendar.set(2020, 9, 11);
        for (CustomerOrderDeliveryDet det : delivery.getDetail()) {
            det.getSku().getProduct().setAvailablefrom(calendar.getTime());
        }
        assertFalse(handler.handle(
                new OrderEventImpl(
                        "", //evt.payment.offline
                        customerOrder,
                        delivery
                )
        ));
        //set allowed time in the past
        calendar.set(2011, 1, 22);
        for (CustomerOrderDeliveryDet det : delivery.getDetail()) {
            det.getSku().getProduct().setAvailablefrom(calendar.getTime());
        }
        assertTrue(handler.handle(
                new OrderEventImpl(
                        "", //evt.payment.offline
                        customerOrder,
                        delivery
                )
        ));
        assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED/*DELIVERY_STATUS_INVENTORY_WAIT*/,  //it push next transition automatically - allocate quantity
                delivery.getDeliveryStatus());
    }
}
