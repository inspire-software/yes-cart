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

package org.yes.cart.web.service.ws.client.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.web.service.ws.client.WsClientAbstractFactory;
import org.yes.cart.web.service.ws.client.WsClientFactory;

/**
 * User: denispavlov
 * Date: 13-10-10
 * Time: 5:16 PM
 */
public class WsClientAbstractFactoryImpl implements WsClientAbstractFactory, ApplicationContextAware {

    private final Logger LOG = LoggerFactory.getLogger(WsClientAbstractFactoryImpl.class);

    private ApplicationContext applicationContext;

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "nodeService-wsFactory")
    public <S> WsClientFactory<S> getFactory(final Class<S> service,
                                             final String userName,
                                             final String password,
                                             final String url,
                                             final long timeout) {

        final NodeService nodeService = this.applicationContext.getBean("nodeService", NodeService.class);

        LOG.debug("Creating WsClientFactory on {} to access {}", nodeService.getCurrentNodeId(),  service);

        return new PerUserPerServiceClientFactory<S>(nodeService, service, userName, password, url, timeout);
    }

    /**
     * Spring IoC.
     *
     * @param applicationContext application context
     *
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
