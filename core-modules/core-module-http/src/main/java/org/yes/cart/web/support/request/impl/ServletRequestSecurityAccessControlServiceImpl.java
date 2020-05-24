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

package org.yes.cart.web.support.request.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.TaskScheduler;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.config.Configuration;
import org.yes.cart.config.ConfigurationContext;
import org.yes.cart.config.RegistrationAware;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.domain.SecurityAccessControlService;
import org.yes.cart.utils.log.Markers;
import org.yes.cart.web.support.request.IPResolver;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * User: denispavlov
 * Date: 21/10/2019
 * Time: 11:28
 */
public class ServletRequestSecurityAccessControlServiceImpl
        implements SecurityAccessControlService<ServletRequest>, Configuration, RegistrationAware, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(ServletRequestSecurityAccessControlServiceImpl.class);

    private SystemService systemService;
    private NodeService nodeService;
    private IPResolver ipResolver;

    private int maxRequestsPerMinute;
    private boolean maxRequestsPerMinuteEnabled = false;
    private int maxRequestsPerMinutePerIP;
    private boolean maxRequestsPerMinutePerIPEnabled = false;
    private String[] blockedIPs;
    private boolean blockedIPsEnabled = false;
    private String[] allowedIPs;
    private boolean allowedIPsEnabled = false;

    private Semaphore requestsAvailable = null;
    private Map<String, Semaphore> requestsAvailablePerIP = new ConcurrentHashMap<>();

    private TaskScheduler executorService;

    private ConfigurationContext cfgContext;

    @Override
    public boolean restrict(final ServletRequest servletRequest) {

        String ip = null;

        if (this.allowedIPsEnabled) {
            ip = lazyIpResolve(servletRequest, ip);
            if (ip != null) {
                for (final String allowedIP : this.allowedIPs) {
                    if (ip.startsWith(allowedIP)) {
                        return false;
                    }
                }
                LOG.debug("Blocked request from IP {} because it is not set in HTTP.allowIPCSV", ip);
                return true;
            }
        }

        if (this.blockedIPsEnabled) {
            ip = lazyIpResolve(servletRequest, ip);
            if (ip != null) {
                for (final String blockedIP : this.blockedIPs) {
                    if (ip.startsWith(blockedIP)) {
                        LOG.debug("Blocked request from IP {} because it is set in HTTP.blockIPCSV", ip);
                        return true;
                    }
                }
            }
        }

        if (this.maxRequestsPerMinuteEnabled) {
            if (requestsAvailable != null && !requestsAvailable.tryAcquire()) {
                ip = lazyIpResolve(servletRequest, ip);
                LOG.debug(Markers.alert(), "Blocked request from IP {} because global requests rate exceeded", ip);
                return true;
            }
        }

        if (this.maxRequestsPerMinutePerIPEnabled) {
            ip = lazyIpResolve(servletRequest, ip);
            if (ip != null) {
                final Semaphore perIp = requestsAvailablePerIP.computeIfAbsent(ip, newIp -> createSemaphone(newIp, this.maxRequestsPerMinutePerIP));
                if (perIp != null && !perIp.tryAcquire()) {
                    LOG.debug(Markers.alert(), "Blocked request from IP {} because per IP requests rate exceeded", ip);
                    return true;
                }
            }
        }

        return false;
    }

    private Semaphore createSemaphone(final String newIp, final int maxRequestsPerMinute) {
        return new Semaphore(maxRequestsPerMinute);
    }

    private String lazyIpResolve(final ServletRequest servletRequest, final String ip) {

        if (ip != null) {
            return ip;
        }

        return ipResolver.resolve((HttpServletRequest) servletRequest);
    }


    /** {@inheritDoc} */
    @Override
    public ConfigurationContext getCfgContext() {
        return cfgContext;
    }

    public void setCfgContext(final ConfigurationContext cfgContext) {
        this.cfgContext = cfgContext;
    }


    public void setSystemService(final SystemService systemService) {
        this.systemService = systemService;
    }

    public void setIpResolver(final IPResolver ipResolver) {
        this.ipResolver = ipResolver;
    }

    public void setNodeService(final NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setExecutorService(final TaskScheduler executorService) {
        this.executorService = executorService;
    }

    @Override
    public void onRegisterEvent() {

        try {

            final String nodeType = this.nodeService.getCurrentNode().getNodeType();

            final String props = this.systemService.getAttributeValue(AttributeNamesKeys.System.SYSTEM_EXTENSION_CFG_SECURITY);
            final Properties all = new Properties();
            all.load(new StringReader(props));

            final int rpm = NumberUtils.toInt(all.getProperty(nodeType + ".HTTP.maxRequestsPerMinute"), -1);
            final int rpmpip = NumberUtils.toInt(all.getProperty(nodeType + ".HTTP.maxRequestsPerMinutePerIP"), -1);
            final String[] blockedIPs = StringUtils.split(all.getProperty(nodeType + ".HTTP.blockIPCSV"), ',');
            final String[] allowedIPs = StringUtils.split(all.getProperty(nodeType + ".HTTP.allowIPCSV"), ',');

            this.maxRequestsPerMinute = rpm;
            this.maxRequestsPerMinuteEnabled = rpm != -1;
            this.maxRequestsPerMinutePerIP = rpmpip;
            this.maxRequestsPerMinutePerIPEnabled = rpmpip != -1;
            this.blockedIPs = blockedIPs != null && blockedIPs.length > 0 ? blockedIPs : null;
            this.blockedIPsEnabled = this.blockedIPs != null;
            this.allowedIPs = allowedIPs != null && allowedIPs.length > 0 ? allowedIPs : null;
            this.allowedIPsEnabled = this.allowedIPs != null;

            if (this.maxRequestsPerMinuteEnabled) {
                this.requestsAvailable = createSemaphone("-", this.maxRequestsPerMinute);
            } else {
                this.requestsAvailable = null;
            }

            this.requestsAvailablePerIP.clear();

        } catch (Exception exp) {

            LOG.error(Markers.alert(), "Unable to configure throttling, malformed configration {}", AttributeNamesKeys.System.SYSTEM_EXTENSION_CFG_SECURITY);

        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        this.executorService.scheduleWithFixedDelay(() -> {

            if (this.maxRequestsPerMinuteEnabled) {
                this.requestsAvailable.release(this.maxRequestsPerMinute - this.requestsAvailable.availablePermits());
            }

            if (this.maxRequestsPerMinutePerIPEnabled) {

                final Iterator<Map.Entry<String, Semaphore>> itEntry = this.requestsAvailablePerIP.entrySet().iterator();
                while (itEntry.hasNext()) {
                    final Semaphore perIp = itEntry.next().getValue();
                    if (this.maxRequestsPerMinutePerIP == perIp.availablePermits()) {
                        // none used since last time - idle
                        itEntry.remove();
                    }
                    perIp.release(this.maxRequestsPerMinutePerIP - perIp.availablePermits());
                }

            }

        }, TimeUnit.MINUTES.toMillis(1));

    }
}
