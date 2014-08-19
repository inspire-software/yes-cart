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

package org.yes.cart.utils.impl;

import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.yes.cart.domain.entity.Auditable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
     * Split given object to array of his field values.
     *
     * @param obj object to convert
     * @return array of fileds values.
     */
    public static Object[] toObjectArray(final Object obj) {

        if (obj == null) {
            return new Object[]{"Null"};
        }

        if (obj.getClass().getName().startsWith("java.lang")) {
            return new Object[]{String.valueOf(obj)};
        }



        Field[] fields = obj.getClass().getDeclaredFields();

        Object[] rez = new Object[fields.length];

        for (int i = 0; i < fields.length; i++) {

            if (Modifier.isStatic(fields[i].getModifiers())) {
                continue;
            }

            if (!fields[i].isAccessible()) {
                fields[i].setAccessible(true);
            }

            try {

                rez[i] = fields[i].get(obj);

                if (rez[i] instanceof HibernateProxy
                        || rez[i] instanceof AbstractPersistentCollection
                        || rez[i] instanceof Auditable
                        ) {
                    rez[i] = fields[i].getName();
                }

            } catch (Exception ex) {

                rez[i] = "Cant get result for " + fields[i].getName() + " field : " + ex.getMessage();

            }
        }

        return rez;

    }

}
