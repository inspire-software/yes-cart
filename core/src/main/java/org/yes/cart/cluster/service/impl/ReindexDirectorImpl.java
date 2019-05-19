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
package org.yes.cart.cluster.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.cache.CacheBundleHelper;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.service.ReindexDirector;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.search.dao.IndexBuilder;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.utils.DateUtils;

import java.time.Instant;

/**
 * User: denispavlov
 * Date: 13/04/2019
 * Time: 16:53
 */
public class ReindexDirectorImpl implements ReindexDirector {

    private static final Logger LOG = LoggerFactory.getLogger(ReindexDirectorImpl.class);

    private static final String INDEX_DONE_STATUS = "DONE";
    private static final String INDEX_RUNNING_STATUS = "RUNNING";
    private static final Object[] INDEX_DISABLED_STATUS = new Object[] { INDEX_DONE_STATUS, 0 };

    private ProductService productService;
    private SystemService systemService;
    private CacheBundleHelper productIndexCaches;
    private NodeService nodeService;

    /*
     * Once a product is reindexed we need to flush all cached information
     * to enforce changes to take immediate effect on the storefront.
     */
    private void flushCache() {

        productIndexCaches.flushBundleCaches();

    }

    Boolean isLuceneIndexDisabled() {
        return nodeService.getCurrentNode().isFtIndexDisabled();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getProductReindexingState() {
        if (isLuceneIndexDisabled()) {
            return INDEX_DISABLED_STATUS;
        }
        final IndexBuilder.FTIndexState state = productService.getProductsFullTextIndexState();
        if (state.isFullTextSearchReindexCompleted()) {
            flushCache();
            return new Object[] { INDEX_DONE_STATUS, state.getLastIndexCount() };
        }
        return new Object[] { INDEX_RUNNING_STATUS, state.getLastIndexCount() };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getProductSkuReindexingState() {
        if (isLuceneIndexDisabled()) {
            return INDEX_DISABLED_STATUS;
        }
        final IndexBuilder.FTIndexState state = productService.getProductsSkuFullTextIndexState();
        if (state.isFullTextSearchReindexCompleted()) {
            flushCache();
            return new Object[] { INDEX_DONE_STATUS, state.getLastIndexCount() };
        }
        return new Object[] { INDEX_RUNNING_STATUS, state.getLastIndexCount() };
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexAllProducts() {
        if (!isLuceneIndexDisabled()) {

            try {
                final Instant now = Instant.now();
                final String inventoryChangeLastKey = "JOB_PRODINVUP_LR_" + nodeService.getCurrentNodeId();
                final String lastRun = systemService.getAttributeValue(inventoryChangeLastKey);
                if (StringUtils.isBlank(lastRun) || DateUtils.iParseSDT(lastRun).isBefore(now)) {
                    // Ensure that product inventory changes have last date which corresponds to indexing start time
                    systemService.updateAttributeValue(inventoryChangeLastKey, DateUtils.formatSDT(now));
                }
            } catch (Exception exp) {
                LOG.error("Unable to update JOB_PRODINVUP_LR_X: " + exp.getMessage(), exp);
            }

            productService.reindexProducts(getProductIndexBatchSize());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexAllProductsSku() {
        if (!isLuceneIndexDisabled()) {
            productService.reindexProductsSku(getProductIndexBatchSize());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexShopProducts(final long shopPk) {
        if (!isLuceneIndexDisabled()) {
            productService.reindexProducts(shopPk, getProductIndexBatchSize());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexShopProductsSku(final long shopPk) {
        if (!isLuceneIndexDisabled()) {
            productService.reindexProductsSku(shopPk, getProductIndexBatchSize());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProduct(final long productPk) {
        if (!isLuceneIndexDisabled()) {
            productService.reindexProduct(productPk);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProductSku(final long productPk) {
        if (!isLuceneIndexDisabled()) {
            productService.reindexProductSku(productPk);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProductSkuCode(final String productCode) {
        if (!isLuceneIndexDisabled()) {
            productService.reindexProductSku(productCode);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProducts(final long[] productPks) {
        if (!isLuceneIndexDisabled()) {
            for (long pk : productPks) {
                productService.reindexProduct(pk);
            }
        }
    }

    /**
     * IoC. node service
     *
     * @param nodeService node service to use
     */
    public void setNodeService(final NodeService nodeService) {
        this.nodeService = nodeService;
    }

    /**
     * IoC. Set product service.
     *
     * @param productService product service to use.
     */
    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    /**
     * IoC. Set product service.
     *
     * @param systemService service to use.
     */
    public void setSystemService(final SystemService systemService) {
        this.systemService = systemService;
    }

    /**
     * IoC. Product index bundle helper
     *
     * @param productIndexCaches product index bundle helper
     */
    public void setProductIndexCaches(final CacheBundleHelper productIndexCaches) {
        this.productIndexCaches = productIndexCaches;
    }

    private int getProductIndexBatchSize() {
        return NumberUtils.toInt(systemService.getAttributeValue(AttributeNamesKeys.System.JOB_REINDEX_PRODUCT_BATCH_SIZE), 100);
    }

}
