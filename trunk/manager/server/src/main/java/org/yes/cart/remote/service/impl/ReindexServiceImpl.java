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

package org.yes.cart.remote.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
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
import org.yes.cart.web.service.ws.client.AsyncFlexContextImpl;
import org.yes.cart.web.service.ws.node.NodeService;
import org.yes.cart.web.service.ws.node.dto.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class ReindexServiceImpl extends SingletonJobRunner implements ReindexService {

    private static final Logger LOG = LoggerFactory.getLogger(ReindexServiceImpl.class);

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

            private final JobStatusListener listener = ctx.getListener();
            public void run() {

                listener.notifyPing();
                try {
                    ThreadLocalAsyncContextUtils.init(ctx);

                    listener.notifyMessage("Indexing stared");

                    final Map<String, Boolean> indexingFinished = ctx.getAttribute(JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE);
                    final Map<String, Integer> lastPositive = new HashMap<String, Integer>();
                    Map<String, Integer> cnt = new HashMap<String, Integer>();
                    for (final Node yesNode : nodeService.getYesNodes()) {
                        indexingFinished.put(yesNode.getNodeId(), Boolean.FALSE);
                        lastPositive.put(yesNode.getNodeId(), 0);
                        cnt.put(yesNode.getNodeId(), 0);
                    }
                    while (isIndexingInProgress(cnt)) {

                        // This should call
                        cnt = remoteBackdoorService.reindexAllProducts(ctx);
                        if (isIndexingInProgress(cnt)) {

                            final StringBuilder state = new StringBuilder("Indexed products so far:\n");
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
                    final StringBuilder state = new StringBuilder("Indexing completed. Last traceable product count:\n");
                    for (final Map.Entry<String, Integer> cntNode : lastPositive.entrySet()) {
                        final String nodeUri = cntNode.getKey();
                        final Integer nodeCnt = cntNode.getValue();
                        state.append(nodeUri).append(": ").append(nodeCnt).append(" ... finished\n");
                    }
                    state.append("CAUTION this may not be the full count of products that was indexed.\n");
                    listener.notifyMessage(state.toString());
                    listener.notifyCompleted(JobStatus.Completion.OK);
                } catch (Throwable trw) {
                    LOG.error(trw.getMessage(), trw);
                    listener.notifyError(trw.getMessage());
                    listener.notifyCompleted(JobStatus.Completion.ERROR);
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
        return doJob(createAsyncContext(true));
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Integer> reindexProduct(long pk) {
        return remoteBackdoorService.reindexProduct(createAsyncContext(false), pk);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Integer> reindexProductSku(long pk) {
        return remoteBackdoorService.reindexProductSku(createAsyncContext(false), pk);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Integer> reindexProductSkuCode(String code) {
        return remoteBackdoorService.reindexProductSkuCode(createAsyncContext(false), code);
    }

    private JobContext createAsyncContext(final boolean bulk) {

        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE, new HashMap<String, Boolean>());

        final AsyncContext flex = new AsyncFlexContextImpl(param);

        // Max char of report to UI since it will get huge and simply will crash the UI, not to mention traffic cost.
        final int logSize = Integer.parseInt(nodeService.getConfiguration().get(AttributeNamesKeys.System.IMPORT_JOB_LOG_SIZE));
        // Timeout - just in case runnable crashes and we need to unlock through timeout.
        final int timeout;
        if (bulk) {
            timeout = Integer.parseInt(nodeService.getConfiguration().get(AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_BULK_INDEX_TIMEOUT_MS));
        } else {
            timeout = Integer.parseInt(nodeService.getConfiguration().get(AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_SINGLE_INDEX_TIMEOUT_MS));
        }

        return new JobContextImpl(true, new JobStatusListenerImpl(logSize, timeout), flex.getAttributes());
    }

}
