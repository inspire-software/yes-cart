package org.yes.cart.utils.impl;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/22/12
 * Time: 8:08 PM
 */
public class ObjectUtil {

    /**
     * Dump map value into String.
     * @param map given map
     * @return  dump map as string
     */
    public static String dump(Map<?, ?> map) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            stringBuilder.append(entry.getKey());
            stringBuilder.append(" : ");
            stringBuilder.append(entry.getValue());
        }
        return stringBuilder.toString();
    }

}
