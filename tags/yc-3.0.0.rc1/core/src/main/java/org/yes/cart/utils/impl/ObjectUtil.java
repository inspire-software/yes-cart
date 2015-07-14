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

package org.yes.cart.utils.impl;

import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.yes.cart.domain.entity.Auditable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/22/12
 * Time: 8:08 PM
 */
public class ObjectUtil {

    /**
     * Dump map value into String.
     *
     * @param map given map
     * @return dump map as string
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
     * Split given object to array of its field values.
     *
     * @param obj object to convert
     * @return array of fields values.
     */
    public static Object[] toObjectArray(final Object obj) {

        if (obj == null) {
            return new Object[]{ null };
        }

        if (obj.getClass().isPrimitive() || obj.getClass().getName().startsWith("java.lang") || obj.getClass().isArray()) {
            return escapeXml(obj);
        }

        final Field[] fields = obj.getClass().getDeclaredFields();

        final Object[] rez = new Object[fields.length];

        for (int i = 0; i < fields.length; i++) {

            if (Modifier.isStatic(fields[i].getModifiers())) {
                rez[i] = "Static: " + fields[i].getName();
                continue;
            }

            if (!fields[i].isAccessible()) {
                fields[i].setAccessible(true);
            }

            try {

                final Object value = fields[i].get(obj);

                if (value instanceof HibernateProxy) {
                    rez[i] = ((HibernateProxy) value).getHibernateLazyInitializer().getIdentifier() + ": " + fields[i].getName() + " (H)";
                } else if (value instanceof Auditable) {
                    rez[i] = ((Auditable) value).getId() + ": " + fields[i].getName();
                } else if (value instanceof PersistentCollection) {
                    rez[i] = "Collection: " + fields[i].getName() + " (H)";
                } else if (value instanceof Collection) {
                    rez[i] = "Collection: " + fields[i].getName();
                } else if (value instanceof Map) {
                    rez[i] = "Map: " + fields[i].getName();
                } else if (value != null) {
                    rez[i] = StringEscapeUtils.escapeXml(String.valueOf(value));
                }

            } catch (Exception ex) {

                rez[i] = "Error: " + fields[i].getName() + ", cause: " + ex.getMessage();

            }
        }

        return rez;

    }


    /**
     * Transform list of objects into list of property array.
     *
     * @param listOfObjects list of objects
     *
     * @return list of object property arrays
     */
    public static List<Object[]> transformTypedResultListToArrayList(List listOfObjects) {

        final List<Object[]> rezList = new ArrayList<Object[]>(listOfObjects.size());

        for (Object obj : listOfObjects) {

            rezList.add(toObjectArray(obj));

        }
        return rezList;
    }

    /**
     * Escape single object or array of objects
     *
     * @param raw objects
     *
     * @return array of objects
     */
    public static Object[] escapeXml(Object raw) {

        if (raw == null) {
            // wrap null into array
            return new Object[] { null };
        }

        if (raw.getClass().isArray()) {
            if (raw instanceof Object[]) {
                // object array - just escape
                final Object[] rawa = (Object[]) raw;
                final Object[] out = new Object[rawa.length];
                for (int i = 0; i < rawa.length; i++) {
                    out[i] = StringEscapeUtils.escapeXml(String.valueOf(rawa[i]));
                }
                return out;
            } else {
                // primitive array - copy over into objects
                int len = Array.getLength(raw);
                Object[] out = new Object[len];
                for(int i = 0; i < len; i++) {
                    out[i] = StringEscapeUtils.escapeXml(String.valueOf(Array.get(raw, i)));
                }
                return out;
            }
        }
        // not array - a single element value
        return new Object[] { StringEscapeUtils.escapeXml(String.valueOf(raw)) };

    }



}
