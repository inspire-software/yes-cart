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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.cluster.service.CacheEvictionQueue;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.utils.ThreadLocalAsyncContextUtils;
import org.yes.cart.utils.log.Markers;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: denispavlov
 * Date: 26/05/2018
 * Time: 08:42
 */
public class CacheEvictionQueueImpl implements CacheEvictionQueue, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(CacheEvictionQueueImpl.class);

    private final Map<Key, Set<Long>> queue = new ConcurrentHashMap<>();

    private int maxQueueSize = 1000;

    @Override
    public void enqueue(final String entityOperation, final String entityName, final Long pkValue) {

        if (this.queue.size() > maxQueueSize) {
            LOG.error(Markers.alert(), "Cache eviction queue is full");
            return;
        }

        final Authentication auth = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication() : null;
        String username = auth != null && auth.isAuthenticated() ? auth.getName() : null;
        if (username == null) {
            final AsyncContext jobContext = ThreadLocalAsyncContextUtils.getContext();
            username = jobContext != null ? jobContext.getAttribute(AsyncContext.USERNAME) : null;
        }

        if (StringUtils.isBlank(username)) {
            LOG.debug("Cannot invalidate cache for entity [" + entityName + "] pk value =  [" + pkValue + "] - no user in context ");
            return;
        }

        queue.compute(new Key(username, entityOperation, entityName), (k, v) ->
                (v == null) ? Collections.singleton(pkValue) :
                        (v.contains(pkValue) ? v :
                        Stream.of(v, Collections.singleton(pkValue))
                                .flatMap(Collection::stream)
                                .collect(Collectors.toSet())));

    }

    @Override
    public CacheEvictionItem dequeue() {

        final Key key = this.queue.isEmpty() ? null : this.queue.keySet().iterator().next();
        if (key == null) {
            return null;
        }

        final Set<Long> update = this.queue.remove(key);

        return new Item(key, update);

    }

    @Override
    public void clear() {

        queue.clear();

    }

    @Override
    public void destroy() throws Exception {
        LOG.debug("Shutting down cache eviction queue");
        this.queue.clear();
    }

    private static class Item implements CacheEvictionItem {

        private final Key key;
        private final Set<Long> pks;


        private Item(final Key key, final Set<Long> pks) {
            this.key = key;
            this.pks = pks;
        }

        @Override
        public String getUser() {
            return key.user;
        }

        @Override
        public String getOperation() {
            return key.entityOperation;
        }

        @Override
        public String getEntityName() {
            return key.entityName;
        }

        @Override
        public Set<Long> getPKs() {
            return pks;
        }
    }

    private static class Key {

        private final String user;
        private final String entityOperation;
        private final String entityName;

        private Key(final String user, final String entityOperation, final String entityName) {
            this.user = user;
            this.entityOperation = entityOperation;
            this.entityName = entityName;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;

            final Key key = (Key) o;

            if (user != null ? !user.equals(key.user) : key.user != null) return false;
            if (!entityOperation.equals(key.entityOperation)) return false;
            return entityName.equals(key.entityName);
        }

        @Override
        public int hashCode() {
            int result = user != null ? user.hashCode() : 0;
            result = 31 * result + entityOperation.hashCode();
            result = 31 * result + entityName.hashCode();
            return result;
        }
    }

    /**
     * Maximum cache eviction items in queue.
     *
     * @param maxQueueSize max size
     */
    public void setMaxQueueSize(final int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }
}
