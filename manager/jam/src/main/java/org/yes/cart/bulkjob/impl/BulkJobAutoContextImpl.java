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

package org.yes.cart.bulkjob.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.service.async.model.AsyncContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This context uses Spring security context, so authentication has to be set.
 *
 * User: denispavlov
 * Date: 12-11-09
 * Time: 8:36 AM
 */
public class BulkJobAutoContextImpl implements AsyncContext {

    private final Map<String, Object> attributes = new HashMap<String, Object>();

    public BulkJobAutoContextImpl() {
        this(null);
    }

    public BulkJobAutoContextImpl(Map<String, Object> attributes) {

        final SecurityContext sc = SecurityContextHolder.getContext();

        if (sc == null || sc.getAuthentication() == null) {
            throw new IllegalStateException("Auto import context created outside of authenticated environment");
        }

        if (!sc.getAuthentication().isAuthenticated()) {
            throw new IllegalStateException("Auto import context authentication is invalid");
        }

        if (attributes != null) {
            this.attributes.putAll(attributes);
        }
        this.attributes.put(USERNAME, sc.getAuthentication().getName());
        this.attributes.put(CREDENTIALS, sc.getAuthentication().getCredentials());
        this.attributes.put(SECURITY_CTX, sc);
        for (final GrantedAuthority auth : sc.getAuthentication().getAuthorities()) {
            if ("ROLE_AUTO".equals(auth.getAuthority())) {
                this.attributes.put(NO_BROADCAST, NO_BROADCAST); // Prevent WS broadcasts if we have null
            }
        }
    }

    /** {@inheritDoc} */
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }

    /** {@inheritDoc} */
    public <T> T getAttribute(final String name) {
        if (this.attributes.containsKey(name)) {
            return (T) this.attributes.get(name);
        }
        return null;
    }
}
