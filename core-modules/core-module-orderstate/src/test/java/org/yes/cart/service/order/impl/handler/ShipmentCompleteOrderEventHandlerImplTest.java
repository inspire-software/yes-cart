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
import org.yes.cart.service.domain.*;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Test covers flow from Order in progress, Delivery shipping in progress/ shipping is progress waiting payment flow.
 * The flow from this state leads to:
 * <p/>
 * Order in progress, Delivery shipped (all online and offline pre paid)<p/>
 * Order in progress, Delivery shipping in progress waiting payment (offline pay on delivery if CAPTURE failed)<p/>
 * <p/>
 * This integration test covers work of the following transitional handlers:<p/>
 * ShipmentCompleteOrderEventHandlerImpl uses payment processor shipment complete for CAPTURE on online pay on delivery,
 * otherwise a simple delivery state transition<p/>
 * <p/>
 *
 * User: denispavlov
 * Date: 19/05/2015
 * Time: 19:43
 */
public class ShipmentCompleteOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {


    private OrderEventHandler pendingHandler;
    private OrderEventHandler confirmHandler;
    private OrderEventHandler allocationHandler;
    private OrderEventHandler packingHandler;
    private OrderEventHandler packedHandler;
    private OrderEventHandler releaseHandler;
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
        releaseHandler = (OrderEventHandler) ctx().getBean("releaseToShipmentOrderEventHandler");
        handler = (OrderEventHandler) ctx().getBean("shipmentCompleteOrderEventHandler");
        orderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        paymentService = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
    }



    protected CustomerOrder createTestOrderOnline(final TestOrderType orderType, final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestOrder(orderType, pgLabel, onePhysicalDelivery);

        return prepareTestOrderOnline(customerOrder);

    }

    protected CustomerOrder createTestSubOrderOnline(final TestOrderType orderType, final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestOrder(orderType, pgLabel, onePhysicalDelivery);

        return prepareTestOrderOnline(customerOrder);

    }

    private CustomerOrder prepareTestOrderOnline(final CustomerOrder customerOrder) throws OrderException {
        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        CustomerOrderDelivery orderDelivery = null;
        for (final CustomerOrderDelivery delivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.STANDARD_DELIVERY_GROUP.equals(delivery.getDeliveryGroup())) {
                orderDelivery = delivery;
                break;
            }
        }


        assertTrue(allocationHandler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        orderDelivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packingHandler.handle(
                new OrderEventImpl("", //evt.release.to.pack
                        customerOrder,
                        orderDelivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packedHandler.handle(
                new OrderEventImpl("", //evt.packing.complete
                        customerOrder,
                        orderDelivery,
                        Collections.EMPTY_MAP)));


        assertTrue(releaseHandler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        orderDelivery,
                        Collections.EMPTY_MAP)));

        // Make sure we are in progress state at this point
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        orderService.update(customerOrder);

        return customerOrder;
    }


    protected CustomerOrder createTestOrderOffline(final TestOrderType orderType, final String pgLabel, final boolean onePhysicalDelivery) throws Exception {

        final CustomerOrder customerOrder = super.createTestOrder(orderType, pgLabel, onePhysicalDelivery);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        CustomerOrderDelivery orderDelivery = null;
        for (final CustomerOrderDelivery delivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.STANDARD_DELIVERY_GROUP.equals(delivery.getDeliveryGroup())) {
                orderDelivery = delivery;
                break;
            }
        }

        assertTrue(confirmHandler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        assertTrue(allocationHandler.handle(
                new OrderEventImpl("", //evt.process.allocation
                        customerOrder,
                        orderDelivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packingHandler.handle(
                new OrderEventImpl("", //evt.release.to.pack
                        customerOrder,
                        orderDelivery,
                        Collections.EMPTY_MAP)));

        assertTrue(packedHandler.handle(
                new OrderEventImpl("", //evt.packing.complete
                        customerOrder,
                        orderDelivery,
                        Collections.EMPTY_MAP)));

        assertTrue(releaseHandler.handle(
                new OrderEventImpl("", //evt.release.to.shipment
                        customerOrder,
                        orderDelivery,
                        Collections.EMPTY_MAP)));

        // Make sure we are in progress state at this point
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        orderService.update(customerOrder);

        return customerOrder;

    }



    @Test
    public void testHandleStandardPaymentOkOnlineAuth() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrderOnline(TestOrderType.STANDARD, label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.shipment.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "689.74"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE, Boolean.TRUE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_COMPLETED, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedSinglePaymentOkOnlineAuth() throws Exception {

        configureTestPG(true, false);

        String label = assertPgFeatures("testPaymentGateway", false, true, true, false);

        CustomerOrder customerOrder = createTestOrderOnline(TestOrderType.MIXED, label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.shipment.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("1034.25",                "1034.25"),
                Arrays.asList(PaymentGateway.AUTH,      PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,            Boolean.TRUE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("1034.25", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED, customerOrder.getOrderStatus());
    }


    @Test
    public void testHandleMixedMultiPaymentOkOnlineAuth() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrderOnline(TestOrderType.MIXED, label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.shipment.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                   "84.77",                    "689.74"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.AUTH,        PaymentGateway.AUTH,        PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.FALSE,              Boolean.FALSE,              Boolean.TRUE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleStandardPaymentOkOfflineAuth() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrderOffline(TestOrderType.STANDARD, label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS_WAITING_PAYMENT);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS_WAITING_PAYMENT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.shipment.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "689.74"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE, Boolean.TRUE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_COMPLETED, customerOrder.getOrderStatus());
    }

    @Test
    public void testHandleMixedPaymentOkOfflineAuth() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrderOffline(TestOrderType.MIXED, label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS_WAITING_PAYMENT);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS_WAITING_PAYMENT.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.shipment.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), new HashMap<String, String>() {{
            put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);
            put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT);
            put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);
        }});

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "259.74",                   "84.77",                    "689.74"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.AUTH,        PaymentGateway.AUTH,        PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_OK,  Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.FALSE,              Boolean.FALSE,              Boolean.TRUE));
        assertEquals("1034.25", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED, customerOrder.getOrderStatus());
    }




    @Test
    public void testHandleStandardPaymentOkOnlineAuthSub() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestSubOrderOnline(TestOrderType.STANDARD, label, false);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS);

        CustomerOrderDelivery delivery = null;
        for (final CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS.equals(orderDelivery.getDeliveryStatus())) {
                assertNull(delivery); // make sure there is only one!
                delivery = orderDelivery;
            }
        }

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.shipment.complete
                        customerOrder,
                        delivery,
                        Collections.EMPTY_MAP)));

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "7.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "0.00", "0.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);

        // Authorisation
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                 "689.74"),
                Arrays.asList(PaymentGateway.AUTH,      PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,            Boolean.TRUE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_COMPLETED, customerOrder.getOrderStatus());
    }


}
