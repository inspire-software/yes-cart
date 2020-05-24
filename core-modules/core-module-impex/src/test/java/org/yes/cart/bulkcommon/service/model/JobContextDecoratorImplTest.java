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

package org.yes.cart.bulkcommon.service.model;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.model.JobContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 24/03/2018
 * Time: 01:28
 */
public class JobContextDecoratorImplTest {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testAsyncAttributeNotOverwritten() throws Exception {

        final JobContext ctx = this.mockery.mock(JobContext.class);

        this.mockery.checking(new Expectations() {{
            allowing(ctx).getAttribute(AsyncContext.ASYNC); will(returnValue("X"));
        }});

        final JobContextDecoratorImpl decorator = new JobContextDecoratorImpl(ctx, Collections.singletonMap(AsyncContext.ASYNC, AsyncContext.ASYNC));

        assertEquals("X", decorator.getAttribute(AsyncContext.ASYNC));


    }

    @Test
    public void testAttributeOverride() throws Exception {

        final JobContext ctx = this.mockery.mock(JobContext.class);

        final Map<String, Object> origin = new HashMap<>();
        origin.put("Override", "X");
        origin.put("Origin", "Z");

        this.mockery.checking(new Expectations() {{
            allowing(ctx).getAttributes(); will(returnValue(origin));
            allowing(ctx).getAttribute("Origin"); will(returnValue("Z"));
        }});

        final JobContextDecoratorImpl decorator = new JobContextDecoratorImpl(ctx, Collections.singletonMap("Override", "Y"));

        assertEquals("Y", decorator.getAttribute("Override"));
        assertEquals("Y", decorator.getAttributes().get("Override"));
        assertEquals("Z", decorator.getAttribute("Origin"));
        assertEquals("Z", decorator.getAttributes().get("Origin"));


    }

    @Test
    public void testAttributeOverrideNull() throws Exception {

        final JobContext ctx = this.mockery.mock(JobContext.class);

        final Map<String, Object> origin = new HashMap<>();
        origin.put("Override", "X");
        origin.put("Origin", "Z");

        this.mockery.checking(new Expectations() {{
            allowing(ctx).getAttributes(); will(returnValue(origin));
            allowing(ctx).getAttribute("Origin"); will(returnValue("Z"));
        }});

        final JobContextDecoratorImpl decorator = new JobContextDecoratorImpl(ctx, Collections.singletonMap("Override", null));

        assertNull(decorator.getAttribute("Override"));
        assertNull(decorator.getAttributes().get("Override"));
        assertEquals("Z", decorator.getAttribute("Origin"));
        assertEquals("Z", decorator.getAttributes().get("Origin"));

    }
}