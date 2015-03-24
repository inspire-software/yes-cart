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

package org.yes.cart.domain.query.impl;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class SearchPhrazeUtilTest {

    @Test
    public void testSplit() {

        List<String> rez = SearchPhrazeUtil.splitForSearch(" | just, phraze--to split;for.test,,hehe++wow");
        assertEquals(8, rez.size());
        assertEquals("just", rez.get(0));
        assertEquals("phraze", rez.get(1));
        assertEquals("to", rez.get(2));
        assertEquals("split", rez.get(3));
        assertEquals("for", rez.get(4));
        assertEquals("test", rez.get(5));
        assertEquals("hehe", rez.get(6));
        assertEquals("wow", rez.get(7));

    }

    @Test
    public void testSplitNullValue() {

        List<String> rez = SearchPhrazeUtil.splitForSearch(null);
        assertEquals(0, rez.size());

    }

    @Test
    public void testSplitEmptyValue() {

        List<String> rez = SearchPhrazeUtil.splitForSearch("");
        assertEquals(0, rez.size());
        rez = SearchPhrazeUtil.splitForSearch("  ,,++||-- + - ");
        assertEquals(0, rez.size());

    }
}
