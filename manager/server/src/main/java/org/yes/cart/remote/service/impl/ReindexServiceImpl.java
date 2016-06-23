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

package org.yes.cart.remote.service.impl;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.bulkjob.impl.BulkJobAutoContextImpl;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.remote.service.ReindexService;
import org.yes.cart.remote.service.RemoteBackdoorService;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.SingletonJobRunner;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.async.model.impl.JobContextImpl;
import org.yes.cart.service.async.utils.ThreadLocalAsyncContextUtils;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.service.ws.client.AsyncFlexContextImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class ReindexServiceImpl extends SingletonJobRunner implements ReindexService {

    private final RemoteBackdoorService remoteBackdoorService;

    private final NodeService nodeService;

    /**
     * Construct reindexer.
     *
     * @param executor task executor
     * @param remoteBackdoorService remote backdoor service.
     * @param nodeService node service
     */
    public ReindexServiceImpl(final TaskExecutor executor,
                              final RemoteBackdoorService remoteBackdoorService,
                              final NodeService nodeService) {
        super(executor);
        this.remoteBackdoorService = remoteBackdoorService;
        this.nodeService = nodeService;
    }

    /** {@inheritDoc} */
    @Override
    public JobStatus getIndexAllStatus(final String token) {
        return getStatus(token);
    }

    /** {@inheritDoc} */
    @Override
    protected Runnable createJobRunnable(final JobContext ctx) {

        return new Runnable() {

            private final JobContext context = ctx;
            private final JobStatusListener listener = ctx.getListener();

            public void run() {

                listener.notifyPing();
                try {
                    ThreadLocalAsyncContextUtils.init(context);

                    long start = System.currentTimeMillis();

                    listener.notifyMessage("Indexing stared\n");

                    final Map<String, Boolean> indexingFinished = context.getAttribute(JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE);
                    final Map<String, Integer> lastPositive = new HashMap<String, Integer>();
                    Map<String, Integer> cnt = new HashMap<String, Integer>();

                    for (final Node yesNode : nodeService.getYesNodes()) {
                        indexingFinished.put(yesNode.getNodeId(), Boolean.FALSE);
                        lastPositive.put(yesNode.getNodeId(), 0);
                        cnt.put(yesNode.getNodeId(), 0);
                    }

                    while (isIndexingInProgress(cnt)) {

                        // This should call
                        cnt = remoteBackdoorService.reindexAllProducts(context);
                        if (isIndexingInProgress(cnt)) {

                            final StringBuilder state = new StringBuilder("Indexing products:\n");
                            for (final Map.Entry<String, Integer> cntNode : cnt.entrySet()) {
                                final String nodeUri = cntNode.getKey();
                                final Integer nodeCnt = cntNode.getValue();
                                if (nodeCnt == null || nodeCnt < 0 || (lastPositive.containsKey(nodeUri) && lastPositive.get(nodeUri) > nodeCnt)) {
                                    // this node finished
                                    state.append(nodeUri).append(": ").append(lastPositive.get(nodeUri)).append(" ... finished\n");
                                } else {
                                    lastPositive.put(nodeUri, nodeCnt);
                                    state.append(nodeUri).append(": ").append(lastPositive.get(nodeUri)).append(" ... in progress\n");
                                }
                            }
                            listener.notifyPing(state.toString());
                            Thread.sleep(5000l);
                        }

                    }

                    final StringBuilder summaryProd = new StringBuilder("Product indexing completed. Last traceable product count:\n");
                    for (final Map.Entry<String, Integer> cntNode : lastPositive.entrySet()) {
                        final String nodeUri = cntNode.getKey();
                        final Integer nodeCnt = cntNode.getValue();
                        summaryProd.append(nodeUri).append(": ").append(nodeCnt).append(" ... finished\n");
                    }
                    listener.notifyMessage(summaryProd.toString());

                    for (final Node yesNode : nodeService.getYesNodes()) {
                        indexingFinished.put(yesNode.getNodeId(), Boolean.FALSE);
                        lastPositive.put(yesNode.getNodeId(), 0);
                        cnt.put(yesNode.getNodeId(), 0);
                    }

                    while (isIndexingInProgress(cnt)) {

                        // This should call
                        cnt = remoteBackdoorService.reindexAllProductsSku(context);
                        if (isIndexingInProgress(cnt)) {

                            final StringBuilder state = new StringBuilder("Indexing SKU:\n");
                            for (final Map.Entry<String, Integer> cntNode : cnt.entrySet()) {
                                final String nodeUri = cntNode.getKey();
                                final Integer nodeCnt = cntNode.getValue();
                                if (nodeCnt == null || nodeCnt < 0 || (lastPositive.containsKey(nodeUri) && lastPositive.get(nodeUri) > nodeCnt)) {
                                    // this node finished
                                    state.append(nodeUri).append(": ").append(lastPositive.get(nodeUri)).append(" ... finished\n");
                                } else {
                                    lastPositive.put(nodeUri, nodeCnt);
                                    state.append(nodeUri).append(": ").append(lastPositive.get(nodeUri)).append(" ... in progress\n");
                                }
                            }
                            listener.notifyPing(state.toString());
                            Thread.sleep(5000l);
                        }

                    }
                    final StringBuilder summarySku = new StringBuilder("SKU indexing completed. Last traceable SKU count:\n");
                    for (final Map.Entry<String, Integer> cntNode : lastPositive.entrySet()) {
                        final String nodeUri = cntNode.getKey();
                        final Integer nodeCnt = cntNode.getValue();
                        summarySku.append(nodeUri).append(": ").append(nodeCnt).append(" ... finished\n");
                    }
                    listener.notifyMessage(summarySku.toString());

                    long finish = System.currentTimeMillis();

                    listener.notifyMessage("\nIndexing completed (" + ((finish - start) / 1000) + "s)");
                    listener.notifyCompleted();
                } catch (Throwable trw) {
                    ShopCodeContext.getLog(this).error(trw.getMessage(), trw);
                    listener.notifyError(trw.getMessage());
                    listener.notifyCompleted();
                } finally {
                    ThreadLocalAsyncContextUtils.clear();
                }

            }
        };
    }

    boolean isIndexingInProgress(Map<String, Integer> cnt) {
        for (final Integer cntNode : cnt.values()) {
            if (cntNode != null && cntNode >= 0) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public String reindexAllProducts() {
        return doJob(createAsyncContext(true, 0L, AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_BULK_INDEX_TIMEOUT_MS));
    }

    /** {@inheritDoc} */
    @Override
    public String reindexShopProducts(final long shopPk) {
        return doJob(createAsyncContext(true, shopPk, AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_BULK_INDEX_TIMEOUT_MS));
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Integer> reindexProduct(long pk) {
        return remoteBackdoorService.reindexProduct(
                createAsyncContext(false, 0L, AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_SINGLE_INDEX_TIMEOUT_MS),
                pk
        );
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Integer> reindexProductSku(long pk) {
        return remoteBackdoorService.reindexProductSku(
                createAsyncContext(false, 0L, AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_SINGLE_INDEX_TIMEOUT_MS),
                pk
        );
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Integer> reindexProductSkuCode(String code) {
        return remoteBackdoorService.reindexProductSkuCode(
                createAsyncContext(false, 0L, AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_SINGLE_INDEX_TIMEOUT_MS),
                code
        );
    }

    private JobContext createAsyncContext(final boolean bulk, final long shopId, final String timeoutKey) {

        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, timeoutKey);
        param.put(JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE, new HashMap<String, Boolean>());
        if (shopId > 0L) {
            param.put(JobContextKeys.NODE_FULL_PRODUCT_INDEX_SHOP, shopId);
        }

        AsyncContext ctx = null;
        try {
            // This is manual access via YUM
            ctx = new AsyncFlexContextImpl(param);
        } catch (IllegalStateException exp) {
            // This is auto access with thread local
            ctx = new BulkJobAutoContextImpl(param);
        }

        // Max char of report to UI since it will get huge and simply will crash the UI, not to mention traffic cost.
        final int logSize = NumberUtils.toInt(nodeService.getConfiguration().get(AttributeNamesKeys.System.IMPORT_JOB_LOG_SIZE), 100);
        // Timeout - just in case runnable crashes and we need to unlock through timeout.
        final int timeout;
        if (bulk) {
            timeout = NumberUtils.toInt(nodeService.getConfiguration().get(AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_BULK_INDEX_TIMEOUT_MS), 100);
        } else {
            timeout = NumberUtils.toInt(nodeService.getConfiguration().get(AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_SINGLE_INDEX_TIMEOUT_MS), 100);
        }

        return new JobContextImpl(true, new JobStatusListenerImpl(logSize, timeout), ctx.getAttributes());
    }

}
