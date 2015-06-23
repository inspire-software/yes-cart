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

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.cluster.node.Message;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.node.RspMessage;
import org.yes.cart.cluster.node.impl.ContextRspMessageImpl;
import org.yes.cart.domain.dto.impl.CacheInfoDTOImpl;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteBackdoorService;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.web.service.ws.BackdoorService;
import org.yes.cart.web.service.ws.CacheDirector;

import java.util.*;

/**
 * User: iazarny@yahoo.com Igor Azarny
 * Date: 4 - feb - 12
 * Time: 5:44 PM
 */
public class RemoteBackdoorServiceImpl implements RemoteBackdoorService {

    private final NodeService nodeService;
    private final BackdoorService localBackdoorService;
    private final CacheDirector localCacheDirector;

    public RemoteBackdoorServiceImpl(final NodeService nodeService,
                                     final BackdoorService localBackdoorService,
                                     final CacheDirector localCacheDirector) {
        this.nodeService = nodeService;
        this.localBackdoorService = localBackdoorService;
        this.localCacheDirector = localCacheDirector;
    }

    /**
     * {@inheritDoc}
     */
    public void warmUp(final AsyncContext context) {

        final Message message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                "WarmUpService.warmUp",
                null,
                context
        );

        nodeService.broadcast(message);

    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Integer> reindexAllProducts(final AsyncContext context) {

        final Map<String, Boolean> indexFinished = context.getAttribute(JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE);
        final Long shopId = context.getAttribute(JobContextKeys.NODE_FULL_PRODUCT_INDEX_SHOP);
        if (indexFinished == null) {
            throw new IllegalArgumentException("Must have [" + JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE + "] attribute [Map<String, Boolean>] in async context");
        }

        final List<Node> cluster = nodeService.getYesNodes();
        final List<String> targets = new ArrayList<String>();
        for (final Node node : cluster) {
            final Boolean finished = indexFinished.get(node.getNodeId()) != null && indexFinished.get(node.getNodeId());
            if (!finished) {
                targets.add(node.getNodeId());
            }
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                (shopId != null && shopId > 0L) ?
                        "BackdoorService.reindexShopProducts" : "BackdoorService.reindexAllProducts",
                shopId,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Integer> indexStatus = new HashMap<String, Integer>();
        for (final Message response : message.getResponses()) {

            indexStatus.put(response.getSource(), (Integer) response.getPayload());

        }
        return indexStatus;

    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Integer> reindexAllProductsSku(final AsyncContext context) {

        final Map<String, Boolean> indexFinished = context.getAttribute(JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE);
        final Long shopId = context.getAttribute(JobContextKeys.NODE_FULL_PRODUCT_INDEX_SHOP);
        if (indexFinished == null) {
            throw new IllegalArgumentException("Must have [" + JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE + "] attribute [Map<String, Boolean>] in async context");
        }

        final List<Node> cluster = nodeService.getYesNodes();
        final List<String> targets = new ArrayList<String>();
        for (final Node node : cluster) {
            final Boolean finished = indexFinished.get(node.getNodeId()) != null && indexFinished.get(node.getNodeId());
            if (!finished) {
                targets.add(node.getNodeId());
            }
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                (shopId != null && shopId > 0L) ?
                        "BackdoorService.reindexShopProductsSku" : "BackdoorService.reindexAllProductsSku",
                shopId,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Integer> indexStatus = new HashMap<String, Integer>();
        for (final Message response : message.getResponses()) {

            indexStatus.put(response.getSource(), (Integer) response.getPayload());

        }
        return indexStatus;

    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Integer> reindexProduct(final AsyncContext context, final long productPk) {

        final List<Node> cluster = nodeService.getYesNodes();
        final List<String> targets = new ArrayList<String>();
        for (final Node node : cluster) {
            targets.add(node.getNodeId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "BackdoorService.reindexProduct",
                productPk,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Integer> indexStatus = new HashMap<String, Integer>();
        for (final Message response : message.getResponses()) {

            indexStatus.put(response.getSource(), (Integer) response.getPayload());

        }
        return indexStatus;

    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Integer> reindexProductSku(final AsyncContext context, final long productPk) {

        final List<Node> cluster = nodeService.getYesNodes();
        final List<String> targets = new ArrayList<String>();
        for (final Node node : cluster) {
            targets.add(node.getNodeId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "BackdoorService.reindexProductSku",
                productPk,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Integer> indexStatus = new HashMap<String, Integer>();
        for (final Message response : message.getResponses()) {

            indexStatus.put(response.getSource(), (Integer) response.getPayload());

        }
        return indexStatus;

    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Integer> reindexProductSkuCode(final AsyncContext context, final String productSkuCode) {

        final List<Node> cluster = nodeService.getYesNodes();
        final List<String> targets = new ArrayList<String>();
        for (final Node node : cluster) {
            targets.add(node.getNodeId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "BackdoorService.reindexProductSkuCode",
                productSkuCode,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Integer> indexStatus = new HashMap<String, Integer>();
        for (final Message response : message.getResponses()) {

            indexStatus.put(response.getSource(), (Integer) response.getPayload());

        }
        return indexStatus;

    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Integer> reindexProducts(final AsyncContext context, final long[] productPks) {

        final List<Node> cluster = nodeService.getYesNodes();
        final List<String> targets = new ArrayList<String>();
        for (final Node node : cluster) {
            targets.add(node.getNodeId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "BackdoorService.reindexProducts",
                productPks,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Integer> indexStatus = new HashMap<String, Integer>();
        for (final Message response : message.getResponses()) {

            indexStatus.put(response.getSource(), (Integer) response.getPayload());

        }
        return indexStatus;

    }

    /**
     * {@inheritDoc}
     */
    public List<Object[]> sqlQuery(final AsyncContext context, final String query, final String node) {

        if (nodeService.getCurrentNodeId().equals(node)) {
            return localBackdoorService.sqlQuery(query);
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                Arrays.asList(node),
                "BackdoorService.sqlQuery",
                query,
                context
        );

        nodeService.broadcast(message);

        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            return (List<Object[]>) message.getResponses().get(0).getPayload();

        }

        return Collections.emptyList();

    }

    /**
     * {@inheritDoc}
     */
    public List<Object[]> hsqlQuery(final AsyncContext context, final String query, final String node) {

        if (nodeService.getCurrentNodeId().equals(node)) {
            return localBackdoorService.hsqlQuery(query);
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                Arrays.asList(node),
                "BackdoorService.hsqlQuery",
                query,
                context
        );

        nodeService.broadcast(message);

        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            return (List<Object[]>) message.getResponses().get(0).getPayload();

        }

        return Collections.emptyList();

    }

    /**
     * {@inheritDoc}
     */
    public List<Object[]> luceneQuery(final AsyncContext context, final String query, final String node) {

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                Arrays.asList(node),
                "BackdoorService.luceneQuery",
                query,
                context
        );

        nodeService.broadcast(message);

        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            return (List<Object[]>) message.getResponses().get(0).getPayload();

        }

        return Collections.emptyList();

    }

    /**
     * {@inheritDoc}
     */
    public Map<String, List<CacheInfoDTOImpl>> getCacheInfo(final AsyncContext context)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<Node> cluster = nodeService.getYesNodes();
        final List<String> targets = new ArrayList<String>();
        for (final Node node : cluster) {
            targets.add(node.getNodeId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "CacheDirector.getCacheInfo",
                null,
                context
        );

        nodeService.broadcast(message);

        final Map<String, List<CacheInfoDTOImpl>> info = new HashMap<String, List<CacheInfoDTOImpl>>();
        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            for (final Message response : message.getResponses()) {

                if (response.getPayload() instanceof List) {
                    info.put(response.getSource(), (List<CacheInfoDTOImpl>) response.getPayload());
                }

            }

        }

        final String yum = nodeService.getCurrentNodeId();
        List<CacheInfoDTOImpl> yumRez = localCacheDirector.getCacheInfo();
        for (final CacheInfoDTOImpl cacheInfoDTO : yumRez) {
            cacheInfoDTO.setNodeId(yum);
        }
        info.put(yum, yumRez);

        return info;
    }


    /**
     * {@inheritDoc}
     */
    public Map<String, Boolean> evictAllCache(final AsyncContext context) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<Node> cluster = nodeService.getYesNodes();
        final List<String> targets = new ArrayList<String>();
        for (final Node node : cluster) {
            targets.add(node.getNodeId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "CacheDirector.evictAllCache",
                null,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Boolean> evicts = new HashMap<String, Boolean>();
        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            for (final Message response : message.getResponses()) {

                evicts.put(response.getSource(), Boolean.TRUE);

            }

        }

        localCacheDirector.evictAllCache();
        evicts.put(nodeService.getCurrentNodeId(), Boolean.TRUE);

        return evicts;

    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Boolean> evictCache(final AsyncContext context, final String name) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<Node> cluster = nodeService.getYesNodes();
        final List<String> targets = new ArrayList<String>();
        for (final Node node : cluster) {
            targets.add(node.getNodeId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "CacheDirector.evictCache",
                name,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Boolean> evicts = new HashMap<String, Boolean>();
        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            for (final Message response : message.getResponses()) {

                evicts.put(response.getSource(), Boolean.TRUE);

            }

        }

        localCacheDirector.evictCache(name);
        evicts.put(nodeService.getCurrentNodeId(), Boolean.TRUE);

        return evicts;

    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Boolean> enableStats(final AsyncContext context, final String name) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<Node> cluster = nodeService.getYesNodes();
        final List<String> targets = new ArrayList<String>();
        for (final Node node : cluster) {
            targets.add(node.getNodeId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "CacheDirector.enableStats",
                name,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Boolean> stats = new HashMap<String, Boolean>();
        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            for (final Message response : message.getResponses()) {

                stats.put(response.getSource(), Boolean.TRUE);

            }

        }

        localCacheDirector.enableStats(name);
        stats.put(nodeService.getCurrentNodeId(), Boolean.TRUE);

        return stats;

    }


    /**
     * {@inheritDoc}
     */
    public Map<String, Boolean> disableStats(final AsyncContext context, final String name) throws UnmappedInterfaceException, UnableToCreateInstanceException {


        final List<Node> cluster = nodeService.getYesNodes();
        final List<String> targets = new ArrayList<String>();
        for (final Node node : cluster) {
            targets.add(node.getNodeId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "CacheDirector.disableStats",
                name,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Boolean> stats = new HashMap<String, Boolean>();
        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            for (final Message response : message.getResponses()) {

                stats.put(response.getSource(), Boolean.TRUE);

            }

        }

        localCacheDirector.disableStats(name);
        stats.put(nodeService.getCurrentNodeId(), Boolean.TRUE);

        return stats;

    }

}
