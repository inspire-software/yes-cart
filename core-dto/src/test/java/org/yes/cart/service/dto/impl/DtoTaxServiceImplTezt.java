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
import org.yes.cart.domain.dto.TaxDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.service.dto.DtoTaxService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 31/10/2014
 * Time: 13:10
 */
public class DtoTaxServiceImplTezt extends BaseCoreDBTestCase {

    private DtoTaxService dtoTaxService;
    private DtoFactory dtoFactory;

    @Override
    @Before
    public void setUp() {
        dtoTaxService = (DtoTaxService) ctx().getBean(DtoServiceSpringKeys.DTO_TAX_SERVICE);
        dtoFactory = (DtoFactory) ctx().getBean(DtoServiceSpringKeys.DTO_FACTORY);
        super.setUp();
    }

    @Test
    public void testFindTaxes() throws Exception {
        TaxDTO t1 = getDto();
        final String shopCode = t1.getShopCode();
        final String currency = t1.getCurrency();
        t1 = dtoTaxService.create(t1);
        TaxDTO t2 = getDto();
        t2.setExclusiveOfPrice(false);
        t2 = dtoTaxService.create(t2);
        TaxDTO t3 = getDto();
        t3.setTaxRate(new BigDecimal("10.00"));
        t3 = dtoTaxService.create(t3);

        // retrieve specific
        final SearchContext filterByCode = createSearchContext("code", false, 0, 10,
                "filter", t1.getCode(),
                "shopCode", shopCode,
                "currency", currency
        );
        List<TaxDTO> taxes = dtoTaxService.findTaxes(filterByCode).getItems();

        assertNotNull(taxes);
        assertEquals(1, taxes.size());
        assertEquals(t1.getCode(), taxes.get(0).getCode());

        // retrieve all EUR
        final SearchContext filterAllEUR = createSearchContext("code", false, 0, 20,
                "shopCode", "SHOIP1",
                "currency", "EUR"
        );
        taxes = dtoTaxService.findTaxes(filterAllEUR).getItems();

        assertNotNull(taxes);
        assertEquals(10, taxes.size());
        assertTrue(taxes.stream().allMatch(tax -> "SHOIP1".equals(tax.getShopCode()) && "EUR".equals(tax.getCurrency())));


        // retrieve exclusive EUR
        final SearchContext filterInclEUR = createSearchContext("code", false, 0, 20,
                "filter", "--",
                "shopCode", "SHOIP1",
                "currency", "EUR"
        );
        taxes = dtoTaxService.findTaxes(filterInclEUR).getItems();

        assertNotNull(taxes);
        assertEquals(7, taxes.size());
        assertTrue(taxes.stream().allMatch(tax -> "SHOIP1".equals(tax.getShopCode()) && "EUR".equals(tax.getCurrency()) && !tax.getExclusiveOfPrice()));

        // retrieve exclusive EUR
        final SearchContext filterExclEUR = createSearchContext("code", false, 0, 20,
                "filter", "++",
                "shopCode", "SHOIP1",
                "currency", "EUR"
        );
        taxes = dtoTaxService.findTaxes(filterExclEUR).getItems();

        assertNotNull(taxes);
        assertEquals(3, taxes.size());
        assertTrue(taxes.stream().allMatch(tax -> "SHOIP1".equals(tax.getShopCode()) && "EUR".equals(tax.getCurrency()) && tax.getExclusiveOfPrice()));


        // retrieve exclusive EUR 10%
        final SearchContext filterInclEUR10 = createSearchContext("code", false, 0, 20,
                "filter", "-%10",
                "shopCode", "SHOIP1",
                "currency", "EUR"
        );
        taxes = dtoTaxService.findTaxes(filterInclEUR10).getItems();

        assertNotNull(taxes);
        assertEquals(2, taxes.size());
        assertTrue(taxes.stream().allMatch(tax -> "SHOIP1".equals(tax.getShopCode()) && "EUR".equals(tax.getCurrency())
                && !tax.getExclusiveOfPrice() && BigDecimal.TEN.compareTo(tax.getTaxRate()) == 0));


        // retrieve EUR 5%
        final SearchContext filterEUR5 = createSearchContext("code", false, 0, 20,
                "filter", "%5",
                "shopCode", "SHOIP1",
                "currency", "EUR"
        );
        taxes = dtoTaxService.findTaxes(filterEUR5).getItems();

        assertNotNull(taxes);
        assertEquals(4, taxes.size());
        assertTrue(taxes.stream().allMatch(tax -> "SHOIP1".equals(tax.getShopCode()) && "EUR".equals(tax.getCurrency())
                && new BigDecimal(5).compareTo(tax.getTaxRate()) == 0));

        // invalid shop
        final SearchContext filterInvalid = createSearchContext("code", false, 0, 20,
                "shopCode", "ZZZZ",
                "currency", "EUR"
        );
        taxes = dtoTaxService.findTaxes(filterInvalid).getItems();

        assertNotNull(taxes);
        assertTrue(taxes.isEmpty());


        dtoTaxService.remove(t1.getTaxId());
        dtoTaxService.remove(t2.getTaxId());
        dtoTaxService.remove(t3.getTaxId());
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
        taxDTO.setShopCode("SHOIP1");
        taxDTO.setCurrency("EUR");
        taxDTO.setCode(UUID.randomUUID().toString());
        taxDTO.setTaxRate(new BigDecimal("20.00"));
        taxDTO.setExclusiveOfPrice(true);
        taxDTO.setDescription("Test description");

        return taxDTO;
    }
}
