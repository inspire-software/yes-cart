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

package org.yes.cart.domain.entity.cache;

import net.sf.ehcache.CacheManager;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.ehcache.internal.EhcacheRegionFactory;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import java.util.Map;

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
    public void start(final SessionFactoryOptions settings, final Map properties) throws CacheException {
        // managed by Spring container
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
        return PROVIDER.getDefaultAccessType();
    }

    /** {@inheritDoc} */
    @Override
    public long nextTimestamp() {
        return PROVIDER.nextTimestamp();
    }


    /** {@inheritDoc} */
    @Override
    public String qualify(final String regionName) {
        return PROVIDER.qualify(regionName);
    }

    /** {@inheritDoc} */
    @Override
    public DomainDataRegion buildDomainDataRegion(final DomainDataRegionConfig regionConfig, final DomainDataRegionBuildingContext buildingContext) {
        return PROVIDER.buildDomainDataRegion(regionConfig, buildingContext);
    }

    /** {@inheritDoc} */
    @Override
    public QueryResultsRegion buildQueryResultsRegion(final String regionName, final SessionFactoryImplementor sessionFactory) {
        return PROVIDER.buildQueryResultsRegion(regionName, sessionFactory);
    }

    /** {@inheritDoc} */
    @Override
    public TimestampsRegion buildTimestampsRegion(final String regionName, final SessionFactoryImplementor sessionFactory) {
        return PROVIDER.buildTimestampsRegion(regionName, sessionFactory);
    }

    /**
     * Spring IoC.
     *
     * @param cacheManager cache manager
     */
    public void setCacheManager(final EhCacheCacheManager cacheManager) {
        PROVIDER = new EhcacheRegionFactory() {
            @Override
            protected CacheManager resolveCacheManager(final SessionFactoryOptions settings, final Map properties) {
                return cacheManager.getCacheManager();
            }
        };
    }


}
