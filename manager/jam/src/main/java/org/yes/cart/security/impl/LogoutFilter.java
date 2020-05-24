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

package org.yes.cart.security.impl;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: denispavlov
 * Date: 22/06/2019
 * Time: 17:40
 */
public class LogoutFilter extends OncePerRequestFilter {

    private RequestMatcher requiresLogoffRequestMatcher = new AntPathRequestMatcher(JWTUtil.AUTH_LOGIN_URL, "DELETE");


    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain chain) throws ServletException, IOException {


        if (requiresLogoffRequestMatcher.matches(request)) {

            final boolean debug = this.logger.isDebugEnabled();

            if (debug) {
                this.logger.info("Log off for user '");
            }

            JWTUtil.sendLogOffJWT(response);
            return;

        }

        chain.doFilter(request, response);
    }

}