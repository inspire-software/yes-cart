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
 * Time: 19:56
 */
public class ProductCategorySearchQueryBuilderTest {

    @Test
    public void testCreateQueryChainNull() throws Exception {

        final List<Query> query = new ProductCategorySearchQueryBuilder().createQueryChain(null, "productCategory.category", null);
        assertNull(query);

    }

    @Test
    public void testCreateQueryChainBlank() throws Exception {

        final List<Query> query = new ProductCategorySearchQueryBuilder().createQueryChain(null, "productCategory.category", "  ");
        assertNull(query);

    }

    @Test
    public void testCreateQueryChainSingle() throws Exception {

        final List<Query> query = new ProductCategorySearchQueryBuilder().createQueryChain(null, "productCategory.category", "1");
        assertNotNull(query);
        assertEquals(1, query.size());
        assertEquals("(productCategory.category:[1 TO 1])^1.0", query.get(0).toString());

    }

    @Test
    public void testCreateQueryChainMultiCollection() throws Exception {

        final List<Query> query = new ProductCategorySearchQueryBuilder().createQueryChain(null, "productCategory.category", Arrays.asList("1", "2", "3"));
        assertNotNull(query);
        assertEquals(1, query.size());
        assertEquals("(productCategory.category:[1 TO 1])^1.0 (productCategory.category:[2 TO 2])^1.0 (productCategory.category:[3 TO 3])^1.0", query.get(0).toString());

    }

    @Test
    public void testCreateQueryChainMultiCollectionWithEmpty() throws Exception {

        final List<Query> query = new ProductCategorySearchQueryBuilder().createQueryChain(null, "productCategory.category", Arrays.asList("1", "", "3"));
        assertNotNull(query);
        assertEquals(1, query.size());
        assertEquals("(productCategory.category:[1 TO 1])^1.0 (productCategory.category:[0 TO 0])^1.0 (productCategory.category:[3 TO 3])^1.0", query.get(0).toString());

    }

    @Test
    public void testCreateQueryChainMultiCollectionEmpty() throws Exception {

        final List<Query> query = new ProductCategorySearchQueryBuilder().createQueryChain(null, "productCategory.category", Collections.emptyList());
        assertNull(query);

    }
}
