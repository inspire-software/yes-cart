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

package org.yes.cart.domain.misc;

import org.junit.Test;

import java.time.Instant;
import java.util.*;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 28/10/2019
 * Time: 12:10
 */
public class SearchContextTest {

    @Test
    public void testGetParameters() throws Exception {

        Map<String, List> collected;

        collected = new SearchContext(null, 0, 0, null, false).getParameters();
        assertNotNull(collected);
        assertTrue(collected.isEmpty());

        collected = new SearchContext(Collections.singletonMap("abc", Collections.singletonList("def")), 0, 0, null, false).getParameters();
        assertNotNull(collected);
        assertTrue(collected.isEmpty());

        collected = new SearchContext(Collections.singletonMap("abc", Collections.singletonList("def")), 0, 0, null, false, "foo").getParameters();
        assertNotNull(collected);
        assertTrue(collected.isEmpty());

        collected = new SearchContext(Collections.singletonMap("abc", Collections.singletonList("def")), 0, 0, null, false, "foo", "abc").getParameters();
        assertNotNull(collected);
        assertFalse(collected.isEmpty());
        assertEquals(Collections.singletonList("def"), collected.get("abc"));

        collected = new SearchContext(Collections.singletonMap("abc", Arrays.asList("def", null, "")), 0, 0, null, false, "foo", "abc").getParameters();
        assertNotNull(collected);
        assertFalse(collected.isEmpty());
        assertEquals(Collections.singletonList("def"), collected.get("abc"));

        final Instant now = Instant.now();
        collected = new SearchContext(Collections.singletonMap("abc", Collections.singletonList(now)), 0, 0, null, false, "foo", "abc").getParameters();
        assertNotNull(collected);
        assertFalse(collected.isEmpty());
        assertEquals(Collections.singletonList(now), collected.get("abc"));

        final Pair<SearchContext.MatchMode, Object> from = SearchContext.MatchMode.GT.toParam(now);
        final Pair<SearchContext.MatchMode, Object> to = SearchContext.MatchMode.LT.toParam(now);

        collected = new SearchContext(Collections.singletonMap("abc", Arrays.asList(from, to)), 0, 0, null, false, "foo", "abc").getParameters();
        assertNotNull(collected);
        assertFalse(collected.isEmpty());
        assertEquals(Arrays.asList(from, to), collected.get("abc"));

    }

    @Test
    public void testReduceParameters() throws Exception {

        Map<String, List> collected;

        collected = new SearchContext(null, 0, 0, null, false).reduceParameters();
        assertNotNull(collected);
        assertTrue(collected.isEmpty());

        collected = new SearchContext(Collections.singletonMap("abc", Collections.singletonList("def")), 0, 0, null, false).reduceParameters("foo");
        assertNotNull(collected);
        assertTrue(collected.isEmpty());

        collected = new SearchContext(Collections.singletonMap("abc", Collections.singletonList("def")), 0, 0, null, false, "foo").reduceParameters("foo");
        assertNotNull(collected);
        assertTrue(collected.isEmpty());

        collected = new SearchContext(Collections.singletonMap("abc", Collections.singletonList("def")), 0, 0, null, false, "foo", "abc").reduceParameters("foo", "abc");
        assertNotNull(collected);
        assertFalse(collected.isEmpty());
        assertEquals(Collections.singletonList("def"), collected.get("abc"));

        collected = new SearchContext(Collections.singletonMap("abc", Arrays.asList("def", null, "")), 0, 0, null, false, "foo", "abc").reduceParameters("foo", "abc");
        assertNotNull(collected);
        assertFalse(collected.isEmpty());
        assertEquals(Collections.singletonList("def"), collected.get("abc"));

        final Instant now = Instant.now();
        collected = new SearchContext(Collections.singletonMap("abc", Collections.singletonList(now)), 0, 0, null, false, "foo", "abc").reduceParameters("foo", "abc");
        assertNotNull(collected);
        assertFalse(collected.isEmpty());
        assertEquals(Collections.singletonList(now), collected.get("abc"));

        final Pair<SearchContext.MatchMode, Object> from = SearchContext.MatchMode.GT.toParam(now);
        final Pair<SearchContext.MatchMode, Object> to = SearchContext.MatchMode.LT.toParam(now);

        collected = new SearchContext(Collections.singletonMap("abc", Arrays.asList(from, to)), 0, 0, null, false, "foo", "abc").reduceParameters("foo", "abc");
        assertNotNull(collected);
        assertFalse(collected.isEmpty());
        assertEquals(Arrays.asList(from, to), collected.get("abc"));


        final Map<String, List> params = new HashMap<>();
        params.put("abc", Collections.singletonList("def"));
        params.put("foo", Collections.singletonList("bar"));

        collected = new SearchContext(params, 0, 0, null, false, "abc", "foo").reduceParameters("foo");
        assertNotNull(collected);
        assertEquals(1, collected.size());
        assertEquals(Collections.singletonList("bar"), collected.get("foo"));

        collected = new SearchContext(params, 0, 0, null, false, "abc", "foo").reduceParameters("abc");
        assertNotNull(collected);
        assertEquals(1, collected.size());
        assertEquals(Collections.singletonList("def"), collected.get("abc"));

        collected = new SearchContext(params, 0, 0, null, false, "abc", "foo").reduceParameters("abc", "foo");
        assertNotNull(collected);
        assertEquals(2, collected.size());
        assertEquals(Collections.singletonList("bar"), collected.get("foo"));
        assertEquals(Collections.singletonList("def"), collected.get("abc"));


    }

    @Test
    public void testExpandParameters() throws Exception {

        Map<String, List> collected;
        final SearchContext ctx = new SearchContext(Collections.singletonMap("abc", Collections.singletonList("def")), 0, 0, null, false, "abc");

        collected = ctx.expandParameter("abc", "abc1", "abc2", "abc3");

        assertNotNull(collected);
        assertFalse(collected.isEmpty());
        assertEquals(4, collected.size());
        assertEquals(Collections.singletonList(SearchContext.JoinMode.OR), collected.get(SearchContext.JOIN));
        assertEquals(Collections.singletonList("def"), collected.get("abc1"));
        assertEquals(Collections.singletonList("def"), collected.get("abc2"));
        assertEquals(Collections.singletonList("def"), collected.get("abc3"));

    }
}