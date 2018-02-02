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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.cache.CacheBundleHelper;
import org.yes.cart.cluster.node.Message;
import org.yes.cart.cluster.node.MessageListener;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.service.BackdoorService;
import org.yes.cart.cluster.service.WarmUpService;
import org.yes.cart.config.ConfigurationListener;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericFTSCapableDAO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.search.dao.IndexBuilder;
import org.yes.cart.search.query.impl.AsIsAnalyzer;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.util.DateUtils;
import org.yes.cart.utils.impl.ObjectUtil;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/28/12
 * Time: 10:02 AM
 */
public class BackdoorServiceImpl implements BackdoorService {

    private static final long serialVersionUID = 20130820L;

    private static final Logger LOG = LoggerFactory.getLogger(BackdoorServiceImpl.class);

    private static final String INDEX_DONE_STATUS = "DONE";
    private static final String INDEX_RUNNING_STATUS = "RUNNING";
    private static final Object[] INDEX_DISABLED_STATUS = new Object[] { INDEX_DONE_STATUS, 0 };

    private ProductService productService;

    private SystemService systemService;

    private CacheBundleHelper productIndexCaches;

    private WarmUpService warmUpService;

    private NodeService nodeService;

    private List<ConfigurationListener> configurationListeners;

    /*
     * Once a product is reindexed we need to flush all cached information
     * to enforce changes to take immediate effect on the storefront.
     */
    private void flushCache() {

        productIndexCaches.flushBundleCaches();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean ping() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warmUp() {
        warmUpService.warmUp();
    }

    Boolean isLuceneIndexDisabled() {
        return nodeService.getCurrentNode().isFtIndexDisabled();
    }


    /**
     * {@inheritDoc}
     */
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
    public void reindexAllProductsSku() {
        if (!isLuceneIndexDisabled()) {
            productService.reindexProductsSku(getProductIndexBatchSize());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void reindexShopProducts(final long shopPk) {
        if (!isLuceneIndexDisabled()) {
            productService.reindexProducts(shopPk, getProductIndexBatchSize());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void reindexShopProductsSku(final long shopPk) {
        if (!isLuceneIndexDisabled()) {
            productService.reindexProductsSku(shopPk, getProductIndexBatchSize());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void reindexProduct(final long productPk) {
        if (!isLuceneIndexDisabled()) {
            productService.reindexProduct(productPk);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void reindexProductSku(final long productPk) {
        if (!isLuceneIndexDisabled()) {
            productService.reindexProductSku(productPk);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void reindexProductSkuCode(final String productCode) {
        if (!isLuceneIndexDisabled()) {
            productService.reindexProductSku(productCode);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void reindexProducts(final long[] productPks) {
        if (!isLuceneIndexDisabled()) {
            for (long pk : productPks) {
                productService.reindexProduct(pk);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<Object[]> sqlQuery(final String query) {

        if (StringUtils.isNotBlank(query)) {

            if (query.toLowerCase().contains("select ")) {

                return ObjectUtil.transformTypedResultListToArrayList(getGenericDao().executeNativeQuery(query));

            } else {

                return Collections.singletonList(ObjectUtil.escapeXml(getGenericDao().executeNativeUpdate(query)));

            }
        }

        return Collections.EMPTY_LIST;

    }


    /**
     * {@inheritDoc}
     */
    public List<Object[]> hsqlQuery(final String query) {

        if (StringUtils.isNotBlank(query)) {

            if (query.toLowerCase().contains("select ")) {

                final List queryRez = getGenericDao().executeHsqlQuery(query);
                return ObjectUtil.transformTypedResultListToArrayList(queryRez);

            } else {
                return ObjectUtil.transformTypedResultListToArrayList(getGenericDao().executeHsqlQuery(query));
            }
        }
        return Collections.EMPTY_LIST;

    }


    /**
     * {@inheritDoc}
     */
    public List<Object[]> luceneQuery(final String luceneQuery) {

        final QueryParser queryParser = new QueryParser("", new AsIsAnalyzer(false));

        try {

            final Query query = queryParser.parse(luceneQuery);

            return ObjectUtil.transformTypedResultListToArrayList(getGenericDao().fullTextSearch(query));

        } catch (Exception e) {

            final String msg = "Cant parse query : " + luceneQuery + " Error : " + e.getMessage();

            LOG.warn(msg);

            throw new IllegalArgumentException(msg, e);

        }

    }

    /**
     * {@inheritDoc}
     */
    public void reloadConfigurations() {

        if (CollectionUtils.isNotEmpty(this.configurationListeners)) {
            for (final ConfigurationListener listener : this.configurationListeners) {
                listener.reload();
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
        this.nodeService.subscribe("BackdoorService.ping", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                BackdoorServiceImpl.this.ping();
                return "OK";
            }
        });
        this.nodeService.subscribe("BackdoorService.warmUp", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                BackdoorServiceImpl.this.warmUp();
                return "OK";
            }
        });
        this.nodeService.subscribe("BackdoorService.getProductReindexingState", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                return BackdoorServiceImpl.this.getProductReindexingState();
            }
        });
        this.nodeService.subscribe("BackdoorService.getProductSkuReindexingState", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                return BackdoorServiceImpl.this.getProductSkuReindexingState();
            }
        });
        this.nodeService.subscribe("BackdoorService.reindexAllProducts", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                BackdoorServiceImpl.this.reindexAllProducts();
                return "OK";
            }
        });
        this.nodeService.subscribe("BackdoorService.reindexAllProductsSku", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                BackdoorServiceImpl.this.reindexAllProductsSku();
                return "OK";
            }
        });
        this.nodeService.subscribe("BackdoorService.reindexShopProducts", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                BackdoorServiceImpl.this.reindexShopProducts((Long) message.getPayload());
                return "OK";
            }
        });
        this.nodeService.subscribe("BackdoorService.reindexShopProductsSku", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                BackdoorServiceImpl.this.reindexShopProductsSku((Long) message.getPayload());
                return "OK";
            }
        });
        this.nodeService.subscribe("BackdoorService.reindexProduct", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                BackdoorServiceImpl.this.reindexProduct((Long) message.getPayload());
                return "OK";
            }
        });
        this.nodeService.subscribe("BackdoorService.reindexProductSku", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                BackdoorServiceImpl.this.reindexProductSku((Long) message.getPayload());
                return "OK";
            }
        });
        this.nodeService.subscribe("BackdoorService.reindexProductSkuCode", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                BackdoorServiceImpl.this.reindexProductSkuCode((String) message.getPayload());
                return "OK";
            }
        });
        this.nodeService.subscribe("BackdoorService.reindexProducts", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                BackdoorServiceImpl.this.reindexProducts((long[]) message.getPayload());
                return "OK";
            }
        });
        this.nodeService.subscribe("BackdoorService.sqlQuery", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                try {
                    return new ArrayList<Serializable[]>((List) self().sqlQuery((String) message.getPayload()));
                } catch (Exception e) {
                    final String msg = "Cant parse SQL query : " + message.getPayload() + " Error : " + e.getMessage();
                    LOG.warn(msg);
                    return new ArrayList<Serializable[]>(Collections.singletonList(new Serializable[]{e.getMessage()}));
                }
            }
        });
        this.nodeService.subscribe("BackdoorService.hsqlQuery", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                try {
                    return new ArrayList<Serializable[]>((List) self().hsqlQuery((String) message.getPayload()));
                } catch (Exception e) {
                    final String msg = "Cant parse HQL query : " + message.getPayload() + " Error : " + e.getMessage();
                    LOG.warn(msg);
                    return new ArrayList<Serializable[]>(Collections.singletonList(new Serializable[]{e.getMessage()}));
                }
            }
        });
        this.nodeService.subscribe("BackdoorService.luceneQuery", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                try {
                    return new ArrayList<Serializable[]>((List) self().luceneQuery((String) message.getPayload()));
                } catch (Exception e) {
                    final String msg = "Cant parse FT query : " + message.getPayload() + " Error : " + e.getMessage();
                    LOG.warn(msg);
                    return new ArrayList<Serializable[]>(Collections.singletonList(new Serializable[]{e.getMessage()}));
                }
            }
        });
        this.nodeService.subscribe("BackdoorService.reloadConfigurations", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                BackdoorServiceImpl.this.reloadConfigurations();
                return "OK";
            }
        });
    }

    /**
     * IoC. Set warn up service.
     *
     * @param warmUpService warm up service to use.
     */
    public void setWarmUpService(final WarmUpService warmUpService) {
        this.warmUpService = warmUpService;
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


    /**
     * IoC. Set configuration listener.
     *
     * @param configurationListeners configuration listener.
     */
    public void setConfigurationListeners(final List<ConfigurationListener> configurationListeners) {
        this.configurationListeners = configurationListeners;
    }

    @SuppressWarnings("unchecked")
    private GenericFTSCapableDAO<Product, Long, Object> getGenericDao() {
        return (GenericFTSCapableDAO) productService.getGenericDao();
    }

    private int getProductIndexBatchSize() {
        return NumberUtils.toInt(systemService.getAttributeValue(AttributeNamesKeys.System.JOB_REINDEX_PRODUCT_BATCH_SIZE), 100);
    }

    private BackdoorService self;

    private BackdoorService self() {
        if (self == null) {
            self = getSelf();
        }
        return self;
    }

    /**
     * Spring IoC.
     *
     * @return spring look up
     */
    public BackdoorService getSelf() {
        return null;
    }


}
