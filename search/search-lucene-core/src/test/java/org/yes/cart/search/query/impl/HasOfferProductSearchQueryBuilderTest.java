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
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.search.dto.NavigationContext;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 01/08/2018
 * Time: 18:40
 */
public class HasOfferProductSearchQueryBuilderTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void createQueryChain() throws Exception {

        final NavigationContext<Query> navigationContext = this.context.mock(NavigationContext.class, "navigationContext");

        this.context.checking(new Expectations() {{
            oneOf(navigationContext).getCustomerShopId(); will(returnValue(1010L));
        }});

        final List<Query> query = new HasOfferProductSearchQueryBuilder().createQueryChain(navigationContext, "any", "EUR");
        assertNotNull(query);
        assertEquals(1, query.size());
        assertEquals("productHasOfferShopIdEUR:[1010 TO 1010]", query.get(0).toString());

        this.context.assertIsSatisfied();


    }

}