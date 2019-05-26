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

package org.yes.cart.cluster.node.impl;

import org.yes.cart.cluster.node.Message;
import org.yes.cart.cluster.node.MessageListener;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.cluster.node.NodeService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 26/05/2019
 * Time: 12:55
 */
public class NoopNodeServiceImpl implements NodeService {

    @Override
    public String getCurrentNodeId() {
        return "TEST";
    }

    @Override
    public Map<String, String> getConfiguration() {
        return Collections.emptyMap();
    }

    @Override
    public List<Node> getCluster() {
        return Collections.emptyList();
    }

    @Override
    public List<Node> getBlacklisted() {
        return Collections.emptyList();
    }

    @Override
    public Node getCurrentNode() {
        return new NodeImpl();
    }

    @Override
    public Node getAdminNode() {
        return new NodeImpl();
    }

    @Override
    public List<Node> getSfNodes() {
        return Collections.emptyList();
    }

    @Override
    public List<Node> getOtherSfNodes() {
        return Collections.emptyList();
    }

    @Override
    public void broadcast(final Message message) {
        // do nothing
    }

    @Override
    public void subscribe(final String subject, final MessageListener listener) {
        // do nothing
    }
}
