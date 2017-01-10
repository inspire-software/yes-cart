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
import org.yes.cart.cache.CacheBundleHelper;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericFullTextSearchCapableDAO;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.util.ShopCodeContext;

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
public class ProductsGlobalIndexProcessorImpl implements Runnable {

    public static final long INDEX_PING_INTERVAL = 15000L;

    private final ProductService productService;
    private final NodeService nodeService;
    private final SystemService systemService;
    private final CacheBundleHelper productCacheHelper;

    public ProductsGlobalIndexProcessorImpl(final ProductService productService,
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

        final Logger log = ShopCodeContext.getLog(this);

        final String nodeId = getNodeId();

        if (isLuceneIndexDisabled()) {
            log.info("Reindexing all products on {} ... disabled", nodeId);
            return;
        }

        final long start = System.currentTimeMillis();

        final GenericFullTextSearchCapableDAO.FTIndexState state = productService.getProductsFullTextIndexState();
        if (!state.isFullTextSearchReindexInProgress()) {

            final int batchSize = getBatchSize();

            log.info("Reindexing all products on {}", nodeId);

            productService.reindexProducts(batchSize, false);

            log.info("Reindexing all SKU on {}", nodeId);

            productService.reindexProductsSku(batchSize);

            log.info("Flushing product caches {}", nodeId);

            productCacheHelper.flushBundleCaches();

        } else {

            log.info("Reindexing all products on {} is already in progress ... skipping", nodeId);

        }

        final long finish = System.currentTimeMillis();

        final long ms = (finish - start);

        log.info("Reindexing on {} ... completed in {}s", nodeId, (ms > 0 ? ms / 1000 : 0));

    }


    protected String getNodeId() {
        return nodeService.getCurrentNodeId();
    }

    protected Boolean isLuceneIndexDisabled() {
        return nodeService.getCurrentNode().isFtIndexDisabled();
    }

    protected int getBatchSize() {
        return NumberUtils.toInt(systemService.getAttributeValue(AttributeNamesKeys.System.JOB_REINDEX_PRODUCT_BATCH_SIZE), 100);
    }


}
