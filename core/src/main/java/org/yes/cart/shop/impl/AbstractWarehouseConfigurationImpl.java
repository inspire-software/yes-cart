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

package org.yes.cart.shop.impl;

import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.config.ConfigurationRegistry;
import org.yes.cart.config.ShopConfiguration;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.WarehouseService;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 09/07/2017
 * Time: 16:47
 */
public abstract class AbstractWarehouseConfigurationImpl implements ShopConfiguration, InitializingBean, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractWarehouseConfigurationImpl.class);

    private ApplicationContext applicationContext;

    private final String warehouseCode;
    private final WarehouseService warehouseService;

    public AbstractWarehouseConfigurationImpl(final String warehouseCode, final WarehouseService warehouseService) {
        this.warehouseCode = warehouseCode;
        this.warehouseService = warehouseService;
    }


    /** {@inheritDoc} */
    @Override
    public void afterPropertiesSet() throws Exception {
        final Warehouse warehouse = this.warehouseService.findSingleByCriteria(Restrictions.eq("code", this.warehouseCode));
        if (warehouse != null) {
            this.doConfigurations(warehouse);
        } else {
            LOG.error("Custom shop configurations cannot be applied because shop with code {} does not exist", this.warehouseCode);
        }
    }

    /**
     * Call to register of the configuration.
     *
     * @param key warehouse key
     * @param configuration configuration to set
     */
    protected void configureWarehouse(final Object key, final Object configuration) {

        if (configuration != null) {
            final Map<String, ConfigurationRegistry> registries = this.applicationContext.getBeansOfType(ConfigurationRegistry.class);
            for (final ConfigurationRegistry registry : registries.values()) {
                if (registry.supports(configuration)) {
                    registry.register(key, configuration);
                    LOG.info("Custom shop configurations for {} ... registering {}", this.warehouseCode, configuration);
                }
            }
        }

    }

    /**
     * Perform configurations necessary.
     *
     * @param warehouse warehouse
     */
    protected abstract void doConfigurations(final Warehouse warehouse);

    /** {@inheritDoc} */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
