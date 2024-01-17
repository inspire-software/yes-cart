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

package org.yes.cart.web.application;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkjob.cron.AbstractCronJobProcessorImpl;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.domain.entity.Job;
import org.yes.cart.domain.entity.JobDefinition;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.impl.JobStatusListenerWithLoggerImpl;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.web.support.seo.SitemapXmlService;

import java.time.Instant;
import java.util.*;

/**
 * User: inspiresoftware
 * Date: 17/01/2024
 * Time: 08:16
 */
public class SitemapXmlProcessorImpl extends AbstractCronJobProcessorImpl implements JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(SitemapXmlProcessorImpl.class);

    private SitemapXmlService sitemapXmlService;
    private NodeService nodeService;
    private ShopService shopService;

    private final JobStatusListener listener = new JobStatusListenerWithLoggerImpl(new JobStatusListenerImpl(), LOG);

    @Override
    protected Pair<JobStatus, Instant> processInternal(final Map<String, Object> context, final Job job, final JobDefinition definition) {

        listener.reset();

        final String nodeId = getNodeId();

        listener.notifyMessage("Generating sitemap.xml on {}", nodeId);

        final Properties properties = readContextAsProperties(context, job, definition);

        final String disableConfig = properties.getProperty("disable-sitemap-for");
        final Set<String> manuallyDisabled = new HashSet<>();
        if (StringUtils.isNotBlank(disableConfig)) {
            for (final String code : StringUtils.split(disableConfig, ',')) {
                manuallyDisabled.add(StringUtils.trim(code));
            }
            listener.notifyMessage("sitemap.xml is disabled for shops {}", disableConfig);
        }

        final List<Shop> shops = shopService.getNonSubShops();
        for (int i = 0; i < shops.size(); i++) {
            final Shop shop = shops.get(i);
            if (!shop.isDisabled() && !manuallyDisabled.contains(shop.getCode())) {

                listener.notifyMessage(" {} of {}: Generating sitemap.xml on {} for shop {}",
                        i + 1, shops.size(), nodeId, shop.getCode());

                if (sitemapXmlService.generateSitemapXmlAndRetain(shop.getCode())) {
                    listener.count("SUCCESS", 1);
                } else {
                    listener.count("ERROR", 1);
                    listener.notifyError("Unable to generate sitemap.xml for " + shop.getCode());
                }

            } else {
                listener.notifyMessage(" {} of {}: Skipping sitemap.xml on {} for shop {}",
                        i + 1, shops.size(), nodeId, shop.getCode());
                listener.count("SKIP", 1);
            }
        }

        LOG.info("Generating sitemap.xml on {} ... completed", nodeId);

        listener.notifyCompleted();

        return new Pair<>(listener.getLatestStatus(), null);


    }

    @Override
    public JobStatus getStatus(final String token) {
        return listener.getLatestStatus();
    }

    protected String getNodeId() {
        return nodeService.getCurrentNodeId();
    }


    /**
     * Spring IoC.
     *
     * @param sitemapXmlService service
     */
    public void setSitemapXmlService(final SitemapXmlService sitemapXmlService) {
        this.sitemapXmlService = sitemapXmlService;
    }

    /**
     * Spring IoC.
     *
     * @param nodeService service
     */
    public void setNodeService(final NodeService nodeService) {
        this.nodeService = nodeService;
    }

    /**
     * Spring IoC.
     *
     * @param shopService service
     */
    public void setShopService(final ShopService shopService) {
        this.shopService = shopService;
    }
}
