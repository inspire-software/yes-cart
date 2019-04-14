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

package org.yes.cart.cluster.service;

/**
 * User: denispavlov
 * Date: 13/04/2019
 * Time: 16:27
 */
public interface ReindexDirector {

    /**
     * @return re-indexing state (0th - [RUNNIG|DONE], 1st - count)
     */
    Object[] getProductReindexingState();

    /**
     * @return re-indexing state (0th - [RUNNIG|DONE], 1st - count)
     */
    Object[] getProductSkuReindexingState();

    /**
     * Reindex all products.
     */
    void reindexAllProducts();

    /**
     * Reindex all products.
     */
    void reindexAllProductsSku();

    /**
     * Reindex all products.
     *
     * @param shopPk shop pk.
     */
    void reindexShopProducts(long shopPk);

    /**
     * Reindex all products.
     *
     * @param shopPk shop pk.
     */
    void reindexShopProductsSku(long shopPk);

    /**
     * Reindex single products.
     *
     * @param productPk product pk.
     */
    void reindexProduct(long productPk);

    /**
     * Reindex single products.
     *
     * @param productPk product pk.
     */
    void reindexProductSku(long productPk);

    /**
     * Reindex single products.
     *
     * @param productCode product SKU code.
     */
    void reindexProductSkuCode(String productCode);


    /**
     * Reindex given set of products.
     *
     * @param productPks product PKs to reindex
     */
    void reindexProducts(long[] productPks);

}
