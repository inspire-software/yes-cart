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
package org.yes.cart.web.service.ws.impl;

import net.sf.ehcache.Element;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.service.ws.AlertDirector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 07/03/2017
 * Time: 20:12
 */
public class AlertDirectorImpl implements AlertDirector {

    private CacheManager cacheManager;

    private final ThreadLocal<SimpleDateFormat> formater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    /** {@inheritDoc} */
    @Override
    public void publish(final Pair<String, String> alert) {
        getAlertsStorage().put(alert, alert);
    }

    /** {@inheritDoc} */
    @Override
    public List<Pair<String, String>> getAlerts() {
        return getAll();
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        getAlertsStorage().clear();
    }

    List<Pair<String, String>> getAll() {
        final net.sf.ehcache.Cache nativeCache = (net.sf.ehcache.Cache) getAlertsStorage().getNativeCache();
        final Map<Object, Element> elems = nativeCache.getAll(nativeCache.getKeys());
        final List<Pair<String, String>> all = new ArrayList<Pair<String, String>>(100);
        for (final Map.Entry<Object, Element> elem : elems.entrySet()) {
            if (elem.getValue() != null && !elem.getValue().isExpired()) {
                final Date last = new Date(elem.getValue().getLatestOfCreationAndUpdateTime());
                final Pair<String, String> elemOriginal = (Pair<String, String>) elem.getValue().getObjectValue();
                final Pair<String, String> elemWithLastTime = new Pair<String, String>(
                        formater.get().format(last) + ": " + elemOriginal.getFirst(),
                        elemOriginal.getSecond()
                );
                all.add(elemWithLastTime);
            }
        }
        return all;
    }

    Cache getAlertsStorage() {
        return getCacheManager().getCache("alertDirector-alertsStorage");
    }

    CacheManager getCacheManager() {
        return cacheManager;
    }


    /** IoC. Set cache manager.  */
    public void setCacheManager(final CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

}
