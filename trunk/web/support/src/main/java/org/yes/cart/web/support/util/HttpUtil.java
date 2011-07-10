package org.yes.cart.web.support.util;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Enumeration;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 9:52 PM
 */
public class HttpUtil {

    public static void requestDump(final HttpServletRequest request) {
        Enumeration en = request.getParameterNames();
        while (en.hasMoreElements()) {
            final Object key = en.nextElement();
            System.out.println(MessageFormat.format("HttpUtil#requestDump param key = [{0}] value = [{1}]",
                    key,
                    request.getParameter((String) key)));
        }

        en = request.getAttributeNames();
        while (en.hasMoreElements()) {
            final Object key = en.nextElement();
            System.out.println(MessageFormat.format("HttpUtil#requestDump attr  key = [{0}] value = [{1}]",
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
        } else if (param instanceof String[]) {
            if (((String[]) param).length > 0) {
                return ((String[]) param)[0];
            }
        }
        return null;

    }
}
