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
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.PriceNavigation;
import org.yes.cart.search.dto.NavigationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 16/11/2014
 * Time: 00:33
 */
public class PriceSearchQueryBuilderTest {

    private final Mockery context = new JUnit4Mockery();


    @Test
    public void testCreateQueryChainNull() throws Exception {

        final PriceNavigation priceNavigation = context.mock(PriceNavigation.class, "priceNavigation");

        final PriceSearchQueryBuilder builder = new PriceSearchQueryBuilder(priceNavigation);

        final List<Query> query = builder.createQueryChain(null, "price", null);
        assertNull(query);

    }

    @Test
    public void testCreateQueryChainBlank() throws Exception {

        final PriceNavigation priceNavigation = context.mock(PriceNavigation.class, "priceNavigation");

        final PriceSearchQueryBuilder builder = new PriceSearchQueryBuilder(priceNavigation);

        final List<Query> query = builder.createQueryChain(null, "price", "   ");
        assertNull(query);

    }

    @Test
    public void testCreateQueryChain() throws Exception {

        final PriceNavigation priceNavigation = context.mock(PriceNavigation.class, "priceNavigation");

        final PriceSearchQueryBuilder builder = new PriceSearchQueryBuilder(priceNavigation);

        final Pair<String, Pair<BigDecimal, BigDecimal>> priceRange =
                new Pair<>("EUR",
                        new Pair<>(
                                new BigDecimal("10"),
                                new BigDecimal("20")));

        final NavigationContext<Query> navigationContext = this.context.mock(NavigationContext.class, "navigationContext");

        context.checking(new Expectations() {{
            oneOf(priceNavigation).decomposePriceRequestParams("EUR-_-10-_-20"); will(returnValue(priceRange));
            oneOf(navigationContext).getCustomerShopId(); will(returnValue(1010L));
        }});

        final List<Query> query = builder.createQueryChain(navigationContext, "price", "EUR-_-10-_-20");
        assertNotNull(query);
        assertEquals(1, query.size());
        assertEquals("facet_price_1010_EUR_range:[1000 TO 1999]", query.get(0).toString());

        this.context.assertIsSatisfied();

    }

}
