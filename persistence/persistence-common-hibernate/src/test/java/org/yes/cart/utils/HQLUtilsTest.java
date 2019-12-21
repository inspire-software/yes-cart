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

package org.yes.cart.utils;

import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.IntegerType;
import org.junit.Test;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;

import java.time.Instant;
import java.util.*;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 03/10/2017
 * Time: 15:05
 */
public class HQLUtilsTest {

    @Test
    public void testCriteriaIlikeAnywhere() throws Exception {

        assertNull(HQLUtils.criteriaIlikeAnywhere(null));
        assertNull(HQLUtils.criteriaIlikeAnywhere("  "));
        assertEquals("%abc%", HQLUtils.criteriaIlikeAnywhere("aBc"));
        assertEquals("% abc %", HQLUtils.criteriaIlikeAnywhere(" aBc "));

    }

    @Test
    public void testCriteriaLikeAnywhere() throws Exception {

        assertNull(HQLUtils.criteriaLikeAnywhere(null));
        assertNull(HQLUtils.criteriaLikeAnywhere("  "));
        assertEquals("%aBc%", HQLUtils.criteriaLikeAnywhere("aBc"));
        assertEquals("% aBc %", HQLUtils.criteriaLikeAnywhere(" aBc "));

    }

    @Test
    public void testCriteriaIeq() throws Exception {

        assertNull(HQLUtils.criteriaIeq(null));
        assertNull(HQLUtils.criteriaIeq("  "));
        assertEquals("abc", HQLUtils.criteriaIeq("aBc"));
        assertEquals(" abc ", HQLUtils.criteriaIeq(" aBc "));

    }

    @Test
    public void testCriteriaEq() throws Exception {

        assertNull(HQLUtils.criteriaEq(null));
        assertNull(HQLUtils.criteriaEq("  "));
        assertEquals("aBc", HQLUtils.criteriaEq("aBc"));
        assertEquals(" aBc ", HQLUtils.criteriaEq(" aBc "));

    }

    @Test
    public void testCriteriaInTest() throws Exception {

        Object test = HQLUtils.criteriaInTest(null);
        assertTrue(test instanceof TypedParameterValue);
        assertTrue(((TypedParameterValue) test).getType() instanceof IntegerType);
        assertEquals(Integer.valueOf(0), ((TypedParameterValue) test).getValue());

        test = HQLUtils.criteriaInTest(Collections.emptyList());
        assertTrue(test instanceof TypedParameterValue);
        assertTrue(((TypedParameterValue) test).getType() instanceof IntegerType);
        assertEquals(Integer.valueOf(0), ((TypedParameterValue) test).getValue());

        test = HQLUtils.criteriaInTest(Collections.singletonList("aBc"));
        assertTrue(test instanceof TypedParameterValue);
        assertTrue(((TypedParameterValue) test).getType() instanceof IntegerType);
        assertEquals(Integer.valueOf(1), ((TypedParameterValue) test).getValue());

    }

    @Test
    public void testCriteriaIn() throws Exception {

        assertNotNull(HQLUtils.criteriaIn(null));
        assertArrayEquals(new String[] { "x" }, HQLUtils.criteriaIn(null).toArray());
        assertNotNull(HQLUtils.criteriaIn(Collections.emptyList()));
        assertArrayEquals(new String[] { "x" }, HQLUtils.criteriaIn(Collections.emptyList()).toArray());
        final Collection coll = Collections.singletonList("aBc");
        assertSame(coll, HQLUtils.criteriaIn(coll));

    }

    @Test
    public void testAppendFilterCriteria() throws Exception {

        final Map<String, List> filter = new LinkedHashMap<>();

        assertWithFilter(null, "select from XEntity e ");

        filter.put("prop1", Collections.singletonList("Val1"));
        filter.put("prop2", null);
        filter.put("prop3", Collections.singletonList(""));

        assertWithFilter(filter,
                "select from XEntity e  where (\n ( lower(e.prop1)  like ?1) \n)",
                "%val1%");

        filter.clear();

        filter.put("prop1", Collections.singletonList("Val1"));
        filter.put("prop2", Collections.singletonList(SearchContext.MatchMode.ANY.toParam(Collections.singleton("val1"))));

        assertWithFilter(filter,
                "select from XEntity e  where (\n ( lower(e.prop1)  like ?1) \n and  ( e.prop2  in ?2) \n)",
                "%val1%", Collections.singleton("val1"));

        filter.clear();

        SearchContext.JoinMode.OR.setMode(filter);
        filter.put("prop1", Collections.singletonList("Val1"));
        filter.put("prop2", Collections.singletonList(SearchContext.MatchMode.ANY.toParam(Collections.singleton("val1"))));

        assertWithFilter(filter,
                "select from XEntity e  where (\n ( lower(e.prop1)  like ?1) \n or  ( e.prop2  in ?2) \n)",
                "%val1%", Collections.singleton("val1"));

        filter.clear();

        final Instant to = Instant.now();
        final Instant from = to.minusMillis(100L);
        filter.put("prop1", Arrays.asList(SearchContext.MatchMode.LE.toParam(from), SearchContext.MatchMode.GT.toParam(to)));

        assertWithFilter(filter,
                "select from XEntity e  where (\n ( e.prop1  <= ?1) \n and  ( e.prop1  > ?2) \n)",
                from, to);

        filter.clear();

        filter.put("prop1", Collections.singletonList("Val1"));
        filter.put("prop2", Collections.singletonList(SearchContext.MatchMode.ANY.toParam(Collections.singleton("val1"))));
        filter.put("prop3", Arrays.asList(SearchContext.MatchMode.LE.toParam(from), SearchContext.MatchMode.GT.toParam(to)));

        assertWithFilter(filter,
                "select from XEntity e  where (\n ( lower(e.prop1)  like ?1) \n and  ( e.prop2  in ?2) \n and  ( e.prop3  <= ?3) \n and  ( e.prop3  > ?4) \n)",
                "%val1%", Collections.singleton("val1"), from, to);

        filter.clear();

        filter.put("prop1", Collections.singletonList(SearchContext.MatchMode.EQ.toParam("Val1")));

        assertWithFilter(filter,
                "select from XEntity e  where (\n ( lower(e.prop1)  = ?1) \n)",
                "val1");

        filter.clear();

        filter.put("prop1", Collections.singletonList(SearchContext.MatchMode.NOTNULL.toParam(null)));
        filter.put("prop2", Collections.singletonList(SearchContext.MatchMode.NULL.toParam(null)));

        assertWithFilter(filter,
                "select from XEntity e  where (\n ( e.prop1  is not null ) \n and  ( e.prop2  is null ) \n)");

        filter.clear();

        filter.put("prop1", Collections.singletonList(SearchContext.MatchMode.NOTEMPTY.toParam(null)));
        filter.put("prop2", Collections.singletonList(SearchContext.MatchMode.EMPTY.toParam(null)));

        assertWithFilter(filter,
                "select from XEntity e  where (\n ( e.prop1  != '' ) \n and  ( e.prop2  = '' ) \n)");


    }

    private void assertWithFilter(final Map<String, List> filter,
                                  final String expected,
                                  final Object ... expectedParams) {

        final StringBuilder criteria = new StringBuilder("select from XEntity e ");
        final List<Object> params = new ArrayList<>();

        HQLUtils.appendFilterCriteria(criteria, params, "e", filter);

        assertEquals(expected, criteria.toString());

        final int expectedParamCount = expectedParams != null ? expectedParams.length : 0;

        for (int i = 0; i < expectedParamCount; i++) {

            assertTrue("Unexpected param at index " + i, params.size() - 1 >= i);
            assertEquals("Parameters does not match at index " + i, params.get(i), expectedParams[i]);

        }

        assertEquals(expectedParamCount, params.size());


    }
}