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
import org.springframework.util.ObjectUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.web.application.ApplicationDirector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Date: 05/09/2020
 * Time: 16:07
 */
public class RestCorsFilter extends CorsFilter {

    private static final List<String> PREFLIGHT = Collections.singletonList("*");

    /**
     * Constructor accepting a {@link CorsConfigurationSource} used by the filter
     * to find the {@link CorsConfiguration} to use for each incoming request.
     *
     * @see UrlBasedCorsConfigurationSource
     */
    public RestCorsFilter() {
        super(configurationSource());
    }

    private static UrlBasedCorsConfigurationSource configurationSource() {
        CorsConfiguration config = new CorsConfiguration() {

            @Override
            public List<String> getAllowedOrigins() {
                final Shop shop = ApplicationDirector.getCurrentShop();
                if (shop != null) {
                    final List<String> origins = new ArrayList<>();
                    final String ao = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_CORS_ALLOWED_ORIGINS);
                    if (StringUtils.isNotBlank(ao)) {
                        if (ao.indexOf(',') != -1) {
                            origins.addAll(Arrays.asList(StringUtils.split(ao, ',')));
                        } else {
                            origins.add(ao);
                        }
                    }
                    return origins;
                } else { // Shop filter would respond with 400 if it is not pre-flight
                    return PREFLIGHT;
                }
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
        config.addExposedHeader("X-CW-TOKEN");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    
}
