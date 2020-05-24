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

package org.yes.cart.web.service.rest;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.node.RspMessage;
import org.yes.cart.cluster.node.impl.RspMessageImpl;

/**
 * User: denispavlov
 * Date: 25/05/2019
 * Time: 10:48
 */
@Controller
@RequestMapping("/connector")
public class WebsupportRestServiceInboundChannel {


    private static final Logger LOG = LoggerFactory.getLogger(WebsupportRestServiceInboundChannel.class);

    private NodeService nodeService;

    /**
     * {@inheritDoc}
     */
    @RequestMapping(
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody void ping() {
        LOG.info("Ping on {}", nodeService.getCurrentNode().getId());
    }

    /**
     * {@inheritDoc}
     */
    @RequestMapping(
            method = RequestMethod.POST,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody RestMessage accept(final @RequestBody RestMessage inbound) {

        final RspMessage wrapped = new RspMessageImpl(
                inbound.getSource(),
                inbound.getTargets(),
                inbound.getSubject(),
                inbound.getPayloadObject()
        );

        // broadcast to self
        nodeService.broadcast(wrapped);

        if (CollectionUtils.isNotEmpty(wrapped.getResponses())) {
            return new RestMessage(wrapped.getResponses().get(0));
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
