/*
 * Copyright 2009- 2016 Denys Pavlov, Igor Azarnyi
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
package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yes.cart.bulkjob.impl.BulkJobAutoContextImpl;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.dto.impl.CacheInfoDTOImpl;
import org.yes.cart.domain.vo.VoCacheInfo;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.cluster.ClusterService;
import org.yes.cart.service.endpoint.SystemEndpointController;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.web.service.ws.client.AsyncContextFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 12/08/2016
 * Time: 22:14
 */
@Component
public class SystemEndpointControllerImpl implements SystemEndpointController {

    private final ClusterService clusterService;
    private final AsyncContextFactory asyncContextFactory;
    private final VoAssemblySupport voAssemblySupport;

    @Autowired
    public SystemEndpointControllerImpl(final ClusterService clusterService,
                                        final AsyncContextFactory asyncContextFactory,
                                        final VoAssemblySupport voAssemblySupport) {
        this.clusterService = clusterService;
        this.asyncContextFactory = asyncContextFactory;
        this.voAssemblySupport = voAssemblySupport;
    }


    /** {@inheritDoc} */
    public List<Object[]> sqlQuery(final String query, final String node) throws Exception{
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_SQL_TIMEOUT_MS);
        return clusterService.sqlQuery(createCtx(param), query, node);
    }

    /** {@inheritDoc} */
    public List<Object[]> hsqlQuery(final String query, final String node) throws Exception {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_SQL_TIMEOUT_MS);
        return clusterService.hsqlQuery(createCtx(param), query, node);
    }

    /** {@inheritDoc} */
    public List<Object[]> luceneQuery(final String query, final String node) throws Exception {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_SQL_TIMEOUT_MS);
        return clusterService.luceneQuery(createCtx(param), query, node);
    }

    /** {@inheritDoc} */
    public List<VoCacheInfo> getCacheInfo() throws Exception {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
        Map<String, List<CacheInfoDTOImpl>> caches = clusterService.getCacheInfo(createCtx(param));
        final List<VoCacheInfo> vos = new ArrayList<>(caches.size() * 250);
        for (final List<CacheInfoDTOImpl> nodeCache : caches.values()) {
            vos.addAll(voAssemblySupport.assembleVos(VoCacheInfo.class, CacheInfoDTOImpl.class, nodeCache));
        }
        return vos;
    }

    /** {@inheritDoc} */
    public List<VoCacheInfo> evictAllCache() throws Exception {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
        clusterService.evictAllCache(createCtx(param));
        return getCacheInfo();
    }

    /** {@inheritDoc} */
    public List<VoCacheInfo> evictCache(final String name) throws Exception {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
        clusterService.evictCache(createCtx(param), name);
        return getCacheInfo();
    }

    /** {@inheritDoc} */
    public List<VoCacheInfo> enableStats(final String name) throws Exception {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
        clusterService.enableStats(createCtx(param), name);
        return getCacheInfo();
    }

    /** {@inheritDoc} */
    public List<VoCacheInfo> disableStats(final String name) throws Exception {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
        clusterService.disableStats(createCtx(param), name);
        return getCacheInfo();
    }

    /** {@inheritDoc} */
    public void warmUp() throws Exception {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_TIMEOUT_MS);
        clusterService.warmUp(createCtx(param));
    }

    private AsyncContext createCtx(final Map<String, Object> param) {
        try {
            // This is manual access via YUM
            return asyncContextFactory.getInstance(param);
        } catch (IllegalStateException exp) {
            // This is auto access with thread local
            return new BulkJobAutoContextImpl(param);
        }
    }


}
