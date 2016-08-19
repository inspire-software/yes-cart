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

package org.yes.cart.service.domain.impl;

import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.WarehouseService;

import java.util.Iterator;
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
        List<Warehouse> shopWarehouses = warehouseService.getByShopId(shop.getShopId(), false);
        assertNotNull(shopWarehouses);
        assertTrue(shopWarehouses.isEmpty());
        warehouseService.assignWarehouse(warehouse.getWarehouseId(), shop.getShopId(), false);
        shopWarehouses = warehouseService.getByShopId(shop.getShopId(), false);
        assertNotNull(shopWarehouses);
        assertFalse(shopWarehouses.isEmpty());
        assertEquals(warehouse.getWarehouseId(), shopWarehouses.get(0).getWarehouseId());

        // unassign
        warehouseService.unassignWarehouse(warehouse.getWarehouseId(), shop.getShopId(), false);
        List<Warehouse> shopWarehousesAfter = warehouseService.getByShopId(shop.getShopId(), false);
        assertNotNull(shopWarehousesAfter);
        assertTrue(shopWarehousesAfter.isEmpty());

    }

    @Test
    public void testSetShopWarehouseRank() {
        createShopAndWareHouse("TESTSHOP123", "TESTWAREHOUSE123");
        List<Warehouse> shopWarehouses = warehouseService.getByShopId(shop.getShopId(), false);
        assertNotNull(shopWarehouses);
        assertTrue(shopWarehouses.isEmpty());

        warehouseService.assignWarehouse(warehouse.getWarehouseId(), shop.getShopId(), false);
        final long[] pk = new long[1];
        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                final Iterator<ShopWarehouse> it = warehouseService.findById(warehouse.getWarehouseId()).getWarehouseShop().iterator();
                ShopWarehouse shopWarehouse = null;
                while (it.hasNext()) {
                    shopWarehouse = it.next();
                    if (shopWarehouse.getShop().getShopId() == shop.getShopId()) {
                        break;
                    } else {
                        shopWarehouse = null;
                    }
                }
                assertNotNull(shopWarehouse);
                assertEquals("Test default rank", new Integer(100), shopWarehouse.getRank());
                pk[0] = shopWarehouse.getShopWarehouseId();
            }
        });

        warehouseService.updateShopWarehouseRank(pk[0], 200);
        ShopWarehouse shopWarehouse = warehouseService.findShopWarehouseById(pk[0]);
        assertEquals("Test default rank", new Integer(200), shopWarehouse.getRank());
    }

    private void createShopAndWareHouse(final String shopCode, final String warehouseCode) {
        shop = entityFactory.getByIface(Shop.class);
        shop.setCode(shopCode);
        shop.setName("Test shop");
        shop.setFspointer("/test");
        shop = shopService.create(shop);
        assertNotNull(shop);
        warehouse = entityFactory.getByIface(Warehouse.class);
        warehouse.setCode(warehouseCode);
        warehouse.setName("Test warehouse");
        warehouse = warehouseService.create(warehouse);
        assertNotNull(warehouse);
    }
}
