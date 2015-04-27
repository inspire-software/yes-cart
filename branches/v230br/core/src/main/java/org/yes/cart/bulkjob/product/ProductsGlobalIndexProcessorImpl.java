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

package org.yes.cart.bulkjob.product;

import org.slf4j.Logger;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.service.ws.node.NodeService;

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

    private final ProductService productService;
    private final NodeService nodeService;

    public ProductsGlobalIndexProcessorImpl(final ProductService productService,
                                            final NodeService nodeService) {
        this.productService = productService;
        this.nodeService = nodeService;
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

        log.info("Reindexing all products on {}", nodeId);

        productService.reindexProducts();

        log.info("Reindexing all SKU on {}", nodeId);

        productService.reindexProductsSku();

        final long finish = System.currentTimeMillis();

        final long ms = (finish - start);

        log.info("Reindexing on {} ... completed in {}s", nodeId, (ms > 0 ? ms / 1000 : 0));

    }


    protected String getNodeId() {
        return nodeService.getCurrentNodeId();
    }

    protected Boolean isLuceneIndexDisabled() {
        return Boolean.TRUE.toString().equals(nodeService.getConfiguration().get(NodeService.LUCENE_INDEX_DISABLED));
    }

}
