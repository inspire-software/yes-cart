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

package org.yes.cart.web.service.ws.client;

import edu.emory.mathcs.backport.java.util.Arrays;
import flex.messaging.FlexContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.service.async.model.AsyncContext;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This context uses ThreadLocal context from FlexContext. Ensure that it is instantiated
 * when FlexContext exists.
 *
 * User: denispavlov
 * Date: 12-11-09
 * Time: 8:36 AM
 */
public class AsyncFlexContextImpl implements AsyncContext {

    private final Map<String, Object> attributes = new HashMap<String, Object>();

    public AsyncFlexContextImpl() {
        this(null);
    }

    public AsyncFlexContextImpl(Map<String, Object> attributes) {

        if (FlexContext.getUserPrincipal() == null) {
            throw new IllegalStateException("Flex web context created outside of authenticated environment");
        }

        if (attributes != null) {
            this.attributes.putAll(attributes);
        }
        this.attributes.put(USERNAME, ((UsernamePasswordAuthenticationToken) FlexContext.getUserPrincipal()).getName());
        //String password = (String) ((UsernamePasswordAuthenticationToken) FlexContext.getUserPrincipal()).getCredentials();
        this.attributes.put(CREDENTIALS, FlexContext.getFlexSession().getAttribute(FLEX_SESSION_CREDENTIALS));
        this.attributes.put(SECURITY_CTX, SecurityContextHolder.getContext());
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
