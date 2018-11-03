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

import net.authorize.sim.Fingerprint;
import org.junit.Test;
import org.yes.cart.payment.CallbackAware;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentAddress;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.PaymentAddressImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.payment.dto.impl.PaymentLineImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AuthorizeNetSimPaymentGatewayImplTest {

    @Test
    public void testGetHiddenFiled() {
        AuthorizeNetSimPaymentGatewayImpl gateway = new AuthorizeNetSimPaymentGatewayImpl();
        assertEquals("<input type='hidden' name='qwerty' value='1234567890'>\n",
                gateway.getHiddenField("qwerty", "1234567890"));
        assertEquals("<input type='hidden' name='qwerty' value='9223372036854775807'>\n",
                gateway.getHiddenField("qwerty", Long.MAX_VALUE));
        assertEquals("<input type='hidden' name='qwerty' value='922337203685477.58'>\n",
                gateway.getHiddenField("qwerty", new BigDecimal("922337203685477.58")));
    }


    @Test
    public void testGetHtmlForm() throws Exception {

        final Map<String, String> params = new HashMap<>();
        params.put(AuthorizeNetSimPaymentGatewayImpl.AN_POST_URL, "http://www.authorize.com/pay");
        params.put(AuthorizeNetSimPaymentGatewayImpl.AN_CANCEL_URL, "http://mydomain.com/result/hint/cancel");
        params.put(AuthorizeNetSimPaymentGatewayImpl.AN_RETURN_POLICY_URL, "http://mydomain.com/retunrpolicy");
        params.put(AuthorizeNetSimPaymentGatewayImpl.AN_TEST_REQUEST, "FALSE");
        params.put(AuthorizeNetSimPaymentGatewayImpl.AN_MD5_HASH_KEY, "Simon");
        params.put(AuthorizeNetSimPaymentGatewayImpl.AN_API_LOGIN_ID, "login1");
        params.put(AuthorizeNetSimPaymentGatewayImpl.AN_TRANSACTION_KEY, "key1");

        final Fingerprint[] fingerprint = new Fingerprint[1];

        final AuthorizeNetSimPaymentGatewayImpl gatewayImpl = new AuthorizeNetSimPaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }

            @Override
            protected Fingerprint getFingerprint(final String orderReference,
                                                 final String apiLoginId,
                                                 final String txKey,
                                                 final String amountString,
                                                 final String currency) {
                if (fingerprint[0] == null) {
                    fingerprint[0] = super.getFingerprint(orderReference, apiLoginId, txKey, amountString, currency);
                }
                return fingerprint[0];
            }
        };

        String htmlFormPart = gatewayImpl.getHtmlForm(
                "holder  name",
                "en",
                BigDecimal.TEN.setScale(2),
                "USD",
                "234-1324-1324-1324sdf-sdf",
                createTestPayment(false)

        );

        assertEquals("<input type='hidden' name='x_login' value='login1'>\n" +
                        "<input type='hidden' name='x_fp_sequence' value='" + fingerprint[0].getSequence() + "'>\n" +
                        "<input type='hidden' name='x_fp_timestamp' value='" + fingerprint[0].getTimeStamp() + "'>\n" +
                        "<input type='hidden' name='x_fp_hash' value='" + fingerprint[0].getFingerprintHash() + "'>\n" +
                        "<input type='hidden' name='x_version' value='3.1'>\n" +
                        "<input type='hidden' name='x_method' value='CC'>\n" +
                        "<input type='hidden' name='x_type' value='AUTH_CAPTURE'>\n" +
                        "<input type='hidden' name='x_amount' value='10.00'>\n" +
                        "<input type='hidden' name='x_currency_code' value='USD'>\n" +
                        "<input type='hidden' name='x_show_form' value='payment_form'>\n" +
                        "<input type='hidden' name='x_test_request' value='FALSE'>\n" +
                        "<input type='hidden' name='x_line_item' value='code2<|>name2<|><|>1<|>10<|>0'>\n" +
                        "<input type='hidden' name='x_cust_id' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='x_invoice_num' value='234132413241324sdfsd'>\n" +
                        "<input type='hidden' name='x_po_num' value='234-1324-1324-1324sdf-sdf'>\n" +
                        "<input type='hidden' name='x_description' value='code2 x 1, bob@doe.com, 1234'>\n" +
                        "<input type='hidden' name='x_cancel_url' value='http://mydomain.com/result/hint/cancel'>\n" +
                        "<input type='hidden' name='x_return_policy_url' value='http://mydomain.com/retunrpolicy'>\n" +
                        "<input type='hidden' name='orderGuid' value='234-1324-1324-1324sdf-sdf'>\n" +
                        "<input type='hidden' name='x_return_policy_url' value='http://mydomain.com/retunrpolicy'>\n",
                htmlFormPart);


        htmlFormPart = gatewayImpl.getHtmlForm(
                "holder  name",
                "en",
                BigDecimal.TEN.setScale(2),
                "EUR",
                "234-1324-1324-1324abc-abc",
                createTestPayment(true)
        );

        assertEquals("<input type='hidden' name='x_login' value='login1'>\n" +
                        "<input type='hidden' name='x_fp_sequence' value='" + fingerprint[0].getSequence() + "'>\n" +
                        "<input type='hidden' name='x_fp_timestamp' value='" + fingerprint[0].getTimeStamp() + "'>\n" +
                        "<input type='hidden' name='x_fp_hash' value='" + fingerprint[0].getFingerprintHash() + "'>\n" +
                        "<input type='hidden' name='x_version' value='3.1'>\n" +
                        "<input type='hidden' name='x_method' value='CC'>\n" +
                        "<input type='hidden' name='x_type' value='AUTH_CAPTURE'>\n" +
                        "<input type='hidden' name='x_amount' value='10.00'>\n" +
                        "<input type='hidden' name='x_currency_code' value='EUR'>\n" +
                        "<input type='hidden' name='x_show_form' value='payment_form'>\n" +
                        "<input type='hidden' name='x_test_request' value='FALSE'>\n" +
                        "<input type='hidden' name='x_line_item' value='code2<|>name2<|><|>1<|>10<|>0'>\n" +
                        "<input type='hidden' name='x_address' value='123, In the middle of'>\n" +
                        "<input type='hidden' name='x_city' value='Nowhere'>\n" +
                        "<input type='hidden' name='x_state' value='NA'>\n" +
                        "<input type='hidden' name='x_zip' value='NA1 NA1'>\n" +
                        "<input type='hidden' name='x_country' value='NA'>\n" +
                        "<input type='hidden' name='x_phone' value='123412341234'>\n" +
                        "<input type='hidden' name='x_email' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='x_cust_id' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='x_ship_to_address' value='324, In the middle of'>\n" +
                        "<input type='hidden' name='x_ship_to_city' value='Nowhere'>\n" +
                        "<input type='hidden' name='x_ship_to_state' value='NA'>\n" +
                        "<input type='hidden' name='x_ship_to_zip' value='NA2 NA2'>\n" +
                        "<input type='hidden' name='x_ship_to_country' value='NA'>\n" +
                        "<input type='hidden' name='x_invoice_num' value='234132413241324abcab'>\n" +
                        "<input type='hidden' name='x_po_num' value='234-1324-1324-1324abc-abc'>\n" +
                        "<input type='hidden' name='x_description' value='code2 x 1, bob@doe.com, 1234'>\n" +
                        "<input type='hidden' name='x_cancel_url' value='http://mydomain.com/result/hint/cancel'>\n" +
                        "<input type='hidden' name='x_return_policy_url' value='http://mydomain.com/retunrpolicy'>\n" +
                        "<input type='hidden' name='orderGuid' value='234-1324-1324-1324abc-abc'>\n" +
                        "<input type='hidden' name='x_return_policy_url' value='http://mydomain.com/retunrpolicy'>\n",
                htmlFormPart);

    }


    private Payment createTestPayment(boolean withAddress) {

        final List<PaymentLine> orderItems = new ArrayList<PaymentLine>() {{
            add(new PaymentLineImpl("code2", "name2", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO, false));
        }};


        final Payment payment = new PaymentImpl();

        payment.setOrderNumber("1234");
        payment.setOrderItems(orderItems);
        payment.setOrderCurrency("USD");
        payment.setOrderLocale("en");
        payment.setBillingEmail("bob@doe.com");

        if (withAddress) {
            final PaymentAddress address = new PaymentAddressImpl();
            address.setAddrline1("123");
            address.setAddrline2("In the middle of");
            address.setCity("Nowhere");
            address.setStateCode("NA");
            address.setCountryCode("NA");
            address.setPostcode("NA1 NA1");
            address.setPhone1("123412341234");
            payment.setBillingAddress(address);
            final PaymentAddress shipTo = new PaymentAddressImpl();
            shipTo.setAddrline1("324");
            shipTo.setAddrline2("In the middle of");
            shipTo.setCity("Nowhere");
            shipTo.setStateCode("NA");
            shipTo.setCountryCode("NA");
            shipTo.setPostcode("NA2 NA2");
            shipTo.setPhone1("4324324324523");
            payment.setShippingAddress(shipTo);
        }

        return payment;

    }



    @Test
    public void testIsSuccess() {

        // default behaviour is payment
        testIsSuccessWithStatus("1", Payment.PAYMENT_STATUS_OK, null, CallbackAware.CallbackOperation.PAYMENT);
        testIsSuccessWithStatus("2", Payment.PAYMENT_STATUS_FAILED, null, CallbackAware.CallbackOperation.PAYMENT);
        testIsSuccessWithStatus("3", Payment.PAYMENT_STATUS_FAILED, null, CallbackAware.CallbackOperation.PAYMENT);
        testIsSuccessWithStatus("4", Payment.PAYMENT_STATUS_PROCESSING, null, CallbackAware.CallbackOperation.PAYMENT);
        testIsSuccessWithStatus("zxcvzxcvzxcv", Payment.PAYMENT_STATUS_FAILED, null, CallbackAware.CallbackOperation.PAYMENT);

        testIsSuccessWithStatus("1", Payment.PAYMENT_STATUS_OK, "any-op", CallbackAware.CallbackOperation.PAYMENT);
        testIsSuccessWithStatus("2", Payment.PAYMENT_STATUS_FAILED, "any-op", CallbackAware.CallbackOperation.PAYMENT);
        testIsSuccessWithStatus("3", Payment.PAYMENT_STATUS_FAILED, "any-op", CallbackAware.CallbackOperation.PAYMENT);
        testIsSuccessWithStatus("4", Payment.PAYMENT_STATUS_PROCESSING, "any-op", CallbackAware.CallbackOperation.PAYMENT);
        testIsSuccessWithStatus("zxcvzxcvzxcv", Payment.PAYMENT_STATUS_FAILED, "any-op", CallbackAware.CallbackOperation.PAYMENT);

        // credit & void are refund behaviour
        testIsSuccessWithStatus("1", Payment.PAYMENT_STATUS_OK, "credit", CallbackAware.CallbackOperation.REFUND);
        testIsSuccessWithStatus("2", Payment.PAYMENT_STATUS_FAILED, "credit", CallbackAware.CallbackOperation.REFUND);
        testIsSuccessWithStatus("3", Payment.PAYMENT_STATUS_FAILED, "credit", CallbackAware.CallbackOperation.REFUND);
        testIsSuccessWithStatus("4", Payment.PAYMENT_STATUS_PROCESSING, "credit", CallbackAware.CallbackOperation.REFUND);
        testIsSuccessWithStatus("zxcvzxcvzxcv", Payment.PAYMENT_STATUS_FAILED, "credit", CallbackAware.CallbackOperation.REFUND);

        testIsSuccessWithStatus("1", Payment.PAYMENT_STATUS_OK, "void", CallbackAware.CallbackOperation.REFUND);
        testIsSuccessWithStatus("2", Payment.PAYMENT_STATUS_FAILED, "void", CallbackAware.CallbackOperation.REFUND);
        testIsSuccessWithStatus("3", Payment.PAYMENT_STATUS_FAILED, "void", CallbackAware.CallbackOperation.REFUND);
        testIsSuccessWithStatus("4", Payment.PAYMENT_STATUS_PROCESSING, "void", CallbackAware.CallbackOperation.REFUND);
        testIsSuccessWithStatus("zxcvzxcvzxcv", Payment.PAYMENT_STATUS_FAILED, "void", CallbackAware.CallbackOperation.REFUND);

        // info behaviour
        testIsSuccessWithStatus("1", Payment.PAYMENT_STATUS_OK, "prior_auth_capture", CallbackAware.CallbackOperation.INFO);
        testIsSuccessWithStatus("2", Payment.PAYMENT_STATUS_FAILED, "prior_auth_capture", CallbackAware.CallbackOperation.INFO);
        testIsSuccessWithStatus("3", Payment.PAYMENT_STATUS_FAILED, "prior_auth_capture", CallbackAware.CallbackOperation.INFO);
        testIsSuccessWithStatus("4", Payment.PAYMENT_STATUS_PROCESSING, "prior_auth_capture", CallbackAware.CallbackOperation.INFO);
        testIsSuccessWithStatus("zxcvzxcvzxcv", Payment.PAYMENT_STATUS_FAILED, "prior_auth_capture", CallbackAware.CallbackOperation.INFO);

    }


    private void testIsSuccessWithStatus(final String status,
                                         final String expectedStatus,
                                         final String xType,
                                         final CallbackAware.CallbackOperation expectedOp) {



        final Map<String, String> params = new HashMap<>();
        params.put(AuthorizeNetSimPaymentGatewayImpl.AN_MD5_HASH_KEY, "Simon");
        params.put(AuthorizeNetSimPaymentGatewayImpl.AN_API_LOGIN_ID, "Login1");

        final AuthorizeNetSimPaymentGatewayImpl gatewayImpl = new AuthorizeNetSimPaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }
        };

        Map<String, String> callBackresult = new HashMap<String, String>() {{

            put("x_amount", "15.00");
            put("x_trans_id", "32100123");
            if (xType != null) {
                put("x_type", xType);
            }
            put("x_response_code", status);
            put("x_MD5_Hash", "TESTINVALID");
            put("orderGuid", "12");

        }};

        // Test failure with invalid signature
        assertEquals(Payment.PAYMENT_STATUS_FAILED, gatewayImpl.getExternalCallbackResult(callBackresult, false).getStatus());
        final CallbackAware.Callback badCallback = gatewayImpl.convertToCallback(callBackresult, false);
        assertEquals(CallbackAware.CallbackOperation.INVALID, badCallback.getOperation());
        assertNull(badCallback.getOrderGuid());
        assertNull(badCallback.getAmount());

        final AuthorizeNetSimPaymentGatewayImpl gatewayImpl2 = new AuthorizeNetSimPaymentGatewayImpl() {
            @Override
            protected boolean isValid(final Map privateCallBackParameters) {
                return true;
            }
        };

        // Test behaviour with valid signature
        assertEquals(expectedStatus, gatewayImpl2.getExternalCallbackResult(callBackresult, false).getStatus());
        final CallbackAware.Callback goodCallback = gatewayImpl2.convertToCallback(callBackresult, false);
        assertEquals("12", goodCallback.getOrderGuid());
        assertEquals(new BigDecimal("15.00"), goodCallback.getAmount());

    }



    @Test
    public void testIsSuccessWithStatus2() {



        final Map<String, String> params = new HashMap<>();
        params.put(AuthorizeNetSimPaymentGatewayImpl.AN_MD5_HASH_KEY, "Simon");
        params.put(AuthorizeNetSimPaymentGatewayImpl.AN_API_LOGIN_ID, "yescartauthorise1");
        params.put(AuthorizeNetSimPaymentGatewayImpl.AN_API_LOGIN_ID, "5yaqwaA8Uk5X");

        final AuthorizeNetSimPaymentGatewayImpl gatewayImpl = new AuthorizeNetSimPaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }

            @Override
            protected boolean isValid(final Map privateCallBackParameters) {
                return true; // Overwritten, since this is performed by pure AuthNet API
            }
        };

        Map<String, String> callBackresult = new HashMap<String, String>() {{

            put("x_amount", "1035.10");
            put("x_trans_id", "2242023261");
            put("x_response_code", "1");
            put("x_MD5_Hash", "E7184C27CDCF2AF604AA50C2174C244C");
            put("orderGuid", "151013162426-26");

        }};


        assertEquals(CallbackAware.CallbackResult.OK, gatewayImpl.getExternalCallbackResult(callBackresult, false));
        final CallbackAware.Callback goodCallback = gatewayImpl.convertToCallback(callBackresult, false);
        assertEquals(CallbackAware.CallbackOperation.PAYMENT, goodCallback.getOperation());
        assertEquals("151013162426-26", goodCallback.getOrderGuid());
        assertEquals(new BigDecimal("1035.10"), goodCallback.getAmount());
        assertTrue(goodCallback.isValidated());
    }


    @Test
    public void testIsSuccessWithStatus2Force() {



        final Map<String, String> params = new HashMap<>();
        params.put(AuthorizeNetSimPaymentGatewayImpl.AN_MD5_HASH_KEY, "Simon");
        params.put(AuthorizeNetSimPaymentGatewayImpl.AN_API_LOGIN_ID, "yescartauthorise1");
        params.put(AuthorizeNetSimPaymentGatewayImpl.AN_API_LOGIN_ID, "5yaqwaA8Uk5X");

        final AuthorizeNetSimPaymentGatewayImpl gatewayImpl = new AuthorizeNetSimPaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }

            @Override
            protected boolean isValid(final Map privateCallBackParameters) {
                return false; // Overwritten, since this is performed by pure AuthNet API
            }
        };

        Map<String, String> callBackresult = new HashMap<String, String>() {{

            put("x_amount", "1035.10");
            put("x_trans_id", "2242023261");
            put("x_response_code", "1");
            put("x_MD5_Hash", "E7184C27CDCF2AF604AA50C2174C244C");
            put("orderGuid", "151013162426-26");

        }};


        assertEquals(CallbackAware.CallbackResult.OK, gatewayImpl.getExternalCallbackResult(callBackresult, true));
        final CallbackAware.Callback badCallback = gatewayImpl.convertToCallback(callBackresult, true);
        assertEquals(CallbackAware.CallbackOperation.PAYMENT, badCallback.getOperation());
        assertEquals("151013162426-26", badCallback.getOrderGuid());
        assertEquals(new BigDecimal("1035.10"), badCallback.getAmount());
        assertFalse(badCallback.isValidated());
    }


}
