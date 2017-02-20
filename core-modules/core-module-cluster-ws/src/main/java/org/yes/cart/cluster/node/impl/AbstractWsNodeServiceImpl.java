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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ServletContextAware;
import org.yes.cart.cluster.node.Message;
import org.yes.cart.cluster.node.MessageListener;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.stream.xml.XStreamProvider;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * User: denispavlov
 * Date: 16/06/2015
 * Time: 16:34
 */
public abstract class AbstractWsNodeServiceImpl implements NodeService, ServletContextAware, DisposableBean {

    protected Logger LOG;

    private final Map<String, String> configuration = new HashMap<String, String>();
    private Node node = new NodeImpl(true, "-", null, "DEFAULT", "YCCLUSTER", "N/A", "", true);
    private final List<Node> cluster = new CopyOnWriteArrayList<Node>();

    protected Map<String, List<MessageListener>> listeners = new HashMap<String, List<MessageListener>>();

    private final SystemService systemService;

    private Resource wsConfiguration;
    private XStreamProvider<List<Node>> wsConfigurationLoader;

    public AbstractWsNodeServiceImpl(final SystemService systemService) {
        this.systemService = systemService;
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
    public abstract void broadcast(final Message message);


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
        loadClusterConfiguration();

    }

    /**
     * Blacklist a node so it is not seen as part of the cluster.
     *
     * @param nodeId node id
     */
    protected void blacklist(final String nodeId) {

        if (nodeId != null) {
            synchronized (this.cluster) {

                Node toBlacklist = null;
                for (final Node node : this.cluster) {
                    if (nodeId.equals(node.getId())) {
                        toBlacklist = node;
                        break;
                    }
                }

                if (toBlacklist != null) {
                    this.cluster.remove(toBlacklist);
                }
            }
        }

    }

    /**
     * Reload configuration for this cluster.
     */
    protected void reloadClusterConfiguration() {

        synchronized (this.cluster) {

            loadClusterConfiguration();
        }

    }


    void loadClusterConfiguration() {

        try {
            this.cluster.clear();

            final List<Node> cluster = wsConfigurationLoader.fromXML(wsConfiguration.getInputStream());

            final List<Node> all = new ArrayList<Node>();
            all.add(this.node);
            for (final Node node : cluster) {
                if (!node.getId().equals(this.node.getId())) {
                    if (StringUtils.isNotBlank(node.getChannel()) || node.isAdmin()) {
                        // only add nodes with specified channels
                        all.add(new NodeImpl(false, node));
                    }
                }
            }
            this.cluster.addAll(all);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

        NodeImpl node = new NodeImpl(true,
                configuration.get(NODE_ID),
                configuration.get(NODE_TYPE),
                configuration.get(NODE_CONFIG),
                configuration.get(CLUSTER_ID),
                configuration.get(VERSION),
                configuration.get(BUILD_NO),
                Boolean.valueOf(luceneDisabledValue)
        );
        node.setChannel(configuration.get(CHANNEL_URI));
        this.node = node;
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

    /**
     * Spring IoC setter.
     *
     * @param wsConfiguration configuration
     */
    public void setWsConfiguration(final Resource wsConfiguration) {
        this.wsConfiguration = wsConfiguration;
    }

    /**
     * Spring IoC setter.
     *
     * @param wsConfigurationLoader configuration loader
     */
    public void setWsConfigurationLoader(final XStreamProvider<List<Node>> wsConfigurationLoader) {
        this.wsConfigurationLoader = wsConfigurationLoader;
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() throws Exception {

        LOG.info("Closing WS channel for node {}", node.getId());

    }

}
