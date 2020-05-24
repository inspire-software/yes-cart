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
package org.yes.cart.service.endpoint.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.yes.cart.service.dto.ManagementService;
import org.yes.cart.service.endpoint.IndexController;

import javax.servlet.http.HttpServletRequest;

/**
 * User: denispavlov
 * Date: 19/05/2017
 * Time: 11:46
 */
@Controller
public class IndexControllerImpl implements IndexController {

    @Autowired
    private ManagementService managementService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public String login() {
        return "login";
    }

    @Override
    public String index() {
        return "index";
    }

    @Override
    public String changePassword(final HttpServletRequest request) {

        String user = request.getParameter("j_username");
        final String pass = request.getParameter("j_password");

        request.setAttribute("j_username", user);

        if (StringUtils.isNotBlank(user) && StringUtils.isNotBlank(pass)) {

            boolean changePass = false;
            try {
                final Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user, pass));
                if (!auth.isAuthenticated()) {
                    request.setAttribute("error", "auth");
                } else {
                    changePass = true;
                }
            } catch (CredentialsExpiredException cee) {
                // OK this is what we are here for
                request.setAttribute("expired", "expired");
                changePass = true;
            } catch (AuthenticationException ae) {
                request.setAttribute("error", "auth");
            }

            if (changePass) {
                final String pass2 = request.getParameter("j_password2");
                final String pass2c = request.getParameter("j_password2c");

                if (pass.equals(pass2)) {
                    request.setAttribute("error", "sameasold");
                } else if (StringUtils.isBlank(pass2) || StringUtils.isBlank(pass2c) || !pass2.equals(pass2c)) {
                    request.setAttribute("error", "nomatch");
                } else {
                    try {
                        managementService.updatePassword(user, pass2, request.getLocale().getLanguage());
                        new SecurityContextLogoutHandler().logout(request, null, null);
                        return "redirect:login.jsp?newpass";
                    } catch (BadCredentialsException bce) {
                        request.setAttribute("error", bce.getMessage());
                    }
                }
            }

        }

        return "changepassword";
    }
}
