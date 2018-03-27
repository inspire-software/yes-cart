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

package org.yes.cart.config.impl;

import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.domain.WarehouseService;

import java.util.List;
import java.util.Properties;

/**
 * User: denispavlov
 * Date: 09/07/2017
 * Time: 16:47
 */
public abstract class AbstractWarehouseConfigurationImpl extends AbstractConfigurationImpl {

    private final WarehouseService warehouseService;

    public AbstractWarehouseConfigurationImpl(final SystemService systemService,
                                              final WarehouseService warehouseService) {
        super(systemService);
        this.warehouseService = warehouseService;
    }


    /** {@inheritDoc} */
    @Override
    protected void onConfigureEvent(final Properties properties) {
        final List<Warehouse> warehouses = this.warehouseService.findAll();
        for (final Warehouse warehouse : warehouses) {
            this.doConfigurations(warehouse, properties);
        }
    }

    /**
     * Perform configurations necessary.
     *
     * @param warehouse warehouse
     * @param properties configuration properties
     */
    protected abstract void doConfigurations(final Warehouse warehouse, final Properties properties);

}
