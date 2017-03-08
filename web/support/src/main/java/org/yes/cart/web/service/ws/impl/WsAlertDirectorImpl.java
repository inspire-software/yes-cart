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
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.service.ws.AlertDirector;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * User: denispavlov
 * Date: 07/03/2017
 * Time: 20:37
 */
public class WsAlertDirectorImpl extends AlertDirectorImpl implements AlertDirector {

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

        nodeService.subscribe("AlertDirector.getAlerts", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                return new ArrayList<Pair<String, String>>(WsAlertDirectorImpl.this.getAlerts());
            }
        });
        nodeService.subscribe("AlertDirector.clear", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {
                WsAlertDirectorImpl.this.clear();
                return "OK";
            }
        });
    }

}
