package org.yes.cart.service.dto.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.SkuWarehouseDTO;
import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.dto.DtoWarehouseService;

import java.math.BigDecimal;
import java.util.List;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoWarehouseServiceImplTest extends BaseCoreDBTestCase {

    private DtoWarehouseService dtoService = null;
    private DtoFactory dtoFactory = null;

    @Before
    public void setUp() throws Exception {
        // Add your code here
        super.setUp();
        dtoFactory = (DtoFactory) ctx.getBean(ServiceSpringKeys.DTO_FACTORY);
        dtoService = (DtoWarehouseService) ctx.getBean(ServiceSpringKeys.DTO_WAREHOUSE_SERVICE);
    }

    @After
    public void tearDown() {
        dtoService = null;
        dtoFactory = null;
        super.tearDown();
    }

    @Test
    public void testCreate() {
        WarehouseDTO dto = getDto(0);
        try {
            dto = dtoService.create(dto);
            assertTrue(dto.getWarehouseId() > 0);
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

    }

    @Test
    public void testUpdate() {
        WarehouseDTO dto = getDto(1);
        try {
            dto = dtoService.create(dto);
            assertTrue(dto.getWarehouseId() > 0);
            dto.setName("testchangename");
            dto = dtoService.update(dto);
            assertEquals(dto.getName(), "testchangename");
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

    }

    @Test
    public void testFindByShopId() {
        try {
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


        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testAssignWarehouse() {
        WarehouseDTO dto = getDto(2);
        try {
            dto = dtoService.create(dto);
            assertTrue(dto.getWarehouseId() > 0);
            dtoService.assignWarehouse(dto.getWarehouseId(), 20L);

            List<WarehouseDTO> dtos = dtoService.findByShopId(20L);
            assertEquals(1, dtos.size());

            dtoService.unassignWarehouse(dto.getWarehouseId(), 20L);

        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testUnassignWarehouse() {
        WarehouseDTO dto = getDto(3);
        try {
            dto = dtoService.create(dto);
            assertTrue(dto.getWarehouseId() > 0);
            dtoService.assignWarehouse(dto.getWarehouseId(), 30L );

            List<WarehouseDTO> dtos = dtoService.findByShopId(30L);
            assertEquals(1, dtos.size());

            dtoService.unassignWarehouse(dto.getWarehouseId(), 30L);
            dtos = dtoService.findByShopId(30L);
            assertTrue(dtos.isEmpty());


        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

    }

    @Test
    public void testRemove() {
        WarehouseDTO dto = getDto(4);
        try {
            dto = dtoService.create(dto);
            assertTrue(dto.getWarehouseId() > 0);
            long id = dto.getWarehouseId();
            dtoService.remove(id);
            dto = dtoService.getById(id);
            assertNull(dto);
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    private WarehouseDTO getDto(int idx) {
        WarehouseDTO dto = dtoFactory.getByIface(WarehouseDTO.class);
        dto.setCode("W"+idx);
        dto.setName("warehouese"+idx);
        dto.setDescription("description"+idx);
        return dto;
    }

    @Test
    public void testCreateSkuOnWarehouse() {
        SkuWarehouseDTO skuWarehouseDTO = dtoFactory.getByIface(SkuWarehouseDTO.class);
        skuWarehouseDTO.setQuantity(BigDecimal.TEN);
        skuWarehouseDTO.setWarehouseId(1L);
        skuWarehouseDTO.setProductSkuId(9999L);

        skuWarehouseDTO = dtoService.createSkuOnWarehouse(skuWarehouseDTO);
        assertTrue(skuWarehouseDTO.getSkuWarehouseId() > 0);

    }

    @Test
    public void testUpdateSkuOnWarehouse() {
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
        assertNull(dtoService.getSkuWarehouseService().getById(13L));
    }





}
