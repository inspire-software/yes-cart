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

package org.yes.cart.web.service.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.ro.NodeRO;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.node.Node;

/**
 * User: denispavlov
 * Date: 19/08/2014
 * Time: 21:45
 */
@Controller
@RequestMapping("/node")
public class NodeController {

    @Autowired
    private NodeService nodeService;


    /**
     * Interface: GET /yes-api/rest/node
     * <p>
     * <p>
     * Node interface returns basic node information.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * NONE
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON example</td><td>
     * <pre><code>
     * {
     *    "shopCode" : "SHOP10",
     *    "nodeId" : "YES1"
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example</td><td>
     * <pre><code>
     * &lt;node&gt;
     *    &lt;node-id&gt;YES1&lt;/node-id&gt;
     *    &lt;shop-code&gt;SHOP10&lt;/shop-code&gt;
     * &lt;/node&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * @return node information
     */
    @RequestMapping(
            value = "",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody NodeRO index() {

        final Node node = nodeService.getCurrentNode();

        final NodeRO nodeRO = new NodeRO();
        nodeRO.setNodeId(node.getId());
        nodeRO.setShopCode(ShopCodeContext.getShopCode());
        return nodeRO;
    }

}
