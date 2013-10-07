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

package org.yes.cart.service.async.utils;

import flex.messaging.FlexContext;
import flex.messaging.NonHttpFlexSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.service.async.model.AsyncContext;

import java.util.UUID;

/**
 * Thread local security allows to set security context for current thread.
 * This is necessary to allows access to web services and secure method
 * invocations.
 *
 * User: denispavlov
 * Date: 13-10-02
 * Time: 11:35 PM
 */
public final class ThreadLocalAsyncContextUtils {

    private static final ThreadLocal<AsyncContext> ctx = new ThreadLocal<AsyncContext>();

    /**
     * Initialise security context from async context.
     * There are two types that we operate with:
     *  1. SecurityContextHolder - that holds the current Spring security context
     *  2. FlexContext - that holds flex credentials (including password) which is used to access WS
     * We nee both of these contexts to allow BulkImport, ReindexService and CacheDirector
     * operate properly
     *
     * @param context async context (most likely derived from AsyncFlexContextImpl that carries 1 & 2)
     */
    public static void init(final AsyncContext context) {
        final SecurityContext security = (SecurityContext) context.getAttribute(AsyncContext.SECURITY_CTX);
        SecurityContextHolder.setContext(security);
        ctx.set(context);
    }

    /**
     * Clear security context.
     */
    public static void clear() {
        SecurityContextHolder.clearContext();
        ctx.set(null);
    }

    /**
     * @return context for this thread
     */
    public static AsyncContext getContext() {
        return ctx.get();
    }

}
