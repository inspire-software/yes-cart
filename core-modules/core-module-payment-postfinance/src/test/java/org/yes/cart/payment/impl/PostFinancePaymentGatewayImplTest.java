package org.yes.cart.payment.impl;

import org.junit.Test;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentAddress;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.PaymentAddressImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.payment.dto.impl.PaymentLineImpl;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * User: denispavlov
 * Date: 08/10/2015
 * Time: 18:08
 */
public class PostFinancePaymentGatewayImplTest {


    @Test
    public void testGetHtmlForm() throws Exception {

        final Map<String, String> params = new HashMap<String, String>();
        params.put(PostFinancePaymentGatewayImpl.PF_POST_URL, "http://www.postfinance.com/pay");
        params.put(PostFinancePaymentGatewayImpl.PF_RESULT_URL_HOME, "http://mydomain.com/result");
        params.put(PostFinancePaymentGatewayImpl.PF_RESULT_URL_CATALOG, "http://mydomain.com/result");
        params.put(PostFinancePaymentGatewayImpl.PF_RESULT_URL_ACCEPT, "http://mydomain.com/result?hint=ok");
        params.put(PostFinancePaymentGatewayImpl.PF_RESULT_URL_DECLINE, "http://mydomain.com/result?hint=de");
        params.put(PostFinancePaymentGatewayImpl.PF_RESULT_URL_EXCEPTION, "http://mydomain.com/result?hint=ex");
        params.put(PostFinancePaymentGatewayImpl.PF_RESULT_URL_CANCEL, "http://mydomain.com/result?hint=ca");
        params.put(PostFinancePaymentGatewayImpl.PF_PSPID, "ID0001");
        params.put(PostFinancePaymentGatewayImpl.PF_SHA_IN, "shain");
        params.put(PostFinancePaymentGatewayImpl.PF_SHA_OUT, "shaout");


        final PostFinancePaymentGatewayImpl gatewayImpl = new PostFinancePaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
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

        assertEquals("<input type='hidden' name='ACCEPTURL' value='http://mydomain.com/result?hint=ok'>\n" +
                        "<input type='hidden' name='AMOUNT' value='1000'>\n" +
                        "<input type='hidden' name='CANCELURL' value='http://mydomain.com/result?hint=ca'>\n" +
                        "<input type='hidden' name='CATALOGURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='CN' value='holder  name'>\n" +
                        "<input type='hidden' name='COM' value='code2x1, bob@doe.com, 1234'>\n" +
                        "<input type='hidden' name='CURRENCY' value='USD'>\n" +
                        "<input type='hidden' name='DECLINEURL' value='http://mydomain.com/result?hint=de'>\n" +
                        "<input type='hidden' name='EMAIL' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='EXCEPTIONURL' value='http://mydomain.com/result?hint=ex'>\n" +
                        "<input type='hidden' name='HOMEURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='LANGUAGE' value='en'>\n" +
                        "<input type='hidden' name='OPERATION' value='SAL'>\n" +
                        "<input type='hidden' name='ORDERID' value='234-1324-1324-1324sdf-sdf'>\n" +
                        "<input type='hidden' name='PSPID' value='ID0001'>\n" +
                        "<input type='hidden' name='SHASIGN' value='3E8661D1CCD6B78D4EE0801C9F014B9D4010DAFD'>\n" +
                        "<input type='hidden' name='USERID' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='SUBMIT2' value=''>\n",
                htmlFormPart);



        htmlFormPart = gatewayImpl.getHtmlForm(
                "holder  name",
                "en",
                BigDecimal.TEN.setScale(2),
                "EUR",
                "234-1324-1324-1324abc-abc",
                createTestPayment(true)
        );

        assertEquals("<input type='hidden' name='ACCEPTURL' value='http://mydomain.com/result?hint=ok'>\n" +
                        "<input type='hidden' name='AMOUNT' value='1000'>\n" +
                        "<input type='hidden' name='CANCELURL' value='http://mydomain.com/result?hint=ca'>\n" +
                        "<input type='hidden' name='CATALOGURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='CN' value='holder  name'>\n" +
                        "<input type='hidden' name='COM' value='code2x1, bob@doe.com, 1234'>\n" +
                        "<input type='hidden' name='CURRENCY' value='EUR'>\n" +
                        "<input type='hidden' name='DECLINEURL' value='http://mydomain.com/result?hint=de'>\n" +
                        "<input type='hidden' name='EMAIL' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='EXCEPTIONURL' value='http://mydomain.com/result?hint=ex'>\n" +
                        "<input type='hidden' name='HOMEURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='LANGUAGE' value='en'>\n" +
                        "<input type='hidden' name='OPERATION' value='SAL'>\n" +
                        "<input type='hidden' name='ORDERID' value='234-1324-1324-1324abc-abc'>\n" +
                        "<input type='hidden' name='OWNERADDRESS' value='123, In the middle of'>\n" +
                        "<input type='hidden' name='OWNERCTY' value='NA'>\n" +
                        "<input type='hidden' name='OWNERTELNO' value='123412341234'>\n" +
                        "<input type='hidden' name='OWNERTOWN' value='Nowhere'>\n" +
                        "<input type='hidden' name='OWNERZIP' value='NA1 NA1'>\n" +
                        "<input type='hidden' name='PSPID' value='ID0001'>\n" +
                        "<input type='hidden' name='SHASIGN' value='85FFDCC3257ABBA193753D38B223E9511F6CC781'>\n" +
                        "<input type='hidden' name='USERID' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='SUBMIT2' value=''>\n",
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
        }

        return payment;

    }


    @Test
    public void testIsSuccess() {


        testIsSuccessWithStatus("5", Payment.PAYMENT_STATUS_OK);
        testIsSuccessWithStatus("9", Payment.PAYMENT_STATUS_OK);
        testIsSuccessWithStatus("51", Payment.PAYMENT_STATUS_OK);
        testIsSuccessWithStatus("91", Payment.PAYMENT_STATUS_OK);
        testIsSuccessWithStatus("0", Payment.PAYMENT_STATUS_FAILED);
        testIsSuccessWithStatus("2", Payment.PAYMENT_STATUS_FAILED);
        testIsSuccessWithStatus("52", Payment.PAYMENT_STATUS_FAILED);
        testIsSuccessWithStatus("92", Payment.PAYMENT_STATUS_FAILED);
        testIsSuccessWithStatus("93", Payment.PAYMENT_STATUS_FAILED);
        testIsSuccessWithStatus("zxcvzxcvzxcv", Payment.PAYMENT_STATUS_FAILED);


    }


    private void testIsSuccessWithStatus(final String status, final String expectedStatus) {



        final Map<String, String> params = new HashMap<String, String>();
        params.put(PostFinancePaymentGatewayImpl.PF_SHA_OUT, "Mysecretsig1875!?");

        final PostFinancePaymentGatewayImpl gatewayImpl = new PostFinancePaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }
        };

        Map<String, String> callBackresult = new HashMap<String, String>() {{

            put("ACCEPTANCE", "1234");
            put("amount", "15");
            put("BRAND", "VISA");
            put("CARDNO", "XXXXXXXXXXXX1111");
            put("currency", "EUR");
            put("NCERROR", "0");
            put("orderID", "12");
            put("PAYID", "32100123");
            put("PM", "CreditCard");
            put("STATUS", status);
            put("SHASIGN", "TESTINVALID");

        }};

        assertEquals(Payment.PAYMENT_STATUS_FAILED, gatewayImpl.getExternalCallbackResult(callBackresult).getStatus());
        assertNull(gatewayImpl.restoreOrderGuid(callBackresult));

        final Map<String, String> sorted = new TreeMap<String, String>();
        gatewayImpl.copyHttpParamsAndRemoveSignature(callBackresult, sorted);
        final String validSignature = new PostFinancePaymentGatewayImpl().sha1sign(sorted, "Mysecretsig1875!?");
        callBackresult.put("SHASIGN", validSignature);


        assertEquals(expectedStatus, gatewayImpl.getExternalCallbackResult(callBackresult).getStatus());
        assertEquals("12", gatewayImpl.restoreOrderGuid(callBackresult));
    }


}