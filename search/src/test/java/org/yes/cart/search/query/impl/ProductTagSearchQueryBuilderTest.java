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

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * User: denispavlov
 * Date: 18/11/2014
 * Time: 00:12
 */
public class ProductTagSearchQueryBuilderTest {


    @Test
    public void testCreateStrictQueryNull() throws Exception {

        final Query query = new ProductTagSearchQueryBuilder().createStrictQuery(10L, 1010L, "tag", null);
        assertNull(query);

    }

    @Test
    public void testCreateStrictQueryBlank() throws Exception {

        final Query query = new ProductTagSearchQueryBuilder().createStrictQuery(10L, 1010L, "tag", "  ");
        assertNull(query);

    }

    @Test
    public void testCreateStrictQueryTag() throws Exception {

        final Query query = new ProductTagSearchQueryBuilder().createStrictQuery(10L, 1010L, "tag", "tag1");
        assertNotNull(query);
        assertEquals("(tag:tag1)^20.0", query.toString());

    }

    @Test
    public void testCreateStrictQueryNewArrival() throws Exception {

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        final Query query = new ProductTagSearchQueryBuilder().createStrictQuery(10L, 1010L, "tag", formatter.parse("20141118001700"));
        assertNotNull(query);
        assertEquals("(tag:newarrival)^20.0 createdTimestamp:[" + formatter.parse("20141118000000").getTime() + " TO 9223372036854775807]", query.toString());

    }

    @Test
    public void testCreateRelaxedQueryNull() throws Exception {

        final Query query = new ProductTagSearchQueryBuilder().createRelaxedQuery(10L, 1010L, "tag", null);
        assertNull(query);

    }

    @Test
    public void testCreateRelaxedQueryBlank() throws Exception {

        final Query query = new ProductTagSearchQueryBuilder().createRelaxedQuery(10L, 1010L, "tag", "  ");
        assertNull(query);

    }

    @Test
    public void testCreateRelaxedQueryTag() throws Exception {

        final Query query = new ProductTagSearchQueryBuilder().createRelaxedQuery(10L, 1010L, "tag", "tag1");
        assertNotNull(query);
        assertEquals("(tag:tag1)^20.0", query.toString());


    }

    @Test
    public void testCreateRelaxedQueryNewArrival() throws Exception {

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        final Query query = new ProductTagSearchQueryBuilder().createRelaxedQuery(10L, 1010L, "tag", formatter.parse("20141118001700"));
        assertNotNull(query);
        assertEquals("(tag:newarrival)^20.0 createdTimestamp:[" + formatter.parse("20141118000000").getTime() + " TO 9223372036854775807]", query.toString());

    }



}
