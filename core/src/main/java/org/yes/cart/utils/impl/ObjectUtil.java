package org.yes.cart.utils.impl;

import org.hibernate.collection.AbstractPersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.util.Assert;
import org.yes.cart.domain.entity.Auditable;

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
