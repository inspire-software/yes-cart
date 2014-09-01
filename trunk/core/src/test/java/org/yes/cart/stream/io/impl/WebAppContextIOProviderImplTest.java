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

package org.yes.cart.stream.io.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;

import javax.servlet.ServletContext;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: denispavlov
 * Date: 30/08/2014
 * Time: 20:03
 */
public class WebAppContextIOProviderImplTest {

    private final Mockery context = new JUnit4Mockery();

    private void assertOsSpecificPathMatch(final String expected, final String actual) {
        final Pattern pattern = Pattern.compile("^(([a-zA-Z]{1}:" + expected + ")||(" + expected + "))$");
        assertTrue("Path '" + actual + "' does not match '" + expected + "'", pattern.matcher(actual).matches());
    }

    @Test
    public void testResolveFileFromUri() throws Exception {

        final ServletContext ctx = context.mock(ServletContext.class, "ctx");

        final WebAppContextIOProviderImpl io = new WebAppContextIOProviderImpl();
        io.setServletContext(ctx);

        context.checking(new Expectations() {{
            allowing(ctx).getRealPath("some/path/to/a/file");
            will(returnValue(io.getOsAwarePath("/realpath")));
        }});

        assertOsSpecificPathMatch(io.getOsAwarePath("/realpath"),
                io.resolveFileFromUri("some/path/to/a/file", null).getAbsolutePath());
        assertOsSpecificPathMatch(io.getOsAwarePath("/realpath"),
                io.resolveFileFromUri("context://some/path/to/a/file", null).getAbsolutePath());

    }

    @Test
    public void testSupports() throws Exception {

        final WebAppContextIOProviderImpl io = new WebAppContextIOProviderImpl();

        assertFalse(io.supports(io.getOsAwarePath("/some/path/to/a/file")));
        assertTrue(io.supports(io.getOsAwarePath("some/path/to/a/file")));
        assertTrue(io.supports(io.getOsAwarePath("context://some/path/to/a/file")));

    }

}
