/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.WSUsernameTokenPrincipal;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.yes.cart.util.ShopCodeContext;

import java.util.List;
import java.util.Map;

/**
 *
 * Copy snatched from http://forum.springsource.org/showthread.php?64492-WS-Security-integration
 * and adapted to new version.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/29/12
 * Time: 12:09 AM
 */
public class BackdoorAuthenticationInInterceptor extends WSS4JInInterceptor implements InitializingBean {

    private AuthenticationManager authenticationManager;

    public BackdoorAuthenticationInInterceptor(){
        super();
    }
    public BackdoorAuthenticationInInterceptor(Map<String, Object> properties){
        super(properties);
    }

    /** {@inheritDoc} */
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(getAuthenticationManager(), "Authentication manager must be set");
        Assert.notNull(getProperties(),"Interceptor properties must be set, even if empty");
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


    /** {@inheritDoc} */
    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        try {
            super.handleMessage(message);
            List<WSHandlerResult> result = (List<WSHandlerResult>) message.getContextualProperty(WSHandlerConstants.RECV_RESULTS);
            if (result != null && !result.isEmpty()) {
                for (WSHandlerResult res : result) {
                    // loop through security engine results
                    for (WSSecurityEngineResult securityResult :  res.getResults()) {
                        int action = (Integer) securityResult.get(WSSecurityEngineResult.TAG_ACTION);
                        // determine if the action was a username token
                        if ((action & WSConstants.UT) > 0) {
                            // get the principal object
                            final WSUsernameTokenPrincipal principal = (WSUsernameTokenPrincipal) securityResult.get(WSSecurityEngineResult.TAG_PRINCIPAL);
                            if (principal.getPassword()==null){
                                principal.setPassword("");
                            }
                            Authentication authentication = new UsernamePasswordAuthenticationToken(principal.getName(), principal.getPassword());
                            authentication = authenticationManager.authenticate(authentication);
                            if (!authentication.isAuthenticated()) {
                                LoggerFactory.getLogger(ShopCodeContext.getShopCode()).warn("User " + principal.getName() + " is not authentic.");
                            }
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }
            }
        } catch (RuntimeException ex) {
            LoggerFactory.getLogger(ShopCodeContext.getShopCode()).error(ex.getMessage(), ex);

            throw ex;
        }
    }


}