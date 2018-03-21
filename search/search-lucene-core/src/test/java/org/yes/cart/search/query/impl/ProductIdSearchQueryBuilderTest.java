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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 16/11/2014
 * Time: 19:03
 */
public class ProductIdSearchQueryBuilderTest {

    @Test
    public void testCreateStrictQueryNull() throws Exception {

        final List<Query> query = new ProductIdSearchQueryBuilder().createQueryChain(null, "productId", null);
        assertNull(query);

    }

    @Test
    public void testCreateStrictQueryBlank() throws Exception {

        final List<Query> query = new ProductIdSearchQueryBuilder().createQueryChain(null, "productId", "  ");
        assertNull(query);

    }

    @Test
    public void testCreateStrictQuerySingle() throws Exception {

        final List<Query> query = new ProductIdSearchQueryBuilder().createQueryChain(null, "productId", "1");
        assertNotNull(query);
        assertEquals(1, query.size());
        assertEquals("(productId:1)^1.0", query.get(0).toString());

    }

    @Test
    public void testCreateStrictQueryMultiCollection() throws Exception {

        final List<Query> query = new ProductIdSearchQueryBuilder().createQueryChain(null, "productId", Arrays.asList("1", "2", "3"));
        assertNotNull(query);
        assertEquals(1, query.size());
        assertEquals("(productId:1)^1.0 (productId:2)^1.0 (productId:3)^1.0", query.get(0).toString());

    }

    @Test
    public void testCreateStrictQueryMultiCollectionWithEmpty() throws Exception {

        final List<Query> query = new ProductIdSearchQueryBuilder().createQueryChain(null, "productId", Arrays.asList("1", "", "3"));
        assertNotNull(query);
        assertEquals(1, query.size());
        assertEquals("(productId:1)^1.0 (productId:3)^1.0", query.get(0).toString());

    }

    @Test
    public void testCreateStrictQueryMultiCollectionEmpty() throws Exception {

        final List<Query> query = new ProductIdSearchQueryBuilder().createQueryChain(null, "productId", Collections.emptyList());
        assertNull(query);

    }

}
