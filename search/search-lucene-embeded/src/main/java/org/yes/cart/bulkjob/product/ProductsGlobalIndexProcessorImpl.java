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
import org.yes.cart.search.dao.IndexBuilder;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.impl.JobStatusListenerWithLoggerImpl;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.ProductService;

import java.time.Instant;
import java.util.Map;
import java.util.Properties;

/**
 * This is a full product reindex job that allows to ensure that indexes are in full sync
 * over time. Indexes can go out of sync due to order placement, inventory changes, price changes
 * category assignments etc.
 *
 * This is especially useful in clustered environments.
 *
 * User: denispavlov
 * Date: 13/11/2013
 * Time: 15:30
 */
public class ProductsGlobalIndexProcessorImpl extends AbstractCronJobProcessorImpl implements JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(ProductsGlobalIndexProcessorImpl.class);

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

        listener.reset();

        final String nodeId = getNodeId();

        if (isLuceneIndexDisabled()) {
            listener.notifyInfo("Reindexing all products on {} ... disabled", nodeId);
            listener.notifyCompleted();
            return new Pair<>(listener.getLatestStatus(), null);
        }

        final IndexBuilder.FTIndexState state = productService.getProductsFullTextIndexState();
        if (!state.isFullTextSearchReindexInProgress()) {

            final Properties properties = readContextAsProperties(context, job, definition);
            final int batchSize = NumberUtils.toInt(properties.getProperty("reindex-batch-size"), 100);

            LOG.info("Reindexing all products on {}", nodeId);
            listener.notifyPing("Indexing products");

            productService.reindexProducts(batchSize, false);

            final IndexBuilder.FTIndexState stateProduct = productService.getProductsFullTextIndexState();
            listener.count("Products", (int) stateProduct.getLastIndexCount());

            LOG.info("Reindexing all SKU on {}", nodeId);
            listener.notifyPing("Indexing SKU");

            productService.reindexProductsSku(batchSize);

            final IndexBuilder.FTIndexState stateSku = productService.getProductsSkuFullTextIndexState();
            listener.count("SKU", (int) stateSku.getLastIndexCount());

            LOG.info("Flushing product caches {}", nodeId);

            productCacheHelper.flushBundleCaches();
            listener.notifyPing(null); // clear message

        } else {

            LOG.info("Reindexing all products on {} is already in progress ... skipping", nodeId);

        }

        LOG.info("Reindexing all on {} ... completed", nodeId);

        listener.notifyCompleted();

        return new Pair<>(listener.getLatestStatus(), null);

    }


    protected String getNodeId() {
        return nodeService.getCurrentNodeId();
    }

    protected Boolean isLuceneIndexDisabled() {
        return nodeService.getCurrentNode().isFtIndexDisabled();
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
