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
 *
 */

package org.yes.cart.domain.interceptor;

import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.cluster.service.CacheDirector;
import org.yes.cart.cluster.service.CacheEvictionQueue;
import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.utils.ThreadLocalAsyncContextUtils;
import org.yes.cart.utils.spring.LinkedHashMapBean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 01-Sep-2013
 * Time: 16:13:01
 * <p/>
 * Delegate cache eviction to shops in case if operation was performed on a cacheable entity.
 */
public class AdminInterceptor extends AuditInterceptor implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(AdminInterceptor.class);

    private ApplicationContext applicationContext;
    private CacheEvictionQueue cacheEvictionQueue;

    private Map<String, Map<String, Set<Pair<String, String>>>> entityOperationCache;
    private Set<String> cachedEntities;

    @Override
    public boolean onSave(Object entity, Serializable serializable, Object[] objects, String[] propertyNames, Type[] types) {

        try {
            return super.onSave(entity, serializable, objects, propertyNames, types);
        } finally {
            if(isEntityInCache(entity)) {
                invalidateCache(CacheDirector.EntityOperation.CREATE, entity.getClass().getSimpleName(), getPk(entity) );
            }
        }

    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

        try {
            super.onDelete(entity, id, state, propertyNames, types);
        } finally {
            if(isEntityInCache(entity)) {
                invalidateCache(CacheDirector.EntityOperation.DELETE, entity.getClass().getSimpleName(), getPk(entity) );
            }

        }

    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        try {
            return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
        } finally {
            if(isEntityInCache(entity)) {
                invalidateCache(CacheDirector.EntityOperation.UPDATE, entity.getClass().getSimpleName(), getPk(entity) );
            }
        }

    }


    void invalidateCache(final String op, final String entityName, final Long pk) {

        if (cacheEvictionQueue == null) {
            synchronized (this) {
                if (cacheEvictionQueue == null) {
                    cacheEvictionQueue = applicationContext.getBean("cacheEvictionQueue", CacheEvictionQueue.class);
                }
            }
        }

        if (isBroadcastAllowed()) {
            cacheEvictionQueue.enqueue(op, entityName, pk);
        }

    }

    private boolean isBroadcastAllowed() {

        final AsyncContext jobContext = ThreadLocalAsyncContextUtils.getContext();

        if (jobContext != null && AsyncContext.NO_BROADCAST.equals(jobContext.getAttribute(AsyncContext.NO_BROADCAST))) {
            return false; // cache evict is off
        }

        return true;

    }

    private Long getPk(final Object entity) {
        if (entity instanceof Identifiable) {
            return ((Identifiable) entity).getId();
        }
        return null;
    }


    boolean isEntityInCache(final Object entity) {
        if (entity != null) {
            final String entityName = entity.getClass().getSimpleName();
            return cachedEntities.contains(entityName);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /** IoC. Set configuration. */
    public void setEntityOperationCache(final LinkedHashMapBean<String, Map<String, Set<Pair<String, String>>>> entityOperationCache) {
        this.entityOperationCache = entityOperationCache;
        this.cachedEntities = new HashSet<>(this.entityOperationCache.keySet());
    }
}
