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

package org.yes.cart.web.service.rest.impl;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;

/**
 * User: denispavlov
 * Date: 25/05/2019
 * Time: 11:32
 */
public interface ConnectorAuthStrategy {

    /**
     * Authenticate according to rules of this strategy.
     *
     * @param request request to authenticate
     *
     * @return true if request was authenticated
     *
     * @throws AuthenticationException if this strategy is applicable but credentials were invalid
     */
    boolean authenticated(HttpServletRequest request) throws BadCredentialsException;

}
