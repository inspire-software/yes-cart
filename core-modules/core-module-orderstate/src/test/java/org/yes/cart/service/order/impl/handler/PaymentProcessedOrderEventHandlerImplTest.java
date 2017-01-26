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
 * This test covers the flow from Waiting Payment state whereby AUTH or AUTH_CAPTURE where in PROCESSING
 * state, which can occur when there is a delay in communication between PG and the Bank. There could be
 * several processing states until Bank send data that funds where captured (e.g. fraud check).
 * The flow from this state may lead to:
 * <p/>
 * Waiting payment in case of AUTH/AUTH_CAPTURE returning processing state<p/>
 * Cancelled in case of AUTH/AUTH_CAPTURE returning failed state<p/>
 * Cancelled waiting payment in case if cancellation could not process all refund for previous AUTH_CAPTURE operations<p/>
 * In Progress with various delivery states for online payments in the event of successful payment<p/>
 * <p/>
 * <p/>
 * This integration test covers work of the following transitional handlers:<p/>
 * PaymentProcessedOrderEventHandlerImpl staring point (it also handles PROCESSING notifications)<p/>
 * CancelOrderWithRefundOrderEventHandlerImpl to void reservation if payment failed and ensure that possible successful
 * payments are refunded<p/>
 * PaymentOkOrderEventHandlerImpl triggering from successful payment and leading to delivery state update events:
 * ProcessAllocationWaitOrderEventHandlerImpl (for D1 Standard), ProcessTimeWaitOrderEventHandlerImpl (for D2 Preorder
 * and D5 Mixed), ProcessInventoryWaitOrderEventHandlerImpl (for D3 Back order), ReleaseToShipmentOrderEventHandlerImpl (for D4
 * Electronic). In case of D4 Online PG automatic CAPTURE operation is triggered (since there is no inventory processing)<p/>
 * <p/>
 * User: denispavlov
 * Date: 27/04/2015
 * Time: 23:40
 */
public class PaymentProcessedOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private OrderEventHandler pendingHandler;
    private OrderEventHandler handler;

    private CustomerOrderService orderService;
    private CustomerOrderPaymentService paymentService;

    @Before
    public void setUp()  {
        super.setUp();
        pendingHandler = (OrderEventHandler) ctx().getBean("pendingOrderEventHandler");
        handler = (OrderEventHandler) ctx().getBean("paymentProcessedOrderEventHandler");
        orderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        paymentService = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
    }


    @Override
    protected CustomerOrder createTestOrder(final TestOrderType orderType, final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestOrder(orderType, pgLabel, onePhysicalDelivery);

        // Make sure we are in processing state at this point
        return prepareTestOrder(customerOrder, CustomerOrder.ORDER_STATUS_WAITING_PAYMENT);

    }

    @Override
    protected CustomerOrder createTestSubOrder(final TestOrderType orderType, final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestSubOrder(orderType, pgLabel, onePhysicalDelivery);

        // Make sure we are in processing state at this point
        return prepareTestOrder(customerOrder, CustomerOrder.ORDER_STATUS_WAITING_PAYMENT);

    }


    protected CustomerOrder createTestOrderOffline(final TestOrderType orderType, final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestOrder(orderType, pgLabel, onePhysicalDelivery);

        // Make sure we are in waiting state at this point
        return prepareTestOrder(customerOrder, CustomerOrder.ORDER_STATUS_WAITING);
    }

    protected CustomerOrder createTestSubOrderOffline(final TestOrderType orderType, final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestSubOrder(orderType, pgLabel, onePhysicalDelivery);

        // Make sure we are in waiting state at this point
        return prepareTestOrder(customerOrder, CustomerOrder.ORDER_STATUS_WAITING);
    }


    private CustomerOrder prepareTestOrder(final CustomerOrder customerOrder, final String expectedStatus) throws OrderException {

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        assertEquals(expectedStatus, customerOrder.getOrderStatus());

        orderService.update(customerOrder);

        return customerOrder;
    }



    @Test
    public void testHandleStandardPaymentProcessingOffline() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrderOffline(TestOrderType.STANDARD, label, false);

        // Authorisation
        assertNoPaymentEntries(customerOrder.getOrdernum());

        assertFalse(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleStandardPaymentProcessingOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                             "689.74"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandlePreorderPaymentProcessingOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("259.74", "259.74"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE));
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleBackorderPaymentProcessingOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.BACKORDER, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "84.77", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("84.77",                              "84.77"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleElectronicPaymentProcessingOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "444.95", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("444.95",                             "444.95"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedSinglePaymentProcessingOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1000.71",                            "1000.71"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedMultiPaymentProcessingOnlineAuth() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1034.25", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1034.25",                            "1034.25"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandleMixedMultiPaymentProcessingOnlineAuthPerShipment() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
                Arrays.asList("689.74",                             "259.74",                           "84.77",
                              "689.74",                             "259.74",                           "84.77"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH,                PaymentGateway.AUTH,
                              PaymentGateway.AUTH,                  PaymentGateway.AUTH,                PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING,
                              Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE,
                              Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleMixedMultiPaymentProcessingPartialOnlineAuthPerShipment() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING_NO + "259.74");

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        // Authorisation per delivery
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
                Arrays.asList("689.74",                     "259.74",                           "84.77",                    "259.74"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.AUTH,                PaymentGateway.AUTH,        PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE,                 Boolean.FALSE,                      Boolean.FALSE,               Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleFullMixedSinglePaymentProcessingOnlineAuth() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, true);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1445.66", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1445.66",                            "1445.66"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleFullMixedMultiPaymentProcessingOnlineAuth() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1479.20", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1479.20",                            "1479.20"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentProcessingOnlineAuthPerShipment() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77", "444.95"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
                Arrays.asList("689.74",                             "259.74",                           "84.77",                            "444.95",
                              "689.74",                             "259.74",                           "84.77",                            "444.95"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH,                PaymentGateway.AUTH,                PaymentGateway.AUTH,
                              PaymentGateway.AUTH,                  PaymentGateway.AUTH,                PaymentGateway.AUTH,                PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING,
                              Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE,                      Boolean.FALSE,
                              Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE,                      Boolean.FALSE)
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

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77", "444.95"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);
            put(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);
        }});

        // Electronic delivery causes Capture, other deliveries are only authorised
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                   "84.77",                            "444.95",                   "84.77"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.AUTH,        PaymentGateway.AUTH,                PaymentGateway.AUTH,        PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE,                Boolean.FALSE,              Boolean.FALSE,                      Boolean.FALSE,              Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleStandardPaymentFailedOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                             "689.74"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandlePreorderPaymentFailedOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("259.74",                             "259.74"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleBackorderPaymentFailedOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.BACKORDER, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "84.77", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("84.77",                              "84.77"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleElectronicPaymentFailedOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "444.95", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("444.95",                             "444.95"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedSinglePaymentFailedOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1000.71",                            "1000.71"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedMultiPaymentFailedOnlineAuth() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1034.25", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1034.25",                            "1034.25"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }

    @Test
    public void testHandleMixedMultiPaymentFailedOnlineAuthPerShipment() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
                Arrays.asList("689.74",                             "259.74",                           "84.77",
                              "689.74",                             "259.74",                           "84.77"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH,                PaymentGateway.AUTH,
                              PaymentGateway.AUTH,                  PaymentGateway.AUTH,                PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING,
                              Payment.PAYMENT_STATUS_FAILED,        Payment.PAYMENT_STATUS_FAILED,      Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE,
                              Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedMultiPaymentFailedPartialOnlineAuthPerShipment() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING_NO + "259.74");

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        // Authorisation per delivery
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING_NO + "259.74");
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
                Arrays.asList("689.74",                     "259.74",                           "84.77",
                              "689.74",                     "259.74",                           "84.77"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.AUTH,                PaymentGateway.AUTH,
                              PaymentGateway.REVERSE_AUTH,  PaymentGateway.AUTH,                PaymentGateway.REVERSE_AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_FAILED,      Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.FALSE,                      Boolean.FALSE,
                              Boolean.FALSE,                Boolean.FALSE,                      Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleFullMixedSinglePaymentFailedOnlineAuth() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, true);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1445.66", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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

        // Single Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1445.66",                            "1445.66"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleFullMixedMultiPaymentFailedOnlineAuth() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1479.20", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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

        // Single Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1479.20",                            "1479.20"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleFullMixedMultiPaymentFailedOnlineAuthPerShipment() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77", "444.95"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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

        // Electronic delivery causes Capture, other deliveries are only authorised
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                             "259.74",                           "84.77",                            "444.95",
                              "689.74",                             "259.74",                           "84.77",                            "444.95"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH,                PaymentGateway.AUTH,                PaymentGateway.AUTH,
                              PaymentGateway.AUTH,                  PaymentGateway.AUTH,                PaymentGateway.AUTH,                PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING,
                              Payment.PAYMENT_STATUS_FAILED,        Payment.PAYMENT_STATUS_FAILED,      Payment.PAYMENT_STATUS_FAILED,      Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE,                      Boolean.FALSE,
                              Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE,                      Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleFullMixedMultiPaymentFailedPartialOnlineAuthPerShipment() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING_NO + "84.77");

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77", "444.95"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING_NO + "84.77");
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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

        // Electronic delivery causes Capture, other deliveries are only authorised
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                   "84.77",                            "444.95",
                              "689.74",                     "259.74",                   "84.77",                            "444.95"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.AUTH,        PaymentGateway.AUTH,                PaymentGateway.AUTH,
                              PaymentGateway.REVERSE_AUTH,  PaymentGateway.REVERSE_AUTH,PaymentGateway.AUTH,                PaymentGateway.REVERSE_AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_FAILED,      Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.FALSE,              Boolean.FALSE,                      Boolean.FALSE,
                              Boolean.FALSE,                Boolean.FALSE,              Boolean.FALSE,                      Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleStandardPaymentFailedOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                             "689.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandlePreorderPaymentFailedOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("259.74",                             "259.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleBackorderPaymentFailedOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.BACKORDER, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "84.77", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("84.77",                              "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleElectronicPaymentFailedOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "444.95", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("444.95",                             "444.95"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedSinglePaymentFailedOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1000.71",                            "1000.71"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedMultiPaymentFailedOnlineCapture() throws Exception {

        configureTestPG(false, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1034.25", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1034.25",                            "1034.25"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }

    @Test
    public void testHandleMixedMultiPaymentFailedOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
                Arrays.asList("689.74",                             "259.74",                           "84.77",
                              "689.74",                             "259.74",                           "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE,
                              PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING,
                              Payment.PAYMENT_STATUS_FAILED,        Payment.PAYMENT_STATUS_FAILED,      Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE,
                              Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedMultiPaymentFailedPartialOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING_NO + "259.74");

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        // Authorisation per delivery
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE, Boolean.FALSE, Boolean.TRUE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("774.51", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING_NO + "259.74");
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
                Arrays.asList("689.74",                     "259.74",                           "84.77",
                              "689.74",                     "259.74",                           "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE,
                              PaymentGateway.REFUND,        PaymentGateway.AUTH_CAPTURE,        PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_FAILED,      Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE,                      Boolean.TRUE,
                              Boolean.FALSE,                Boolean.FALSE,                      Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedMultiPaymentFailedPartialOnlineCapturePerShipmentRefundFailed() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING_NO + "259.74");

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        // Authorisation per delivery
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                           "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE,                      Boolean.TRUE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("774.51", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING_NO + "259.74");
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.REFUND_FAIL_NO + "84.77");

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
                Arrays.asList("689.74",                     "259.74",                           "84.77",
                              "689.74",                     "259.74",                           "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE,
                              PaymentGateway.REFUND,        PaymentGateway.AUTH_CAPTURE,        PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_FAILED,      Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE,                      Boolean.TRUE,
                              Boolean.FALSE,                Boolean.FALSE,                      Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("84.77", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleFullMixedSinglePaymentFailedOnlineCapture() throws Exception {

        configureTestPG(false, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, true);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1445.66", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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

        // Single Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1445.66",                            "1445.66"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleFullMixedMultiPaymentFailedOnlineCapture() throws Exception {

        configureTestPG(false, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1479.20", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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

        // Single Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1479.20",                            "1479.20"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleFullMixedMultiPaymentFailedOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77", "444.95"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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

        // Electronic delivery causes Capture, other deliveries are only authorised
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                             "259.74",                           "84.77",                            "444.95",
                              "689.74",                             "259.74",                           "84.77",                            "444.95"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE,
                              PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING,
                              Payment.PAYMENT_STATUS_FAILED,        Payment.PAYMENT_STATUS_FAILED,      Payment.PAYMENT_STATUS_FAILED,      Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE,                      Boolean.FALSE,
                              Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE,                      Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleFullMixedMultiPaymentPartialOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING_NO + "84.77");

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77", "444.95"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1394.43", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING_NO + "84.77");
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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

        // Electronic delivery causes Capture, other deliveries are only authorised
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                   "84.77",                            "444.95",
                              "689.74",                     "259.74",                   "84.77",                            "444.95"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE,
                              PaymentGateway.REFUND,        PaymentGateway.REFUND,      PaymentGateway.AUTH_CAPTURE,        PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_FAILED,      Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.TRUE,               Boolean.FALSE,                      Boolean.TRUE,
                              Boolean.FALSE,                Boolean.FALSE,              Boolean.FALSE,                      Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMulti2PaymentPartialOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING_NO + "84.77");

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                   "84.77",                            "444.95"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.TRUE,               Boolean.FALSE,                      Boolean.TRUE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1394.43", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        // Do one more processing update
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));


        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING_NO + "84.77");
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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

        // Electronic delivery causes Capture, other deliveries are only authorised
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                   "84.77",                            "444.95",
                              "689.74",                     "259.74",                   "84.77",                            "444.95",
                                                                                        "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE,
                              PaymentGateway.REFUND,        PaymentGateway.REFUND,      PaymentGateway.AUTH_CAPTURE,        PaymentGateway.REFUND,
                                                                                        PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK,
                                                                                        Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.TRUE,                 Boolean.TRUE,               Boolean.FALSE,                      Boolean.TRUE,
                              Boolean.FALSE,                Boolean.FALSE,              Boolean.FALSE,                      Boolean.FALSE,
                                                                                        Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentPartialOnlineCapturePerShipmentRefundFailed() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING_NO + "84.77");

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                   "84.77",                            "444.95"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.TRUE,               Boolean.FALSE,                      Boolean.TRUE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1394.43", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING_NO + "84.77");
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.REFUND_FAIL_NO + "259.74");

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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

        // Electronic delivery causes Capture, other deliveries are only authorised
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                       "84.77",                            "444.95",
                              "689.74",                     "259.74",                       "84.77",                            "444.95"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,    PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE,
                              PaymentGateway.REFUND,        PaymentGateway.REFUND,          PaymentGateway.AUTH_CAPTURE,        PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,      Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_FAILED,  Payment.PAYMENT_STATUS_FAILED,      Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.TRUE,                   Boolean.FALSE,                      Boolean.TRUE,
                              Boolean.FALSE,                Boolean.FALSE,                  Boolean.FALSE,                      Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("259.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleFullMixedMulti2PaymentPartialOnlineCapturePerShipmentRefundFailed() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING_NO + "84.77");

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                   "84.77",                            "444.95"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.TRUE,               Boolean.FALSE,                      Boolean.TRUE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1394.43", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        // Do one more processing update
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING_NO + "84.77");
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.REFUND_FAIL_NO + "259.74");

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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

        // Electronic delivery causes Capture, other deliveries are only authorised
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                       "84.77",                            "444.95",
                              "689.74",                     "259.74",                       "84.77",                            "444.95",
                                                                                            "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,    PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE,
                              PaymentGateway.REFUND,        PaymentGateway.REFUND,          PaymentGateway.AUTH_CAPTURE,        PaymentGateway.REFUND,
                                                                                            PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,      Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_FAILED,  Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK,
                                                                                            Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.TRUE,                 Boolean.TRUE,                   Boolean.FALSE,                      Boolean.TRUE,
                              Boolean.FALSE,                Boolean.FALSE,                  Boolean.FALSE,                      Boolean.FALSE,
                                                                                            Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("259.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }






    @Test
    public void testHandleStandardPaymentOkOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                             "689.74"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandlePreorderPaymentOkOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("259.74",                             "259.74"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleBackorderPaymentOkOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.BACKORDER, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "84.77", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("84.77",                              "84.77"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleElectronicPaymentOkOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "444.95", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, true);
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        // Authorisation + Capture and straight into in progress
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("444.95",                             "444.95",                   "444.95"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH,        PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,              Boolean.TRUE));
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("444.95", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleElectronicPaymentOkOnlineAuthPaymentFailedCapture() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "444.95", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT);

        // Authorisation + Capture and straight into in progress
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("444.95",                             "444.95",                   "444.95"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH,        PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,              Boolean.FALSE));
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedSinglePaymentOkOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1000.71",                            "1000.71"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedMultiPaymentOkOnlineAuth() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1034.25", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1034.25",                            "1034.25"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

    }

    @Test
    public void testHandleMixedMultiPaymentOkOnlineAuthPerShipment() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
                Arrays.asList("689.74",                             "259.74",                           "84.77",
                              "689.74",                             "259.74",                           "84.77"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH,                PaymentGateway.AUTH,
                              PaymentGateway.AUTH,                  PaymentGateway.AUTH,                PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,            Payment.PAYMENT_STATUS_OK,          Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE,
                              Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleFullMixedSinglePaymentOkOnlineAuth() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, true);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1445.66", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1445.66",                            "1445.66",                  "1445.66"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH,        PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,              Boolean.TRUE));
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1445.66", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleFullMixedSinglePaymentOkOnlineAuthPaymentFailedCapture() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, true);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1445.66", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1445.66",                            "1445.66",                  "1445.66"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH,        PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,              Boolean.FALSE));
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentOkOnlineAuth() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1479.20", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1479.20",                            "1479.20",                  "1479.20"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH,        PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,              Boolean.TRUE));
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1479.20", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentOkOnlineAuthPaymentFailedCapture() throws Exception {

        configureTestPG(true, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1479.20", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1479.20",                            "1479.20",                  "1479.20"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH,        PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,              Boolean.FALSE));
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentOkOnlineAuthPerShipment() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77", "444.95"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
                Arrays.asList("689.74",                             "259.74",                           "84.77",                            "444.95",
                              "689.74",                             "259.74",                           "84.77",                            "444.95",
                                                                                                                                            "444.95"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH,                PaymentGateway.AUTH,                PaymentGateway.AUTH,
                              PaymentGateway.AUTH,                  PaymentGateway.AUTH,                PaymentGateway.AUTH,                PaymentGateway.AUTH,
                                                                                                                                            PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING,
                              Payment.PAYMENT_STATUS_OK,            Payment.PAYMENT_STATUS_OK,          Payment.PAYMENT_STATUS_OK,          Payment.PAYMENT_STATUS_OK,
                                                                                                                                            Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE,                      Boolean.FALSE,
                              Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE,                      Boolean.FALSE,
                                                                                                                                            Boolean.TRUE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("444.95", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentOkOnlineAuthPerShipmentPaymentFailedCapture() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77", "444.95"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
                Arrays.asList("689.74",                             "259.74",                           "84.77",                            "444.95",
                              "689.74",                             "259.74",                           "84.77",                            "444.95",
                                                                                                                                            "444.95"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH,                PaymentGateway.AUTH,                PaymentGateway.AUTH,
                              PaymentGateway.AUTH,                  PaymentGateway.AUTH,                PaymentGateway.AUTH,                PaymentGateway.AUTH,
                                                                                                                                            PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING,
                              Payment.PAYMENT_STATUS_OK,            Payment.PAYMENT_STATUS_OK,          Payment.PAYMENT_STATUS_OK,          Payment.PAYMENT_STATUS_OK,
                                                                                                                                            Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE,                      Boolean.FALSE,
                              Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE,                      Boolean.FALSE,
                                                                                                                                            Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleStandardPaymentOkOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);

        // Capture
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                             "689.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.TRUE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandlePreorderPaymentOkOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));


        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // Capture
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("259.74",                             "259.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.TRUE));
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("259.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleBackorderPaymentOkOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.BACKORDER, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "84.77", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);

        // Capture
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("84.77",                              "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.TRUE));
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("84.77", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandleElectronicPaymentOkOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "444.95", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        // Capture and straight into in progress
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("444.95",                             "444.95"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.TRUE));
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("444.95", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandleMixedSinglePaymentOkOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1000.71",                            "1000.71"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.TRUE));
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1000.71", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedMultiPaymentOkOnlineCapture() throws Exception {

        configureTestPG(false, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1034.25", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1034.25",                            "1034.25"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.TRUE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1034.25", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedMultiPaymentOkOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
                Arrays.asList("689.74",                             "259.74",                           "84.77",
                              "689.74",                             "259.74",                           "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE,
                              PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,            Payment.PAYMENT_STATUS_OK,          Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.TRUE,                         Boolean.TRUE,                       Boolean.TRUE,
                              Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1034.25", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }





    @Test
    public void testHandleFullMixedSinglePaymentOkOnlineCapture() throws Exception {

        configureTestPG(false, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, true);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1445.66", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1445.66",                            "1445.66"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.TRUE));
        assertEquals("1445.66", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1445.66", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentOkOnlineCapture() throws Exception {

        configureTestPG(false, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1479.20", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1479.20",                            "1479.20"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.TRUE));
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1479.20", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleFullMixedMultiPaymentOkCaptureAuthPerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77", "444.95"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
                Arrays.asList("689.74",                             "259.74",                               "84.77",                                "444.95",
                              "689.74",                             "259.74",                               "84.77",                                "444.95"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE,            PaymentGateway.AUTH_CAPTURE,            PaymentGateway.AUTH_CAPTURE,
                              PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE,            PaymentGateway.AUTH_CAPTURE,            PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING,      Payment.PAYMENT_STATUS_PROCESSING,      Payment.PAYMENT_STATUS_PROCESSING,
                              Payment.PAYMENT_STATUS_OK,            Payment.PAYMENT_STATUS_OK,              Payment.PAYMENT_STATUS_OK,              Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,                          Boolean.FALSE,                          Boolean.FALSE,
                              Boolean.TRUE,                         Boolean.TRUE,                           Boolean.TRUE,                           Boolean.TRUE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1479.20", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleStandardPaymentOkNotSettledOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.UNSETTLED);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);

        // Unsettled Capture
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                             "689.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandlePreorderPaymentOkNotSettledOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.PREORDER, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "259.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.UNSETTLED);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));


        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "3.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);

        // Unsettled Capture
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("259.74",                             "259.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("259.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("259.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

    }




    @Test
    public void testHandleBackorderPaymentOkNotSettledOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.BACKORDER, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "84.77", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.UNSETTLED);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "4.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);

        // Unsettled Capture
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("84.77",                              "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("84.77", customerOrder.getOrderTotal().toPlainString());
        assertEquals("84.77", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

    }




    @Test
    public void testHandleElectronicPaymentOkNotSettledOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.ELECTRONIC, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "444.95", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.UNSETTLED);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        // Unsettled Capture and straight into in progress
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("444.95",                             "444.95"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("444.95", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

    }



    @Test
    public void testHandleMixedMultiPaymentOkNotSettledOnlineCapture() throws Exception {

        configureTestPG(false, false, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1034.25", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.UNSETTLED);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1034.25",                            "1034.25"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1034.25", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedMultiPaymentOkNotSettledOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.UNSETTLED);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
                Arrays.asList("689.74",                             "259.74",                           "84.77",
                              "689.74",                             "259.74",                           "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE,
                              PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,            Payment.PAYMENT_STATUS_OK,          Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE,
                              Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1034.25", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedMultiPaymentOkNotSettledPartialOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.UNSETTLED_NO + "259.74");

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
                Arrays.asList("689.74",                             "259.74",                           "84.77",
                              "689.74",                             "259.74",                           "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE,
                              PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,            Payment.PAYMENT_STATUS_OK,          Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.TRUE,                         Boolean.FALSE,                      Boolean.TRUE,
                              Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1034.25", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleMixedSinglePaymentOkNotSettledOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertSinglePaymentEntry(customerOrder.getOrdernum(), "1000.71", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.UNSETTLED);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
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
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1000.71",                            "1000.71"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,          PaymentGateway.AUTH_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1000.71", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleStandardPaymentProcessingOfflineSub() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestSubOrderOffline(TestOrderType.STANDARD, label, false);

        // Authorisation
        assertNoPaymentEntries(customerOrder.getOrdernum());

        assertFalse(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleStandardPaymentProcessingOnlineAuthSub() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestSubOrder(TestOrderType.STANDARD, label, false);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                             "689.74"),
                Arrays.asList(PaymentGateway.AUTH,                  PaymentGateway.AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }



}
