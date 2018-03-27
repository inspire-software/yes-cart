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
import org.yes.cart.shoppingcart.InventoryResolver;

import java.util.Properties;

/**
 * User: denispavlov
 * Date: 09/07/2017
 * Time: 16:47
 */
public class WarehouseInventoryConfigurationImpl extends AbstractWarehouseConfigurationImpl {

    public WarehouseInventoryConfigurationImpl(final SystemService systemService,
                                               final WarehouseService warehouseService) {
        super(systemService, warehouseService);
    }

    void registerCustomInventoryResolver(final Warehouse warehouse, final Properties properties) {

        final InventoryResolver inventoryResolver = determineConfiguration(properties, warehouse.getCode() + ".inventoryResolver", InventoryResolver.class);

        customise(warehouse.getCode(), warehouse.getCode(), InventoryResolver.class, inventoryResolver);
    }

    /** {@inheritDoc} */
    @Override
    protected void doConfigurations(final Warehouse warehouse, final Properties properties) {
        this.registerCustomInventoryResolver(warehouse, properties);
    }

}
