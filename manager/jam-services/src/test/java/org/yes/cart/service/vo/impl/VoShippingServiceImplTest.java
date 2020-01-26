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
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.vo.VoShippingService;
import org.yes.cart.utils.DateUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 29/09/2019
 * Time: 11:24
 */
public class VoShippingServiceImplTest extends BaseCoreDBTestCase {

    private VoShippingService voShippingService;

    @Before
    public void setUp() {
        voShippingService = (VoShippingService) ctx().getBean("voCarrierService");
        super.setUp();
    }

    @Test
    public void testGetCarriers() throws Exception {

        VoSearchContext ctxFind = new VoSearchContext();
        ctxFind.setParameters(Collections.singletonMap("filter", Collections.singletonList("carrier")));
        ctxFind.setSize(10);

        final List<VoCarrierInfo> carriers = voShippingService.getFilteredCarriers(ctxFind).getItems();
        assertNotNull(carriers);
        assertFalse(carriers.isEmpty());

        final VoCarrierInfo carrier = carriers.get(0);

        final List<VoShopCarrier> shopCarriers = voShippingService.getShopCarriers(carrier.getCarrierShops().get(0).getShopId());
        assertNotNull(shopCarriers);
        assertFalse(shopCarriers.isEmpty());
        assertTrue(shopCarriers.stream().anyMatch(cr -> cr.getCarrierId() == carrier.getCarrierId()));

        final List<VoShopCarrierAndSla> carriersAndSla = voShippingService.getShopCarriersAndSla(carrier.getCarrierShops().get(0).getShopId());
        assertNotNull(carriersAndSla);
        assertFalse(carriersAndSla.isEmpty());
        assertTrue(carriersAndSla.stream().anyMatch(cr -> cr.getCarrierId() == carrier.getCarrierId()));

    }

    @Test
    public void testCarrierCRUD() throws Exception {

        final VoCarrier carrier = new VoCarrier();
        carrier.setName("TEST CRUD");
        carrier.setDisplayNames(Collections.singletonList(MutablePair.of("en", "Test")));

        final VoCarrier created = voShippingService.createCarrier(carrier);
        assertTrue(created.getCarrierId() > 0L);

        final VoCarrier afterCreated = voShippingService.getCarrierById(created.getCarrierId());
        assertNotNull(afterCreated);
        assertEquals("TEST CRUD", afterCreated.getName());
        assertEquals("Test", afterCreated.getDisplayNames().get(0).getSecond());

        afterCreated.setDescription("Test");
        final VoCarrierShopLink shop = new VoCarrierShopLink();
        shop.setCarrierId(afterCreated.getCarrierId());
        shop.setShopId(10L);
        afterCreated.setCarrierShops(Collections.singletonList(shop));

        final VoCarrier updated = voShippingService.updateCarrier(afterCreated);
        assertEquals("Test", updated.getDescription());
        assertEquals(10L, updated.getCarrierShops().get(0).getShopId());

        VoSearchContext ctxFind = new VoSearchContext();
        ctxFind.setParameters(Collections.singletonMap("filter", Collections.singletonList(carrier.getCode())));
        ctxFind.setSize(10);

        assertTrue(voShippingService.getFilteredCarriers(ctxFind).getItems().stream().anyMatch(ca -> ca.getCarrierId() == created.getCarrierId()));

        voShippingService.removeCarrier(updated.getCarrierId());

        assertFalse(voShippingService.getFilteredCarriers(ctxFind).getItems().stream().anyMatch(ca -> ca.getCarrierId() == created.getCarrierId()));

    }


    @Test
    public void testCarrierSlaCRUD() throws Exception {

        VoSearchContext ctxFind = new VoSearchContext();
        ctxFind.setSize(10);

        final VoCarrierInfo carrier = voShippingService.getFilteredCarriers(ctxFind).getItems().get(0);

        final VoCarrierSla carrierSla = new VoCarrierSla();
        carrierSla.setCarrierId(carrier.getCarrierId());
        carrierSla.setSlaType(CarrierSla.FREE);
        carrierSla.setName("TEST CRUD");
        carrierSla.setDisplayNames(Collections.singletonList(MutablePair.of("en", "Test")));
        carrierSla.setExcludeWeekDays(Arrays.asList("1", "2"));
        carrierSla.setExcludeCustomerTypes("B2B,B2C");
        carrierSla.setExcludeDates(Collections.singletonList(
                MutablePair.of(DateUtils.ldParseSDT("2009-01-01"), DateUtils.ldParseSDT("2009-01-05"))
        ));
        carrierSla.setSupportedPaymentGateways(Arrays.asList("testPG1", "testPG2"));
        carrierSla.setSupportedFulfilmentCentres(Arrays.asList("WAREHOUSE_1", "WAREHOUSE_2"));


        final VoCarrierSla created = voShippingService.createCarrierSla(carrierSla);
        assertTrue(created.getCarrierId() > 0L);
        assertEquals("1", created.getExcludeWeekDays().get(0));
        assertEquals("B2B,B2C", created.getExcludeCustomerTypes());
        assertEquals(DateUtils.ldParseSDT("2009-01-01"), created.getExcludeDates().get(0).getFirst());

        final VoCarrierSla afterCreated = voShippingService.getCarrierSlaById(created.getCarrierslaId());
        assertNotNull(afterCreated);
        assertEquals("TEST CRUD", afterCreated.getName());
        assertEquals("Test", afterCreated.getDisplayNames().get(0).getSecond());

        afterCreated.setDescription("Test");

        final VoCarrierSla updated = voShippingService.updateCarrierSla(afterCreated);
        assertEquals("Test", updated.getDescription());

        assertTrue(voShippingService.getCarrierSlas(carrier.getCarrierId()).stream().anyMatch(ca -> ca.getCarrierslaId() == created.getCarrierslaId()));

        voShippingService.removeCarrierSla(updated.getCarrierslaId());

        assertFalse(voShippingService.getCarrierSlas(carrier.getCarrierId()).stream().anyMatch(ca -> ca.getCarrierslaId() == created.getCarrierslaId()));

    }

}