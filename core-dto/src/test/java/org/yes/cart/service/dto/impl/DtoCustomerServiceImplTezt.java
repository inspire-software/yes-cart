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
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.DtoServiceSpringKeys;
import org.yes.cart.domain.dto.AttrValueCustomerDTO;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.CustomerDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoCustomerService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

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
    public void setUp() {
        dtoFactory = (DtoFactory) ctx().getBean(DtoServiceSpringKeys.DTO_FACTORY);
        dtoService = (DtoCustomerService) ctx().getBean(DtoServiceSpringKeys.DTO_CUSTOMER_SERVICE);
        dtoAttrService = (DtoAttributeService) ctx().getBean(DtoServiceSpringKeys.DTO_ATTRIBUTE_SERVICE);
        super.setUp();
    }


    @Test
    public void testFindCustomer() throws Exception {
        CustomerDTO jane = getCustomerDto(getTestName() + "_c");
        jane = dtoService.createForShop(jane, 10L);
        assertTrue(jane.getCustomerId() > 0);
        jane.setFirstname("Jane");
        jane.setLastname("Gav");
        jane.setCustomerType("B2C");
        jane = dtoService.update(jane);
        CustomerDTO bob = getCustomerDto(getTestName() + "_d");
        bob = dtoService.createForShop(bob, 10L);
        assertTrue(bob.getCustomerId() > 0);
        bob.setFirstname("Bob");
        bob.setLastname("Doe");
        bob.setCustomerType("B2B");
        bob = dtoService.update(bob);

        final Set<Long> shopIds = Collections.singleton(10L);
        SearchContext ctx;
        SearchResult<CustomerDTO> rez;

        // check by id
        ctx = new SearchContext(Collections.singletonMap("filter", Collections.singletonList("#" + jane.getCustomerId())), 0, 10, null, false, "filter");
        rez = dtoService.findCustomer(shopIds, ctx);
        assertEquals(1, rez.getTotal());
        assertEquals(1, rez.getItems().size());
        assertEquals("Jane", rez.getItems().get(0).getFirstname());

        // check by name
        ctx = new SearchContext(Collections.singletonMap("filter", Collections.singletonList("?bob")), 0, 10, null, false, "filter");
        rez = dtoService.findCustomer(shopIds, ctx);
        assertEquals(1, rez.getTotal());
        assertEquals(1, rez.getItems().size());
        assertEquals("Bob", rez.getItems().get(0).getFirstname());

        // check by type
        ctx = new SearchContext(Collections.singletonMap("filter", Collections.singletonList("$b2c")), 0, 10, null, false, "filter");
        rez = dtoService.findCustomer(shopIds, ctx);
        assertTrue(rez.getTotal() > 0);
        assertEquals("B2C", rez.getItems().get(0).getCustomerType());

        dtoService.remove(bob.getCustomerId());
        dtoService.remove(jane.getCustomerId());
    }


    @Test
    public void testCreate() throws Exception {
        CustomerDTO dto = getCustomerDto(getTestName());
        dto = dtoService.createForShop(dto, 10L);
        assertTrue(dto.getCustomerId() > 0);
        dtoService.remove(dto.getCustomerId());
    }

    @Test
    public void testUpdate() throws Exception {
        CustomerDTO dto = getCustomerDto(getTestName());
        dto = dtoService.createForShop(dto, 10L);
        assertTrue(dto.getCustomerId() > 0);
        dto.setFirstname("Jane");
        dto.setLastname("Gav");
        dto = dtoService.update(dto);
        assertEquals("Jane", dto.getFirstname());
        assertEquals("Gav", dto.getLastname());
        dtoService.remove(dto.getCustomerId());
    }

    @Test
    public void testUpdateCustomerTags() throws Exception {
        CustomerDTO dto = getCustomerDto(getTestName());
        dto = dtoService.createForShop(dto, 10L);
        assertTrue(dto.getCustomerId() > 0);
        dto.setFirstname("Jane");
        dto.setLastname("Gav");
        dto = dtoService.update(dto);
        dtoService.updateCustomerTags(dto, "tag1 tag2 tag2 tag3");
        dto = dtoService.getById(dto.getCustomerId());
        assertEquals("Jane", dto.getFirstname());
        assertEquals("Gav", dto.getLastname());
        assertEquals("tag1 tag2 tag3", dto.getTag());
        dtoService.updateCustomerTags(dto, "");
        dto = dtoService.getById(dto.getCustomerId());
        assertEquals("Jane", dto.getFirstname());
        assertEquals("Gav", dto.getLastname());
        assertNull(dto.getTag());
        dtoService.remove(dto.getCustomerId());
    }

    @Test
    public void testCreateEntityAttributeValue() throws Exception {
        CustomerDTO dto = getCustomerDto(getTestName());
        dto = dtoService.createForShop(dto, 10L);
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
        dto = dtoService.createForShop(dto, 10L);
        assertTrue(dto.getCustomerId() > 0);
        AttributeDTO attrDto = dtoAttrService.getById(1030); //CUSTOMER_PHONE
        AttrValueCustomerDTO attrValueDTO = dtoFactory.getByIface(AttrValueCustomerDTO.class);
        attrValueDTO.setCustomerId(dto.getCustomerId());
        attrValueDTO.setVal("+380978159999");
        attrValueDTO.setAttributeDTO(attrDto);
        dtoService.createEntityAttributeValue(attrValueDTO);
        List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(dto.getCustomerId());
        assertFalse(list.isEmpty());
        for (AttrValueDTO av : list) {
            if (AttributeNamesKeys.Customer.CUSTOMER_PHONE.equals(av.getAttributeDTO().getCode())) {
                assertEquals("+380978159999", av.getVal());
            }
        }
    }

    @Test
    public void testUpdateEntityAttributeValue() throws Exception {
        CustomerDTO dto = getCustomerDto(getTestName());
        dto = dtoService.createForShop(dto, 10L);
        assertTrue(dto.getCustomerId() > 0);
        AttributeDTO attrDto = dtoAttrService.getById(1030); //CUSTOMER_PHONE
        AttrValueCustomerDTO attrValueDTO = dtoFactory.getByIface(AttrValueCustomerDTO.class);
        attrValueDTO.setCustomerId(dto.getCustomerId());
        attrValueDTO.setVal("+380978159999");
        attrValueDTO.setAttributeDTO(attrDto);
        dtoService.createEntityAttributeValue(attrValueDTO);
        List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(dto.getCustomerId());
        assertFalse(list.isEmpty());
        AttrValueCustomerDTO aDto = null;
        for (AttrValueDTO av : list) {
            if (AttributeNamesKeys.Customer.CUSTOMER_PHONE.equals(av.getAttributeDTO().getCode())) {
                assertEquals("+380978159999", av.getVal());
                aDto = (AttrValueCustomerDTO) av;
            }
        }
        assertNotNull(aDto);
        aDto.setVal("+44555123456");
        aDto = (AttrValueCustomerDTO) dtoService.updateEntityAttributeValue(aDto);
        assertEquals("+44555123456", aDto.getVal());
    }



    @Test
    public void testDeleteAttributeValue() throws Exception {
        CustomerDTO dto = getCustomerDto(getTestName());
        dto = dtoService.createForShop(dto, 10L);
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
