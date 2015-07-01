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

package org.yes.cart.remote.service.impl;

import org.yes.cart.cluster.node.Message;
import org.yes.cart.cluster.node.impl.ContextRspMessageImpl;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.remote.service.RemoteNodeService;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.web.service.ws.client.AsyncFlexContextImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-10-07
 * Time: 2:33 PM
 */
public class RemoteNodeServiceImpl implements RemoteNodeService {

    private final NodeService nodeService;

    public RemoteNodeServiceImpl(final NodeService nodeService) {
        this.nodeService = nodeService;
    }

    /** {@inheritDoc} */
    public List<Node> getCluster() {

        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_TIMEOUT_MS);
        final AsyncContext context = new AsyncFlexContextImpl(param);

        final Message message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                "BackdoorService.ping",
                null,
                context
        );

        // Broadcasting ping message should result in refreshing of the cluster view
        nodeService.broadcast(message);

        return nodeService.getCluster();
    }
}
