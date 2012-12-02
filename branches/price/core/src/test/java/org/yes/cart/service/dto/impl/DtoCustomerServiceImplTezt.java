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
import org.yes.cart.domain.dto.AttrValueCustomerDTO;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.CustomerDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoCustomerService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCustomerServiceImplTezt extends BaseCoreDBTestCase {

    private DtoFactory dtoFactory;
    private DtoCustomerService dtoService;
    private DtoAttributeService dtoAttrService;

    @Before
    public void setUp() throws Exception {
        dtoFactory = (DtoFactory) ctx().getBean(ServiceSpringKeys.DTO_FACTORY);
        dtoService = (DtoCustomerService) ctx().getBean(ServiceSpringKeys.DTO_CUSTOMER_SERVICE);
        dtoAttrService = (DtoAttributeService) ctx().getBean(ServiceSpringKeys.DTO_ATTRIBUTE_SERVICE);
    }

    @Test
    public void testCreate() throws Exception {
        CustomerDTO dto = getCustomerDto(getTestName());
        dto = dtoService.create(dto);
        assertTrue(dto.getCustomerId() > 0);
        dtoService.remove(dto.getCustomerId());
    }

    @Test
    public void testUpdate() throws Exception {
        CustomerDTO dto = getCustomerDto(getTestName());
        dto = dtoService.create(dto);
        assertTrue(dto.getCustomerId() > 0);
        dto.setFirstname("Jane");
        dto.setLastname("Gav");
        dto = dtoService.update(dto);
        assertEquals("Jane", dto.getFirstname());
        assertEquals("Gav", dto.getLastname());
        dtoService.remove(dto.getCustomerId());
    }

    @Test
    public void testCreateEntityAttributeValue() throws Exception {
        CustomerDTO dto = getCustomerDto(getTestName());
        dto = dtoService.create(dto);
        assertTrue(dto.getCustomerId() > 0);
        AttributeDTO attrDto = dtoAttrService.getById(1030); //CUSTOMER_PHONE
        AttrValueCustomerDTO attrValueDTO = dtoFactory.getByIface(AttrValueCustomerDTO.class);
        attrValueDTO.setCustomerId(dto.getCustomerId());
        attrValueDTO.setVal("+380978159999");
        attrValueDTO.setAttributeDTO(attrDto);
        dtoService.createEntityAttributeValue(attrValueDTO);
        dto = dtoService.getById(dto.getCustomerId());
        assertFalse(dto.getAttributes().isEmpty());
        assertEquals("+380978159999", dto.getAttributes().iterator().next().getVal());
    }

    @Test
    public void testGetEntityAttributes() throws Exception {
        CustomerDTO dto = getCustomerDto(getTestName());
        dto = dtoService.create(dto);
        assertTrue(dto.getCustomerId() > 0);
        AttributeDTO attrDto = dtoAttrService.getById(1030); //CUSTOMER_PHONE
        AttrValueCustomerDTO attrValueDTO = dtoFactory.getByIface(AttrValueCustomerDTO.class);
        attrValueDTO.setCustomerId(dto.getCustomerId());
        attrValueDTO.setVal("+380978159999");
        attrValueDTO.setAttributeDTO(attrDto);
        dtoService.createEntityAttributeValue(attrValueDTO);
        List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(dto.getCustomerId());
        assertFalse(list.isEmpty());
        assertEquals("+380978159999", list.get(0).getVal());
    }

    @Test
    public void testUpdateEntityAttributeValue() throws Exception {
        CustomerDTO dto = getCustomerDto(getTestName());
        dto = dtoService.create(dto);
        assertTrue(dto.getCustomerId() > 0);
        AttributeDTO attrDto = dtoAttrService.getById(1030); //CUSTOMER_PHONE
        AttrValueCustomerDTO attrValueDTO = dtoFactory.getByIface(AttrValueCustomerDTO.class);
        attrValueDTO.setCustomerId(dto.getCustomerId());
        attrValueDTO.setVal("+380978159999");
        attrValueDTO.setAttributeDTO(attrDto);
        dtoService.createEntityAttributeValue(attrValueDTO);
        List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(dto.getCustomerId());
        assertFalse(list.isEmpty());
        assertEquals("+380978159999", list.get(0).getVal());
        AttrValueCustomerDTO aDto = (AttrValueCustomerDTO) list.get(0);
        aDto.setVal("+44555123456");
        aDto = (AttrValueCustomerDTO) dtoService.updateEntityAttributeValue(aDto);
        assertEquals("+44555123456", aDto.getVal());
    }

    @Test
    public void testDeleteAttributeValue() throws Exception {
        CustomerDTO dto = getCustomerDto(getTestName());
        dto = dtoService.create(dto);
        assertTrue(dto.getCustomerId() > 0);
        AttributeDTO attrDto = dtoAttrService.getById(1030); //CUSTOMER_PHONE
        AttrValueCustomerDTO attrValueDTO = dtoFactory.getByIface(AttrValueCustomerDTO.class);
        attrValueDTO.setCustomerId(dto.getCustomerId());
        attrValueDTO.setVal("+380978159999");
        attrValueDTO.setAttributeDTO(attrDto);
        dtoService.createEntityAttributeValue(attrValueDTO);
        dto = dtoService.getById(dto.getCustomerId());
        assertFalse(dto.getAttributes().isEmpty());
        assertEquals("+380978159999", dto.getAttributes().iterator().next().getVal());
        dtoService.deleteAttributeValue(dto.getAttributes().iterator().next().getAttrvalueId());
        dto = dtoService.getById(dto.getCustomerId());
        assertTrue(dto.getAttributes().isEmpty());
    }

    private CustomerDTO getCustomerDto(String prefix) {
        CustomerDTO dto = dtoFactory.getByIface(CustomerDTO.class);
        dto.setEmail(prefix + "john@doe.com");
        dto.setFirstname(prefix + "John");
        dto.setLastname(prefix + "Doe");
        return dto;
    }
}
