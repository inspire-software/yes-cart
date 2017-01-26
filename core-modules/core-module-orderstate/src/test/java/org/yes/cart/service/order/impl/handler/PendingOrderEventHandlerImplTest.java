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
import org.yes.cart.payment.impl.TestPaymentGatewayImpl;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.OrderItemAllocationException;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * This test covers the starting flow for all orders that may lead to:
 * <p/>
 * Waiting state for orders that have offline PG<p/>
 * Waiting payment in case of AUTH/AUTH_CAPTURE returning processing state<p/>
 * Cancelled in case of AUTH/AUTH_CAPTURE returning failed state<p/>
 * Cancelled waiting payment in case if cancellation could not process all refund for previous AUTH_CAPTURE operations<p/>
 * In Progress with various delivery states for online payments in the event of successful payment<p/>
 * <p/>
 * <p/>
 * This integration test covers work of the following transitional handlers:<p/>
 * PendingOrderEventHandlerImpl staring point<p/>
 * PaymentOfflineOrderEventHandlerImpl leading to Waiting state (for CC manager to take payment offline)<p/>
 * PaymentProcessingOrderEventHandlerImpl leading to Waiting payment (to wait for confirmation callback)<p/>
 * CancelOrderWithRefundOrderEventHandlerImpl to void reservation if payment failed and ensure that possible successful
 * payments are refunded<p/>
 * PaymentOkOrderEventHandlerImpl triggering from successful payment and leading to delivery state update events:
 * ProcessAllocationWaitOrderEventHandlerImpl (for D1 Standard), ProcessTimeWaitOrderEventHandlerImpl (for D2 Preorder
 * and D5 Mixed), ProcessInventoryWaitOrderEventHandlerImpl (for D3 Back order), ReleaseToShipmentOrderEventHandlerImpl (for D4
 * Electronic). In case of D4 Online PG automatic CAPTURE operation is triggered (since there is no inventory processing)<p/>
 * <p/>
 * User: Denis Pavlov
 * Date: 15-May-2015
 * Time: 14:12:54
 */
public class PendingOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private OrderEventHandler handler;

    private CustomerOrderService orderService;
    private CustomerOrderPaymentService paymentService;

    @Before
    public void setUp()  {
        super.setUp();
        handler = (OrderEventHandler) ctx().getBean("pendingOrderEventHandler");
        orderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        paymentService = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
    }

    @Test
    public void testHandleStandardReserveFailedOnline() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        // deliberately remove all stock
        debitInventoryAndAssert(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00", "0.00");

        try {
            handler.handle(
                    new OrderEventImpl("", //evt.pending
                            customerOrder,
                            null,
                            Collections.EMPTY_MAP));
            fail("Handler must fail with exception");
        } catch (OrderItemAllocationException oiae) {
            // Ok we failed on reservation
        }

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");


        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ON_FULLFILMENT);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandleStandardReserveFailedOffline() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        // deliberately remove all stock
        debitInventoryAndAssert(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00", "0.00");

        try {
            handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP));
            fail("Handler must fail with exception");
        } catch (OrderItemAllocationException oiae) {
            // Ok we failed on reservation
        }

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");


        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ON_FULLFILMENT);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());

    }



    @Test
    public void testHandleStandardReserveFailedExternal() throws Exception {

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        // deliberately remove all stock
        debitInventoryAndAssert(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00", "0.00");

        try {
            handler.handle(
                    new OrderEventImpl("", //evt.pending
                            customerOrder,
                            null,
                            Collections.EMPTY_MAP));
            fail("Handler must fail with exception");
        } catch (OrderItemAllocationException oiae) {
            // Ok we failed on reservation
        }

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");


        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ON_FULLFILMENT);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());

    }



    @Test
    public void testHandleStandardPaymentOkOfflineAuth() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");


        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandlePreorderPaymentOkOfflineAuth() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleBackorderPaymentOkOfflineAuth() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.BACKORDER, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleElectronicPaymentOkOfflineAuth() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedSinglePaymentOkOfflineAuth() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertTrue(customerOrder.getDelivery().size() == 1);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedMultiPaymentOkOfflineAuth() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertTrue(customerOrder.getDelivery().size() > 1);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedFullMultiPaymentOkOfflineAuth() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() > 1);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleStandardPaymentOkOfflineCapture() throws Exception {

        String label = assertPgFeatures("prePaymentGateway", false, false, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");


        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandlePreorderPaymentOkOfflineCapture() throws Exception {

        String label = assertPgFeatures("prePaymentGateway", false, false, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleBackorderPaymentOkOfflineCapture() throws Exception {

        String label = assertPgFeatures("prePaymentGateway", false, false, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.BACKORDER, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleElectronicPaymentOkOfflineCapture() throws Exception {

        String label = assertPgFeatures("prePaymentGateway", false, false, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedSinglePaymentOkOfflineCapture() throws Exception {

        String label = assertPgFeatures("prePaymentGateway", false, false, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertTrue(customerOrder.getDelivery().size() == 1);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedMultiPaymentOkOfflineCapture() throws Exception {

        String label = assertPgFeatures("prePaymentGateway", false, false, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertTrue(customerOrder.getDelivery().size() > 1);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedFullMultiPaymentOkOfflineCapture() throws Exception {

        String label = assertPgFeatures("prePaymentGateway", false, false, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() > 1);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleStandardPaymentOkOnlineAuth() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandlePreorderPaymentOkOnlineAuth() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
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
    public void testHandleBackorderPaymentOkOnlineAuth() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.BACKORDER, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "84.77", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleElectronicPaymentOkOnlineAuth() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        // Authorisation + Capture and straight into in progress
        assertAuthCapturePaymentEntries(customerOrder.getOrdernum(), "444.95", Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("444.95", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleElectronicPaymentOkOnlineAuthPaymentFailedCapture() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT);

        // Authorisation + Capture and straight into in progress
        assertAuthCapturePaymentEntries(customerOrder.getOrdernum(), "444.95", Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedSinglePaymentOkOnlineAuth() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
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

        // Single Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedMultiPaymentOkOnlineAuth() throws Exception {

        configureTestPG(true, false);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});

        // Single Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1034.25", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

    }

    @Test
    public void testHandleMixedMultiPaymentOkOnlineAuthPerShipment() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});

        // Authorisation per delivery
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleFullMixedSinglePaymentOkOnlineAuth() throws Exception {

        configureTestPG(true, false);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 2); // Single mixed delivery  + Electronic
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.MIX_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);
        }});

        // Electronic delivery causes Capture
        assertAuthCapturePaymentEntries(customerOrder.getOrdernum(), "1445.66", Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1445.66", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleFullMixedSinglePaymentOkOnlineAuthPaymentFailedCapture() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 2); // Single mixed delivery  + Electronic
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.MIX_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT);
        }});

        // Electronic delivery causes Capture
        assertAuthCapturePaymentEntries(customerOrder.getOrdernum(), "1445.66", Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentOkOnlineAuth() throws Exception {

        configureTestPG(true, false);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
            put(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);
        }});

        // Electronic delivery causes Capture
        assertAuthCapturePaymentEntries(customerOrder.getOrdernum(), "1479.20", Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1479.20", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentOkOnlineAuthPaymentFailedCapture() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
            put(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT);
        }});

        // Electronic delivery causes Capture
        assertAuthCapturePaymentEntries(customerOrder.getOrdernum(), "1479.20", Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentOkOnlineAuthPerShipment() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
            put(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);
        }});

        // Electronic delivery causes Capture, other deliveries are only authorised
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77", "444.95", "444.95"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("444.95", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentOkOnlineAuthPerShipmentPaymentFailedCapture() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
            put(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT);
        }});

        // Electronic delivery causes Capture, other deliveries are only authorised
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                   "84.77",                    "444.95",                   "444.95"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.AUTH,        PaymentGateway.AUTH,        PaymentGateway.AUTH,        PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                Boolean.FALSE,              Boolean.FALSE,              Boolean.FALSE,              Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleStandardPaymentOkOnlineCapture() throws Exception {

        configureTestPG(false, true);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);

        // Capture
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandlePreorderPaymentOkOnlineCapture() throws Exception {

        configureTestPG(false, true);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));


        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // Capture
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("259.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleBackorderPaymentOkOnlineCapture() throws Exception {

        configureTestPG(false, true);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.BACKORDER, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);

        // Capture
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "84.77", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("84.77", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandleElectronicPaymentOkOnlineCapture() throws Exception {

        configureTestPG(false, true);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        // Capture and straight into in progress
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "444.95", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("444.95", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandleMixedSinglePaymentOkOnlineCapture() throws Exception {

        configureTestPG(false, true);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
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

        // Single Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1000.71", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedMultiPaymentOkOnlineCapture() throws Exception {

        configureTestPG(false, false);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});

        // Single Capture
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1034.25", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1034.25", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedMultiPaymentOkOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});

        // Capture per delivery
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE, Boolean.TRUE, Boolean.TRUE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1034.25", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }





    @Test
    public void testHandleFullMixedSinglePaymentOkOnlineCapture() throws Exception {

        configureTestPG(false, false);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 2); // Single mixed delivery  + Electronic
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.MIX_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);
        }});

        // Electronic delivery causes Capture
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1445.66", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1445.66", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentOkOnlineCapture() throws Exception {

        configureTestPG(false, false);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
            put(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);
        }});

        // Electronic delivery causes Capture
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1479.20", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1479.20", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleFullMixedMultiPaymentOkCaptureAuthPerShipment() throws Exception {

        configureTestPG(false, true);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
            put(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);
        }});

        // Electronic delivery causes Capture, other deliveries are only authorised
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77", "444.95"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1479.20", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleStandardPaymentOkNotSettledOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.UNSETTLED);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);

        // Unsettled Capture
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandlePreorderPaymentOkNotSettledOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.UNSETTLED);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));


        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // Unsettled Capture
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("259.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

    }




    @Test
    public void testHandleBackorderPaymentOkNotSettledOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.UNSETTLED);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.BACKORDER, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);

        // Unsettled Capture
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "84.77", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("84.77", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

    }




    @Test
    public void testHandleElectronicPaymentOkNotSettledOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.UNSETTLED);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        // Unsettled Capture and straight into in progress
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "444.95", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("444.95", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

    }



    @Test
    public void testHandleMixedMultiPaymentOkNotSettledOnlineCapture() throws Exception {

        configureTestPG(false, false, TestPaymentGatewayImpl.UNSETTLED);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});

        // Unsettled Single Capture
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1034.25", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1034.25", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedMultiPaymentOkNotSettledOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.UNSETTLED);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});

        // Unsettled Capture per delivery
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1034.25", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedMultiPaymentOkNotSettledPartialOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.UNSETTLED_NO + "259.74");

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});

        // Partial Unsettled Capture per delivery
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                       "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,    PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,      Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE,                  Boolean.TRUE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1034.25", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleMixedSinglePaymentOkNotSettledOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.UNSETTLED);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
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

        // Single Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1000.71", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleStandardPaymentProcessingOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandlePreorderPaymentProcessingOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleBackorderPaymentProcessingOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.BACKORDER, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "84.77", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleElectronicPaymentProcessingOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "444.95", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedSinglePaymentProcessingOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
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
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Single Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedMultiPaymentProcessingOnlineAuth() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Single Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1034.25", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());

    }



    @Test
    public void testHandleMixedMultiPaymentProcessingOnlineAuthPerShipment() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation per delivery
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedMultiPaymentProcessingPartialOnlineAuthPerShipment() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING_NO + "259.74");

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation per delivery
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                           "84.77"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.AUTH,                PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.FALSE,                      Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleFullMixedSinglePaymentProcessingOnlineAuth() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 2); // Single mixed delivery  + Electronic
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Single Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1445.66", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleFullMixedMultiPaymentProcessingOnlineAuth() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Single Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1479.20", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentProcessingOnlineAuthPerShipment() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Electronic delivery causes Capture, other deliveries are only authorised
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77", "444.95"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleFullMixedMultiPaymentProcessingPartialOnlineAuthPerShipment() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING_NO + "84.77");

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Electronic delivery causes Capture, other deliveries are only authorised
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                   "84.77",                            "444.95"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.AUTH,        PaymentGateway.AUTH,                PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.FALSE,              Boolean.FALSE,                      Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleStandardPaymentProcessingOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandlePreorderPaymentProcessingOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleBackorderPaymentProcessingOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.BACKORDER, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "84.77", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleElectronicPaymentProcessingOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "444.95", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedSinglePaymentProcessingOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
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
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Single Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedMultiPaymentProcessingOnlineCapture() throws Exception {

        configureTestPG(false, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Single Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1034.25", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());

    }



    @Test
    public void testHandleMixedMultiPaymentProcessingOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation per delivery
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedMultiPaymentProcessingPartialOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING_NO + "259.74");

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation per delivery
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                           "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE,                      Boolean.TRUE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("774.51", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedSinglePaymentProcessingOnlineCapture() throws Exception {

        configureTestPG(false, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 2); // Single mixed delivery  + Electronic
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Single Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1445.66", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentProcessingOnlineCapture() throws Exception {

        configureTestPG(false, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Single Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1479.20", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleFullMixedMultiPaymentProcessingOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Electronic delivery causes Capture, other deliveries are only authorised
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77", "444.95"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentProcessingPartialOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING_NO + "84.77");

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Electronic delivery causes Capture, other deliveries are only authorised
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                       "84.77",                            "444.95"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,    PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,      Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.TRUE,                   Boolean.FALSE,                      Boolean.TRUE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1394.43", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleStandardPaymentFailedOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.AUTH_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandlePreorderPaymentFailedOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.AUTH_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleBackorderPaymentFailedOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.AUTH_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.BACKORDER, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "84.77", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleElectronicPaymentFailedOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.AUTH_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "444.95", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedSinglePaymentFailedOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.AUTH_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 1); // Single mixed delivery
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Single Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedMultiPaymentFailedOnlineAuth() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.AUTH_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Single Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1034.25", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandleMixedMultiPaymentFailedOnlineAuthPerShipment() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.AUTH_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation per delivery
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_FAILED, Payment.PAYMENT_STATUS_FAILED, Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedMultiPaymentFailedPartialOnlineAuthPerShipment() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.AUTH_FAIL_NO + "259.74");

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation per delivery
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                      "259.74",                      "84.77",                        "689.74"),
                Arrays.asList(PaymentGateway.AUTH,           PaymentGateway.AUTH,           PaymentGateway.AUTH,            PaymentGateway.REVERSE_AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,     Payment.PAYMENT_STATUS_FAILED, Payment.PAYMENT_STATUS_FAILED,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                 Boolean.FALSE,                 Boolean.FALSE,                  Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedSinglePaymentFailedOnlineAuth() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.AUTH_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 2); // Single mixed delivery  + Electronic
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1445.66", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentFailedOnlineAuth() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.AUTH_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1479.20", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentFailedOnlineAuthPerShipment() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.AUTH_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation failed
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77", "444.95"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_FAILED, Payment.PAYMENT_STATUS_FAILED, Payment.PAYMENT_STATUS_FAILED, Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentFailedPartialOnlineAuthPerShipment() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.AUTH_FAIL_NO + "84.77");

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation failed for last one so we reverse all
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                   "84.77",                        "444.95",                       "689.74",                     "259.74",                         "444.95"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.AUTH,        PaymentGateway.AUTH,            PaymentGateway.AUTH,            PaymentGateway.REVERSE_AUTH,  PaymentGateway.REVERSE_AUTH,      PaymentGateway.REVERSE_AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_FAILED,  Payment.PAYMENT_STATUS_OK,      Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,        Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.FALSE,              Boolean.FALSE,                  Boolean.FALSE,                  Boolean.FALSE,                Boolean.FALSE,                    Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleStandardPaymentFailedOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandlePreorderPaymentFailedOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleBackorderPaymentFailedOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.BACKORDER, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "84.77", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleElectronicPaymentFailedOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "444.95", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedSinglePaymentFailedOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 1); // Single mixed delivery
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Single Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedMultiPaymentFailedOnlineCapture() throws Exception {

        configureTestPG(false, false, TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Single Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1034.25", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandleMixedMultiPaymentFailedOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation per delivery
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_FAILED, Payment.PAYMENT_STATUS_FAILED, Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedMultiPaymentFailedPartialOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL_NO + "259.74");

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Capture per delivery - if at least one capture is successful we need to refund properly via cancellation, so we move to waiting payment
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                      "259.74",                      "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,   PaymentGateway.AUTH_CAPTURE,   PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,     Payment.PAYMENT_STATUS_FAILED, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                  Boolean.FALSE,                 Boolean.TRUE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("774.51", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleFullMixedSinglePaymentFailedOnlineCapture() throws Exception {

        configureTestPG(false, false, TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 2); // Single mixed delivery  + Electronic
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Capture
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1445.66", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentFailedOnlineCapture() throws Exception {

        configureTestPG(false, false, TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Capture
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1479.20", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_FAILED, false);
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentFailedOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Capture failed
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77", "444.95"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_FAILED, Payment.PAYMENT_STATUS_FAILED, Payment.PAYMENT_STATUS_FAILED, Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleFullMixedMultiPaymentFailedPartialOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL_NO + "84.77");

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Capture per delivery - if at least one capture is successful we need to refund properly via cancellation, so we move to waiting payment
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                   "84.77",                        "444.95"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,PaymentGateway.AUTH_CAPTURE,    PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_FAILED,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.TRUE,               Boolean.FALSE,                  Boolean.TRUE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1394.43", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleStandardPaymentOkOfflineAuthSub() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestSubOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");


        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleStandardPaymentOkOnlineAuthSub() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestSubOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


}
