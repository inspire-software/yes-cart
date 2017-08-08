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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.shoppingcart.InventoryResolver;

/**
 * User: denispavlov
 * Date: 09/07/2017
 * Time: 16:47
 */
public class WarehouseInventoryConfigurationImpl extends AbstractWarehouseConfigurationImpl {

    private static final Logger LOG = LoggerFactory.getLogger(WarehouseInventoryConfigurationImpl.class);

    private InventoryResolver inventoryResolver;

    public WarehouseInventoryConfigurationImpl(final String warehouseCode, final WarehouseService warehouseService) {
        super(warehouseCode, warehouseService);
    }

    void registerCustomInventoryResolver(final Warehouse warehouse) {
        if (inventoryResolver != null) {
            configureWarehouse(warehouse.getCode(), inventoryResolver);
        }
    }

    /** {@inheritDoc} */
    protected void doConfigurations(final Warehouse warehouse) {
        this.registerCustomInventoryResolver(warehouse);
    }

    /**
     * Spring IoC.
     *
     * @param inventoryResolver resolver
     */
    public void setInventoryResolver(final InventoryResolver inventoryResolver) {
        this.inventoryResolver = inventoryResolver;
    }

}
