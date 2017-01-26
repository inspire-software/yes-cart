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

import static org.junit.Assert.*;

/**
 * This handler cover case when the order is cancelled from Admin.
 * <p/>
 * At this point we can cancel the order. The handler works out the amount to be refunded
 * and actions refund. In case if refund failed the order enters Cancelled/Returned waiting refund
 * state and will remain in this state until picked up by RefundProcessedOrderEventHandlerImpl handler
 * or manual refund is forced.
 * <p/>
 * This test covers the starting flow for all orders that may lead to:
 * Cancelled in case of successful VOID_CAPTURE/REFUND or absence of payment<p/>
 * Cancelled waiting payment in case if cancellation could not process all refund for previous
 * Returned in case of successful VOID_CAPTURE/REFUND or absence of payment and the order was
 * shipped or partially shipped<p/>
 * Returned waiting payment in case if cancellation could not process all refund for previous
 * AUTH_CAPTURE operations and the order was shipped or partially shipped<p/>
 * <p/>
 * <p/>
 * This integration test covers work of the following transitional handlers:<p/>
 * CancelOrderWithRefundOrderEventHandlerImpl for cancelling the existing order<p/>
 *
 * User: denispavlov
 * Date: 15/05/2015
 * Time: 07:44
 */
public class CancelOrderWithRefundOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private OrderEventHandler pendingHandler;
    private OrderEventHandler allocationHandler;
    private OrderEventHandler packingHandler;
    private OrderEventHandler packedHandler;
    private OrderEventHandler releaseHandler;
    private OrderEventHandler shippedHandler;
    private OrderEventHandler handler;

    private CustomerOrderService orderService;
    private CustomerOrderPaymentService paymentService;

    @Before
    public void setUp()  {
        super.setUp();
        pendingHandler = (OrderEventHandler) ctx().getBean("pendingOrderEventHandler");
        allocationHandler = (OrderEventHandler) ctx().getBean("processAllocationOrderEventHandler");
        packingHandler = (OrderEventHandler) ctx().getBean("releaseToPackOrderEventHandler");
        packedHandler = (OrderEventHandler) ctx().getBean("packCompleteOrderEventHandler");
        releaseHandler = (OrderEventHandler) ctx().getBean("releaseToShipmentOrderEventHandler");
        shippedHandler = (OrderEventHandler) ctx().getBean("shipmentCompleteOrderEventHandler");
        handler = (OrderEventHandler) ctx().getBean("cancelOrderWithRefundOrderEventHandler");
        orderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        paymentService = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
    }


    @Test
    public void testHandleStandardPaymentProcessingOnlineAuth() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));


        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_PROCESSING, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleFullMixedMultiPaymentProcessingPartialOnlineAuthPerShipment() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.PROCESSING_NO + "84.77");

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING_NO + "84.77");

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
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
                Arrays.asList("689.74", "259.74", "84.77", "444.95",
                        "689.74", "259.74", "444.95"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH,
                        PaymentGateway.REVERSE_AUTH, PaymentGateway.REVERSE_AUTH, PaymentGateway.REVERSE_AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_OK,
                        Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE,
                        Boolean.FALSE, Boolean.FALSE, Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleStandardPaymentProcessingOnlineCapture() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                         "689.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,      PaymentGateway.VOID_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                    Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        // Really strange case but this is acceptable for now as we only want to count paid captures
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleStandardPaymentProcessingOnlineCaptureFailedVoid() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.VOID_CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                         "689.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,      PaymentGateway.VOID_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                    Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedMultiPaymentProcessingOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
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
                              PaymentGateway.VOID_CAPTURE,          PaymentGateway.VOID_CAPTURE,        PaymentGateway.VOID_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING,
                              Payment.PAYMENT_STATUS_OK,            Payment.PAYMENT_STATUS_OK,          Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE,
                              Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedMultiPaymentProcessingOnlineCapturePerShipmentVoidFailed() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.VOID_CAPTURE_FAIL_NO + "259.74");

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
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
                              PaymentGateway.VOID_CAPTURE,          PaymentGateway.VOID_CAPTURE,        PaymentGateway.VOID_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_PROCESSING,
                              Payment.PAYMENT_STATUS_OK,            Payment.PAYMENT_STATUS_FAILED,      Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE,
                              Boolean.FALSE,                        Boolean.FALSE,                      Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleMixedMultiPaymentProcessingPartialOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING_NO + "259.74");

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING_NO + "259.74");

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
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
                              PaymentGateway.REFUND,        PaymentGateway.VOID_CAPTURE,        PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,          Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE,                      Boolean.TRUE,
                              Boolean.FALSE,                Boolean.FALSE,                      Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedMultiPaymentProcessingPartialOnlineCapturePerShipmentRefund() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING_NO + "259.74");

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING_NO + "259.74");
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.REFUND_FAIL_NO + "84.77");

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
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
                              PaymentGateway.REFUND,        PaymentGateway.VOID_CAPTURE,        PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,          Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE,                      Boolean.TRUE,
                              Boolean.FALSE,                Boolean.FALSE,                      Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("84.77", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleStandardPaymentOkOfflineAuth() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));


        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));


        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");


        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleMixedMultiPaymentOkOnlineAuthPerShipment() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});

        // refund just before allocation happened
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
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
                Arrays.asList("689.74", "259.74", "84.77",
                        "689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.AUTH, PaymentGateway.AUTH,
                        PaymentGateway.REVERSE_AUTH, PaymentGateway.REVERSE_AUTH, PaymentGateway.REVERSE_AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK,
                        Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE,
                        Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedMultiPaymentOkOnlineCapturePerShipment() throws Exception {

        configureTestPG(false, true);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));


        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});

        // refund just before allocation happened
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
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

        // Capture per delivery
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                       "84.77",
                              "689.74",                     "259.74",                       "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,    PaymentGateway.AUTH_CAPTURE,
                              PaymentGateway.REFUND,        PaymentGateway.REFUND,          PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,      Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,      Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.TRUE,                   Boolean.TRUE,
                              Boolean.FALSE,                Boolean.FALSE,                  Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedMultiPaymentOkOnlineCapturePerShipmentRefundFailed() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.REFUND_FAIL_NO + "84.77");

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));


        assertEquals(customerOrder.getDelivery().size(), 3);
        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});

        // refund just before allocation happened
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
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

        // Capture per delivery
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "259.74", "84.77",
                        "689.74", "259.74", "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE, PaymentGateway.AUTH_CAPTURE,
                        PaymentGateway.REFUND, PaymentGateway.REFUND, PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK,
                        Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.TRUE,                 Boolean.TRUE,                   Boolean.TRUE,
                              Boolean.FALSE,                Boolean.FALSE,                  Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("84.77", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleStandardInventoryAllocatedAuth() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

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

        assertTrue(allocationHandler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED);

        // refund after allocation happened
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));



        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_DEALLOCATED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.REVERSE_AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleStandardInventoryPackingAuth() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

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

        assertTrue(allocationHandler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packingHandler.handle(
                new OrderEventImpl("", //evt.release.to.pack
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_PACKING);

        // refund after allocation happened
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));



        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_DEALLOCATED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.REVERSE_AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleStandardInventoryShipReadyAuth() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

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

        assertTrue(allocationHandler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packingHandler.handle(
                new OrderEventImpl("", //evt.release.to.pack
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packedHandler.handle(
                new OrderEventImpl("", //evt.packing.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY);

        // refund after allocation happened
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));



        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_DEALLOCATED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.REVERSE_AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleStandardInventoryShippingAuth() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

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

        assertTrue(allocationHandler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packingHandler.handle(
                new OrderEventImpl("", //evt.release.to.pack
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packedHandler.handle(
                new OrderEventImpl("", //evt.packing.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(releaseHandler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        orderService.update(customerOrder);

        try {
            // refund during shipping
            handler.handle(
                    new OrderEventImpl("", //evt.order.cancel.refund
                            customerOrder,
                            null,
                            Collections.EMPTY_MAP));
            fail("Cancellation during shipping in progress is not allowed");
        } catch (OrderException oe) {
            // expected as we are shipping and need to wait till customer receives it and returns
        }

        // refresh after exception
        customerOrder = orderService.findByReference(customerOrder.getOrdernum());

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.TRUE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleStandardInventoryShippedAuth() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

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

        assertTrue(allocationHandler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packingHandler.handle(
                new OrderEventImpl("", //evt.release.to.pack
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packedHandler.handle(
                new OrderEventImpl("", //evt.packing.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(releaseHandler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(shippedHandler.handle(
                new OrderEventImpl("", //evt.shipment.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);
        assertEquals(CustomerOrder.ORDER_STATUS_COMPLETED, customerOrder.getOrderStatus());

        // refund for completed order (i.e. return)
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RETURNED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74",                       "689.74"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.CAPTURE,         PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,      Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.TRUE,                   Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_RETURNED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleStandardInventoryShippedAuthRefundFailed() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.REFUND_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

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

        assertTrue(allocationHandler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packingHandler.handle(
                new OrderEventImpl("", //evt.release.to.pack
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packedHandler.handle(
                new OrderEventImpl("", //evt.packing.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(releaseHandler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(shippedHandler.handle(
                new OrderEventImpl("", //evt.shipment.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);
        assertEquals(CustomerOrder.ORDER_STATUS_COMPLETED, customerOrder.getOrderStatus());

        // refund for completed order (i.e. return)
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RETURNED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74",                       "689.74"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.CAPTURE,         PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,      Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                Boolean.TRUE,                   Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_RETURNED_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleStandardInventoryShippingAuthNoCapture() throws Exception {

        configureTestPG(true, true, TestPaymentGatewayImpl.CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

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

        assertTrue(allocationHandler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packingHandler.handle(
                new OrderEventImpl("", //evt.release.to.pack
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packedHandler.handle(
                new OrderEventImpl("", //evt.packing.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(releaseHandler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT);

        // refund during shipping
        assertTrue(handler.handle(
                    new OrderEventImpl("", //evt.order.cancel.refund
                            customerOrder,
                            null,
                            Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_DEALLOCATED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74",                       "689.74"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.CAPTURE,         PaymentGateway.REVERSE_AUTH),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_FAILED,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.FALSE,                  Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleStandardInventoryAllocatedCapture() throws Exception {

        configureTestPG(false, true);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

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

        assertTrue(allocationHandler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED);

        // refund after allocation happened
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));



        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_DEALLOCATED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleStandardInventoryPackingCapture() throws Exception {

        configureTestPG(false, true);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

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

        assertTrue(allocationHandler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));


        assertTrue(packingHandler.handle(
                new OrderEventImpl("", //evt.release.to.pack
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_PACKING);

        // refund after allocation happened
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));



        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_DEALLOCATED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleStandardInventoryShipReadyCapture() throws Exception {

        configureTestPG(false, true);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

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

        assertTrue(allocationHandler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));


        assertTrue(packingHandler.handle(
                new OrderEventImpl("", //evt.release.to.pack
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packedHandler.handle(
                new OrderEventImpl("", //evt.packing.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY);

        // refund after allocation happened
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));



        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_DEALLOCATED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleStandardInventoryShippingCapture() throws Exception {

        configureTestPG(false, true);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

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

        assertTrue(allocationHandler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));


        assertTrue(packingHandler.handle(
                new OrderEventImpl("", //evt.release.to.pack
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packedHandler.handle(
                new OrderEventImpl("", //evt.packing.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));


        assertTrue(releaseHandler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        orderService.update(customerOrder);

        try {
            // refund during shipping
            handler.handle(
                    new OrderEventImpl("", //evt.order.cancel.refund
                            customerOrder,
                            null,
                            Collections.EMPTY_MAP));
            fail("Cancellation during shipping in progress is not allowed");
        } catch (OrderException oe) {
            // expected as we are shipping and need to wait till customer receives it and returns
        }

        // refresh after exception
        customerOrder = orderService.findByReference(customerOrder.getOrdernum());

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleStandardInventoryShippedCapture() throws Exception {

        configureTestPG(false, true);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

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

        assertTrue(allocationHandler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));


        assertTrue(packingHandler.handle(
                new OrderEventImpl("", //evt.release.to.pack
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packedHandler.handle(
                new OrderEventImpl("", //evt.packing.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));


        assertTrue(releaseHandler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(shippedHandler.handle(
                new OrderEventImpl("", //evt.shipment.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);
        assertEquals(CustomerOrder.ORDER_STATUS_COMPLETED, customerOrder.getOrderStatus());

        // refund for completed order (i.e. return)
        assertTrue(handler.handle(
                    new OrderEventImpl("", //evt.order.cancel.refund
                            customerOrder,
                            null,
                            Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RETURNED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "689.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE, PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE, Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_RETURNED, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleStandardInventoryShippedCaptureRefundFailed() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.REFUND_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

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

        assertTrue(allocationHandler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));


        assertTrue(packingHandler.handle(
                new OrderEventImpl("", //evt.release.to.pack
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packedHandler.handle(
                new OrderEventImpl("", //evt.packing.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));


        assertTrue(releaseHandler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertTrue(shippedHandler.handle(
                new OrderEventImpl("", //evt.shipment.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);
        assertEquals(CustomerOrder.ORDER_STATUS_COMPLETED, customerOrder.getOrderStatus());

        // refund for completed order (i.e. return)
        assertTrue(handler.handle(
                    new OrderEventImpl("", //evt.order.cancel.refund
                            customerOrder,
                            null,
                            Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RETURNED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_RETURNED_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleStandardPaymentProcessingOnlineCaptureSub() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestSubOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                         "689.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,      PaymentGateway.VOID_CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING,Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                    Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        // Really strange case but this is acceptable for now as we only want to count paid captures
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


}
