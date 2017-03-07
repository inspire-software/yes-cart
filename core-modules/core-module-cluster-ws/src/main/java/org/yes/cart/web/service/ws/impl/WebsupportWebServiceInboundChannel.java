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

package org.yes.cart.web.service.ws.impl;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.node.RspMessage;
import org.yes.cart.cluster.node.impl.RspMessageImpl;
import org.yes.cart.web.service.ws.WebServiceInboundChannel;
import org.yes.cart.web.service.ws.WsMessage;

import javax.jws.WebService;
import javax.xml.ws.BindingType;

/**
 * User: denispavlov
 * Date: 17/06/2015
 * Time: 13:26
 */
@WebService(endpointInterface = "org.yes.cart.web.service.ws.WebServiceInboundChannel",
        serviceName = "WebServiceInboundChannel")
@BindingType(value=javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_MTOM_BINDING)
public class WebsupportWebServiceInboundChannel implements WebServiceInboundChannel {

    private static final Logger LOG = LoggerFactory.getLogger(WebsupportWebServiceInboundChannel.class);

    private NodeService nodeService;

    /**
     * {@inheritDoc}
     */
    public void ping() {
        LOG.info("Ping on {}", nodeService.getCurrentNode().getId());
    }

    /**
     * {@inheritDoc}
     */
    public WsMessage accept(final WsMessage inbound) {

        final RspMessage wrapped = new RspMessageImpl(
                inbound.getSource(),
                inbound.getTargets(),
                inbound.getSubject(),
                inbound.getPayloadObject()
        );

        // broadcast to self
        nodeService.broadcast(wrapped);

        if (CollectionUtils.isNotEmpty(wrapped.getResponses())) {
            return new WsMessage(wrapped.getResponses().get(0));
        }
        return null;

    }

    /**
     * Spring IoC.
     *
     * @param nodeService node service
     */
    public void setNodeService(final NodeService nodeService) {
        this.nodeService = nodeService;
    }
}
