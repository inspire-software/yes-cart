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

import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.model.JobStatus;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public interface ReindexService {

    /**
     * Get index job status by token.
     *
     * @param context web service context
     * @param token job token
     *
     * @return status of indexing
     */
    JobStatus getIndexJobStatus(AsyncContext context, String token);

    /**
     * Reindex all products.
     *
     * @param context web service context
     *
     * @return quantity product in created index.
     */
    String reindexAllProducts(AsyncContext context);

    /**
     * Reindex all products.
     *
     * @param context web service context
     * @param shopPk shop pk.
     *
     * @return quantity product in created index.
     */
    String reindexShopProducts(AsyncContext context, long shopPk);

}
