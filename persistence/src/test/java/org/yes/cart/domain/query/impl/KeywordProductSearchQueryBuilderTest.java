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

import org.apache.lucene.search.Query;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * User: denispavlov
 * Date: 16/11/2014
 * Time: 17:51
 */
public class KeywordProductSearchQueryBuilderTest {

    @Test
    public void testCreateStrictQueryNull() throws Exception {

        final Query query = new KeywordProductSearchQueryBuilder().createStrictQuery(10L, "query", null);
        assertNull(query);

    }

    @Test
    public void testCreateStrictQueryBlank() throws Exception {

        final Query query = new KeywordProductSearchQueryBuilder().createStrictQuery(10L, "query", "   ");
        assertNull(query);

    }

    @Test
    public void testCreateStrictQuerySingle() throws Exception {

        final Query query = new KeywordProductSearchQueryBuilder().createStrictQuery(10L, "query", "SearchWord");
        assertNotNull(query);
        assertEquals("(name:SearchWord~0.6^3.0 displayName:SearchWord~0.6^4.0 brand:searchword~0.8^4.0 code:SearchWord~0.8^10.0 sku.code:SearchWord~0.8^10.0 attribute.val:SearchWord~0.65^2.0 sku.attribute.val:SearchWord~0.65^2.0)", query.toString());

    }

    @Test
    public void testCreateStrictQueryMulti() throws Exception {

        final Query query = new KeywordProductSearchQueryBuilder().createStrictQuery(10L, "query", "Search, Word");
        assertNotNull(query);
        assertEquals("(name:Search, Word~0.6^3.0 displayName:Search, Word~0.6^4.0 brand:search, word~0.8^4.0 code:Search, Word~0.8^10.0 sku.code:Search, Word~0.8^10.0 attribute.val:Search, Word~0.65^2.0 sku.attribute.val:Search, Word~0.65^2.0) (name:search~0.6^2.5 displayName:search~0.6^3.0 brand:search~0.8^3.0 code:search~0.8^4.0 sku.code:search~0.8^4.0 attribute.attrvalsearch:search^0.5 sku.attribute.attrvalsearch:search^0.5) (name:word~0.6^2.5 displayName:word~0.6^3.0 brand:word~0.8^3.0 code:word~0.8^4.0 sku.code:word~0.8^4.0 attribute.attrvalsearch:word^0.5 sku.attribute.attrvalsearch:word^0.5)", query.toString());

    }


    @Test
    public void testCreateRelaxedQueryNull() throws Exception {

        final Query query = new KeywordProductSearchQueryBuilder().createRelaxedQuery(10L, "query", null);
        assertNull(query);

    }

    @Test
    public void testCreateRelaxedQueryBlank() throws Exception {

        final Query query = new KeywordProductSearchQueryBuilder().createRelaxedQuery(10L, "query", "   ");
        assertNull(query);

    }


    @Test
    public void testCreateRelaxedQuerySingle() throws Exception {

        final Query query = new KeywordProductSearchQueryBuilder().createRelaxedQuery(10L, "query", "SearchWord");
        assertNotNull(query);
        assertEquals("(name:searchword~0.5^2.5 displayName:searchword~0.5^3.0 brand:searchword~0.7^3.0 code:searchword~0.7^4.0 sku.code:searchword~0.7^4.0 code_stem:searchword~0.75 sku.code_stem:searchword~0.75 attribute.attrvalsearch:searchword^0.5 sku.attribute.attrvalsearch:searchword^0.5 description:searchword^0.8)", query.toString());

    }

    @Test
    public void testCreateRelaxedQueryMulti() throws Exception {

        final Query query = new KeywordProductSearchQueryBuilder().createRelaxedQuery(10L, "query", "Search, Word");
        assertNotNull(query);
        assertEquals("(name:search~0.5^2.5 displayName:search~0.5^3.0 brand:search~0.7^3.0 code:search~0.7^4.0 sku.code:search~0.7^4.0 code_stem:search~0.75 sku.code_stem:search~0.75 attribute.attrvalsearch:search^0.5 sku.attribute.attrvalsearch:search^0.5 description:search^0.8) (name:word~0.5^2.5 displayName:word~0.5^3.0 brand:word~0.7^3.0 code:word~0.7^4.0 sku.code:word~0.7^4.0 code_stem:word~0.75 sku.code_stem:word~0.75 attribute.attrvalsearch:word^0.5 sku.attribute.attrvalsearch:word^0.5 description:word^0.8)", query.toString());

    }
}
