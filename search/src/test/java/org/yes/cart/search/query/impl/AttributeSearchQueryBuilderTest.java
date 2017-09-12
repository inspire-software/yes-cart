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

package org.yes.cart.search.query.impl;

import org.apache.lucene.search.Query;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 15/11/2014
 * Time: 23:51
 */
public class AttributeSearchQueryBuilderTest {

    @Test
    public void testCreateQueryChainNull() throws Exception {

        final List<Query> query = new AttributeSearchQueryBuilder().createQueryChain(null, "size", null);
        assertNull(query);

    }

    @Test
    public void testCreateQueryChainBlank() throws Exception {

        final List<Query> query = new AttributeSearchQueryBuilder().createQueryChain(null, "size", "   ");
        assertNull(query);

    }

    @Test
    public void testCreateQueryChainSingle() throws Exception {

        final List<Query> query = new AttributeSearchQueryBuilder().createQueryChain(null, "size", "30");
        assertNotNull(query);
        assertEquals(1, query.size());
        assertEquals("(size:30)^3.5", query.get(0).toString());

    }

    @Test
    public void testCreateQueryChainRange() throws Exception {

        final List<Query> query = new AttributeSearchQueryBuilder().createQueryChain(null, "size", "30-_-50");
        assertNotNull(query);
        assertEquals(1, query.size());
        assertEquals("(size_range:[30 TO 49])^3.5", query.get(0).toString());

    }

    @Test
    public void testCreateQueryChainRangeFrom() throws Exception {

        final List<Query> query = new AttributeSearchQueryBuilder().createQueryChain(null, "size", "30-_-");
        assertNotNull(query);
        assertEquals(1, query.size());
        assertEquals("(size_range:[30 TO 9223372036854775807])^3.5", query.get(0).toString());

    }

    @Test
    public void testCreateQueryChainRangeTo() throws Exception {

        final List<Query> query = new AttributeSearchQueryBuilder().createQueryChain(null, "size", "-_-50");
        assertNotNull(query);
        assertEquals(1, query.size());
        assertEquals("(size_range:[-9223372036854775808 TO 49])^3.5", query.get(0).toString());

    }

}
