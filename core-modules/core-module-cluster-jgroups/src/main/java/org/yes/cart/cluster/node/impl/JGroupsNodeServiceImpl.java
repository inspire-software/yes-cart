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
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.util.Rsp;
import org.jgroups.util.RspList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.web.context.ServletContextAware;
import org.yes.cart.cluster.node.*;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.util.log.Markers;

import javax.servlet.ServletContext;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

/**
 * User: denispavlov
 * Date: 16/06/2015
 * Time: 16:34
 */
public class JGroupsNodeServiceImpl implements NodeService, ServletContextAware, DisposableBean {

    private Logger LOG;

    private final Map<String, String> configuration = new HashMap<String, String>();
    private Node node = new NodeImpl(true, "-", null, "DEFAULT", "YCCLUSTER", "N/A", "", true);
    private final List<Node> cluster = new CopyOnWriteArrayList<Node>();
    private final Map<Address, String> clusterAddresses = new HashMap<Address, String>();

    private JChannel jChannel;
    private MessageDispatcher jChannelMessageDispatcher;
    private Map<String, List<MessageListener>> listeners = new HashMap<String, List<MessageListener>>();

    private final SystemService systemService;

    private String jgroupsConfiguration = "yc-jgroups-udp.xml";

    private final Executor executor;

    public JGroupsNodeServiceImpl(final SystemService systemService,
                                  final Executor executor) {
        this.systemService = systemService;
        this.executor = executor;
    }

    /** {@inheritDoc} */
    public String getCurrentNodeId() {
        return node.getId();
    }

    /** {@inheritDoc} */
    public Map<String, String> getConfiguration() {
        final Map<String, String> all = new HashMap<String, String>();
        all.putAll(configuration);
        all.put(
                AttributeNamesKeys.System.IMPORT_JOB_LOG_SIZE,
                systemService.getAttributeValueOrDefault(AttributeNamesKeys.System.IMPORT_JOB_LOG_SIZE, "10000")
        );
        all.put(
                AttributeNamesKeys.System.IMPORT_JOB_TIMEOUT_MS,
                systemService.getAttributeValueOrDefault(AttributeNamesKeys.System.IMPORT_JOB_TIMEOUT_MS, "60000")
        );
        all.put(
                AttributeNamesKeys.System.SYSTEM_BACKDOOR_TIMEOUT_MS,
                systemService.getAttributeValueOrDefault(AttributeNamesKeys.System.SYSTEM_BACKDOOR_TIMEOUT_MS, "60000")
        );
        all.put(
                AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_BULK_INDEX_TIMEOUT_MS,
                systemService.getAttributeValueOrDefault(AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_BULK_INDEX_TIMEOUT_MS, "60000")
        );
        all.put(
                AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_SINGLE_INDEX_TIMEOUT_MS,
                systemService.getAttributeValueOrDefault(AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_SINGLE_INDEX_TIMEOUT_MS, "60000")
        );
        all.put(
                AttributeNamesKeys.System.SYSTEM_BACKDOOR_SQL_TIMEOUT_MS,
                systemService.getAttributeValueOrDefault(AttributeNamesKeys.System.SYSTEM_BACKDOOR_SQL_TIMEOUT_MS, "60000")
        );
        all.put(
                AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS,
                systemService.getAttributeValueOrDefault(AttributeNamesKeys.System.SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS, "60000")
        );
        all.put(
                AttributeNamesKeys.System.SYSTEM_BACKDOOR_IMAGE_TIMEOUT_MS,
                systemService.getAttributeValueOrDefault(AttributeNamesKeys.System.SYSTEM_BACKDOOR_IMAGE_TIMEOUT_MS, "60000")
        );
        return all;
    }

    /** {@inheritDoc} */
    public List<Node> getCluster() {

        return Collections.unmodifiableList(this.cluster);

    }

    /** {@inheritDoc} */
    public Node getCurrentNode() {
        return node;
    }

    /** {@inheritDoc} */
    public Node getAdminNode() {

        final List<Node> cluster = getCluster();
        for (final Node node : cluster) {
            if (node.isAdmin()) {
                return node;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    public List<Node> getSfNodes() {

        final List<Node> cluster = getCluster();
        final List<Node> yes = new ArrayList<Node>();
        for (final Node node : cluster) {
            if (!node.isAdmin()) {
                yes.add(node);
            }
        }
        return Collections.unmodifiableList(yes);
    }

    /** {@inheritDoc} */
    public List<Node> getOtherSfNodes() {

        final List<Node> cluster = getCluster();
        final List<Node> yes = new ArrayList<Node>();
        for (final Node node : cluster) {
            if (!node.isAdmin() && !node.isCurrent()) {
                yes.add(node);
            }
        }
        return Collections.unmodifiableList(yes);
    }

    /** {@inheritDoc} */
    public void broadcast(final Message message) {

        LOG.debug("Sending message: {}", message);

        if (message instanceof RspMessage) {

            RspMessage rsp = (RspMessage) message;

            try {

                RspList<Message> result =jChannelMessageDispatcher.castMessage(null,
                        new org.jgroups.Message(null, null, rsp),
                        RequestOptions.SYNC());

                for (Rsp<Message> single : result.values()) {
                    if (single.wasReceived() && single.getException() == null) {
                        rsp.addResponse(single.getValue());
                    } else {
                        rsp.addResponse(new BasicMessageImpl(null, message.getSubject(), Message.LOST));
                    }
                }

            } catch (Exception e) {

                LOG.error(Markers.alert(), "Node message failure [" + message + "] from " + node.getId() + ", cause: " + e.getMessage(), e);

            }

        } else {
            try {

                jChannel.send(null, message);

            } catch (Exception e) {

                LOG.error(Markers.alert(), "Node message failure [" + message + "] from " + node.getId() + ", cause: " + e.getMessage(), e);

            }
        }
    }

    /** {@inheritDoc} */
    public void subscribe(final String subject, final MessageListener listener) {
        synchronized (this) {
            List<MessageListener> subjectListeners = listeners.get(subject);
            if (subjectListeners == null) {
                subjectListeners = new ArrayList<MessageListener>();
                listeners.put(subject, subjectListeners);
            }
            subjectListeners.add(listener);
            LOG.debug("Registering listener for topic {}", subject);
        }
    }

    /** {@inheritDoc} */
    public void setServletContext(final ServletContext servletContext) {

        initNodeFromServletContext(servletContext);
        startJGroupsChannel();

    }

    void startJGroupsChannel() {

        try {
            this.jChannel = new JChannel(this.jgroupsConfiguration);

            LOG.info("Initialised JChannel for {}", node.getId());

        } catch (Exception e) {
            throw new RuntimeException("Unable to create JChannel: " + e.getMessage(), e);
        }

        final Runnable onViewAccepted = new Runnable() {
            @Override
            public void run() {

                synchronized (cluster) {
                    try {

                        // Refresh view and add self first
                        cluster.clear();
                        clusterAddresses.clear();
                        cluster.add(node);
                        clusterAddresses.put(jChannel.getAddress(), node.getId());
                        ((NodeImpl) node).setChannel(jChannel.getAddress().toString());

                        // Send HELLO
                        final Message intro = new BasicMessageImpl(node.getId(), "HELLO", node);

                        RspList<Message> result =jChannelMessageDispatcher.castMessage(null,
                                new org.jgroups.Message(null, null, intro),
                                RequestOptions.SYNC());

                        for (Rsp<Message> single : result.values()) {
                            if (!single.getSender().equals(jChannel.getAddress())) {
                                if (single.wasReceived() && single.getException() == null) {

                                    final Message rsp = single.getValue();
                                    final NodeImpl node = new NodeImpl(false, (Node) rsp.getPayload());
                                    node.setChannel(single.getSender().toString());
                                    cluster.add(node);
                                    clusterAddresses.put(single.getSender(), rsp.getSource());

                                    LOG.info("Found member {} at {}", rsp.getSource(), single.getSender());

                                }
                            }
                        }

                    } catch (Exception e) {
                        LOG.error(Markers.alert(), "Node message failure [HELLO] from " + node.getId() + ", cause: " + e.getMessage(), e);
                    }
                }
            }
        };

        final ReceiverAdapter receiverAdapter = new ReceiverAdapter() {
            public void receive(final org.jgroups.Message msg) {

                if (isMessageValid(msg)) {

                    final Message notification = (Message) msg.getObject();
                    final List<MessageListener> subjectListeners = listeners.get(notification.getSubject());
                    if (subjectListeners != null) {

                        for (final MessageListener listener : subjectListeners) {

                            listener.onMessageReceived(notification);

                        }

                    } else {
                        LOG.warn("No listeners for message: {}", msg.getObject());
                    }

                }

            }

            @Override
            public void viewAccepted(final View view) {

                LOG.info("View accepted: {}", view.getViewId());
                executor.execute(onViewAccepted);

            }
        };

        final RequestHandler requestHandler = new RequestHandler() {

            @Override
            public Object handle(final org.jgroups.Message msg) throws Exception {

                if (isMessageValid(msg)) {

                    final Message notification = (Message) msg.getObject();
                    final List<MessageListener> subjectListeners = listeners.get(notification.getSubject());
                    if (CollectionUtils.isNotEmpty(subjectListeners)) {

                        final List rsp = new ArrayList();
                        for (final MessageListener listener : subjectListeners) {

                           rsp.add(listener.onMessageReceived(notification));

                        }

                        if (rsp.size() > 1) {
                           return rsp; // if many listeners return list
                        } else if (rsp.size() == 1) {
                            return rsp.get(0); // otherwise return first
                        }

                    } else {
                        LOG.warn("No listeners for message: {}", msg.getObject());
                    }

                }

                return null;

            }

        };

        jChannelMessageDispatcher = new MessageDispatcher(jChannel, receiverAdapter, receiverAdapter, requestHandler);
        addDefaultListeners();

        try {

            jChannel.connect(node.getClusterId());

            LOG.info("Initialised JChannel");

        } catch (Exception e) {
            throw new RuntimeException("Unable to connect to JChannel '" + node.getClusterId() + "': " + e.getMessage(), e);
        }


    }

    boolean isMessageValid(final org.jgroups.Message msg) {

        if (msg.getObject() instanceof Message) {

            final Message notification = (Message) msg.getObject();

            if ((notification.getTargets() == null ||
                    notification.getTargets().contains(node.getId()))
                    && (!notification.getSource().equals(node.getId()))) {

                LOG.debug("Message received: {}", msg.getObject());

                return true;

            } else {

                LOG.debug("Message received but was not intended for this node: {}", msg.getObject());

            }

        } else {

            LOG.error("Message received was not of type org.yes.cart.cluster.node.Message: {}", msg.getObject());

        }

        return false;

    }

    void initNodeFromServletContext(final ServletContext servletContext) {


        final Enumeration parameters = servletContext.getInitParameterNames();
        while (parameters.hasMoreElements()) {

            final String key = String.valueOf(parameters.nextElement());
            final String value = servletContext.getInitParameter(key);

            configuration.put(key, value);

        }

        final String luceneDisabled = configuration.get(LUCENE_INDEX_DISABLED);
        final String luceneDisabledValue = luceneDisabled != null ?
                Boolean.valueOf(luceneDisabled).toString() : Boolean.FALSE.toString();
        configuration.put(LUCENE_INDEX_DISABLED, luceneDisabledValue);

        node = new NodeImpl(true,
                configuration.get(NODE_ID),
                configuration.get(NODE_TYPE),
                configuration.get(NODE_CONFIG),
                configuration.get(CLUSTER_ID),
                configuration.get(VERSION),
                configuration.get(BUILD_NO),
                Boolean.valueOf(luceneDisabledValue)
        );
        this.cluster.add(node);

        LOG = LoggerFactory.getLogger(node.getClusterId() + "." + node.getNodeId());

        if (LOG.isInfoEnabled()) {

            final StringBuilder stats = new StringBuilder();

            stats.append("\nLoading configuration for node...");
            stats.append("\nNode: ").append(node.getId());

            for (final Map.Entry<String, String> entry : configuration.entrySet()) {
                stats.append('\n').append(entry.getKey()).append(": ").append(entry.getValue());
            }

            LOG.info(stats.toString());
        }

    }

    void addDefaultListeners() {

        // Receive request for identification
        subscribe("HELLO", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {

                LOG.debug("Received HELLO from: {}", message.getSource());

                return new BasicMessageImpl(node.getId(), Arrays.asList(message.getSource()), "HELLO", node);

            }
        });


        // Process bye request
        subscribe("BYE", new MessageListener() {
            @Override
            public Serializable onMessageReceived(final Message message) {

                LOG.debug("Received BYE from: {}", message.getSource());

                synchronized (cluster) {
                    final Node node = new NodeImpl(false, (Node) message.getPayload());
                    final List<Node> remove = new ArrayList<Node>();
                    for (final Node clusterNode : cluster) {
                        if (clusterNode.getId().equals(node.getId())) {
                            remove.add(clusterNode);
                            final Iterator<Map.Entry<Address, String>> addressesIt = clusterAddresses.entrySet().iterator();
                            while (addressesIt.hasNext()) {
                                if (addressesIt.next().getValue().equals(node.getId())) {
                                    addressesIt.remove();
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    if (!remove.isEmpty()) {
                        cluster.removeAll(remove);
                    }
                }
                return null;
            }
        });

    }

    /**
     * Spring IoC setter.
     *
     * @param jgroupsConfiguration path to configuration file
     */
    public void setJgroupsConfiguration(final String jgroupsConfiguration) {
        this.jgroupsConfiguration = jgroupsConfiguration;
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() throws Exception {

        LOG.info("Closing JGroups channel for node {}", node.getId());
        try {
            jChannel.send(null, new BasicMessageImpl(node.getId(), "BYE", node));
            Thread.sleep(100L); // give some time for message to be sent
        } catch (Exception e) {
            LOG.error(Markers.alert(), "Node message failure [BYE] from " + node.getId() + ", cause: " + e.getMessage(), e);
        }
        jChannel.close();

    }

}
