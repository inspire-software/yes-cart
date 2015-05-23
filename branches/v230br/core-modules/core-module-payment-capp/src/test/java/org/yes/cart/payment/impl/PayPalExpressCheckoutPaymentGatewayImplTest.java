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

package org.yes.cart.payment.impl;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.payment.dto.Payment;

import java.math.BigDecimal;
import java.util.Map;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/20/11
 * Time: 8:20 AM
 */
public class PayPalExpressCheckoutPaymentGatewayImplTest extends CappPaymentModuleDBTestCase {

    private PayPalExpressCheckoutPaymentGatewayImpl paymentGateway;

    private boolean isTestAllowed() {
        return "true".equals(System.getProperty("testPgPayPalExpress"));
        //return true;
    }

    @Before
    public void setUp() throws Exception {
        if (isTestAllowed()) {
            paymentGateway = (PayPalExpressCheckoutPaymentGatewayImpl) ctx().getBean("payPalExpressPaymentGateway");
        }
    }


    @Test
    public void testAll3Calls() throws Exception {
        if (isTestAllowed()) {

            Map<String, String> nvpCallResult;
            try {
                nvpCallResult = paymentGateway.setExpressCheckoutMethod(
                        new BigDecimal("123.98"),
                        "USD"
                );

                assertEquals("Express checkout call must be ok for setExpressCheckoutMethod ",
                        Payment.PAYMENT_STATUS_OK, paymentGateway.getExternalCallbackResult(nvpCallResult));
                final String token = nvpCallResult.get(AbstractPayPalPaymentGatewayImpl.PP_EC_TOKEN);
                assertTrue("The TOKEN must be not exmpty", StringUtils.isNotBlank(token));


                nvpCallResult = paymentGateway.getExpressCheckoutDetails(
                        token
                );

                assertEquals("Express checkout call must be ok for getExpressCheckoutDetails",
                        Payment.PAYMENT_STATUS_OK, paymentGateway.getExternalCallbackResult(nvpCallResult));
                final String payerId = "asdasd"; //nvpCallResult.get(AbstractPayPalPaymentGatewayImpl.PP_EC_PAYERID);
                assertTrue("The payerId must be not exmpty", StringUtils.isNotBlank(payerId));

                nvpCallResult = paymentGateway.doDoExpressCheckoutPayment(
                        token,
                        "badplayerid" ,
                        new BigDecimal("123.98") ,
                        "USD"
                );

                assertEquals("Express checkout call must be ok for doDoExpressCheckoutPayment",
                        Payment.PAYMENT_STATUS_FAILED, paymentGateway.getExternalCallbackResult(nvpCallResult));


            } finally {

                dumpDataBase("express", new String[]{"TPAYMENTGATEWAYPARAMETER"});

            }


        }


    }


    @Override
    public String getVisaCardNumber() {
        return null;  //not need for test
    }
}
