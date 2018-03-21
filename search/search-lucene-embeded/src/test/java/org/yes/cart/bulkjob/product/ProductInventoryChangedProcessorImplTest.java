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

package org.yes.cart.bulkjob.product;

import org.junit.Test;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.SearchQueryFactory;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.search.query.ProductSearchQueryBuilder;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 27/04/2015
 * Time: 15:10
 */
public class ProductInventoryChangedProcessorImplTest extends BaseCoreDBTestCase {


    @Test
    public void testRun() throws Exception {

        final WarehouseService warehouseService = ctx().getBean("warehouseService", WarehouseService.class);
        final ProductService productService = ctx().getBean("productService", ProductService.class);
        final SkuWarehouseService skuWarehouseService = ctx().getBean("skuWarehouseService", SkuWarehouseService.class);
        final SearchQueryFactory searchQueryFactory = ctx().getBean("ftQueryFactory", SearchQueryFactory.class);

        final List<Warehouse> warehouses = warehouseService.getByShopId(10L, false);

        Product product = productService.findById(9998L);
        assertEquals(Product.AVAILABILITY_STANDARD, product.getAvailability());

        final String skuCode = product.getDefaultSku().getCode();
        Pair<BigDecimal, BigDecimal> quantity = skuWarehouseService.findQuantity(warehouses, product.getDefaultSku().getCode());
        assertTrue(quantity.getFirst().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(quantity.getFirst().compareTo(quantity.getSecond()) > 0);

        final BigDecimal oldQuantity = quantity.getFirst();

        productService.reindexProduct(product.getId());

        final NavigationContext context = searchQueryFactory.getFilteredNavigationQueryChain(10L, 10L, null, null,
                false, Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_ID_FIELD, (List) Collections.singletonList("9998")));

        List<ProductSearchResultDTO> rez = productService.getProductSearchResultDTOByQuery(
                context, 0, 1, null, false).getResults();
        assertNotNull(rez);
        assertEquals(1, rez.size());

        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus transactionStatus) {
                // native update to bypass indexing on save!!
                for (final Warehouse warehouse : warehouses) {
                    productService.getGenericDao().executeNativeUpdate("update TSKUWAREHOUSE set QUANTITY = 0"
                            + ", UPDATED_TIMESTAMP = '2099-01-01 00:00:00' where WAREHOUSE_ID = "
                            + warehouse.getWarehouseId() + " and SKU_CODE = '" + skuCode + "'");
                }
            }
        });

        product = productService.findById(9998L);

        quantity = skuWarehouseService.findQuantity(warehouseService.getByShopId(10L, false), product.getDefaultSku().getCode());
        assertTrue(quantity.getFirst().compareTo(BigDecimal.ZERO) == 0);
        assertTrue(quantity.getFirst().compareTo(quantity.getSecond()) <= 0);


        getTx().execute(runInTransactionNow(productService, skuWarehouseService));

        final CacheManager mgr = ctx().getBean("cacheManager", CacheManager.class);

        mgr.getCache("productService-productSearchResultDTOByQuery").clear();
        mgr.getCache("productSkuService-productSkuSearchResultDTOByQuery").clear();

        rez = productService.getProductSearchResultDTOByQuery(context, 0, 1, null, false).getResults();
        assertNotNull(rez);
        assertEquals(0, rez.size());


        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus transactionStatus) {
                // native update to bypass indexing on save!!
                for (final Warehouse warehouse : warehouses) {
                    productService.getGenericDao().executeNativeUpdate("update TSKUWAREHOUSE set QUANTITY = " + oldQuantity.toPlainString()
                            + ", UPDATED_TIMESTAMP = '2099-01-01 00:00:00' where WAREHOUSE_ID = "
                            + warehouse.getWarehouseId() + " and SKU_CODE = '" + skuCode + "'");
                }
            }
        });

        product = productService.findById(9998L);

        quantity = skuWarehouseService.findQuantity(warehouseService.getByShopId(10L, false), product.getDefaultSku().getCode());
        assertTrue(quantity.getFirst().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(quantity.getFirst().compareTo(quantity.getSecond()) > 0);




        getTx().execute(runInTransactionNow(productService, skuWarehouseService));

        mgr.getCache("productService-productSearchResultDTOByQuery").clear();
        mgr.getCache("productSkuService-productSkuSearchResultDTOByQuery").clear();

        rez = productService.getProductSearchResultDTOByQuery(context, 0, 1, null, false).getResults();
        assertNotNull(rez);
        assertEquals(1, rez.size());



    }

    TransactionCallbackWithoutResult runInTransactionNow(final ProductService productService, final SkuWarehouseService skuWarehouseService) {
        return new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus transactionStatus) {
                new ProductInventoryChangedProcessorImpl(skuWarehouseService, productService, null, null, null, null) {
                    @Override
                    protected String getNodeId() {
                        return "TEST";
                    }

                    @Override
                    protected Boolean isLuceneIndexDisabled() {
                        return false;
                    }

                    @Override
                    protected int getBatchSize() {
                        return 100;
                    }

                    @Override
                    protected long getDeltaCheckDelay() {
                        return 100;
                    }

                    @Override
                    protected int getDeltaCheckSize() {
                        return 100;
                    }

                    @Override
                    protected int getChangeMaxSize() {
                        return 1000;
                    }

                    @Override
                    public ProductInventoryChangedProcessorInternal getSelf() {
                        return this;
                    }

                    @Override
                    protected void flushCaches() {

                    }
                }.doRun(Instant.now()); // this should reindex product and it will be removed as there is no inventory
            }
        };
    }


}
