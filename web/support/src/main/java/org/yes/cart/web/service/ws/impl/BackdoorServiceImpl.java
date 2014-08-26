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

package org.yes.cart.web.service.ws.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.springframework.beans.BeansException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.query.impl.AsIsAnalyzer;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.utils.impl.ObjectUtil;
import org.yes.cart.web.service.ws.BackdoorService;
import org.yes.cart.web.service.ws.node.NodeService;
import org.yes.cart.web.service.ws.node.WarmUpService;

import javax.jws.WebService;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/28/12
 * Time: 10:02 AM
 */
@WebService(endpointInterface = "org.yes.cart.web.service.ws.BackdoorService",
        serviceName = "BackdoorService")
public class BackdoorServiceImpl implements BackdoorService, ApplicationContextAware, ServletContextAware {

    private static final long serialVersionUID = 20130820L;

    private ProductService productService;

    private ApplicationContext applicationContext;

    private ServletContext servletContext;

    private CacheManager cacheManager;

    private WarmUpService warmUpService;

    private NodeService nodeService;

    /*
     * Once a product is reindexed we need to flush all cached information
     * to enforce changes to take immediate effect on the storefront.
     */
    private void flushCache() {
        safeFlushCache(cacheManager.getCache("priceService-minimalRegularPrice"));
        safeFlushCache(cacheManager.getCache("priceService-allCurrentPrices"));
        safeFlushCache(cacheManager.getCache("productService-productById"));
        safeFlushCache(cacheManager.getCache("productService-skuById"));
        safeFlushCache(cacheManager.getCache("productService-productBySkuCode"));
        safeFlushCache(cacheManager.getCache("productSkuService-productSkuBySkuCode"));
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
        return Boolean.TRUE.toString().equals(nodeService.getConfiguration().get(NodeService.LUCENE_INDEX_DISABLED));
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
        flushCache();
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

                    return getGenericDao().executeNativeQuery(query);

                } else {

                    return Collections.singletonList(new Object[]{getGenericDao().executeNativeUpdate(query)});

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
                    return transformTypedResultListToArrayList(queryRez);

                } else {
                    return Collections.singletonList(new Object[]{getGenericDao().executeHsqlQuery(query)});
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

            return transformTypedResultListToArrayList(getGenericDao().fullTextSearch(query));

        } catch (Exception e) {

            final String msg = "Cant parse query : " + luceneQuery + " Error : " + e.getMessage();

            ShopCodeContext.getLog(this).warn(msg);

            return Collections.singletonList(new Object[]{msg});

        }

    }

    private List<Object[]> transformTypedResultListToArrayList(List queryRez) {

        final List<Object[]> rezList = new ArrayList<Object[]>(queryRez.size());

        for (Object obj : queryRez) {

            rezList.add(ObjectUtil.toObjectArray(obj));

        }
        return rezList;
    }

    /**
     * IoC. node service
     *
     * @param nodeService node service to use
     */
    public void setNodeService(final NodeService nodeService) {
        this.nodeService = nodeService;
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


    /**
     * {@inheritDoc}
     */
    public String getImageVaultPath() throws IOException {

        /*final File ycsimg = new File(applicationContext.getResource("WEB-INF").getFile().getAbsolutePath()
                + File.separator + ".." + File.separator + ".." + File.separator + "yes-shop"
                + File.separator + "default" + File.separator + "imagevault" + File.separator); */

        final File ycsimg = new File(servletContext.getRealPath("/default/imagevault/"));

        return ycsimg.getAbsolutePath();
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * {@inheritDoc}
     */
    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
