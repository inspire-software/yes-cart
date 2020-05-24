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

package org.yes.cart.cluster.node.impl;

import com.fasterxml.jackson.core.JsonParseException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.yes.cart.cluster.node.ContextRspMessage;
import org.yes.cart.cluster.node.Message;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.utils.log.Markers;
import org.yes.cart.web.service.rest.RestMessage;

import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 24/05/2019
 * Time: 21:01
 */
public class ManagerRestNodeServiceImpl extends AbstractRestNodeServiceImpl implements NodeService {

    private final SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
    private final RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);

    private Set<String> reloadClusterTopics = new HashSet<>();

    public ManagerRestNodeServiceImpl(final SystemService systemService) {
        super(systemService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void broadcast(final Message message) {

        final ContextRspMessage restMessage = (ContextRspMessage) message;
        final AsyncContext context = restMessage.getAsyncContext();

        if (AsyncContext.NO_BROADCAST.equals(context.getAttribute(AsyncContext.NO_BROADCAST))) {
            log.debug("Broadcasting switched off for context of message {}", message.getSubject());
            return;
        }

        if (reloadClusterTopics.contains(message.getSubject())) {
            log.info("Reloading cluster information before {}", message.getSubject());
            reloadClusterConfiguration();
        }

        final List<String> targets = message.getTargets();

        final List<Node> cluster = new ArrayList<>(getSfNodes());
        if (CollectionUtils.isNotEmpty(targets)) {
            cluster.removeIf(node -> !targets.contains(node.getId()));
        }


        final HttpHeaders headers = getRestServiceInboundChannelHeaders(context, context.getAttribute(AsyncContext.TIMEOUT_KEY));

        for (final Node yesNode : cluster) {
            try {

                final HttpEntity<RestMessage> msg = new HttpEntity<>(new RestMessage(restMessage), headers);

                final ResponseEntity<RestMessage> rsp = this.restTemplate.postForEntity(
                        yesNode.getChannel(), msg, RestMessage.class);

                if (rsp != null && rsp.getBody() != null) {
                    restMessage.addResponse(
                            new BasicMessageImpl(
                                    rsp.getBody().getSource(),
                                    rsp.getBody().getTargets(),
                                    rsp.getBody().getSubject(),
                                    rsp.getBody().getPayloadObject()
                            ));
                }

            } catch (RestClientException rce) {

                if (rce.getCause() instanceof ConnectException) {

                    blacklist(yesNode.getId());

                    if (log.isErrorEnabled()) {
                        log.error(Markers.alert(), "Node message failure [" + message + "] to channel ["
                                + yesNode.getId() + ":" + yesNode.getChannel() + "] . Blacklisting this node due to connection exception.");
                    }

                } else if (rce.getCause() instanceof JsonParseException) {

                    blacklist(yesNode.getId());

                    if (log.isErrorEnabled()) {
                        log.error(Markers.alert(), "Node message failure [" + message + "] to channel ["
                                + yesNode.getId() + ":" + yesNode.getChannel() + "] . Blacklisting this node due to malformed message.");
                    }

                } else {

                    if (log.isErrorEnabled()) {
                        log.error(Markers.alert(), "Node message failure [" + message + "] to channel ["
                                        + yesNode.getId() + ":" + yesNode.getChannel() + "] . Exception occurred during ws call",
                                rce);
                    }

                }

            } catch (Exception e) {

                if (log.isErrorEnabled()) {
                    log.error(Markers.alert(), "Node message failure [" + message + "] to channel ["
                                    + yesNode.getId() + ":" + yesNode.getChannel() + "] . Exception occurred during ws call",
                            e);
                }

            }
        }

    }


    private HttpHeaders getRestServiceInboundChannelHeaders(final AsyncContext context,
                                                            final String timeoutKey) {


        final String userName = context.getAttribute(AsyncContext.USERNAME);
        final String password = context.getAttribute(AsyncContext.CREDENTIALS);
        final String passwordHash = context.getAttribute(AsyncContext.CREDENTIALS_HASH);
        final boolean hashed = StringUtils.isNotBlank(passwordHash);
        final String pwd = hashed ? passwordHash : password;

        final int timeout = NumberUtils.toInt(getConfiguration().get(timeoutKey), 1000);
        clientHttpRequestFactory.setConnectTimeout(timeout);
        clientHttpRequestFactory.setReadTimeout(timeout);

        final HttpHeaders headers = new HttpHeaders();

        final String basic = userName + ":" + pwd;
        final byte[] encodedBytes = Base64.encodeBase64(basic.getBytes(StandardCharsets.UTF_8));

        headers.set("Authorization", "Basic " + new String(encodedBytes));

        return headers;

    }


    /**
     * Spring IoC.
     *
     * @param reloadClusterTopics topics that will cause reloading of cluster xml mapping
     */
    public void setReloadClusterTopics(final Set<String> reloadClusterTopics) {
        this.reloadClusterTopics = new HashSet<>(reloadClusterTopics);
    }
}
