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
        assertEquals("((name:SearchWord~1)^2.5 (displayName:SearchWord~2)^4.0 (brand:searchword)^5.0 (categoryName:SearchWord~2)^4.5 (code:SearchWord)^10.0 (manufacturerCode:SearchWord)^10.0 (sku.code:SearchWord)^10.0 (sku.manufacturerCode:SearchWord)^10.0 (attribute.attrvalsearchprimary:searchword)^15.0 (attribute.attrvalsearchphrase:searchword)^3.5)", query.toString());

    }

    @Test
    public void testCreateStrictQueryMulti() throws Exception {

        final Query query = new KeywordProductSearchQueryBuilder().createStrictQuery(10L, "query", "Search, Word");
        assertNotNull(query);
        assertEquals("((name:Search, Word~1)^2.5 (displayName:Search, Word~2)^4.0 (brand:search, word)^5.0 (categoryName:Search, Word~2)^4.5 (code:Search, Word)^10.0 (manufacturerCode:Search, Word)^10.0 (sku.code:Search, Word)^10.0 (sku.manufacturerCode:Search, Word)^10.0 (attribute.attrvalsearchprimary:search, word)^15.0 (attribute.attrvalsearchphrase:search, word)^3.5) ((brand:search)^5.0 (categoryName_stem:search)^4.5 (code:search)^4.0 (manufacturerCode:search)^4.0 (sku.code:search)^4.0 (sku.manufacturerCode:search)^4.0 (attribute.attrvalsearchphrase:search)^2.75) ((brand:word)^5.0 (categoryName_stem:word)^4.5 (code:word)^4.0 (manufacturerCode:word)^4.0 (sku.code:word)^4.0 (sku.manufacturerCode:word)^4.0 (attribute.attrvalsearchphrase:word)^2.75)", query.toString());

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
        assertEquals("((name:searchword~2)^2.5 (displayName:searchword~2)^3.0 (displayName_stem:searchword~2)^2.5 (brand:searchword~2)^3.5 (categoryName:searchword~2)^3.5 (categoryName_stem:searchword~2)^2.0 (code:searchword~2)^4.0 (manufacturerCode:searchword~2)^4.0 (sku.code:searchword~2)^4.0 (sku.manufacturerCode:searchword~2)^4.0 (code_stem:searchword~2)^1.0 (manufacturerCode_stem:searchword~2)^1.0 (sku.code_stem:searchword~2)^1.0 (sku.manufacturerCode_stem:searchword~2)^1.0 (attribute.attrvalsearch:searchword~2)^2.75 (description_stem:searchword)^0.5)", query.toString());

    }

    @Test
    public void testCreateRelaxedQueryMulti() throws Exception {

        final Query query = new KeywordProductSearchQueryBuilder().createRelaxedQuery(10L, "query", "Search, Word");
        assertNotNull(query);
        assertEquals("((name:search~2)^2.5 (displayName:search~2)^3.0 (displayName_stem:search~2)^2.5 (brand:search~2)^3.5 (categoryName:search~2)^3.5 (categoryName_stem:search~2)^2.0 (code:search~2)^4.0 (manufacturerCode:search~2)^4.0 (sku.code:search~2)^4.0 (sku.manufacturerCode:search~2)^4.0 (code_stem:search~2)^1.0 (manufacturerCode_stem:search~2)^1.0 (sku.code_stem:search~2)^1.0 (sku.manufacturerCode_stem:search~2)^1.0 (attribute.attrvalsearch:search~2)^2.75 (description_stem:search)^0.5) ((name:word~2)^2.5 (displayName:word~2)^3.0 (displayName_stem:word~2)^2.5 (brand:word~2)^3.5 (categoryName:word~2)^3.5 (categoryName_stem:word~2)^2.0 (code:word~2)^4.0 (manufacturerCode:word~2)^4.0 (sku.code:word~2)^4.0 (sku.manufacturerCode:word~2)^4.0 (code_stem:word~2)^1.0 (manufacturerCode_stem:word~2)^1.0 (sku.code_stem:word~2)^1.0 (sku.manufacturerCode_stem:word~2)^1.0 (attribute.attrvalsearch:word~2)^2.75 (description_stem:word)^0.5)", query.toString());

    }
}
