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

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public class ProductIndexingPerformanceTest extends AbstractTestDAO {

    private GenericFTSCapableDAO<Product, Long, Object> productDao;

    @Before
    public void setUp()  {
        productDao = (GenericFTSCapableDAO<Product, Long, Object>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_DAO);

        super.setUp();
    }

    @Ignore("Performance sampling only")
    @Test
    public void testCreateNewProductNoSkuNoCategory() throws InterruptedException {

        final int maxIterations = 1000;

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                System.out.println("Starting test with product count: " + productDao.findCountByCriteria());

                final long start = System.currentTimeMillis();
                for (int i = 0; i < maxIterations; i++) {
                    productDao.fullTextSearchReindex(false, 1000);
                    final long now = System.currentTimeMillis();
                    System.out.println("Cycle: " + i + ", average cycle (ms): " + ((now - start) / (i + 1)));
                }
                status.setRollbackOnly();

            }
        });

    }


}
