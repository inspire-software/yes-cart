package org.yes.cart.domain.entityindexer.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

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
     * Push pk value to inderlar set for reindex.
     *
     * @param productPkValue product primary key value.
     */
    public void submitIndexTask(final Long productPkValue) {

        try {
            reindexQueue.put(productPkValue);
            threadPool.submit(
                    new Runnable() {

                        public void run() {
                            try {
                                productDao.fullTextSearchReindex(reindexQueue.take());
                            } catch (InterruptedException e) {
                                LOG.error("Cant get product pk from queue to reindex", e);
                            }
                        }
                    }
            );
        } catch (InterruptedException e) {
            LOG.error("Cant put product pk from queue to reindex", e);
        }

    }

}
