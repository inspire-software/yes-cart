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

package org.yes.cart.web.page.component.filterednavigation.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.misc.navigation.price.PriceTierNode;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.search.PriceNavigation;
import org.yes.cart.search.dto.FilteredNavigationRecord;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: denispavlov
 * Date: 20/03/2018
 * Time: 10:22
 */
public class PriceFilteredNavigationSupportImplTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testGetPriceNavigationRecords() {

        final PriceTierTree priceTierTree = this.context.mock(PriceTierTree.class, "priceTierTree");
        final PriceTierNode priceTierNode1 = this.context.mock(PriceTierNode.class, "priceTierNode1");
        final PriceTierNode priceTierNode2 = this.context.mock(PriceTierNode.class, "priceTierNode2");
        final PriceTierNode priceTierNode3 = this.context.mock(PriceTierNode.class, "priceTierNode3");
        final PriceNavigation priceNavigation = this.context.mock(PriceNavigation.class, "priceNavigation");

        this.context.checking(new Expectations() {{
            allowing(priceTierTree).getPriceTierNodes("EUR");
            will(returnValue(Arrays.asList(priceTierNode1, priceTierNode2, priceTierNode3)));
            allowing(priceTierNode1).getFrom(); will(returnValue(new BigDecimal("0")));
            allowing(priceTierNode1).getTo(); will(returnValue(new BigDecimal("100")));
            allowing(priceNavigation).composePriceRequestParams("EUR", new BigDecimal("0"), new BigDecimal("100"));
            will(returnValue("EUR-_-0-_-100"));
            allowing(priceTierNode2).getFrom(); will(returnValue(new BigDecimal("100")));
            allowing(priceTierNode2).getTo(); will(returnValue(new BigDecimal("300")));
            allowing(priceNavigation).composePriceRequestParams("EUR", new BigDecimal("100"), new BigDecimal("300"));
            will(returnValue("EUR-_-100-_-300"));
            allowing(priceTierNode3).getFrom(); will(returnValue(new BigDecimal("300")));
            allowing(priceTierNode3).getTo(); will(returnValue(new BigDecimal("500")));
            allowing(priceNavigation).composePriceRequestParams("EUR", new BigDecimal("300"), new BigDecimal("500"));
            will(returnValue("EUR-_-300-_-500"));
            allowing(priceTierTree).getPriceTierNodes("UAH");
            will(returnValue(Collections.emptyList()));
        }});

        final PriceFilteredNavigationSupportImpl support =
                new PriceFilteredNavigationSupportImpl(null, null, null, null, priceNavigation);

        List<FilteredNavigationRecord> navigationRecords = support.getPriceNavigationRecords(priceTierTree, "EUR", null);
        assertNotNull(navigationRecords);
        assertEquals(3, navigationRecords.size());
        assertEquals("EUR-_-0-_-100", navigationRecords.get(0).getValue());
        assertEquals("EUR-_-100-_-300", navigationRecords.get(1).getValue());
        assertEquals("EUR-_-300-_-500", navigationRecords.get(2).getValue());
        // In other currency
        navigationRecords = support.getPriceNavigationRecords(priceTierTree, "UAH", null);
        assertNotNull(navigationRecords);
        assertEquals(0, navigationRecords.size()); // we only use explicit navs (no auto exchange)
    }

}