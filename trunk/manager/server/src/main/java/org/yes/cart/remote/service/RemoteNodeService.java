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

package org.yes.cart.remote.service;

import org.yes.cart.web.service.ws.node.dto.Node;

import java.util.List;

/**
 * User: denispavlov
 * Date: 13-10-07
 * Time: 2:22 PM
 */
public interface RemoteNodeService {

    /**
     * All registered nodes in this cluster.
     *
     * E.g. if we have YUM0, YES0, YES1 and YES2 nodes
     * this methods should return four nodes.
     *
     * @return node objects
     */
    List<Node> getCluster();

}
