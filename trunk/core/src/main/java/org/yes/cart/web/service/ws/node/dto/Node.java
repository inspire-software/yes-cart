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

package org.yes.cart.web.service.ws.node.dto;

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
    boolean isYum();

    /**
     * @return node id
     */
    String getNodeId();

    /**
     * @return node backdoor URI
     */
    String getBackdoorUri();

    /**
     * @return node cache manager URI
     */
    String getCacheManagerUri();
}
