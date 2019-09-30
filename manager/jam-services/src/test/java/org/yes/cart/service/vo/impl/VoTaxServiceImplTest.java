/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.service.vo.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.vo.VoTax;
import org.yes.cart.domain.vo.VoTaxConfig;
import org.yes.cart.service.vo.VoTaxService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 29/09/2019
 * Time: 12:41
 */
public class VoTaxServiceImplTest extends BaseCoreDBTestCase {

    private VoTaxService voTaxService;

    @Before
    public void setUp() {
        voTaxService = (VoTaxService) ctx().getBean("voTaxService");
        super.setUp();
    }

    @Test
    public void testGetTaxes() throws Exception {

        final List<VoTax> taxesNoFilter = voTaxService.getFilteredTax("SHOIP1", "EUR", null, 10);
        assertNotNull(taxesNoFilter);
        assertFalse(taxesNoFilter.isEmpty());

        final List<VoTax> taxesFind = voTaxService.getFilteredTax("SHOIP1", "EUR", "VAT (CC_TEST1)", 10);
        assertNotNull(taxesFind);
        assertFalse(taxesFind.isEmpty());
        assertEquals("VAT (CC_TEST1)", taxesFind.get(0).getCode());

        final List<VoTax> taxesExclusive = voTaxService.getFilteredTax("SHOIP1", "EUR", "++", 10);
        assertNotNull(taxesExclusive);
        assertFalse(taxesExclusive.isEmpty());
        assertTrue(taxesExclusive.get(0).isExclusiveOfPrice());

        final List<VoTax> taxesInclusive = voTaxService.getFilteredTax("SHOIP1", "EUR", "--", 10);
        assertNotNull(taxesInclusive);
        assertFalse(taxesInclusive.isEmpty());
        assertFalse(taxesInclusive.get(0).isExclusiveOfPrice());

        final List<VoTax> taxesByRate = voTaxService.getFilteredTax("SHOIP1", "EUR", "%5", 10);
        assertNotNull(taxesByRate);
        assertFalse(taxesByRate.isEmpty());
        assertTrue(new BigDecimal(5).compareTo(taxesByRate.get(0).getTaxRate()) == 0);

    }

    @Test
    public void testTaxCRUD() throws Exception {

        final VoTax tax = new VoTax();
        tax.setCode("TESTCRUD");
        tax.setShopCode("SHOIP2");
        tax.setCurrency("USD");
        tax.setTaxRate(BigDecimal.TEN);

        final VoTax created = voTaxService.createTax(tax);
        assertTrue(created.getTaxId() > 0L);

        final VoTax afterCreated = voTaxService.getTaxById(created.getTaxId());
        assertNotNull(afterCreated);
        assertEquals("TESTCRUD", afterCreated.getCode());

        afterCreated.setDescription("Test");

        final VoTax updated = voTaxService.updateTax(afterCreated);
        assertEquals("Test", updated.getDescription());


        assertFalse(voTaxService.getFilteredTax("SHOIP2", "USD", "TESTCRUD", 10).isEmpty());

        voTaxService.removeTax(updated.getTaxId());

        assertTrue(voTaxService.getFilteredTax("SHOIP2", "USD", "TESTCRUD", 10).isEmpty());

    }


    @Test
    public void testTaxConfigCRD() throws Exception {

        final VoTax tax = voTaxService.getFilteredTax("SHOIP1", "EUR", null, 10).get(0);

        final VoTaxConfig taxConfig = new VoTaxConfig();
        taxConfig.setTaxId(tax.getTaxId());

        final VoTaxConfig created = voTaxService.createTaxConfig(taxConfig);
        assertTrue(created.getTaxConfigId() > 0L);

        final VoTaxConfig afterCreated = voTaxService.getTaxConfigById(created.getTaxConfigId());
        assertNotNull(afterCreated);

        assertTrue(voTaxService.getFilteredTaxConfig(tax.getTaxId(), null, 10).stream().anyMatch(tc -> tc.getTaxConfigId() == created.getTaxConfigId()));

        voTaxService.removeTaxConfig(created.getTaxConfigId());

        assertFalse(voTaxService.getFilteredTaxConfig(tax.getTaxId(), null, 10).stream().anyMatch(tc -> tc.getTaxConfigId() == created.getTaxConfigId()));

    }
}