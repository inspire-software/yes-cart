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

package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.DtoServiceSpringKeys;
import org.yes.cart.domain.dto.InventoryDTO;
import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.dto.DtoInventoryService;
import org.yes.cart.service.dto.support.impl.InventoryFilterImpl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 29/05/2015
 * Time: 16:25
 */
public class DtoInventoryServiceImplTezt extends BaseCoreDBTestCase {

    private DtoInventoryService dtoService;
    private DtoFactory dtoFactory;

    @Before
    public void setUp() {
        dtoFactory = (DtoFactory) ctx().getBean(DtoServiceSpringKeys.DTO_FACTORY);
        dtoService = (DtoInventoryService) ctx().getBean("dtoInventoryService");
        super.setUp();
    }


    @Test
    public void testGetInventoryList() throws Exception {

        final InventoryFilterImpl filter = new InventoryFilterImpl();

        // Test no stock when no warehouse
        List<InventoryDTO> stock = dtoService.getInventoryList(filter);
        assertTrue(stock.isEmpty());

        // Has stock when warehouse selected
        filter.setWarehouse(dtoService.getWarehouses().get(0));
        stock = dtoService.getInventoryList(filter);
        assertFalse(stock.isEmpty());

        // Test partial SKU match
        filter.setProductCode("CC_TEST");
        stock = dtoService.getInventoryList(filter);
        assertFalse(stock.isEmpty());

        Set<String> sku = new HashSet<String>();
        for (final InventoryDTO inventory : stock) {
            sku.add(inventory.getSkuCode());
        }

        assertTrue(sku.contains("CC_TEST1"));
        assertTrue(sku.contains("CC_TEST2"));
        assertTrue(sku.contains("CC_TEST3"));

        // Test name SKU match
        filter.setProductCode("cc test 11");
        stock = dtoService.getInventoryList(filter);
        assertFalse(stock.isEmpty());

        for (final InventoryDTO inventory : stock) {
            assertTrue("CC_TEST11".equals(inventory.getSkuCode()));
        }

        // Test exact SKU match
        filter.setProductCode("CC_TEST1");
        filter.setProductCodeExact(true);
        stock = dtoService.getInventoryList(filter);
        assertFalse(stock.isEmpty());

        for (final InventoryDTO inventory : stock) {
            assertTrue("CC_TEST1".equals(inventory.getSkuCode()));
        }

        // Test SKU no match
        filter.setProductCode("something really weird not matching");
        filter.setProductCodeExact(true);
        stock = dtoService.getInventoryList(filter);
        assertTrue(stock.isEmpty());

    }

    @Test
    public void testGetWarehouses() throws Exception {

        final List<WarehouseDTO> warehouseDTOs = dtoService.getWarehouses();
        assertFalse(warehouseDTOs.isEmpty());

        final Set<String> codes = new HashSet<String>();
        for (final WarehouseDTO warehouseDTO : warehouseDTOs) {
            codes.add(warehouseDTO.getCode());
        }

        assertTrue(codes.contains("WAREHOUSE_1"));
        assertTrue(codes.contains("WAREHOUSE_2"));
        assertTrue(codes.contains("WAREHOUSE_3"));

    }

    @Test
    public void testRemoveInventory() throws Exception {

        InventoryDTO dto = dtoService.getInventory(10L);
        assertNotNull(dto);

        dtoService.removeInventory(dto.getSkuWarehouseId());

        dto = dtoService.getInventory(10L);

        assertEquals(BigDecimal.ZERO.setScale(2), dto.getQuantity().setScale(2));
        assertEquals(BigDecimal.ONE.setScale(2), dto.getReserved().setScale(2)); // make sure we are not removing reserved

    }

    @Test
    public void testCreateInventory() throws Exception {

        final InventoryFilterImpl filter = new InventoryFilterImpl();
        filter.setWarehouse(dtoService.getWarehouses().get(0));
        filter.setProductCode("ABC-0001");

        assertTrue(dtoService.getInventoryList(filter).isEmpty());

        InventoryDTO dto = getDto(filter.getProductCode(), filter.getWarehouse().getCode());
        dto = dtoService.createInventory(dto);
        assertTrue(dto.getSkuWarehouseId() > 0L);

        final List<InventoryDTO> stock = dtoService.getInventoryList(filter);
        assertFalse(stock.isEmpty());

        assertEquals(filter.getProductCode(), stock.get(0).getSkuCode());
        assertEquals(BigDecimal.TEN.setScale(2), stock.get(0).getQuantity());

    }

    @Test
    public void testUpdateInventory() throws Exception {

        final InventoryFilterImpl filter = new InventoryFilterImpl();
        filter.setWarehouse(dtoService.getWarehouses().get(0));
        filter.setProductCode("ABC-0002");

        assertTrue(dtoService.getInventoryList(filter).isEmpty());

        InventoryDTO dto = getDto(filter.getProductCode(), filter.getWarehouse().getCode());
        dto = dtoService.createInventory(dto);
        assertTrue(dto.getSkuWarehouseId() > 0L);

        List<InventoryDTO> stock = dtoService.getInventoryList(filter);
        assertFalse(stock.isEmpty());

        assertEquals(filter.getProductCode(), stock.get(0).getSkuCode());
        assertEquals(BigDecimal.TEN.setScale(2), stock.get(0).getQuantity());

        dto.setQuantity(BigDecimal.ONE);
        dto = dtoService.updateInventory(dto);

        stock = dtoService.getInventoryList(filter);
        assertFalse(stock.isEmpty());

        assertEquals(filter.getProductCode(), stock.get(0).getSkuCode());
        assertEquals(BigDecimal.ONE.setScale(2), stock.get(0).getQuantity());


    }


    private InventoryDTO getDto(String sku, String warehouse) {
        InventoryDTO dto = dtoFactory.getByIface(InventoryDTO.class);
        dto.setSkuCode(sku);
        dto.setQuantity(BigDecimal.TEN);
        dto.setWarehouseCode(warehouse);
        return dto;
    }

}
