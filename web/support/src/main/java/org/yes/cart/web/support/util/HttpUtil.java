package org.yes.cart.web.support.util;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 9:52 PM
 */
public class HttpUtil {



    public static void dumpRequest(final String prefix, final ServletRequest request) {
        Enumeration en = request.getParameterNames();
        while (en.hasMoreElements()) {
            final Object key = en.nextElement();
            System.out.println(MessageFormat.format("HttpUtil#dumpRequest param key = [{0}] value = [{1}]",
                    key,
                    request.getParameter((String) key)));
        }

        en = request.getAttributeNames();
        while (en.hasMoreElements()) {
            final Object key = en.nextElement();
            System.out.println(MessageFormat.format("HttpUtil#dumpRequest attr  key = [{0}] value = [{1}]",
                    key,
                    request.getAttribute((String) key)));
        }
    }


    /**
     * Work with with param values, when it can return
     * parameter value as string or as array of strings with single value.
     *
     *
     * @param param parameters
     * @return value
     */
    public static String getSingleValue(final Object param) {
        if (param instanceof String) {
            return (String) param;
        } else if (param instanceof Collection) {
            if (!((Collection) param).isEmpty()) {
                return ((Collection) param).iterator().next().toString();
            }
        } else if (param instanceof String[]) {
            if (((String[]) param).length > 0) {
                return ((String[]) param)[0];
            }
        }
        return null;

    }
}
