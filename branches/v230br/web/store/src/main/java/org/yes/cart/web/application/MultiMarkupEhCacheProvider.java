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

package org.yes.cart.web.application;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.wicket.markup.MarkupCache;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: denispavlov
 * Date: 28/08/2014
 * Time: 13:58
 */
public class MultiMarkupEhCacheProvider<T> implements MarkupCache.ICache<String, T> {

    private final Cache CACHE;

    public MultiMarkupEhCacheProvider(final CacheManager cacheManager,
                                      final String cacheKey) {
        this.CACHE = cacheManager.getCache(cacheKey);
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        CACHE.clear();
    }


    /** {@inheritDoc} */
    @Override
    public boolean remove(final String key) {
        CACHE.evict(key);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public T get(final String key) {
        final Cache.ValueWrapper wrapper = CACHE.get(key);
        if (wrapper != null) {
            return (T) wrapper.get();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<String> getKeys() {
        final Ehcache ehcache = (Ehcache) CACHE.getNativeCache();
        return ehcache.getKeys();
    }

    /** {@inheritDoc} */
    @Override
    public Collection<T> getValues() {
        final Ehcache ehcache = (Ehcache) CACHE.getNativeCache();
        final List<T> values = new ArrayList<T>();
        for (final Object key : ehcache.getKeys()) {
            final Element elem = ehcache.get(key);
            if (elem.getObjectValue() != null && !elem.isExpired()) {
               final T val = (T) elem.getObjectValue();
                values.add(val);
            }
        }
        return values;
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsKey(final String key) {
        return get(key) != null;
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        final Ehcache ehcache = (Ehcache) CACHE.getNativeCache();
        return ehcache.getSize();
    }

    /** {@inheritDoc} */
    @Override
    public void put(final String key, final T value) {
        CACHE.put(key, value);
    }

    /** {@inheritDoc} */
    @Override
    public void shutdown() {
        // Will be handled by Ehcache engine
    }
}
