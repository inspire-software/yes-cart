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

package org.yes.cart.service.cluster.impl;

import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.bulkjob.impl.BulkJobAutoContextImpl;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.service.async.AsyncContextFactory;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.service.async.model.impl.JobContextImpl;
import org.yes.cart.service.cluster.AsyncExecutor;
import org.yes.cart.service.cluster.ClusterService;
import org.yes.cart.service.cluster.ProductAsyncSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 14/11/2019
 * Time: 07:49
 */
public class ProductAsyncSupportImpl implements ProductAsyncSupport {

    private final ClusterService clusterService;
    private final AsyncContextFactory asyncContextFactory;
    private final NodeService nodeService;
    private final AsyncExecutor asyncExecutor;


    public ProductAsyncSupportImpl(final ClusterService clusterService,
                                   final AsyncContextFactory asyncContextFactory,
                                   final NodeService nodeService,
                                   final AsyncExecutor asyncExecutor) {
        this.clusterService = clusterService;
        this.asyncContextFactory = asyncContextFactory;
        this.nodeService = nodeService;
        this.asyncExecutor = asyncExecutor;
    }


    /** {@inheritDoc} */
    @Override
    public void asyncIndexProduct(final long productId) {
        final JobContext ctx = createJobContext();
        this.asyncExecutor.asyncExecute(() -> clusterService.reindexProduct(ctx, productId));
    }

    /** {@inheritDoc} */
    @Override
    public void asyncIndexSku(final long skuId) {
        final JobContext ctx = createJobContext();
        this.asyncExecutor.asyncExecute(() -> clusterService.reindexProductSku(ctx, skuId));
    }

    /** {@inheritDoc} */
    @Override
    public void asyncIndexSku(final String sku) {
        final JobContext ctx = createJobContext();
        this.asyncExecutor.asyncExecute(() -> clusterService.reindexProductSkuCode(ctx, sku));
    }

    private JobContext createJobContext() {

        final Map<String, Object> param = new HashMap<>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_CONNECTOR_PRODUCT_SINGLE_INDEX_TIMEOUT_MS);
        param.put(JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE, new HashMap<String, Boolean>());
        param.putAll(createAuthCtx().getAttributes());

        // Max char of report to UI since it will get huge and simply will crash the UI, not to mention traffic cost.
        final int logSize = NumberUtils.toInt(nodeService.getConfiguration().get(AttributeNamesKeys.System.IMPORT_JOB_LOG_SIZE), 100);
        // Timeout - just in case runnable crashes and we need to unlock through timeout.
        final int timeout = NumberUtils.toInt(nodeService.getConfiguration().get(AttributeNamesKeys.System.SYSTEM_CONNECTOR_PRODUCT_SINGLE_INDEX_TIMEOUT_MS), 100);

        return new JobContextImpl(true, new JobStatusListenerImpl(logSize, timeout), param);
    }

    private AsyncContext createAuthCtx() {

        final Map<String, Object> param = new HashMap<>();

        try {
            // This is manual access via Admin
            return asyncContextFactory.getInstance(param);
        } catch (IllegalStateException exp) {
            // This is auto access with thread local
            return new BulkJobAutoContextImpl(param);
        }
    }


}
