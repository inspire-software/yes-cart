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

import com.liqpay.LiqPay;
import junit.framework.TestCase;
import org.junit.Test;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.payment.dto.impl.PaymentLineImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/22/12
 * Time: 6:16 PM
 */
public class LiqPayPaymentGatewayImplTest extends TestCase {

    @Test
    public void testGetHtmlForm() throws Exception {

        final Map<String, String> params = new HashMap<String, String>();
        params.put(LiqPayPaymentGatewayImpl.LP_MERCHANT_ID, "00001");
        params.put(LiqPayPaymentGatewayImpl.LP_MERCHANT_KEY, "secret");
        params.put(LiqPayPaymentGatewayImpl.LP_PAYWAY_URL, "card");
        params.put(LiqPayPaymentGatewayImpl.LP_SERVER_URL, "http://mydomain.com/callback");
        params.put(LiqPayPaymentGatewayImpl.LP_RESULT_URL, "http://mydomain.com/result");


        final LiqPayPaymentGatewayImpl gatewayImpl = new LiqPayPaymentGatewayImpl() {

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
                createTestPayment()

        );

        assertEquals(htmlFormPart,
                "<input type=\"hidden\" name=\"amount\" value=\"10.00\" />\n" +
                "<input type=\"hidden\" name=\"server_url\" value=\"http://mydomain.com/callback\" />\n" +
                "<input type=\"hidden\" name=\"description\" value=\"code2 x 1, bob@doe.com, 1234\" />\n" +
                "<input type=\"hidden\" name=\"pay_way\" value=\"card\" />\n" +
                "<input type=\"hidden\" name=\"result_url\" value=\"http://mydomain.com/result\" />\n" +
                "<input type=\"hidden\" name=\"public_key\" value=\"00001\" />\n" +
                "<input type=\"hidden\" name=\"type\" value=\"buy\" />\n" +
                "<input type=\"hidden\" name=\"order_id\" value=\"234-1324-1324-1324sdf-sdf\" />\n" +
                "<input type=\"hidden\" name=\"signature\" value=\"p5wT9l3BB3XxwRnyVLOQNX8LFvQ=\" />\n" +
                "<input type=\"hidden\" name=\"currency\" value=\"USD\" />\n");


    }


    private Payment createTestPayment() {

        final List<PaymentLine> orderItems = new ArrayList<PaymentLine>() {{
            add(new PaymentLineImpl("code2", "name2", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO, false));
        }};


        final Payment payment = new PaymentImpl();

        payment.setOrderNumber("1234");
        payment.setOrderItems(orderItems);
        payment.setOrderCurrency("USD");
        payment.setOrderLocale("en");
        payment.setBillingEmail("bob@doe.com");

        return payment;

    }


    @Test
    public void testIsSuccess() {


        testIsSuccessWithStatus("success", true);
        testIsSuccessWithStatus("wait_secure", true);
        testIsSuccessWithStatus("sandbox", true);
        testIsSuccessWithStatus("failure", false);
        testIsSuccessWithStatus("zxcvzxcvzxcv", false);


    }

    private void testIsSuccessWithStatus(final String status, final boolean expectedOk) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put(LiqPayPaymentGatewayImpl.LP_MERCHANT_ID, "00001");
        params.put(LiqPayPaymentGatewayImpl.LP_MERCHANT_KEY, "secret");
        params.put(LiqPayPaymentGatewayImpl.LP_PAYWAY_URL, "card");
        params.put(LiqPayPaymentGatewayImpl.LP_SERVER_URL, "http://mydomain.com/callback");
        params.put(LiqPayPaymentGatewayImpl.LP_RESULT_URL, "http://mydomain.com/result");

        final String privateKey = "secret";
        final String publicKey = "00001";
        final String amount = "10.00";
        final String currency = "EUR";
        final String description = "My products";
        final String order_id = "ORDER-001";
        final String type = "buy";
        final String sender_phone = "+380 44 123 1212";
        final String transaction_id = "TRANS-0001";

        final String validSignature = new LiqPay(publicKey, privateKey).str_to_sign(privateKey +
                amount  +
                currency +
                publicKey +
                order_id +
                type +
                description  +
                status +
                transaction_id +
                sender_phone);


        final LiqPayPaymentGatewayImpl gatewayImpl = new LiqPayPaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }
        };

        Map<String, String> callBackresult = new HashMap<String, String>() {{

            put("amount", amount);
            put("currency", currency);
            put("order_id", order_id);
            put("type", type);
            put("description", description);
            put("status", status);
            put("transaction_id", transaction_id);
            put("sender_phone", sender_phone);
            put("signature", "IO534oPTqpiGNeNWN3rfn2uwpug=");

        }};

        assertFalse(gatewayImpl.isSuccess(callBackresult));


        callBackresult.put("signature", validSignature);


        assertEquals(expectedOk, gatewayImpl.isSuccess(callBackresult));
    }


}
