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
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.dto.DtoAttributeService;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoAttributeServiceImplTezt extends BaseCoreDBTestCase {

    private DtoAttributeService dtoAttributeService;
    private DtoFactory dtoFactory;

    @Before
    public void setUp() throws Exception {
        dtoAttributeService = (DtoAttributeService) ctx().getBean(ServiceSpringKeys.DTO_ATTRIBUTE_SERVICE);
        dtoFactory = (DtoFactory) ctx().getBean(ServiceSpringKeys.DTO_FACTORY);
    }

    @Test
    public void testCreate() throws Exception {
        AttributeDTO dto = getDto();
        dto = dtoAttributeService.create(dto);
        assertTrue(dto.getAttributeId() > 0);
    }

    @Test
    public void testUpdate() throws Exception {
        AttributeDTO dto = getDto();
        dto = dtoAttributeService.create(dto);
        long id = dto.getAttributeId();
        assertTrue(dto.getAttributeId() > 0);
        assertEquals("string value", dto.getVal());
        dto.setVal("i love you meat bags");
        dtoAttributeService.update(dto);
        dto = dtoAttributeService.getById(id);
        assertEquals("i love you meat bags", dto.getVal());
    }

    @Test
    public void testFindByAttributeGroupCode() throws Exception {
        List<AttributeDTO> dtos = dtoAttributeService.findByAttributeGroupCode("CATEGORY");
        assertNotNull(dtos);
        assertEquals(3, dtos.size());
        dtos = dtoAttributeService.findByAttributeGroupCode("NONEXISTINGGROUP");
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    public void testFindAvailableAttributes() throws Exception {
        List<AttributeDTO> dtos = dtoAttributeService.findAvailableAttributes(
                "CATEGORY",
                Collections.singletonList("CATEGORY_ITEMS_PER_PAGE")
        );
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        for (AttributeDTO dto : dtos) {
            assertFalse("CATEGORY_ITEMS_PER_PAGE".equals(dto.getCode()));
        }
    }

    @Test
    public void testFindAttributesWithMultipleValues() throws Exception {
        List<AttributeDTO> dtos = dtoAttributeService.findAttributesWithMultipleValues(
                "PRODUCT");
        assertNotNull(dtos);
        assertFalse(dtos.isEmpty());
        for (AttributeDTO dto : dtos) {
            assertTrue(dto.isAllowduplicate());
        }
        dtos = dtoAttributeService.findAttributesWithMultipleValues(
                "SYSTEM");
        assertNull(dtos);
    }

    private AttributeDTO getDto() {
        AttributeDTO dto = dtoFactory.getByIface(AttributeDTO.class);
        dto.setCode("TESTCODE");
        dto.setMandatory(true);
        dto.setVal("string value");
        dto.setName("test attr");
        dto.setDescription("test attr description");
        dto.setEtypeId(1000L); //string
        dto.setAttributegroupId(1006L); //customer
        dto.setAllowduplicate(true);
        dto.setAllowfailover(false);
        dto.setRegexp("[a-zA-Z]");
        return dto;
    }
}
