/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.security.impl;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: denispavlov
 * Date: 01/06/2019
 * Time: 19:26
 */
public class RestAwareExceptionTranslationFilter extends ExceptionTranslationFilter {

    public RestAwareExceptionTranslationFilter(final String loginFormUrl) {
        super(new LoginUrlAuthenticationEntryPoint(loginFormUrl));
    }

    @Override
    protected void sendStartAuthentication(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain, final AuthenticationException reason) throws ServletException, IOException {

        if ("application/json".equalsIgnoreCase(request.getHeader("Accept"))) {
            // REST API response
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            final StringBuilder error = new StringBuilder();
            error.append("{\"error\":\"UNAUTHORIZED\"}");
            response.getWriter().write(error.toString());
        } else {
            // Redirect to login page
            super.sendStartAuthentication(request, response, chain, reason);
        }
    }
}
