/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

import org.apache.commons.lang.StringUtils;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.node.RspMessage;
import org.yes.cart.cluster.node.impl.ContextRspMessageImpl;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.utils.RunAsUserAuthentication;
import org.yes.cart.service.async.utils.ThreadLocalAsyncContextUtils;
import org.yes.cart.web.service.ws.CacheDirector;
import org.yes.cart.web.service.ws.client.AsyncContextFactory;

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

    private static final Logger LOG = LoggerFactory.getLogger(AdminInterceptor.class);

    private ApplicationContext applicationContext;
    private NodeService nodeService;
    private AsyncContextFactory asyncContextFactory;

    private Map<String, Map<String, Set<Pair<String, String>>>> entityOperationCache;
    private Set<String> cachedEntities;

    private TaskExecutor executor;

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

        if (executor != null) {
            if (nodeService == null) {
                synchronized (this) {
                    if (nodeService == null) {
                        nodeService = applicationContext.getBean("nodeService", NodeService.class);
                    }
                    if (asyncContextFactory == null) {
                        asyncContextFactory = applicationContext.getBean("webAppManagerAsyncContextFactory", AsyncContextFactory.class);
                    }
                }
            }

            final Runnable evictCache = this.createEvictCacheRunnable(op, entityName, pk);

            this.executor.execute(evictCache);
        }

    }

    private Runnable createEvictCacheRunnable(final String op, final String entityName, final Long pk) {

        final AsyncContext jobContext = ThreadLocalAsyncContextUtils.getContext();
        final Authentication auth = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication() : null;
        final String username = auth != null && auth.isAuthenticated() ? auth.getName() : null;

        return new Runnable() {

            @Override
            public void run() {

                try {

                    final AsyncContext threadContext;
                    if (StringUtils.isBlank(username)) {
                        threadContext = jobContext;
                    } else {
                        SecurityContextHolder.getContext().setAuthentication(new RunAsUserAuthentication(username, "", Collections.EMPTY_LIST));
                        final Map<String, Object> params = new HashMap<String, Object>();
                        params.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS);
                        threadContext = asyncContextFactory.getInstance(params);
                    }

                    if (threadContext == null) {
                        LOG.debug("Cannot invalidate cache for entity [" + entityName + "] pk value =  [" + pk + "] - no async context ");
                        return;
                    }


                    final List<Node> cluster = nodeService.getSfNodes();
                    final List<String> targets = new ArrayList<String>();
                    for (final Node node : cluster) {
                        targets.add(node.getId());
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
                            threadContext
                    );

                    nodeService.broadcast(message);


                } catch (Exception exp) {
                    LOG.error("Unable to perform cache eviction: " + exp.getMessage(), exp);
                } finally {
                    SecurityContextHolder.clearContext();
                }

            }
        };

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
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /** IoC. Set configuration. */
    public void setEntityOperationCache(final Map<String, Map<String, Set<Pair<String, String>>>> entityOperationCache) {
        this.entityOperationCache = entityOperationCache;
        this.cachedEntities = new HashSet<String>(this.entityOperationCache.keySet());
    }

    /** IoC. Set configuration. */
    public void setEntityOperationCacheExecutor(final TaskExecutor executor) {
        this.executor = executor;
    }
}
