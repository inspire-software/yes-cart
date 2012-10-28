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

package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.SkuWarehouseDTO;
import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.dto.DtoWarehouseService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoWarehouseServiceImplTezt extends BaseCoreDBTestCase {

    private DtoWarehouseService dtoService;
    private DtoFactory dtoFactory;

    @Before
    public void setUp() throws Exception {
        dtoFactory = (DtoFactory) ctx().getBean(ServiceSpringKeys.DTO_FACTORY);
        dtoService = (DtoWarehouseService) ctx().getBean(ServiceSpringKeys.DTO_WAREHOUSE_SERVICE);
    }

    @Test
    public void testCreate() throws Exception {
        WarehouseDTO dto = getDto(0);
        dto = dtoService.create(dto);
        assertTrue(dto.getWarehouseId() > 0);
    }

    @Test
    public void testUpdate() throws Exception {
        WarehouseDTO dto = getDto(1);
        dto = dtoService.create(dto);
        assertTrue(dto.getWarehouseId() > 0);
        dto.setName("testchangename");
        dto = dtoService.update(dto);
        assertEquals(dto.getName(), "testchangename");
    }

    @Test
    public void testFindByShopId() throws Exception {
        List<WarehouseDTO> dtos = dtoService.findByShopId(10L);
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        //shop has not assigned warehouses
        dtos = dtoService.findByShopId(20L);
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
        //not existing shop
        dtos = dtoService.findByShopId(21L);
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    public void testAssignWarehouse() throws Exception {
        WarehouseDTO dto = getDto(2);
        dto = dtoService.create(dto);
        assertTrue(dto.getWarehouseId() > 0);
        dtoService.assignWarehouse(dto.getWarehouseId(), 20L);
        List<WarehouseDTO> dtos = dtoService.findByShopId(20L);
        assertEquals(1, dtos.size());
        dtoService.unassignWarehouse(dto.getWarehouseId(), 20L);
    }

    @Test
    public void testUnassignWarehouse() throws Exception {
        WarehouseDTO dto = getDto(3);
        dto = dtoService.create(dto);
        assertTrue(dto.getWarehouseId() > 0);
        dtoService.assignWarehouse(dto.getWarehouseId(), 30L);
        List<WarehouseDTO> dtos = dtoService.findByShopId(30L);
        assertEquals(1, dtos.size());
        dtoService.unassignWarehouse(dto.getWarehouseId(), 30L);
        dtos = dtoService.findByShopId(30L);
        assertTrue(dtos.isEmpty());
    }

    @Test
    public void testRemove() throws Exception {
        WarehouseDTO dto = getDto(4);
        dto = dtoService.create(dto);
        assertTrue(dto.getWarehouseId() > 0);
        long id = dto.getWarehouseId();
        dtoService.remove(id);
        dto = dtoService.getById(id);
        assertNull(dto);
    }

    private WarehouseDTO getDto(int idx) {
        WarehouseDTO dto = dtoFactory.getByIface(WarehouseDTO.class);
        dto.setCode("W" + idx);
        dto.setName("warehouese" + idx);
        dto.setDescription("description" + idx);
        return dto;
    }

    @Test
    public void testCreateSkuOnWarehouse() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        SkuWarehouseDTO skuWarehouseDTO = dtoFactory.getByIface(SkuWarehouseDTO.class);
        skuWarehouseDTO.setQuantity(BigDecimal.TEN);
        skuWarehouseDTO.setWarehouseId(1L);
        skuWarehouseDTO.setProductSkuId(9999L);
        skuWarehouseDTO = dtoService.createSkuOnWarehouse(skuWarehouseDTO);
        assertTrue(skuWarehouseDTO.getSkuWarehouseId() > 0);
    }

    @Test
    public void testUpdateSkuOnWarehouse() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        SkuWarehouseDTO skuWarehouseDTO = dtoFactory.getByIface(SkuWarehouseDTO.class);
        skuWarehouseDTO.setQuantity(BigDecimal.TEN);
        skuWarehouseDTO.setWarehouseId(1L);
        skuWarehouseDTO.setProductSkuId(9999L);
        skuWarehouseDTO = dtoService.createSkuOnWarehouse(skuWarehouseDTO);
        assertTrue(skuWarehouseDTO.getSkuWarehouseId() > 0);
        skuWarehouseDTO.setQuantity(BigDecimal.ONE);
        skuWarehouseDTO = dtoService.updateSkuOnWarehouse(skuWarehouseDTO);
        assertEquals(BigDecimal.ONE, skuWarehouseDTO.getQuantity());
    }

    @Test
    public void testRemoveSkuOnWarehouse() {
        dtoService.removeSkuOnWarehouse(13L);
        SkuWarehouse sw = dtoService.getSkuWarehouseService().getById(13L);
        assertEquals(BigDecimal.ZERO.setScale(2), sw.getQuantity().setScale(2));
        assertEquals(BigDecimal.ZERO.setScale(2), sw.getReserved().setScale(2));
    }
}
