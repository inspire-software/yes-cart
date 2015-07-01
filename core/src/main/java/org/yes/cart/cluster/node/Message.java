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
import java.util.List;

/**
 * User: denispavlov
 * Date: 16/06/2015
 * Time: 10:34
 */
public interface Message extends Serializable {

    final Serializable LOST = new Serializable() { };

    /**
     * @return nodeId of the message sender ({@link org.yes.cart.cluster.node.Node#getNodeId()})
     */
    String getSource();

    /**
     * @return list of nodeId's for intended recipients or null if intended for all nodes
     *         ({@link org.yes.cart.cluster.node.Node#getNodeId()})
     */
    List<String> getTargets();

    /**
     * @return message subject (e.g. HELLO, CLEARCACHE)
     */
    String getSubject();

    /**
     * @return actual message
     */
    Serializable getPayload();
}
