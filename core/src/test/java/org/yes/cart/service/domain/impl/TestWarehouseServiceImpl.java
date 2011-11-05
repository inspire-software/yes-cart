package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
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
    private GenericDAO<ShopWarehouse, Long> shopWarehouseDao;
    private Shop shop;
    private Warehouse warehouse;

    @Before
    public void setUp() throws Exception {
        shopService = (ShopService) ctx.getBean(ServiceSpringKeys.SHOP_SERVICE);
        warehouseService = (WarehouseService) ctx.getBean(ServiceSpringKeys.WAREHOUSE_SERVICE);
        entityFactory = warehouseService.getGenericDao().getEntityFactory();
        shopWarehouseDao = (GenericDAO) ctx.getBean(DaoServiceBeanKeys.SHOP_WAREHOUSE_DAO);
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
    }

    @Test
    public void testSetShopWarehouseRank() {
        createShopAndWareHouse("TESTSHOP123", "TESTWAREHOUSE123");
        List<Warehouse> shopWarehouses = warehouseService.findByShopId(shop.getShopId());
        assertNotNull(shopWarehouses);
        assertTrue(shopWarehouses.isEmpty());
        ShopWarehouse shopWarehouse = warehouseService.assignWarehouse(warehouse.getWarehouseId(), shop.getShopId());

        assertEquals("Test default rank", 100, shopWarehouse.getRank());

        warehouseService.setShopWarehouseRank(shopWarehouse.getShopWarehouseId(), 200);

        shopWarehouse = shopWarehouseDao.findById(shopWarehouse.getShopWarehouseId());

        assertEquals("Test default rank", 200, shopWarehouse.getRank());

    }

    /**
     * Unassign warehouse to shop test.
     */
    @Test
    public void testUnassignWarehouse() {

        testAssignWarehouse();

        warehouseService.unassignWarehouse(warehouse.getWarehouseId(), shop.getShopId());

        List<Warehouse> shopWarehouses = warehouseService.findByShopId(shop.getShopId());
        assertNotNull(shopWarehouses);
        assertTrue(shopWarehouses.isEmpty());


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
