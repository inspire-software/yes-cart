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
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.utils.ThreadLocalAsyncContextUtils;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.service.ws.CacheDirector;
import org.yes.cart.web.service.ws.client.AsyncFlexContextImpl;
import org.yes.cart.web.service.ws.client.WsAbstractFactoryClientFactory;
import org.yes.cart.web.service.ws.client.WsClientFactory;
import org.yes.cart.web.service.ws.node.NodeService;
import org.yes.cart.web.service.ws.node.dto.Node;

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

    private ApplicationContext applicationContext;
    private NodeService nodeService;
    private WsAbstractFactoryClientFactory wsAbstractFactoryClientFactory;

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

        if (nodeService == null) {
            synchronized (this) {
                if (nodeService == null) {
                    nodeService = applicationContext.getBean("nodeService", NodeService.class);
                    wsAbstractFactoryClientFactory = applicationContext.getBean("wsAbstractFactoryClientFactory", WsAbstractFactoryClientFactory.class);
                }
            }
        }

        final AsyncContext async = getAsyncContext();
        if (async == null) {
            ShopCodeContext.getLog(this)
                    .error("Cannot invalidate cache for entity [" + entityName + "] pk value =  [" + pk + "] - no async context ");
            return;
        }
        String userName = async.getAttribute(AsyncContext.USERNAME);
        String password = async.getAttribute(AsyncContext.CREDENTIALS);
        final int timeout = Integer.parseInt(nodeService.getConfiguration().get(AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS));

        for (final Node node : nodeService.getYesNodes()) {

            try {

                final WsClientFactory<CacheDirector> factory =
                        wsAbstractFactoryClientFactory.getFactory(CacheDirector.class, userName, password, node.getCacheManagerUri(), timeout);
                CacheDirector wsCacheDirector = factory.getService();
                wsCacheDirector.onCacheableChange(op, entityName, pk);
                factory.release(wsCacheDirector);
                wsCacheDirector = null;

            } catch (Exception e) {

                ShopCodeContext.getLog(this)
                        .error("Cannot invalidate cache for entity [" + entityName
                                + "] pk value =  [" + pk + "] url [" + node.getNodeId() + ":" + node.getCacheManagerUri() + "]");

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


    private AsyncContext getAsyncContext() {

        try {
            return new AsyncFlexContextImpl();
        } catch (IllegalStateException ise) {
            // if we are here we are in async op already
            return ThreadLocalAsyncContextUtils.getContext();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /** IoC. Set configuration. */
    public void setEntityOperationCache(final Map<String, Map<String, Set<Pair<String, String>>>> entityOperationCache) {
        this.entityOperationCache = entityOperationCache;
        this.cachedEntities = new HashSet<String>(this.entityOperationCache.keySet());
    }

}
