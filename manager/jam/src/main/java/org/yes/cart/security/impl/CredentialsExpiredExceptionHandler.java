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
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: denispavlov
 * Date: 19/05/2018
 * Time: 08:32
 */
public class CredentialsExpiredExceptionHandler extends
        SimpleUrlAuthenticationFailureHandler {

    private String usernameParameter = "j_username";
    private String expiredAttribute = "expired";

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final AuthenticationException exception) throws IOException, ServletException {

        final String user = request.getParameter(usernameParameter);

        request.setAttribute(usernameParameter, user);
        request.setAttribute(expiredAttribute, expiredAttribute);

        super.onAuthenticationFailure(request, response, exception);

    }

    /**
     * Spring IoC.
     *
     * @param usernameParameter username parameter
     */
    public void setUsernameParameter(final String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    /**
     * Spring IoC.
     *
     * @param expiredAttribute expired password marker
     */
    public void setExpiredAttribute(final String expiredAttribute) {
        this.expiredAttribute = expiredAttribute;
    }
}
