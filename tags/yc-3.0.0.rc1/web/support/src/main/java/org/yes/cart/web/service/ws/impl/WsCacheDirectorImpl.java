/*
 * Copyright 2013 Denys Pavlov, Igor Azarnyi
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

import org.yes.cart.cluster.node.Message;
import org.yes.cart.cluster.node.MessageListener;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.domain.dto.impl.CacheInfoDTOImpl;
import org.yes.cart.web.service.ws.CacheDirector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Service responsible  to evict particular cache(s) depending on entity and operation.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 18 Aug 2013
 * Time: 9:50 AM
 */
public class WsCacheDirectorImpl extends CacheDirectorImpl implements CacheDirector {

    private NodeService nodeService;

    public NodeService getNodeService() {
        return nodeService;
    }

    /**
     * Spring IoC.
     *
     * @param nodeService node service
     */
    public void setNodeService(final NodeService nodeService) {

        this.nodeService = nodeService;

        nodeService.subscribe("CacheDirector.getCacheInfo", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                final Node node = nodeService.getCurrentNode();
                final ArrayList<CacheInfoDTOImpl> caches = new ArrayList<CacheInfoDTOImpl>();
                for (final CacheInfoDTOImpl cache : WsCacheDirectorImpl.this.getCacheInfo()) {
                    cache.setNodeId(node.getNodeId());
                    cache.setNodeUri(node.getChannel());
                    caches.add(cache);
                }
                return caches;
            }
        });
        nodeService.subscribe("CacheDirector.evictAllCache", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                WsCacheDirectorImpl.this.evictAllCache();
                return "OK";
            }
        });
        nodeService.subscribe("CacheDirector.evictCache", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                WsCacheDirectorImpl.this.evictCache((String) message.getPayload());
                return "OK";
            }
        });
        nodeService.subscribe("CacheDirector.enableStats", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                WsCacheDirectorImpl.this.enableStats((String) message.getPayload());
                return "OK";
            }
        });
        nodeService.subscribe("CacheDirector.disableStats", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                WsCacheDirectorImpl.this.disableStats((String) message.getPayload());
                return "OK";
            }
        });
        nodeService.subscribe("CacheDirector.onCacheableChange", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {

                final Map<String, Object> payload = (Map<String, Object>) message.getPayload();

                return WsCacheDirectorImpl.this.onCacheableChange(
                        (String) payload.get("entityOperation"),
                        (String) payload.get("entityName"),
                        (Long) payload.get("pkValue")
                );

            }
        });
    }

}
