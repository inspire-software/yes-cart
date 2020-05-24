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

package org.yes.cart.service.async.model.impl;

import org.junit.Test;
import org.yes.cart.service.async.model.AsyncContext;

import java.util.Collections;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 24/03/2018
 * Time: 01:18
 */
public class JobContextImplTest {

    @Test
    public void isAsyncFalse() throws Exception {

        final JobContextImpl ctx = new JobContextImpl(false, null);
        assertFalse(ctx.isAsync());
        assertNull(ctx.getAttribute(AsyncContext.ASYNC));

    }

    @Test
    public void isAsyncFalseP() throws Exception {

        final JobContextImpl ctx = new JobContextImpl(false, null, Collections.singletonMap("K", "V"));
        assertFalse(ctx.isAsync());
        assertNull(ctx.getAttribute(AsyncContext.ASYNC));
        assertEquals("V", ctx.getAttribute("K"));

    }

    @Test
    public void isAsyncFalseOverride() throws Exception {

        final JobContextImpl ctx = new JobContextImpl(false, null, Collections.singletonMap(AsyncContext.ASYNC, AsyncContext.ASYNC));
        assertFalse(ctx.isAsync());
        assertNull(ctx.getAttribute(AsyncContext.ASYNC));

    }


    @Test
    public void isAsyncTrue() throws Exception {

        final JobContextImpl ctx = new JobContextImpl(true, null);
        assertTrue(ctx.isAsync());
        assertEquals(AsyncContext.ASYNC, ctx.getAttribute(AsyncContext.ASYNC));

    }

    @Test
    public void isAsyncTrueP() throws Exception {

        final JobContextImpl ctx = new JobContextImpl(true, null, Collections.singletonMap("K", "V"));
        assertTrue(ctx.isAsync());
        assertEquals(AsyncContext.ASYNC, ctx.getAttribute(AsyncContext.ASYNC));
        assertEquals("V", ctx.getAttribute("K"));

    }

    @Test
    public void isAsyncTrueOverride() throws Exception {

        final JobContextImpl ctx = new JobContextImpl(true, null, Collections.singletonMap(AsyncContext.ASYNC, "XXX"));
        assertTrue(ctx.isAsync());
        assertEquals(AsyncContext.ASYNC, ctx.getAttribute(AsyncContext.ASYNC));

    }

}