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

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkjob.cron.AbstractCronJobProcessorImpl;
import org.yes.cart.cache.CacheBundleHelper;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.domain.entity.Job;
import org.yes.cart.domain.entity.JobDefinition;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.impl.JobStatusListenerWithLoggerImpl;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.utils.DateUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Processor that scrolls though all modified inventory and re-indexes products so that
 * all latest information is propagated to all nodes.
 *
 * Last time this job runs is stored in system preferences: JOB_PRODINVUP_LR_[NODEID]
 * So that next run we only scan orders that have changed since last job run.
 *
 * User: denispavlov
 * Date: 27/04/2015
 * Time: 15:42
 */
public class ProductInventoryChangedProcessorImpl extends AbstractCronJobProcessorImpl
        implements ProductInventoryChangedProcessorInternal, JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(ProductInventoryChangedProcessorImpl.class);

    private SkuWarehouseService skuWarehouseService;
    private ProductService productService;
    private NodeService nodeService;
    private CacheBundleHelper productCacheHelper;

    private final JobStatusListener listener = new JobStatusListenerWithLoggerImpl(new JobStatusListenerImpl(), LOG);

    /** {@inheritDoc} */
    @Override
    public JobStatus getStatus(final String token) {
        return listener.getLatestStatus();
    }

    /** {@inheritDoc} */
    @Override
    public Pair<JobStatus, Instant> processInternal(final Map<String, Object> context, final Job job, final JobDefinition definition) {

        final Properties properties = readContextAsProperties(context, job, definition);
        final Instant lastCheckpoint = job.getCheckpoint();
        final Instant newCheckpoint = Instant.now();

        listener.reset();

        final String nodeId = getNodeId();

        if (isLuceneIndexDisabled()) {
            listener.notifyInfo("Reindexing products inventory updates on {} ... disabled", nodeId);
            listener.notifyCompleted();
            return new Pair<>(listener.getLatestStatus(), lastCheckpoint);
        }

        if (isFullIndexInProgress()) {
            listener.notifyInfo("Reindexing inventory updates on {}, reindex ALL is already in progress", nodeId);
            listener.notifyCompleted();
            return new Pair<>(listener.getLatestStatus(), newCheckpoint);
        }

        int batchSize = NumberUtils.toInt(properties.getProperty("reindex-batch-size"), 100);

        listener.notifyInfo("Check changed orders products to be reindexed on {}, batch {}", nodeId, batchSize);

        List<String> productSkus = skuWarehouseService.findProductSkuForWhichInventoryChangedAfter(lastCheckpoint);

        if (productSkus != null && !productSkus.isEmpty()) {

            // Check again to see if we do not have bulk updates
            int count = productSkus.size();
            int full = NumberUtils.toInt(properties.getProperty("inventory-full-threshold"), 1000);
            int checkDelay = NumberUtils.toInt(properties.getProperty("inventory-delta-seconds"), 15) * 1000;
            int checkDelta = NumberUtils.toInt(properties.getProperty("inventory-update-delta"), 100);

            boolean runBatch = count < full;

            if (runBatch) {
                try {
                    Thread.sleep(checkDelay);
                } catch (InterruptedException e) {
                    // OK
                }
                productSkus = skuWarehouseService.findProductSkuForWhichInventoryChangedAfter(lastCheckpoint);
                int delta = productSkus.size() - count;
                int maxDelta = checkDelta;
                if (delta > maxDelta) {
                    listener.notifyInfo("Detected bulking operation ... {} inventory records changed in past {} seconds (max: {}). Postponing check until next run.",
                            delta, checkDelay / 1000, maxDelta);
                    listener.notifyCompleted();
                    return new Pair<>(listener.getLatestStatus(), lastCheckpoint);
                }
            }

            // Check whether we need batch or full
            count = productSkus.size();
            runBatch = count < full;

            listener.notifyInfo("Last change detected {}, indexing will run {}", count, (runBatch ? "batch" : "full"));

            listener.notifyInfo("Inventory changed for {} since {}", count, DateUtils.formatSDT(lastCheckpoint));

            if (runBatch) {
                int fromIndex = 0;
                int toIndex;
                while (fromIndex < productSkus.size()) {

                    if (isFullIndexInProgress()) {
                        listener.notifyInfo("Reindexing inventory updates on {}, reindex ALL is already in progress", nodeId);
                        listener.notifyCompleted();
                        return new Pair<>(listener.getLatestStatus(), newCheckpoint);
                    }

                    toIndex = fromIndex + batchSize > productSkus.size() ? productSkus.size() : fromIndex + batchSize;
                    final List<String> skuBatch = productSkus.subList(fromIndex, toIndex);
                    LOG.debug("Reindexing SKU {}  ... so far reindexed {}", skuBatch, fromIndex);

                    self().reindexBatch(skuBatch, batchSize);

                    fromIndex = toIndex;

                }
                listener.count("Reindexed on " + nodeId, count);
            } else {

                if (isFullIndexInProgress()) {
                    listener.notifyInfo("Reindexing inventory updates on {}, reindex ALL is already in progress", nodeId);
                    listener.notifyCompleted();
                    return new Pair<>(listener.getLatestStatus(), newCheckpoint);
                }

                self().reindexBatch(null, batchSize);
                listener.notifyInfo("Reindexing inventory updates on {}, reindexed ALL", nodeId);
            }

            flushCaches();
        }

        listener.notifyCompleted();

        return new Pair<>(listener.getLatestStatus(), newCheckpoint);
    }

    protected void flushCaches() {

        productCacheHelper.flushBundleCaches();

    }

    protected boolean isFullIndexInProgress() {
        return productService.getProductsFullTextIndexState().isFullTextSearchReindexInProgress() ||
                productService.getProductsSkuFullTextIndexState().isFullTextSearchReindexInProgress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexBatch(final List<String> skuCodes, final int batchSize) {

        if (skuCodes == null) {
            // do full reindex
            productService.reindexProducts(batchSize, true);
            productService.reindexProductsSku(batchSize, true);

        } else {
            for (final String sku : skuCodes) {
                // batch only
                productService.reindexProductSku(sku);
            }
        }

    }

    protected String getNodeId() {
        return nodeService.getCurrentNodeId();
    }

    protected Boolean isLuceneIndexDisabled() {
        return nodeService.getCurrentNode().isFtIndexDisabled();
    }


    private ProductInventoryChangedProcessorInternal self;

    private ProductInventoryChangedProcessorInternal self() {
        if (self == null) {
            self = getSelf();
        }
        return self;
    }

    public ProductInventoryChangedProcessorInternal getSelf() {
        return null;
    }

    /**
     * Spring IoC.
     *
     * @param skuWarehouseService service
     */
    public void setSkuWarehouseService(final SkuWarehouseService skuWarehouseService) {
        this.skuWarehouseService = skuWarehouseService;
    }

    /**
     * Spring IoC.
     *
     * @param productService service
     */
    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    /**
     * Spring IoC.
     *
     * @param nodeService service
     */
    public void setNodeService(final NodeService nodeService) {
        this.nodeService = nodeService;
    }

    /**
     * Spring IoC.
     *
     * @param productCacheHelper service
     */
    public void setProductCacheHelper(final CacheBundleHelper productCacheHelper) {
        this.productCacheHelper = productCacheHelper;
    }
}
