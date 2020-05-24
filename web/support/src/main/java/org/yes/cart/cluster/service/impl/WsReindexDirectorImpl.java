/*
 * Copyright 2009 Inspire-Software.com
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
package org.yes.cart.cluster.service.impl;

import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.service.ReindexDirector;

/**
 * User: denispavlov
 * Date: 13/04/2019
 * Time: 17:11
 */
public class WsReindexDirectorImpl extends ReindexDirectorImpl implements ReindexDirector {

    /**
     * Spring IoC.
     *
     * @param nodeService node service
     */
    public void setNodeService(final NodeService nodeService) {

        super.setNodeService(nodeService);

        nodeService.subscribe("ReindexDirector.getProductReindexingState", message -> WsReindexDirectorImpl.this.getProductReindexingState());
        nodeService.subscribe("ReindexDirector.getProductSkuReindexingState", message -> WsReindexDirectorImpl.this.getProductSkuReindexingState());
        nodeService.subscribe("ReindexDirector.reindexAllProducts", message -> {
            WsReindexDirectorImpl.this.reindexAllProducts();
            return "OK";
        });
        nodeService.subscribe("ReindexDirector.reindexAllProductsSku", message -> {
            WsReindexDirectorImpl.this.reindexAllProductsSku();
            return "OK";
        });
        nodeService.subscribe("ReindexDirector.reindexShopProducts", message -> {
            WsReindexDirectorImpl.this.reindexShopProducts((Long) message.getPayload());
            return "OK";
        });
        nodeService.subscribe("ReindexDirector.reindexShopProductsSku", message -> {
            WsReindexDirectorImpl.this.reindexShopProductsSku((Long) message.getPayload());
            return "OK";
        });
        nodeService.subscribe("ReindexDirector.reindexProduct", message -> {
            WsReindexDirectorImpl.this.reindexProduct((Long) message.getPayload());
            return "OK";
        });
        nodeService.subscribe("ReindexDirector.reindexProductSku", message -> {
            WsReindexDirectorImpl.this.reindexProductSku((Long) message.getPayload());
            return "OK";
        });
        nodeService.subscribe("ReindexDirector.reindexProductSkuCode", message -> {
            WsReindexDirectorImpl.this.reindexProductSkuCode((String) message.getPayload());
            return "OK";
        });
        nodeService.subscribe("ReindexDirector.reindexProducts", message -> {
            WsReindexDirectorImpl.this.reindexProducts((long[]) message.getPayload());
            return "OK";
        });
    }

}
