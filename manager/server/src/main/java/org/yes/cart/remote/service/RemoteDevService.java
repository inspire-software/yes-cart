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

package org.yes.cart.remote.service;

import org.yes.cart.domain.dto.impl.CacheInfoDTOImpl;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 12-11-28
 * Time: 3:07 PM
 */
public interface RemoteDevService {


    String URL_TYPE_CACHE_DIRECTOR = "cacheDirector";

    String URL_TYPE_REINDEX_DOOR = "reindexDoor";



    /**
     * Execute sql and return result.
     * DML operatin also allowed, in this case result has quantity of affected rows.
     *
     * @param query query ot execute.
     * @param node node on which to run query
     *
     * @return list of rows
     */
    List<Object[]> sqlQuery(String query, String node);

    /**
     * Execute hsql and return result.
     *
     * @param query query ot execute.
     * @param node node on which to run query
     *
     * @return list of rows
     */
    List<Object[]> hsqlQuery(String query, String node);

    /**
     * Execute lucene and return result.
     *
     * @param query query ot execute.
     * @param node node on which to run query
     *
     * @return list of rows
     */
    List<Object[]> luceneQuery(String query, String node);

    /**
     * Get cache information.
     *
     * @return list of information per each cache per node.
     */
    Map<String, List<CacheInfoDTOImpl>> getCacheInfo() throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Evict all caches , which are represent in getCacheInfo list.
     *
     * @return state if the cache has evicted on nodes
     */
    Map<String, Boolean> evictAllCache() throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Evict cache by name.
     *
     * @param name name of cache to evict
     *
     * @return state if the cache has evicted on nodes
     */
    Map<String, Boolean> evictCache(String name) throws UnmappedInterfaceException, UnableToCreateInstanceException;


}
