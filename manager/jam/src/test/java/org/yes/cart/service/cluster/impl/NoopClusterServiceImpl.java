/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.service.cluster.impl;

import org.yes.cart.cluster.node.Node;
import org.yes.cart.cluster.node.impl.NodeImpl;
import org.yes.cart.domain.dto.impl.CacheInfoDTO;
import org.yes.cart.domain.dto.impl.ConfigurationDTO;
import org.yes.cart.domain.dto.impl.ModuleDTO;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.cluster.ClusterService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: inspiresoftware
 * Date: 27/01/2021
 * Time: 14:30
 */
public class NoopClusterServiceImpl implements ClusterService {

    @Override
    public Node getCurrentNode() {
        return new NodeImpl();
    }

    @Override
    public List<Node> getClusterInfo(final AsyncContext context) {
        return Collections.emptyList();
    }

    @Override
    public List<Node> getBlacklistedInfo(final AsyncContext context) {
        return Collections.emptyList();
    }

    @Override
    public List<ModuleDTO> getModuleInfo(final AsyncContext context, final String node) {
        return Collections.emptyList();
    }

    @Override
    public Map<String, List<ConfigurationDTO>> getConfigurationInfo(final AsyncContext context) {
        return Collections.emptyMap();
    }

    @Override
    public void warmUp(final AsyncContext context) {

    }

    @Override
    public Map<String, Pair<Long, Boolean>> getProductReindexingState(final AsyncContext context) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Pair<Long, Boolean>> getProductSkuReindexingState(final AsyncContext context) {
        return Collections.emptyMap();
    }

    @Override
    public void reindexAllProducts(final AsyncContext context) {

    }

    @Override
    public void reindexAllProductsSku(final AsyncContext context) {

    }

    @Override
    public void reindexProduct(final AsyncContext context, final long productPk) {

    }

    @Override
    public void reindexProductSku(final AsyncContext context, final long productSkuPk) {

    }

    @Override
    public void reindexProductSkuCode(final AsyncContext context, final String productSkuCode) {

    }

    @Override
    public void reindexProducts(final AsyncContext context, final long[] productPks) {

    }

    @Override
    public Map<String, List<String>> supportedQueries(final AsyncContext context) {
        return Collections.emptyMap();
    }

    @Override
    public List<Object[]> runQuery(final AsyncContext context, final String type, final String query, final String node) {
        return Collections.emptyList();
    }

    @Override
    public void reloadConfigurations(final AsyncContext context) {

    }

    @Override
    public Map<String, List<CacheInfoDTO>> getCacheInfo(final AsyncContext context) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Boolean> evictAllCache(final AsyncContext context) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Boolean> evictCache(final AsyncContext context, final String name) {
        return null;
    }

    @Override
    public Map<String, Boolean> enableCacheStats(final AsyncContext context, final String name) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Boolean> disableCacheStats(final AsyncContext context, final String name) {
        return Collections.emptyMap();
    }

    @Override
    public List<Pair<String, String>> getAlerts(final AsyncContext context) {
        return Collections.emptyList();
    }
}
