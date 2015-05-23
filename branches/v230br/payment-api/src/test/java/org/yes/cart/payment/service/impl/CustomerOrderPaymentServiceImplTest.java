/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

import org.junit.Test;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.entity.impl.CustomerOrderPaymentEntity;
import org.yes.cart.payment.service.CustomerOrderPaymentService;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:45:45
 */
public class CustomerOrderPaymentServiceImplTest extends BasePaymentModuleDBTestCase {

    @Test
    public void testFindBy() {
        CustomerOrderPaymentService service = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
        CustomerOrderPayment payment = getCustomerOrderPayment(new BigDecimal("123.00"), "123-45", "123-45-0", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK);
        payment = service.create(payment);
        assertTrue(payment.getCustomerOrderPaymentId() > 0);
        assertEquals(1, service.findBy(null, null, (String) null, (String) null).size());
        assertEquals("5678", service.findBy(null, null, (String) null, (String) null).get(0).getCardNumber());
        assertEquals(1, service.findBy(null, null, Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(1, service.findBy(null, null, null, PaymentGateway.AUTH).size());
        assertEquals(1, service.findBy("123-45", null, (String) null, (String) null).size());
        assertEquals(1, service.findBy(null, "123-45-0", (String) null, (String) null).size());
        assertEquals(1, service.findBy("123-45", "123-45-0", Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());
    }

    @Test
    public void testFindByArray() {
        CustomerOrderPaymentService service = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
        CustomerOrderPayment payment = getCustomerOrderPayment(new BigDecimal("123.00"), "123-45", "123-45-0", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK);
        payment = service.create(payment);
        assertTrue(payment.getCustomerOrderPaymentId() > 0);
        assertEquals(1, service.findBy(null, null, (String[]) null, (String[]) null).size());
        assertEquals("5678", service.findBy(null, null, (String[]) null, (String[]) null).get(0).getCardNumber());
        assertEquals(1, service.findBy(null, null, new String[] { Payment.PAYMENT_STATUS_OK }, (String[]) null).size());
        assertEquals(1, service.findBy(null, null, null, new String[] { PaymentGateway.AUTH }).size());
        assertEquals(1, service.findBy("123-45", null, (String[]) null, (String[]) null).size());
        assertEquals(1, service.findBy(null, "123-45-0", (String[]) null, (String[]) null).size());
        assertEquals(1, service.findBy("123-45", "123-45-0", new String[] { Payment.PAYMENT_STATUS_OK }, new String[] { PaymentGateway.AUTH }).size());
    }

    @Test
    public void testGetOrderAmountAuth() {
        CustomerOrderPaymentService service = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
        service.create(getCustomerOrderPayment(new BigDecimal("223.00"), "223-45", "223-45-0", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK));
        service.create(getCustomerOrderPayment(new BigDecimal("0.45"), "223-45", "223-45-1", PaymentGateway.AUTH, Payment.PAYMENT_STATUS_OK));
        assertEquals("0.00", service.getOrderAmount("223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountAuthCaptureOk() {
        CustomerOrderPaymentService service = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
        service.create(getCustomerOrderPayment(new BigDecimal("223.00"), "223-45", "223-45-0", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK));
        service.create(getCustomerOrderPayment(new BigDecimal("0.45"), "223-45", "223-45-1", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK));
        assertEquals("223.45", service.getOrderAmount("223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountAuthCaptureNotOk() {
        CustomerOrderPaymentService service = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
        service.create(getCustomerOrderPayment(new BigDecimal("223.00"), "223-45", "223-45-0", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_FAILED));
        service.create(getCustomerOrderPayment(new BigDecimal("0.45"), "223-45", "223-45-1", PaymentGateway.AUTH_CAPTURE, Payment.PAYMENT_STATUS_OK));
        assertEquals("0.45", service.getOrderAmount("223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountCaptureOk() {
        CustomerOrderPaymentService service = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
        service.create(getCustomerOrderPayment(new BigDecimal("223.00"), "223-45", "223-45-0", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_OK));
        service.create(getCustomerOrderPayment(new BigDecimal("0.45"), "223-45", "223-45-1", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_OK));
        assertEquals("223.45", service.getOrderAmount("223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountCaptureNotOk() {
        CustomerOrderPaymentService service = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
        service.create(getCustomerOrderPayment(new BigDecimal("223.00"), "223-45", "223-45-0", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_PROCESSING));
        service.create(getCustomerOrderPayment(new BigDecimal("0.45"), "223-45", "223-45-1", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_OK));
        assertEquals("0.45", service.getOrderAmount("223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountCaptureRefundOk() {
        CustomerOrderPaymentService service = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
        service.create(getCustomerOrderPayment(new BigDecimal("223.00"), "223-45", "223-45-0", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_OK));
        service.create(getCustomerOrderPayment(new BigDecimal("0.45"), "223-45", "223-45-1", PaymentGateway.REFUND, Payment.PAYMENT_STATUS_OK));
        assertEquals("222.55", service.getOrderAmount("223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountCaptureRefundFailed() {
        CustomerOrderPaymentService service = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
        service.create(getCustomerOrderPayment(new BigDecimal("223.00"), "223-45", "223-45-0", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_OK));
        service.create(getCustomerOrderPayment(new BigDecimal("0.45"), "223-45", "223-45-1", PaymentGateway.REFUND, Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED));
        assertEquals("223.00", service.getOrderAmount("223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountCaptureVoidNotMatching() {
        CustomerOrderPaymentService service = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
        service.create(getCustomerOrderPayment(new BigDecimal("223.00"), "223-45", "223-45-0", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_OK));
        service.create(getCustomerOrderPayment(new BigDecimal("0.45"), "223-45", "223-45-1", PaymentGateway.VOID_CAPTURE, Payment.PAYMENT_STATUS_OK));
        assertEquals("223.00", service.getOrderAmount("223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountCaptureVoidOk() {
        CustomerOrderPaymentService service = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
        service.create(getCustomerOrderPayment(new BigDecimal("223.00"), "223-45", "223-45-0", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_OK));
        service.create(getCustomerOrderPayment(new BigDecimal("223.00"), "223-45", "223-45-1", PaymentGateway.VOID_CAPTURE, Payment.PAYMENT_STATUS_OK));
        assertEquals("0.00", service.getOrderAmount("223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountCaptureVoidNotOk() {
        CustomerOrderPaymentService service = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
        service.create(getCustomerOrderPayment(new BigDecimal("223.00"), "223-45", "223-45-0", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_OK));
        service.create(getCustomerOrderPayment(new BigDecimal("0.45"), "223-45", "223-45-1", PaymentGateway.VOID_CAPTURE, Payment.PAYMENT_STATUS_PROCESSING));
        assertEquals("223.00", service.getOrderAmount("223-45").toPlainString());
    }

    @Test
    public void testGetOrderAmountCaptureProcessingVoid() {
        CustomerOrderPaymentService service = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
        service.create(getCustomerOrderPayment(new BigDecimal("223.00"), "223-45", "223-45-0", PaymentGateway.CAPTURE, Payment.PAYMENT_STATUS_PROCESSING));
        service.create(getCustomerOrderPayment(new BigDecimal("0.45"), "223-45", "223-45-1", PaymentGateway.VOID_CAPTURE, Payment.PAYMENT_STATUS_OK));
        assertEquals("0.00", service.getOrderAmount("223-45").toPlainString());
    }

    private CustomerOrderPayment getCustomerOrderPayment(final BigDecimal amount,
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
        payment.setCardStartDate(new Date());
        payment.setOrderCurrency("EUR");
        payment.setOrderDate(new Date());
        payment.setPaymentAmount(amount);
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
