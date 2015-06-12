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
import org.yes.cart.bulkjob.cron.AbstractLastRunDependentProcessorImpl;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.RuntimeAttributeService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.service.ws.node.NodeService;

import java.util.Date;
import java.util.List;

/**
 * Processor that scrolls though all modified inventory and re-indexes products so that
 * all latest information is propagated to all nodes.
 *
 * Last time this job runs is stored in system preferences: JOB_PROD_INV_UPDATE_LAST_RUN_[NODEID]
 * So that next run we only scan orders that have changed since last job run.
 *
 * User: denispavlov
 * Date: 27/04/2015
 * Time: 15:42
 */
public class ProductInventoryChangedProcessorImpl extends AbstractLastRunDependentProcessorImpl
        implements Runnable {

    private static final String LAST_RUN_PREF = "JOB_PROD_INV_UPDATE_LAST_RUN_";

    private final SkuWarehouseService skuWarehouseService;
    private final ProductService productService;
    private final NodeService nodeService;

    private int batchSize = 20;

    public ProductInventoryChangedProcessorImpl(final SkuWarehouseService skuWarehouseService,
                                                final ProductService productService,
                                                final NodeService nodeService,
                                                final SystemService systemService,
                                                final RuntimeAttributeService runtimeAttributeService) {
        super(systemService, runtimeAttributeService);
        this.skuWarehouseService = skuWarehouseService;
        this.productService = productService;
        this.nodeService = nodeService;
    }

    /** {@inheritDoc} */
    @Override
    protected String getLastRunPreferenceAttributeName() {
        return LAST_RUN_PREF + getNodeId();
    }

    /** {@inheritDoc} */
    @Override
    protected void doRun(final Date lastRun) {

        final Logger log = ShopCodeContext.getLog(this);

        final String nodeId = getNodeId();

        if (isLuceneIndexDisabled()) {
            log.info("Reindexing products inventory updates on {} ... disabled", nodeId);
            return;
        }

        final long start = System.currentTimeMillis();

        log.info("Check changed orders products to be reindexed on {}, batch {}", nodeId, batchSize);

        final List<String> productSkus = skuWarehouseService.findProductSkuForWhichInventoryChangedAfter(lastRun);

        if (productSkus != null && !productSkus.isEmpty()) {

            log.info("Inventory changed for {} since {}", productSkus.size(), lastRun);

            int count = 0;
            for (final String sku : productSkus) {

                productService.reindexProductSku(sku);
                if (++count % this.batchSize == 0 ) {
                    //flush a batch of updates and release memory:
                    productService.getGenericDao().flush();
                    productService.getGenericDao().clear();
                }

            }

            log.info("Reindexed {} SKU", count);

        }


        final long finish = System.currentTimeMillis();

        final long ms = (finish - start);

        log.info("Reindexing inventory updates on {} ... completed in {}s", nodeId, (ms > 0 ? ms / 1000 : 0));

    }

    protected String getNodeId() {
        return nodeService.getCurrentNodeId();
    }

    protected Boolean isLuceneIndexDisabled() {
        return Boolean.TRUE.toString().equals(nodeService.getConfiguration().get(NodeService.LUCENE_INDEX_DISABLED));
    }

    /**
     * Batch size for remote index update.
     *
     * @param batchSize batch size
     */
    public void setBatchSize(final int batchSize) {
        this.batchSize = batchSize;
    }
}
