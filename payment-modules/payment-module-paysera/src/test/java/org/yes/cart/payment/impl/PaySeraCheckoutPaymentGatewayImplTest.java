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

import org.junit.Ignore;
import org.junit.Test;
import org.yes.cart.payment.CallbackAware;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

        gateway = new PaySeraCheckoutPaymentGatewayImpl() {
            @Override
            boolean isValid(final String dataAsBase64, final String ss1AsBase64) {
                return true;
            }
        };

        final Map<String, String[]> callBackresult = getPaymentParameters();

        final CallbackAware.Callback callback = gateway.convertToCallback(callBackresult, false);

        assertNotNull(callback);
        assertEquals(CallbackAware.CallbackOperation.PAYMENT, callback.getOperation());
        assertEquals(new BigDecimal("129.35"), callback.getAmount());
        assertEquals("ae8ad05c-d91f-45e2-9538-7ebb170602ff", callback.getOrderGuid());
        assertTrue(callback.isValidated());

    }

    @Ignore("TODO: implement refunds if available")
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

        gateway = new PaySeraCheckoutPaymentGatewayImpl() {
            @Override
            boolean isValid(final String dataAsBase64, final String ss1AsBase64) {
                return false;
            }
        };

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

        gateway = new PaySeraCheckoutPaymentGatewayImpl() {
            @Override
            boolean isValid(final String dataAsBase64, final String ss1AsBase64) {
                return false;
            }
        };

        final Map<String, String[]> callBackresult = getPaymentParameters();

        final CallbackAware.Callback callback = gateway.convertToCallback(callBackresult, true);

        assertNotNull(callback);
        assertEquals(CallbackAware.CallbackOperation.PAYMENT, callback.getOperation());
        assertEquals(new BigDecimal("129.35"), callback.getAmount());
        assertEquals("ae8ad05c-d91f-45e2-9538-7ebb170602ff", callback.getOrderGuid());
        assertFalse(callback.isValidated());

    }

    private Map<String, String[]> getPaymentParameters() {

        final String data =
                "c3VibWl0X3g9ODEmc3VibWl0X3k9MjImcF9sYXN0bmFtZT1Eb3UmcF96aXA9TlcxKzZYRSZwYXl0ZXh0PUF0bGlrdGFzK21vayVDNCU5N2ppbWFzK3UlQzUlQkUrdSVDNSVCRXNha3l0YXMrcHJla2VzK2h0dHAlM0ElMkYlMkZsb2NhbGhvc3QrcGFyZHVvdHV2J" +
                "UM0JTk3amUrJTI4dSVDNSVCRXNha3ltbytudW1lcmlzKy0rYWU4YWQwNWMtZDkxZi00NWUyLTk1MzgtN2ViYjE3MDYwMmZmJTI5JTNBKyUyQStwcm9kdWN0K3NrdSsxK3gzKyUyQStwcm9kdWN0K3NrdSsyK3gxKyUyQStOZXh0K2RleStkZWxpdmVyeSt4MSslMkErcHJ" +
                "vZHVjdCtza3UrMSt4MislMkErTmV4dCtkZXkrZGVsaXZlcnkreDEmcF9jaXR5PUxvbmRvbiZwX2VtYWlsPWpvaG4uZG91LWMzNDYxM2IzLWVhMmItNDllOC1iNmNjLWNlZGNhN2JhMzM3ZiU0MGRvbWFpbi5jb20mY3VycmVuY3k9RVVSJmxhbmc9ZW5nJnByb2plY3RpZ" +
                "D0xODgzNzUmYW1vdW50PTEyOTM1JnBfZmlyc3RuYW1lPUpvaG4mdGVzdD0xJm9yZGVyaWQ9YWU4YWQwNWMtZDkxZi00NWUyLTk1MzgtN2ViYjE3MDYwMmZmJnZlcnNpb249MS42JnBfY291bnRyeWNvZGU9R0ImcF9zdHJlZXQ9MjIxQitCYWtlcitTdHJlZXQrJm9yaWd" +
                "pbmFsX3BheXRleHQ9QXRsaWt0YXMrbW9rJUM0JTk3amltYXMrdSVDNSVCRSt1JUM1JUJFc2FreXRhcytwcmVrZXMrJTVCc2l0ZV9uYW1lJTVEK3BhcmR1b3R1diVDNCU5N2plKyUyOHUlQzUlQkVzYWt5bW8rbnVtZXJpcystKyU1Qm9yZGVyX25yJTVEJTI5JTNBJTBBJ" +
                "TJBK3Byb2R1Y3Qrc2t1KzEreDMlMEElMkErcHJvZHVjdCtza3UrMit4MSUwQSUyQStOZXh0K2RleStkZWxpdmVyeSt4MSUwQSUyQStwcm9kdWN0K3NrdSsxK3gyJTBBJTJBK05leHQrZGV5K2RlbGl2ZXJ5K3gxJTBBJnR5cGU9RU1BJnBheW1lbnQ9d2FsbGV0JmNvdW5" +
                "0cnk9R0ImX2NsaWVudF9sYW5ndWFnZT1lbmcmbV9wYXlfcmVzdG9yZWQ9NDI5MDQ0MzQ2JmZyYW1lPTAmYWNjb3VudD1URVNUMTIzNDU2Nzg5MCZzdGF0dXM9MSZwYXlhbW91bnQ9MTI5MzUmcGF5Y3VycmVuY3k9RVVSJnJlcXVlc3RpZD00MjkwNDQzNDYmbmFtZT1OY" +
                "W1lJnN1cmVuYW1lPUxhc3QrbmFtZSZwYXllcl9pcF9jb3VudHJ5PUdCJnBheWVyX2NvdW50cnk9R0I=";
        final String ss1 = "59053ea89a474f5303b728a29b3bbf20";
        final String ss2 = "VdTS5cI-oj_zquvhJka3qCY9bFbbpBirx6YfMYa6DOttwZPFNs7lHotC0aHmgKEEtIc1nOWB-jHFgfakthxcEgoOv6jBUvnOIqKHctiRZFS7icFFs_rJMeM_kZF_js7bMBvfZzmqZEBwUtHEqyS6F-GGTVHMS0HhQ3WsMioIbjA=";

        final Map<String, String[]> cb = new LinkedHashMap<>();
        cb.put("data", new String[] { data });
        cb.put("ss1", new String[] { ss1 });
        cb.put("ss2", new String[] { ss2 });
        return cb;

    }


}