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

package org.yes.cart.util;

import java.util.*;

/**
 * User: denispavlov
 * Date: 15/05/2015
 * Time: 08:34
 */
public class HttpParamsUtils {

    private HttpParamsUtils() {
    }

    /**
     * Bulk parameters converter from array of objects to single value strings.
     *
     * @param httpParams raw param (could be single or array).
     *
     * @return simple string key value parameters mapping
     */
    public static Map<String, String> createSingleValueMap(Map httpParams) {
        final Map<String, String> clean = new HashMap<String, String>();
        if (httpParams != null) {
            for (final Map.Entry<Object, Object> entry : ((Map<Object, Object>) httpParams).entrySet()) {
                final String value = getSingleValue(entry.getValue());
                clean.put(entry.getKey().toString(), value);
            }
        }
        return clean;
    }


    /**
     * Simple extraction of single string values from request as request
     * values come in as arrays of strings.
     *
     * Workaround problem with wicket param values, when it can return
     * parameter value as string or as array of strings with single value.
     * This behavior depends on url encoding strategy.
     *
     * @param param parameters
     *
     * @return value
     */
    public static String getSingleValue(final Object param) {
        if (param instanceof String) {
            return (String) param;
        } else if (param instanceof String[]) {
            if (((String[])param).length > 0) {
                return ((String [])param)[0];
            }
        }
        return null;

    }


    /**
     * Reverse function of {@link #createSingleValueMap(Map)}.
     *
     * @param httpParams raw param (could be single or array).
     *
     * @return simple string key value parameters mapping
     */
    public static Map<String, String[]> createArrayValueMap(Map httpParams) {
        final Map<String, String[]> clean = new HashMap<String, String[]>();
        if (httpParams != null) {
            for (final Map.Entry<Object, Object> entry : ((Map<Object, Object>) httpParams).entrySet()) {
                final String[] value = getArrayValue(entry.getValue());
                clean.put(entry.getKey().toString(), value);
            }
        }
        return clean;
    }

    /**
     * Reverse function of {@link #getSingleValue(Object)}.
     *
     * @param param parameters
     *
     * @return array of strings
     */
    public static String[] getArrayValue(final Object param) {
        if (param == null) {
            return null;
        } else if (param instanceof String[]) {
            return (String[]) param;
        }
        return new String[] { param.toString() };

    }

    private static List<String> CARD_PARAMS = Arrays.asList(
            // CyberSource
            "card_accountNumber", "card_cvNumber",
            // AuthNET, PayPal
            "ccNumber", "ccSecCode",
            // PayPal Pro
            "ACCT", "CVV2"
    );

    /**
     * Print parameters to log.
     *
     * Due to security issues some parameters are filtered out (see {@link #CARD_PARAMS})
     *
     * @param header Header text.
     * @param map http parameters
     */
    public static String stringify(final String header, final Map map) {
        StringBuilder dest = new StringBuilder();
        dest.append(header);
        if (map != null && !map.isEmpty()) {
            List<String> keys = new ArrayList<String>(map.keySet());
            Collections.sort(keys);

            Iterator<String> iter = keys.iterator();

            String key, val;
            while (iter.hasNext()) {
                key = iter.next();
                if (CARD_PARAMS.contains(key)) {
                    val = "XXXX Security XXXX";
                } else {
                    val = HttpParamsUtils.getSingleValue(map.get(key));
                }
                dest.append(key);
                dest.append('=');
                dest.append(val);
                dest.append('\n');
            }
        }
        return dest.toString();
    }


}
