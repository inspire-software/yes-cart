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
package org.yes.cart.cluster.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.cluster.service.ClusterUtilsService;
import org.yes.cart.cluster.service.WarmUpService;
import org.yes.cart.config.ConfigurationListener;

import java.util.List;

/**
 * User: denispavlov
 * Date: 13/04/2019
 * Time: 16:54
 */
public class ClusterUtilsServiceImpl implements ClusterUtilsService {

    private WarmUpService warmUpService;

    private List<ConfigurationListener> configurationListeners;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean ping() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warmUp() {
        warmUpService.warmUp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reloadConfigurations() {

        if (CollectionUtils.isNotEmpty(this.configurationListeners)) {
            for (final ConfigurationListener listener : this.configurationListeners) {
                listener.reload();
            }
        }

    }

    /**
     * IoC. Set warn up service.
     *
     * @param warmUpService warm up service to use.
     */
    public void setWarmUpService(final WarmUpService warmUpService) {
        this.warmUpService = warmUpService;
    }

    /**
     * IoC. Set configuration listener.
     *
     * @param configurationListeners configuration listener.
     */
    public void setConfigurationListeners(final List<ConfigurationListener> configurationListeners) {
        this.configurationListeners = configurationListeners;
    }

}
