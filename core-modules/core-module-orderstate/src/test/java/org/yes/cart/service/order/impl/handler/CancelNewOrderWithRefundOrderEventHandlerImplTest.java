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
 * This handler cover case for unsuccessful callback notifications which occur when
 * items could not be reserved during PendingOrderEventHandlerImpl processing.
 * <p/>
 * At this point we can cancel the order. However if the order is served using external
 * form this callback may contain successful payment notification in which case we need to
 * void/cancel it.
 * <p/>
 * This test covers the starting flow for all orders that may lead to:
 * Cancelled in case of successful VOID_CAPTURE/REFUND or absence of payment<p/>
 * Cancelled waiting payment in case if cancellation could not process all refund for previous
 * AUTH_CAPTURE operations<p/>
 * <p/>
 * <p/>
 * This integration test covers work of the following transitional handlers:<p/>
 * CancelNewOrderWithRefundOrderEventHandlerImpl for cancelling the new order<p/>
 *
 * User: denispavlov
 * Date: 15/05/2015
 * Time: 07:44
 */
public class CancelNewOrderWithRefundOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private OrderEventHandler pendingHandler;
    private OrderEventHandler handler;

    private CustomerOrderService orderService;
    private CustomerOrderPaymentService paymentService;


    @Before
    public void setUp()  {
        super.setUp();
        pendingHandler = (OrderEventHandler) ctx().getBean("pendingOrderEventHandler");
        handler = (OrderEventHandler) ctx().getBean("cancelNewOrderWithRefundOrderEventHandler");
        orderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        paymentService = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
    }



    @Override
    protected CustomerOrder createTestOrder(final TestOrderType orderType, final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestOrder(orderType, pgLabel, onePhysicalDelivery);
        return prepareTestOrder(customerOrder);

    }

    @Override
    protected CustomerOrder createTestSubOrder(final TestOrderType orderType, final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestSubOrder(orderType, pgLabel, onePhysicalDelivery);
        return prepareTestOrder(customerOrder);

    }

    private CustomerOrder prepareTestOrder(final CustomerOrder customerOrder) throws OrderException {

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
    public void testHandleStandardReserveFailedOnline() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }

    @Test
    public void testHandleMixedSingleReserveFailedOnline() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 1);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }

    @Test
    public void testHandleMixedMultiReserveFailedOnline() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 3);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }

    @Test
    public void testHandleFullMixedReserveFailedOnline() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

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

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }

    @Test
    public void testHandleStandardReserveFailedPaymentOkRefundNotSupportedExternal() throws Exception {

        configureTestExtPG("4");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment OK, but refund is not supported
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "689.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE, PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED),
                Arrays.asList(Boolean.TRUE, Boolean.FALSE)
        );
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());

    }

    @Test
    public void testHandleMixedSingleReserveFailedPaymentOkRefundNotSupportedExternal() throws Exception {

        configureTestExtPG("4");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 1);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment OK, but refund is not supported
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1000.71",                     "1000.71"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE)
        );
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1000.71", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());

    }

    @Test
    public void testHandleMixedMultiReserveFailedPaymentOkRefundNotSupportedExternal() throws Exception {

        configureTestExtPG("4");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 3);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment OK, but refund is not supported
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1034.25",                    "1034.25"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE)
        );
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1034.25", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandleFullMixedReserveFailedPaymentOkRefundNotSupportedExternal() throws Exception {

        configureTestExtPG("4");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));

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
                Arrays.asList("1479.20",                    "1479.20"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1479.20", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandleStandardReserveFailedPaymentOkRefundOkExternal() throws Exception {

        configureTestExtPG("1");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment OK, but refund is not supported
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE)
        );
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandleMixedSingleReserveFailedPaymentOkRefundOkExternal() throws Exception {

        configureTestExtPG("1");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 1);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment OK, but refund is not supported
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1000.71",                     "1000.71"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE)
        );
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandleMixedMultiReserveFailedPaymentOkRefundOkExternal() throws Exception {

        configureTestExtPG("1");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 3);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment OK, but refund is not supported
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1034.25",                    "1034.25"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE)
        );
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }

    @Test
    public void testHandleFullMixedReserveFailedPaymentOkRefundOkExternal() throws Exception {

        configureTestExtPG("1");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));

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
                Arrays.asList("1479.20",                    "1479.20"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandleStandardReserveFailedPaymentOkRefundFailedExternal() throws Exception {

        configureTestExtPG("5");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment OK, but refund is not supported
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE)
        );
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());

    }

    @Test
    public void testHandleMixedSingleReserveFailedPaymentOkRefundFailedExternal() throws Exception {

        configureTestExtPG("5");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 1);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment OK, but refund is not supported
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1000.71",                     "1000.71"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE)
        );
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1000.71", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());

    }

    @Test
    public void testHandleMixedMultiReserveFailedPaymentOkRefundFailedExternal() throws Exception {

        configureTestExtPG("5");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 3);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment OK, but refund is not supported
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1034.25",                    "1034.25"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE)
        );
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1034.25", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandleStandardReserveFailedPaymentOkRefundProcessingExternal() throws Exception {

        configureTestExtPG("3");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment OK, but refund is not supported
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE)
        );
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());

    }

    @Test
    public void testHandleMixedSingleReserveFailedPaymentOkRefundProcessingExternal() throws Exception {

        configureTestExtPG("3");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 1);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment OK, but refund is not supported
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1000.71",                     "1000.71"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE)
        );
        assertEquals("1000.71", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1000.71", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());

    }

    @Test
    public void testHandleMixedMultiReserveFailedPaymentOkRefundProcessingExternal() throws Exception {

        configureTestExtPG("3");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 3);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment OK, but refund is not supported
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1034.25",                    "1034.25"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE)
        );
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1034.25", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandleFullMixedReserveFailedPaymentOkRefundProcessingExternal() throws Exception {

        configureTestExtPG("3");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));

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
                Arrays.asList("1479.20",                    "1479.20"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE)
        );
        assertEquals("1479.20", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1479.20", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandleStandardReserveFailedPaymentFailedExternal() throws Exception {

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "5");
                        }})));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment failed
        assertSinglePaymentEntry(customerOrder.getOrdernum(),
                "689.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_FAILED, Boolean.FALSE);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }

    @Test
    public void testHandleMixedSingleReserveFailedPaymentFailedExternal() throws Exception {

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, true);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "5");
                        }})));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 1);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment failed
        assertSinglePaymentEntry(customerOrder.getOrdernum(),
                "1000.71", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_FAILED, Boolean.FALSE);
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }

    @Test
    public void testHandleMixedMultiReserveFailedPaymentFailedExternal() throws Exception {

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.MIXED, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "5");
                        }})));

        // check reserved quantity
        // standard
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");
        // preorder
        assertInventory(WAREHOUSE_ID, "CC_TEST6", "500.00", "0.00");
        // backorder
        assertInventory(WAREHOUSE_ID, "CC_TEST5-NOINV", "0.00", "0.00");

        assertTrue(customerOrder.getDelivery().size() == 3);
        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment failed
        assertSinglePaymentEntry(customerOrder.getOrdernum(),
                "1034.25", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_FAILED, Boolean.FALSE);
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandleFullMixedReserveFailedPaymentFailedExternal() throws Exception {

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.FULL, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "5");
                        }})));

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

        // payment failed
        assertSinglePaymentEntry(customerOrder.getOrdernum(),
                "1479.20", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_FAILED, Boolean.FALSE);
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }


    @Test
    public void testHandleStandardReserveFailedOnlineSub() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestSubOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }



    @Test
    public void testHandleStandardReserveFailedPaymentOkRefundOkExternalSub() throws Exception {

        configureTestExtPG("1");

        String label = assertPgFeatures("testExtFormPaymentGateway", true, true, false, false);

        CustomerOrder customerOrder = createTestSubOrder(TestOrderType.STANDARD, label, false);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.new.order.cancel.refund
                        customerOrder,
                        null,
                        new HashMap() {{
                            put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1");
                        }})));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "0.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_WAIT);

        // payment OK, but refund is not supported
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74"),
                Arrays.asList(PaymentGateway.AUTH_CAPTURE,  PaymentGateway.REFUND),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.TRUE,                 Boolean.FALSE)
        );
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }



}