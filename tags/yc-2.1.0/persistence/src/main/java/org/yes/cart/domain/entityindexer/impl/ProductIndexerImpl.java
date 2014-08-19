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

package org.yes.cart.domain.entityindexer.impl;

import org.slf4j.Logger;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entityindexer.ProductIndexer;
import org.yes.cart.util.ShopCodeContext;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 5/5/12
 * Time: 1:34 PM
 */
public class ProductIndexerImpl implements ProductIndexer {

    private final ExecutorService threadPool;

    private final ArrayBlockingQueue<Long> reindexQueue;

    private final GenericDAO<Product, Long> productDao;

    /**
     * Construct indexer.
     *
     * @param productDao     dao to perform reindex
     * @param queueLength    queue length to reindex product
     * @param threadPoolSize quantity of executors.
     */
    public ProductIndexerImpl(final GenericDAO<Product, Long> productDao, final int queueLength, final int threadPoolSize) {

        this.productDao = productDao;

        reindexQueue = new ArrayBlockingQueue<Long>(queueLength);

        threadPool = Executors.newFixedThreadPool(threadPoolSize);

    }

    /**
     * Push pk value to indexer set for reindex.
     *
     * @param productPkValue product primary key value.
     */
    public void submitIndexTask(final Long productPkValue) {

        final Logger log = ShopCodeContext.getLog(this);
        try {
            reindexQueue.put(productPkValue);
            threadPool.submit(
                    new Runnable() {

                        public void run() {
                            try {
                                productDao.fullTextSearchReindex(reindexQueue.take());
                            } catch (InterruptedException e) {
                                log.error("Cant get product pk from queue to reindex", e);
                            }
                        }
                    }
            );
        } catch (InterruptedException e) {
            log.error("Cant put product pk from queue to reindex", e);
        }

    }

}
