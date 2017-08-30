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
import org.yes.cart.search.query.impl.InStockProductSearchQueryBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: denispavlov
 * Date: 17/07/2017
 * Time: 20:38
 */
public class InStockProductSearchQueryBuilderTest {

    @Test
    public void testCreateStrictQueryInStock() throws Exception {

        final Query query = new InStockProductSearchQueryBuilder().createStrictQuery(10L, 1010L, "any", "1");
        assertNotNull(query);
        assertEquals("productInStockFlagShopId1:[1010 TO 1010]", query.toString());

    }

    @Test
    public void testCreateStrictQueryNoStock() throws Exception {

        final Query query = new InStockProductSearchQueryBuilder().createStrictQuery(10L, 1010L, "any", "0");
        assertNotNull(query);
        assertEquals("productInStockFlagShopId0:[1010 TO 1010]", query.toString());

    }

    @Test
    public void testCreateRelaxedQueryInStock() throws Exception {

        final Query query = new InStockProductSearchQueryBuilder().createStrictQuery(10L, 1010L, "any", "1");
        assertNotNull(query);
        assertEquals("productInStockFlagShopId1:[1010 TO 1010]", query.toString());

    }

    @Test
    public void testCreateRelaxedQueryNoStock() throws Exception {

        final Query query = new InStockProductSearchQueryBuilder().createStrictQuery(10L, 1010L, "any", "0");
        assertNotNull(query);
        assertEquals("productInStockFlagShopId0:[1010 TO 1010]", query.toString());

    }
}
