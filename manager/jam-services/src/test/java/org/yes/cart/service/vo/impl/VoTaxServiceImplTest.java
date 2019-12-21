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
import org.yes.cart.domain.vo.VoSearchContext;
import org.yes.cart.domain.vo.VoTax;
import org.yes.cart.domain.vo.VoTaxConfig;
import org.yes.cart.service.vo.VoTaxService;

import java.math.BigDecimal;
import java.util.Collections;
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

        VoSearchContext ctxAny = new VoSearchContext();
        ctxAny.setParameters(Collections.emptyMap());
        ctxAny.setSize(10);
        final List<VoTax> taxesNoFilter = voTaxService.getFilteredTax("SHOIP1", "EUR", ctxAny).getItems();
        assertNotNull(taxesNoFilter);
        assertFalse(taxesNoFilter.isEmpty());

        VoSearchContext ctxFind = new VoSearchContext();
        ctxFind.setParameters(Collections.singletonMap("filter", Collections.singletonList("VAT (CC_TEST1)")));
        ctxFind.setSize(10);
        final List<VoTax> taxesFind = voTaxService.getFilteredTax("SHOIP1", "EUR", ctxFind).getItems();
        assertNotNull(taxesFind);
        assertFalse(taxesFind.isEmpty());
        assertEquals("VAT (CC_TEST1)", taxesFind.get(0).getCode());

        VoSearchContext ctxExclusive = new VoSearchContext();
        ctxExclusive.setParameters(Collections.singletonMap("filter", Collections.singletonList("++")));
        ctxExclusive.setSize(10);
        final List<VoTax> taxesExclusive = voTaxService.getFilteredTax("SHOIP1", "EUR", ctxExclusive).getItems();
        assertNotNull(taxesExclusive);
        assertFalse(taxesExclusive.isEmpty());
        assertTrue(taxesExclusive.get(0).isExclusiveOfPrice());

        VoSearchContext ctxInclusive = new VoSearchContext();
        ctxInclusive.setParameters(Collections.singletonMap("filter", Collections.singletonList("--")));
        ctxInclusive.setSize(10);
        final List<VoTax> taxesInclusive = voTaxService.getFilteredTax("SHOIP1", "EUR", ctxInclusive).getItems();
        assertNotNull(taxesInclusive);
        assertFalse(taxesInclusive.isEmpty());
        assertFalse(taxesInclusive.get(0).isExclusiveOfPrice());

        VoSearchContext ctxRate = new VoSearchContext();
        ctxRate.setParameters(Collections.singletonMap("filter", Collections.singletonList("%5")));
        ctxRate.setSize(10);
        final List<VoTax> taxesByRate = voTaxService.getFilteredTax("SHOIP1", "EUR", ctxRate).getItems();
        assertNotNull(taxesByRate);
        assertFalse(taxesByRate.isEmpty());
        assertTrue(taxesByRate.stream().allMatch(tax -> new BigDecimal(5).compareTo(tax.getTaxRate()) == 0));

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

        VoSearchContext ctxExact = new VoSearchContext();
        ctxExact.setParameters(Collections.singletonMap("filter", Collections.singletonList("TESTCRUD")));
        ctxExact.setSize(10);

        assertFalse(voTaxService.getFilteredTax("SHOIP2", "USD", ctxExact).getItems().isEmpty());

        voTaxService.removeTax(updated.getTaxId());

        assertTrue(voTaxService.getFilteredTax("SHOIP2", "USD", ctxExact).getItems().isEmpty());

    }


    @Test
    public void testTaxConfigCRD() throws Exception {

        VoSearchContext ctxAny = new VoSearchContext();
        ctxAny.setParameters(Collections.emptyMap());
        ctxAny.setSize(10);

        final VoTax tax = voTaxService.getFilteredTax("SHOIP1", "EUR", ctxAny).getItems().get(0);

        final VoTaxConfig taxConfig = new VoTaxConfig();
        taxConfig.setTaxId(tax.getTaxId());

        final VoTaxConfig created = voTaxService.createTaxConfig(taxConfig);
        assertTrue(created.getTaxConfigId() > 0L);

        final VoTaxConfig afterCreated = voTaxService.getTaxConfigById(created.getTaxConfigId());
        assertNotNull(afterCreated);

        assertTrue(voTaxService.getFilteredTaxConfig(tax.getTaxId(), ctxAny).getItems().stream().anyMatch(tc -> tc.getTaxConfigId() == created.getTaxConfigId()));

        voTaxService.removeTaxConfig(created.getTaxConfigId());

        assertFalse(voTaxService.getFilteredTaxConfig(tax.getTaxId(), ctxAny).getItems().stream().anyMatch(tc -> tc.getTaxConfigId() == created.getTaxConfigId()));

    }
}