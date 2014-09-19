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
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.dto.impl.CacheInfoDTOImpl;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteBackdoorService;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.web.service.ws.BackdoorService;
import org.yes.cart.web.service.ws.CacheDirector;
import org.yes.cart.web.service.ws.client.WsAbstractFactoryClientFactory;
import org.yes.cart.web.service.ws.client.WsClientFactory;
import org.yes.cart.web.service.ws.node.NodeService;
import org.yes.cart.web.service.ws.node.dto.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: iazarny@yahoo.com Igor Azarny
 * Date: 4 - feb - 12
 * Time: 5:44 PM
 */
public class RemoteBackdoorServiceImpl implements RemoteBackdoorService {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteBackdoorServiceImpl.class);

    private final NodeService nodeService;
    private final BackdoorService localBackdoorService;
    private final CacheDirector localCacheDirector;
    private final WsAbstractFactoryClientFactory wsAbstractFactoryClientFactory;


    public RemoteBackdoorServiceImpl(final NodeService nodeService,
                                     final BackdoorService localBackdoorService,
                                     final CacheDirector localCacheDirector,
                                     final WsAbstractFactoryClientFactory wsAbstractFactoryClientFactory) {
        this.nodeService = nodeService;
        this.localBackdoorService = localBackdoorService;
        this.localCacheDirector = localCacheDirector;
        this.wsAbstractFactoryClientFactory = wsAbstractFactoryClientFactory;
    }

    /**
     * {@inheritDoc}
     */
    public void warmUp(final AsyncContext context) {
        for (final Node yesNode : nodeService.getYesNodes()) {
            try {
                final WsClientFactory<BackdoorService> factory =
                        getBackdoorService(context, yesNode.getBackdoorUri(),
                                AttributeNamesKeys.System.SYSTEM_BACKDOOR_TIMEOUT_MS);

                BackdoorService service = factory.getService();
                try {
                    service.warmUp();
                } finally {
                    factory.release(service);
                    service = null;
                }

            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Cannot warmUp server,  url ["
                            + yesNode.getNodeId() + ":" + yesNode.getBackdoorUri()
                            + "] . Will try next one, if exists",
                            e);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Integer> reindexAllProducts(final AsyncContext context) {
        final Map<String, Boolean> indexFinished = context.getAttribute(JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE);
        if (indexFinished == null) {
            throw new IllegalArgumentException("Must have [" + JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE + "] attribute [Map<String, Boolean>] in async context");
        }
        final Map<String, Integer> indexStatus = new HashMap<String, Integer>();
        for (final Node yesNode : nodeService.getYesNodes()) {
            try {
                final Boolean finished = indexFinished.get(yesNode) != null && indexFinished.get(yesNode);
                if (!finished) {
                    final WsClientFactory<BackdoorService> factory =
                            getBackdoorService(context, yesNode.getBackdoorUri(),
                                    AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_BULK_INDEX_TIMEOUT_MS);

                    BackdoorService service = factory.getService();
                    try {
                        indexStatus.put(yesNode.getNodeId(), service.reindexAllProducts());
                    } finally {
                        factory.release(service);
                        service = null;
                    }

                }
            } catch (Exception e) {
                indexStatus.put(yesNode.getNodeId(), null);
                if (LOG.isErrorEnabled()) {
                    LOG.error("Cannot reindex products,  url ["
                            + yesNode.getNodeId() + ":" + yesNode.getBackdoorUri()
                            + "] . Will try next one, if exists",
                            e);
                }
            }

        }
        return indexStatus;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Integer> reindexProduct(final AsyncContext context, final long productPk) {
        final Map<String, Integer> reindexResult = new HashMap<String, Integer>();
        for (final Node yesNode : nodeService.getYesNodes()) {
            try {
                final WsClientFactory<BackdoorService> factory =
                        getBackdoorService(context, yesNode.getBackdoorUri(),
                                AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_SINGLE_INDEX_TIMEOUT_MS);

                BackdoorService service = factory.getService();
                try {
                    reindexResult.put(yesNode.getNodeId(), service.reindexProduct(productPk));
                } finally {
                    factory.release(service);
                    service = null;
                }

            } catch (Exception e) {
                reindexResult.put(yesNode.getNodeId(), -1);
                if (LOG.isErrorEnabled()) {
                    LOG.error("Cannot reindex product [" + productPk + "],  url ["
                            + yesNode.getNodeId() + ":" + yesNode.getBackdoorUri()
                            + "] . Will try next one, if exists",
                            e);
                }
            }

        }
        return reindexResult;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Integer> reindexProductSku(final AsyncContext context, final long productPk) {
        final Map<String, Integer> reindexResult = new HashMap<String, Integer>();
        for (final Node yesNode : nodeService.getYesNodes()) {
            try {
                final WsClientFactory<BackdoorService> factory =
                        getBackdoorService(context, yesNode.getBackdoorUri(),
                                AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_SINGLE_INDEX_TIMEOUT_MS);

                BackdoorService service = factory.getService();
                try {
                    reindexResult.put(yesNode.getNodeId(), service.reindexProductSku(productPk));
                } finally {
                    factory.release(service);
                    service = null;
                }

            } catch (Exception e) {
                reindexResult.put(yesNode.getNodeId(), null);
                if (LOG.isErrorEnabled()) {
                    LOG.error("Cannot reindex product sku [" + productPk + "],  url ["
                            + yesNode.getNodeId() + ":" + yesNode.getBackdoorUri()
                            + "] . Will try next one, if exists",
                            e);
                }
            }

        }
        return reindexResult;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Integer> reindexProductSkuCode(final AsyncContext context, final String productSkuCode) {
        final Map<String, Integer> reindexResult = new HashMap<String, Integer>();
        for (final Node yesNode : nodeService.getYesNodes()) {
            try {
                final WsClientFactory<BackdoorService> factory =
                        getBackdoorService(context, yesNode.getBackdoorUri(),
                                AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_SINGLE_INDEX_TIMEOUT_MS);

                BackdoorService service = factory.getService();
                try {
                    reindexResult.put(yesNode.getNodeId(), service.reindexProductSkuCode(productSkuCode));
                } finally {
                    factory.release(service);
                    service = null;
                }

            } catch (Exception e) {
                reindexResult.put(yesNode.getNodeId(), null);
                if (LOG.isErrorEnabled()) {
                    LOG.error("Cannot reindex product sku [" + productSkuCode + "],  url ["
                            + yesNode.getNodeId() + ":" + yesNode.getBackdoorUri()
                            + "] . Will try next one, if exists",
                            e);
                }
            }

        }
        return reindexResult;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Integer> reindexProducts(final AsyncContext context, final long[] productPks) {
        final Map<String, Integer> reindexResult = new HashMap<String, Integer>();
        for (final Node yesNode : nodeService.getYesNodes()) {
            try {
                final WsClientFactory<BackdoorService> factory =
                        getBackdoorService(context, yesNode.getBackdoorUri(),
                                AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_SINGLE_INDEX_TIMEOUT_MS);

                BackdoorService service = factory.getService();
                try {
                    reindexResult.put(yesNode.getNodeId(), service.reindexProducts(productPks));
                } finally {
                    factory.release(service);
                    service = null;
                }

            } catch (Exception e) {
                reindexResult.put(yesNode.getNodeId(), null);
                if (LOG.isErrorEnabled()) {
                    LOG.error("Cannot reindex products [" + productPks + "],  url ["
                            + yesNode.getNodeId() + ":" + yesNode.getBackdoorUri()
                            + "] . Will try next one, if exists",
                            e);
                }
            }

        }
        return reindexResult;
    }

    /**
     * {@inheritDoc}
     */
    public List<Object[]> sqlQuery(final AsyncContext context, final String query, final String node) {
        if (nodeService.getCurrentNodeId().equals(node)) {
            return localBackdoorService.sqlQuery(query);
        }
        final WsClientFactory<BackdoorService> factory =
                getBackdoorService(context, getBackdoorUriForNode(node, false),
                        AttributeNamesKeys.System.SYSTEM_BACKDOOR_SQL_TIMEOUT_MS);

        BackdoorService service = factory.getService();
        try {
            return service.sqlQuery(query);
        } finally {
            factory.release(service);
            service = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<Object[]> hsqlQuery(final AsyncContext context, final String query, final String node) {
        if (nodeService.getCurrentNodeId().equals(node)) {
            return localBackdoorService.hsqlQuery(query);
        }
        final WsClientFactory<BackdoorService> factory =
                getBackdoorService(context, getBackdoorUriForNode(node, false),
                        AttributeNamesKeys.System.SYSTEM_BACKDOOR_SQL_TIMEOUT_MS);

        BackdoorService service = factory.getService();
        try {
            return service.hsqlQuery(query);
        } finally {
            factory.release(service);
            service = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<Object[]> luceneQuery(final AsyncContext context, final String query, final String node) {
        final WsClientFactory<BackdoorService> factory =
                getBackdoorService(context, getBackdoorUriForNode(node, true),
                        AttributeNamesKeys.System.SYSTEM_BACKDOOR_SQL_TIMEOUT_MS);

        BackdoorService service = factory.getService();
        try {
            return service.luceneQuery(query);
        } finally {
            factory.release(service);
            service = null;
        }
    }

    private String getBackdoorUriForNode(final String node, final boolean yesOnly) {
        final List<Node> nodes = nodeService.getCluster();
        for (final Node n : nodes) {
            if (n.getNodeId().equals(node)) {
                if (yesOnly && n.isYum()) {
                    throw new IllegalArgumentException("Unable to perform operation on YUM node");
                }
                return n.getBackdoorUri();
            }
        }
        throw new IllegalArgumentException("Unknown node: " + node);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, List<CacheInfoDTOImpl>> getCacheInfo(final AsyncContext context)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Map<String, List<CacheInfoDTOImpl>> info = new HashMap<String, List<CacheInfoDTOImpl>>();

        for (final Node yesNode : nodeService.getYesNodes()) {

            try {

                final List<CacheInfoDTOImpl> rez = new ArrayList<CacheInfoDTOImpl>();

                final WsClientFactory<CacheDirector> factory = getCacheDirector(context, yesNode.getCacheManagerUri());
                CacheDirector cacheDirector = factory.getService();
                List<CacheInfoDTOImpl> shopRez = null;
                try {
                    shopRez = cacheDirector.getCacheInfo();
                } finally {
                    factory.release(cacheDirector);
                    cacheDirector = null;
                }

                for (final CacheInfoDTOImpl cacheInfoDTO : shopRez) {
                    cacheInfoDTO.setNodeId(yesNode.getNodeId());
                    cacheInfoDTO.setNodeUri(yesNode.getCacheManagerUri());
                    rez.add(cacheInfoDTO);
                }

                info.put(yesNode.getNodeId(), rez);

            } catch (Exception e) {

                info.put(yesNode.getNodeId(), null);

                if (LOG.isWarnEnabled()) {
                    LOG.warn("Cannot to get cache info  from url ["
                            + yesNode.getNodeId() + ":" + yesNode.getCacheManagerUri()
                            + "] . Will try next one, if exists",
                            e);

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

        final Map<String, Boolean> evicts = new HashMap<String, Boolean>();
        for (final Node yesNode : nodeService.getYesNodes()) {

            try {

                final WsClientFactory<CacheDirector> factory = getCacheDirector(context, yesNode.getCacheManagerUri());
                CacheDirector cacheDirector = factory.getService();
                try {
                    cacheDirector.evictAllCache();
                } finally {
                    factory.release(cacheDirector);
                    cacheDirector = null;
                }
                evicts.put(yesNode.getNodeId(), Boolean.TRUE);

            } catch (Exception e) {
                evicts.put(yesNode.getNodeId(), Boolean.FALSE);
                if (LOG.isErrorEnabled()) {
                    LOG.error("Cannot evict cache,  url ["
                            + yesNode.getNodeId() + ":" + yesNode.getCacheManagerUri()
                            + "] . Will try next one, if exists",
                            e);

                }

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

        final Map<String, Boolean> evicts = new HashMap<String, Boolean>();
        for (final Node yesNode : nodeService.getYesNodes()) {

            try {

                final WsClientFactory<CacheDirector> factory = getCacheDirector(context, yesNode.getCacheManagerUri());
                CacheDirector cacheDirector = factory.getService();
                try {
                    cacheDirector.evictCache(name);
                } finally {
                    factory.release(cacheDirector);
                    cacheDirector = null;
                }
                evicts.put(yesNode.getNodeId(), Boolean.TRUE);

            } catch (Exception e) {
                evicts.put(yesNode.getNodeId(), Boolean.FALSE);
                if (LOG.isErrorEnabled()) {
                    LOG.error("Cannot evict cache [" + name + "],  url ["
                            + yesNode.getNodeId() + ":" + yesNode.getCacheManagerUri()
                            + "] . Will try next one, if exists",
                            e);

                }

            }
        }

        localCacheDirector.evictCache(name);
        evicts.put(nodeService.getCurrentNodeId(), Boolean.TRUE);

        return evicts;

    }

    private WsClientFactory<BackdoorService> getBackdoorService(final AsyncContext context,
                                                                final String backdoorUrl,
                                                                final String timeoutKey) {


        final String userName = context.getAttribute(AsyncContext.USERNAME);
        final String password = context.getAttribute(AsyncContext.CREDENTIALS);

        final int timeout = Integer.parseInt(nodeService.getConfiguration().get(timeoutKey));

        return wsAbstractFactoryClientFactory.getFactory(BackdoorService.class, userName, password, backdoorUrl, timeout);

    }


    private WsClientFactory<CacheDirector> getCacheDirector(final AsyncContext context,
                                                            final String cacheDirUrl) {

        final String userName = context.getAttribute(AsyncContext.USERNAME);
        final String password = context.getAttribute(AsyncContext.CREDENTIALS);
        final int timeout = Integer.parseInt(nodeService.getConfiguration().get(AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS));

        return wsAbstractFactoryClientFactory.getFactory(CacheDirector.class, userName, password, cacheDirUrl, timeout);

    }


}
