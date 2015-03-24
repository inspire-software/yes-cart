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

package org.yes.cart.web.service.ws.node.dto.impl;

import org.yes.cart.web.service.ws.node.dto.Node;

/**
 * User: denispavlov
 * Date: 13-10-03
 * Time: 7:03 PM
 */
public class NodeImpl implements Node {

    private boolean current;
    private boolean yum;
    private String nodeId;
    private String backdoorUri;
    private String cacheManagerUri;

    public NodeImpl() {
    }

    public NodeImpl(final boolean current,
                    final String nodeId,
                    final String backdoorUri,
                    final String cacheManagerUri) {
        this.current = current;
        this.nodeId = nodeId;
        this.backdoorUri = backdoorUri;
        this.cacheManagerUri = cacheManagerUri;
        this.yum = nodeId != null && nodeId.startsWith("YUM");
    }


    /**
     * @return true if this dto represents current server
     */
    @Override
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
    @Override
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
     * @return node id
     */
    @Override
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
     * @return node backdoor URI
     */
    @Override
    public String getBackdoorUri() {
        return backdoorUri;
    }

    /**
     * @param backdoorUri node backdoor URI
     */
    public void setBackdoorUri(final String backdoorUri) {
        this.backdoorUri = backdoorUri;
    }

    /**
     * @return node cache manager URI
     */
    @Override
    public String getCacheManagerUri() {
        return cacheManagerUri;
    }

    /**
     * @param cacheManagerUri node cache manager URI
     */
    public void setCacheManagerUri(final String cacheManagerUri) {
        this.cacheManagerUri = cacheManagerUri;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final NodeImpl node = (NodeImpl) o;

        if (current != node.current) return false;
        if (yum != node.yum) return false;
        if (backdoorUri != null ? !backdoorUri.equals(node.backdoorUri) : node.backdoorUri != null) return false;
        if (cacheManagerUri != null ? !cacheManagerUri.equals(node.cacheManagerUri) : node.cacheManagerUri != null)
            return false;
        if (nodeId != null ? !nodeId.equals(node.nodeId) : node.nodeId != null) return false;

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = (current ? 1 : 0);
        result = 31 * result + (yum ? 1 : 0);
        result = 31 * result + (nodeId != null ? nodeId.hashCode() : 0);
        result = 31 * result + (backdoorUri != null ? backdoorUri.hashCode() : 0);
        result = 31 * result + (cacheManagerUri != null ? cacheManagerUri.hashCode() : 0);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "NodeImpl{" +
                "current=" + current +
                ", yum=" + yum +
                ", nodeId='" + nodeId + '\'' +
                ", backdoorUri='" + backdoorUri + '\'' +
                ", cacheManagerUri='" + cacheManagerUri + '\'' +
                '}';
    }
}
