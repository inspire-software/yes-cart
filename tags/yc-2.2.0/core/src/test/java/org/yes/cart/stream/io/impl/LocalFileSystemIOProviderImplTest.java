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

import org.junit.Test;

import java.io.File;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: denispavlov
 * Date: 30/08/2014
 * Time: 20:00
 */
public class LocalFileSystemIOProviderImplTest {


    private void assertOsSpecificPathMatch(final String expected, final String actual) {
        final Pattern pattern = Pattern.compile("^(([a-zA-Z]{1}:" + expected.replace("\\", "\\\\") + ")||(" + expected.replace("\\", "\\\\") + "))$");
        assertTrue("Path '" + actual + "' does not match '" + expected + "'", pattern.matcher(actual).matches());
    }

    private void assertOsSpecificPathNoMatch(final String expected, final String actual) {
        final Pattern pattern = Pattern.compile("^(([a-zA-Z]{1}:" + expected.replace("\\", "\\\\") + ")||(" + expected.replace("\\", "\\\\") + "))$");
        assertFalse("Path '" + actual + "' does not match '" + expected + "'", pattern.matcher(actual).matches());
    }

    @Test
    public void testResolveFileFromUri() throws Exception {

        final LocalFileSystemIOProviderImpl io = new LocalFileSystemIOProviderImpl();

        assertOsSpecificPathMatch(io.getOsAwarePath("/some/path/to/a/file"),
                io.resolveFileFromUri("/some/path/to/a/file", null).getAbsolutePath());
        assertOsSpecificPathMatch(io.getOsAwarePath("/some/path/to/a/file"),
                io.resolveFileFromUri("file:///some/path/to/a/file", null).getAbsolutePath());
        assertOsSpecificPathMatch(io.getOsAwarePath("/some/path/to/a/file"),
                io.resolveFileFromUri("file:" + File.separator + File.separator + "/some/path/to/a/file", null).getAbsolutePath());

        final String current = io.resolveFileFromUri("file://some/path/to/a/file", null).getAbsolutePath();
        assertOsSpecificPathNoMatch(io.getOsAwarePath("/some/path/to/a/file"), current);
        assertTrue(current.endsWith(io.getOsAwarePath("/some/path/to/a/file")));

    }

    @Test
    public void testSupports() throws Exception {

        final LocalFileSystemIOProviderImpl io = new LocalFileSystemIOProviderImpl();

        assertFalse(io.supports(io.getOsAwarePath("some/path/to/a/file")));
        assertTrue(io.supports(io.getOsAwarePath("/some/path/to/a/file")));
        assertTrue(io.supports(io.getOsAwarePath("file:///some/path/to/a/file")));
        assertTrue(io.supports("file://" + io.getOsAwarePath("/some/path/to/a/file")));

    }
}
