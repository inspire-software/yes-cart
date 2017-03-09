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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.cluster.node.ContextRspMessage;
import org.yes.cart.cluster.node.Message;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.util.log.Markers;
import org.yes.cart.web.service.ws.WebServiceInboundChannel;
import org.yes.cart.web.service.ws.WsMessage;
import org.yes.cart.web.service.ws.client.WsClientAbstractFactory;
import org.yes.cart.web.service.ws.client.WsClientFactory;

import java.net.ConnectException;
import java.util.*;

/**
 * User: denispavlov
 * Date: 17/06/2015
 * Time: 13:46
 */
public class ManagerWsNodeServiceImpl extends AbstractWsNodeServiceImpl implements NodeService {

    private final WsClientAbstractFactory wsClientAbstractFactory;

    private Set<String> reloadClusterTopics = new HashSet<String>();

    public ManagerWsNodeServiceImpl(final SystemService systemService,
                                    final WsClientAbstractFactory wsClientAbstractFactory) {
        super(systemService);
        this.wsClientAbstractFactory = wsClientAbstractFactory;
    }

    /**
     * {@inheritDoc}
     */
    public void broadcast(final Message message) {

        if (reloadClusterTopics.contains(message.getSubject())) {
            LOG.info("Reloading cluster information before {}", message.getSubject());
            reloadClusterConfiguration();
        }

        final ContextRspMessage wsMessage = (ContextRspMessage) message;
        final AsyncContext context = wsMessage.getAsyncContext();

        if (AsyncContext.NO_BROADCAST.equals(context.getAttribute(AsyncContext.NO_BROADCAST))) {
            LOG.debug("Broadcasting switched off for context of message {}", message.getSubject());
            return;
        }

        final List<String> targets = message.getTargets();

        final List<Node> cluster = new ArrayList<Node>(getSfNodes());
        if (CollectionUtils.isNotEmpty(targets)) {
            final Iterator<Node> clusterIt = cluster.iterator();
            while (clusterIt.hasNext()) {
                if (!targets.contains(clusterIt.next().getId())) {
                    clusterIt.remove();
                }
            }
        }


        for (final Node yesNode : cluster) {
            try {
                final WsClientFactory<WebServiceInboundChannel> factory =
                        getWebServiceInboundChannel(context, yesNode.getChannel(),
                                (String) context.getAttribute(AsyncContext.TIMEOUT_KEY));

                WebServiceInboundChannel service = factory.getService();
                try {
                    final WsMessage rsp = service.accept(new WsMessage(wsMessage));
                    if (rsp instanceof WsMessage) {
                        wsMessage.addResponse(
                                new BasicMessageImpl(
                                        rsp.getSource(),
                                        rsp.getTargets(),
                                        rsp.getSubject(),
                                        rsp.getPayloadObject()
                                ));
                    }
                } finally {
                    factory.release(service);
                    service = null;
                }

            } catch (javax.xml.ws.WebServiceException wse) {

                if (wse.getCause() instanceof ConnectException) {

                    blacklist(yesNode.getId());

                    if (LOG.isErrorEnabled()) {
                        LOG.error(Markers.alert(), "Node message failure [" + message + "] to  url ["
                                + yesNode.getId() + "] . Blacklisting this node",
                                wse);
                    }

                } else {

                    if (LOG.isErrorEnabled()) {
                        LOG.error(Markers.alert(), "Node message failure [" + message + "] to  url ["
                                + yesNode.getId() + "] . Exception occurred during ws call",
                                wse);
                    }

                }

            } catch (Exception e) {

                if (LOG.isErrorEnabled()) {
                    LOG.error(Markers.alert(), "Node message failure [" + message + "] to  url ["
                            + yesNode.getId() + "] . Exception occurred during ws call",
                            e);
                }

            }
        }

    }


    private WsClientFactory<WebServiceInboundChannel> getWebServiceInboundChannel(final AsyncContext context,
                                                                                  final String backdoorUrl,
                                                                                  final String timeoutKey) {


        final String userName = context.getAttribute(AsyncContext.USERNAME);
        final String password = context.getAttribute(AsyncContext.CREDENTIALS);
        final String passwordHash = context.getAttribute(AsyncContext.CREDENTIALS_HASH);
        final boolean hashed = StringUtils.isNotBlank(passwordHash);
        final String pwd = hashed ? passwordHash : password;

        final int timeout = NumberUtils.toInt(getConfiguration().get(timeoutKey), 1000);

        return wsClientAbstractFactory.getFactory(WebServiceInboundChannel.class, userName, pwd, hashed, backdoorUrl, timeout);

    }


    /**
     * Spring IoC.
     *
     * @param reloadClusterTopics topics that will cause reloading of cluster xml mapping
     */
    public void setReloadClusterTopics(final Set<String> reloadClusterTopics) {
        this.reloadClusterTopics = new HashSet<String>(reloadClusterTopics);
    }
}
