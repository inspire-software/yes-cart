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
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.service.ws.node.NodeService;

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
public class ProductsPassedAvailbilityDateIndexProcessorImpl implements Runnable {

    private final ProductService productService;
    private final NodeService nodeService;

    public ProductsPassedAvailbilityDateIndexProcessorImpl(final ProductService productService,
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
            log.info("Reindexing discontinued products on {} ... disabled", nodeId);
            return;
        }

        log.info("Reindexing discontinued products on {}", nodeId);

        final long start = System.currentTimeMillis();

        final List<Object> discontinued = productService.getGenericDao().findQueryObjectByNamedQuery("DISCONTINUED.PRODUCTS.AFTER.DATE", now());

        for (final Object pk : discontinued) {
            log.debug("Reindexing discontinued product {} on {}", pk, nodeId);
            productService.reindexProduct((Long) pk);
        }

        log.info("Reindexing discontinued products on {}, reindexed {}", nodeId, discontinued.size());

        final long finish = System.currentTimeMillis();

        final long ms = (finish - start);

        log.info("Reindexing discontinued on {} ... completeed {}s", nodeId, (ms > 0 ? ms / 1000 : 0));

    }

    protected String getNodeId() {
        return nodeService.getCurrentNodeId();
    }

    protected Boolean isLuceneIndexDisabled() {
        return Boolean.TRUE.toString().equals(nodeService.getConfiguration().get(NodeService.LUCENE_INDEX_DISABLED));
    }

    protected Date now() {
        return new Date();
    }
}
