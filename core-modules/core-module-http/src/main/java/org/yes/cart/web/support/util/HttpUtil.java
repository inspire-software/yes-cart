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

package org.yes.cart.web.support.util;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.util.HttpParamsUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 9:52 PM
 */
public class HttpUtil {


    private static void dumpParamsAndAttrs(final ServletRequest req, final StringBuilder stringBuilder) {

        stringBuilder.append(HttpParamsUtils.stringify("\nParameters\n", req.getParameterMap()));

        Enumeration attributeNames = req.getAttributeNames();
        final Map<String, String> map = new HashMap<String, String>();
        while (attributeNames.hasMoreElements()) {
            final String key = (String) attributeNames.nextElement();
            map.put(key, String.valueOf(req.getAttribute(key)));
        }
        stringBuilder.append(HttpParamsUtils.stringify("\nAttributes\n", map));

    }

    private static void dumpHeaders(final HttpServletRequest hReq, final StringBuilder stringBuilder) {
        final Enumeration headerNames = hReq.getHeaderNames();
        if (headerNames != null) {
            final Map<String, String> map = new HashMap<String, String>();
            while (headerNames.hasMoreElements()) {
                final String key = (String) headerNames.nextElement();
                map.put(key, String.valueOf(hReq.getHeader(key)));
            }
            stringBuilder.append(HttpParamsUtils.stringify("\nHeaders\n", map));
        }
    }

    private static void dumpCookies(final HttpServletRequest hReq, final StringBuilder stringBuilder) {
        final Cookie[] cookies = hReq.getCookies();
        if (cookies != null) {
            final Map<String, String> map = new HashMap<String, String>();
            for (Cookie cookie : cookies) {
                map.put(cookie.getName(), cookie.getValue());
            }
            stringBuilder.append(HttpParamsUtils.stringify("\nCookies", map));
        }
    }


    /**
     * Dump http request.
     * @param request http request to dump.
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
     * GET request parameters as map.
     *
     * @param requestURL request URL {@link HttpServletRequest#getRequestURL()}
     * @param pathVariables path markers that should be identified as extra parameters
     *
     * @return map of parameters (with preserved other)
     */
    public static Map<String, List<String>> getParameters(final String requestURL,
                                                          final Set<String> pathVariables) {
        return getParameters(requestURL, pathVariables, false);
    }

    /**
     * GET request parameters as map.
     *
     * @param requestURL request URL {@link HttpServletRequest#getRequestURL()}
     * @param pathVariables path markers that should be identified as extra parameters
     * @param removeDuplicates remove duplicate values
     *
     * @return map of parameters (with preserved other)
     */
    public static Map<String, List<String>> getParameters(final String requestURL,
                                                          final Set<String> pathVariables,
                                                          final boolean removeDuplicates) {


        final Map<String, List<String>> parameters = new LinkedHashMap<String, List<String>>();

        try {
            final String[] request = StringUtils.splitPreserveAllTokens(requestURL, '?');

            String key = null;

            if (request != null && request.length > 0) {
                final String[] pathPairs = StringUtils.splitPreserveAllTokens(request[0], '/');

                for (String pathItem : pathPairs) {
                    if (key != null) {
                        final List<String> values;
                        if (!parameters.containsKey(key)) {
                            values = new LinkedList<String>();
                            parameters.put(key, values);
                        } else {
                            values = parameters.get(key);
                        }
                        final String value = URLDecoder.decode(pathItem, "UTF-8");
                        if (!removeDuplicates || !values.contains(value)) {
                            values.add(value);
                        }
                        key = null;
                    } else if (pathVariables.contains(pathItem)) {
                        key = pathItem; // next path is value
                    }
                }
            }

            if (request != null && request.length > 1) {

                final String[] parameterPairs = StringUtils.splitPreserveAllTokens(request[1], '&');
                for (String parameterPair : parameterPairs) {
                    final int idx = parameterPair.indexOf("=");
                    key = idx > 0 ? URLDecoder.decode(parameterPair.substring(0, idx), "UTF-8") : parameterPair;
                    final List<String> values;
                    if (!parameters.containsKey(key)) {
                        values = new LinkedList<String>();
                        parameters.put(key, values);
                    } else {
                        values = parameters.get(key);
                    }
                    final String value = idx > 0 && parameterPair.length() > idx + 1 ? URLDecoder.decode(parameterPair.substring(idx + 1), "UTF-8") : null;
                    if (!removeDuplicates || !values.contains(value)) {
                        values.add(value);
                    }
                }
            }

        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        }
        return parameters;

    }

    /**
     * GET request parameters as map.
     *
     * @param request request
     * @param pathVariables path markers that should be identified as extra parameters
     *
     * @return map of parameters (with preserved other)
     */
    public static Map<String, List<String>> getParameters(final HttpServletRequest request,
                                                          final Set<String> pathVariables) {

        return getParameters(request, pathVariables, false);
    }

    /**
     * GET request parameters as map.
     *
     * @param request request
     * @param pathVariables path markers that should be identified as extra parameters
     * @param removeDuplicates remove duplicate values
     *
     * @return map of parameters (with preserved other)
     */
    public static Map<String, List<String>> getParameters(final HttpServletRequest request,
                                                          final Set<String> pathVariables,
                                                          final boolean removeDuplicates) {

        final String query = request.getQueryString();
        if (StringUtils.isNotBlank(query)) {
            return getParameters(request.getRequestURL().toString().concat("?").concat(query), pathVariables, removeDuplicates);
        }
        return getParameters(request.getRequestURL().toString(), pathVariables, removeDuplicates);
    }


    /**
     * GET and POST request parameters as map.
     *
     * @param request request
     * @param pathVariables path markers that should be identified as extra parameters
     *
     * @return map of parameters (with preserved other)
     */
    public static Map<String, List<String>> allParameters(final HttpServletRequest request,
                                                          final Set<String> pathVariables) {

        return allParameters(request, pathVariables, false);
    }


    /**
     * GET and POST request parameters as map.
     *
     * @param request request
     * @param pathVariables path markers that should be identified as extra parameters
     * @param removeDuplicates remove duplicate values
     *
     * @return map of parameters (with preserved other)
     */
    public static Map<String, List<String>> allParameters(final HttpServletRequest request,
                                                          final Set<String> pathVariables,
                                                          final boolean removeDuplicates) {

        final Map<String, List<String>> params = getParameters(request, pathVariables, removeDuplicates);

        for (final Map.Entry<String, Object> param : (Set<Map.Entry>) request.getParameterMap().entrySet()) {

            final String key = param.getKey();

            final List<String> values;
            if (!params.containsKey(key)) {
                values = new LinkedList<String>();
                params.put(key, values);
            } else {
                values = params.get(key);
            }

            if (param.getValue() instanceof String[]) {
                for (final String value : (String[]) param.getValue()) {
                    if (!removeDuplicates || !values.contains(value)) {
                        values.add(value);
                    }
                }
            } else if (param.getValue() instanceof String) {
                final String value = (String) param.getValue();
                if (!removeDuplicates || !values.contains(value)) {
                    values.add(value);
                }
            }

        }

        return params;
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
                if (param instanceof List) {
                    return ((List) param).get(0).toString();
                }
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
