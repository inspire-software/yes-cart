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

package org.yes.cart.web.service.ws.node;

import org.yes.cart.web.service.ws.node.dto.Node;

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
    /** Backdoor URI context parameter */
    String BACKDOOR_URI = "BACKDOOR_URI";
    /** Backdoor URI context parameter */
    String CACHEDIRECTOR_URI = "CACHEDIRECTOR_URI";
    /** Lucene index disabled (use to suppress indexing jobs if yes-shop and yes-api on the same Servlet container) */
    String LUCENE_INDEX_DISABLED = "LUCENE_INDEX_DISABLED";

    /**
     * Current node identification. This should be set in the
     * Context.xml for YUM:
     *   <Parameter name="NODE_ID" value="YUM" override="false"/>
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
     * E.g. if we have YUM0, YES0, YES1 and YES2 nodes
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
     * YUM node in this cluster.
     *
     * @return YUM node
     */
    Node getYumNode();

    /**
     * YesCart storefront nodes.
     *
     * @return YeS nodes
     */
    List<Node> getYesNodes();

    /**
     * YesCart storefront nodes excluding this one if this node is YeS.
     *
     * E.g. if we have YUM0, YES0, YES1 and YES2 nodes
     * and current node is YUM0 then nodes returned are YES0, YES1 and YES2 nodes.
     * and current node is YES0 then nodes returned are YES1 and YES2 nodes.
     * and current node is YES1 then nodes returned are YES0 and YES2 nodes.
     * and current node is YES2 then nodes returned are YES0 and YES1 nodes.
     *
     * @return YeS nodes
     */
    List<Node> getOtherYesNodes();

}
