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

package org.yes.cart.web.service.ws.node.impl;

import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.context.ServletContextAware;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.AttrValueSystem;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.service.domain.AttributeGroupService;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.EtypeService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.service.ws.node.NodeService;
import org.yes.cart.web.service.ws.node.dto.Node;
import org.yes.cart.web.service.ws.node.dto.impl.NodeImpl;

import javax.servlet.ServletContext;
import java.util.*;

/**
 * User: denispavlov
 * Date: 13-10-03
 * Time: 6:50 PM
 */
public class NodeServiceImpl implements NodeService, ServletContextAware {

    private final Map<String, String> configuration = new HashMap<String, String>();

    private NodeImpl node = new NodeImpl(true, "-", null, null);

    private final SystemService systemService;
    private final AttributeService attributeService;
    private final EtypeService etypeService;
    private final AttributeGroupService attributeGroupService;


    public NodeServiceImpl(final SystemService systemService,
                           final AttributeService attributeService,
                           final EtypeService etypeService,
                           final AttributeGroupService attributeGroupService) {
        this.systemService = systemService;
        this.attributeService = attributeService;
        this.etypeService = etypeService;
        this.attributeGroupService = attributeGroupService;
    }

    /** {@inheritDoc} */
    @Override
    public String getCurrentNodeId() {
        return node.getNodeId();
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getConfiguration() {
        return configuration;
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "nodeService-cluster")
    public List<Node> getCluster() {

        final Map<String, AttrValueSystem> configs = systemService.getAttributeValues();

        final Set<String> nodes = new HashSet<String>();

        for (final String key : configs.keySet()) {
            if (key.startsWith(AttributeNamesKeys.System.SYSTEM_BACKDOOR_URI)
                    && key.length() > AttributeNamesKeys.System.SYSTEM_BACKDOOR_URI.length()) {
                nodes.add(key.substring(AttributeNamesKeys.System.SYSTEM_BACKDOOR_URI.length() + 1));
            } else if (key.startsWith(AttributeNamesKeys.System.SYSTEM_CACHEDIRECTOR_URI)
                    && key.length() > AttributeNamesKeys.System.SYSTEM_CACHEDIRECTOR_URI.length()) {
                nodes.add(key.substring(AttributeNamesKeys.System.SYSTEM_CACHEDIRECTOR_URI.length() + 1));
            }
        }

        final List<Node> cluster = new ArrayList<Node>();
        for (final String node : nodes) {
            if (node.equals(this.node.getNodeId())) {
                cluster.add(this.node);
            } else {
                final AttrValueSystem backdoorUri = configs.get(AttributeNamesKeys.System.SYSTEM_BACKDOOR_URI + "_" + node);
                final AttrValueSystem cacheDirectorUri = configs.get(AttributeNamesKeys.System.SYSTEM_CACHEDIRECTOR_URI + "_" + node);
                cluster.add(new NodeImpl(false, node,
                        backdoorUri != null ? backdoorUri.getVal() : null,
                        cacheDirectorUri != null ? cacheDirectorUri.getVal() : null
                ));
            }
        }

        return Collections.unmodifiableList(cluster);
    }

    /** {@inheritDoc} */
    @Override
    public Node getCurrentNode() {
        return node;
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "nodeService-yumNode")
    public Node getYumNode() {

        final List<Node> cluster = getCluster();
        for (final Node node : cluster) {
            if (node.isYum()) {
                return node;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "nodeService-yesNodes")
    public List<Node> getYesNodes() {

        final List<Node> cluster = getCluster();
        final List<Node> yes = new ArrayList<Node>();
        for (final Node node : cluster) {
            if (!node.isYum()) {
                yes.add(node);
            }
        }
        return Collections.unmodifiableList(yes);
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "nodeService-otherYesNodes")
    public List<Node> getOtherYesNodes() {

        final List<Node> cluster = getCluster();
        final List<Node> yes = new ArrayList<Node>();
        for (final Node node : cluster) {
            if (!node.isYum() && !node.isCurrent()) {
                yes.add(node);
            }
        }
        return Collections.unmodifiableList(yes);
    }

    /** {@inheritDoc} */
    @Override
    public void setServletContext(final ServletContext servletContext) {

        final Logger log = ShopCodeContext.getLog(this);

        log.info("Loading configuration for node...");

        final Enumeration parameters = servletContext.getInitParameterNames();
        while (parameters.hasMoreElements()) {

            final String key = String.valueOf(parameters.nextElement());
            final String value = servletContext.getInitParameter(key);

            log.info("{}: {}", key, value);

            configuration.put(key, value);

        }

        node = new NodeImpl(true,
                configuration.get(NODE_ID),
                configuration.get(BACKDOOR_URI),
                configuration.get(CACHEDIRECTOR_URI)
        );

        final String backdoorKey = AttributeNamesKeys.System.SYSTEM_BACKDOOR_URI + "_" + node.getNodeId();
        final String cacheDirectorKey = AttributeNamesKeys.System.SYSTEM_CACHEDIRECTOR_URI + "_" + node.getNodeId();

        final Set<String> codes = attributeService.getAllAttributeCodes();
        if (!codes.contains(backdoorKey)) {
            final Attribute attribute = attributeService.getGenericDao().getEntityFactory().getByIface(Attribute.class);
            attribute.setCode(backdoorKey);
            attribute.setName(backdoorKey);
            attribute.setAttributeGroup(attributeGroupService.getAttributeGroupByCode("SYSTEM"));
            attribute.setEtype(etypeService.findSingleByCriteria(Restrictions.eq("businesstype", "URI")));
            attribute.setGuid(backdoorKey);
            attributeService.create(attribute);
        }

        if (!codes.contains(cacheDirectorKey)) {
            final Attribute attribute = attributeService.getGenericDao().getEntityFactory().getByIface(Attribute.class);
            attribute.setCode(cacheDirectorKey);
            attribute.setName(cacheDirectorKey);
            attribute.setAttributeGroup(attributeGroupService.getAttributeGroupByCode("SYSTEM"));
            attribute.setEtype(etypeService.findSingleByCriteria(Restrictions.eq("businesstype", "URI")));
            attribute.setGuid(cacheDirectorKey);
            attributeService.create(attribute);
        }

        systemService.updateAttributeValue(backdoorKey, configuration.get(BACKDOOR_URI));
        systemService.updateAttributeValue(cacheDirectorKey, configuration.get(CACHEDIRECTOR_URI));

        log.info("Loading configuration for node {}... success", node.getNodeId());
    }

}
