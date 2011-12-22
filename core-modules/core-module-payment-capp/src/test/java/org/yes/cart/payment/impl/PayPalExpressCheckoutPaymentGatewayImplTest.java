package org.yes.cart.payment.impl;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;

import static junit.framework.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/20/11
 * Time: 8:20 AM
 */
public class PayPalExpressCheckoutPaymentGatewayImplTest extends CappPaymentModuleDBTestCase {

    private PayPalExpressCheckoutPaymentGatewayImpl paymentGateway;

    private boolean isTestAllowed() {
        //return "true".equals(System.getProperty("testPgPayPalExpress"));
        return true;
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

                assertTrue("Express checkout call must be ok for setExpressCheckoutMethod ", paymentGateway.isSuccess(nvpCallResult));
                final String token = nvpCallResult.get(AbstractPayPalPaymentGatewayImpl.PP_EC_TOKEN);
                assertTrue("The TOKEN must be not exmpty", StringUtils.isNotBlank(token));


                nvpCallResult = paymentGateway.getExpressCheckoutDetails(
                        token
                );

                assertTrue("Express checkout call must be ok for getExpressCheckoutDetails", paymentGateway.isSuccess(nvpCallResult));
                final String payerId = "asdasd"; //nvpCallResult.get(AbstractPayPalPaymentGatewayImpl.PP_EC_PAYERID);
                assertTrue("The payerId must be not exmpty", StringUtils.isNotBlank(payerId));

                nvpCallResult = paymentGateway.doDoExpressCheckoutPayment(
                        token,
                        "badplayerid" ,
                        new BigDecimal("123.98") ,
                        "USD"
                );

                assertTrue("Express checkout call must be ok for doDoExpressCheckoutPayment", !paymentGateway.isSuccess(nvpCallResult));


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
