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
import org.yes.cart.domain.dto.ShopBackdoorUrlDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.impl.CacheInfoDTOImpl;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteBackdoorService;
import org.yes.cart.remote.service.RemoteDevService;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.dto.DtoShopBackdoorUrlService;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.web.service.ws.client.AsyncFlexContextImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 12-11-28
 * Time: 3:10 PM
 */
public class RemoteDevServiceImpl implements RemoteDevService {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteDevServiceImpl.class);

    private final RemoteBackdoorService remoteBackdoorService;

    private final DtoShopService dtoShopService;

    private final DtoShopBackdoorUrlService dtoShopBackdoorUrlService;



    public RemoteDevServiceImpl(final RemoteBackdoorService remoteBackdoorService,
                                final DtoShopService dtoShopService,
                                final DtoShopBackdoorUrlService dtoShopBackdoorUrlService) {
        this.remoteBackdoorService = remoteBackdoorService;
        this.dtoShopService = dtoShopService;
        this.dtoShopBackdoorUrlService = dtoShopBackdoorUrlService;
    }

    /** {@inheritDoc} */
    public List<Object[]> sqlQuery(final String query) {
        return remoteBackdoorService.sqlQuery(createCtx(), query);
    }

    /** {@inheritDoc} */
    public List<Object[]> hsqlQuery(final String query) {
        return remoteBackdoorService.hsqlQuery(createCtx(), query);
    }

    /** {@inheritDoc} */
    public List<Object[]> luceneQuery(final String query) {
        return remoteBackdoorService.luceneQuery(createCtx(), query);
    }

    /** {@inheritDoc} */
    public List<CacheInfoDTOImpl> getCacheInfo() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return remoteBackdoorService.getCacheInfo(createCtx());
    }

    /** {@inheritDoc} */
    public void evictCache() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        remoteBackdoorService.evictCache(createCtx());
    }

    private AsyncContext createCtx() {

        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(
                AsyncContext.WEB_SERVICE_URI,
                getAdminUrl(
                        null,
                        RemoteDevService.URL_TYPE_REINDEX_DOOR)
                );

        final AsyncContext flex = new AsyncFlexContextImpl(param);

        return flex;
    }


    /**
     * GEt admin url for given shop.
     * @param shopCode optional shop
     * @param urlType url type - atm backdoor or cache director.
     * @return fully composed url if found.
     */
    String getAdminUrl(final String  shopCode, final String urlType) {

        try {

            final List<ShopDTO> allShops = dtoShopService.getAll();

            final Map<Pair<String, String>, List<CacheInfoDTOImpl>> rez =
                    new HashMap<Pair<String, String>, List<CacheInfoDTOImpl>>(allShops.size());

            for (ShopDTO shop : allShops) {

                if (shopCode == null ||  shop.getCode().equals(shopCode) ) {

                    final List<ShopBackdoorUrlDTO> urls = this.dtoShopBackdoorUrlService.getAllByShopId(
                            shop.getShopId());

                    for (ShopBackdoorUrlDTO url : urls) {

                        String urlBase = url.getUrl();

                        if (RemoteDevService.URL_TYPE_CACHE_DIRECTOR.equals(urlType)) {

                            return urlBase + "/services/cachedirector";

                        } else if (RemoteDevService.URL_TYPE_REINDEX_DOOR.equals(urlType)) {

                            return urlBase + "/services/backdoor";

                        } else {

                            throw new Exception("Type is incorrect expect "
                                    + RemoteDevService.URL_TYPE_CACHE_DIRECTOR
                                    + " or "
                                    + RemoteDevService.URL_TYPE_REINDEX_DOOR);

                        }
                    }
                }
            }


        } catch (Exception e) {

            if (LOG.isErrorEnabled()) {

                LOG.error("Cannot gate admin url for [" + shopCode + "] type is [" + urlType + "]", e);

            }


        }


        return null;


    }

}
