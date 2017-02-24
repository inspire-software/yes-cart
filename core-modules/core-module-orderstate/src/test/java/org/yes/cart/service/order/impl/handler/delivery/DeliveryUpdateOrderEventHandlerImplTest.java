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

package org.yes.cart.service.order.impl.handler.delivery;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.service.order.impl.handler.AbstractEventHandlerImplTest;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 17/02/2017
 * Time: 12:06
 */
public class DeliveryUpdateOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private OrderEventHandler pendingHandler;
    private OrderEventHandler confirmHandler;

    private OrderEventHandler handler;

    private CustomerOrderService orderService;
    private CustomerOrderPaymentService paymentService;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Before
    public void setUp()  {
        super.setUp();
        handler = (OrderEventHandler) ctx().getBean("deliveryUpdateOrderEventHandler");
        pendingHandler = (OrderEventHandler) ctx().getBean("pendingOrderEventHandler");
        confirmHandler = (OrderEventHandler) ctx().getBean("paymentConfirmedOrderEventHandler");
        orderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        paymentService = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
    }

    Date date(final String date) throws Exception{
        return format.parse(date);
    }

    @Test
    public void testHandleStandardPaymentOfflineNotConfirmed() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        orderService.update(customerOrder);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");


        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, customerOrder.getOrderStatus());

        final OrderDeliveryStatusUpdateImpl update1 = new OrderDeliveryStatusUpdateImpl(
                customerOrder.getOrdernum(),
                "WAREHOUSE_1",
                Arrays.<OrderDeliveryLineStatusUpdate>asList(
                    new OrderDeliveryLineStatusUpdateImpl(
                            null, "CC_TEST1", "packing", date("2017-02-17"), date("2017-02-20"), null, null, null, null, false, null, null,
                            Collections.singletonMap("TrackingURL", new Pair<String, String>("http://tracking.com?ref=00001", null))
                    )
                )
        );

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.update
                        customerOrder,
                        null,
                        Collections.singletonMap("update", update1))));

        orderService.update(customerOrder);

        CustomerOrder updated = orderService.findByReference(customerOrder.getOrdernum());

        assertNotNull(updated);

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, updated.getOrderStatus());
        assertDeliveryStates(updated.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // check reserved quantity has not changed
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertEquals(1, updated.getDelivery().size());
        final CustomerOrderDelivery delivery1 = updated.getDelivery().iterator().next();
        assertEquals(date("2017-02-17"), delivery1.getDeliveryEstimatedMin());
        assertNull(delivery1.getDeliveryEstimatedMax());
        assertNull(delivery1.getDeliveryGuaranteed());

        final Collection<CustomerOrderDeliveryDet> details1 = delivery1.getDetail();
        final Map<String, CustomerOrderDeliveryDet> detailsBySKU1 = new HashMap<String, CustomerOrderDeliveryDet>();
        for (final CustomerOrderDeliveryDet detail : details1) {
            detailsBySKU1.put(detail.getProductSkuCode(), detail);
        }

        final CustomerOrderDeliveryDet test11 = detailsBySKU1.get("CC_TEST1");
        assertNotNull(test11);
        assertEquals(date("2017-02-17"), test11.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-20"), test11.getDeliveryEstimatedMax());
        assertNull(test11.getDeliveryGuaranteed());
        assertEquals("packing", test11.getDeliveryRemarks());
        assertNull(test11.getDeliveryConfirmed());
        assertFalse(test11.isDeliveryRejected());
        assertFalse(test11.isDeliveryDifferent());
        assertNotNull(test11.getAllValues());
        final Pair<String, String> trackingUrl = test11.getValue("TrackingURL");
        assertNotNull(trackingUrl);
        assertEquals("http://tracking.com?ref=00001", trackingUrl.getFirst());
        assertNull(trackingUrl.getSecond());

        final CustomerOrderDeliveryDet test12 = detailsBySKU1.get("CC_TEST2");
        assertNotNull(test12);
        assertNull(test12.getDeliveryEstimatedMin());
        assertNull(test12.getDeliveryEstimatedMax());
        assertNull(test12.getDeliveryGuaranteed());
        assertNull(test12.getDeliveryRemarks());
        assertNull(test12.getDeliveryConfirmed());
        assertFalse(test12.isDeliveryRejected());
        assertFalse(test12.isDeliveryDifferent());


        final OrderDeliveryStatusUpdateImpl update2 = new OrderDeliveryStatusUpdateImpl(
                customerOrder.getOrdernum(),
                "WAREHOUSE_1",
                Arrays.<OrderDeliveryLineStatusUpdate>asList(
                        new OrderDeliveryLineStatusUpdateImpl(
                                null, "CC_TEST2", "packing 2", date("2017-02-17"), date("2017-02-21"), null, null, null, null, false, null, null
                        )
                )
        );

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.update
                        updated,
                        null,
                        Collections.singletonMap("update", update2))));

        orderService.update(updated);

        updated = orderService.findByReference(updated.getOrdernum());

        assertNotNull(updated);

        assertEquals(CustomerOrder.ORDER_STATUS_WAITING, updated.getOrderStatus());
        assertDeliveryStates(updated.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED);

        // check reserved quantity has not changed
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertEquals(1, updated.getDelivery().size());
        final CustomerOrderDelivery delivery2 = updated.getDelivery().iterator().next();
        assertEquals(date("2017-02-17"), delivery2.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-21"), delivery2.getDeliveryEstimatedMax());
        assertNull(delivery2.getDeliveryGuaranteed());

        final Collection<CustomerOrderDeliveryDet> details2 = delivery2.getDetail();
        final Map<String, CustomerOrderDeliveryDet> detailsBySKU2 = new HashMap<String, CustomerOrderDeliveryDet>();
        for (final CustomerOrderDeliveryDet detail : details2) {
            detailsBySKU2.put(detail.getProductSkuCode(), detail);
        }

        final CustomerOrderDeliveryDet test21 = detailsBySKU2.get("CC_TEST1");
        assertNotNull(test21);
        assertEquals(date("2017-02-17"), test21.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-20"), test21.getDeliveryEstimatedMax());
        assertNull(test21.getDeliveryGuaranteed());
        assertEquals("packing", test21.getDeliveryRemarks());
        assertNull(test21.getDeliveryConfirmed());
        assertFalse(test21.isDeliveryRejected());
        assertFalse(test21.isDeliveryDifferent());

        final CustomerOrderDeliveryDet test22 = detailsBySKU2.get("CC_TEST2");
        assertNotNull(test22);
        assertEquals(date("2017-02-17"), test22.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-21"), test22.getDeliveryEstimatedMax());
        assertNull(test22.getDeliveryGuaranteed());
        assertEquals("packing 2", test22.getDeliveryRemarks());
        assertNull(test22.getDeliveryConfirmed());
        assertFalse(test22.isDeliveryRejected());
        assertFalse(test22.isDeliveryDifferent());


        // no payment because it is made when CC manager approves the order
        assertNoPaymentEntries(customerOrder.getOrdernum());
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

    }



    @Test
    public void testHandleStandardPaymentOfflineAuthConfirmed() throws Exception {

        String label = assertPgFeatures("courierPaymentGateway", false, false, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        orderService.update(customerOrder);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertTrue(confirmHandler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        orderService.update(customerOrder);


        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);

        // single payment AUTH because it is made when CC manager approves the order
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        final OrderDeliveryStatusUpdateImpl update1 = new OrderDeliveryStatusUpdateImpl(
                customerOrder.getOrdernum(),
                "WAREHOUSE_1",
                Arrays.<OrderDeliveryLineStatusUpdate>asList(
                        new OrderDeliveryLineStatusUpdateImpl(
                                null, "CC_TEST1", "packing", date("2017-02-17"), date("2017-02-20"), null, null, null, null, false, null, null
                        )
                )
        );

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.update
                        customerOrder,
                        null,
                        Collections.singletonMap("update", update1))));

        orderService.update(customerOrder);

        CustomerOrder updated = orderService.findByReference(customerOrder.getOrdernum());

        assertNotNull(updated);

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, updated.getOrderStatus());
        assertDeliveryStates(updated.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_PACKING);

        // Although we just void in auto, the allocation
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertEquals(1, updated.getDelivery().size());
        final CustomerOrderDelivery delivery1 = updated.getDelivery().iterator().next();
        assertEquals(date("2017-02-17"), delivery1.getDeliveryEstimatedMin());
        assertNull(delivery1.getDeliveryEstimatedMax());
        assertNull(delivery1.getDeliveryGuaranteed());

        final Collection<CustomerOrderDeliveryDet> details1 = delivery1.getDetail();
        final Map<String, CustomerOrderDeliveryDet> detailsBySKU1 = new HashMap<String, CustomerOrderDeliveryDet>();
        for (final CustomerOrderDeliveryDet detail : details1) {
            detailsBySKU1.put(detail.getProductSkuCode(), detail);
        }

        final CustomerOrderDeliveryDet test11 = detailsBySKU1.get("CC_TEST1");
        assertNotNull(test11);
        assertEquals(date("2017-02-17"), test11.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-20"), test11.getDeliveryEstimatedMax());
        assertNull(test11.getDeliveryGuaranteed());
        assertEquals("packing", test11.getDeliveryRemarks());
        assertNull(test11.getDeliveryConfirmed());
        assertFalse(test11.isDeliveryRejected());
        assertFalse(test11.isDeliveryDifferent());

        final CustomerOrderDeliveryDet test12 = detailsBySKU1.get("CC_TEST2");
        assertNotNull(test12);
        assertNull(test12.getDeliveryEstimatedMin());
        assertNull(test12.getDeliveryEstimatedMax());
        assertNull(test12.getDeliveryGuaranteed());
        assertNull(test12.getDeliveryRemarks());
        assertNull(test12.getDeliveryConfirmed());
        assertFalse(test12.isDeliveryRejected());
        assertFalse(test12.isDeliveryDifferent());


        final OrderDeliveryStatusUpdateImpl update2 = new OrderDeliveryStatusUpdateImpl(
                customerOrder.getOrdernum(),
                "WAREHOUSE_1",
                Arrays.<OrderDeliveryLineStatusUpdate>asList(
                        new OrderDeliveryLineStatusUpdateImpl(
                                null, "CC_TEST2", "packing 2", date("2017-02-17"), date("2017-02-21"), null, null, null, null, false, null, null
                        )
                )
        );

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.update
                        updated,
                        null,
                        Collections.singletonMap("update", update2))));

        orderService.update(updated);

        updated = orderService.findByReference(updated.getOrdernum());

        assertNotNull(updated);

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, updated.getOrderStatus());
        assertDeliveryStates(updated.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_PACKING);

        // check reserved quantity has not changed
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertEquals(1, updated.getDelivery().size());
        final CustomerOrderDelivery delivery2 = updated.getDelivery().iterator().next();
        assertEquals(date("2017-02-17"), delivery2.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-21"), delivery2.getDeliveryEstimatedMax());
        assertNull(delivery2.getDeliveryGuaranteed());

        final Collection<CustomerOrderDeliveryDet> details2 = delivery2.getDetail();
        final Map<String, CustomerOrderDeliveryDet> detailsBySKU2 = new HashMap<String, CustomerOrderDeliveryDet>();
        for (final CustomerOrderDeliveryDet detail : details2) {
            detailsBySKU2.put(detail.getProductSkuCode(), detail);
        }

        final CustomerOrderDeliveryDet test21 = detailsBySKU2.get("CC_TEST1");
        assertNotNull(test21);
        assertEquals(date("2017-02-17"), test21.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-20"), test21.getDeliveryEstimatedMax());
        assertNull(test21.getDeliveryGuaranteed());
        assertEquals("packing", test21.getDeliveryRemarks());
        assertNull(test21.getDeliveryConfirmed());
        assertFalse(test21.isDeliveryRejected());
        assertFalse(test21.isDeliveryDifferent());

        final CustomerOrderDeliveryDet test22 = detailsBySKU2.get("CC_TEST2");
        assertNotNull(test22);
        assertEquals(date("2017-02-17"), test22.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-21"), test22.getDeliveryEstimatedMax());
        assertNull(test22.getDeliveryGuaranteed());
        assertEquals("packing 2", test22.getDeliveryRemarks());
        assertNull(test22.getDeliveryConfirmed());
        assertFalse(test22.isDeliveryRejected());
        assertFalse(test22.isDeliveryDifferent());


        final OrderDeliveryStatusUpdateImpl update3 = new OrderDeliveryStatusUpdateImpl(
                customerOrder.getOrdernum(),
                "WAREHOUSE_1",
                Arrays.<OrderDeliveryLineStatusUpdate>asList(
                        new OrderDeliveryLineStatusUpdateImpl(
                                null, "CC_TEST1", "delivered 1", null, null, null, date("2017-02-21"), null, new BigDecimal(2), false, "INV-001",  date("2017-02-22")
                        ),
                        new OrderDeliveryLineStatusUpdateImpl(
                                null, "CC_TEST2", "delivered 2", null, null, null, date("2017-02-21"), null, new BigDecimal(1), false, "INV-001",  date("2017-02-22")
                        )
                )
        );

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.update
                        updated,
                        null,
                        Collections.singletonMap("update", update3))));

        orderService.update(updated);

        updated = orderService.findByReference(updated.getOrdernum());

        assertNotNull(updated);

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, updated.getOrderStatus());
        assertDeliveryStates(updated.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS_WAITING_PAYMENT);

        // check reserved quantity has not changed
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertEquals(1, updated.getDelivery().size());
        final CustomerOrderDelivery delivery3 = updated.getDelivery().iterator().next();
        assertEquals(date("2017-02-17"), delivery3.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-21"), delivery3.getDeliveryEstimatedMax());
        assertNull(delivery3.getDeliveryGuaranteed());
        assertEquals(date("2017-02-21"), delivery3.getDeliveryConfirmed());

        final Collection<CustomerOrderDeliveryDet> details3 = delivery3.getDetail();
        final Map<String, CustomerOrderDeliveryDet> detailsBySKU3 = new HashMap<String, CustomerOrderDeliveryDet>();
        for (final CustomerOrderDeliveryDet detail : details3) {
            detailsBySKU3.put(detail.getProductSkuCode(), detail);
        }

        final CustomerOrderDeliveryDet test31 = detailsBySKU3.get("CC_TEST1");
        assertNotNull(test31);
        assertEquals(date("2017-02-17"), test31.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-20"), test31.getDeliveryEstimatedMax());
        assertNull(test31.getDeliveryGuaranteed());
        assertEquals(date("2017-02-21"), test31.getDeliveryConfirmed());
        assertEquals("delivered 1", test31.getDeliveryRemarks());
        assertFalse(test31.isDeliveryRejected());
        assertFalse(test31.isDeliveryDifferent());
        assertEquals("INV-001", test31.getSupplierInvoiceNo());
        assertEquals(date("2017-02-22"), test31.getSupplierInvoiceDate());

        final CustomerOrderDeliveryDet test32 = detailsBySKU3.get("CC_TEST2");
        assertNotNull(test32);
        assertEquals(date("2017-02-17"), test32.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-21"), test32.getDeliveryEstimatedMax());
        assertNull(test32.getDeliveryGuaranteed());
        assertEquals(date("2017-02-21"), test32.getDeliveryConfirmed());
        assertEquals("delivered 2", test32.getDeliveryRemarks());
        assertFalse(test32.isDeliveryRejected());
        assertFalse(test32.isDeliveryDifferent());
        assertEquals("INV-001", test32.getSupplierInvoiceNo());
        assertEquals(date("2017-02-22"), test32.getSupplierInvoiceDate());

        // single payment AUTH because CAPTURE should be confirmed by CC manager
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

    }




    @Test
    public void testHandleStandardPaymentOfflineCaptureConfirmed() throws Exception {

        String label = assertPgFeatures("prePaymentGateway", false, false, false, false);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        orderService.update(customerOrder);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertTrue(confirmHandler.handle(
                new OrderEventImpl("", //evt.payment.confirmed
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        orderService.update(customerOrder);


        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);

        // Prepaid
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        final OrderDeliveryStatusUpdateImpl update1 = new OrderDeliveryStatusUpdateImpl(
                customerOrder.getOrdernum(),
                "WAREHOUSE_1",
                Arrays.<OrderDeliveryLineStatusUpdate>asList(
                        new OrderDeliveryLineStatusUpdateImpl(
                                null, "CC_TEST1", "packing", date("2017-02-17"), date("2017-02-20"), null, null, null, null, false, null, null
                        )
                )
        );

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.update
                        customerOrder,
                        null,
                        Collections.singletonMap("update", update1))));

        orderService.update(customerOrder);

        CustomerOrder updated = orderService.findByReference(customerOrder.getOrdernum());

        assertNotNull(updated);

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, updated.getOrderStatus());
        assertDeliveryStates(updated.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_PACKING);

        // Although we just void in auto, the allocation
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertEquals(1, updated.getDelivery().size());
        final CustomerOrderDelivery delivery1 = updated.getDelivery().iterator().next();
        assertEquals(date("2017-02-17"), delivery1.getDeliveryEstimatedMin());
        assertNull(delivery1.getDeliveryEstimatedMax());
        assertNull(delivery1.getDeliveryGuaranteed());

        final Collection<CustomerOrderDeliveryDet> details1 = delivery1.getDetail();
        final Map<String, CustomerOrderDeliveryDet> detailsBySKU1 = new HashMap<String, CustomerOrderDeliveryDet>();
        for (final CustomerOrderDeliveryDet detail : details1) {
            detailsBySKU1.put(detail.getProductSkuCode(), detail);
        }

        final CustomerOrderDeliveryDet test11 = detailsBySKU1.get("CC_TEST1");
        assertNotNull(test11);
        assertEquals(date("2017-02-17"), test11.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-20"), test11.getDeliveryEstimatedMax());
        assertNull(test11.getDeliveryGuaranteed());
        assertEquals("packing", test11.getDeliveryRemarks());
        assertNull(test11.getDeliveryConfirmed());
        assertFalse(test11.isDeliveryRejected());
        assertFalse(test11.isDeliveryDifferent());

        final CustomerOrderDeliveryDet test12 = detailsBySKU1.get("CC_TEST2");
        assertNotNull(test12);
        assertNull(test12.getDeliveryEstimatedMin());
        assertNull(test12.getDeliveryEstimatedMax());
        assertNull(test12.getDeliveryGuaranteed());
        assertNull(test12.getDeliveryRemarks());
        assertNull(test12.getDeliveryConfirmed());
        assertFalse(test12.isDeliveryRejected());
        assertFalse(test12.isDeliveryDifferent());


        final OrderDeliveryStatusUpdateImpl update2 = new OrderDeliveryStatusUpdateImpl(
                customerOrder.getOrdernum(),
                "WAREHOUSE_1",
                Arrays.<OrderDeliveryLineStatusUpdate>asList(
                        new OrderDeliveryLineStatusUpdateImpl(
                                null, "CC_TEST2", "packing 2", date("2017-02-17"), date("2017-02-21"), null, null, null, null, false, null, null
                        )
                )
        );

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.update
                        updated,
                        null,
                        Collections.singletonMap("update", update2))));

        orderService.update(updated);

        updated = orderService.findByReference(updated.getOrdernum());

        assertNotNull(updated);

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, updated.getOrderStatus());
        assertDeliveryStates(updated.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_PACKING);

        // check reserved quantity has not changed
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertEquals(1, updated.getDelivery().size());
        final CustomerOrderDelivery delivery2 = updated.getDelivery().iterator().next();
        assertEquals(date("2017-02-17"), delivery2.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-21"), delivery2.getDeliveryEstimatedMax());
        assertNull(delivery2.getDeliveryGuaranteed());

        final Collection<CustomerOrderDeliveryDet> details2 = delivery2.getDetail();
        final Map<String, CustomerOrderDeliveryDet> detailsBySKU2 = new HashMap<String, CustomerOrderDeliveryDet>();
        for (final CustomerOrderDeliveryDet detail : details2) {
            detailsBySKU2.put(detail.getProductSkuCode(), detail);
        }

        final CustomerOrderDeliveryDet test21 = detailsBySKU2.get("CC_TEST1");
        assertNotNull(test21);
        assertEquals(date("2017-02-17"), test21.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-20"), test21.getDeliveryEstimatedMax());
        assertNull(test21.getDeliveryGuaranteed());
        assertEquals("packing", test21.getDeliveryRemarks());
        assertNull(test21.getDeliveryConfirmed());
        assertFalse(test21.isDeliveryRejected());
        assertFalse(test21.isDeliveryDifferent());

        final CustomerOrderDeliveryDet test22 = detailsBySKU2.get("CC_TEST2");
        assertNotNull(test22);
        assertEquals(date("2017-02-17"), test22.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-21"), test22.getDeliveryEstimatedMax());
        assertNull(test22.getDeliveryGuaranteed());
        assertEquals("packing 2", test22.getDeliveryRemarks());
        assertNull(test22.getDeliveryConfirmed());
        assertFalse(test22.isDeliveryRejected());
        assertFalse(test22.isDeliveryDifferent());


        final OrderDeliveryStatusUpdateImpl update3 = new OrderDeliveryStatusUpdateImpl(
                customerOrder.getOrdernum(),
                "WAREHOUSE_1",
                Arrays.<OrderDeliveryLineStatusUpdate>asList(
                        new OrderDeliveryLineStatusUpdateImpl(
                                null, "CC_TEST1", "delivered 1", null, null, null, date("2017-02-21"), null, new BigDecimal(2), false, "INV-001",  date("2017-02-22")
                        ),
                        new OrderDeliveryLineStatusUpdateImpl(
                                null, "CC_TEST2", "delivered 2", null, null, null, date("2017-02-21"), null, new BigDecimal(1), false, "INV-001",  date("2017-02-22")
                        )
                )
        );

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.update
                        updated,
                        null,
                        Collections.singletonMap("update", update3))));

        orderService.update(updated);

        updated = orderService.findByReference(updated.getOrdernum());

        assertNotNull(updated);

        assertEquals(CustomerOrder.ORDER_STATUS_COMPLETED, updated.getOrderStatus());
        assertDeliveryStates(updated.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);

        // check reserved quantity has not changed
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertEquals(1, updated.getDelivery().size());
        final CustomerOrderDelivery delivery3 = updated.getDelivery().iterator().next();
        assertEquals(date("2017-02-17"), delivery3.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-21"), delivery3.getDeliveryEstimatedMax());
        assertNull(delivery3.getDeliveryGuaranteed());
        assertEquals(date("2017-02-21"), delivery3.getDeliveryConfirmed());

        final Collection<CustomerOrderDeliveryDet> details3 = delivery3.getDetail();
        final Map<String, CustomerOrderDeliveryDet> detailsBySKU3 = new HashMap<String, CustomerOrderDeliveryDet>();
        for (final CustomerOrderDeliveryDet detail : details3) {
            detailsBySKU3.put(detail.getProductSkuCode(), detail);
        }

        final CustomerOrderDeliveryDet test31 = detailsBySKU3.get("CC_TEST1");
        assertNotNull(test31);
        assertEquals(date("2017-02-17"), test31.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-20"), test31.getDeliveryEstimatedMax());
        assertNull(test31.getDeliveryGuaranteed());
        assertEquals(date("2017-02-21"), test31.getDeliveryConfirmed());
        assertEquals("delivered 1", test31.getDeliveryRemarks());
        assertFalse(test31.isDeliveryRejected());
        assertFalse(test31.isDeliveryDifferent());
        assertEquals("INV-001", test31.getSupplierInvoiceNo());
        assertEquals(date("2017-02-22"), test31.getSupplierInvoiceDate());

        final CustomerOrderDeliveryDet test32 = detailsBySKU3.get("CC_TEST2");
        assertNotNull(test32);
        assertEquals(date("2017-02-17"), test32.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-21"), test32.getDeliveryEstimatedMax());
        assertNull(test32.getDeliveryGuaranteed());
        assertEquals(date("2017-02-21"), test32.getDeliveryConfirmed());
        assertEquals("delivered 2", test32.getDeliveryRemarks());
        assertFalse(test32.isDeliveryRejected());
        assertFalse(test32.isDeliveryDifferent());
        assertEquals("INV-001", test32.getSupplierInvoiceNo());
        assertEquals(date("2017-02-22"), test32.getSupplierInvoiceDate());

        // Prepaid
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK, true);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

    }




    @Test
    public void testHandleStandardPaymentOnline() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        orderService.update(customerOrder);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        final OrderDeliveryStatusUpdateImpl update1 = new OrderDeliveryStatusUpdateImpl(
                customerOrder.getOrdernum(),
                "WAREHOUSE_1",
                Arrays.<OrderDeliveryLineStatusUpdate>asList(
                        new OrderDeliveryLineStatusUpdateImpl(
                                null, "CC_TEST1", "packing", date("2017-02-17"), date("2017-02-20"), null, null, null, null, false, null, null
                        )
                )
        );

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.update
                        customerOrder,
                        null,
                        Collections.singletonMap("update", update1))));

        orderService.update(customerOrder);

        CustomerOrder updated = orderService.findByReference(customerOrder.getOrdernum());

        assertNotNull(updated);

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, updated.getOrderStatus());
        assertDeliveryStates(updated.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_PACKING);

        // Although we just void in auto, the allocation
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertEquals(1, updated.getDelivery().size());
        final CustomerOrderDelivery delivery1 = updated.getDelivery().iterator().next();
        assertEquals(date("2017-02-17"), delivery1.getDeliveryEstimatedMin());
        assertNull(delivery1.getDeliveryEstimatedMax());
        assertNull(delivery1.getDeliveryGuaranteed());

        final Collection<CustomerOrderDeliveryDet> details1 = delivery1.getDetail();
        final Map<String, CustomerOrderDeliveryDet> detailsBySKU1 = new HashMap<String, CustomerOrderDeliveryDet>();
        for (final CustomerOrderDeliveryDet detail : details1) {
            detailsBySKU1.put(detail.getProductSkuCode(), detail);
        }

        final CustomerOrderDeliveryDet test11 = detailsBySKU1.get("CC_TEST1");
        assertNotNull(test11);
        assertEquals(date("2017-02-17"), test11.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-20"), test11.getDeliveryEstimatedMax());
        assertNull(test11.getDeliveryGuaranteed());
        assertEquals("packing", test11.getDeliveryRemarks());
        assertNull(test11.getDeliveryConfirmed());
        assertFalse(test11.isDeliveryRejected());
        assertFalse(test11.isDeliveryDifferent());

        final CustomerOrderDeliveryDet test12 = detailsBySKU1.get("CC_TEST2");
        assertNotNull(test12);
        assertNull(test12.getDeliveryEstimatedMin());
        assertNull(test12.getDeliveryEstimatedMax());
        assertNull(test12.getDeliveryGuaranteed());
        assertNull(test12.getDeliveryRemarks());
        assertNull(test12.getDeliveryConfirmed());
        assertFalse(test12.isDeliveryRejected());
        assertFalse(test12.isDeliveryDifferent());


        final OrderDeliveryStatusUpdateImpl update2 = new OrderDeliveryStatusUpdateImpl(
                customerOrder.getOrdernum(),
                "WAREHOUSE_1",
                Arrays.<OrderDeliveryLineStatusUpdate>asList(
                        new OrderDeliveryLineStatusUpdateImpl(
                                null, "CC_TEST2", "packing 2", date("2017-02-17"), date("2017-02-21"), null, null, null, null, false, null, null
                        )
                )
        );

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.update
                        updated,
                        null,
                        Collections.singletonMap("update", update2))));

        orderService.update(updated);

        updated = orderService.findByReference(updated.getOrdernum());

        assertNotNull(updated);

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, updated.getOrderStatus());
        assertDeliveryStates(updated.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_PACKING);

        // check reserved quantity has not changed
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertEquals(1, updated.getDelivery().size());
        final CustomerOrderDelivery delivery2 = updated.getDelivery().iterator().next();
        assertEquals(date("2017-02-17"), delivery2.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-21"), delivery2.getDeliveryEstimatedMax());
        assertNull(delivery2.getDeliveryGuaranteed());

        final Collection<CustomerOrderDeliveryDet> details2 = delivery2.getDetail();
        final Map<String, CustomerOrderDeliveryDet> detailsBySKU2 = new HashMap<String, CustomerOrderDeliveryDet>();
        for (final CustomerOrderDeliveryDet detail : details2) {
            detailsBySKU2.put(detail.getProductSkuCode(), detail);
        }

        final CustomerOrderDeliveryDet test21 = detailsBySKU2.get("CC_TEST1");
        assertNotNull(test21);
        assertEquals(date("2017-02-17"), test21.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-20"), test21.getDeliveryEstimatedMax());
        assertNull(test21.getDeliveryGuaranteed());
        assertEquals("packing", test21.getDeliveryRemarks());
        assertNull(test21.getDeliveryConfirmed());
        assertFalse(test21.isDeliveryRejected());
        assertFalse(test21.isDeliveryDifferent());

        final CustomerOrderDeliveryDet test22 = detailsBySKU2.get("CC_TEST2");
        assertNotNull(test22);
        assertEquals(date("2017-02-17"), test22.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-21"), test22.getDeliveryEstimatedMax());
        assertNull(test22.getDeliveryGuaranteed());
        assertEquals("packing 2", test22.getDeliveryRemarks());
        assertNull(test22.getDeliveryConfirmed());
        assertFalse(test22.isDeliveryRejected());
        assertFalse(test22.isDeliveryDifferent());

        // We still got only AUTH because delivery is not confirmed
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        final OrderDeliveryStatusUpdateImpl update3 = new OrderDeliveryStatusUpdateImpl(
                customerOrder.getOrdernum(),
                "WAREHOUSE_1",
                Arrays.<OrderDeliveryLineStatusUpdate>asList(
                        new OrderDeliveryLineStatusUpdateImpl(
                                null, "CC_TEST1", "delivered 1", null, null, null, date("2017-02-21"), null, new BigDecimal(2), false, "INV-001",  date("2017-02-22")
                        ),
                        new OrderDeliveryLineStatusUpdateImpl(
                                null, "CC_TEST2", "delivered 2", null, null, null, date("2017-02-21"), null, new BigDecimal(1), false, "INV-001",  date("2017-02-22")
                        )
                )
        );

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.update
                        updated,
                        null,
                        Collections.singletonMap("update", update3))));

        orderService.update(updated);

        updated = orderService.findByReference(updated.getOrdernum());

        assertNotNull(updated);

        assertEquals(CustomerOrder.ORDER_STATUS_COMPLETED, updated.getOrderStatus());
        assertDeliveryStates(updated.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);

        // check reserved quantity has not changed
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertEquals(1, updated.getDelivery().size());
        final CustomerOrderDelivery delivery3 = updated.getDelivery().iterator().next();
        assertEquals(date("2017-02-17"), delivery3.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-21"), delivery3.getDeliveryEstimatedMax());
        assertNull(delivery3.getDeliveryGuaranteed());
        assertEquals(date("2017-02-21"), delivery3.getDeliveryConfirmed());

        final Collection<CustomerOrderDeliveryDet> details3 = delivery3.getDetail();
        final Map<String, CustomerOrderDeliveryDet> detailsBySKU3 = new HashMap<String, CustomerOrderDeliveryDet>();
        for (final CustomerOrderDeliveryDet detail : details3) {
            detailsBySKU3.put(detail.getProductSkuCode(), detail);
        }

        final CustomerOrderDeliveryDet test31 = detailsBySKU3.get("CC_TEST1");
        assertNotNull(test31);
        assertEquals(date("2017-02-17"), test31.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-20"), test31.getDeliveryEstimatedMax());
        assertNull(test31.getDeliveryGuaranteed());
        assertEquals(date("2017-02-21"), test31.getDeliveryConfirmed());
        assertEquals("delivered 1", test31.getDeliveryRemarks());
        assertFalse(test31.isDeliveryRejected());
        assertFalse(test31.isDeliveryDifferent());
        assertEquals("INV-001", test31.getSupplierInvoiceNo());
        assertEquals(date("2017-02-22"), test31.getSupplierInvoiceDate());

        final CustomerOrderDeliveryDet test32 = detailsBySKU3.get("CC_TEST2");
        assertNotNull(test32);
        assertEquals(date("2017-02-17"), test32.getDeliveryEstimatedMin());
        assertEquals(date("2017-02-21"), test32.getDeliveryEstimatedMax());
        assertNull(test32.getDeliveryGuaranteed());
        assertEquals(date("2017-02-21"), test32.getDeliveryConfirmed());
        assertEquals("delivered 2", test32.getDeliveryRemarks());
        assertFalse(test32.isDeliveryRejected());
        assertFalse(test32.isDeliveryDifferent());
        assertEquals("INV-001", test32.getSupplierInvoiceNo());
        assertEquals(date("2017-02-22"), test32.getSupplierInvoiceDate());

        // Shipment confirmation triggers CAPTURE
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74", "689.74"),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE, Boolean.TRUE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

    }



    @Test
    public void testHandleStandardPaymentOnlinePartialShipment() throws Exception {

        String label = assertPgFeatures("testPaymentGateway", false, true, true, true);

        CustomerOrder customerOrder = createTestOrder(TestOrderType.STANDARD, label, false);

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        orderService.update(customerOrder);

        // check reserved quantity
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "2.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "1.00");

        assertDeliveryStates(customerOrder.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT);

        // Authorisation
        assertSinglePaymentEntry(customerOrder.getOrdernum(), "689.74", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK, false);
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("0.00", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());

        final OrderDeliveryStatusUpdateImpl update1 = new OrderDeliveryStatusUpdateImpl(
                customerOrder.getOrdernum(),
                "WAREHOUSE_1",
                Arrays.<OrderDeliveryLineStatusUpdate>asList(
                        new OrderDeliveryLineStatusUpdateImpl(
                                null, "CC_TEST1", "delivered", date("2017-02-17"), date("2017-02-20"), null, date("2017-02-21"), null, new BigDecimal(2), false, "INV-001",  date("2017-02-22")
                        )
                )
        );

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.update
                        customerOrder,
                        null,
                        Collections.singletonMap("update", update1))));

        orderService.update(customerOrder);

        CustomerOrder updated = orderService.findByReference(customerOrder.getOrdernum());

        assertNotNull(updated);

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, updated.getOrderStatus());
        assertDeliveryStates(updated.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_PACKING);

        // Although we just void in auto, the allocation
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertEquals(1, updated.getDelivery().size());
        final CustomerOrderDelivery delivery1 = updated.getDelivery().iterator().next();
        assertNull(delivery1.getDeliveryEstimatedMin());
        assertNull(delivery1.getDeliveryEstimatedMax());
        assertNull(delivery1.getDeliveryGuaranteed());

        final Collection<CustomerOrderDeliveryDet> details1 = delivery1.getDetail();
        final Map<String, CustomerOrderDeliveryDet> detailsBySKU1 = new HashMap<String, CustomerOrderDeliveryDet>();
        for (final CustomerOrderDeliveryDet detail : details1) {
            detailsBySKU1.put(detail.getProductSkuCode(), detail);
        }

        final CustomerOrderDeliveryDet test11 = detailsBySKU1.get("CC_TEST1");
        assertNotNull(test11);
        assertNull(test11.getDeliveryEstimatedMin());
        assertNull(test11.getDeliveryEstimatedMax());
        assertNull(test11.getDeliveryGuaranteed());
        assertEquals("delivered", test11.getDeliveryRemarks());
        assertEquals(date("2017-02-21"), test11.getDeliveryConfirmed());
        assertFalse(test11.isDeliveryRejected());
        assertFalse(test11.isDeliveryDifferent());

        final CustomerOrderDeliveryDet test12 = detailsBySKU1.get("CC_TEST2");
        assertNotNull(test12);
        assertNull(test12.getDeliveryEstimatedMin());
        assertNull(test12.getDeliveryEstimatedMax());
        assertNull(test12.getDeliveryGuaranteed());
        assertNull(test12.getDeliveryRemarks());
        assertNull(test12.getDeliveryConfirmed());
        assertFalse(test12.isDeliveryRejected());
        assertFalse(test12.isDeliveryDifferent());


        final OrderDeliveryStatusUpdateImpl update2 = new OrderDeliveryStatusUpdateImpl(
                customerOrder.getOrdernum(),
                "WAREHOUSE_1",
                Arrays.<OrderDeliveryLineStatusUpdate>asList(
                        new OrderDeliveryLineStatusUpdateImpl(
                                null, "CC_TEST2", "rejected", null, null, null, null, null, null, true, "INV-001",  date("2017-02-22")
                        )
                )
        );

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.delivery.update
                        updated,
                        null,
                        Collections.singletonMap("update", update2))));

        orderService.update(updated);

        updated = orderService.findByReference(updated.getOrdernum());

        assertNotNull(updated);

        assertEquals(CustomerOrder.ORDER_STATUS_COMPLETED, updated.getOrderStatus());
        assertDeliveryStates(updated.getDelivery(), CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);

        // check reserved quantity has not changed
        assertInventory(WAREHOUSE_ID, "CC_TEST1", "9.00", "0.00");
        assertInventory(WAREHOUSE_ID, "CC_TEST2", "1.00", "0.00");

        assertEquals(1, updated.getDelivery().size());
        final CustomerOrderDelivery delivery2 = updated.getDelivery().iterator().next();
        assertNull(delivery2.getDeliveryEstimatedMin());
        assertNull(delivery2.getDeliveryEstimatedMax());
        assertNull(delivery2.getDeliveryGuaranteed());
        assertEquals(date("2017-02-21"), delivery2.getDeliveryConfirmed());

        final Collection<CustomerOrderDeliveryDet> details2 = delivery2.getDetail();
        final Map<String, CustomerOrderDeliveryDet> detailsBySKU2 = new HashMap<String, CustomerOrderDeliveryDet>();
        for (final CustomerOrderDeliveryDet detail : details2) {
            detailsBySKU2.put(detail.getProductSkuCode(), detail);
        }

        final CustomerOrderDeliveryDet test21 = detailsBySKU2.get("CC_TEST1");
        assertNotNull(test21);
        assertNull(test21.getDeliveryEstimatedMin());
        assertNull(test21.getDeliveryEstimatedMax());
        assertNull(test21.getDeliveryGuaranteed());
        assertEquals("delivered", test21.getDeliveryRemarks());
        assertEquals(date("2017-02-21"), test21.getDeliveryConfirmed());
        assertFalse(test21.isDeliveryRejected());
        assertFalse(test21.isDeliveryDifferent());

        final CustomerOrderDeliveryDet test22 = detailsBySKU2.get("CC_TEST2");
        assertNotNull(test22);
        assertNull(test22.getDeliveryEstimatedMin());
        assertNull(test22.getDeliveryEstimatedMax());
        assertNull(test22.getDeliveryGuaranteed());
        assertEquals("rejected", test22.getDeliveryRemarks());
        assertNull(test22.getDeliveryConfirmed());
        assertTrue(test22.isDeliveryRejected());
        assertTrue(test22.isDeliveryDifferent());

        // Shipment confirmation triggers CAPTURE
        assertMultiPaymentEntry(customerOrder.getOrdernum(),
                Arrays.asList("689.74",                     "689.74"),
                Arrays.asList(PaymentGateway.AUTH,          PaymentGateway.CAPTURE),
                Arrays.asList(Payment.PAYMENT_STATUS_OK,    Payment.PAYMENT_STATUS_OK),
                Arrays.asList(Boolean.FALSE,                Boolean.TRUE));
        assertEquals("689.74", customerOrder.getOrderTotal().toPlainString());
        assertEquals("689.74", paymentService.getOrderAmount(customerOrder.getOrdernum()).toPlainString());

    }


}