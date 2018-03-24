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

package org.yes.cart.service.async.utils;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.service.async.model.AsyncContext;

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

    private static final ThreadLocal<AsyncContext> CTX = new ThreadLocal<>();

    /**
     * Initialise security context from async context.
     * There are two types that we operate with:
     *  1. AsyncContext - that holds credentials (including password) which is used to access WS
     *  2. SecurityContext - that holds the current Spring security context as {@link AsyncContext#SECURITY_CTX} attribute
     * We nee both of these contexts to allow BulkImport, ReindexService and CacheDirector
     * operate properly
     *
     * @param context async context (most likely derived from AsyncFlexContextImpl that carries 1 & 2)
     */
    public static void init(final AsyncContext context) {
        final SecurityContext security = (SecurityContext) context.getAttribute(AsyncContext.SECURITY_CTX);
        final boolean forceSetSecurity = forceSetSecurityContext(context);
        if (forceSetSecurity) {
            SecurityContextHolder.setContext(security);
        }
        CTX.set(context);
    }

    /**
     * Clear security context.
     */
    public static void clear() {

        final AsyncContext context = getContext();
        final boolean forceSetSecurity = forceSetSecurityContext(context);
        if (forceSetSecurity) {
            SecurityContextHolder.clearContext();
        }
        CTX.remove();
    }

    /**
     * Explicitly remove thread locals to prevent memory leaks.
     */
    public static  void destroy() {
        clear();
    }


    /**
     * @return context for this thread
     */
    public static AsyncContext getContext() {
        return CTX.get();
    }


    private static boolean forceSetSecurityContext(final AsyncContext context) {
        // If this is asynchronous job then we need to set security context in thread
        return context != null && AsyncContext.ASYNC.equals(context.getAttribute(AsyncContext.ASYNC));
    }

}
