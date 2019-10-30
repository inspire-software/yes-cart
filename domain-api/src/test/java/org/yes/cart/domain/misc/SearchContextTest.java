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

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 28/10/2019
 * Time: 12:10
 */
public class SearchContextTest {

    @Test
    public void collectSingleValueParameters() throws Exception {

        Map<String, Object> collected;

        collected = new SearchContext(null, 0, 0, null, false).collectSingleValueParameters();
        assertNotNull(collected);
        assertTrue(collected.isEmpty());

        collected = new SearchContext(Collections.singletonMap("abc", Collections.singletonList("def")), 0, 0, null, false).collectSingleValueParameters();
        assertNotNull(collected);
        assertTrue(collected.isEmpty());

        collected = new SearchContext(Collections.singletonMap("abc", Collections.singletonList("def")), 0, 0, null, false).collectSingleValueParameters("foo");
        assertNotNull(collected);
        assertTrue(collected.isEmpty());

        collected = new SearchContext(Collections.singletonMap("abc", Collections.singletonList("def")), 0, 0, null, false).collectSingleValueParameters("foo", "abc");
        assertNotNull(collected);
        assertFalse(collected.isEmpty());
        assertEquals("def", collected.get("abc"));




    }

}