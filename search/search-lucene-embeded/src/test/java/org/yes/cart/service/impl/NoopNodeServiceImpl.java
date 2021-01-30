/*
 * Copyright 2009 Inspire-Software.com - All Rights Reserved
 * Unauthorized copying, modification or redistribution of this file
 * via any medium is strictly prohibited without explicit written permission.
 * Proprietary and confidential.
 */

package org.yes.cart.service.impl;

import org.yes.cart.cluster.node.Message;
import org.yes.cart.cluster.node.MessageListener;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.node.impl.NodeImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 10/06/2018
 * Time: 09:36
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

    }

    @Override
    public void subscribe(final String subject, final MessageListener listener) {

    }
}
