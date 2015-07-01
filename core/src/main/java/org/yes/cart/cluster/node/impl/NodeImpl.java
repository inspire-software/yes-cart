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

import org.yes.cart.cluster.node.Node;

/**
 * User: denispavlov
 * Date: 13-10-03
 * Time: 7:03 PM
 */
public class NodeImpl implements Node {

    private boolean current;
    private boolean yum;

    private String id;
    private String nodeId;
    private String nodeType;
    private String nodeConfig;
    private String clusterId;
    private boolean luceneIndexDisabled;

    private String channel;

    public NodeImpl() {
    }

    public NodeImpl(final boolean current,
                    final Node node) {
        this(current, node.getNodeId(), node.getNodeType(), node.getNodeConfig(), node.getClusterId(), node.isLuceneIndexDisabled());
        setChannel(node.getChannel());
    }



    public NodeImpl(final boolean current,
                    final String nodeId,
                    final String nodeType,
                    final String nodeConfig,
                    final String clusterId,
                    final boolean luceneIndexDisabled) {
        this.id = clusterId != null && nodeId != null ? clusterId.concat(".").concat(nodeId) : "N/A";
        this.current = current;
        this.yum = "YUM".equals(nodeType);
        this.nodeId = nodeId;
        this.nodeType = nodeType;
        this.nodeConfig = nodeConfig;
        this.clusterId = clusterId;
        this.luceneIndexDisabled = luceneIndexDisabled;
        this.channel = "";
    }


    /**
     * @return true if this dto represents current server
     */
    public boolean isCurrent() {
        return current;
    }

    /**
     * @param current true if this dto represents current server
     */
    public void setCurrent(final boolean current) {
        this.current = current;
    }

    /**
     * @return true if current node is yum
     */
    public boolean isYum() {
        return yum;
    }

    /**
     * @param yum true if current node is yum
     */
    public void setYum(final boolean yum) {
        this.yum = yum;
    }


    /**
     * @return node id in the cluster
     */
    public String getId() {
        return id;
    }

    /**
     * @param id node id in the cluster
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * @return node id
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * @param nodeId node id
     */
    public void setNodeId(final String nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * @return type of node (YES, API or YUM)
     */
    public String getNodeType() {
        return nodeType;
    }

    /**
     * @param nodeType type of node (YES, API or YUM)
     */
    public void setNodeType(final String nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * @return node config (DEFAULT, STAGING, PRODUCTION)
     */
    public String getNodeConfig() {
        return nodeConfig;
    }

    /**
     * @param nodeConfig node config (DEFAULT, STAGING, PRODUCTION)
     */
    public void setNodeConfig(final String nodeConfig) {
        this.nodeConfig = nodeConfig;
    }

    /**
     * @return cluster ID
     */
    public String getClusterId() {
        return clusterId;
    }

    /**
     * @param clusterId cluster ID
     */
    public void setClusterId(final String clusterId) {
        this.clusterId = clusterId;
    }

    /**
     * @return flag if lucene index is disabled
     */
    public boolean isLuceneIndexDisabled() {
        return luceneIndexDisabled;
    }

    /**
     * @param luceneIndexDisabled flag if lucene index is disabled
     */
    public void setLuceneIndexDisabled(final boolean luceneIndexDisabled) {
        this.luceneIndexDisabled = luceneIndexDisabled;
    }

    /**
     * @return channel
     */
    public String getChannel() {
        return channel;
    }

    /**
     * @param channel channel
     */
    public void setChannel(final String channel) {
        this.channel = channel;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final NodeImpl node = (NodeImpl) o;

        if (current != node.current) return false;
        if (luceneIndexDisabled != node.luceneIndexDisabled) return false;
        if (yum != node.yum) return false;
        if (clusterId != null ? !clusterId.equals(node.clusterId) : node.clusterId != null) return false;
        if (nodeConfig != null ? !nodeConfig.equals(node.nodeConfig) : node.nodeConfig != null) return false;
        if (nodeId != null ? !nodeId.equals(node.nodeId) : node.nodeId != null) return false;
        if (nodeType != null ? !nodeType.equals(node.nodeType) : node.nodeType != null) return false;

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = (current ? 1 : 0);
        result = 31 * result + (yum ? 1 : 0);
        result = 31 * result + (nodeId != null ? nodeId.hashCode() : 0);
        result = 31 * result + (nodeType != null ? nodeType.hashCode() : 0);
        result = 31 * result + (nodeConfig != null ? nodeConfig.hashCode() : 0);
        result = 31 * result + (clusterId != null ? clusterId.hashCode() : 0);
        result = 31 * result + (luceneIndexDisabled ? 1 : 0);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "NodeImpl{" +
                "current=" + current +
                ", yum=" + yum +
                ", nodeId='" + nodeId + '\'' +
                ", nodeType='" + nodeType + '\'' +
                ", nodeConfig='" + nodeConfig + '\'' +
                ", clusterId='" + clusterId + '\'' +
                ", luceneIndexDisabled=" + luceneIndexDisabled +
                '}';
    }
}
