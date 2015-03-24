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

package org.yes.cart.service.federation.impl;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.yes.cart.service.federation.ShopFederationStrategy;

import java.util.List;

/**
 * User: denispavlov
 * Date: 13/10/2014
 * Time: 10:25
 */
public abstract class CacheableImpexFederationFacadeImpl extends AbstractImpexFederationFilterImpl {

    private final CacheManager cacheManager;
    private final List<String> cachesToFlushForTransientEntities;

    protected CacheableImpexFederationFacadeImpl(final ShopFederationStrategy shopFederationStrategy,
                                                 final List<String> roles,
                                                 final CacheManager cacheManager,
                                                 final List<String> cachesToFlushForTransientEntities) {
        super(shopFederationStrategy, roles);
        this.cacheManager = cacheManager;
        this.cachesToFlushForTransientEntities = cachesToFlushForTransientEntities;
    }

    /**
     * Perform cache flush if this is transient entity.
     *
     * @param object object about to be persisted
     *
     * @return true if caches were flushed
     */
    protected boolean flushCachesIfTransient(final Object object) {
        if (isTransientEntity(object)) {
            for (final String cacheName : cachesToFlushForTransientEntities) {
                final Cache cache = cacheManager.getCache(cacheName);
                cache.clear();
            }
            return true;
        }
        return false;
    }

}
