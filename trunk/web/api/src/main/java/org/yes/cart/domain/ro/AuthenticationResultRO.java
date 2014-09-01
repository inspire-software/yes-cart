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

package org.yes.cart.domain.ro;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: denispavlov
 * Date: 21/08/2014
 * Time: 14:55
 */
@XmlRootElement(name = "authentication-result")
public class AuthenticationResultRO {

    private boolean authenticated;
    private String code;
    private String greeting;
    private TokenRO tokenRO;

    public AuthenticationResultRO() {
    }

    /**
     * Failure constructor.
     *
     * @param code error code
     */
    public AuthenticationResultRO(final String code) {
        this.authenticated = false;
        this.code = code;
    }


    /**
     * Success constructor.
     *
     * @param greeting user greeting
     * @param token token
     */
    public AuthenticationResultRO(final String greeting, final TokenRO token) {
        this.authenticated = true;
        this.greeting = greeting;
        this.tokenRO = token;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(final boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(final String greeting) {
        this.greeting = greeting;
    }

    @XmlElement(name = "token")
    public TokenRO getTokenRO() {
        return tokenRO;
    }

    public void setTokenRO(final TokenRO tokenRO) {
        this.tokenRO = tokenRO;
    }
}
