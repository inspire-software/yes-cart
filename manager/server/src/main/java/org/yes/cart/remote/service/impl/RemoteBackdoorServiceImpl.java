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
import org.yes.cart.domain.dto.impl.CacheInfoDTOImpl;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteBackdoorService;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.web.service.ws.BackdoorService;
import org.yes.cart.web.service.ws.CacheDirector;
import org.yes.cart.web.service.ws.client.AsyncFlexContextImpl;
import org.yes.cart.web.service.ws.client.BackdoorServiceClientFactory;
import org.yes.cart.web.service.ws.client.CacheDirectorClientFactory;

import java.io.IOException;
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

    private final static int defaultTimeout = 60000;

    private final List<String> cacheDirectorUrl;


    /**
     * Construct remote service to manage shop.
     *
     * @param cacheDirectorUrl  urls of cache dir
     */
    public RemoteBackdoorServiceImpl(List<String> cacheDirectorUrl) {
        this.cacheDirectorUrl = cacheDirectorUrl;
    }


    /**
     * {@inheritDoc}
     */
    public int reindexAllProducts(final AsyncContext context) {
        return getBackdoorService(context, 300000).reindexAllProducts();
    }

    /**
     * {@inheritDoc}
     */
    public int reindexProduct(final AsyncContext context, final long productPk) {
        return getBackdoorService(context, defaultTimeout).reindexProduct(productPk);
    }

    /**
     * {@inheritDoc}
     */
    public int reindexProductSku(final AsyncContext context, final long productPk) {
        return getBackdoorService(context, defaultTimeout).reindexProductSku(productPk);
    }

    /**
     * {@inheritDoc}
     */
    public int reindexProductSkuCode(final AsyncContext context, final String productSkuCode) {
        return getBackdoorService(context, defaultTimeout).reindexProductSkuCode(productSkuCode);
    }

    /**
     * {@inheritDoc}
     */
    public int reindexProducts(final AsyncContext context, final long[] productPks) {
        return getBackdoorService(context, defaultTimeout).reindexProducts(productPks);
    }

    /**
     * {@inheritDoc}
     */
    public List<Object[]> sqlQuery(final AsyncContext context, final String query) {
        return getBackdoorService(context, defaultTimeout).sqlQuery(query);
    }

    /**
     * {@inheritDoc}
     */
    public List<Object[]> hsqlQuery(final AsyncContext context, final String query) {
        return getBackdoorService(context, defaultTimeout).hsqlQuery(query);
    }

    /**
     * {@inheritDoc}
     */
    public List<Object[]> luceneQuery(final AsyncContext context, final String query) {
        return getBackdoorService(context, defaultTimeout).luceneQuery(query);
    }

    /**
     * {@inheritDoc}
     */
    public List<CacheInfoDTOImpl> getCacheInfo(final AsyncContext context)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<CacheInfoDTOImpl> rez = new ArrayList<CacheInfoDTOImpl>();

        for (String url : cacheDirectorUrl) {

            try {

                final CacheDirector cacheDirector = getCacheDirector(context, url);

                final List<CacheInfoDTOImpl> shopRez = cacheDirector.getCacheInfo();

                rez.addAll(shopRez); //todo shop code

            } catch (Exception e) {

                if (LOG.isWarnEnabled()) {
                    LOG.warn("Cannot to get cache info  from url ["
                            + url
                            + "] . Will try next one, if exists",
                            e);

                }


            }

        }



        return rez;
    }


    /**
     * {@inheritDoc}
     */
    public void evictCache(final AsyncContext context) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        for (String url : cacheDirectorUrl) {

            try {

                final CacheDirector cacheDirector = getCacheDirector(context, url);

                cacheDirector.evictCache();

            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Cannot evict cache,  url ["
                            + url
                            + "] . Will try next one, if exists",
                            e);

                }

            }
        }


    }

    private CacheDirector getCacheDirector(final AsyncContext context, final String cacheDirUrl) {
        final Map<String, Object> ctxAttr = new HashMap<String,Object>(context.getAttributes());
        ctxAttr.put(AsyncContext.WEB_SERVICE_URI, cacheDirUrl);
        final AsyncContext newCtx = new AsyncFlexContextImpl(ctxAttr);
        return getCacheDirector(newCtx, defaultTimeout);
    }

    /**
     * Get actual remote service.
     *
     * @param context web service context.
     * @param timeout timeout for operation.
     * @return {@BackdoorService}
     */
    private CacheDirector getCacheDirector(final AsyncContext context, final long timeout) {


        String userName = context.getAttribute(AsyncContext.USERNAME);
        String password = context.getAttribute(AsyncContext.CREDENTIALS);
        String uri = context.getAttribute(AsyncContext.WEB_SERVICE_URI);

        return getCacheDirectorClientFactory().getCacheDirector(
                userName,
                password,
                uri, timeout);  //TODO: YC-149 move timeouts to config

    }



    /**
     * {@inheritDoc}
     */
    public String getImageVaultPath(final AsyncContext context) throws IOException {
        return getBackdoorService(context, defaultTimeout).getImageVaultPath();
    }

    private BackdoorServiceClientFactory backdoorServiceClientFactory = null;
    private CacheDirectorClientFactory cacheDirectorClientFactory = null;

    private synchronized BackdoorServiceClientFactory getBackdoorServiceClientFactory() {
        if (backdoorServiceClientFactory == null) {
            backdoorServiceClientFactory = new BackdoorServiceClientFactory();
        }
        return backdoorServiceClientFactory;
    }

    private synchronized CacheDirectorClientFactory getCacheDirectorClientFactory() {
        if (cacheDirectorClientFactory == null) {
            cacheDirectorClientFactory = new CacheDirectorClientFactory();
        }
        return cacheDirectorClientFactory;
    }

    /**
     * Get actual remote service.
     *
     * @param context web service context.
     * @param timeout timeout for operation.
     * @return {@BackdoorService}
     */
    private BackdoorService getBackdoorService(final AsyncContext context, final long timeout) {


        String userName = context.getAttribute(AsyncContext.USERNAME);
        String password = context.getAttribute(AsyncContext.CREDENTIALS);
        String uri = context.getAttribute(AsyncContext.WEB_SERVICE_URI);

        return getBackdoorServiceClientFactory().getBackdoorService(
                userName,
                password,
                uri, timeout);  //TODO: YC-149 move timeouts to config

    }




}
