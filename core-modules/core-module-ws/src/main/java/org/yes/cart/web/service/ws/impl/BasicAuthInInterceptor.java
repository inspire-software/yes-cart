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

package org.yes.cart.web.service.ws.impl;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.interceptor.SoapInterceptor;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * User: denispavlov
 * Date: 24/10/2017
 * Time: 16:52
 */
public class BasicAuthInInterceptor extends AbstractSoapInterceptor implements SoapInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(BasicAuthInInterceptor.class);

    private AuthenticationManager authenticationManager;

    public BasicAuthInInterceptor() {
        super(Phase.PRE_PROTOCOL);
    }

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
    public void handleMessage(final SoapMessage message) throws Fault {
        try {
            final Object policy = message.get(AuthorizationPolicy.class);

            if (policy instanceof AuthorizationPolicy) {

                final AuthorizationPolicy auth = (AuthorizationPolicy) policy;

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        auth.getUserName(),
                        auth.getPassword()
                );
                LOG.debug("Receiving WS request from user {}", auth.getUserName());
                authentication = authenticationManager.authenticate(authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else {
                throw new BadCredentialsException("BasicAuth is required");
            }


        } catch (RuntimeException ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }
}
