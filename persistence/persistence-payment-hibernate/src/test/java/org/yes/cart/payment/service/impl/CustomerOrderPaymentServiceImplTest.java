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

package org.yes.cart.payment.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.entity.impl.CustomerOrderPaymentEntity;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.utils.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:45:45
 */
public class CustomerOrderPaymentServiceImplTest extends BasePaymentModuleDBTestCase {

    private CustomerOrderPaymentService service;


    @Override
    @Before
    public void setUp() throws Exception {
        this.service = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
        super.setUp();
    }


    @Test
    public void testFindCustomerOrderPayments() throws Exception {

        service.create(createCustomerOrderPayment(new BigDecimal("123.00"), "FIND-123-45", "FIND-123-45-0", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK));
        service.create(createCustomerOrderPayment(new BigDecimal("123.00"), "FIND-123-45", "FIND-123-45-0", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_OK));
        service.create(createCustomerOrderPayment(new BigDecimal("123.00"), "FIND-123-45", "FIND-123-45-0", PaymentGateway.REFUND, Payment.PAYMENT_STATUS_PROCESSING));
        service.create(createCustomerOrderPayment(new BigDecimal("123.00"), "FIND-123-45", "FIND-123-45-0", PaymentGateway.VOID_CAPTURE, Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED));
        service.create(createCustomerOrderPayment(new BigDecimal("123.00"), "FIND-123-45", "FIND-123-45-0", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK));


        final Set<String> shopAll = null;
        final Set<String> shop10 = Collections.singleton("SHOIP1");


        List<CustomerOrderPayment> list;
        int count;

        final Map<String, List> filterNone = null;

        count = service.findCustomerOrderPaymentCount(shopAll, filterNone);
        assertTrue(count > 0);
        list = service.findCustomerOrderPayment(0, 1, "createdTimestamp", false, shopAll, filterNone);
        assertFalse(list.isEmpty());


        final Map<String, List> filterAny = new HashMap<>();
        SearchContext.JoinMode.OR.setMode(filterAny);
        filterAny.put("orderNumber", Collections.singletonList("FIND-123"));
        filterAny.put("orderShipment", Collections.singletonList("ZZZZZ"));

        count = service.findCustomerOrderPaymentCount(shopAll, filterAny);
        assertEquals(5, count);
        list = service.findCustomerOrderPayment(0, 3, "createdTimestamp", false, shopAll, filterAny);
        assertEquals(3, list.size());
        list = service.findCustomerOrderPayment(3, 3, "createdTimestamp", false, shopAll, filterAny);
        assertEquals(2, list.size());


        final Map<String, List> filterSpecific = new HashMap<>();
        filterSpecific.put("orderNumber", Collections.singletonList("FIND-123"));
        filterSpecific.put("orderShipment", Collections.singletonList("FIND-123"));

        count = service.findCustomerOrderPaymentCount(shopAll, filterSpecific);
        assertEquals(5, count);
        list = service.findCustomerOrderPayment(0, 1, "createdTimestamp", false, shopAll, filterSpecific);
        assertEquals(1, list.size());

        final Map<String, List> filterNoMatch = Collections.singletonMap("orderNumber", Collections.singletonList("ZZZZZZZ"));

        count = service.findCustomerOrderPaymentCount(shopAll, filterNoMatch);
        assertEquals(0, count);
        list = service.findCustomerOrderPayment(0, 1, "createdTimestamp", false, shopAll, filterNoMatch);
        assertEquals(0, list.size());

        final Map<String, List> filterIncludeProcessing = new HashMap<>();
        filterIncludeProcessing.put("orderNumber", Collections.singletonList("FIND-123"));
        filterIncludeProcessing.put("paymentProcessorResult", Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED));

        count = service.findCustomerOrderPaymentCount(shop10, filterIncludeProcessing);
        assertEquals(2, count);
        list = service.findCustomerOrderPayment(0, 1, "createdTimestamp", false, shop10, filterIncludeProcessing);
        assertFalse(list.isEmpty());

        final Map<String, List> filterIncludeProcessingVoidCapture = new HashMap<>();
        filterIncludeProcessingVoidCapture.put("orderNumber", Collections.singletonList("FIND-123"));
        filterIncludeProcessingVoidCapture.put("transactionOperation", Collections.singletonList(PaymentGateway.VOID_CAPTURE));
        filterIncludeProcessingVoidCapture.put("paymentProcessorResult", Arrays.asList(Payment.PAYMENT_STATUS_PROCESSING, Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED));

        count = service.findCustomerOrderPaymentCount(shop10, filterIncludeProcessingVoidCapture);
        assertEquals(1, count);
        list = service.findCustomerOrderPayment(0, 1, "createdTimestamp", false, shop10, filterIncludeProcessingVoidCapture);
        assertFalse(list.isEmpty());


        final Map<String, List> filterCreated = new HashMap<>();
        filterCreated.put("orderNumber", Collections.singletonList("FIND-123"));
        filterCreated.put("createdTimestamp", Arrays.asList(
                SearchContext.MatchMode.GE.toParam(DateUtils.iParseSDT("2019-01-01")),
                SearchContext.MatchMode.LT.toParam(DateUtils.iParseSDT("2099-01-01"))
                )
        );

        count = service.findCustomerOrderPaymentCount(shop10, filterCreated);
        assertEquals(5, count);
        list = service.findCustomerOrderPayment(0, 2, "createdTimestamp", false, shop10, filterCreated);
        assertEquals(2, list.size());



    }

    @Test
    public void testFindBy() {
        CustomerOrderPayment fb1 = createCustomerOrderPayment(new BigDecimal("123.00"), "FB1-123-45", "FB1-123-45-0", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK);
        fb1 = service.create(fb1);
        assertTrue(fb1.getCustomerOrderPaymentId() > 0);
        CustomerOrderPayment fb2 = createCustomerOrderPayment(new BigDecimal("123.00"), "FB2-123-45", "FB2-123-45-0", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK);
        fb2 = service.create(fb2);
        assertTrue(fb2.getCustomerOrderPaymentId() > 0);

        assertTrue(service.findCustomerOrderPayment(null, null, (String) null, (String) null).size() >= 2);
        assertEquals(Payment.PAYMENT_STATUS_OK, service.findCustomerOrderPayment(null, null, Payment.PAYMENT_STATUS_OK, null).get(0).getPaymentProcessorResult());
        assertEquals(PaymentGateway.AUTH, service.findCustomerOrderPayment(null, null, null, PaymentGateway.AUTH).get(0).getTransactionOperation());
        assertEquals(fb1.getOrderNumber(), service.findCustomerOrderPayment(fb1.getOrderNumber(), null, (String) null, (String) null).get(0).getOrderNumber());
        assertEquals(fb1.getOrderShipment(), service.findCustomerOrderPayment(null, fb1.getOrderShipment(), (String) null, (String) null).get(0).getOrderShipment());
        assertEquals(fb1.getCustomerOrderPaymentId(), service.findCustomerOrderPayment(fb1.getOrderNumber(), fb1.getOrderShipment(), Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).get(0).getCustomerOrderPaymentId());
    }

    @Test
    public void testFindByArray() {
        CustomerOrderPayment fba1 = createCustomerOrderPayment(new BigDecimal("123.00"), "FBA1-123-45", "FBA1-123-45-0", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK);
        fba1 = service.create(fba1);
        assertTrue(fba1.getCustomerOrderPaymentId() > 0);
        CustomerOrderPayment fba2 = createCustomerOrderPayment(new BigDecimal("123.00"), "FBA2-123-45", "FBA2-123-45-0", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK);
        fba2 = service.create(fba2);
        assertTrue(fba2.getCustomerOrderPaymentId() > 0);

        assertTrue(service.findCustomerOrderPayment(null, null, (String[]) null, (String[]) null).size() >= 2);
        assertEquals(Payment.PAYMENT_STATUS_OK, service.findCustomerOrderPayment(null, null, new String[] { Payment.PAYMENT_STATUS_OK }, (String[]) null).get(0).getPaymentProcessorResult());
        assertEquals(PaymentGateway.AUTH, service.findCustomerOrderPayment(null, null, null, new String[] { PaymentGateway.AUTH }).get(0).getTransactionOperation());
        assertEquals(fba1.getOrderNumber(), service.findCustomerOrderPayment(fba1.getOrderNumber(), null, (String[]) null, (String[]) null).get(0).getOrderNumber());
        assertEquals(fba1.getOrderShipment(), service.findCustomerOrderPayment(null, fba1.getOrderShipment(), (String[]) null, (String[]) null).get(0).getOrderShipment());
        assertEquals(fba1.getCustomerOrderPaymentId(), service.findCustomerOrderPayment(fba1.getOrderNumber(), fba1.getOrderShipment(), new String[] { Payment.PAYMENT_STATUS_OK }, new String[] { PaymentGateway.AUTH }).get(0).getCustomerOrderPaymentId());
    }

    @Test
    public void testGetOrderAmountAuth() {
        service.create(createCustomerOrderPayment(new BigDecimal("223.00"), "A-OK-223-45", "A-OK-223-45-0", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK));
        service.create(createCustomerOrderPayment(new BigDecimal("0.45"), "A-OK-223-45", "A-OK-223-45-1", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK));
        assertEquals("0.00", service.getOrderAmount("A-OK-223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountAuthCaptureOk() {
        service.create(createCustomerOrderPayment(new BigDecimal("223.00"), "AC-OK-223-45", "AC-OK-223-45-0", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK));
        service.create(createCustomerOrderPayment(new BigDecimal("0.45"), "AC-OK-223-45", "AC-OK-223-45-1", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK));
        assertEquals("223.45", service.getOrderAmount("AC-OK-223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountAuthCaptureNotOk() {
        service.create(createCustomerOrderPayment(new BigDecimal("223.00"), "AC-PF-223-45", "AC-PF-223-45-0", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_FAILED));
        service.create(createCustomerOrderPayment(new BigDecimal("0.45"), "AC-PF-223-45", "AC-PF-223-45-1", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK));
        assertEquals("0.45", service.getOrderAmount("AC-PF-223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountCaptureOk() {
        service.create(createCustomerOrderPayment(new BigDecimal("223.00"), "C-OK-223-45", "C-OK-223-45-0", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_OK));
        service.create(createCustomerOrderPayment(new BigDecimal("0.45"), "C-OK-223-45", "C-OK-223-45-1", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_OK));
        assertEquals("223.45", service.getOrderAmount("C-OK-223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountCaptureNotOk() {
        service.create(createCustomerOrderPayment(new BigDecimal("223.00"), "C-PR-223-45", "C-PR-223-45-0", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_PROCESSING));
        service.create(createCustomerOrderPayment(new BigDecimal("0.45"), "C-PR-223-45", "C-PR-223-45-1", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_OK));
        assertEquals("0.45", service.getOrderAmount("C-PR-223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountCaptureRefundOk() {
        service.create(createCustomerOrderPayment(new BigDecimal("223.00"), "CR-OK-223-45", "CR-OK-223-45-0", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_OK));
        service.create(createCustomerOrderPayment(new BigDecimal("0.45"), "CR-OK-223-45", "CR-OK-223-45-1", PaymentGateway.REFUND, Payment.PAYMENT_STATUS_OK));
        assertEquals("222.55", service.getOrderAmount("CR-OK-223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountCaptureRefundFailed() {
        service.create(createCustomerOrderPayment(new BigDecimal("223.00"), "CR-MP-223-45", "CR-MP-223-45-0", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_OK));
        service.create(createCustomerOrderPayment(new BigDecimal("0.45"), "CR-MP-223-45", "CR-MP-223-45-1", PaymentGateway.REFUND, Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED));
        assertEquals("223.00", service.getOrderAmount("CR-MP-223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountCaptureVoidNotMatching() {
        service.create(createCustomerOrderPayment(new BigDecimal("223.00"), "CV-OK-223-45", "CV-OK-223-45-0", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_OK));
        service.create(createCustomerOrderPayment(new BigDecimal("0.45"), "CV-OK-223-45", "CV-OK-223-45-1", PaymentGateway.VOID_CAPTURE, Payment.PAYMENT_STATUS_OK));
        assertEquals("223.00", service.getOrderAmount("CV-OK-223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountCaptureVoidOk() {
        service.create(createCustomerOrderPayment(new BigDecimal("223.00"), "CVF-OK-223-45", "CVF-OK-223-45-0", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_OK));
        service.create(createCustomerOrderPayment(new BigDecimal("223.00"), "CVF-OK-223-45", "CVF-OK-223-45-1", PaymentGateway.VOID_CAPTURE, Payment.PAYMENT_STATUS_OK));
        assertEquals("0.00", service.getOrderAmount("CVF-OK-223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountCaptureVoidNotOk() {
        service.create(createCustomerOrderPayment(new BigDecimal("223.00"), "CV-PR-223-45", "CV-PR-223-45-0", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_OK));
        service.create(createCustomerOrderPayment(new BigDecimal("0.45"), "CV-PR-223-45", "CV-PR-223-45-1", PaymentGateway.VOID_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING));
        assertEquals("223.00", service.getOrderAmount("CV-PR-223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountCaptureProcessingVoid() {
        service.create(createCustomerOrderPayment(new BigDecimal("223.00"), "CV-PR2-223-45", "CV-PR2-223-45-0", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_PROCESSING));
        service.create(createCustomerOrderPayment(new BigDecimal("0.45"), "CV-PR2-223-45", "CV-PR2-223-45-1", PaymentGateway.VOID_CAPTURE, Payment.PAYMENT_STATUS_OK));
        assertEquals("0.00", service.getOrderAmount("CV-PR2-223-45").toPlainString());
    }

    private CustomerOrderPayment createCustomerOrderPayment(final BigDecimal amount,
                                                            final String orderNum,
                                                            final String shipmentNum,
                                                            final String operation,
                                                            final String result) {
        CustomerOrderPayment payment = new CustomerOrderPaymentEntity();
        payment.setCardExpireMonth("02");
        payment.setCardExpireYear("2020");
        payment.setCardHolderName("Bender Rodrigues");
        payment.setCardIssueNumber("123");
        payment.setCardNumber("41111111115678");
        payment.setCardStartDate(LocalDate.now());
        payment.setOrderCurrency("EUR");
        payment.setOrderDate(LocalDateTime.now());
        payment.setPaymentAmount(amount);
        payment.setTaxAmount(amount.divide(new BigDecimal("1.2"), 10, BigDecimal.ROUND_UP).multiply(new BigDecimal("1.2")).setScale(2, BigDecimal.ROUND_UP));
        payment.setOrderNumber(orderNum);
        payment.setShopCode("SHOIP1");
        payment.setOrderShipment(shipmentNum);
        payment.setPaymentProcessorBatchSettlement(false);
        payment.setPaymentProcessorResult(result);
        payment.setTransactionAuthorizationCode("0987654321");
        payment.setTransactionGatewayLabel("testPg");
        payment.setTransactionOperation(operation);
        payment.setTransactionOperationResultCode("100");
        payment.setTransactionOperationResultMessage("Ok");
        payment.setTransactionReferenceId("123-45-0");
        payment.setTransactionRequestToken("token-0-012340-450-4253023-604536046535-60");
        return payment;
    }
}
