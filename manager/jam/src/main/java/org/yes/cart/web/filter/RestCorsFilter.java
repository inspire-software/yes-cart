/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.web.filter;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ObjectUtils;
import org.springframework.web.cors.*;
import org.springframework.web.filter.OncePerRequestFilter;
import org.yes.cart.utils.RuntimeConstants;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Re-vamping "org.springframework.web.filter.CorsFilter" so that we can inject dynamic configs.
 *
 * User: inspiresoftware
 * Date: 10/12/2020
 * Time: 09:50
 */
public class RestCorsFilter extends OncePerRequestFilter {

    private List<String> allowedOrigins = Collections.emptyList();

    private CorsConfigurationSource configSource;
    private CorsProcessor processor = new DefaultCorsProcessor();

    private UrlBasedCorsConfigurationSource configurationSource() {

        final CorsConfiguration config = new CorsConfiguration() {

            @Override
            public List<String> getAllowedOrigins() {
                return allowedOrigins;
            }

            @Override
            public String checkOrigin(final String requestOrigin) {
                if (!org.springframework.util.StringUtils.hasText(requestOrigin)) {
                    return null;
                }
                final List<String> allowedOrigins = getAllowedOrigins();
                if (ObjectUtils.isEmpty(allowedOrigins)) {
                    return null;
                }

                if (allowedOrigins.contains(ALL)) {
                    if (this.getAllowCredentials() != Boolean.TRUE) {
                        return ALL;
                    }
                    else {
                        return requestOrigin;
                    }
                }
                for (String allowedOrigin : allowedOrigins) {
                    if (requestOrigin.equalsIgnoreCase(allowedOrigin)) {
                        return requestOrigin;
                    }
                }

                return null;

            }
        };

        config.setAllowCredentials(true);

        config.addAllowedHeader("*");

        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");

        config.addExposedHeader("Pragma");
        config.addExposedHeader("Cache-Control");
        config.addExposedHeader("Expires");
        config.addExposedHeader("Date");
        config.addExposedHeader("Last-Modified");
        config.addExposedHeader("Connection");
        config.addExposedHeader("Keep-Alive");
        config.addExposedHeader("Content-Encoding");
        config.addExposedHeader("Content-Language");
        config.addExposedHeader("Content-Length");
        config.addExposedHeader("Content-Type");
        config.addExposedHeader("Transfer-Encoding");
        config.addExposedHeader(HttpHeaders.AUTHORIZATION);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final CorsConfiguration corsConfiguration = this.configSource.getCorsConfiguration(request);
        boolean isValid = this.processor.processRequest(corsConfiguration, request, response);
        if (isValid && !CorsUtils.isPreFlightRequest(request)) {
            filterChain.doFilter(request, response);
        }
    }


    public void setConfig(final RuntimeConstants config) {

        final String allowedOrigins = config.getConstant("admin.api.cors.allowed.origins");
        if (StringUtils.isNotBlank(allowedOrigins)) {
            final List<String> origins = new ArrayList<>();
            final String ao = allowedOrigins;
            if (StringUtils.isNotBlank(ao)) {
                if (ao.indexOf(',') != -1) {
                    origins.addAll(Arrays.asList(StringUtils.split(ao, ',')));
                } else {
                    origins.add(ao);
                }
            }
            this.allowedOrigins = origins;
        }
        this.configSource = this.configurationSource();

    }


}
