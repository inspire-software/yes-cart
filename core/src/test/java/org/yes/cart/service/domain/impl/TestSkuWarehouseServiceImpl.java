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

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestSkuWarehouseServiceImpl extends BaseCoreDBTestCase {

    private WarehouseService warehouseService;
    private SkuWarehouseService skuWarehouseService;
    private ProductSkuService productSkuService;

    @Override
    @Before
    public void setUp()  {
        warehouseService = (WarehouseService) ctx().getBean(ServiceSpringKeys.WAREHOUSE_SERVICE);
        skuWarehouseService = (SkuWarehouseService) ctx().getBean(ServiceSpringKeys.SKU_WAREHOUSE_SERVICE);
        productSkuService = (ProductSkuService) ctx().getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);
        super.setUp();

    }

    @Test
    public void testCreate() {
        SkuWarehouse skuWarehouse = skuWarehouseService.getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
        skuWarehouse.setSkuCode("PRODUCT7");
        skuWarehouse.setWarehouse(warehouseService.findById(1L));
        skuWarehouse.setQuantity(BigDecimal.TEN);
        skuWarehouse = skuWarehouseService.create(skuWarehouse);
        assertTrue(skuWarehouse.getSkuWarehouseId() > 0);
        skuWarehouseService.delete(skuWarehouse);
    }

    // TODO: YC-64 fix to not depend on order or running
    @Test
    public void testGetQuantity() {
        SkuWarehouse skuWarehouse;
        skuWarehouse = skuWarehouseService.getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
        skuWarehouse.setSkuCode("PRODUCT7");
        skuWarehouse.setWarehouse(warehouseService.findById(2L));
        skuWarehouse.setQuantity(new BigDecimal("10.00"));
        skuWarehouse.setReserved(new BigDecimal("5.00"));
        skuWarehouseService.create(skuWarehouse);
        ProductSku psku = productSkuService.findById(11006L);
        SkuWarehouse rez2 = skuWarehouseService.findByWarehouseSku(warehouseService.findById(2L), psku.getCode());
        assertEquals(new BigDecimal("10.00"), rez2.getQuantity());
        assertEquals(new BigDecimal("5.00"), rez2.getReserved());
    }

    /**
     * No records about quantity on warehouses.
     */
    @Test
    public void testGetQuantity2() {
        ProductSku psku = productSkuService.findById(11007L);
        assertNull(skuWarehouseService.findByWarehouseSku(warehouseService.findById(2L), psku.getCode()));
    }

    @Test
    public void testUpdate() {
        SkuWarehouse skuWarehouse = skuWarehouseService.getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
        skuWarehouse.setSkuCode("PRODUCT7");
        skuWarehouse.setWarehouse(warehouseService.findById(1L));
        skuWarehouse.setQuantity(BigDecimal.TEN);
        skuWarehouse = skuWarehouseService.create(skuWarehouse);
        assertTrue(skuWarehouse.getSkuWarehouseId() > 0);
        skuWarehouse.setQuantity(BigDecimal.ONE);
        skuWarehouse = skuWarehouseService.update(skuWarehouse);
        assertEquals(BigDecimal.ONE, skuWarehouse.getQuantity());
        long pk = skuWarehouse.getSkuWarehouseId();
        skuWarehouseService.delete(skuWarehouse);
        skuWarehouse = skuWarehouseService.findById(pk);
        assertNull(skuWarehouse);
    }

    @Test
    public void testDelete() {
        SkuWarehouse skuWarehouse = skuWarehouseService.getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
        skuWarehouse.setSkuCode("PRODUCT7");
        skuWarehouse.setWarehouse(warehouseService.findById(1L));
        skuWarehouse.setQuantity(BigDecimal.TEN);
        skuWarehouse = skuWarehouseService.create(skuWarehouse);
        assertTrue(skuWarehouse.getSkuWarehouseId() > 0);
        long pk = skuWarehouse.getSkuWarehouseId();
        skuWarehouseService.delete(skuWarehouse);
        skuWarehouse = skuWarehouseService.findById(pk);
        assertNull(skuWarehouse);
    }

    @Test
    public void testReserveQuantity() {
        final Warehouse warehouse = warehouseService.findById(1L);
        ProductSku productSku = productSkuService.findById(10004L); // 4 items on 1 warehouse
        assertEquals(MoneyUtils.ZERO, skuWarehouseService.reservation(warehouse, productSku.getCode(), new BigDecimal("3.00"))); // 4 total and 3 reserved
        SkuWarehouse rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());

        assertEquals(new BigDecimal("4.00"), rez.getQuantity());
        assertEquals(new BigDecimal("3.00"), rez.getReserved());
        assertEquals(new BigDecimal("10.00"), skuWarehouseService.reservation(warehouse, productSku.getCode(), new BigDecimal("11.00"))); // 4 total and 4 reserved 10 to reserve
        rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());
        assertEquals(new BigDecimal("4.00"), rez.getQuantity());
        assertEquals(new BigDecimal("4.00"), rez.getReserved());
    }

    @Test
    public void testReserveBackorderQuantity() {
        final Warehouse warehouse = warehouseService.findById(1L);
        ProductSku productSku = productSkuService.findById(10004L); // 4 items on 1 warehouse
        assertEquals(MoneyUtils.ZERO, skuWarehouseService.reservation(warehouse, productSku.getCode(), new BigDecimal("3.00"))); // 4 total and 3 reserved
        SkuWarehouse rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());

        assertEquals(new BigDecimal("4.00"), rez.getQuantity());
        assertEquals(new BigDecimal("3.00"), rez.getReserved());
        assertEquals(new BigDecimal("0.00"), skuWarehouseService.reservation(warehouse, productSku.getCode(), new BigDecimal("11.00"), true)); // 4 total and 14 reserved 0 to reserve
        rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());
        assertEquals(new BigDecimal("4.00"), rez.getQuantity());
        assertEquals(new BigDecimal("14.00"), rez.getReserved());
    }

    /**
     * Need to reduce reserved quantity and reduce available quantity
     */
    @Test
    public void testReservationAndVoidReservation() {
        final Warehouse warehouse = warehouseService.findById(1L);
        ProductSku productSku = productSkuService.findById(10004L); // 4 items on 1 warehouse
        //3 items reservation
        BigDecimal toReserve = skuWarehouseService.reservation(warehouse, productSku.getCode(), new BigDecimal("3.00"));
        assertEquals(MoneyUtils.ZERO, toReserve); // 4 total and 3 reserved
        SkuWarehouse rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());
        assertEquals(new BigDecimal("4.00"), rez.getQuantity());
        assertEquals(new BigDecimal("3.00"), rez.getReserved());
        // void 2 items
        BigDecimal toVoidReserve = skuWarehouseService.voidReservation(warehouse, productSku.getCode(), new BigDecimal("2.00"));
        assertEquals(MoneyUtils.ZERO, toVoidReserve); // 4 total and 3 reserved
        rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());
        assertEquals(new BigDecimal("4.00"), rez.getQuantity());
        assertEquals(new BigDecimal("1.00"), rez.getReserved());
        // void 1 item
        toVoidReserve = skuWarehouseService.voidReservation(warehouse, productSku.getCode(), new BigDecimal("1.00"));
        assertEquals(MoneyUtils.ZERO, toVoidReserve); // 4 total and 3 reserved
        rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());
        assertEquals(new BigDecimal("4.00"), rez.getQuantity());
        assertEquals(new BigDecimal("0.00"), rez.getReserved());
        // void 5 items
        toVoidReserve = skuWarehouseService.voidReservation(warehouse, productSku.getCode(), new BigDecimal("5.00"));
        assertEquals(new BigDecimal("5.00"), toVoidReserve);
        rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());
        assertEquals(new BigDecimal("4.00"), rez.getQuantity());
        assertEquals(new BigDecimal("0.00"), rez.getReserved());
    }

    /**
     * Need to reduce reserved quantity and reduce available quantity
     */
    @Test
    public void testReservationBackorderAndVoidReservation() {
        final Warehouse warehouse = warehouseService.findById(1L);
        ProductSku productSku = productSkuService.findById(10004L); // 4 items on 1 warehouse
        //3 items reservation
        BigDecimal toReserve = skuWarehouseService.reservation(warehouse, productSku.getCode(), new BigDecimal("10.00"), true);
        assertEquals(MoneyUtils.ZERO, toReserve); // 4 total and 10 reserved
        SkuWarehouse rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());
        assertEquals(new BigDecimal("4.00"), rez.getQuantity());
        assertEquals(new BigDecimal("10.00"), rez.getReserved());
        // void 2 items
        BigDecimal toVoidReserve = skuWarehouseService.voidReservation(warehouse, productSku.getCode(), new BigDecimal("2.00"));
        assertEquals(MoneyUtils.ZERO, toVoidReserve); // 4 total and 8 reserved
        rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());
        assertEquals(new BigDecimal("4.00"), rez.getQuantity());
        assertEquals(new BigDecimal("8.00"), rez.getReserved());
        // void 5 item
        toVoidReserve = skuWarehouseService.voidReservation(warehouse, productSku.getCode(), new BigDecimal("5.00"));
        assertEquals(MoneyUtils.ZERO, toVoidReserve); // 4 total and 3 reserved
        rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());
        assertEquals(new BigDecimal("4.00"), rez.getQuantity());
        assertEquals(new BigDecimal("3.00"), rez.getReserved());
        // void 5 items
        toVoidReserve = skuWarehouseService.voidReservation(warehouse, productSku.getCode(), new BigDecimal("5.00"));
        assertEquals(new BigDecimal("2.00"), toVoidReserve);
        rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());
        assertEquals(new BigDecimal("4.00"), rez.getQuantity());
        assertEquals(new BigDecimal("0.00"), rez.getReserved());
    }

    @Test
    public void testDebitCredit() {
        final Warehouse warehouse = warehouseService.findById(1L);
        ProductSku productSku = productSkuService.findById(10004L); // 4 items on 1 warehouse
        assertEquals(MoneyUtils.ZERO, skuWarehouseService.debit(warehouse, productSku.getCode(), new BigDecimal("3.00"))); // 2 total and 1 reserved
        SkuWarehouse rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());
        assertEquals(new BigDecimal("1.00"), rez.getQuantity());
        assertEquals(new BigDecimal("0.00"), rez.getReserved());
        assertEquals(MoneyUtils.ZERO, skuWarehouseService.debit(warehouse, productSku.getCode(), new BigDecimal("1.00"))); // 2 total and 1 reserved
        rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());
        assertEquals(new BigDecimal("0.00"), rez.getQuantity());
        assertEquals(new BigDecimal("0.00"), rez.getReserved());
        assertEquals(new BigDecimal("7.00"), skuWarehouseService.debit(warehouse, productSku.getCode(), new BigDecimal("7.00")));
        rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());
        assertEquals(new BigDecimal("0.00"), rez.getQuantity());
        assertEquals(new BigDecimal("0.00"), rez.getReserved());
        assertEquals(new BigDecimal("0.00"), skuWarehouseService.debit(warehouse, productSku.getCode(), new BigDecimal("0.00")));
        rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());
        assertEquals(new BigDecimal("0.00"), rez.getQuantity());
        assertEquals(new BigDecimal("0.00"), rez.getReserved());
        skuWarehouseService.credit(warehouse, productSku.getCode(), new BigDecimal("74.00"));
        rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());
        assertEquals(new BigDecimal("74.00"), rez.getQuantity());
        assertEquals(new BigDecimal("0.00"), rez.getReserved());
        skuWarehouseService.credit(warehouse, productSku.getCode(), new BigDecimal("0.00"));
        rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());
        assertEquals(new BigDecimal("74.00"), rez.getQuantity());
        assertEquals(new BigDecimal("0.00"), rez.getReserved());
        assertEquals(new BigDecimal("6.00"), skuWarehouseService.debit(warehouse, productSku.getCode(), new BigDecimal("80.00")));
        rez = skuWarehouseService.findByWarehouseSku(warehouse, productSku.getCode());
        assertEquals(new BigDecimal("0.00"), rez.getQuantity());
        assertEquals(new BigDecimal("0.00"), rez.getReserved());
    }

    @Test
    public void testFindProductSkusOnWarehouse() {
        //10000 product id - sobot has 4 skus on 1 warehouse
        List<SkuWarehouse> skusWarehouseList = skuWarehouseService.getProductSkusOnWarehouse(10000L, 1L);
        assertEquals(4, skusWarehouseList.size());
        for (SkuWarehouse skuWarehouse : skusWarehouseList) {
            if ("SOBOT-BEER".equals(skuWarehouse.getSkuCode())) {
                assertEquals(1, skuWarehouse.getQuantity().intValue());
            }
            if ("SOBOT-PINK".equals(skuWarehouse.getSkuCode())) {
                assertEquals(2, skuWarehouse.getQuantity().intValue());
            }
            if ("SOBOT-LIGHT".equals(skuWarehouse.getSkuCode())) {
                assertEquals(3, skuWarehouse.getQuantity().intValue());
            }
            if ("SOBOT-ORIG".equals(skuWarehouse.getSkuCode())) {
                assertEquals(4, skuWarehouse.getQuantity().intValue());
            }
        }
    }


}
