/*
 * Copyright 2009 Inspire-Software.com
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
import org.yes.cart.domain.dto.BrandDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.service.dto.DtoBrandService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoBrandServiceImplTezt extends BaseCoreDBTestCase {

    private DtoBrandService dtoService;
    private DtoFactory dtoFactory;

    @Override
    @Before
    public void setUp() {
        dtoService = (DtoBrandService) ctx().getBean(DtoServiceSpringKeys.DTO_BRAND_SERVICE);
        dtoFactory = (DtoFactory) ctx().getBean(DtoServiceSpringKeys.DTO_FACTORY);
        super.setUp();
    }

    @Test
    public void testFindBrand() throws Exception {

        // full name
        final SearchContext filterSony = createSearchContext("name", false, 0, 10, "filter", "Sony");
        SearchResult<BrandDTO> dto = dtoService.findBrands(filterSony);
        assertFalse(dto.getItems().isEmpty());
        assertEquals("Sony", dto.getItems().get(0).getName());

        // partial
        final SearchContext filterFuture = createSearchContext("name", false, 0, 10, "filter", "future");
        dto = dtoService.findBrands(filterFuture);
        assertFalse(dto.getItems().isEmpty());
        assertEquals("FutureRobots", dto.getItems().get(0).getName());
    }

    @Test
    public void testCreate() throws Exception {
        BrandDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getBrandId() > 0);
    }

    @Test
    public void testUpdate() throws Exception {
        BrandDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getBrandId() > 0);
        long id = dto.getBrandId();
        dto.setDescription("Yeah, well, I'm gonna go build my own theme park, with blackjack and hookers. In fact, forget the park!");
        dtoService.update(dto);
        dto = dtoService.getById(id);
        assertEquals("Yeah, well, I'm gonna go build my own theme park, with blackjack and hookers. In fact, forget the park!",
                dto.getDescription());
    }

    @Test
    public void testGetAll() throws Exception {
        List<BrandDTO> list = dtoService.getAll();
        assertFalse(list.isEmpty());
    }

    @Test
    public void testRemove() throws Exception {
        BrandDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getBrandId() > 0);
        long id = dto.getBrandId();
        dtoService.remove(id);
        dto = dtoService.getById(id);
        assertNull(dto);
    }

    private BrandDTO getDto() {
        BrandDTO dto = dtoFactory.getByIface(BrandDTO.class);
        dto.setName("AutoZAZ");
        dto.setDescription("Eared Car Producer");
        return dto;
    }
}
