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

package org.yes.cart.web.support.request.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.web.support.request.IPResolver;

import javax.servlet.http.HttpServletRequest;

/**
 * User: denispavlov
 * Date: 03/06/2015
 * Time: 10:18
 */
public class HeaderOrientedIPResolverImpl implements IPResolver {

    private final String[] HEADER_CHAIN = new String[] {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    /**
     * {@inheritDoc}
     */
    public String resolve(final HttpServletRequest httpServletRequest) {

        for (final String header : HEADER_CHAIN) {

            String ipAddress = httpServletRequest.getHeader(header);
            if (StringUtils.isNotBlank(ipAddress)) {
                return ipAddress;
            }

        }

        return httpServletRequest.getRemoteAddr();
    }
}
