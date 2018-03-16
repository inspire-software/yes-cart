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
import org.yes.cart.payment.CallbackAware;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.payment.dto.impl.PaymentLineImpl;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/20/11
 * Time: 8:20 AM
 */
public class PayPalExpressCheckoutPaymentGatewayImplTest extends PaymentModuleDBTestCase {

    private static final Logger LOG = LoggerFactory.getLogger(PayPalExpressCheckoutPaymentGatewayImplTest.class);

    private PayPalExpressCheckoutPaymentGatewayImpl paymentGateway;

    private Boolean enabled;
    private String user;
    private String pass;
    private String sign;

    private boolean isTestAllowed() {

        enabled = Boolean.valueOf(System.getProperty("testPgPayPalExpress"));

        user = System.getProperty("testPgPayPalExpressUser");
        pass = System.getProperty("testPgPayPalExpressPass");
        sign = System.getProperty("testPgPayPalExpressSignature");

        boolean testConfigured = StringUtils.isNotBlank(user) && StringUtils.isNotBlank(pass) && StringUtils.isNotBlank(sign);

        if (enabled && !testConfigured) {

            LOG.error("To run PayPal Express test please enter all necessary configurations");

            enabled = false;
        }

        if (enabled) {
            LOG.info("\n\n*** Running PayPal Express using: {} ****\n\n", user);
        }

        return enabled;

    }

    @Override
    protected String testContextName() {
        return "test-payment-module-paypal.xml";
    }

    @Before
    public void setUp() throws Exception {

        final boolean allowed = isTestAllowed();

        if (!allowed) {
            LOG.warn("\n\n" +
                    "***\n" +
                    "PayPal Express integration test is DISABLED.\n" +
                    "You can enable test in /env/maven/${env}/config-module-paypal-[on/off].properties\n" +
                    "Set:\n" +
                    "testPgPayPalExpress=true\n" +
                    "testPgPayPalExpressUser=XXXXX\n" +
                    "testPgPayPalExpressPass=XXXXX\n" +
                    "testPgPayPalExpressSignature=XXXXX\n\n" +
                    "***\n\n\n");
        }

        assumeTrue(allowed);

        if (allowed) {
            paymentGateway = (PayPalExpressCheckoutPaymentGatewayImpl) ctx().getBean("payPalExpressPaymentGateway");

            final Map<String, PaymentGatewayParameter> params = new HashMap<>();
            for (final PaymentGatewayParameter param : paymentGateway.getPaymentGatewayParameters()) {
                params.put(param.getLabel(), param);
            }

            final PaymentGatewayParameter user = params.get("API_USER_NAME");
            final PaymentGatewayParameter key = params.get("API_USER_PASSWORD");
            final PaymentGatewayParameter md5 = params.get("SIGNATURE");

            user.setValue(this.user);
            key.setValue(this.pass);
            md5.setValue(this.sign);

            paymentGateway.updateParameter(user);
            paymentGateway.updateParameter(key);
            paymentGateway.updateParameter(md5);

        }
    }


    @Test
    public void testAllCalls() throws Exception {
        assumeTrue(isTestAllowed());

        Map<String, String> nvpCallResult;
        try {

            final Payment pay = createPayment();

            String redirectUrl = paymentGateway.setExpressCheckoutMethod(pay, "123");

            assertTrue("Must redirect to PayPal if ok",
                    redirectUrl.startsWith("https://www.sandbox.paypal.com/cgi-bin/webscr"));

            final String token = redirectUrl.substring(
                    redirectUrl.indexOf("&token=") + "&token=".length(),
                    redirectUrl.indexOf("&cmd=_express-checkout"));

            assertTrue("The TOKEN must be not empty", StringUtils.isNotBlank(token));


            nvpCallResult = paymentGateway.getExpressCheckoutDetails(
                    token
            );

            assertEquals(nvpCallResult.get("TOKEN"), token);
            assertEquals(nvpCallResult.get("AMT"), pay.getPaymentAmount().toPlainString());
            assertTrue("The payerId is not set because it is not authorised",
                    StringUtils.isBlank(nvpCallResult.get(AbstractPayPalNVPPaymentGatewayImpl.PP_EC_PAYERID)));

            nvpCallResult = paymentGateway.doExpressCheckoutPayment(pay, token);
            assertTrue(nvpCallResult.isEmpty());

            assertEquals("Express checkout call must be authorised before DoExpressCheckoutPayment",
                    CallbackAware.CallbackResult.FAILED, paymentGateway.getExternalCallbackResult(nvpCallResult));


        } finally {

            dumpDataBase("express", new String[]{"TPAYMENTGATEWAYPARAMETER"});

        }

    }

    private Payment createPayment() {

        final Payment payment = new PaymentImpl();

        payment.setPaymentAmount(new BigDecimal("123.98"));
        payment.setTaxAmount(new BigDecimal("20.66"));
        payment.setOrderCurrency("USD");
        payment.setOrderNumber("12345");

        final PaymentLine ship = new PaymentLineImpl(
                "SHIP",
                "Shipping",
                new BigDecimal("1"),
                new BigDecimal("5.50"),
                new BigDecimal("0.92"),
                true
        );

        final PaymentLine sku1 = new PaymentLineImpl(
                "SKU001",
                "SKU 001",
                new BigDecimal("2"),
                new BigDecimal("39.50"),
                new BigDecimal("13.16"),
                false
        );

        final PaymentLine sku2 = new PaymentLineImpl(
                "SKU002",
                "SKU 002",
                new BigDecimal("1"),
                new BigDecimal("39.48"),
                new BigDecimal("6.58"),
                false
        );

        payment.setOrderItems(new ArrayList<>(Arrays.asList(sku1, sku2, ship)));

        return payment;
    }


    @Override
    public String getVisaCardNumber() {
        return null;  //not need for test
    }
}
