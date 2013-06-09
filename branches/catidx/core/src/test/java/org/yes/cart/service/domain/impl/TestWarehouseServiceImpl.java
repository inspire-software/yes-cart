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

package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.WarehouseService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestWarehouseServiceImpl extends BaseCoreDBTestCase {

    private WarehouseService warehouseService;
    private ShopService shopService;
    private EntityFactory entityFactory;
    private Shop shop;
    private Warehouse warehouse;

    @Before
    public void setUp() {
        shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        warehouseService = (WarehouseService) ctx().getBean(ServiceSpringKeys.WAREHOUSE_SERVICE);
        entityFactory = warehouseService.getGenericDao().getEntityFactory();
        super.setUp();
    }

    /**
     * Assign warehouse to shop test.
     */
    @Test
    public void testAssignWarehouse() {
        createShopAndWareHouse("TESTSHOP", "TESTWAREHOUSE");
        List<Warehouse> shopWarehouses = warehouseService.findByShopId(shop.getShopId());
        assertNotNull(shopWarehouses);
        assertTrue(shopWarehouses.isEmpty());
        ShopWarehouse shopWarehouse = warehouseService.assignWarehouse(warehouse.getWarehouseId(), shop.getShopId());
        assertNotNull(shopWarehouse);
        assertEquals(warehouse.getWarehouseId(), shopWarehouse.getWarehouse().getWarehouseId());
        assertEquals(shop.getShopId(), shopWarehouse.getShop().getShopId());
        shopWarehouses = warehouseService.findByShopId(shop.getShopId());
        assertNotNull(shopWarehouses);
        assertFalse(shopWarehouses.isEmpty());
        assertEquals(warehouse.getWarehouseId(), shopWarehouses.get(0).getWarehouseId());

        // unassign
        warehouseService.unassignWarehouse(warehouse.getWarehouseId(), shop.getShopId());
        List<Warehouse> shopWarehousesAfter = warehouseService.findByShopId(shop.getShopId());
        assertNotNull(shopWarehousesAfter);
        assertTrue(shopWarehousesAfter.isEmpty());

    }

    @Test
    public void testSetShopWarehouseRank() {
        createShopAndWareHouse("TESTSHOP123", "TESTWAREHOUSE123");
        List<Warehouse> shopWarehouses = warehouseService.findByShopId(shop.getShopId());
        assertNotNull(shopWarehouses);
        assertTrue(shopWarehouses.isEmpty());

        ShopWarehouse shopWarehouse = warehouseService.assignWarehouse(warehouse.getWarehouseId(), shop.getShopId());
        assertEquals("Test default rank", new Integer(100), shopWarehouse.getRank());

        warehouseService.updateShopWarehouseRank(shopWarehouse.getShopWarehouseId(), 200);
        shopWarehouse = warehouseService.findShopWarehouseById(shopWarehouse.getShopWarehouseId());
        assertEquals("Test default rank", new Integer(200), shopWarehouse.getRank());
    }

    private void createShopAndWareHouse(final String shopCode, final String warehouseCode) {
        shop = entityFactory.getByIface(Shop.class);
        shop.setCode(shopCode);
        shop.setName("Test shop");
        shop.setFspointer("/test");
        shop.setImageVaultFolder("/imagevault");
        shop = shopService.create(shop);
        assertNotNull(shop);
        warehouse = entityFactory.getByIface(Warehouse.class);
        warehouse.setCode(warehouseCode);
        warehouse.setName("Test warehouse");
        warehouse = warehouseService.create(warehouse);
        assertNotNull(warehouse);
    }
}
