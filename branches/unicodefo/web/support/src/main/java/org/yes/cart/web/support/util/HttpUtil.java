package org.yes.cart.web.support.util;

import org.yes.cart.constants.AttributeNamesKeys;

import javax.mail.Message;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
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


    private static void dumpParamsAndAttrs(final ServletRequest req, final StringBuilder stringBuilder) {
        final Enumeration parameterNames = req.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            final String key = (String) parameterNames.nextElement();
            stringBuilder.append(MessageFormat.format("\nParameter {0}={1}" , key, req.getParameter(key)));
        }
        Enumeration attributeNames = req.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final String key = (String) attributeNames.nextElement();
            stringBuilder.append(MessageFormat.format("\nAttribute {0}={1}" , key, req.getAttribute(key)));
        }
    }

    private static void dumpHeaders(final HttpServletRequest hReq, final StringBuilder stringBuilder) {
        final Enumeration headerNames = hReq.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                final String key = (String) headerNames.nextElement();
                stringBuilder.append(MessageFormat.format("\nHeader {0}={1}" , key, hReq.getHeader(key)));
            }
        }
    }

    private static void dumpCookies(final HttpServletRequest hReq, final StringBuilder stringBuilder) {
        final Cookie[] cookies = hReq.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                stringBuilder.append(MessageFormat.format("\nCookie {0}={1}" , cookie.getName(), cookie.getValue()));
            }
        }
    }


    /**
     * Dump http request.
     * @param request thhp request to dump.
     * @return String with dump.
     */
    public static String dumpRequest(final HttpServletRequest request) {
        final StringBuilder stringBuilder = new StringBuilder();
        if (request == null) {
            stringBuilder.append("#dumpRequest request is null");
        } else {
            dumpParamsAndAttrs(request, stringBuilder);
            dumpHeaders(request, stringBuilder);
            dumpCookies(request, stringBuilder);
        }
        return stringBuilder.toString();
    }


    /**
     * Work with with param values, when it can return
     * parameter value as string or as array of strings with single value.
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
