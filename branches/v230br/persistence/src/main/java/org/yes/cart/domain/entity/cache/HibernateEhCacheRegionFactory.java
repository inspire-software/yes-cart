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

package org.yes.cart.domain.entity.cache;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory;
import org.hibernate.cache.spi.*;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.Settings;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import java.util.Properties;

/**
 * Static locator to inject Spring configured CacheManager for consistent configuration
 *
 * User: denispavlov
 * Date: 13-10-15
 * Time: 7:46 PM
 */
public class HibernateEhCacheRegionFactory implements RegionFactory {

    private static RegionFactory PROVIDER;

    /** {@inheritDoc} */
    @Override
    public void start(final Settings settings, final Properties properties) throws CacheException {
        // manager by Spring container
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        // manager by Spring container
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMinimalPutsEnabledByDefault() {
        return PROVIDER.isMinimalPutsEnabledByDefault();
    }

    /** {@inheritDoc} */
    @Override
    public AccessType getDefaultAccessType() {
        return AccessType.READ_WRITE;
    }

    /** {@inheritDoc} */
    @Override
    public long nextTimestamp() {
        return PROVIDER.nextTimestamp();
    }

    /** {@inheritDoc} */
    @Override
    public EntityRegion buildEntityRegion(final String regionName, final Properties properties, final CacheDataDescription metadata) throws CacheException {
        return PROVIDER.buildEntityRegion(regionName, properties, metadata);
    }

    /** {@inheritDoc} */
    @Override
    public NaturalIdRegion buildNaturalIdRegion(final String regionName, final Properties properties, final CacheDataDescription metadata) throws CacheException {
        return PROVIDER.buildNaturalIdRegion(regionName, properties, metadata);
    }

    /** {@inheritDoc} */
    @Override
    public CollectionRegion buildCollectionRegion(final String regionName, final Properties properties, final CacheDataDescription metadata) throws CacheException {
        return PROVIDER.buildCollectionRegion(regionName, properties, metadata);
    }

    /** {@inheritDoc} */
    @Override
    public QueryResultsRegion buildQueryResultsRegion(final String regionName, final Properties properties) throws CacheException {
        return PROVIDER.buildQueryResultsRegion(regionName, properties);
    }

    /** {@inheritDoc} */
    @Override
    public TimestampsRegion buildTimestampsRegion(final String regionName, final Properties properties) throws CacheException {
        return PROVIDER.buildTimestampsRegion(regionName, properties);
    }

    /**
     * Spring IoC.
     *
     * @param cacheManager cache manager
     */
    public void setCacheManager(final EhCacheCacheManager cacheManager) {
        PROVIDER = new SingletonEhCacheRegionFactory(null) {{
            manager = cacheManager.getCacheManager();
        }};
    }


}
