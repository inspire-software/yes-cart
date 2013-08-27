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

import org.yes.cart.domain.dto.impl.CacheInfoDTOImpl;
import org.yes.cart.remote.service.RemoteBackdoorService;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.web.service.ws.BackdoorService;
import org.yes.cart.web.service.ws.CacheDirector;
import org.yes.cart.web.service.ws.client.BackdoorServiceClientFactory;

import java.io.IOException;
import java.util.List;

/**
 * User: iazarny@yahoo.com Igor Azarny
 * Date: 4 - feb - 12
 * Time: 5:44 PM
 */
public class RemoteBackdoorServiceImpl implements RemoteBackdoorService {

    private final static  int defaultTimeout = 60000;


    /** {@inheritDoc} */
    public int reindexAllProducts(final AsyncContext context) {
        return getBackdoorService(context, 300000).reindexAllProducts();
    }

    /** {@inheritDoc} */
    public int reindexProduct(final AsyncContext context, final long productPk) {
        return getBackdoorService(context, defaultTimeout).reindexProduct(productPk);
    }

    /** {@inheritDoc} */
    public int reindexProductSku(final AsyncContext context, final long productPk) {
        return getBackdoorService(context, defaultTimeout).reindexProductSku(productPk);
    }

    /** {@inheritDoc} */
    public int reindexProductSkuCode(final AsyncContext context, final String productSkuCode) {
        return getBackdoorService(context, defaultTimeout).reindexProductSkuCode(productSkuCode);
    }

    /** {@inheritDoc} */
    public int reindexProducts(final AsyncContext context, final long[] productPks) {
        return getBackdoorService(context, defaultTimeout).reindexProducts(productPks);
    }

    /** {@inheritDoc} */
    public List<Object[]> sqlQuery(final AsyncContext context, final String query) {
        return getBackdoorService(context, defaultTimeout).sqlQuery(query);
    }

    /** {@inheritDoc} */
    public List<Object[]> hsqlQuery(final AsyncContext context, final String query) {
        return getBackdoorService(context, defaultTimeout).hsqlQuery(query);
    }

    /** {@inheritDoc} */
    public List<Object[]> luceneQuery(final AsyncContext context, final String query) {
        return getBackdoorService(context, defaultTimeout).luceneQuery(query);
    }

    /** {@inheritDoc} */
    public List<CacheInfoDTOImpl> getCacheInfo(final AsyncContext context) {
        return getCacheDirector(context, defaultTimeout).getCacheInfo();
}

    /** {@inheritDoc} */
    public void evictCache(final AsyncContext context) {
        getCacheDirector(context, defaultTimeout).evictCache();
    }

    /** {@inheritDoc} */
    public String getImageVaultPath(final AsyncContext context) throws IOException {
        return getBackdoorService(context, defaultTimeout).getImageVaultPath();
    }

    private BackdoorServiceClientFactory backdoorServiceClientFactory = null;

    private synchronized BackdoorServiceClientFactory getBackdoorServiceClientFactory() {

        if (backdoorServiceClientFactory == null) {
            backdoorServiceClientFactory = new BackdoorServiceClientFactory();
        }

        return backdoorServiceClientFactory;

    }

    /**
     * Get actual remote service.
     *
     * @param context web service context.
     * @param timeout  timeout for operation.
     * @return  {@BackdoorService}
     */
    private  BackdoorService getBackdoorService(final AsyncContext context, final long timeout) {


        String userName = context.getAttribute(AsyncContext.USERNAME);
        String password = context.getAttribute(AsyncContext.CREDENTIALS);
        String uri = context.getAttribute(AsyncContext.WEB_SERVICE_URI);

        return getBackdoorServiceClientFactory().getBackdoorService(
                userName,
                password,
                uri, timeout);  //TODO: YC-149 move timeouts to config

    }


    /**
     * Get actual remote service.
     *
     * @param context web service context.
     * @param timeout  timeout for operation.
     * @return  {@BackdoorService}
     */
    private CacheDirector getCacheDirector(final AsyncContext context, final long timeout) {


        String userName = context.getAttribute(AsyncContext.USERNAME);
        String password = context.getAttribute(AsyncContext.CREDENTIALS);
        String uri = context.getAttribute(AsyncContext.WEB_SERVICE_URI);

        return getBackdoorServiceClientFactory().getCacheDirector(
                userName,
                password,
                uri, timeout);  //TODO: YC-149 move timeouts to config

    }


}
