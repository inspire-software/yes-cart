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

import net.sf.ehcache.CacheManager;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.impl.CacheInfoDTOImpl;
import org.yes.cart.domain.query.impl.AsIsAnalyzer;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.utils.impl.ObjectUtil;
import org.yes.cart.web.service.ws.BackdoorService;

import javax.jws.WebService;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/28/12
 * Time: 10:02 AM
 */
@WebService(endpointInterface = "org.yes.cart.web.service.ws.BackdoorService",
        serviceName = "BackdoorService")
public class BackdoorServiceImpl implements BackdoorService, ApplicationContextAware, ServletContextAware {

    private static final long serialVersionUID = 20120129L;

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private ProductService productService;

    private ApplicationContext applicationContext;

    private ServletContext servletContext;


    /**
     * {@inheritDoc}
     */
    public int reindexAllProducts() {
        return productService.reindexProducts();
    }

    /**
     * {@inheritDoc}
     */
    public int reindexProduct(long productPk) {
        return productService.reindexProduct(productPk);
    }

    /**
     * {@inheritDoc}
     */
    public int reindexProducts(long[] productPks) {
        int rez = 0;
        for (long pk : productPks) {
            rez += productService.reindexProduct(pk);
        }
        return rez;
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
            LOG.warn(msg);
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
            LOG.warn(msg);
            return Collections.singletonList(new Object[]{msg});
        }

    }


    /**
     * {@inheritDoc}
     */
    public List<Object[]> luceneQuery(final String luceneQuery) {

        final QueryParser queryParser = new QueryParser(Version.LUCENE_31, "", new AsIsAnalyzer());

        try {

            final Query query = queryParser.parse(luceneQuery);

            return transformTypedResultListToArrayList(getGenericDao().fullTextSearch(query));

        } catch (Exception e) {

            final String msg = "Cant parse query : " + luceneQuery + " Error : " + e.getMessage();

            LOG.warn(msg);

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
     * IoC. Set product service.
     *
     * @param productService product service to use.
     */
    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    @SuppressWarnings("unchecked")
    private GenericDAO<Object, Long> getGenericDao() {
        return productService.getGenericDao();
    }

    /**
     * {@inheritDoc}
     */
    public List<CacheInfoDTOImpl> getCacheInfo() {
        final CacheManager cm = applicationContext.getBean("cacheManager", CacheManager.class);
        final String[] cacheNames = cm.getCacheNames();
        final List<CacheInfoDTOImpl> rez = new ArrayList<CacheInfoDTOImpl>(cacheNames.length);
        for (String cacheName : cacheNames) {
            final Cache cache = cm.getCache(cacheName);
            rez.add(
                    new CacheInfoDTOImpl(
                            cache.getName(),
                            cache.getSize(),
                            cache.getMemoryStoreSize(),
                            cache.getDiskStoreSize(),
                            0,0/*cache.calculateInMemorySize(),
                            cache.calculateOnDiskSize()*/   /*heavy operation*/
                    )
            );

        }
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    public void evictCache() {
        final CacheManager cm = applicationContext.getBean("cacheManager", CacheManager.class);
        final String[] cacheNames = cm.getCacheNames();
        for (String cacheName : cacheNames) {
            final Cache cache = cm.getCache(cacheName);
            cache.removeAll();
        }
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
