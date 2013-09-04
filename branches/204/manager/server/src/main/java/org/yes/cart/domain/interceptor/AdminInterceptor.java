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
 *
 */

package org.yes.cart.domain.interceptor;

import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.web.service.ws.CacheDirector;
import org.yes.cart.web.service.ws.client.AsyncFlexContextImpl;
import org.yes.cart.web.service.ws.client.CacheDirectorClientFactory;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 01-Sep-2013
 * Time: 16:13:01
 * <p/>
 * Delegate cache eviction to shops in case if operation was performed on some cachable entity.
 */
public class AdminInterceptor extends AuditInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(AdminInterceptor.class);

    private final static Set<String> cachedEntities;

    private final static int defaultTimeout = 60000;

    private CacheDirectorClientFactory cacheDirectorClientFactory;

    private final List<String> cacheDirectorUrl;



    static {

        cachedEntities = new HashSet<String>();

        cachedEntities.add(CacheDirector.EntityName.ATTRIBUTE);
        cachedEntities.add(CacheDirector.EntityName.PRODUCT);
        cachedEntities.add(CacheDirector.EntityName.CATEGORY);
        cachedEntities.add(CacheDirector.EntityName.PRICE);
        cachedEntities.add(CacheDirector.EntityName.PRODUCT_TYPE_ATTR);

        cachedEntities.add(CacheDirector.EntityName.SEO_IMAGE);
        cachedEntities.add(CacheDirector.EntityName.SHOP);
        cachedEntities.add(CacheDirector.EntityName.SYSTEM);

    }

    /**
     * Create admin hibernate interceptor.
     * @param cacheDirectorUrl url to invalidate caches.
     */
    public AdminInterceptor(List<String> cacheDirectorUrl) {
        this.cacheDirectorUrl = cacheDirectorUrl;
    }

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
    public boolean onFlushDirty(Object entity, Serializable serializable, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        try {
            return super.onFlushDirty(entity, serializable, currentState, previousState, propertyNames, types);
        } finally {
            if(isEntityInCache(entity)) {
                invalidateCache(CacheDirector.EntityOperation.UPDATE, entity.getClass().getSimpleName(), getPk(entity) );
            }
        }

    }


    void invalidateCache(final String op, final String entityName, final Long pk) {

        for (String url : cacheDirectorUrl) {

            try {

                getCacheDirector(url).onCacheableChange(op, entityName, pk);

            } catch (Exception e) {

                LOG.error("Cannot invalidate cache for entity [" + entityName  + "] pk value =  [" + pk  + "] url [" + url + "]");


            }

        }

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


    private CacheDirector getCacheDirector(final String cacheDirUrl) {
        final AsyncContext newCtx = new AsyncFlexContextImpl();
        String userName = newCtx.getAttribute(AsyncContext.USERNAME);
        String password = newCtx.getAttribute(AsyncContext.CREDENTIALS);
        return getCacheDirectorClientFactory().getCacheDirector(
                userName,
                password,
                cacheDirUrl,
                defaultTimeout);  //TODO: YC-149 move timeouts to config
    }

    private synchronized CacheDirectorClientFactory getCacheDirectorClientFactory() {
        if (cacheDirectorClientFactory == null) {
            cacheDirectorClientFactory = new CacheDirectorClientFactory();
        }
        return cacheDirectorClientFactory;
    }


}
