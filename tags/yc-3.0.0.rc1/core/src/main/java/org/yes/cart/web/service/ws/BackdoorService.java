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

package org.yes.cart.web.service.ws;

import java.util.List;

/**
 * Back door administrative service.
 * Need to have ability:
 *
 * 1. reindex product on demand on storefront side instead of management side.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/28/12
 * Time: 9:50 AM
 */
public interface BackdoorService {

    /**
     * @return true if service is online
     */
    boolean ping();

    /**
     * Preload main caches.
     */
    void warmUp();

    /**
     * Reindex all products.
     *
     * @return quantity of objects in index
     */
    int reindexAllProducts();

    /**
     * Reindex all products.
     *
     * @return quantity of objects in index
     */
    int reindexAllProductsSku();

    /**
     * Reindex all products.
     *
     * @param shopPk shop pk.
     *
     * @return quantity of objects in index
     */
    int reindexShopProducts(long shopPk);

    /**
     * Reindex all products.
     *
     * @param shopPk shop pk.
     *
     * @return quantity of objects in index
     */
    int reindexShopProductsSku(long shopPk);

    /**
     * Reindex single products.
     *
     * @param productPk product pk.
     *
     * @return quantity of objects in index
     */
    int reindexProduct(long productPk);

    /**
     * Reindex single products.
     *
     * @param productPk product pk.
     *
     * @return quantity of objects in index
     */
    int reindexProductSku(long productPk);

    /**
     * Reindex single products.
     *
     * @param productCode product SKU code.
     *
     * @return quantity of objects in index
     */
    int reindexProductSkuCode(String productCode);


    /**
     * Reindex given set of products.
     *
     * @param productPks product PKs to reindex
     *
     * @return quantity of objects in index
     */
    int reindexProducts(long[] productPks);


    /**
     * Execute sql and return result.
     * DML operating also allowed, in this case result has quantity of affected rows.
     *
     * @param query query ot execute.
     * @return list of rows
     */
    List<Object[]> sqlQuery(String query);

    /**
     * Execute hsql and return result.
     *
     * @param query query ot execute.
     *
     * @return list of rows
     */
    List<Object[]> hsqlQuery(String query);

    /**
     * Execute lucene and return result.
     *
     * @param query query ot execute.
     *
     * @return list of rows
     */
    List<Object[]> luceneQuery(String query);

}
