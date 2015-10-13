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

package org.yes.cart.service.order.impl.handler;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.OrderItemAllocationException;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Test covers flow from Order in progress, Delivery waiting for allocation flow (i.e. Standard items delivery).
 * The flow from this state may lead to:
 * <p/>
 * Order in progress, Delivery inventory allocated state if inventory was allocated<p/>
 * <p/>
 * This integration test covers work of the following transitional handlers:<p/>
 * ProcessAllocationOrderEventHandlerImpl attempt to allocate the necessary quantity, if cannot allocate and exception is raised<p/>
 * <p/>
 *
 * User: denispavlov
 * Date: 19/05/2015
 * Time: 19:43
 */
public class ProcessAllocationOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private OrderEventHandler pendingHandler;
    private OrderEventHandler handler;

    private CustomerOrderService orderService;
    private CustomerOrderPaymentService paymentService;

    @Before
    public void setUp()  {
        super.setUp();
        pendingHandler = (OrderEventHandler) ctx().getBean("pendingOrderEventHandler");
        handler = (OrderEventHandler) ctx().getBean("processAllocationOrderEventHandler");
        orderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        paymentService = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
    }


    @Override
    protected CustomerOrder createTestOrder(final TestOrderType orderType, final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestOrder(orderType, pgLabel, onePhysicalDelivery);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // Make sure we are in progress state at this point
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        orderService.update(customerOrder);

        return customerOrder;

    }



    @Test
    public void testHandleStandardInventorySurplus() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleStandardInventorySufficient() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        debitInventoryAndAssert(WAREHOUSE_ID, "CC_TEST1", "7.00", "2.00", "0.00");

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "2.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleStandardInventoryEnoughForOneOrder() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        // create enough for 2 orders
        creditInventoryAndAssert(WAREHOUSE_ID, "CC_TEST2", "1.00", "2.00", "0.00");

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);
        createTestOrder(TestOrderType.STANDARD, label, false);

        // create enough only for one order
        debitInventoryAndAssert(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00", "2.00");

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "4.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "2.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleStandardInventoryInsufficient() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        // create enough only for one order
        debitInventoryAndAssert(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00", "1.00");

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        try {
            handler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP));
            fail("Should fail as the inventory has depleted");
        } catch (OrderItemAllocationException oiae) {
            // expected
        }

        // Need to check everything has rolled back!

        final CustomerOrder orderAfterException = orderService.findByReference(customerOrder.getOrdernum());

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "1.00");

        assertDeliveryStates(orderAfterException.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);

        // Authorisation
        assertSinglePaymentEntry(orderAfterException.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("689.74", orderAfterException.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(orderAfterException.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, orderAfterException.getOrderStatus());
    }



}
