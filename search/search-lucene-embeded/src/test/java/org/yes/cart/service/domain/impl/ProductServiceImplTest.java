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

package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.GenericFTSCapableDAO;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSearchResultPageDTO;
import org.yes.cart.search.SearchQueryFactory;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.search.query.ProductSearchQueryBuilder;
import org.yes.cart.service.domain.ProductService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductServiceImplTest extends BaseCoreDBTestCase {

    private ProductService productService;
    private SearchQueryFactory searchQueryFactory;

    @Override
    @Before
    public void setUp() {
        productService = (ProductService) ctx().getBean(ServiceSpringKeys.PRODUCT_SERVICE);
        searchQueryFactory = (SearchQueryFactory) ctx().getBean(ServiceSpringKeys.FT_QUERY_FACTORY);

        super.setUp();
    }


    @Test
    public void testGetProductSearchResultDTOByQuery() {


        // Single category
        getTxReadOnly().execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus status) {

                ((GenericFTSCapableDAO) productService.getGenericDao()).fullTextSearchReindex(false, 1000);

                NavigationContext context = searchQueryFactory.getFilteredNavigationQueryChain(10L, 10L, null, Collections.singletonList(101L), false, null);
                final ProductSearchResultPageDTO searchRes = productService.getProductSearchResultDTOByQuery(
                        context,
                        0,
                        100,
                        ProductSearchQueryBuilder.PRODUCT_NAME_SORT_FIELD,
                        false
                );
                assertEquals("Failed [" + context.toString() + "]", 3, searchRes.getResults().size());
                ProductSearchResultDTO bender = searchRes.getResults().get(0);
                assertEquals("Бендер Згибатель Родригес", bender.getName("ru"));
                assertEquals("Бендер Згинач Родріґес", bender.getName("ua"));
                assertEquals("Robots", bender.getType("xx"));
                assertEquals("Robots", bender.getType("en"));
                assertEquals("Роботы", bender.getType("ru"));
                assertEquals("Роботи", bender.getType("ua"));

            }
        });

        // Category with subs
        getTxReadOnly().execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus status) {

                ((GenericFTSCapableDAO) productService.getGenericDao()).fullTextSearchReindex(false, 1000);

                NavigationContext context = searchQueryFactory.getFilteredNavigationQueryChain(10L, 10L, null, Collections.singletonList(101L), true, null);
                final ProductSearchResultPageDTO searchRes = productService.getProductSearchResultDTOByQuery(
                        context,
                        0,
                        100,
                        ProductSearchQueryBuilder.PRODUCT_NAME_SORT_FIELD,
                        false
                );
                assertTrue("Failed [" + context.toString() + "]", 2 < searchRes.getResults().size());

                final Set<String> names = new HashSet<>();
                for (final ProductSearchResultDTO item : searchRes.getResults()) {
                    names.add(item.getCode());
                }

                assertTrue("CC_TEST7 is from 104 with parent 101", names.contains("CC_TEST7"));

            }
        });


    }

}
