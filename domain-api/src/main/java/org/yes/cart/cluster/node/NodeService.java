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

package org.yes.cart.cluster.node;

import java.util.List;
import java.util.Map;

/**
 * Service for managing node configurations.
 *
 * User: denispavlov
 * Date: 13-10-03
 * Time: 6:43 PM
 */
public interface NodeService {

    /** Node ID context parameter */
    String NODE_ID = "NODE_ID";
    /** Node Type context parameter (SF[X], API or ADM; where [X] is storefront classifier) */
    String NODE_TYPE = "NODE_TYPE";
    /** Node config context parameter (DEFAULT, STAGING or PRODUCTION) */
    String NODE_CONFIG = "NODE_CONFIG";
    /** Version number */
    String VERSION = "VERSION";
    /** Build number */
    String BUILD_NO = "BUILD_NO";
    /** Cluster ID context parameter */
    String CLUSTER_ID = "CLUSTER_ID";
    /** Lucene index disabled (use to suppress indexing jobs if yes-shop and yes-api on the same Servlet container) */
    String LUCENE_INDEX_DISABLED = "LUCENE_INDEX_DISABLED";

    /** WS Channel URI context parameter */
    String CHANNEL_URI = "CHANNEL_URI";

    /**
     * Current node identification. This should be set in the
     * Context.xml for Admin:
     *   <Parameter name="NODE_ID" value="JAM" override="false"/>
     * For storefront context per instance YES[d], where d is node index from 0..n:
     *   <Parameter name="NODE_ID" value="YES0" override="false"/>
     *   <Parameter name="NODE_ID" value="YES1" override="false"/>
     *   <Parameter name="NODE_ID" value="YES2" override="false"/>
     *   ...
     *
     * @return current node identifier
     */
    String getCurrentNodeId();

    /**
     * Context.xml configurations included in this node.
     *
     * @return configurations
     */
    Map<String, String> getConfiguration();

    /**
     * All registered nodes in this cluster.
     *
     * E.g. if we have JAM, YES0, YES1 and YES2 nodes
     * this methods should return four nodes.
     *
     * @return node objects
     */
    List<Node> getCluster();

    /**
     * Current node object.
     *
     * @return this node
     */
    Node getCurrentNode();

    /**
     * Admin node in this cluster.
     *
     * @return Admin node
     */
    Node getAdminNode();

    /**
     * YesCart storefront nodes.
     *
     * @return nodes
     */
    List<Node> getSfNodes();

    /**
     * YesCart storefront nodes excluding this one if this node is YeS.
     *
     * E.g. if we have JAM, YES0, YES1 and YES2 nodes
     * and current node is JAM then nodes returned are YES0, YES1 and YES2 nodes.
     * and current node is YES0 then nodes returned are YES1 and YES2 nodes.
     * and current node is YES1 then nodes returned are YES0 and YES2 nodes.
     * and current node is YES2 then nodes returned are YES0 and YES1 nodes.
     *
     * @return YeS nodes
     */
    List<Node> getOtherSfNodes();

    /**
     * Send message to specified nodes in the cluster.
     *
     * @param message message to broadcast
     */
    void broadcast(Message message);

    /**
     * Subscribe to messages.
     *
     * @param subject subject
     * @param listener listener that will be notified
     */
    void subscribe(final String subject, final MessageListener listener);

}
