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
package org.yes.cart.web.service.rest.impl;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * User: denispavlov
 * Date: 25/05/2019
 * Time: 11:29
 */
public class ConnectorInterceptor extends WebContentInterceptor {

    private List<ConnectorAuthStrategy> strategies;

    public ConnectorInterceptor(final List<ConnectorAuthStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws ServletException {

        for (final ConnectorAuthStrategy strategy : this.strategies) {

            if (strategy.authenticated(request)) {

                return super.preHandle(request, response, handler);

            }

        }

        throw new BadCredentialsException("Forbidden");

    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        SecurityContextHolder.clearContext();
    }
}
