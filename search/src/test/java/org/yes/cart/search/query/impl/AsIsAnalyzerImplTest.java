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

import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.Query;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class AsIsAnalyzerImplTest {

    private static final String LUCENE_QUERY = "+productCategory.category:104 +attribute.attribute:BATTERY_TYPE +attribute.val:Litium";
    private static final String LUCENE_QUERY_LOWERCASE = "+productCategory.category:104 +attribute.attribute:battery_type +attribute.val:litium";
    private static final String[] FIELDS = {"productCategory.category", "attribute.attribute", "attribute.val"};

    @Test
    public void testThatQueryIsNotTransformedByAnalyzerWithMultipleFieldsQueryParser() throws Exception {
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(FIELDS, new AsIsAnalyzer(false));
        Query query = queryParser.parse(LUCENE_QUERY);
        assertEquals(LUCENE_QUERY, query.toString());

        queryParser = new MultiFieldQueryParser(FIELDS, new AsIsAnalyzer(true));
        query = queryParser.parse(LUCENE_QUERY);
        assertEquals(LUCENE_QUERY_LOWERCASE, query.toString());

    }
}
