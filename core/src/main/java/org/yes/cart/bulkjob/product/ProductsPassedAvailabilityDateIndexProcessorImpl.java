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

import org.slf4j.Logger;
import org.yes.cart.cache.CacheBundleHelper;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.util.ShopCodeContext;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Since we need to remove discontinued items from index we need a cron job to
 * select products whose availableTo date is before now.
 *
 * For these products we force reindexing that removed them from the index.
 *
 * User: denispavlov
 * Date: 13/11/2013
 * Time: 15:30
 */
public class ProductsPassedAvailabilityDateIndexProcessorImpl implements ProductsPassedAvailabilityDateIndexProcessorInternal {

    private final ProductService productService;
    private final NodeService nodeService;
    private final CacheBundleHelper productCacheHelper;

    private int batchSize = 100;
    private int numberOfDays = 1;

    public ProductsPassedAvailabilityDateIndexProcessorImpl(final ProductService productService,
                                                            final NodeService nodeService,
                                                            final CacheBundleHelper productCacheHelper) {
        this.productService = productService;
        this.nodeService = nodeService;
        this.productCacheHelper = productCacheHelper;
    }


    /** {@inheritDoc} */
    @Override
    public void run() {

        final Logger log = ShopCodeContext.getLog(this);

        final String nodeId = getNodeId();

        if (isLuceneIndexDisabled()) {
            log.info("Reindexing discontinued products on {} ... disabled", nodeId);
            return;
        }

        log.info("Reindexing discontinued products on {}", nodeId);

        final long start = System.currentTimeMillis();

        final List<Long> discontinued = self().findDiscontinuedProductsIds();

        if (discontinued != null && discontinued.size() > 0) {

            int fromIndex = 0;
            int toIndex = 0;
            while (fromIndex < discontinued.size()) {

                toIndex = fromIndex + batchSize > discontinued.size() ? discontinued.size() : fromIndex + batchSize;
                final List<Long> pkBatch = discontinued.subList(fromIndex, toIndex);
                log.info("Reindexing discontinued products {}  ... so far reindexed {}", pkBatch, fromIndex);

                self().reindexBatch(pkBatch);

                fromIndex = toIndex;

            }

            flushCaches();

        }

        log.info("Reindexing discontinued products on {}, reindexed {}", nodeId, discontinued.size());

        final long finish = System.currentTimeMillis();

        final long ms = (finish - start);

        log.info("Reindexing discontinued on {} ... completeed {}s", nodeId, (ms > 0 ? ms / 1000 : 0));

    }

    protected void flushCaches() {

        productCacheHelper.flushBundleCaches();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> findDiscontinuedProductsIds() {
        return (List) productService.getGenericDao()
                .findQueryObjectByNamedQuery("DISCONTINUED.PRODUCTS.AFTER.DATE.CHANGED.AFTER", now(0), now(numberOfDays));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexBatch(final List<Long> pks) {

        for (final Long pk : pks) {

            productService.reindexProduct(pk);

        }

    }

    protected String getNodeId() {
        return nodeService.getCurrentNodeId();
    }

    protected Boolean isLuceneIndexDisabled() {
        return nodeService.getCurrentNode().isFtIndexDisabled();
    }

    protected Date now(int minusDays) {
        final Calendar now = Calendar.getInstance();
        if (minusDays != 0) {
            now.add(Calendar.DAY_OF_YEAR, -minusDays);
        }
        return now.getTime();
    }

    /**
     * Batch size for remote index update.
     *
     * @param batchSize batch size
     */
    public void setBatchSize(final int batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * Number of days in past before current run that the available to date has changed.
     *
     * @param numberOfDays number of days
     */
    public void setNumberOfDays(final int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    private ProductsPassedAvailabilityDateIndexProcessorInternal self;

    private ProductsPassedAvailabilityDateIndexProcessorInternal self() {
        if (self == null) {
            self = getSelf();
        }
        return self;
    }

    public ProductsPassedAvailabilityDateIndexProcessorInternal getSelf() {
        return null;
    }

}
