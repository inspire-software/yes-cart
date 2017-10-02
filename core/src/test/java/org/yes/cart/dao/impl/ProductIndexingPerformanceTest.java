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

package org.yes.cart.dao.impl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.dao.GenericFTSCapableDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public class ProductIndexingPerformanceTest extends AbstractTestDAO {

    private int maxIterations = 500;
    private int batchSize = 250;

    private GenericFTSCapableDAO<Product, Long, Object> productDao;
    private GenericFTSCapableDAO<ProductSku, Long, Object> productSkuDao;

    @Before
    public void setUp()  {
        productDao = (GenericFTSCapableDAO<Product, Long, Object>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_DAO);
        productSkuDao = (GenericFTSCapableDAO<ProductSku, Long, Object>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_SKU_DAO);

        super.setUp();
    }

// Performance sampling on MySQL
//    @Override
//    protected AbstractDatabaseTester createDatabaseTester() throws Exception {
//        return null;
//    }

    private void runIndexing(final String entity, final GenericFTSCapableDAO dao, final int maxIterations, final int batchSize) throws InterruptedException {

        getTxReadOnly().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                System.out.println("Starting " + entity + " test with count: " + dao.findCountByCriteria(null));

                final long start = System.currentTimeMillis();
                for (int i = 0; i < maxIterations; i++) {
                    dao.fullTextSearchReindex(false, batchSize);
                    final long now = System.currentTimeMillis();
                    System.out.println("Cycle: " + i + ", average cycle (ms): " + ((now - start) / (i + 1)));
                }
                status.setRollbackOnly();

            }
        });

    }


    @Ignore("Performance sampling only")
    @Test
    public void testProductIndexPerformance() throws InterruptedException {

        runIndexing("product", this.productDao, maxIterations, batchSize);

    }

    @Ignore("Performance sampling only")
    @Test
    public void testProductSkuIndexPerformance() throws InterruptedException {

        runIndexing("sku", this.productSkuDao, maxIterations, batchSize);

    }


}
