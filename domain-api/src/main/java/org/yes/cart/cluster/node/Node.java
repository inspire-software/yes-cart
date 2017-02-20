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

import java.io.Serializable;

/**
 * User: denispavlov
 * Date: 13-10-04
 * Time: 6:18 PM
 */
public interface Node extends Serializable {

    /**
     * @return true if this dto represents current server
     */
    boolean isCurrent();

    /**
     * @return true if current node is yum
     */
    boolean isAdmin();

    /**
     * @return node id in the cluster
     */
    String getId();

    /**
     * @return node id
     */
    String getNodeId();

    /**
     * @return type of node (SF[X], API or ADM; where [X] is storefront classifier)
     */
    String getNodeType();

    /**
     * @return node config (DEFAULT, STAGING, PRODUCTION)
     */
    String getNodeConfig();

    /**
     * @return cluster ID
     */
    String getClusterId();

    /**
     * @return flag if lucene index is disabled
     */
    boolean isFtIndexDisabled();

    /**
     * @return node backdoor URI
     */
    String getChannel();

    /**
     * Application node version.
     *
     * @return version
     */
    String getVersion();

    /**
     * Application node build number.
     *
     * @return build number
     */
    String getBuildNo();

    /**
     * Get full version number.
     *
     * @return full version number
     */
    String getFullVersion();

}
