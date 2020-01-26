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
import org.yes.cart.domain.dto.CarrierDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.service.dto.DtoCarrierService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 21/01/2020
 * Time: 08:17
 */
public class DtoCarrierServiceImplTest extends BaseCoreDBTestCase {

    private DtoCarrierService dtoService;
    private DtoFactory dtoFactory;


    @Before
    public void setUp() {
        dtoFactory = (DtoFactory) ctx().getBean(DtoServiceSpringKeys.DTO_FACTORY);
        dtoService = (DtoCarrierService) ctx().getBean(DtoServiceSpringKeys.DTO_CARRIER_SERVICE);
        super.setUp();
    }

    @Test
    public void testCreate() throws Exception {
        CarrierDTO dto = getDto(0);
        dto = dtoService.create(dto);
        assertTrue(dto.getCarrierId() > 0);
    }

    @Test
    public void testUpdate() throws Exception {
        CarrierDTO dto = getDto(1);
        dto = dtoService.create(dto);
        assertTrue(dto.getCarrierId() > 0);
        dto.setName("testchangename");
        dto = dtoService.update(dto);
        assertEquals(dto.getName(), "testchangename");
    }


    @Test
    public void testFindCarriers() throws Exception {

        final List<Long> shopIdsNone = Collections.singletonList(9999L);
        final List<Long> shopIds = Arrays.asList(10L);
        SearchContext ctx;
        SearchResult<CarrierDTO> rez;

        // check by code
        ctx = createSearchContext("guid", false, 0, 10,
                "filter", "1_CARRIER",
                "shopIds", shopIds
        );
        rez = dtoService.findCarriers(ctx);
        assertEquals(1, rez.getTotal());
        assertEquals(1, rez.getItems().size());
        assertEquals("1_CARRIER", rez.getItems().get(0).getCode());

        // check non for wrong shop IDS
        ctx = createSearchContext("guid", false, 0, 10,
                "shopIds", shopIdsNone
        );
        rez = dtoService.findCarriers(ctx);
        assertEquals(0, rez.getTotal());

        // check all
        ctx = createSearchContext("guid", false, 0, 10,
                "shopIds", shopIds
        );
        rez = dtoService.findCarriers(ctx);
        assertEquals(2, rez.getTotal());
        assertEquals(2, rez.getItems().size());
        assertEquals("1_CARRIER", rez.getItems().get(0).getCode());
        assertEquals("2_CARRIER", rez.getItems().get(1).getCode());

        // check all shops
        ctx = createSearchContext("guid", false, 0, 10,
                "filter", "_CARRIER"
        );
        rez = dtoService.findCarriers(ctx);
        assertEquals(3, rez.getTotal());
        assertEquals(3, rez.getItems().size());
        assertEquals("1_CARRIER", rez.getItems().get(0).getCode());
        assertEquals("2_CARRIER", rez.getItems().get(1).getCode());
        assertEquals("3_CARRIER", rez.getItems().get(2).getCode());

    }


    @Test
    public void testAssignCarrier() throws Exception {
        CarrierDTO dto = getDto(2);
        dto = dtoService.create(dto);
        assertTrue(dto.getCarrierId() > 0);
        dtoService.assignToShop(dto.getCarrierId(), 30L, true);
        Map<CarrierDTO, Boolean> dtos = dtoService.findAllByShopId(30L);
        assertEquals(1, dtos.size());
        CarrierDTO updated = dtoService.getById(dto.getCarrierId());
        assertEquals(1, updated.getCarrierShop().size());
        dtoService.unassignFromShop(dto.getCarrierId(), 30L, false);
        dtos = dtoService.findAllByShopId(30L);
        assertEquals(0, dtos.size());
    }

    @Test
    public void testRemove() throws Exception {
        CarrierDTO dto = getDto(4);
        dto = dtoService.create(dto);
        assertTrue(dto.getCarrierId() > 0);
        long id = dto.getCarrierId();
        dtoService.remove(id);
        dto = dtoService.getById(id);
        assertNull(dto);
    }



    private CarrierDTO getDto(int idx) {
        CarrierDTO dto = dtoFactory.getByIface(CarrierDTO.class);
        dto.setCode("c" + idx);
        dto.setName("c" + idx);
        dto.setDescription("description" + idx);
        return dto;
    }


}