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

package org.yes.cart.remote.service.impl;

import org.yes.cart.bulkjob.impl.BulkJobAutoContextImpl;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.dto.impl.CacheInfoDTOImpl;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteBackdoorService;
import org.yes.cart.remote.service.RemoteDevService;
import org.yes.cart.service.async.model.AsyncContext;
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

    private final RemoteBackdoorService remoteBackdoorService;


    public RemoteDevServiceImpl(final RemoteBackdoorService remoteBackdoorService) {
        this.remoteBackdoorService = remoteBackdoorService;
    }

    /** {@inheritDoc} */
    public List<Object[]> sqlQuery(final String query, final String node) {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_SQL_TIMEOUT_MS);
        return remoteBackdoorService.sqlQuery(createCtx(param), query, node);
    }

    /** {@inheritDoc} */
    public List<Object[]> hsqlQuery(final String query, final String node) {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_SQL_TIMEOUT_MS);
        return remoteBackdoorService.hsqlQuery(createCtx(param), query, node);
    }

    /** {@inheritDoc} */
    public List<Object[]> luceneQuery(final String query, final String node) {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_SQL_TIMEOUT_MS);
        return remoteBackdoorService.luceneQuery(createCtx(param), query, node);
    }

    /** {@inheritDoc} */
    public Map<String, List<CacheInfoDTOImpl>> getCacheInfo() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
        return remoteBackdoorService.getCacheInfo(createCtx(param));
    }

    /** {@inheritDoc} */
    public Map<String, Boolean> evictAllCache() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
        return remoteBackdoorService.evictAllCache(createCtx(param));
    }

    /** {@inheritDoc} */
    public Map<String, Boolean> evictCache(final String name) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
        return remoteBackdoorService.evictCache(createCtx(param), name);
    }

    /** {@inheritDoc} */
    public Map<String, Boolean> enableStats(final String name) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
        return remoteBackdoorService.enableStats(createCtx(param), name);
    }

    /** {@inheritDoc} */
    public Map<String, Boolean> disableStats(final String name) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
        return remoteBackdoorService.disableStats(createCtx(param), name);
    }

    /** {@inheritDoc} */
    public void warmUp() {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_TIMEOUT_MS);
        remoteBackdoorService.warmUp(createCtx(param));
    }

    private AsyncContext createCtx(final Map<String, Object> param) {
        try {
            // This is manual access via YUM
            return new AsyncFlexContextImpl(param);
        } catch (IllegalStateException exp) {
            // This is auto access with thread local
            return new BulkJobAutoContextImpl(param);
        }
    }


}
