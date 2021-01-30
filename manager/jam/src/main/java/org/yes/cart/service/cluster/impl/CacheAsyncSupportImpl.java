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

import org.yes.cart.bulkjob.impl.BulkJobAutoContextImpl;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.service.async.AsyncContextFactory;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.cluster.CacheAsyncSupport;
import org.yes.cart.service.cluster.ClusterService;

import java.util.HashMap;
import java.util.Map;

/**
 * User: inspiresoftware
 * Date: 22/01/2021
 * Time: 12:00
 */
public class CacheAsyncSupportImpl implements CacheAsyncSupport {

    private final ClusterService clusterService;
    private final AsyncContextFactory asyncContextFactory;

    public CacheAsyncSupportImpl(final ClusterService clusterService,
                                 final AsyncContextFactory asyncContextFactory) {
        this.clusterService = clusterService;
        this.asyncContextFactory = asyncContextFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void asyncEvictCache(final String... caches) {
        final Map<String, Object> param = new HashMap<>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_CONNECTOR_CACHE_TIMEOUT_MS);
        for (final String name : caches) {
            clusterService.evictCache(createCtx(param), name);
        }
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
