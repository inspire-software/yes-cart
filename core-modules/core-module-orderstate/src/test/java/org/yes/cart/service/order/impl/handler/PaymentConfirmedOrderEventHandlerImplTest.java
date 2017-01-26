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
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This test covers flow for from Waiting state where order is waiting for offline payment to
 * be taken. The flow from this state may lead to:
 * <p/>
 * In Progress with various delivery states for online payments in the event of successful payment<p/>
 * <p/>
 * This integration test covers work of the following transitional handlers:<p/>
 * PaymentConfirmedOrderEventHandlerImpl starting point<p/>
 * PaymentOkOrderEventHandlerImpl triggering from successful payment and leading to delivery state update events:
 * ProcessAllocationWaitOrderEventHandlerImpl (for D1 Standard), ProcessTimeWaitOrderEventHandlerImpl (for D2 Preorder
 * and D5 Mixed), ProcessInventoryWaitOrderEventHandlerImpl (for D3 Back order), ReleaseToShipmentOrderEventHandlerImpl (for D4
 * Electronic). In case of D4 Online PG automatic CAPTURE operation is triggered (since there is no inventory processing)<p/>
 * <p/>
 *
 * User: denispavlov
 * Date: 19/05/2015
 * Time: 19:43
 */
public class PaymentConfirmedOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private OrderEventHandler pendingHandler;
    private OrderEventHandler handler;

    private CustomerOrderService orderService;
    private CustomerOrderPaymentService paymentService;


    @Before
    public void setUp()  {
        super.setUp();
        pendingHandler = (OrderEventHandler) ctx().getBean("pendingOrderEventHandler");
        handler = (OrderEventHandler) ctx().getBean("paymentConfirmedOrderEventHandler");
        orderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        paymentService = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
    }


    @Override
    protected CustomerOrder createTestOrder(final TestOrderType orderType, final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestOrder(orderType, pgLabel, onePhysicalDelivery);

        // Make sure we are in waiting state at this point
        return prepareTestOrder(customerOrder, CustomerOrder.ORDER_STATUS_WAITING);

    }

    @Override
    protected CustomerOrder createTestSubOrder(final TestOrderType orderType, final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestSubOrder(orderType, pgLabel, onePhysicalDelivery);

        // Make sure we are in waiting state at this point
        return prepareTestOrder(customerOrder, CustomerOrder.ORDER_STATUS_WAITING);

    }

    protected CustomerOrder createTestOrderOnline(final TestOrderType orderType, final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestOrder(orderType, pgLabel, onePhysicalDelivery);

        // Make sure we are in processing state at this point
        return prepareTestOrder(customerOrder, CustomerOrder.ORDER_STATUS_WAITING_PAYMENT);

    }

    protected CustomerOrder createTestSubOrderOnline(final TestOrderType orderType, final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestSubOrder(orderType, pgLabel, onePhysicalDelivery);

        // Make sure we are in processing state at this point
        return prepareTestOrder(customerOrder, CustomerOrder.ORDER_STATUS_WAITING_PAYMENT);

    }

    private CustomerOrder prepareTestOrder(final CustomerOrder customerOrder, final String expectedStatus) throws OrderException {

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        assertEquals(expectedStatus, customerOrder.getOrderStatus());

        orderService.update(customerOrder);

        return customerOrder;
    }

    @Test
    public void testHandleStandardPaymentProcessingOnline() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrderOnline(TestOrderType.STANDARD, label, false);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertFalse(handler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
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
    public void testHandleStandardPaymentOkOfflineAuth() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");


        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);

        // CC manager approves the order, so we create AUTH
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandlePreorderPaymentOkOfflineAuth() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // CC manager approves the order, so we create AUTH
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleBackorderPaymentOkOfflineAuth() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.BACKORDER, label, false);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);

        // CC manager approves the order, so we create AUTH
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "84.77", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleElectronicPaymentOkOfflineAuth() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS_WAITING_PAYMENT);

        // CC manager approves the order, so we create AUTH
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "444.95", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedSinglePaymentOkOfflineAuth() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
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
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // CC manager approves the order, so we create AUTH
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedMultiPaymentOkOfflineAuth() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
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
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP,          CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP,         CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP,    CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});

        // CC manager approves the order, so we create AUTH
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
    public void testHandleMixedFullMultiPaymentOkOfflineAuth() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
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
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP,          CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP,         CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP,    CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
            put(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP,        CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS_WAITING_PAYMENT);
        }});

        // CC manager approves the order, so we create AUTH
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                   "84.77",                    "444.95"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.AUTH,        PaymentGateway.AUTH,        PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.FALSE,              Boolean.FALSE,              Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleStandardPaymentOkOfflineCapture() throws Exception {

        String label = assertPgFeatures("prePaymentGateway", false, false, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");


        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);

        // CC manager approves the order, so we create AUTH
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandlePreorderPaymentOkOfflineCapture() throws Exception {

        String label = assertPgFeatures("prePaymentGateway", false, false, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // CC manager approves the order, so we create AUTH
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("259.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleBackorderPaymentOkOfflineCapture() throws Exception {

        String label = assertPgFeatures("prePaymentGateway", false, false, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.BACKORDER, label, false);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);

        // CC manager approves the order, so we create AUTH
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "84.77", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("84.77", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleElectronicPaymentOkOfflineCapture() throws Exception {

        String label = assertPgFeatures("prePaymentGateway", false, false, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        // CC manager approves the order, so we create AUTH
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "444.95", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("444.95", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedSinglePaymentOkOfflineCapture() throws Exception {

        String label = assertPgFeatures("prePaymentGateway", false, false, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
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
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // CC manager approves the order, so we create AUTH
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1000.71", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedMultiPaymentOkOfflineCapture() throws Exception {

        String label = assertPgFeatures("prePaymentGateway", false, false, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
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
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP,          CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP,         CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP,    CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});


        // CC manager approves the order, so we create AUTH
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1034.25", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1034.25", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedFullMultiPaymentOkOfflineCapture() throws Exception {

        String label = assertPgFeatures("prePaymentGateway", false, false, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
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
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP,          CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP,         CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP,    CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
            put(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP,        CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);
        }});

        // CC manager approves the order, so we create AUTH
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1479.20", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1479.20", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleStandardPaymentProcessingOnlineSub() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestSubOrderOnline(TestOrderType.STANDARD, label, false);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertFalse(handler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
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
    public void testHandleMixedMultiPaymentOkOfflineAuthSub() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestSubOrder(TestOrderType.MIXED, label, false);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
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
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP,          CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP,         CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP,    CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});

        // CC manager approves the order, so we create AUTH
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                   "84.77"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.AUTH,        PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.FALSE,              Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


}
