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

package org.yes.cart.service.domain.impl;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.entity.Tax;
import org.yes.cart.domain.entity.TaxConfig;
import org.yes.cart.service.domain.TaxConfigService;
import org.yes.cart.service.domain.TaxService;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

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
        vat.setCode("VAT20");
        vat.setTaxRate(new BigDecimal("20.00"));
        vat.setShopCode("SHOP10");
        vat.setCurrency("EUR");
        vat.setDescription("Basic VAT");

        taxService.create(vat);

        final TaxConfig cfgShopLevel = taxConfigService.getGenericDao().getEntityFactory().getByIface(TaxConfig.class);
        cfgShopLevel.setTax(vat);

        taxConfigService.create(cfgShopLevel);

        final Map<String, List> filterByTax = Collections.singletonMap("taxIds", Collections.singletonList(vat.getTaxId()));
        final List<TaxConfig> cfgShopLevelRes = taxConfigService.findTaxConfigs(0, 10, null, false, filterByTax);
        assertEquals(1, cfgShopLevelRes.size());
        assertEquals(cfgShopLevel.getTaxConfigId(), cfgShopLevelRes.get(0).getTaxConfigId());

        final TaxConfig cfgCountryLevel = taxConfigService.getGenericDao().getEntityFactory().getByIface(TaxConfig.class);
        cfgCountryLevel.setTax(vat);
        cfgCountryLevel.setCountryCode("GB");

        taxConfigService.create(cfgCountryLevel);

        final Map<String, List> filterByTaxAndCountry = new HashMap<>();
        filterByTaxAndCountry.put("taxIds", Collections.singletonList(vat.getTaxId()));
        filterByTaxAndCountry.put("countryCode", Collections.singletonList("GB"));
        final List<TaxConfig> cfgCountryLevelRes = taxConfigService.findTaxConfigs(0, 10, null, false, filterByTaxAndCountry);
        assertEquals(1, cfgCountryLevelRes.size());
        assertEquals(cfgCountryLevel.getTaxConfigId(), cfgCountryLevelRes.get(0).getTaxConfigId());

        final TaxConfig cfgStateLevel = taxConfigService.getGenericDao().getEntityFactory().getByIface(TaxConfig.class);
        cfgStateLevel.setTax(vat);
        cfgStateLevel.setCountryCode("GB");
        cfgStateLevel.setStateCode("GB-CAM");

        taxConfigService.create(cfgStateLevel);

        final Map<String, List> filterByTaxAndState = new HashMap<>();
        filterByTaxAndState.put("taxIds", Collections.singletonList(vat.getTaxId()));
        filterByTaxAndState.put("countryCode", Collections.singletonList("GB"));
        filterByTaxAndState.put("stateCode", Collections.singletonList("GB-CAM"));
        final List<TaxConfig> cfgStateLevelRes = taxConfigService.findTaxConfigs(0, 10, null, false, filterByTaxAndState);
        assertEquals(1, cfgStateLevelRes.size());
        assertEquals(cfgStateLevel.getTaxConfigId(), cfgStateLevelRes.get(0).getTaxConfigId());

        final TaxConfig cfgProductLevel = taxConfigService.getGenericDao().getEntityFactory().getByIface(TaxConfig.class);
        cfgProductLevel.setTax(vat);
        cfgProductLevel.setProductCode("ABC-001");

        taxConfigService.create(cfgProductLevel);

        final Map<String, List> filterByTaxAndSKU = new HashMap<>();
        filterByTaxAndSKU.put("taxIds", Collections.singletonList(vat.getTaxId()));
        filterByTaxAndSKU.put("productCode", Collections.singletonList("ABC-001"));
        final List<TaxConfig> cfgProductLevelRes = taxConfigService.findTaxConfigs(0, 10, null, false, filterByTaxAndSKU);
        assertEquals(1, cfgProductLevelRes.size());
        assertEquals(cfgProductLevel.getTaxConfigId(), cfgProductLevelRes.get(0).getTaxConfigId());


        final List<TaxConfig> cfgAllLevelRes = taxConfigService.findTaxConfigs(0, 10, null, false, filterByTax);
        assertEquals(4, cfgAllLevelRes.size());

        for (final TaxConfig tc : cfgAllLevelRes) {
            taxConfigService.delete(tc);
        }

        final List<TaxConfig> cfgAllLevelResAfterDelete = taxConfigService.findTaxConfigs(0, 10, null, false, filterByTax);
        assertTrue(cfgAllLevelResAfterDelete.isEmpty());

    }

    @Test
    public void testComparatorConsistent() throws Exception {

        final TaxConfigService taxConfigService = ctx().getBean("taxConfigService", TaxConfigService.class);

        final Comparator<TaxConfig> comparator = TaxConfigServiceImpl.PRIORITY;

        final TaxConfig shop10_us_usus_cctest1 = taxConfigService.findById(1014L);
        final TaxConfig shop10 = taxConfigService.findById(1010L);
        final TaxConfig shop10_ua = taxConfigService.findById(1011L);
        final TaxConfig shop10_us_usus = taxConfigService.findById(1012L);
        final TaxConfig shop10_ua_cctest1 = taxConfigService.findById(1013L);
        final TaxConfig shop10_cctest1 = taxConfigService.findById(1015L);

        assertEquals(-1, comparator.compare(shop10_us_usus_cctest1, shop10));
        assertEquals(1, comparator.compare(shop10, shop10_us_usus_cctest1));
        assertEquals(-1, comparator.compare(shop10_us_usus_cctest1, shop10_us_usus));
        assertEquals(1, comparator.compare(shop10_us_usus, shop10_us_usus_cctest1));
        assertEquals(-1, comparator.compare(shop10_us_usus_cctest1, shop10_cctest1));
        assertEquals(1, comparator.compare(shop10_cctest1, shop10_us_usus_cctest1));
        assertEquals(-1, comparator.compare(shop10_us_usus, shop10));
        assertEquals(1, comparator.compare(shop10, shop10_us_usus));
        assertEquals(1, comparator.compare(shop10_us_usus, shop10_cctest1));
        assertEquals(-1, comparator.compare(shop10_cctest1, shop10_us_usus));

        assertEquals(-1, comparator.compare(shop10_ua, shop10));
        assertEquals(1, comparator.compare(shop10, shop10_ua));
        assertEquals(1, comparator.compare(shop10_ua, shop10_ua_cctest1));
        assertEquals(-1, comparator.compare(shop10_ua_cctest1, shop10_ua));
        assertEquals(1, comparator.compare(shop10_ua, shop10_cctest1));
        assertEquals(-1, comparator.compare(shop10_cctest1, shop10_ua));
        assertEquals(-1, comparator.compare(shop10_ua_cctest1, shop10));
        assertEquals(1, comparator.compare(shop10, shop10_ua_cctest1));
        assertEquals(-1, comparator.compare(shop10_ua_cctest1, shop10_cctest1));
        assertEquals(1, comparator.compare(shop10_cctest1, shop10_ua_cctest1));


        final List<TaxConfig> listUs = new ArrayList<>(Arrays.asList(
                shop10_us_usus_cctest1,
                shop10,
                shop10_us_usus,
                shop10_cctest1
        ));

        listUs.sort(comparator);

        assertEquals(shop10_us_usus_cctest1, listUs.get(0));
        assertEquals(shop10_cctest1, listUs.get(1));
        assertEquals(shop10_us_usus, listUs.get(2));
        assertEquals(shop10, listUs.get(3));


        final List<TaxConfig> listUa = new ArrayList<>(Arrays.asList(
                shop10,
                shop10_ua,
                shop10_ua_cctest1,
                shop10_cctest1
        ));


        listUa.sort(comparator);

        assertEquals(shop10_ua_cctest1, listUa.get(0));
        assertEquals(shop10_cctest1, listUa.get(1));
        assertEquals(shop10_ua, listUa.get(2));
        assertEquals(shop10, listUa.get(3));


    }

    @Test
    public void testGetTaxIdBy() throws Exception {

        final TaxService taxService = ctx().getBean("taxService", TaxService.class);
        final TaxConfigService taxConfigService = ctx().getBean("taxConfigService", TaxConfigService.class);

        final List<Tax> startTaxes = taxService.getTaxesByShopCode("SHOIP1", "EUR");
        assertFalse(startTaxes.isEmpty());
        assertEquals(7, startTaxes.size());

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
