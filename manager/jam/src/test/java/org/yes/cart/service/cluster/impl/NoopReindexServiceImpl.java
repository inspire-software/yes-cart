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

import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.async.model.impl.JobStatusImpl;
import org.yes.cart.service.cluster.ReindexService;

/**
 * User: inspiresoftware
 * Date: 27/01/2021
 * Time: 14:37
 */
public class NoopReindexServiceImpl implements ReindexService {

    @Override
    public JobStatus getIndexJobStatus(final AsyncContext context, final String token) {
        return new JobStatusImpl();
    }

    @Override
    public JobStatus reindexAllProducts(final AsyncContext context) {
        return new JobStatusImpl();
    }

    @Override
    public JobStatus reindexShopProducts(final AsyncContext context, final long shopPk) {
        return new JobStatusImpl();
    }

}
