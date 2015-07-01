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
     * Work around problem with wicket param values, when it can return
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
     * Print parameters to log.
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
                val = HttpParamsUtils.getSingleValue(map.get(key));
                dest.append(key);
                dest.append('=');
                dest.append(val);
                dest.append('\n');
            }
        }
        return dest.toString();
    }


}
