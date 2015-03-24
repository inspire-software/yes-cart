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
 */

package org.yes.cart.remote.service.impl;

import org.yes.cart.remote.service.RemoteNodeService;
import org.yes.cart.web.service.ws.node.NodeService;
import org.yes.cart.web.service.ws.node.dto.Node;

import java.util.List;

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
        return nodeService.getCluster();
    }
}
