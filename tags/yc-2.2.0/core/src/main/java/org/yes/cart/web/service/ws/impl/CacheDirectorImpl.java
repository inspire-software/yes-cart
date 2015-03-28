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
package org.yes.cart.web.service.ws.impl;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.yes.cart.domain.dto.impl.CacheInfoDTOImpl;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.service.ws.CacheDirector;

import java.util.*;

/**
 * Service responsible  to evict particular cache(s) depending on entity and operation.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 18 Aug 2013
 * Time: 9:50 AM
 */
public class CacheDirectorImpl implements CacheDirector {

    private Map<String, Map<String, Set<Pair<String, String>>>> entityOperationCache;

    private CacheManager cacheManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean ping() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public List<CacheInfoDTOImpl> getCacheInfo() {
        final Collection<String> cacheNames = cacheManager.getCacheNames();
        final List<CacheInfoDTOImpl> rez = new ArrayList<CacheInfoDTOImpl>(cacheNames.size());
        for (String cacheName : cacheNames) {
            final Cache cache = cacheManager.getCache(cacheName);
            final net.sf.ehcache.Cache nativeCache = (net.sf.ehcache.Cache) cache.getNativeCache();
            rez.add(
                    new CacheInfoDTOImpl(
                            nativeCache.getName(),
                            nativeCache.getSize(),
                            nativeCache.getMemoryStoreSize(),
                            nativeCache.getDiskStoreSize(),
                            0,0/*nativeCache.calculateInMemorySize(),
                            nativeCache.calculateOnDiskSize()*/   /*heavy operation*/
                    )
            );

        }
        return rez;
    }


    CacheManager getCacheManager() {
        return cacheManager;
    }

    /**
     * {@inheritDoc}
     */
    public void evictAllCache() {
        final CacheManager cm = getCacheManager();
        for (String cacheName : cm.getCacheNames()) {
            final Cache cache = cm.getCache(cacheName);
            cache.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void evictCache(final String cacheName) {
        final CacheManager cm = getCacheManager();
        final Cache cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    public int onCacheableChange(final String entityOperation, final String entityName, final Long pkValue) {

        int cnt = 0;

        final Set<Pair<String, String>> cacheNames = resolveCacheNames(entityOperation, entityName);

        if (cacheNames != null) {

            final CacheManager cm = getCacheManager();

            for (Pair<String, String> cacheStrategy : cacheNames) {

                final Cache cache = cm.getCache(cacheStrategy.getFirst());

                if (cache != null) {

                    if("all".equals(cacheStrategy.getSecond())) {

                        cache.clear();

                        cnt ++;

                    } else if("key".equals(cacheStrategy.getSecond())) {

                        cache.evict(pkValue);

                        cnt ++;

                    } else {

                        ShopCodeContext.getLog(this).warn("The [" + cacheStrategy.getSecond() + "] cache eviction strategy not supported");

                    }

                }

            }

        }

        return cnt;
    }

    /**
     * Resolve caches names for invalidation for given entity and operation.
     * @param entityOperation given operation
     * @param entityName given entity name
     * @return set of cache names
     */
    Set<Pair<String, String>> resolveCacheNames(final String entityOperation, final String entityName) {

        final Map<String, Set<Pair<String, String>>> entOperations = this.entityOperationCache.get(entityName);

        if (entOperations != null) {
            return entOperations.get(entityOperation);
        }

        return null;

    }

    /** IoC. Set configuration. */
    public void setEntityOperationCache(final Map<String, Map<String, Set<Pair<String, String>>>> entityOperationCache) {
        this.entityOperationCache = entityOperationCache;
    }

    /** IoC. Set cache manager.  */
    public void setCacheManager(final CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

}
