/*
 * Copyright 2009 Inspire-Software.com
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

import org.junit.Test;
import org.yes.cart.payment.CallbackAware;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: inspiresoftware
 * Date: 07/10/2020
 * Time: 11:22
 */
public class PaySeraCheckoutPaymentGatewayImplTest {

    private PaySeraCheckoutPaymentGatewayImpl gateway;

    @Test
    public void convertToCallbackConfirm() throws Exception {

        gateway = new PaySeraCheckoutPaymentGatewayImpl();

        final Map<String, String[]> callBackresult = getPaymentParameters();

        final CallbackAware.Callback callback = gateway.convertToCallback(callBackresult, false);

        assertNotNull(callback);
        assertEquals(CallbackAware.CallbackOperation.INVALID, callback.getOperation());
        assertNull(callback.getAmount());
        assertNull(callback.getOrderGuid());
        assertFalse(callback.isValidated());

// TODO: make a passing test for valid payment
//        assertEquals(CallbackAware.CallbackOperation.PAYMENT, callback.getOperation());
//        assertEquals(new BigDecimal("42.75"), callback.getAmount());
//        assertEquals("160309183235-123", callback.getOrderGuid());
//        assertTrue(callback.isValidated());

    }

    @Test
    public void convertToCallbackRefund() throws Exception {

        gateway = new PaySeraCheckoutPaymentGatewayImpl();

        final Map<String, String[]> callBackresult = getRefundParameters();

        final CallbackAware.Callback callback = gateway.convertToCallback(callBackresult, false);

        assertNotNull(callback);
        assertNotNull(callback);
        assertEquals(CallbackAware.CallbackOperation.INVALID, callback.getOperation());
        assertNull(callback.getAmount());
        assertNull(callback.getOrderGuid());
        assertFalse(callback.isValidated());

// TODO: make a passing test for valid payment
//        assertEquals(CallbackAware.CallbackOperation.REFUND, callback.getOperation());
//        assertEquals(new BigDecimal("43.70"), callback.getAmount());
//        assertEquals("171130110742-63", callback.getOrderGuid());
//        assertTrue(callback.isValidated());

    }

    private Map<String, String[]> getRefundParameters() {

        // TODO: mimic callback parameters for refund

        return new HashMap<String, String[]>() {{

            put("data", new String[] { "parse_str(base64_decode(strtr($_GET['data'], array('-' => '+', '_' => '/'))), $params);" });
            put("ss1", new String[] { "ss1 = md5(data + password)" });
            put("ss2", new String[] { "sign of data parameter" });

        }};
    }


    @Test
    public void convertToCallbackInvalid() throws Exception {

        gateway = new PaySeraCheckoutPaymentGatewayImpl();

        Map<String, String[]> callBackresult = Collections.emptyMap();

        final CallbackAware.Callback callback = gateway.convertToCallback(callBackresult, false);

        assertNotNull(callback);
        assertEquals(CallbackAware.CallbackOperation.INVALID, callback.getOperation());
        assertNull(callback.getAmount());
        assertNull(callback.getOrderGuid());
        assertFalse(callback.isValidated());

    }

    @Test
    public void convertToCallbackForced() throws Exception {

        gateway = new PaySeraCheckoutPaymentGatewayImpl();

        final Map<String, String[]> callBackresult = getPaymentParameters();

        final CallbackAware.Callback callback = gateway.convertToCallback(callBackresult, true);

        assertNotNull(callback);
        assertEquals(CallbackAware.CallbackOperation.PAYMENT, callback.getOperation());
        assertNull(callback.getAmount());
        assertNull(callback.getOrderGuid());
        assertFalse(callback.isValidated());

// TODO: make a passing test for valid payment
//        assertEquals(CallbackAware.CallbackOperation.PAYMENT, callback.getOperation());
//        assertEquals(new BigDecimal("42.75"), callback.getAmount());
//        assertEquals("160309183235-123", callback.getOrderGuid());
//        assertFalse(callback.isValidated());

    }

    private Map<String, String[]> getPaymentParameters() {

        // TODO: mimic callback parameters

        return new HashMap<String, String[]>() {{

            put("data", new String[] { "parse_str(base64_decode(strtr($_GET['data'], array('-' => '+', '_' => '/'))), $params);" });
            put("ss1", new String[] { "ss1 = md5(data + password)" });
            put("ss2", new String[] { "sign of data parameter" });

        }};
    }


}