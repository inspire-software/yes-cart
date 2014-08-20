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

package org.yes.cart.domain.ro;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: denispavlov
 * Date: 19/08/2014
 * Time: 22:16
 */
@XmlRootElement(name = "node")
public class NodeRO {

    private String nodeId;
    private String shopCode;

    @XmlElement(name = "node-id")
    @JsonProperty(value = "node-id")
    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(final String nodeId) {
        this.nodeId = nodeId;
    }

    @XmlElement(name = "shop-code")
    @JsonProperty(value = "shop-code")
    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }
}
