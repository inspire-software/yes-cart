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
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.PriceNavigation;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * User: denispavlov
 * Date: 16/11/2014
 * Time: 00:33
 */
public class PriceSearchQueryBuilderTest {

    private final Mockery context = new JUnit4Mockery();


    @Test
    public void testCreateStrictQueryNull() throws Exception {

        final PriceNavigation priceNavigation = context.mock(PriceNavigation.class, "priceNavigation");

        final PriceSearchQueryBuilder buider = new PriceSearchQueryBuilder(priceNavigation);

        final Query query = buider.createStrictQuery(10L, "price", null);
        assertNull(query);

    }

    @Test
    public void testCreateStrictQueryBlank() throws Exception {

        final PriceNavigation priceNavigation = context.mock(PriceNavigation.class, "priceNavigation");

        final PriceSearchQueryBuilder buider = new PriceSearchQueryBuilder(priceNavigation);

        final Query query = buider.createStrictQuery(10L, "price", "   ");
        assertNull(query);

    }

    @Test
    public void testCreateStrictQuery() throws Exception {

        final PriceNavigation priceNavigation = context.mock(PriceNavigation.class, "priceNavigation");

        final PriceSearchQueryBuilder buider = new PriceSearchQueryBuilder(priceNavigation);

        final Pair<String, Pair<BigDecimal, BigDecimal>> priceRange =
                new Pair<String, Pair<BigDecimal, BigDecimal>>("EUR",
                        new Pair<BigDecimal, BigDecimal>(
                                new BigDecimal("10"),
                                new BigDecimal("20")));

        context.checking(new Expectations() {{
            one(priceNavigation).decomposePriceRequestParams("EUR-_-10-_-20"); will(returnValue(priceRange));
        }});

        final Query query = buider.createStrictQuery(10L, "price", "EUR-_-10-_-20");
        assertNotNull(query);
        assertEquals("sku.skuPrice:[00000010_EUR_00001000 TO 00000010_EUR_00002000}", query.toString());

    }


    @Test
    public void testCreateRelaxedQueryNull() throws Exception {

        final PriceNavigation priceNavigation = context.mock(PriceNavigation.class, "priceNavigation");

        final PriceSearchQueryBuilder buider = new PriceSearchQueryBuilder(priceNavigation);

        final Query query = buider.createRelaxedQuery(10L, "price", null);
        assertNull(query);

    }

    @Test
    public void testCreateRelaxedQueryBlank() throws Exception {

        final PriceNavigation priceNavigation = context.mock(PriceNavigation.class, "priceNavigation");

        final PriceSearchQueryBuilder buider = new PriceSearchQueryBuilder(priceNavigation);

        final Query query = buider.createRelaxedQuery(10L, "price", "   ");
        assertNull(query);

    }

    @Test
    public void testCreateRelaxedQuery() throws Exception {

        final PriceNavigation priceNavigation = context.mock(PriceNavigation.class, "priceNavigation");

        final PriceSearchQueryBuilder buider = new PriceSearchQueryBuilder(priceNavigation);

        final Pair<String, Pair<BigDecimal, BigDecimal>> priceRange =
                new Pair<String, Pair<BigDecimal, BigDecimal>>("EUR",
                        new Pair<BigDecimal, BigDecimal>(
                                new BigDecimal("10"),
                                new BigDecimal("20")));

        context.checking(new Expectations() {{
            one(priceNavigation).decomposePriceRequestParams("EUR-_-10-_-20"); will(returnValue(priceRange));
        }});

        final Query query = buider.createRelaxedQuery(10L, "price", "EUR-_-10-_-20");
        assertNotNull(query);
        assertEquals("sku.skuPrice:[00000010_EUR_00001000 TO 00000010_EUR_00002000}", query.toString());

    }
}
