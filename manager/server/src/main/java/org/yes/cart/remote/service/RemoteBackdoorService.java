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

import java.io.IOException;
import java.util.List;

/**
 * User: iazarny@yahoo.com Igor Azarny
 * Date: 4 - feb - 12
 * Time: 5:40 PM
 */
public interface RemoteBackdoorService {

    /**
     * Reindex all products.
     * @return quantity product in index.
     */
    int reindexAllProducts();

    /**
     * Reindex single products.
     * @param productPk product pk.
     * @return quantity of objects in index
     */
    int reindexProduct(long productPk);


    /**
     * Reindex given set of products.
     * @param productPks product PKs to reindex
     * @return quantity of objects in index
     */
    int reindexProducts(long[] productPks);


    /**
     * Execute sql and return result.
     * DML operatin also allowed, in this case result has quantity of affected rows.
     *
     * @param query query ot execute.
     * @return list of rows
     */
    List<Object[]> sqlQuery(String query);

    /**
     * Execute hsql and return result.
     *
     * @param query query ot execute.
     * @return list of rows
     */
    List<Object[]> hsqlQuery(String query);

    /**
     * Execute lucene and return result.
     *
     * @param query query ot execute.
     * @return list of rows
     */
    List<Object[]> luceneQuery(String query);

    /**
     * Get chache information.
     * @return list of information per each cache.
     */
    List<CacheInfoDTOImpl> getCacheInfo();

    /**
     * Evict alli caches , which are represent in getCacheInfo list.
     */
    void evictCache();

    /**
     * Get real path to image vault on shop application. Need to allow have different web  context for yes-shop
     * @return  real path to image vault
     * @throws IOException    in case of io error.
     */
    String getImageVaultPath() throws IOException;


}
