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

package org.yes.cart.payment.impl;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.service.CustomerOrderPaymentService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PayPalProPaymentGatewayImplTest extends PaymentModuleDBTestCase {

    private PaymentProcessorSurrogate paymentProcessor;
    private PayPalProPaymentGatewayImpl payPalProPaymentGateway;
    private CustomerOrderPaymentService customerOrderPaymentService;

    private Boolean enabled;
    private String user;
    private String pass;
    private String sign;
    private String card;
    private String expMM;
    private String expYYYY;

    private boolean isTestAllowed() {

        enabled = Boolean.valueOf(System.getProperty("testPgPayPro"));

        user = System.getProperty("testPgPayProUser");
        pass = System.getProperty("testPgPayProPass");
        sign = System.getProperty("testPgPayProSignature");
        card = System.getProperty("testPgPayProCard");
        expMM = System.getProperty("testPgPayProCardMM");
        expYYYY = System.getProperty("testPgPayProCardYYYY");

        boolean testConfigured = StringUtils.isNotBlank(user) && StringUtils.isNotBlank(pass) && StringUtils.isNotBlank(sign)
                && StringUtils.isNotBlank(card) && StringUtils.isNotBlank(expMM) && StringUtils.isNotBlank(expYYYY);

        if (enabled && !testConfigured) {
            System.out.println("To run PayPal Pro test please enter configuration for your test account " +
                    "(testPgPayProUser, testPgPayProPass, testPgPayProSignature, testPgPayProCard, testPgPayProCardMM and testPgPayProCardYYYY, )");
            enabled = false;
        }

        if (enabled) {
            System.out.println("Running PayPal Pro using: " + user);
        }

        return enabled;

    }

    protected String testContextName() {
        return "test-core-module-payment-paypal.xml";
    }

    @Before
    public void setUp() throws Exception {
        assumeTrue(isTestAllowed());
        if (isTestAllowed()) {
            customerOrderPaymentService = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
            payPalProPaymentGateway = (PayPalProPaymentGatewayImpl) ctx().getBean("payPalProPaymentGateway");
            paymentProcessor = new PaymentProcessorSurrogate(customerOrderPaymentService, payPalProPaymentGateway);


            final Map<String, PaymentGatewayParameter> params = new HashMap<String, PaymentGatewayParameter>();
            for (final PaymentGatewayParameter param : payPalProPaymentGateway.getPaymentGatewayParameters()) {
                params.put(param.getLabel(), param);
            }

            final PaymentGatewayParameter user = params.get("API_USER_NAME");
            final PaymentGatewayParameter key = params.get("API_USER_PASSWORD");
            final PaymentGatewayParameter md5 = params.get("SIGNATURE");

            user.setValue(this.user);
            key.setValue(this.pass);
            md5.setValue(this.sign);

            payPalProPaymentGateway.updateParameter(user);
            payPalProPaymentGateway.updateParameter(key);
            payPalProPaymentGateway.updateParameter(md5);

        }
    }

    @Test
    public void testGetPaymentGatewayParameters() {
        assumeTrue(isTestAllowed());
        for (PaymentGatewayParameter parameter : payPalProPaymentGateway.getPaymentGatewayParameters()) {
            assertEquals("payPalProPaymentGateway", parameter.getPgLabel());
        }
    }

    @Test
    public void testAuthPlusReverseAuthorization() {
        assumeTrue(isTestAllowed());
        String orderNum = UUID.randomUUID().toString();
        CustomerOrder customerOrder = createCustomerOrder(orderNum);
        // The whole operation is completed successfully
        assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.authorize(
                        customerOrder,
                        createCardParameters()));
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.AUTH).size());
        //lets perform reverse authorization
        paymentProcessor.reverseAuthorizations(orderNum);
        //two records for reverse
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.REVERSE_AUTH).size());
        //total 54 records
        assertEquals(4,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        null).size());
    }


    @Test
    public void testAuthPlusCapture() {
        assumeTrue(isTestAllowed());
        String orderNum = UUID.randomUUID().toString();
        CustomerOrder customerOrder = createCustomerOrder(orderNum);
        // The whole operation is completed successfully
        assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.authorize(
                        customerOrder,
                        createCardParameters()));
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.AUTH).size());
        //capture on first completed shipment
        Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.shipmentComplete(customerOrder, iter.next().getDeliveryNum()));
        assertEquals(1,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.CAPTURE).size());
        //capture on second completed shipment
        assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.shipmentComplete(customerOrder, iter.next().getDeliveryNum()));
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.CAPTURE).size());
    }

    @Test
    public void testAuthPlusCaptureLess() {
        assumeTrue(isTestAllowed());
        String orderNum = UUID.randomUUID().toString();
        CustomerOrder customerOrder = createCustomerOrder(orderNum);
        // The whole operation is completed successfully
        assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.authorize(
                        customerOrder,
                        createCardParameters()));
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.AUTH).size());
        //capture on first completed shipment
        Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.shipmentComplete(customerOrder, iter.next().getDeliveryNum()));
        assertEquals(1,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.CAPTURE).size());
        //capture on second completed shipment
        assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.shipmentComplete(customerOrder, iter.next().getDeliveryNum(), new BigDecimal("-23.23")));
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.CAPTURE).size());
    }

    @Test
    @Ignore("Ignored for now since transactions seems not to be immediately refundable")
    public void testAuthPlusCapturePlusVoidCapture() {
        assumeTrue(isTestAllowed());
        orderCancellationFlow(false);
    }

    @Test
    @Ignore("Ignored for now since transactions seems not to be immediately refundable")
    public void testAuthPlusCapturePlusRefund() {
        assumeTrue(isTestAllowed());
        //??? how to submit settlement
        orderCancellationFlow(true);
    }

    private void orderCancellationFlow(boolean useRefund) {
        String orderNum = UUID.randomUUID().toString();
        CustomerOrder customerOrder = createCustomerOrder(orderNum);
        // The whole operation is completed successfully
        assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.authorize(
                        customerOrder,
                        createCardParameters()));
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.AUTH).size());
        //capture on first completed shipment
        Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        paymentProcessor.shipmentComplete(customerOrder, iter.next().getDeliveryNum());
        assertEquals(1,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.CAPTURE).size());
        //capture on second completed shipment
        paymentProcessor.shipmentComplete(customerOrder, iter.next().getDeliveryNum());
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.CAPTURE).size());
        //lets void capture
        assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.cancelOrder(customerOrder, useRefund));
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        useRefund ? PaymentGateway.REFUND : PaymentGateway.VOID_CAPTURE).size());
        assertEquals(6,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        null).size());
    }

    @Test
    public void testAuthCapture() {
        assumeTrue(isTestAllowed());
        String orderNum = UUID.randomUUID().toString();
        CustomerOrder customerOrder = createCustomerOrder(orderNum);
        // The whole operation is completed successfully
        assertEquals(Payment.PAYMENT_STATUS_FAILED,
                paymentProcessor.authorizeCapture(
                        customerOrder,
                        createCardParameters()));
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED,
                        PaymentGateway.AUTH_CAPTURE).size());
    }

    protected Map createCardParameters() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("ccHolderName", "JOHN DOU");
        params.put("ccNumber", getVisaCardNumber());
        params.put("ccExpireMonth", expMM);  // paypal test account
        params.put("ccExpireYear", expYYYY); // paypal test account
        params.put("ccSecCode", "111");
        params.put("ccType", "Visa");
        params.put("ccHolderName", "JOHN DOU");
        return params;
    }

    public String getVisaCardNumber() {
        return card;  //this is from test account
    }
}
