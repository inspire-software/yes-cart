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
 *
 */

package org.yes.cart.domain.interceptor;

import org.hibernate.type.Type;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.node.RspMessage;
import org.yes.cart.cluster.node.impl.ContextRspMessageImpl;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.utils.ThreadLocalAsyncContextUtils;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.service.ws.CacheDirector;
import org.yes.cart.web.service.ws.client.AsyncFlexContextImpl;

import java.io.Serializable;
import java.util.*;

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

        if (nodeService == null) {
            synchronized (this) {
                if (nodeService == null) {
                    nodeService = applicationContext.getBean("nodeService", NodeService.class);
                }
            }
        }

        final AsyncContext async = getAsyncContext();
        if (async == null) {
            ShopCodeContext.getLog(this)
                    .warn("Cannot invalidate cache for entity [" + entityName + "] pk value =  [" + pk + "] - no async context ");
            return;
        }

        final List<Node> cluster = nodeService.getYesNodes();
        final List<String> targets = new ArrayList<String>();
        for (final Node node : cluster) {
            targets.add(node.getNodeId());
        }

        final HashMap<String, Object> payload = new HashMap<String, Object>();
        payload.put("entityOperation", op);
        payload.put("entityName", entityName);
        payload.put("pkValue", pk);

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "CacheDirector.onCacheableChange",
                payload,
                async
        );

        nodeService.broadcast(message);

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
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
            return new AsyncFlexContextImpl(params);
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
