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

package org.yes.cart.payment.persistence.entity.impl;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 12/02/2017
 * Time: 15:21
 */
public class PaymentGatewayCallbackEntityTest {

    private PaymentGatewayCallbackEntity entity1 = new PaymentGatewayCallbackEntity();
    private PaymentGatewayCallbackEntity entity2 = new PaymentGatewayCallbackEntity();

    @Test
    public void testParameterMapEmpty() throws Exception {

        final Map<String, String[]> values1 = new LinkedHashMap<String, String[]>();

        entity1.setParameterMap(values1);

        assertEquals("", entity1.getParameterMapInternal());

        entity2.setParameterMapInternal(entity1.getParameterMapInternal());

        final Map<String, String[]> values2 = entity2.getParameterMap();

        assertNotNull(values2);
        assertTrue(values2.isEmpty());

    }

    @Test
    public void testParameterMap() throws Exception {

        final Map<String, String[]> values1 = new LinkedHashMap<String, String[]>();
        values1.put("key1", new String[] { "value1" });
        values1.put("key2", new String[] { "value21", "value22", "value23" });
        values1.put("key3", new String[] { "value3" });

        entity1.setParameterMap(values1);

        assertEquals(
                "key1=value1\n" +
                "key2=value21\tvalue22\tvalue23\n" +
                "key3=value3",
                entity1.getParameterMapInternal()
        );

        entity2.setParameterMapInternal(entity1.getParameterMapInternal());

        final Map<String, String[]> values2 = entity2.getParameterMap();

        final List<String> keys = new ArrayList<String>(values2.keySet());
        assertArrayEquals(new String[] { "key1", "key2", "key3" }, keys.toArray());

        assertArrayEquals(new String[] { "value1" }, values2.get("key1"));
        assertArrayEquals(new String[] { "value21", "value22", "value23" }, values2.get("key2"));
        assertArrayEquals(new String[] { "value3" }, values2.get("key3"));

    }
}