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

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.cache.CacheBundleHelper;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SystemService;

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

    private static final Logger LOG = LoggerFactory.getLogger(ProductsPassedAvailabilityDateIndexProcessorImpl.class);

    private final ProductService productService;
    private final NodeService nodeService;
    private final SystemService systemService;
    private final CacheBundleHelper productCacheHelper;

    private int numberOfDays = 1;

    public ProductsPassedAvailabilityDateIndexProcessorImpl(final ProductService productService,
                                                            final NodeService nodeService,
                                                            final SystemService systemService,
                                                            final CacheBundleHelper productCacheHelper) {
        this.productService = productService;
        this.nodeService = nodeService;
        this.systemService = systemService;
        this.productCacheHelper = productCacheHelper;
    }


    /** {@inheritDoc} */
    @Override
    public void run() {

        final String nodeId = getNodeId();

        if (isLuceneIndexDisabled()) {
            LOG.info("Reindexing discontinued products on {} ... disabled", nodeId);
            return;
        }

        LOG.info("Reindexing discontinued products on {}", nodeId);

        final List<Long> discontinued = self().findDiscontinuedProductsIds();

        if (discontinued != null && discontinued.size() > 0) {

            final int batchSize = getBatchSize();

            int fromIndex = 0;
            int toIndex = 0;
            while (fromIndex < discontinued.size()) {

                toIndex = fromIndex + batchSize > discontinued.size() ? discontinued.size() : fromIndex + batchSize;
                final List<Long> pkBatch = discontinued.subList(fromIndex, toIndex);
                LOG.info("Reindexing discontinued products {}  ... so far reindexed {}", pkBatch, fromIndex);

                self().reindexBatch(pkBatch);

                fromIndex = toIndex;

            }

            flushCaches();

        }

        LOG.info("Reindexing discontinued products on {}, reindexed {}", nodeId, discontinued != null ? discontinued.size() : 0);

        LOG.info("Reindexing discontinued on {} ... completed", nodeId);

    }

    protected int getBatchSize() {
        return NumberUtils.toInt(systemService.getAttributeValue(AttributeNamesKeys.System.JOB_REINDEX_PRODUCT_BATCH_SIZE), 100);
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
