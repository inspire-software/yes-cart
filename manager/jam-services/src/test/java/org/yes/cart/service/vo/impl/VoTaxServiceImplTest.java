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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        final Map<String, List> ctxAnyParams = new HashMap<>();
        ctxAnyParams.put("shopCode", Collections.singletonList("SHOIP1"));
        ctxAnyParams.put("currency", Collections.singletonList("EUR"));
        VoSearchContext ctxAny = new VoSearchContext();
        ctxAny.setParameters(ctxAnyParams);
        ctxAny.setSize(10);
        final List<VoTax> taxesNoFilter = voTaxService.getFilteredTax(ctxAny).getItems();
        assertNotNull(taxesNoFilter);
        assertFalse(taxesNoFilter.isEmpty());

        final Map<String, List> ctxFindParams = new HashMap<>();
        ctxFindParams.put("shopCode", Collections.singletonList("SHOIP1"));
        ctxFindParams.put("currency", Collections.singletonList("EUR"));
        ctxFindParams.put("filter", Collections.singletonList("VAT (CC_TEST1)"));
        VoSearchContext ctxFind = new VoSearchContext();
        ctxFind.setParameters(ctxFindParams);
        ctxFind.setSize(10);
        final List<VoTax> taxesFind = voTaxService.getFilteredTax(ctxFind).getItems();
        assertNotNull(taxesFind);
        assertFalse(taxesFind.isEmpty());
        assertEquals("VAT (CC_TEST1)", taxesFind.get(0).getCode());

        final Map<String, List> ctxExclusiveParams = new HashMap<>();
        ctxExclusiveParams.put("shopCode", Collections.singletonList("SHOIP1"));
        ctxExclusiveParams.put("currency", Collections.singletonList("EUR"));
        ctxExclusiveParams.put("filter", Collections.singletonList("++"));
        VoSearchContext ctxExclusive = new VoSearchContext();
        ctxExclusive.setParameters(ctxExclusiveParams);
        ctxExclusive.setSize(10);
        final List<VoTax> taxesExclusive = voTaxService.getFilteredTax(ctxExclusive).getItems();
        assertNotNull(taxesExclusive);
        assertFalse(taxesExclusive.isEmpty());
        assertTrue(taxesExclusive.get(0).isExclusiveOfPrice());

        final Map<String, List> ctxInclusiveParams = new HashMap<>();
        ctxInclusiveParams.put("shopCode", Collections.singletonList("SHOIP1"));
        ctxInclusiveParams.put("currency", Collections.singletonList("EUR"));
        ctxInclusiveParams.put("filter", Collections.singletonList("--"));
        VoSearchContext ctxInclusive = new VoSearchContext();
        ctxInclusive.setParameters(ctxInclusiveParams);
        ctxInclusive.setSize(10);
        final List<VoTax> taxesInclusive = voTaxService.getFilteredTax(ctxInclusive).getItems();
        assertNotNull(taxesInclusive);
        assertFalse(taxesInclusive.isEmpty());
        assertFalse(taxesInclusive.get(0).isExclusiveOfPrice());

        final Map<String, List> ctxRateParams = new HashMap<>();
        ctxRateParams.put("shopCode", Collections.singletonList("SHOIP1"));
        ctxRateParams.put("currency", Collections.singletonList("EUR"));
        ctxRateParams.put("filter", Collections.singletonList("%5"));
        VoSearchContext ctxRate = new VoSearchContext();
        ctxRate.setParameters(ctxRateParams);
        ctxRate.setSize(10);
        final List<VoTax> taxesByRate = voTaxService.getFilteredTax(ctxRate).getItems();
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

        final Map<String, List> ctxParams = new HashMap<>();
        ctxParams.put("shopCode", Collections.singletonList("SHOIP2"));
        ctxParams.put("currency", Collections.singletonList("USD"));
        ctxParams.put("filter", Collections.singletonList("TESTCRUD"));
        VoSearchContext ctxExact = new VoSearchContext();
        ctxExact.setParameters(ctxParams);
        ctxExact.setSize(10);

        assertFalse(voTaxService.getFilteredTax(ctxExact).getItems().isEmpty());

        voTaxService.removeTax(updated.getTaxId());

        assertTrue(voTaxService.getFilteredTax(ctxExact).getItems().isEmpty());

    }


    @Test
    public void testTaxConfigCRD() throws Exception {

        final Map<String, List> ctxTParams = new HashMap<>();
        ctxTParams.put("shopCode", Collections.singletonList("SHOIP1"));
        ctxTParams.put("currency", Collections.singletonList("EUR"));
        VoSearchContext ctxTAny = new VoSearchContext();
        ctxTAny.setParameters(ctxTParams);
        ctxTAny.setSize(10);

        final VoTax tax = voTaxService.getFilteredTax(ctxTAny).getItems().get(0);

        final VoTaxConfig taxConfig = new VoTaxConfig();
        taxConfig.setTax(tax);

        final VoTaxConfig created = voTaxService.createTaxConfig(taxConfig);
        assertTrue(created.getTaxConfigId() > 0L);
        assertNotNull(created.getTax());
        assertEquals(created.getTax().getTaxId(), tax.getTaxId());

        final VoTaxConfig afterCreated = voTaxService.getTaxConfigById(created.getTaxConfigId());
        assertNotNull(afterCreated);

        final Map<String, List> ctxTcParams = new HashMap<>();
        ctxTcParams.put("shopCode", Collections.singletonList("SHOIP1"));
        ctxTcParams.put("currency", Collections.singletonList("EUR"));
        VoSearchContext ctxTcAny = new VoSearchContext();
        ctxTcAny.setParameters(ctxTcParams);
        ctxTcAny.setSize(10);

        assertTrue(voTaxService.getFilteredTaxConfig(ctxTcAny).getItems().stream().allMatch(tc -> tc.getTax() != null));
        assertTrue(voTaxService.getFilteredTaxConfig(ctxTcAny).getItems().stream().anyMatch(tc -> tc.getTaxConfigId() == created.getTaxConfigId()));

        voTaxService.removeTaxConfig(created.getTaxConfigId());

        assertFalse(voTaxService.getFilteredTaxConfig(ctxTcAny).getItems().stream().anyMatch(tc -> tc.getTaxConfigId() == created.getTaxConfigId()));

    }
}