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
import org.yes.cart.domain.entity.TaxConfig;
import org.yes.cart.service.domain.TaxConfigService;
import org.yes.cart.service.domain.TaxService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: denispavlov
 * Date: 27/10/2014
 * Time: 20:13
 */
public class TaxConfigServiceImplTest extends BaseCoreDBTestCase {

    @Test
    public void testCrud() throws Exception {

        final TaxService taxService = ctx().getBean("taxService", TaxService.class);
        final TaxConfigService taxConfigService = ctx().getBean("taxConfigService", TaxConfigService.class);

        final Tax vat = taxService.getGenericDao().getEntityFactory().getByIface(Tax.class);
        vat.setCode("VAT");
        vat.setTaxRate(new BigDecimal("20.00"));
        vat.setShopCode("SHOP10");
        vat.setCurrency("EUR");
        vat.setDescription("Basic VAT");

        taxService.create(vat);

        final TaxConfig cfgShopLevel = taxConfigService.getGenericDao().getEntityFactory().getByIface(TaxConfig.class);
        cfgShopLevel.setTax(vat);

        taxConfigService.create(cfgShopLevel);

        final List<TaxConfig> cfgShopLevelRes = taxConfigService.findByTaxId(vat.getTaxId(), null, null, null);
        assertEquals(1, cfgShopLevelRes.size());
        assertEquals(cfgShopLevel.getTaxConfigId(), cfgShopLevelRes.get(0).getTaxConfigId());

        final TaxConfig cfgCountryLevel = taxConfigService.getGenericDao().getEntityFactory().getByIface(TaxConfig.class);
        cfgCountryLevel.setTax(vat);
        cfgCountryLevel.setCountryCode("GB");

        taxConfigService.create(cfgCountryLevel);

        final List<TaxConfig> cfgCountryLevelRes = taxConfigService.findByTaxId(vat.getTaxId(), "GB", null, null);
        assertEquals(1, cfgCountryLevelRes.size());
        assertEquals(cfgCountryLevel.getTaxConfigId(), cfgCountryLevelRes.get(0).getTaxConfigId());

        final TaxConfig cfgStateLevel = taxConfigService.getGenericDao().getEntityFactory().getByIface(TaxConfig.class);
        cfgStateLevel.setTax(vat);
        cfgStateLevel.setCountryCode("GB");
        cfgStateLevel.setStateCode("GB-CAM");

        taxConfigService.create(cfgStateLevel);

        final List<TaxConfig> cfgStateLevelRes = taxConfigService.findByTaxId(vat.getTaxId(), "GB", "GB-CAM", null);
        assertEquals(1, cfgStateLevelRes.size());
        assertEquals(cfgStateLevel.getTaxConfigId(), cfgStateLevelRes.get(0).getTaxConfigId());

        final TaxConfig cfgProductLevel = taxConfigService.getGenericDao().getEntityFactory().getByIface(TaxConfig.class);
        cfgProductLevel.setTax(vat);
        cfgProductLevel.setProductCode("ABC-001");

        taxConfigService.create(cfgProductLevel);

        final List<TaxConfig> cfgProductLevelRes = taxConfigService.findByTaxId(vat.getTaxId(), null, null, "ABC-001");
        assertEquals(1, cfgProductLevelRes.size());
        assertEquals(cfgProductLevel.getTaxConfigId(), cfgProductLevelRes.get(0).getTaxConfigId());


        final List<TaxConfig> cfgAllLevelRes = taxConfigService.findByTaxId(vat.getTaxId(), null, null, null);
        assertEquals(4, cfgAllLevelRes.size());

        for (final TaxConfig tc : cfgAllLevelRes) {
            taxConfigService.delete(tc);
        }

        final List<TaxConfig> cfgAllLevelResAfterDelete = taxConfigService.findByTaxId(vat.getTaxId(), null, null, null);
        assertTrue(cfgAllLevelResAfterDelete.isEmpty());

    }


    @Test
    public void testGetTaxIdBy() throws Exception {

        final TaxService taxService = ctx().getBean("taxService", TaxService.class);
        final TaxConfigService taxConfigService = ctx().getBean("taxConfigService", TaxConfigService.class);

        final List<Tax> startTaxes = taxService.getTaxesByShopCode("SHOIP1", "EUR");
        assertFalse(startTaxes.isEmpty());
        assertEquals(6, startTaxes.size());

        final Long shopDefaultEURTax = taxConfigService.getTaxIdBy("SHOIP1", "EUR", null, null, null);
        assertEquals(Long.valueOf(1010L), shopDefaultEURTax);

        final Long shopCountryGBTax = taxConfigService.getTaxIdBy("SHOIP1", "EUR", "GB", null, null);
        assertEquals(Long.valueOf(1010L), shopCountryGBTax);

        final Long shopSateGBCAMTax = taxConfigService.getTaxIdBy("SHOIP1", "EUR", "GB", "GB-CAM", null);
        assertEquals(Long.valueOf(1010L), shopSateGBCAMTax);

        final Long shopProductGBCAMTax = taxConfigService.getTaxIdBy("SHOIP1", "EUR", "GB", "GB-CAM", "CC_TEST1");
        assertEquals(Long.valueOf(1015L), shopProductGBCAMTax);

        final Long shopCountryUATax = taxConfigService.getTaxIdBy("SHOIP1", "EUR", "UA", null, null);
        assertEquals(Long.valueOf(1011L), shopCountryUATax);

        final Long shopProductUATax = taxConfigService.getTaxIdBy("SHOIP1", "EUR", "UA", null, "CC_TEST2");
        assertEquals(Long.valueOf(1011L), shopProductUATax);

        final Long shopStateUSUSTax = taxConfigService.getTaxIdBy("SHOIP1", "EUR", "US", "US-US", null);
        assertEquals(Long.valueOf(1012L), shopStateUSUSTax);

        final Long shopStateUSUSTax2 = taxConfigService.getTaxIdBy("SHOIP1", "EUR", "US", "US-US", "CC_TEST2");
        assertEquals(Long.valueOf(1012L), shopStateUSUSTax2);

        final Long shopCountryUAProductTax = taxConfigService.getTaxIdBy("SHOIP1", "EUR", "UA", null, "CC_TEST1");
        assertEquals(Long.valueOf(1013L), shopCountryUAProductTax);

        final Long shopProductUSUSTax = taxConfigService.getTaxIdBy("SHOIP1", "EUR", "US", "US-US", "CC_TEST1");
        assertEquals(Long.valueOf(1014L), shopProductUSUSTax);

        final Long shopProductTax = taxConfigService.getTaxIdBy("SHOIP1", "EUR", null, null, "CC_TEST1");
        assertEquals(Long.valueOf(1015L), shopProductTax);

    }

}
