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

package org.yes.cart.bulkjob.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.node.RspMessage;
import org.yes.cart.cluster.node.impl.ContextRspMessageImpl;
import org.yes.cart.cluster.service.CacheEvictionQueue;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.service.async.AsyncContextFactory;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.utils.RunAsUserAuthentication;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: denispavlov
 * Date: 26/05/2018
 * Time: 17:10
 */
public class CacheEvictionQueueProcessorImpl implements Runnable, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(CacheEvictionQueueProcessorImpl.class);

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private CacheEvictionQueue cacheEvictionQueue;
    private NodeService nodeService;
    private AsyncContextFactory asyncContextFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {

        int count = 0;

        try {

            CacheEvictionQueue.CacheEvictionItem item;
            while (!this.shutdown.get() && (item = this.cacheEvictionQueue.dequeue()) != null) {

                try {
                    SecurityContextHolder.getContext().setAuthentication(new RunAsUserAuthentication(item.getUser(), "", Collections.EMPTY_LIST));
                    final Map<String, Object> params = new HashMap<>();
                    params.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_CONNECTOR_CACHE_TIMEOUT_MS);
                    final AsyncContext threadContext = asyncContextFactory.getInstance(params);

                    final List<Node> cluster = nodeService.getSfNodes();
                    final List<String> targets = new ArrayList<>();
                    for (final Node node : cluster) {
                        targets.add(node.getId());
                    }

                    final HashMap<String, Object> payload = new HashMap<>();
                    payload.put("entityOperation", item.getOperation());
                    payload.put("entityName", item.getEntityName());
                    payload.put("pkValues", item.getPKs().toArray(new Long[item.getPKs().size()]));

                    final RspMessage message = new ContextRspMessageImpl(
                            nodeService.getCurrentNodeId(),
                            targets,
                            "CacheDirector.onCacheableBulkChange",
                            payload,
                            threadContext
                    );

                    nodeService.broadcast(message);
                    count++;
                } finally {
                    SecurityContextHolder.clearContext();
                }
            }

        } catch (Exception exp) {
            LOG.error("Unable to perform remote cache eviction: " + exp.getMessage(), exp);
        }

        LOG.info("Performed {} cache eviction calls", count);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() throws Exception {
        this.shutdown.set(true);
    }

    /**
     * Spring IoC.
     *
     * @param cacheEvictionQueue cach eviction queue.
     */
    public void setCacheEvictionQueue(final CacheEvictionQueue cacheEvictionQueue) {
        this.cacheEvictionQueue = cacheEvictionQueue;
    }

    /**
     * Spring IoC.
     *
     * @param nodeService node service
     */
    public void setNodeService(final NodeService nodeService) {
        this.nodeService = nodeService;
    }

    /**
     * Spring IoC.
     *
     * @param asyncContextFactory factory
     */
    public void setAsyncContextFactory(final AsyncContextFactory asyncContextFactory) {
        this.asyncContextFactory = asyncContextFactory;
    }

}
