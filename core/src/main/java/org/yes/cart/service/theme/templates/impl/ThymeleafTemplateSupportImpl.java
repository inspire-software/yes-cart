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

package org.yes.cart.service.theme.templates.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.thymeleaf.cache.*;
import org.thymeleaf.context.IContext;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.yes.cart.service.theme.templates.TemplateSupport;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 17/02/2019
 * Time: 18:18
 */
public class ThymeleafTemplateSupportImpl implements TemplateSupport {

    private static final Logger LOG = LoggerFactory.getLogger(GroovyGStringTemplateSupportImpl.class);

    private final Cache TEMPLATE_CACHE;
    private final SpringTemplateEngine engine;

    public ThymeleafTemplateSupportImpl(final CacheManager cacheManager) throws ClassNotFoundException {

        TEMPLATE_CACHE = cacheManager.getCache("contentService-templateSupport");

        final SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setCacheManager(new ThymeleafCacheManager());
        this.engine = engine;

    }

    @Override
    public Template get(final String template) {

        return new Template() {

            private final String tmp = template;

            @Override
            public String make(final Map binding) {

                return engine.process(this.tmp, new IContext() {
                    @Override
                    public Locale getLocale() {
                        final Object localeObject = binding.get("localeObject");
                        if (localeObject instanceof Locale) {
                            return (Locale) localeObject;
                        }
                        final Object obj = binding.get("locale");
                        if (obj != null) {
                            if (obj instanceof Locale) {
                                return (Locale) obj;
                            } else if (obj instanceof String) {
                                return new Locale((String) obj);
                            }
                        }
                        return Locale.ENGLISH;
                    }

                    @Override
                    public boolean containsVariable(final String name) {
                        return binding.containsKey(name);
                    }

                    @Override
                    public Set<String> getVariableNames() {
                        return binding.keySet();
                    }

                    @Override
                    public Object getVariable(final String name) {
                        return binding.get(name);
                    }
                });

            }
        };

    }

    private class ThymeleafCacheManager extends AbstractCacheManager implements ICacheManager {

        @Override
        protected ICache<TemplateCacheKey, TemplateModel> initializeTemplateCache() {
            return new CacheToICacheAdapter<>(TEMPLATE_CACHE);
        }

        @Override
        protected ICache<ExpressionCacheKey, Object> initializeExpressionCache() {
            return new CacheToICacheAdapter<>(TEMPLATE_CACHE);
        }
    }

    private static final class CacheToICacheAdapter<K, V> implements ICache<K, V> {

        private final Cache cache;

        public CacheToICacheAdapter(final Cache cache) {
            this.cache = cache;
        }

        @Override
        public void put(final K key, final V value) {
            cache.put(key, value);
        }

        @Override
        public V get(K key) {
            final Cache.ValueWrapper vw = cache.get(key);
            if (vw != null) {
                return (V) vw.get();
            }
            return null;
        }

        @Override
        public V get(final K key, final ICacheEntryValidityChecker<? super K, ? super V> validityChecker) {
            return get(key);
        }

        @Override
        public void clear() {
            cache.clear();
        }

        @Override
        public void clearKey(K key) {
            cache.evict(key);
        }

        @Override
        public Set<K> keySet() {
            return Collections.emptySet();
        }
    }
}


