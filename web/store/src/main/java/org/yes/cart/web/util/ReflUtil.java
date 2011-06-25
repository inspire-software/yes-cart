package org.yes.cart.web.util;

import java.lang.reflect.Field;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/24/11
 * Time: 3:44 PM
 */
public class ReflUtil {

    /**
     * Get filed value using reflection.
     * @param clazz      class
     * @param instance   instance of class
     * @param name       field name.
     * @return   value of filed if found or null otherwise.
     */
    public static Object getFieldValue(final Class<?> clazz, final Object instance, final String name) {
        try {
            Field f = clazz.getDeclaredField(name);
            if(!f.isAccessible()) {
                f.setAccessible(true);
            }
            return f.get(instance);
        } catch (NoSuchFieldException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

}
