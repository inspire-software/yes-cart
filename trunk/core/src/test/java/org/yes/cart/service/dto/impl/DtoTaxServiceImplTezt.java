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
import org.yes.cart.domain.dto.TaxDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.dto.DtoTaxService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 31/10/2014
 * Time: 13:10
 */
public class DtoTaxServiceImplTezt extends BaseCoreDBTestCase {

    private DtoTaxService dtoTaxService;
    private DtoFactory dtoFactory;

    @Before
    public void setUp() {
        dtoTaxService = (DtoTaxService) ctx().getBean(ServiceSpringKeys.DTO_TAX_SERVICE);
        dtoFactory = (DtoFactory) ctx().getBean(ServiceSpringKeys.DTO_FACTORY);
        super.setUp();
    }


    @Test
    public void testFindByParameters() throws Exception {
        TaxDTO taxDTO = getDto();
        taxDTO = dtoTaxService.create(taxDTO);

        // retrieve specific
        List<TaxDTO> taxes = dtoTaxService.findByParameters(
                taxDTO.getCode(),
                taxDTO.getShopCode(),
                taxDTO.getCurrency());

        assertNotNull(taxes);
        assertEquals(1, taxes.size());

        // retrieve all
        taxes = dtoTaxService.findByParameters(
                null,
                null,
                null);

        assertNotNull(taxes);
        assertEquals(9, taxes.size());

        // retrieve non-existent
        taxes = dtoTaxService.findByParameters(
                "zzzz",
                null,
                null);

        assertNotNull(taxes);
        assertEquals(0, taxes.size());

        dtoTaxService.remove(taxDTO.getTaxId());
    }


    @Test
    public void testCreate() throws Exception {
        TaxDTO taxDTO = getDto();
        taxDTO = dtoTaxService.create(taxDTO);
        assertTrue(taxDTO.getTaxId() > 0);
        dtoTaxService.remove(taxDTO.getTaxId());
    }

    @Test
    public void testUpdate() throws Exception {
        TaxDTO taxDTO = getDto();
        taxDTO = dtoTaxService.create(taxDTO);
        assertTrue(taxDTO.getTaxId() > 0);
        taxDTO.setDescription("other desc");
        taxDTO = dtoTaxService.update(taxDTO);
        assertEquals("other desc", taxDTO.getDescription());
        dtoTaxService.remove(taxDTO.getTaxId());
    }

    @Test
    public void testRemove() throws Exception {
        TaxDTO taxDTO = getDto();
        taxDTO = dtoTaxService.create(taxDTO);
        assertTrue(taxDTO.getTaxId() > 0);
        long id = taxDTO.getTaxId();
        dtoTaxService.remove(id);
        taxDTO = dtoTaxService.getById(id);
        assertNull(taxDTO);
    }


    private TaxDTO getDto() {

        TaxDTO taxDTO = dtoFactory.getByIface(TaxDTO.class);
        taxDTO.setShopCode("SHOP10");
        taxDTO.setCurrency("EUR");
        taxDTO.setCode("TESTCODE");
        taxDTO.setTaxRate(new BigDecimal("20.00"));
        taxDTO.setExclusiveOfPrice(true);
        taxDTO.setDescription("Test description");

        return taxDTO;
    }
}
