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

package org.yes.cart.service.async.utils;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * User: denispavlov
 * Date: 22/10/2015
 * Time: 18:29
 */
public class RunAsUserAuthentication extends AbstractAuthenticationToken {

    private final String username;
    private final String password;

    public RunAsUserAuthentication(final String username, final String password, final Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.username = username;
        this.password = password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getCredentials() {
        return password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getPrincipal() {
        return username;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthenticated() {
        return true;
    }

}
