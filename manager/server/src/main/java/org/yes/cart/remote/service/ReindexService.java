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

import org.yes.cart.service.async.model.JobStatus;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public interface ReindexService {

    /**
     * Get index job status by token.
     *
     * @param token job token
     * @return status of indexing
     */
    JobStatus getIndexAllStatus(final String token);

    /**
     * Reindex all products.
     *
     * @return quantity product in created index.
     */
    String reindexAllProducts();

    /**
     * Reindex product by given primary key.
     *
     * @param pk product primary key
     * @return quantity of products in created index.
     */
    int reindexProduct(long pk);

    /**
     * Reindex product by given sku primary key.
     *
     * @param pk sku primary key
     * @return quantity of products in created index.
     */
    int reindexProductSku(long pk);

    /**
     * Reindex product by given sku primary key.
     *
     * @param code sku code
     * @return quantity of products in created index.
     */
    int reindexProductSkuCode(String code);

}
