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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.bulkjob.impl.BulkJobAutoContextImpl;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.dto.impl.CacheInfoDTO;
import org.yes.cart.domain.dto.impl.ModuleDTO;
import org.yes.cart.domain.vo.VoCacheInfo;
import org.yes.cart.domain.vo.VoClusterNode;
import org.yes.cart.domain.vo.VoJobStatus;
import org.yes.cart.domain.vo.VoModule;
import org.yes.cart.service.async.AsyncContextFactory;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.cluster.ClusterService;
import org.yes.cart.service.cluster.ReindexService;
import org.yes.cart.service.endpoint.SystemEndpointController;
import org.yes.cart.service.vo.VoAssemblySupport;

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
    private final ReindexService reindexService;
    private final AsyncContextFactory asyncContextFactory;
    private final VoAssemblySupport voAssemblySupport;

    @Autowired
    public SystemEndpointControllerImpl(final ClusterService clusterService,
                                        final ReindexService reindexService,
                                        final AsyncContextFactory asyncContextFactory,
                                        final VoAssemblySupport voAssemblySupport) {
        this.clusterService = clusterService;
        this.reindexService = reindexService;
        this.asyncContextFactory = asyncContextFactory;
        this.voAssemblySupport = voAssemblySupport;
    }

    /** {@inheritDoc} */
    @Override
    public @ResponseBody
    List<VoClusterNode> getClusterInfo() throws Exception {
        final Map<String, Object> param = new HashMap<>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_TIMEOUT_MS);
        final List<Node> cluster = clusterService.getClusterInfo(createCtx(param));
        return voAssemblySupport.assembleVos(VoClusterNode.class, Node.class, cluster);
    }

    /** {@inheritDoc} */
    @Override
    public @ResponseBody
    List<VoModule> getModuleInfo(@PathVariable("node") String node) throws Exception {
        final Map<String, Object> param = new HashMap<>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_TIMEOUT_MS);
        final List<ModuleDTO> modules = clusterService.getModuleInfo(createCtx(param), node);
        return voAssemblySupport.assembleVos(VoModule.class, ModuleDTO.class, modules);
    }


    /** {@inheritDoc} */
    @Override
    public @ResponseBody
    List<VoClusterNode> reloadConfigurations() throws Exception {
        final Map<String, Object> param = new HashMap<>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
        clusterService.reloadConfigurations(createCtx(param));
        evictAllCache();
        return getClusterInfo();
    }


    /** {@inheritDoc} */
    @Override
    public @ResponseBody
    List<Object[]> sqlQuery(@RequestBody final String query, @PathVariable("node") final String node) throws Exception{
        final Map<String, Object> param = new HashMap<>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_SQL_TIMEOUT_MS);
        return clusterService.sqlQuery(createCtx(param), query, node);
    }

    /** {@inheritDoc} */
    @Override
    public @ResponseBody
    List<Object[]> hsqlQuery(@RequestBody final String query, @PathVariable("node") final String node) throws Exception {
        final Map<String, Object> param = new HashMap<>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_SQL_TIMEOUT_MS);
        return clusterService.hsqlQuery(createCtx(param), query, node);
    }

    /** {@inheritDoc} */
    @Override
    public @ResponseBody
    List<Object[]> ftQuery(@RequestBody final String query, @PathVariable("node") final String node) throws Exception {
        final Map<String, Object> param = new HashMap<>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_SQL_TIMEOUT_MS);
        return clusterService.ftQuery(createCtx(param), query, node);
    }

    /** {@inheritDoc} */
    @Override
    public @ResponseBody
    List<VoCacheInfo> getCacheInfo() throws Exception {
        final Map<String, Object> param = new HashMap<>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
        Map<String, List<CacheInfoDTO>> caches = clusterService.getCacheInfo(createCtx(param));
        final List<VoCacheInfo> vos = new ArrayList<>(caches.size() * 250);
        for (final List<CacheInfoDTO> nodeCache : caches.values()) {
            vos.addAll(voAssemblySupport.assembleVos(VoCacheInfo.class, CacheInfoDTO.class, nodeCache));
        }
        return vos;
    }

    /** {@inheritDoc} */
    @Override
    public @ResponseBody
    List<VoCacheInfo> evictAllCache() throws Exception {
        final Map<String, Object> param = new HashMap<>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
        clusterService.evictAllCache(createCtx(param));
        return getCacheInfo();
    }

    /** {@inheritDoc} */
    @Override
    public @ResponseBody
    List<VoCacheInfo> evictCache(@PathVariable("name") final String name) throws Exception {
        final Map<String, Object> param = new HashMap<>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
        clusterService.evictCache(createCtx(param), name);
        return getCacheInfo();
    }

    /** {@inheritDoc} */
    @Override
    public @ResponseBody
    List<VoCacheInfo> enableStats(@PathVariable("name") final String name) throws Exception {
        final Map<String, Object> param = new HashMap<>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
        clusterService.enableStats(createCtx(param), name);
        return getCacheInfo();
    }

    /** {@inheritDoc} */
    @Override
    public @ResponseBody
    List<VoCacheInfo> disableStats(@PathVariable("name") final String name) throws Exception {
        final Map<String, Object> param = new HashMap<>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
        clusterService.disableStats(createCtx(param), name);
        return getCacheInfo();
    }

    /** {@inheritDoc} */
    @Override
    public @ResponseBody
    void warmUp() throws Exception {
        final Map<String, Object> param = new HashMap<>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_TIMEOUT_MS);
        clusterService.warmUp(createCtx(param));
    }

    /** {@inheritDoc} */
    @Override
    public @ResponseBody
    VoJobStatus getIndexJobStatus(@PathVariable("token") final String token) {
        final JobStatus status = reindexService.getIndexJobStatus(createCtx(null), token);
        final VoJobStatus vo = new VoJobStatus();
        vo.setToken(status.getToken());
        vo.setState(status.getState().name());
        vo.setReport(status.getReport());
        if (status.getCompletion() != null) {
            vo.setCompletion(status.getCompletion().name());
        }
        return vo;
    }

    /** {@inheritDoc} */
    @Override
    public @ResponseBody
    VoJobStatus reindexAllProducts() {
        final String token = reindexService.reindexAllProducts(createCtx(null));
        return getIndexJobStatus(token);
    }

    /** {@inheritDoc} */
    @Override
    public @ResponseBody
    VoJobStatus reindexShopProducts(@PathVariable("id") final long shopPk) {
        final String token = reindexService.reindexShopProducts(createCtx(null), shopPk);
        return getIndexJobStatus(token);
    }

    private AsyncContext createCtx(final Map<String, Object> param) {
        try {
            // This is manual access via Admin
            return asyncContextFactory.getInstance(param);
        } catch (IllegalStateException exp) {
            // This is auto access with thread local
            return new BulkJobAutoContextImpl(param);
        }
    }


}
