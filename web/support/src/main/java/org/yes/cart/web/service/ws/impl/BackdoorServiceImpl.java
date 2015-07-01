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

package org.yes.cart.web.service.ws.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.yes.cart.cluster.node.Message;
import org.yes.cart.cluster.node.MessageListener;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.service.WarmUpService;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.query.impl.AsIsAnalyzer;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.utils.impl.ObjectUtil;
import org.yes.cart.web.service.ws.BackdoorService;

import java.io.Serializable;
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

    private ProductService productService;

    private CacheManager cacheManager;

    private WarmUpService warmUpService;

    private NodeService nodeService;

    /*
     * Once a product is reindexed we need to flush all cached information
     * to enforce changes to take immediate effect on the storefront.
     */
    private void flushCache() {
        safeFlushCache(cacheManager.getCache("priceService-minimalPrice"));
        safeFlushCache(cacheManager.getCache("priceService-allCurrentPrices"));
        safeFlushCache(cacheManager.getCache("priceService-allPrices"));
        safeFlushCache(cacheManager.getCache("productService-productById"));
        safeFlushCache(cacheManager.getCache("productService-skuById"));
        safeFlushCache(cacheManager.getCache("productService-productBySkuCode"));
        safeFlushCache(cacheManager.getCache("productSkuService-productSkuBySkuCode"));
        safeFlushCache(cacheManager.getCache("skuWarehouseService-productSkusOnWarehouse"));
        safeFlushCache(cacheManager.getCache("skuWarehouseService-productOnWarehouse"));
        safeFlushCache(cacheManager.getCache("web.decoratorFacade-decorate"));
        safeFlushCache(cacheManager.getCache("web.bookmarkService-seoProductDecode"));
        safeFlushCache(cacheManager.getCache("web.bookmarkService-seoProductEncode"));
        safeFlushCache(cacheManager.getCache("web.bookmarkService-seoSkuDecode"));
        safeFlushCache(cacheManager.getCache("web.bookmarkService-seoSkuEncode"));
    }

    private void safeFlushCache(final Cache cache) {

        if(cache != null) {
            cache.clear();
        }

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
        return nodeService.getCurrentNode().isLuceneIndexDisabled();
    }

    /**
     * {@inheritDoc}
     */
    public int reindexAllProducts() {
        final int count;
        if (isLuceneIndexDisabled()) {
            count = -1; // signifies job's done
        } else {
            count = productService.reindexProducts();
        }
        if (count == -1) {
            flushCache();
        }
        return count;
    }

    /**
     * {@inheritDoc}
     */
    public int reindexAllProductsSku() {
        final int count;
        if (isLuceneIndexDisabled()) {
            count = -1; // signifies job's done
        } else {
            count = productService.reindexProductsSku();
        }
        if (count == -1) {
            flushCache();
        }
        return count;
    }

    /**
     * {@inheritDoc}
     */
    public int reindexShopProducts(final long shopPk) {
        final int count;
        if (isLuceneIndexDisabled()) {
            count = -1; // signifies job's done
        } else {
            count = productService.reindexProducts(shopPk);
        }
        if (count == -1) {
            flushCache();
        }
        return count;
    }

    /**
     * {@inheritDoc}
     */
    public int reindexShopProductsSku(final long shopPk) {
        final int count;
        if (isLuceneIndexDisabled()) {
            count = -1; // signifies job's done
        } else {
            count = productService.reindexProductsSku(shopPk);
        }
        if (count == -1) {
            flushCache();
        }
        return count;
    }

    /**
     * {@inheritDoc}
     */
    public int reindexProduct(final long productPk) {
        final int count;
        if (isLuceneIndexDisabled()) {
            count = 0;
        } else {
            count = productService.reindexProduct(productPk);
        }
        flushCache();
        return count;
    }

    /**
     * {@inheritDoc}
     */
    public int reindexProductSku(final long productPk) {
        final int count;
        if (isLuceneIndexDisabled()) {
            count = 0;
        } else {
            count = productService.reindexProductSku(productPk);
        }
        flushCache();
        return count;
    }

    /**
     * {@inheritDoc}
     */
    public int reindexProductSkuCode(final String productCode) {
        final int count;
        if (isLuceneIndexDisabled()) {
            count = 0;
        } else {
            count = productService.reindexProductSku(productCode);
        }
        flushCache();
        return count;
    }

    /**
     * {@inheritDoc}
     */
    public int reindexProducts(final long[] productPks) {
        final int count;
        if (isLuceneIndexDisabled()) {
            count = 0;
        } else {
            int rez = 0;
            for (long pk : productPks) {
                rez += productService.reindexProduct(pk);
            }
            count = rez;
        }
        flushCache();
        return count;
    }

    /**
     * {@inheritDoc}
     */
    public List<Object[]> sqlQuery(final String query) {

        try {

            if (StringUtils.isNotBlank(query)) {

                if (query.toLowerCase().contains("select ")) {

                    return ObjectUtil.transformTypedResultListToArrayList(getGenericDao().executeNativeQuery(query));

                } else {

                    return Collections.singletonList(ObjectUtil.escapeXml(getGenericDao().executeNativeUpdate(query)));

                }
            }

            return Collections.EMPTY_LIST;

        } catch (Exception e) {
            final String msg = "Cant parse query : " + query + " Error : " + e.getMessage();
            ShopCodeContext.getLog(this).warn(msg);
            return Collections.singletonList(new Object[]{msg});
        }

    }


    /**
     * {@inheritDoc}
     */
    public List<Object[]> hsqlQuery(final String query) {
        try {

            if (StringUtils.isNotBlank(query)) {

                if (query.toLowerCase().contains("select ")) {

                    final List queryRez = getGenericDao().executeHsqlQuery(query);
                    return ObjectUtil.transformTypedResultListToArrayList(queryRez);

                } else {
                    return ObjectUtil.transformTypedResultListToArrayList(getGenericDao().executeHsqlQuery(query));
                }
            }
            return Collections.EMPTY_LIST;
        } catch (Exception e) {
            final String msg = "Cant parse query : " + query + " Error : " + e.getMessage();
            ShopCodeContext.getLog(this).warn(msg);
            return Collections.singletonList(new Object[]{msg});
        }

    }


    /**
     * {@inheritDoc}
     */
    public List<Object[]> luceneQuery(final String luceneQuery) {

        final QueryParser queryParser = new QueryParser(Version.LUCENE_31, "", new AsIsAnalyzer(false));

        try {

            final Query query = queryParser.parse(luceneQuery);

            return ObjectUtil.transformTypedResultListToArrayList(getGenericDao().fullTextSearch(query));

        } catch (Exception e) {

            final String msg = "Cant parse query : " + luceneQuery + " Error : " + e.getMessage();

            ShopCodeContext.getLog(this).warn(msg);

            return Collections.singletonList(new Object[]{msg});

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
        this.nodeService.subscribe("BackdoorService.reindexAllProducts", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                return BackdoorServiceImpl.this.reindexAllProducts();
            }
        });
        this.nodeService.subscribe("BackdoorService.reindexAllProductsSku", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                return BackdoorServiceImpl.this.reindexAllProductsSku();
            }
        });
        this.nodeService.subscribe("BackdoorService.reindexShopProducts", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                return BackdoorServiceImpl.this.reindexShopProducts((Long) message.getPayload());
            }
        });
        this.nodeService.subscribe("BackdoorService.reindexShopProductsSku", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                return BackdoorServiceImpl.this.reindexShopProductsSku((Long) message.getPayload());
            }
        });
        this.nodeService.subscribe("BackdoorService.reindexProduct", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                return BackdoorServiceImpl.this.reindexProduct((Long) message.getPayload());
            }
        });
        this.nodeService.subscribe("BackdoorService.reindexProductSku", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                return BackdoorServiceImpl.this.reindexProductSku((Long) message.getPayload());
            }
        });
        this.nodeService.subscribe("BackdoorService.reindexProductSkuCode", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                return BackdoorServiceImpl.this.reindexProductSkuCode((String) message.getPayload());
            }
        });
        this.nodeService.subscribe("BackdoorService.reindexProducts", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                return BackdoorServiceImpl.this.reindexProducts((long[]) message.getPayload());
            }
        });
        this.nodeService.subscribe("BackdoorService.sqlQuery", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                return new ArrayList<Serializable[]>((List) self().sqlQuery((String) message.getPayload()));
            }
        });
        this.nodeService.subscribe("BackdoorService.hsqlQuery", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                return new ArrayList<Serializable[]>((List) self().hsqlQuery((String) message.getPayload()));
            }
        });
        this.nodeService.subscribe("BackdoorService.luceneQuery", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                return new ArrayList<Serializable[]>((List) self().luceneQuery((String) message.getPayload()));
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
     * IoC. Set cache manager service.
     *
     * @param cacheManager cache manager
     */
    public void setCacheManager(final CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @SuppressWarnings("unchecked")
    private GenericDAO<Product, Long> getGenericDao() {
        return productService.getGenericDao();
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
