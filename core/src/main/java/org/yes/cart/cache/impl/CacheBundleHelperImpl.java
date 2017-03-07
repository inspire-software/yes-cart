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

package org.yes.cart.cache.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.yes.cart.cache.CacheBundleHelper;

import java.util.Collections;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 17/07/2016
 * Time: 12:19
 */
public class CacheBundleHelperImpl implements CacheBundleHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CacheBundleHelperImpl.class);

    private CacheManager cacheManager;
    private Set<String> caches = Collections.emptySet();

    @Override
    public void flushBundleCaches() {

        for (final String cache : caches) {

            safeFlushCache(cache);

        }

    }

    private void safeFlushCache(final String cacheName) {

        final Cache cache = cacheManager.getCache(cacheName);

        if(cache != null) {
            cache.clear();
        } else {
            LOG.warn("Cache {} does not exist", cacheName);
        }

    }


    /**
     * IoC. Set cache names.
     *
     * @param caches cache names
     */
    public void setCaches(final Set<String> caches) {
        this.caches = caches;
    }

    /**
     * IoC. Set cache manager service.
     *
     * @param cacheManager cache manager
     */
    public void setCacheManager(final CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

}
