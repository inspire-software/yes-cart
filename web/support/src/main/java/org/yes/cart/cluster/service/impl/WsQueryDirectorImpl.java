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
package org.yes.cart.cluster.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.service.QueryDirector;
import org.yes.cart.cluster.service.QueryDirectorPlugin;
import org.yes.cart.domain.misc.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13/04/2019
 * Time: 17:19
 */
public class WsQueryDirectorImpl extends QueryDirectorImpl implements QueryDirector {

    private static final Logger LOG = LoggerFactory.getLogger(WsQueryDirectorImpl.class);

    public WsQueryDirectorImpl(final List<QueryDirectorPlugin> plugins) {
        super(plugins);
    }

    /**
     * Spring IoC.
     *
     * @param nodeService node service
     */
    public void setNodeService(final NodeService nodeService) {

        nodeService.subscribe("QueryDirector.supportedQueries", message -> {
            try {
                return new ArrayList<String>((List) WsQueryDirectorImpl.this.supportedQueries());
            } catch (Exception e) {
                final String msg = "Can not retrieve list of supported queries. Error : " + e.getMessage();
                LOG.warn(msg);
                return new ArrayList<>(Collections.singletonList(e.getMessage()));
            }
        });

        nodeService.subscribe("QueryDirector.runQuery", message -> {
            try {
                final Pair<String, String> typeAndQuery = (Pair<String, String>) message.getPayload();
                return new ArrayList<Serializable[]>((List) WsQueryDirectorImpl.this.runQuery(typeAndQuery.getFirst(), typeAndQuery.getSecond()));
            } catch (Exception e) {
                final String msg = "Can not parse query: " + message.getPayload() + ". Error : " + e.getMessage();
                LOG.warn(msg);
                return new ArrayList<>(Collections.singletonList(new Serializable[]{e.getMessage()}));
            }
        });
    }

}
