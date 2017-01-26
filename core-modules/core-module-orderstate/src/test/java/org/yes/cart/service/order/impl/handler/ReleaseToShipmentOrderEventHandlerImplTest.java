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
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

/**
 * Test covers flow from Order in progress, Delivery ready for shipment flow.
 * The flow from this state leads to:
 * <p/>
 * Order in progress, Delivery shipping in progress (all pre paid and online CAPTURE successful)<p/>
 * Order in progress, Delivery ready for shipment waiting payment (online CAPTURE failed)<p/>
 * Order in progress, Delivery shipping in progress waiting payment (offline pay on delivery)<p/>
 * <p/>
 * This integration test covers work of the following transitional handlers:<p/>
 * ReleaseToShipmentOrderEventHandlerImpl uses payment processor shipment complete for CAPTURE on online pay on delivery,
 * otherwise a simple delivery state transition<p/>
 * <p/>
 *
 * User: denispavlov
 * Date: 19/05/2015
 * Time: 19:43
 */
public class ReleaseToShipmentOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {


    private OrderEventHandler pendingHandler;
    private OrderEventHandler confirmHandler;
    private OrderEventHandler allocationHandler;
    private OrderEventHandler packingHandler;
    private OrderEventHandler packedHandler;
    private OrderEventHandler handler;

    private CustomerOrderService orderService;
    private CustomerOrderPaymentService paymentService;

    @Before
    public void setUp()  {
        super.setUp();
        pendingHandler = (OrderEventHandler) ctx().getBean("pendingOrderEventHandler");
        confirmHandler = (OrderEventHandler) ctx().getBean("paymentConfirmedOrderEventHandler");
        allocationHandler = (OrderEventHandler) ctx().getBean("processAllocationOrderEventHandler");
        packingHandler = (OrderEventHandler) ctx().getBean("releaseToPackOrderEventHandler");
        packedHandler = (OrderEventHandler) ctx().getBean("packCompleteOrderEventHandler");
        handler = (OrderEventHandler) ctx().getBean("releaseToShipmentOrderEventHandler");
        orderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        paymentService = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
    }


    protected CustomerOrder createTestOrderOnlineStandard(final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestOrder(TestOrderType.STANDARD, pgLabel, onePhysicalDelivery);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        assertTrue(allocationHandler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        customerOrder.getDelivery().iterator().next(),
                        Collections.EMPTY_MAP)));

        assertTrue(packingHandler.handle(
                new OrderEventImpl("", //evt.release.to.pack
                        customerOrder,
                        customerOrder.getDelivery().iterator().next(),
                        Collections.EMPTY_MAP)));

        assertTrue(packedHandler.handle(
                new OrderEventImpl("", //evt.packing.complete
                        customerOrder,
                        customerOrder.getDelivery().iterator().next(),
                        Collections.EMPTY_MAP)));

        // Make sure we are in progress state at this point
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        orderService.update(customerOrder);

        return customerOrder;

    }



    protected CustomerOrder createTestOrderOnlineElectronic(final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestOrder(TestOrderType.ELECTRONIC, pgLabel, onePhysicalDelivery);

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


    protected CustomerOrder createTestSubOrderOnlineElectronic(final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestSubOrder(TestOrderType.ELECTRONIC, pgLabel, onePhysicalDelivery);

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



    protected CustomerOrder createTestOrderOfflineStandard(final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestOrder(TestOrderType.STANDARD, pgLabel, onePhysicalDelivery);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        assertTrue(confirmHandler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        assertTrue(allocationHandler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        customerOrder.getDelivery().iterator().next(),
                        Collections.EMPTY_MAP)));

        assertTrue(packingHandler.handle(
                new OrderEventImpl("", //evt.release.to.pack
                        customerOrder,
                        customerOrder.getDelivery().iterator().next(),
                        Collections.EMPTY_MAP)));

        assertTrue(packedHandler.handle(
                new OrderEventImpl("", //evt.packing.complete
                        customerOrder,
                        customerOrder.getDelivery().iterator().next(),
                        Collections.EMPTY_MAP)));

        // Make sure we are in progress state at this point
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        orderService.update(customerOrder);

        return customerOrder;

    }


    @Test
    public void testHandleStandardPaymentOkOnlineAuth() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrderOnlineStandard(label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "689.74"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE, Boolean.TRUE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleStandardPaymentProcessingOnlineAuth() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrderOnlineStandard(label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        // make CAPTURE fail
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.PROCESSING);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                 "689.74"),
                Arrays.asList(PaymentGateway.AUTH,      PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,Payment.PAYMENT_STATUS_PROCESSING),
                Arrays.asList(Boolean.FALSE,            Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleStandardPaymentFailedOnlineAuth() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrderOnlineStandard(label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        // make CAPTURE fail
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                 "689.74"),
                Arrays.asList(PaymentGateway.AUTH,      PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,            Boolean.FALSE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleStandardPaymentFailedManualOnlineAuth() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrderOnlineStandard(label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        // make CAPTURE fail
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // now manual retry
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        new HashMap() {{
                            put("forceManualProcessing", Boolean.TRUE);
                            put("forceManualProcessingMessage", "Manual message");
                        }})));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                 "689.74",                       "689.74"),
                Arrays.asList(PaymentGateway.AUTH,      PaymentGateway.CAPTURE,         PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,Payment.PAYMENT_STATUS_FAILED,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,            Boolean.FALSE,                  Boolean.TRUE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleElectronicPaymentOkOnlineAuth() throws Exception {

        // Fail the first capture
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrderOnlineElectronic(label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        // Make payment pass
        deactivateTestPgParameter(TestPaymentGatewayImpl.CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("444.95",                 "444.95",                       "444.95"),
                Arrays.asList(PaymentGateway.AUTH,      PaymentGateway.CAPTURE,         PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,Payment.PAYMENT_STATUS_FAILED,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,            Boolean.FALSE,                  Boolean.TRUE));
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("444.95", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleElectronicPaymentOkManualOnlineAuth() throws Exception {

        // Fail the first capture
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrderOnlineElectronic(label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        // this should fail again
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // now manual retry
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        new HashMap() {{
                            put("forceManualProcessing", Boolean.TRUE);
                            put("forceManualProcessingMessage", "Manual message");
                        }})));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("444.95",                 "444.95",                       "444.95",                       "444.95"),
                Arrays.asList(PaymentGateway.AUTH,      PaymentGateway.CAPTURE,         PaymentGateway.CAPTURE,         PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,Payment.PAYMENT_STATUS_FAILED,  Payment.PAYMENT_STATUS_FAILED,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,            Boolean.FALSE,                  Boolean.FALSE,                  Boolean.TRUE));
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("444.95", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleElectronicPaymentFailedOnlineAuth() throws Exception {

        // Fail the first capture
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrderOnlineElectronic(label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        // this should fail again
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("444.95",                 "444.95",                       "444.95"),
                Arrays.asList(PaymentGateway.AUTH,      PaymentGateway.CAPTURE,         PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,Payment.PAYMENT_STATUS_FAILED,  Payment.PAYMENT_STATUS_FAILED),
                Arrays.asList(Boolean.FALSE,            Boolean.FALSE,                  Boolean.FALSE));
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleStandardOnlineCapture() throws Exception {

        configureTestPG(false, true);

        String label = assertPgFeatures("testPaymentGateway", false, true, false, true);

        CustomerOrder customerOrder = createTestOrderOnlineStandard(label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

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
    public void testHandleStandardPaymentOkOfflineAuth() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrderOfflineStandard(label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS_WAITING_PAYMENT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, Boolean.FALSE);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }



    @Test
    public void testHandleStandardPaymentOkOfflineCapture() throws Exception {

        String label = assertPgFeatures("prePaymentGateway", false, false, false, false);

        CustomerOrder customerOrder = createTestOrderOfflineStandard(label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, Boolean.TRUE);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }





    @Test
    public void testHandleElectronicPaymentOkOnlineAuthSub() throws Exception {

        // Fail the first capture
        activateTestPgParameterSetOn(TestPaymentGatewayImpl.CAPTURE_FAIL);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestSubOrderOnlineElectronic(label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        // Make payment pass
        deactivateTestPgParameter(TestPaymentGatewayImpl.CAPTURE_FAIL);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST9", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("444.95",                     "444.95",                       "444.95"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.CAPTURE,         PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_FAILED,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.FALSE,                  Boolean.TRUE));
        assertEquals("444.95", customerOrder.getOrderTotal().toPlainString());
        assertEquals("444.95", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
    }


}
