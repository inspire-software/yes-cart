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

package org.yes.cart.bulkjob.product;

import org.junit.Test;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.NavigationContext;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.service.domain.ProductService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 13/11/2013
 * Time: 16:29
 */
public class ProductsPassedAvailbilityDateIndexProcessorImplTest extends BaseCoreDBTestCase {

    @Test
    public void testRun() throws Exception {

        final ProductService productService = ctx().getBean("productService", ProductService.class);
        final LuceneQueryFactory luceneQueryFactory = ctx().getBean("luceneQueryFactory", LuceneQueryFactory.class);

        Product product = productService.findById(9998L); // bender available to 2040

        productService.reindexProduct(product.getId());

        final NavigationContext context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_ID_FIELD, (List) Arrays.asList("9998")));

        List<ProductSearchResultDTO> rez = productService.getProductSearchResultDTOByQuery(
                context.getProductQuery(), 0, 1, null, false).getResults();
        assertNotNull(rez);
        assertEquals(1, rez.size());

        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus transactionStatus) {
                // native update to bypass indexing on save!!
                productService.getGenericDao().executeNativeUpdate("update TPRODUCT set AVAILABLETO = '1999-01-01 00:00:00' where PRODUCT_ID = 9998");
            }
        });

        product = productService.findById(9998L); // bender available to 2040

        assertNotNull(product.getAvailableto());
        assertTrue(product.getAvailableto().before(new Date()));


        final ProductsPassedAvailbilityDateIndexProcessorImpl processor = new ProductsPassedAvailbilityDateIndexProcessorImpl(productService);

        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus transactionStatus) {
                processor.run(); // this should reindex it
            }
        });

        final CacheManager mgr = ctx().getBean("cacheManager", CacheManager.class);

        mgr.getCache("productService-productSearchResultDTOByQuery").clear();
        mgr.getCache("productSkuService-productSkuSearchResultDTOByQuery").clear();

        rez = productService.getProductSearchResultDTOByQuery(context.getProductQuery(), 0, 1, null, false).getResults();
        assertNotNull(rez);
        assertEquals(0, rez.size());


    }

}
