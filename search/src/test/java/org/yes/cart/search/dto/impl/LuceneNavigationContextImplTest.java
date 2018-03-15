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

package org.yes.cart.search.dto.impl;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 26/11/2014
 * Time: 00:35
 */
public class LuceneNavigationContextImplTest {


    @Test
    public void testCategoriesCanBeNull() throws Exception {

        assertNull(new LuceneNavigationContextImpl(10L, 10L, "en", null, false, Collections.EMPTY_MAP, null, null).getCategories());

    }

    @Test
    public void testParametersCanBeNull() throws Exception {

        final LuceneNavigationContextImpl ctx = new LuceneNavigationContextImpl(10L, 10L, "en", null, false, null, null, null);

        assertNotNull(ctx.getFilterParametersNames());
        assertTrue(ctx.getFilterParametersNames().isEmpty());

        assertNotNull(ctx.getFilterParameterValues("non-existent"));
        assertTrue(ctx.getFilterParameterValues("non-existent").isEmpty());

        final Map<String, List<String>> copy1 = ctx.getMutableCopyFilterParameters();
        assertNotNull(copy1);
        assertTrue(copy1.isEmpty());

        final Map<String, List<String>> copy2 = ctx.getMutableCopyFilterParameters();
        assertNotNull(copy2);
        assertTrue(copy2.isEmpty());

        assertNotSame(copy1, copy2);

    }


    @Test
    public void testGetMutableCopyFilterParameters() throws Exception {

        final LuceneNavigationContextImpl ctx = new LuceneNavigationContextImpl(10L, 10L, "en", null, false,
                new HashMap() {{
                    put("p1", Arrays.asList("1", "2"));
                    put("p2", Arrays.asList("2", "3"));
                }}, null, null);

        assertNotNull(ctx.getFilterParametersNames());
        assertFalse(ctx.getFilterParametersNames().isEmpty());
        assertTrue(ctx.getFilterParametersNames().contains("p1"));
        assertTrue(ctx.getFilterParametersNames().contains("p2"));
        try {
            ctx.getFilterParametersNames().add("p3");
            fail("Must be immutable");
        } catch (UnsupportedOperationException uoe) {
            // OK
        }
        try {
            ctx.getFilterParametersNames().remove("p1");
            fail("Must be immutable");
        } catch (UnsupportedOperationException uoe) {
            // OK
        }

        assertNotNull(ctx.getFilterParameterValues("p1"));
        assertFalse(ctx.getFilterParameterValues("p1").isEmpty());
        assertEquals("1", ctx.getFilterParameterValues("p1").get(0));
        assertEquals("2", ctx.getFilterParameterValues("p1").get(1));
        assertEquals("2", ctx.getFilterParameterValues("p2").get(0));
        assertEquals("3", ctx.getFilterParameterValues("p2").get(1));
        try {
            ctx.getFilterParameterValues("p1").add("4");
            fail("Must be immutable");
        } catch (UnsupportedOperationException uoe) {
            // OK
        }
        try {
            ctx.getFilterParameterValues("p1").remove("1");
            fail("Must be immutable");
        } catch (UnsupportedOperationException uoe) {
            // OK
        }

        final Map<String, List<String>> copy1 = ctx.getMutableCopyFilterParameters();
        assertNotNull(copy1);
        assertEquals(2, copy1.size());
        assertNotNull(copy1.get("p1"));
        assertEquals("1", copy1.get("p1").get(0));
        assertEquals("2", copy1.get("p1").get(1));
        assertNotNull(copy1.get("p2"));
        assertEquals("2", copy1.get("p2").get(0));
        assertEquals("3", copy1.get("p2").get(1));

        final Map<String, List<String>> copy2 = ctx.getMutableCopyFilterParameters();
        assertNotNull(copy2);
        assertEquals(2, copy2.size());
        assertNotNull(copy2.get("p1"));
        assertEquals("1", copy2.get("p1").get(0));
        assertEquals("2", copy2.get("p1").get(1));
        assertNotNull(copy2.get("p2"));
        assertEquals("2", copy2.get("p2").get(0));
        assertEquals("3", copy2.get("p2").get(1));

        assertNotSame(copy1, copy2);

        // Must be mutable
        copy1.put("p3", Arrays.asList("3", "4"));
        copy1.get("p1").add("3");

    }


    @Test
    public void testEqualsNull() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false, Collections.EMPTY_MAP, null, null);
        assertFalse(ctx1.equals(null));

    }

    @Test
    public void testEqualsByShopId() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false, Collections.EMPTY_MAP, null, null);
        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false, Collections.EMPTY_MAP, null, null);
        assertTrue(ctx1.equals(ctx2));
        assertTrue(ctx2.equals(ctx1));
        assertEquals(ctx1.hashCode(), ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);
    }

    @Test
    public void testNotEqualsByShopId() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false, Collections.EMPTY_MAP, null, null);
        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(11L, 11L, "en", null, false, Collections.EMPTY_MAP, null, null);
        assertFalse(ctx1.equals(ctx2));
        assertFalse(ctx2.equals(ctx1));
        assertFalse(ctx1.hashCode() == ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);
    }


    @Test
    public void testNotEqualsBySubShopId() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false, Collections.EMPTY_MAP, null, null);
        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(10L, 1010L, "en", null, false, Collections.EMPTY_MAP, null, null);
        assertFalse(ctx1.equals(ctx2));
        assertFalse(ctx2.equals(ctx1));
        assertFalse(ctx1.hashCode() == ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);
    }

    @Test
    public void testNotEqualsByInclusion() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false, Collections.EMPTY_MAP, null, null);
        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(10L, 10L, "en", null, true, Collections.EMPTY_MAP, null, null);
        assertFalse(ctx1.equals(ctx2));
        assertFalse(ctx2.equals(ctx1));
        assertFalse(ctx1.hashCode() == ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);
    }

    @Test
    public void testEqualsByCategories1() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", new ArrayList<>(Collections.singletonList(10L)), false, Collections.EMPTY_MAP, null, null);
        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(10L, 10L, "en", new ArrayList<>(Collections.singletonList(10L)), false, Collections.EMPTY_MAP, null, null);
        assertTrue(ctx1.equals(ctx2));
        assertTrue(ctx2.equals(ctx1));
        assertEquals(ctx1.hashCode(), ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);
    }

    @Test
    public void testEqualsByCategories2() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", new ArrayList<>(Arrays.asList(10L, 11L)), false, Collections.EMPTY_MAP, null, null);
        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(10L, 10L, "en", new ArrayList<>(Arrays.asList(10L, 11L)), false, Collections.EMPTY_MAP, null, null);
        assertTrue(ctx1.equals(ctx2));
        assertTrue(ctx2.equals(ctx1));
        assertEquals(ctx1.hashCode(), ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);
    }

    @Test
    public void testNotEqualsByCategories1() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", new ArrayList<>(Collections.singletonList(10L)), false, Collections.EMPTY_MAP, null, null);
        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(10L, 10L, "en", new ArrayList<>(Collections.singletonList(11L)), false, Collections.EMPTY_MAP, null, null);
        assertFalse(ctx1.equals(ctx2));
        assertFalse(ctx2.equals(ctx1));
        assertFalse(ctx1.hashCode() == ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);
    }

    @Test
    public void testNotEqualsByCategories2() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", new ArrayList<>(Collections.singletonList(10L)), false, Collections.EMPTY_MAP, null, null);
        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false, Collections.EMPTY_MAP, null, null);
        assertFalse(ctx1.equals(ctx2));
        assertFalse(ctx2.equals(ctx1));
        assertFalse(ctx1.hashCode() == ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);
    }


    @Test
    public void testNotEqualsByCategories3() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false, Collections.EMPTY_MAP, null, null);
        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(10L, 10L, "en", new ArrayList<>(Collections.singletonList(10L)), false, Collections.EMPTY_MAP, null, null);
        assertFalse(ctx1.equals(ctx2));
        assertFalse(ctx2.equals(ctx1));
        assertFalse(ctx1.hashCode() == ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);
    }

    @Test
    public void testNotEqualsByCategories4() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", new ArrayList<>(Collections.singletonList(10L)), false, Collections.EMPTY_MAP, null, null);
        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(10L, 10L, "en", new ArrayList<>(Arrays.asList(10L, 11L)), false, Collections.EMPTY_MAP, null, null);
        assertFalse(ctx1.equals(ctx2));
        assertFalse(ctx2.equals(ctx1));
        assertFalse(ctx1.hashCode() == ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);
    }

    @Test
    public void testNotEqualsByCategories5() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", new ArrayList<>(Arrays.asList(10L, 11L)), false, Collections.EMPTY_MAP, null, null);
        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(10L, 10L, "en", new ArrayList<>(Collections.singletonList(10L)), false, Collections.EMPTY_MAP, null, null);
        assertFalse(ctx1.equals(ctx2));
        assertFalse(ctx2.equals(ctx1));
        assertFalse(ctx1.hashCode() == ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);
    }


    @Test
    public void testEqualsByParams1() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false, Collections.singletonMap("p1", null), null, null);
        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false, Collections.singletonMap("p1", null), null, null);
        assertTrue(ctx1.equals(ctx2));
        assertTrue(ctx2.equals(ctx1));
        assertEquals(ctx1.hashCode(), ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);

    }

    @Test
    public void testEqualsByParams2() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false,
                Collections.singletonMap("p1", new ArrayList<>(Arrays.asList("1", "2"))), null, null);
        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false,
                Collections.singletonMap("p1", new ArrayList<>(Arrays.asList("1", "2"))), null, null);
        assertTrue(ctx1.equals(ctx2));
        assertTrue(ctx2.equals(ctx1));
        assertEquals(ctx1.hashCode(), ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);

    }

    @Test
    public void testEqualsByParams3() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false,
                new HashMap() {{
                    put("p1", new ArrayList<>(Arrays.asList("1", "2")));
                    put("p2", new ArrayList<>(Arrays.asList("3", "4")));
                }}, null, null);
        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false,
                new HashMap() {{
                    put("p1", new ArrayList<>(Arrays.asList("1", "2")));
                    put("p2", new ArrayList<>(Arrays.asList("3", "4")));
                }}, null, null);
        assertTrue(ctx1.equals(ctx2));
        assertTrue(ctx2.equals(ctx1));
        assertEquals(ctx1.hashCode(), ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);

    }

    @Test
    public void testNotEqualsByParams1() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false,
                Collections.EMPTY_MAP, null, null);
        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false,
                Collections.singletonMap("p1", new ArrayList<>(Arrays.asList("1", "2"))), null, null);
        assertFalse(ctx1.equals(ctx2));
        assertFalse(ctx2.equals(ctx1));
        assertFalse(ctx1.hashCode() == ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);

    }

    @Test
    public void testNotEqualsByParams2() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false,
                Collections.singletonMap("p1", new ArrayList<>(Arrays.asList("1", "2"))), null, null);
        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false,
                Collections.EMPTY_MAP, null, null);
        assertFalse(ctx1.equals(ctx2));
        assertFalse(ctx2.equals(ctx1));
        assertFalse(ctx1.hashCode() == ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);

    }


    @Test
    public void testNotEqualsByParams3() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false,
                new HashMap() {{
                    put("p1", new ArrayList<>(Arrays.asList("1", "2")));
                }}, null, null);
        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false,
                new HashMap() {{
                    put("p1", new ArrayList<>(Arrays.asList("1", "3")));
                }}, null, null);
        assertFalse(ctx1.equals(ctx2));
        assertFalse(ctx2.equals(ctx1));
        assertFalse(ctx1.hashCode() == ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);

    }

    @Test
    public void testEqualsByParams4() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false,
                new HashMap() {{
                    put("p1", new ArrayList<>(Arrays.asList("1", "2")));
                    put("p2", new ArrayList<>(Arrays.asList("4", "3")));
                }}, null, null);
        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false,
                new HashMap() {{
                    put("p1", new ArrayList<>(Arrays.asList("1", "2")));
                    put("p2", new ArrayList<>(Arrays.asList("3", "4")));
                }}, null, null);
        assertFalse(ctx1.equals(ctx2));
        assertFalse(ctx2.equals(ctx1));
        assertFalse(ctx1.hashCode() == ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);

    }

    @Test
    public void testEqualsAfterCopy() throws Exception {

        final LuceneNavigationContextImpl ctx1 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false,
                new HashMap() {{
                    put("p1", new ArrayList<>(Arrays.asList("1", "2")));
                    put("p2", new ArrayList<>(Arrays.asList("4", "3")));
                }}, null, null);

        final LuceneNavigationContextImpl ctx2 = new LuceneNavigationContextImpl(10L, 10L, "en", null, false,
                ctx1.getMutableCopyFilterParameters(), null, null);
        assertTrue(ctx1.equals(ctx2));
        assertTrue(ctx2.equals(ctx1));
        assertEquals(ctx1.hashCode(), ctx2.hashCode());
        assertTrue(ctx1.hashCode() != 0L);

    }
}
