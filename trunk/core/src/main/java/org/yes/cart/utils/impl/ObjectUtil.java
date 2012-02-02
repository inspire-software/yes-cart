package org.yes.cart.utils.impl;

import org.springframework.util.Assert;

import java.lang.reflect.Field;
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


    /**
     * Split given object to array of his field values.
     * @param obj object to convert
     * @return array of fileds values.
     */
    public static Object [] toObjectArray(final Object obj) {

        if (obj == null) {
            return new Object [] {"Null"};
        }

        if (obj.getClass().getName().startsWith("java.lang")) {
            return new Object [] {String.valueOf(obj)};

        }

        Field [] fields = obj.getClass().getDeclaredFields();

        Object [] rez = new Object [fields.length];

        for (int i = 0 ; i < fields.length; i++) {
            if(!fields[i].isAccessible()) {
                fields[i].setAccessible(true);
            }
            try {
                rez[i] = fields[i].get(obj);
            } catch (Exception ex) {

                rez[i] = "Cant get result : " + ex.getMessage();

            }
        }

        return rez;

    }

}
