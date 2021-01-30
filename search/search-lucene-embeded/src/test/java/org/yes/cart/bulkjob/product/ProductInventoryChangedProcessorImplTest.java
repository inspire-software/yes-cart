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

package org.yes.cart.bulkjob.product;

import org.junit.Test;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.bulkjob.cron.CronJobProcessor;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.search.SearchQueryFactory;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.search.query.ProductSearchQueryBuilder;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        final CronJobProcessor productInventoryChangedProcessor = ctx().getBean("productInventoryChangedProcessor", CronJobProcessor.class);

        final Map<String, Object> ctx = configureJobContext("productInventoryChangedProcessor", null);

        final Instant checkpoint = Instant.now();
        configureJob(ctx, job -> job.setCheckpoint(checkpoint));

        final long warehouseId = 2L;
        final String skuCode = "BENDER-ua";
        final Warehouse warehouse = warehouseService.findById(warehouseId);

        SkuWarehouse inventory = skuWarehouseService.findByWarehouseSku(warehouse, skuCode);
        assertNotNull(inventory);

        assertEquals(SkuWarehouse.AVAILABILITY_STANDARD, inventory.getAvailability());

        assertTrue(inventory.getQuantity().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(inventory.getQuantity().compareTo(inventory.getReserved()) > 0);

        final BigDecimal oldQuantity = inventory.getQuantity();

        productService.reindexProductSku(skuCode);

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
                productService.getGenericDao().executeNativeUpdate("update TSKUWAREHOUSE set QUANTITY = 0"
                        + ", UPDATED_TIMESTAMP = '2099-01-01 00:00:00' where WAREHOUSE_ID = "
                        + warehouseId + " and SKU_CODE = '" + skuCode + "'");
            }
        });

        inventory = skuWarehouseService.findByWarehouseSku(warehouse, skuCode);

        assertTrue(inventory.getQuantity().compareTo(BigDecimal.ZERO) == 0);
        assertTrue(inventory.getQuantity().compareTo(inventory.getReserved()) <= 0);

        // Run job
        productInventoryChangedProcessor.process(ctx);

        final JobStatus status1 = ((JobStatusAware) productInventoryChangedProcessor).getStatus(null);
        assertNotNull(status1);
        assertTrue(status1.getReport(),
                status1.getReport().contains("Inventory changed for 1 since "));


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
                productService.getGenericDao().executeNativeUpdate("update TSKUWAREHOUSE set QUANTITY = " + oldQuantity.toPlainString()
                        + ", UPDATED_TIMESTAMP = '2099-01-01 00:00:00' where WAREHOUSE_ID = "
                        + warehouseId + " and SKU_CODE = '" + skuCode + "'");
            }
        });

        inventory = skuWarehouseService.findByWarehouseSku(warehouse, skuCode);

        assertTrue(inventory.getQuantity().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(inventory.getQuantity().compareTo(inventory.getReserved()) > 0);

        // Run job
        productInventoryChangedProcessor.process(ctx);

        final JobStatus status2 = ((JobStatusAware) productInventoryChangedProcessor).getStatus(null);
        assertNotNull(status2);
        assertTrue(status2.getReport(),
                status2.getReport().contains("Inventory changed for 1 since "));

        mgr.getCache("productService-productSearchResultDTOByQuery").clear();
        mgr.getCache("productSkuService-productSkuSearchResultDTOByQuery").clear();

        rez = productService.getProductSearchResultDTOByQuery(context, 0, 1, null, false).getResults();
        assertNotNull(rez);
        assertEquals(1, rez.size());



    }

}
