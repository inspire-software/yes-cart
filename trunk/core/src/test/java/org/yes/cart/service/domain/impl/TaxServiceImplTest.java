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

package org.yes.cart.service.domain.impl;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.entity.Tax;
import org.yes.cart.service.domain.TaxService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 27/10/2014
 * Time: 19:39
 */
public class TaxServiceImplTest extends BaseCoreDBTestCase {

    @Test
    public void testCrud() throws Exception {

        int start, all;

        final TaxService taxService = ctx().getBean("taxService", TaxService.class);

        final List<Tax> startTaxes = taxService.getTaxesByShopCode("SHOP10", "EUR");
        start = startTaxes.size();

        final Tax vatHi = taxService.getGenericDao().getEntityFactory().getByIface(Tax.class);
        vatHi.setCode("VAT21");
        vatHi.setTaxRate(new BigDecimal("21.00"));
        vatHi.setShopCode("SHOP10");
        vatHi.setCurrency("EUR");
        vatHi.setDescription("High Rate VAT");

        taxService.create(vatHi);

        final Tax vatLo = taxService.getGenericDao().getEntityFactory().getByIface(Tax.class);
        vatLo.setCode("VAT6");
        vatLo.setTaxRate(new BigDecimal("6.00"));
        vatLo.setShopCode("SHOP10");
        vatLo.setCurrency("EUR");
        vatLo.setDescription("Low Rate VAT");

        taxService.create(vatLo);

        final List<Tax> allTaxes = taxService.getTaxesByShopCode("SHOP10", "EUR");
        all = allTaxes.size();

        assertTrue(all == start + 2);

        vatLo.setShopCode("SHOP20");

        taxService.update(vatLo);

        final List<Tax> afterUpdateTaxes = taxService.getTaxesByShopCode("SHOP10", "EUR");
        all = afterUpdateTaxes.size();

        assertTrue(all == start + 1);

        taxService.delete(vatHi);
        taxService.delete(vatLo);


        final List<Tax> afterDeleteTaxes = taxService.getTaxesByShopCode("SHOP10", "EUR");
        all = afterDeleteTaxes.size();

        assertTrue(all == start);

    }

    @Test
    public void testGetTaxesByShopCode() throws Exception {

        final TaxService taxService = ctx().getBean("taxService", TaxService.class);

        final List<Long> expectedEur = Arrays.asList(1010L, 1011L, 1012L, 1013L, 1014L, 1015L);
        final List<Tax> eurTaxes = taxService.getTaxesByShopCode("SHOIP1", "EUR");
        assertFalse(eurTaxes.isEmpty());
        assertEquals(6, eurTaxes.size());
        for (final Tax tax : eurTaxes) {
            assertTrue(expectedEur.contains(tax.getTaxId()));
        }

        final List<Long> expectedUsd = Arrays.asList(1020L);
        final List<Tax> usdTaxes = taxService.getTaxesByShopCode("SHOIP1", "USD");
        assertFalse(usdTaxes.isEmpty());
        assertEquals(1, usdTaxes.size());
        for (final Tax tax : usdTaxes) {
            assertTrue(expectedUsd.contains(tax.getTaxId()));
        }

        final List<Long> expectedUah = Arrays.asList(1030L);
        final List<Tax> uahTaxes = taxService.getTaxesByShopCode("SHOIP1", "UAH");
        assertFalse(uahTaxes.isEmpty());
        assertEquals(1, uahTaxes.size());
        for (final Tax tax : uahTaxes) {
            assertTrue(expectedUah.contains(tax.getTaxId()));
        }


    }
}
