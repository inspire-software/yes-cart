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
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 19/05/2018
 * Time: 08:23
 */
public class ExceptionMappingAuthenticationFailureHandler extends
        SimpleUrlAuthenticationFailureHandler {

    private final Map<String, AuthenticationFailureHandler> failureUrlMap = new HashMap<>();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        final AuthenticationFailureHandler handler = failureUrlMap.get(exception.getClass().getName());

        if (handler != null) {
            handler.onAuthenticationFailure(request, response, exception);
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }

    /**
     * Sets the map of exception types (by name) to URLs.
     *
     * @param failureUrlMap the map keyed by the fully-qualified name of the exception
     *                      class, with the corresponding failure URL as the value.
     * @throws IllegalArgumentException if the entries are not Strings or the URL is not
     *                                  valid.
     */
    public void setExceptionMappings(Map<?, ?> failureUrlMap) {
        this.failureUrlMap.clear();
        for (Map.Entry<?, ?> entry : failureUrlMap.entrySet()) {
            Object exception = entry.getKey();
            Object handler = entry.getValue();
            Assert.isInstanceOf(String.class, exception,
                    "Exception key must be a String (the exception classname).");
            Assert.isInstanceOf(AuthenticationFailureHandler.class, handler,
                    "URL must be an AuthenticationFailureHandler");
            this.failureUrlMap.put((String) exception, (AuthenticationFailureHandler) handler);
        }
    }
}
