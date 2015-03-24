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
import org.yes.cart.domain.dto.TaxConfigDTO;
import org.yes.cart.domain.dto.TaxDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.dto.DtoTaxConfigService;
import org.yes.cart.service.dto.DtoTaxService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 31/10/2014
 * Time: 13:24
 */
public class DtoTaxConfigServiceImplTezt extends BaseCoreDBTestCase {

    private DtoTaxService dtoTaxService;
    private DtoTaxConfigService dtoTaxConfigService;
    private DtoFactory dtoFactory;

    @Before
    public void setUp() {
        dtoTaxService = (DtoTaxService) ctx().getBean(ServiceSpringKeys.DTO_TAX_SERVICE);
        dtoTaxConfigService = (DtoTaxConfigService) ctx().getBean(ServiceSpringKeys.DTO_TAX_CONFIG_SERVICE);
        dtoFactory = (DtoFactory) ctx().getBean(ServiceSpringKeys.DTO_FACTORY);
        super.setUp();
    }



    @Test
    public void testFindByParameters() throws Exception {
        TaxDTO taxDTO = getDto();
        taxDTO = dtoTaxService.create(taxDTO);

        TaxConfigDTO taxConfigDTO1 = getDto(taxDTO, "CC_TEST1");
        taxConfigDTO1 = dtoTaxConfigService.create(taxConfigDTO1);

        TaxConfigDTO taxConfigDTO2 = getDto(taxDTO, "CC_TEST2");
        taxConfigDTO2 = dtoTaxConfigService.create(taxConfigDTO2);

        // retrieve specific
        List<TaxConfigDTO> taxCfgs = dtoTaxConfigService.findByTaxId(
                taxDTO.getTaxId(),
                null,
                null,
                "CC_TEST1");

        assertNotNull(taxCfgs);
        assertEquals(1, taxCfgs.size());

        // retrieve all
        taxCfgs = dtoTaxConfigService.findByTaxId(
                taxDTO.getTaxId(),
                null,
                null,
                null);

        assertNotNull(taxCfgs);
        assertEquals(2, taxCfgs.size());

        // retrieve non-existent
        taxCfgs = dtoTaxConfigService.findByTaxId(
                taxDTO.getTaxId(),
                "zzzz",
                null,
                null);

        assertNotNull(taxCfgs);
        assertEquals(0, taxCfgs.size());

        dtoTaxConfigService.remove(taxConfigDTO1.getTaxConfigId());
        dtoTaxConfigService.remove(taxConfigDTO2.getTaxConfigId());
        dtoTaxService.remove(taxDTO.getTaxId());
    }


    @Test
    public void testCreate() throws Exception {

        TaxDTO taxDTO = dtoTaxService.getById(1010L);

        TaxConfigDTO taxConfigDTO1 = getDto(taxDTO, "CC_TEST1");
        taxConfigDTO1 = dtoTaxConfigService.create(taxConfigDTO1);

        assertTrue(taxDTO.getTaxId() > 0);
        dtoTaxConfigService.remove(taxConfigDTO1.getTaxConfigId());
    }

    @Test
    public void testUpdate() throws Exception {

        TaxDTO taxDTO = dtoTaxService.getById(1010L);

        TaxConfigDTO taxConfigDTO1 = getDto(taxDTO, "CC_TEST1");
        taxConfigDTO1 = dtoTaxConfigService.create(taxConfigDTO1);

        assertTrue(taxConfigDTO1.getTaxConfigId() > 0);
        taxConfigDTO1.setCountryCode("UA");
        taxConfigDTO1 = dtoTaxConfigService.update(taxConfigDTO1);
        assertEquals("UA", taxConfigDTO1.getCountryCode());
        dtoTaxConfigService.remove(taxConfigDTO1.getTaxConfigId());

    }

    @Test
    public void testRemove() throws Exception {

        TaxDTO taxDTO = dtoTaxService.getById(1010L);

        TaxConfigDTO taxConfigDTO1 = getDto(taxDTO, "CC_TEST1");
        taxConfigDTO1 = dtoTaxConfigService.create(taxConfigDTO1);

        assertTrue(taxConfigDTO1.getTaxConfigId() > 0);
        long id = taxConfigDTO1.getTaxConfigId();
        dtoTaxConfigService.remove(id);
        taxConfigDTO1 = dtoTaxConfigService.getById(id);
        assertNull(taxConfigDTO1);
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

    private TaxConfigDTO getDto(TaxDTO taxDTO, String sku) {

        TaxConfigDTO taxCfgDTO = dtoFactory.getByIface(TaxConfigDTO.class);
        taxCfgDTO.setTaxId(taxDTO.getTaxId());
        taxCfgDTO.setProductCode(sku);

        return taxCfgDTO;

    }

}
