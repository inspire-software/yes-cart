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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.yes.cart.service.dto.ManagementService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * User: denispavlov
 * Date: 31/05/2019
 * Time: 09:40
 */
public class ChangePasswordFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ManagementService managementService;

    private AuthenticationManager authenticationManager;

    private RequestMatcher requiresChangePwdRequestMatcher = new AntPathRequestMatcher(JWTUtil.AUTH_CHANGE_PWD_URL, "POST");


    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain chain) throws ServletException, IOException {


        if (requiresChangePwdRequestMatcher.matches(request)) {

            final boolean debug = this.logger.isDebugEnabled();

            try {

                LoginData creds = objectMapper
                        .readValue(request.getInputStream(), LoginData.class);

                if (debug) {
                    this.logger
                            .info("Change password for user '"
                                    + (creds != null ? creds.getUsername() : "N/A") + "'");
                }

                if (creds != null && StringUtils.isNotBlank(creds.getUsername()) && StringUtils.isNotBlank(creds.getPassword())) {

                    try {
                        final Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(creds.getUsername(), creds.getPassword()));
                        if (!auth.isAuthenticated()) {
                            this.logger
                                    .info("Change password for user '"
                                            + creds.getUsername() + "' bad credentials");
                            sendResponse(response, JWTUtil.CredentialsState.AUTH_CREDENTAILS_INVALID.name());
                            return;
                        }
                        this.logger
                                .info("Change password for user '"
                                        + creds.getUsername() + "' still valid old credentials");
                    } catch (CredentialsExpiredException cee) {
                        // OK this is what we are here for
                        this.logger
                                .info("Change password for user '"
                                        + creds.getUsername() + "' old credentials expired ");
                    } catch (AuthenticationException ae) {
                        sendResponse(response, JWTUtil.CredentialsState.AUTH_CREDENTAILS_INVALID.name());
                        return;
                    }

                    final String pass2 = creds.getNpassword();
                    final String pass2c = creds.getCpassword();

                    if (creds.getPassword().equalsIgnoreCase(pass2)) {
                        this.logger
                                .info("Change password for user '"
                                        + creds.getUsername() + "' cannot use previous password ");
                        sendResponse(response, JWTUtil.CredentialsState.AUTH_CHANGEPWD_SAMEASOLD.name());
                        return;
                    } else if (StringUtils.isBlank(pass2) || StringUtils.isBlank(pass2c) || !pass2.equals(pass2c)) {
                        this.logger
                                .info("Change password for user '"
                                        + creds.getUsername() + "' new and confirm don't match ");
                        sendResponse(response, JWTUtil.CredentialsState.AUTH_CHANGEPWD_NOMATCH.name());
                        return;
                    } else {
                        try {
                            managementService.updatePassword(creds.getUsername(), pass2, request.getLocale().getLanguage());
                            new SecurityContextLogoutHandler().logout(request, null, null);
                            this.logger
                                    .info("Change password for user '"
                                            + creds.getUsername() + "' changed successfully ");

                            sendResponse(response, null);
                            return;

                        } catch (BadCredentialsException bce) {
                            this.logger
                                    .info("Change password for user '"
                                            + creds.getUsername() + "' new credentials invalid ");
                            sendResponse(response, bce.getMessage());
                            return;
                        }
                    }

                }

            } catch (AuthenticationException failed) {

                SecurityContextHolder.clearContext();

                if (debug) {
                    this.logger.debug("Change password failed: " + failed);
                }

                sendResponse(response, failed.getMessage());
                return;
            }

        }

        chain.doFilter(request, response);
    }

    private void sendResponse(final HttpServletResponse response, final String error) throws IOException {

        final boolean success = StringUtils.isBlank(error);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        final StringBuilder jsonRsp = new StringBuilder();
        jsonRsp.append("{\"status\":").append(success ? 200 : 400);
        if (!success) {
            jsonRsp.append(",\"error\":\"").append(error).append("\"");
        }
        jsonRsp.append("}");
        response.getWriter().write(jsonRsp.toString());

    }

    /**
     * Spring IoC.
     *
     * @param managementService management service
     */
    public void setManagementService(final ManagementService managementService) {
        this.managementService = managementService;
    }

    /**
     * Spring IoC.
     *
     * @param authenticationManager auth manager
     */
    public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}
