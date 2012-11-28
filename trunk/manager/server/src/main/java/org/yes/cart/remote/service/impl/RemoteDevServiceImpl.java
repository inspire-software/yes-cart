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
import org.yes.cart.remote.service.RemoteDevService;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.web.service.ws.client.AsyncFlexContextImpl;

import java.util.List;

/**
 * User: denispavlov
 * Date: 12-11-28
 * Time: 3:10 PM
 */
public class RemoteDevServiceImpl implements RemoteDevService {

    private final RemoteBackdoorService remoteBackdoorService;


    public RemoteDevServiceImpl(final RemoteBackdoorService remoteBackdoorService) {
        this.remoteBackdoorService = remoteBackdoorService;
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
    public List<CacheInfoDTOImpl> getCacheInfo() {
        return remoteBackdoorService.getCacheInfo(createCtx());
    }

    /** {@inheritDoc} */
    public void evictCache() {
        remoteBackdoorService.evictCache(createCtx());
    }

    private AsyncContext createCtx() {
        final AsyncContext flex = new AsyncFlexContextImpl();
        return flex;
    }

}
