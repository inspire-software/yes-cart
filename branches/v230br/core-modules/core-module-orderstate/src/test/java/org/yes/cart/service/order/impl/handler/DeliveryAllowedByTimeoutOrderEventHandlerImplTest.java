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
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Test covers flow from Order in progress, Delivery waiting for date flow (i.e. Preordered items delivery).
 * The flow from this state may lead to:
 * <p/>
 * Order in progress, Delivery waiting for date state if item is not yet available<p/>
 * Order in progress, Delivery waiting for inventory state if no inventory available but the item is available<p/>
 * Order in progress, Delivery inventory allocated state if inventory was allocated<p/>
 * <p/>
 * This integration test covers work of the following transitional handlers:<p/>
 * DeliveryAllowedByTimeoutOrderEventHandlerImpl starting point<p/>
 * DeliveryAllowedByInventoryOrderEventHandlerImpl triggering from successful check for product available date<p/>
 * <p/>
 *
 * User: denispavlov
 * Date: 19/05/2015
 * Time: 19:43
 */
public class DeliveryAllowedByTimeoutOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private OrderEventHandler pendingHandler;
    private OrderEventHandler handler;

    private CustomerOrderService orderService;
    private CustomerOrderPaymentService paymentService;

    @Before
    public void setUp()  {
        super.setUp();
        pendingHandler = (OrderEventHandler) ctx().getBean("pendingOrderEventHandler");
        handler = (OrderEventHandler) ctx().getBean("deliveryAllowedByTimeoutOrderEventHandler");
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

        return customerOrder;

    }


    @Test
    public void testHandlePreorderInventorySurplus() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        // make sure we have just enough
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        changeAvailabilityDatesAndAssert("CC_TEST6", new Date(0L), true);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.allowed.timeout
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "497.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandlePreorderInventorySufficient() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        // make sure we have just enough
        debitInventoryAndAssert(WAREHOUSE_ID, "CC_TEST6", "497.00", "3.00", "0.00");

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "3.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        changeAvailabilityDatesAndAssert("CC_TEST6", new Date(0L), true);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.allowed.timeout
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandlePreorderInventoryEnoughForOneOrder() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        // make sure we have just enough
        debitInventoryAndAssert(WAREHOUSE_ID, "CC_TEST6", "497.00", "3.00", "0.00");

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);
        createTestOrder(TestOrderType.PREORDER, label, false); // second order to double reserve

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "3.00", "6.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        changeAvailabilityDatesAndAssert("CC_TEST6", new Date(0L), true);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.allowed.timeout
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "0.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandlePreorderInventoryInsufficient() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        // make sure we have just enough
        debitInventoryAndAssert(WAREHOUSE_ID, "CC_TEST6", "499.00", "1.00", "0.00");

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "1.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        changeAvailabilityDatesAndAssert("CC_TEST6", new Date(0L), true);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.allowed.timeout
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "1.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandlePreorderInventoryNotYetAvailable() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertFalse(handler.handle(
                new OrderEventImpl("", //evt.delivery.allowed.timeout
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedSingleInventorySurplus() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        // make sure we have just enough
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertTrue(customerOrder.getDelivery().size() == 1); // Single mixed delivery
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        changeAvailabilityDatesAndAssert("CC_TEST6", new Date(0L), true);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.allowed.timeout
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedSingleInventorySufficient() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        // make sure we have just enough
        debitInventoryAndAssert(WAREHOUSE_ID, "CC_TEST6", "497.00", "3.00", "0.00");

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "3.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertTrue(customerOrder.getDelivery().size() == 1); // Single mixed delivery
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        changeAvailabilityDatesAndAssert("CC_TEST6", new Date(0L), true);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.allowed.timeout
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "3.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedSingleInventoryEnoughForOneOrder() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        // make sure we have just enough
        debitInventoryAndAssert(WAREHOUSE_ID, "CC_TEST6", "497.00", "3.00", "0.00");

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);
        createTestOrder(TestOrderType.PREORDER, label, false); // second order to double reserve

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "3.00", "6.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");


        assertTrue(customerOrder.getDelivery().size() == 1); // Single mixed delivery
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        changeAvailabilityDatesAndAssert("CC_TEST6", new Date(0L), true);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.allowed.timeout
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "3.00", "6.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedSingleInventoryInsufficient() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        // make sure we have just enough
        debitInventoryAndAssert(WAREHOUSE_ID, "CC_TEST6", "499.00", "1.00", "0.00");

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "1.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");


        assertTrue(customerOrder.getDelivery().size() == 1); // Single mixed delivery
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        changeAvailabilityDatesAndAssert("CC_TEST6", new Date(0L), true);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.allowed.timeout
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "1.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedSingleInventoryNotYetAvailable() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        // make sure we have just enough
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");


        assertTrue(customerOrder.getDelivery().size() == 1); // Single mixed delivery
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertFalse(handler.handle(
                new OrderEventImpl("", //evt.delivery.allowed.timeout
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");


        assertTrue(customerOrder.getDelivery().size() == 1); // Single mixed delivery
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

}
