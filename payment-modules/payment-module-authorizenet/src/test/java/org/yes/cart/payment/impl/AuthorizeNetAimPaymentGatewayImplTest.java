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
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentGatewayInternalForm;
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
public class AuthorizeNetAimPaymentGatewayImplTest extends PaymentModuleDBTestCase {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizeNetAimPaymentGatewayImplTest.class);

    private PaymentProcessorSurrogate paymentProcessor;
    private PaymentGatewayInternalForm authorizeNetAimPaymentGateway;
    private CustomerOrderPaymentService customerOrderPaymentService;

    private Boolean enabled;
    private String user;
    private String txkey;
    private String md5;

    private boolean isTestAllowed() {

        enabled = Boolean.valueOf(System.getProperty("testPgAuthorizeNetAim"));

        user = System.getProperty("testPgAuthorizeNetAimUser");
        txkey = System.getProperty("testPgAuthorizeNetAimTxKey");
        md5 = System.getProperty("testPgAuthorizeNetAimMD5");


        boolean testConfigured = StringUtils.isNotBlank(user) && StringUtils.isNotBlank(txkey) && StringUtils.isNotBlank(md5);

        if (enabled && !testConfigured) {

            LOG.error("To run AuthorizeNetAim test please enter all necessary configurations");

            enabled = false;
        }

        if (enabled) {
            LOG.info("\n\n*** Running AuthorizeNetAim using: {} ****\n\n", user);
        }

        return enabled;
    }

    @Override
    protected String testContextName() {
        return "test-payment-module-authorizenet.xml";
    }

    @Before
    public void setUp() throws Exception {

        final boolean allowed = isTestAllowed();

        if (!allowed) {
            LOG.warn("\n\n" +
                    "***\n" +
                    "AuthorizeNetAim integration test is DISABLED.\n" +
                    "You can enable test in /env/maven/${env}/config-module-authorizenet-[on/off].properties\n" +
                    "Set:\n" +
                    "testPgAuthorizeNetAim=true\n" +
                    "testPgAuthorizeNetAimUser=XXXXX\n" +
                    "testPgAuthorizeNetAimTxKey=XXXXX\n" +
                    "testPgAuthorizeNetAimMD5=XXXXX\n\n" +
                    "***\n\n\n");
        }

        assumeTrue(allowed);

        if (allowed) {
            customerOrderPaymentService = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
            authorizeNetAimPaymentGateway = (PaymentGatewayInternalForm) ctx().getBean("authorizeNetAimPaymentGateway");
            paymentProcessor = new PaymentProcessorSurrogate(customerOrderPaymentService, authorizeNetAimPaymentGateway);

            final Map<String, PaymentGatewayParameter> params = new HashMap<>();
            for (final PaymentGatewayParameter param : authorizeNetAimPaymentGateway.getPaymentGatewayParameters()) {
                params.put(param.getLabel(), param);
            }

            final PaymentGatewayParameter user = params.get("API_LOGIN_ID");
            final PaymentGatewayParameter key = params.get("TRANSACTION_KEY");
            final PaymentGatewayParameter md5 = params.get("MD5_HASH_KEY");

            user.setValue(this.user);
            key.setValue(this.txkey);
            md5.setValue(this.md5);

            authorizeNetAimPaymentGateway.updateParameter(user);
            authorizeNetAimPaymentGateway.updateParameter(key);
            authorizeNetAimPaymentGateway.updateParameter(md5);

        }
    }

    @Test
    public void testGetPaymentGatewayParameters() {
        assumeTrue(isTestAllowed());

        for (PaymentGatewayParameter parameter : authorizeNetAimPaymentGateway.getPaymentGatewayParameters()) {
            assertEquals("authorizeNetAimPaymentGateway", parameter.getPgLabel());
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

    /**
     * Test to check is possible capture less, that authorized.
     */
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

        //capture on second completed shipment with sum more that authorized

        assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.shipmentComplete(customerOrder, iter.next().getDeliveryNum(), new BigDecimal("-3.34")));
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.CAPTURE).size());
    }


    @Test
    public void testAuthPlusCapturePlusVoidCapture() {
        assumeTrue(isTestAllowed());
        orderCancelationFlow(false);
    }

    /* public void testAuthPlusCapturePlusRefund() {
       //??? how to submit settlement
       // during this test i have got  error no 53 - The referenced transaction does not meet the criteria for issuing a credit.
       // explanation
       ///Refunds cannot be tested while the payment gateway is in Test Mode
       //If you authorize or capture a transaction, and the transaction is not yet settled by the payment gateway, you cannot issue a refund.
       if (isTestAllowed()) {
           orderCancelationFlow(true);
       }
   } */

    private void orderCancelationFlow(boolean useRefund) {
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
        assertEquals(
                2,
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
        assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.authorizeCapture(
                        customerOrder,
                        createCardParameters()));
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.AUTH_CAPTURE).size());
    }

    @Override
    public String getVisaCardNumber() {
        return "4007000000027"; // Second Visa Test Card: 4012888818888
    }
}