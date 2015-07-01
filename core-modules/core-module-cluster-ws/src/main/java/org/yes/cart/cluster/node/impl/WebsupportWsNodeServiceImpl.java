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

package org.yes.cart.cluster.node.impl;

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.cluster.node.Message;
import org.yes.cart.cluster.node.MessageListener;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.node.RspMessage;
import org.yes.cart.service.domain.SystemService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: denispavlov
 * Date: 17/06/2015
 * Time: 13:47
 */
public class WebsupportWsNodeServiceImpl extends AbstractWsNodeServiceImpl implements NodeService {

    public WebsupportWsNodeServiceImpl(final SystemService systemService) {
        super(systemService);
    }

    /**
     * {@inheritDoc}
     */
    public void broadcast(final Message message) {

        LOG.debug("Sending message locally: {}", message);

        final List<MessageListener> subjectListeners = listeners.get(message.getSubject());
        if (CollectionUtils.isNotEmpty(subjectListeners)) {

            final ArrayList<Serializable> rsp = new ArrayList<Serializable>();
            for (final MessageListener listener : subjectListeners) {

                rsp.add(listener.onMessageReceived(message));

            }

            if (message instanceof RspMessage) {

                if (rsp.size() > 1) {
                    ((RspMessage) message).addResponse(new BasicMessageImpl(
                            getCurrentNodeId(),
                            Arrays.asList(message.getSource()),
                            message.getSubject(),
                            rsp)); // if many listeners return list
                } else if (rsp.size() == 1) {
                    ((RspMessage) message).addResponse(new BasicMessageImpl(
                            getCurrentNodeId(),
                            Arrays.asList(message.getSource()),
                            message.getSubject(),
                            rsp.get(0))); // otherwise return first
                }

            }

        } else {
            LOG.warn("No listeners for message: {}", message);
        }

    }
}
