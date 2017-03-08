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

package org.yes.cart.service.cluster;

import org.yes.cart.cluster.node.Node;
import org.yes.cart.domain.dto.impl.CacheInfoDTOImpl;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.async.model.AsyncContext;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 */
public interface ClusterService {

    /**
     * All registered nodes in this cluster.
     *
     * E.g. if we have JAM, YES0, YES1 and YES2 nodes
     * this methods should return four nodes.
     *
     * @param context web service context
     *
     * @return node objects
     */
    List<Node> getClusterInfo(AsyncContext context);

    /**
     * Warm up all storefront servers.
     *
     * @param context web service context
     */
    void warmUp(AsyncContext context);

    /**
     * Reindex all products status.
     *
     * @param context web service context
     * @return quantity product in index.
     */
    Map<String, Pair<Integer, Boolean>> getProductReindexingState(AsyncContext context);

    /**
     * Reindex all products.
     *
     * @param context web service context
     * @return quantity product in index.
     */
    Map<String, Pair<Integer, Boolean>> getProductSkuReindexingState(AsyncContext context);

    /**
     * Reindex all products status.
     *
     * @param context web service context
     */
    void reindexAllProducts(AsyncContext context);

    /**
     * Reindex all products.
     *
     * @param context web service context
     */
    void reindexAllProductsSku(AsyncContext context);

    /**
     * Reindex single product.
     *
     * @param context web service context
     * @param productPk product pk.
     */
    void reindexProduct(AsyncContext context, long productPk);

    /**
     * Reindex single product by sku.
     *
     * @param context web service context
     * @param productSkuPk product SKU pk.
     */
    void reindexProductSku(AsyncContext context, long productSkuPk);

    /**
     * Reindex single product by sku.
     *
     * @param context web service context
     * @param productSkuCode product SKU code.
     */
    void reindexProductSkuCode(AsyncContext context, String productSkuCode);


    /**
     * Reindex given set of products.
     *
     * @param context web service context
     * @param productPks product PKs to reindex
     */
    void reindexProducts(AsyncContext context, long[] productPks);


    /**
     * Execute sql and return result.
     * DML operating also allowed, in this case result has quantity of affected rows.
     *
     * @param context web service context
     * @param query query ot execute.
     * @param node node (0..n) on which to run the query
     *
     * @return list of rows
     */
    List<Object[]> sqlQuery(AsyncContext context, String query, String node);

    /**
     * Execute hsql and return result.
     *
     * @param context web service context
     * @param query query ot execute.
     * @param node node (0..n) on which to run the query
     *
     * @return list of rows
     */
    List<Object[]> hsqlQuery(AsyncContext context, String query, String node);

    /**
     * Execute lucene and return result.
     *
     * @param context web service context
     * @param query query ot execute.
     * @param node node (0..n) on which to run the query
     *
     * @return list of rows
     */
    List<Object[]> luceneQuery(AsyncContext context, String query, String node);

    /**
     * Get cache information.
     *
     * @param context web service context
     * @return list of information per each cache.
     */
    Map<String, List<CacheInfoDTOImpl>> getCacheInfo(AsyncContext context)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Evict all caches , which are represent in getCacheInfo list.
     *
     * @param context web service context
     */
    Map<String, Boolean> evictAllCache(AsyncContext context) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Evict cache by name.
     *
     * @param context web service context
     * @param name name of cache to evict
     */
    Map<String, Boolean> evictCache(AsyncContext context, String name) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Enable cache statistics by name.
     *
     * @param context web service context
     * @param name name of cache to evict
     */
    Map<String, Boolean> enableStats(AsyncContext context, String name) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Disable cache statistics by name.
     *
     * @param context web service context
     * @param name name of cache to evict
     */
    Map<String, Boolean> disableStats(AsyncContext context, String name) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get platform alerts
     *
     * @param context web service context
     * @return list of alerts
     */
    List<Pair<String, String>> getAlerts(AsyncContext context) throws UnmappedInterfaceException, UnableToCreateInstanceException;

}
