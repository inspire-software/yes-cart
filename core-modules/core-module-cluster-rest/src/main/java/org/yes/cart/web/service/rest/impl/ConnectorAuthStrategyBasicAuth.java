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

package org.yes.cart.web.service.rest.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.xerces.impl.dv.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

/**
 * User: denispavlov
 * Date: 25/05/2019
 * Time: 12:13
 */
public class ConnectorAuthStrategyBasicAuth implements ConnectorAuthStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectorAuthStrategyBasicAuth.class);

    private AuthenticationManager authenticationManager;

    /**
     * @return the authenticationManager
     */
    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    /**
     * @param authenticationManager the authenticationManager to set
     */
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public boolean authenticated(final HttpServletRequest request) throws BadCredentialsException {
        try {

            final String basic = request.getHeader("Authorization");

            if (basic != null && basic.toLowerCase().startsWith("basic ")) {

                final String[] basicToken = StringUtils.split(basic, ' ');
                final byte[] decoded = basicToken != null && (basicToken.length > 1) ? Base64.decode(basicToken[1]) : null;
                final String decodedString = decoded != null ? new String(decoded, StandardCharsets.UTF_8) : null;
                final String[] userAndPass = decodedString != null && decodedString.contains(":") ?
                        StringUtils.split(decodedString, ':') : new String[] { null, null };
                final String user = userAndPass[0];
                final String passw = userAndPass[1];

                if (StringUtils.isBlank(user) && StringUtils.isBlank(passw)) {
                    throw new BadCredentialsException("Invalid credentials");
                }
                
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user,
                        passw
                );
                LOG.debug("Receiving REST request from user {}", user);
                authentication = authenticationManager.authenticate(authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return true;
                
            }

        } catch (RuntimeException ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }

        return false;
    }
}
