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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.cluster.node.Message;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.node.RspMessage;
import org.yes.cart.cluster.node.impl.ContextRspMessageImpl;
import org.yes.cart.cluster.service.*;
import org.yes.cart.domain.dto.impl.CacheInfoDTO;
import org.yes.cart.domain.dto.impl.ConfigurationDTO;
import org.yes.cart.domain.dto.impl.ModuleDTO;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.service.cluster.ClusterService;

import java.util.*;

/**
 * User: denispavlov
 */
public class ClusterServiceImpl implements ClusterService {

    private static final Logger LOG = LoggerFactory.getLogger(ClusterServiceImpl.class);

    private final NodeService nodeService;
    private final BackdoorService localBackdoorService;
    private final CacheDirector localCacheDirector;
    private final CacheEvictionQueue cacheEvictionQueue;
    private final AlertDirector localAlertDirector;
    private final ModuleDirector localModuleDirector;

    public ClusterServiceImpl(final NodeService nodeService,
                              final BackdoorService localBackdoorService,
                              final CacheDirector localCacheDirector,
                              final CacheEvictionQueue cacheEvictionQueue,
                              final AlertDirector localAlertDirector,
                              final ModuleDirector localModuleDirector) {
        this.nodeService = nodeService;
        this.localBackdoorService = localBackdoorService;
        this.localCacheDirector = localCacheDirector;
        this.cacheEvictionQueue = cacheEvictionQueue;
        this.localAlertDirector = localAlertDirector;
        this.localModuleDirector = localModuleDirector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Node> getClusterInfo(final AsyncContext context) {

        final Message message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                "BackdoorService.ping",
                null,
                context
        );

        // Broadcasting ping message should result in refreshing of the cluster view
        nodeService.broadcast(message);

        return nodeService.getCluster();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Node> getBlacklistedInfo(final AsyncContext context) {

        return nodeService.getBlacklisted();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ModuleDTO> getModuleInfo(final AsyncContext context, final String node) {

        if (nodeService.getCurrentNodeId().equals(node)) {
            return localModuleDirector.getModules();
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                Collections.singletonList(node),
                "ModuleDirector.getModules",
                null,
                context
        );

        nodeService.broadcast(message);

        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            return (List<ModuleDTO>) message.getResponses().get(0).getPayload();

        }

        return Collections.emptyList();


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<ConfigurationDTO>> getConfigurationInfo(final AsyncContext context) {

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                determineAllSfTargets(),
                "ModuleDirector.getConfigurations",
                null,
                context
        );

        nodeService.broadcast(message);

        final Map<String, List<ConfigurationDTO>> info = new HashMap<>();
        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            for (final Message response : message.getResponses()) {

                if (response.getPayload() instanceof List) {
                    info.put(response.getSource(), (List<ConfigurationDTO>) response.getPayload());
                }

            }

        }

        final String admin = nodeService.getCurrentNodeId();
        List<ConfigurationDTO> adminRez = localModuleDirector.getConfigurations();
        for (final ConfigurationDTO configurationDTO : adminRez) {
            configurationDTO.setNodeId(admin);
        }

        info.put(admin, adminRez);

        return info;


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warmUp(final AsyncContext context) {

        final Message message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                "WarmUpService.warmUp",
                null,
                context
        );

        nodeService.broadcast(message);

    }

    private List<String> determineIndexCapableTargets() {
        final List<Node> cluster = nodeService.getSfNodes();
        final List<String> targets = new ArrayList<>();
        for (final Node node : cluster) {
            if (!node.isFtIndexDisabled()) {
                targets.add(node.getId());
            }
        }
        return targets;
    }


    private List<String> determineAllSfTargets() {
        final List<Node> cluster = nodeService.getSfNodes();
        final List<String> targets = new ArrayList<>();
        for (final Node node : cluster) {
            targets.add(node.getId());
        }
        return targets;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Pair<Long, Boolean>> getProductReindexingState(final AsyncContext context) {

        final Map<String, Boolean> indexFinished = context.getAttribute(JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE);
        if (indexFinished == null) {
            throw new IllegalArgumentException("Must have [" + JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE + "] attribute [Map<String, Boolean>] in async context");
        }

        final List<String> targets = new ArrayList<>();
        for (final String prospectiveTarget : determineIndexCapableTargets()) {
            final Boolean finished = indexFinished.get(prospectiveTarget) != null && indexFinished.get(prospectiveTarget);
            if (!finished) {
                targets.add(prospectiveTarget);
            }
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "BackdoorService.getProductReindexingState",
                null,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Pair<Long, Boolean>> indexStatus = new HashMap<>();
        for (final Message response : message.getResponses()) {

            final Object[] rsp = (Object[]) response.getPayload();
            indexStatus.put(
                    response.getSource(),
                    new Pair<>(Long.valueOf(ObjectUtils.defaultIfNull(rsp[1], "0").toString()), "DONE".equals(rsp[0])));

        }
        return indexStatus;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Pair<Long, Boolean>> getProductSkuReindexingState(final AsyncContext context) {

        final Map<String, Boolean> indexFinished = context.getAttribute(JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE);
        final Long shopId = context.getAttribute(JobContextKeys.NODE_FULL_PRODUCT_INDEX_SHOP);
        if (indexFinished == null) {
            throw new IllegalArgumentException("Must have [" + JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE + "] attribute [Map<String, Boolean>] in async context");
        }

        final List<String> targets = new ArrayList<>();
        for (final String prospectiveTarget : determineIndexCapableTargets()) {
            final Boolean finished = indexFinished.get(prospectiveTarget) != null && indexFinished.get(prospectiveTarget);
            if (!finished) {
                targets.add(prospectiveTarget);
            }
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "BackdoorService.getProductSkuReindexingState",
                shopId,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Pair<Long, Boolean>> indexStatus = new HashMap<>();
        for (final Message response : message.getResponses()) {

            final Object[] rsp = (Object[]) response.getPayload();
            indexStatus.put(response.getSource(),
                    new Pair<>(Long.valueOf(ObjectUtils.defaultIfNull(rsp[1], "0").toString()), "DONE".equals(rsp[0])));

        }
        return indexStatus;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexAllProducts(final AsyncContext context) {

        final Long shopId = context.getAttribute(JobContextKeys.NODE_FULL_PRODUCT_INDEX_SHOP);

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                determineIndexCapableTargets(),
                (shopId != null && shopId > 0L) ?
                        "BackdoorService.reindexShopProducts" : "BackdoorService.reindexAllProducts",
                shopId,
                context
        );

        nodeService.broadcast(message);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexAllProductsSku(final AsyncContext context) {

        final Long shopId = context.getAttribute(JobContextKeys.NODE_FULL_PRODUCT_INDEX_SHOP);

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                determineIndexCapableTargets(),
                (shopId != null && shopId > 0L) ?
                        "BackdoorService.reindexShopProductsSku" : "BackdoorService.reindexAllProductsSku",
                shopId,
                context
        );

        nodeService.broadcast(message);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProduct(final AsyncContext context, final long productPk) {

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                determineIndexCapableTargets(),
                "BackdoorService.reindexProduct",
                productPk,
                context
        );

        nodeService.broadcast(message);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProductSku(final AsyncContext context, final long productPk) {

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                determineIndexCapableTargets(),
                "BackdoorService.reindexProductSku",
                productPk,
                context
        );

        nodeService.broadcast(message);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProductSkuCode(final AsyncContext context, final String productSkuCode) {

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                determineIndexCapableTargets(),
                "BackdoorService.reindexProductSkuCode",
                productSkuCode,
                context
        );

        nodeService.broadcast(message);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProducts(final AsyncContext context, final long[] productPks) {

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                determineIndexCapableTargets(),
                "BackdoorService.reindexProducts",
                productPks,
                context
        );

        nodeService.broadcast(message);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object[]> sqlQuery(final AsyncContext context, final String query, final String node) {

        if (nodeService.getCurrentNodeId().equals(node)) {
            try {
                return localBackdoorService.sqlQuery(query);
            } catch (Exception e) {
                final String msg = "Cant parse SQL query : " + query + " Error : " + e.getMessage();
                LOG.warn(msg);
                return new ArrayList<>(Collections.singletonList(new Object[]{e.getMessage()}));
            }
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                Collections.singletonList(node),
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
    @Override
    public List<Object[]> hsqlQuery(final AsyncContext context, final String query, final String node) {

        if (nodeService.getCurrentNodeId().equals(node)) {
            try {
                return localBackdoorService.hsqlQuery(query);
            } catch (Exception e) {
                final String msg = "Cant parse HQL query : " + query + " Error : " + e.getMessage();
                LOG.warn(msg);
                return new ArrayList<>(Collections.singletonList(new Object[]{e.getMessage()}));
            }
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                Collections.singletonList(node),
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
    @Override
    public List<Object[]> ftQuery(final AsyncContext context, final String query, final String node) {

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                Collections.singletonList(node),
                "BackdoorService.ftQuery",
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
    @Override
    public void reloadConfigurations(final AsyncContext context) {

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                determineAllSfTargets(),
                "BackdoorService.reloadConfigurations",
                null,
                context
        );

        nodeService.broadcast(message);

        localBackdoorService.reloadConfigurations();

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<CacheInfoDTO>> getCacheInfo(final AsyncContext context) {

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                determineAllSfTargets(),
                "CacheDirector.getCacheInfo",
                null,
                context
        );

        nodeService.broadcast(message);

        final Map<String, List<CacheInfoDTO>> info = new HashMap<>();
        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            for (final Message response : message.getResponses()) {

                if (response.getPayload() instanceof List) {
                    info.put(response.getSource(), (List<CacheInfoDTO>) response.getPayload());
                }

            }

        }

        final String admin = nodeService.getCurrentNodeId();
        List<CacheInfoDTO> adminRez = localCacheDirector.getCacheInfo();
        for (final CacheInfoDTO cacheInfoDTO : adminRez) {
            cacheInfoDTO.setNodeId(admin);
        }
        info.put(admin, adminRez);

        return info;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Boolean> evictAllCache(final AsyncContext context) {

        final Boolean force = context.getAttribute("force");
        final boolean doForcefully = force != null && force;

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                determineAllSfTargets(),
                "CacheDirector.evictAllCache",
                doForcefully,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Boolean> evicts = new HashMap<>();
        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            for (final Message response : message.getResponses()) {

                evicts.put(response.getSource(), Boolean.TRUE);

            }

        }

        // Clear any pending cache evictions because we already cleared all cache
        cacheEvictionQueue.clear();

        localCacheDirector.evictAllCache(doForcefully);
        evicts.put(nodeService.getCurrentNodeId(), Boolean.TRUE);

        return evicts;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Boolean> evictCache(final AsyncContext context, final String name) {

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                determineAllSfTargets(),
                "CacheDirector.evictCache",
                name,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Boolean> evicts = new HashMap<>();
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
    @Override
    public Map<String, Boolean> enableStats(final AsyncContext context, final String name) {

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                determineAllSfTargets(),
                "CacheDirector.enableStats",
                name,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Boolean> stats = new HashMap<>();
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
    @Override
    public Map<String, Boolean> disableStats(final AsyncContext context, final String name) {

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                determineAllSfTargets(),
                "CacheDirector.disableStats",
                name,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Boolean> stats = new HashMap<>();
        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            for (final Message response : message.getResponses()) {

                stats.put(response.getSource(), Boolean.TRUE);

            }

        }

        localCacheDirector.disableStats(name);
        stats.put(nodeService.getCurrentNodeId(), Boolean.TRUE);

        return stats;

    }

    @Override
    public List<Pair<String, String>> getAlerts(final AsyncContext context) {

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                determineAllSfTargets(),
                "AlertDirector.getAlerts",
                null,
                context
        );

        nodeService.broadcast(message);

        final List<Pair<String, String>> alerts = new ArrayList<>(150);
        alerts.addAll(localAlertDirector.getAlerts());
        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            for (final Message response : message.getResponses()) {

                alerts.addAll((List) response.getPayload());

            }

        }

        return alerts;

    }
}
