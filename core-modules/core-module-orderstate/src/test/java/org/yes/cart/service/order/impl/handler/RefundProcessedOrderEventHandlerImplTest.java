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
import org.yes.cart.payment.impl.TestExtFormPaymentGatewayImpl;
import org.yes.cart.payment.impl.TestPaymentGatewayImpl;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.OrderItemAllocationException;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * This handler finalises case for unsuccessful callback notifications which occur when
 * items could not be refunded during CancelNewOrderWithRefundOrderEventHandlerImpl and/or
 * CancelOrderWithRefundOrderEventHandlerImpl processing.
 * <p/>
 * At this point we can canceled the order. However the refund process has not returned a
 * successful result.
 * <p/>
 * This test covers the starting flow for all orders that may lead to:
 * Cancelled in case of successful VOID_CAPTURE/REFUND or absence of payment<p/>
 * Retuned in case of successful VOID_CAPTURE/REFUND or absence of payment<p/>
 * <p/>
 * <p/>
 * This integration test covers work of the following transitional handlers:<p/>
 * RefundProcessedOrderEventHandlerImpl for processing payment cancellation<p/>
 *
 * User: denispavlov
 * Date: 21/05/2015
 * Time: 17:29
 */
public class RefundProcessedOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private OrderEventHandler pendingHandler;
    private OrderEventHandler paymentProcessedHandler;
    private OrderEventHandler allocationHandler;
    private OrderEventHandler packingHandler;
    private OrderEventHandler packedHandler;
    private OrderEventHandler releaseHandler;
    private OrderEventHandler shippedHandler;
    private OrderEventHandler cancelNewOrderHandler;
    private OrderEventHandler cancelWithRefundHandler;
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
        paymentProcessedHandler = (OrderEventHandler) ctx().getBean("paymentProcessedOrderEventHandler");
        cancelNewOrderHandler = (OrderEventHandler) ctx().getBean("cancelNewOrderWithRefundOrderEventHandler");
        cancelWithRefundHandler = (OrderEventHandler) ctx().getBean("cancelOrderWithRefundOrderEventHandler");
        handler = (OrderEventHandler) ctx().getBean("refundProcessedOrderEventHandler");
        orderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        paymentService = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
    }


    protected CustomerOrder createTestOrderOnlineProcessing(final TestOrderType orderType, final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestOrder(orderType, pgLabel, onePhysicalDelivery);

        return prepareTestOrderOnline(customerOrder);

    }

    protected CustomerOrder createTestSubOrderOnlineProcessing(final TestOrderType orderType, final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestSubOrder(orderType, pgLabel, onePhysicalDelivery);

        return prepareTestOrderOnline(customerOrder);

    }


    private CustomerOrder prepareTestOrderOnline(final CustomerOrder customerOrder) throws OrderException {
        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        orderService.update(customerOrder);

        // Make sure we are in processing state at this point
        assertEquals(CustomerOrder.ORDER_STATUS_WAITING_PAYMENT, customerOrder.getOrderStatus());

        return customerOrder;
    }


    protected CustomerOrder createTestOrderFailedReserve(final TestOrderType orderType, final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestOrder(orderType, pgLabel, onePhysicalDelivery);

        // deliberately remove all stock
        return prepareTestOrderFailerReserve(customerOrder);

    }

    protected CustomerOrder createTestSubOrderFailedReserve(final TestOrderType orderType, final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestSubOrder(orderType, pgLabel, onePhysicalDelivery);
        return prepareTestOrderFailerReserve(customerOrder);


    }

    private CustomerOrder prepareTestOrderFailerReserve(final CustomerOrder customerOrder) throws OrderException {
        // deliberately remove all stock
        debitInventoryAndAssert(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00", "0.00");

        try {
            pendingHandler.handle(
                    new OrderEventImpl("", //evt.pending
                            customerOrder,
                            null,
                            Collections.EMPTY_MAP));
            fail("Handler must fail with exception");
        } catch (OrderItemAllocationException oiae) {
            // Ok we failed on reservation
        }

        // Need to get fresh order from DB after rollback, same as in handler filter
        return orderService.findByReference(customerOrder.getGuid());
    }


    @Test
    public void testHandleMixedMultiPaymentPartialOnlineCapturePerShipmentRefundFailed() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING_NO + "259.74");

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrderOnlineProcessing(TestOrderType.MIXED, label, false);

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING_NO + "259.74");
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.REFUND_FAIL_NO + "84.77");

        // payment failed
        assertTrue(paymentProcessedHandler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        deactivateTestPgParameter(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);
        deactivateTestPgParameter(TestPaymentGatewayImpl.REFUND_FAIL_NO + "84.77");

        // attempt refund again
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.refund.processed
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
                              "689.74",                     "259.74",                           "84.77",
                                                                                                "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE,
                              PaymentGateway.REFUND,        PaymentGateway.AUTH_CAPTURE,        PaymentGateway.REFUND,
                                                                                                PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_FAILED,      Payment.PAYMENT_STATUS_FAILED,
                                                                                                Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE,                      Boolean.TRUE,
                              Boolean.FALSE,                Boolean.FALSE,                      Boolean.FALSE,
                                                                                                Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedMultiPaymentPartialOnlineCapturePerShipmentRefundManual() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING_NO + "259.74");

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrderOnlineProcessing(TestOrderType.MIXED, label, false);

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING_NO + "259.74");
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.REFUND_FAIL_NO + "84.77");

        // payment failed
        assertTrue(paymentProcessedHandler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        deactivateTestPgParameter(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);

        // attempt refund again
        assertFalse(handler.handle(
                new OrderEventImpl("", //evt.refund.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // attempt refund manual
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.refund.processed
                        customerOrder,
                        null,
                        new HashMap() {{
                            put("forceManualProcessing", Boolean.TRUE);
                            put("forceManualProcessingMessage", "Manual message");
                        }})));


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
                              "689.74",                     "259.74",                           "84.77",
                                                                                                "84.77",
                                                                                                "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE,
                              PaymentGateway.REFUND,        PaymentGateway.AUTH_CAPTURE,        PaymentGateway.REFUND,
                                                                                                PaymentGateway.REFUND,
                                                                                                PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_FAILED,      Payment.PAYMENT_STATUS_FAILED,
                                                                                                Payment.PAYMENT_STATUS_FAILED,
                                                                                                Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE,                      Boolean.TRUE,
                              Boolean.FALSE,                Boolean.FALSE,                      Boolean.FALSE,
                                                                                                Boolean.FALSE,
                                                                                                Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleStandardReserveFailedPaymentOkRefundNotSupportedExternal() throws Exception {

        configureTestExtPG("4");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrderFailedReserve(TestOrderType.STANDARD, label, false);

        assertTrue(cancelNewOrderHandler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));


        // attempt refund manual
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.refund.processed
                        customerOrder,
                        null,
                        new HashMap() {{
                            put("forceManualProcessing", Boolean.TRUE);
                            put("forceManualProcessingMessage", "Manual message");
                        }})));


        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment OK, but refund is not supported
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74",                                           "689.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND,                              PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE,                                      Boolean.FALSE)
        );
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }



    @Test
    public void testHandleFullMixedReserveFailedPaymentOkRefundProcessingExternal() throws Exception {

        configureTestExtPG("3");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrderFailedReserve(TestOrderType.FULL, label, false);

        assertTrue(cancelNewOrderHandler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));


        configureTestExtPG("1");

        // refund
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.refund.processed
                        customerOrder,
                        null,
                        Collections.emptyMap())));


        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");
        // electronic
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 4);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment OK, but refund is not supported
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1479.20",                    "1479.20",                          "1479.20"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND,              PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE,                      Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }



    @Test
    public void testHandleMixedMultiPaymentOkOnlineCapturePerShipmentRefund() throws Exception {

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
        assertTrue(cancelWithRefundHandler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));


        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());

        deactivateTestPgParameter(TestPaymentGatewayImpl.REFUND_FAIL_NO + "84.77");

        // refund
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.refund.processed
                        customerOrder,
                        null,
                        Collections.emptyMap())));

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
                              "689.74",                     "259.74",                       "84.77",
                                                                                            "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,    PaymentGateway.AUTH_CAPTURE,
                              PaymentGateway.REFUND,        PaymentGateway.REFUND,          PaymentGateway.REFUND,
                                                                                            PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,      Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,      Payment.PAYMENT_STATUS_FAILED,
                                                                                            Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.TRUE,                   Boolean.TRUE,
                              Boolean.FALSE,                Boolean.FALSE,                  Boolean.FALSE,
                                                                                            Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedMultiPaymentOkOnlineCapturePerShipmentRefund2() throws Exception {

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
        assertTrue(cancelWithRefundHandler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));


        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());

        // refund
        assertFalse(handler.handle(
                new OrderEventImpl("", //evt.refund.processed
                        customerOrder,
                        null,
                        Collections.emptyMap())));

        deactivateTestPgParameter(TestPaymentGatewayImpl.REFUND_FAIL_NO + "84.77");

        // refund
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.refund.processed
                        customerOrder,
                        null,
                        Collections.emptyMap())));

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
                              "689.74",                     "259.74",                       "84.77",
                                                                                            "84.77",
                                                                                            "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,    PaymentGateway.AUTH_CAPTURE,
                              PaymentGateway.REFUND,        PaymentGateway.REFUND,          PaymentGateway.REFUND,
                                                                                            PaymentGateway.REFUND,
                                                                                            PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,      Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,      Payment.PAYMENT_STATUS_FAILED,
                                                                                            Payment.PAYMENT_STATUS_FAILED,
                                                                                            Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.TRUE,                   Boolean.TRUE,
                              Boolean.FALSE,                Boolean.FALSE,                  Boolean.FALSE,
                                                                                            Boolean.FALSE,
                                                                                            Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
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

        // refund for completed order (i.e. return)
        assertTrue(cancelWithRefundHandler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RETURNED);
        assertEquals(CustomerOrder.ORDER_STATUS_RETURNED_WAITING_PAYMENT, customerOrder.getOrderStatus());

        deactivateTestPgParameter(TestPaymentGatewayImpl.REFUND_FAIL);

        // refund
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.refund.processed
                        customerOrder,
                        null,
                        Collections.emptyMap())));


        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74",                       "689.74",                       "689.74"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.CAPTURE,         PaymentGateway.REFUND,          PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,      Payment.PAYMENT_STATUS_FAILED,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.TRUE,                   Boolean.FALSE,                  Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_RETURNED, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleStandardInventoryShippedAuthRefundFailed2() throws Exception {

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

        // refund for completed order (i.e. return)
        assertTrue(cancelWithRefundHandler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RETURNED);
        assertEquals(CustomerOrder.ORDER_STATUS_RETURNED_WAITING_PAYMENT, customerOrder.getOrderStatus());

        // refund fails again
        assertFalse(handler.handle(
                new OrderEventImpl("", //evt.refund.processed
                        customerOrder,
                        null,
                        Collections.emptyMap())));


        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74",                       "689.74",                       "689.74"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.CAPTURE,         PaymentGateway.REFUND,          PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,      Payment.PAYMENT_STATUS_FAILED,  Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,                Boolean.TRUE,                   Boolean.FALSE,                  Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_RETURNED_WAITING_PAYMENT, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleStandardInventoryShippedAuthRefundFailedManual() throws Exception {

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

        // refund for completed order (i.e. return)
        assertTrue(cancelWithRefundHandler.handle(
                new OrderEventImpl("", //evt.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RETURNED);
        assertEquals(CustomerOrder.ORDER_STATUS_RETURNED_WAITING_PAYMENT, customerOrder.getOrderStatus());

        // refund fails again
        assertFalse(handler.handle(
                new OrderEventImpl("", //evt.refund.processed
                        customerOrder,
                        null,
                        Collections.emptyMap())));

        // refund Manual
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.refund.processed
                        customerOrder,
                        null,
                        new HashMap() {{
                            put("forceManualProcessing", Boolean.TRUE);
                            put("forceManualProcessingMessage", "Manual message");
                        }})));


        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74",                       "689.74",                       "689.74",                       "689.74"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.CAPTURE,         PaymentGateway.REFUND,          PaymentGateway.REFUND,          PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,      Payment.PAYMENT_STATUS_FAILED,  Payment.PAYMENT_STATUS_FAILED,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.TRUE,                   Boolean.FALSE,                  Boolean.FALSE,                  Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_RETURNED, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleMixedMultiPaymentPartialOnlineCapturePerShipmentRefundFailedSub() throws Exception {

        configureTestPG(false, true, TestPaymentGatewayImpl.PROCESSING_NO + "259.74");

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestSubOrderOnlineProcessing(TestOrderType.MIXED, label, false);

        deactivateTestPgParameter(TestPaymentGatewayImpl.PROCESSING_NO + "259.74");
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.REFUND_FAIL_NO + "84.77");

        // payment failed
        assertTrue(paymentProcessedHandler.handle(
                new OrderEventImpl("", //evt.payment.processed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        deactivateTestPgParameter(TestPaymentGatewayImpl.AUTH_CAPTURE_FAIL);
        deactivateTestPgParameter(TestPaymentGatewayImpl.REFUND_FAIL_NO + "84.77");

        // attempt refund again
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.refund.processed
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
                              "689.74",                     "259.74",                           "84.77",
                              "84.77"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.AUTH_CAPTURE,        PaymentGateway.AUTH_CAPTURE,
                              PaymentGateway.REFUND,        PaymentGateway.AUTH_CAPTURE,        PaymentGateway.REFUND,
                              PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_PROCESSING,  Payment.PAYMENT_STATUS_OK,
                              Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_FAILED,      Payment.PAYMENT_STATUS_FAILED,
                              Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE,                      Boolean.TRUE,
                              Boolean.FALSE,                Boolean.FALSE,                      Boolean.FALSE,
                              Boolean.FALSE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleStandardReserveFailedPaymentOkRefundNotSupportedExternalSub() throws Exception {

        configureTestExtPG("4");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestSubOrderFailedReserve(TestOrderType.STANDARD, label, false);

        assertTrue(cancelNewOrderHandler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));


        // attempt refund manual
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.refund.processed
                        customerOrder,
                        null,
                        new HashMap() {{
                            put("forceManualProcessing", Boolean.TRUE);
                            put("forceManualProcessingMessage", "Manual message");
                        }})));


        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment OK, but refund is not supported
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74",                                           "689.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND,                              PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE,                                      Boolean.FALSE)
        );
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }

}
