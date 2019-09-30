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
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoFulfilmentCentre;
import org.yes.cart.domain.vo.VoInventory;
import org.yes.cart.domain.vo.VoShopFulfilmentCentre;
import org.yes.cart.service.vo.VoFulfilmentService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 24/09/2019
 * Time: 16:04
 */
public class VoFulfilmentServiceImplTest extends BaseCoreDBTestCase {

    private VoFulfilmentService voFulfilmentService;

    @Before
    public void setUp() {
        voFulfilmentService = (VoFulfilmentService) ctx().getBean("voFulfilmentService");
        super.setUp();
    }

    @Test
    public void testGetCentres() throws Exception {

        final List<VoFulfilmentCentre> fcs = voFulfilmentService.getAllFulfilmentCentres();
        assertNotNull(fcs);
        assertFalse(fcs.isEmpty());

        final List<VoShopFulfilmentCentre> fcs10 = voFulfilmentService.getShopFulfilmentCentres(10L);
        assertNotNull(fcs10);
        assertFalse(fcs10.isEmpty());

        final List<VoShopFulfilmentCentre> fcsX = voFulfilmentService.getShopFulfilmentCentres(88888888L);
        assertNotNull(fcsX);
        assertTrue(fcsX.isEmpty());

    }

    @Test
    public void testCentreCRUD() throws Exception {

        final VoFulfilmentCentre fulfilmentCentre = new VoFulfilmentCentre();
        fulfilmentCentre.setCode("TESTCRUD");
        fulfilmentCentre.setName("TEST CRUD");
        fulfilmentCentre.setDisplayNames(Collections.singletonList(MutablePair.of("en", "Test")));

        final VoFulfilmentCentre created = voFulfilmentService.createFulfilmentCentre(fulfilmentCentre);
        assertTrue(created.getWarehouseId() > 0L);

        VoFulfilmentCentre afterCreated = voFulfilmentService.getFulfilmentCentreById(created.getWarehouseId());
        assertNotNull(afterCreated);
        assertEquals("TEST CRUD", afterCreated.getName());

        created.setName("TEST CRUD UPDATE");

        final VoFulfilmentCentre updated = voFulfilmentService.updateFulfilmentCentre(created);
        assertEquals("TEST CRUD UPDATE", updated.getName());

        assertTrue(voFulfilmentService.getAllFulfilmentCentres().stream().anyMatch(fc -> fc.getWarehouseId() == updated.getWarehouseId()));

        voFulfilmentService.removeFulfilmentCentre(updated.getWarehouseId());

        assertFalse(voFulfilmentService.getAllFulfilmentCentres().stream().anyMatch(fc -> fc.getWarehouseId() == updated.getWarehouseId()));

    }

    @Test
    public void testGetInventory() throws Exception {

        final List<VoInventory> invNone = voFulfilmentService.getFilteredInventory(3L, "XXXXX", 10);
        assertNotNull(invNone);
        assertTrue(invNone.isEmpty());

        final List<VoInventory> invByName = voFulfilmentService.getFilteredInventory(2L, "Ben", 10);
        assertNotNull(invByName);
        assertFalse(invByName.isEmpty());
        assertTrue(invByName.get(0).getSkuCode().contains("BENDER"));

        final List<VoInventory> invByCode = voFulfilmentService.getFilteredInventory(2L, "!BENDER-ua", 10);
        assertNotNull(invByCode);
        assertFalse(invByCode.isEmpty());
        assertEquals("BENDER-ua", invByCode.get(0).getSkuCode());

        final List<VoInventory> invReserved = voFulfilmentService.getFilteredInventory(2L, "+1", 10);
        assertNotNull(invReserved);
        assertFalse(invReserved.isEmpty());
        assertEquals("PRODUCT6", invReserved.get(0).getSkuCode());
        assertTrue(BigDecimal.ZERO.compareTo(invReserved.get(0).getReserved()) < 0);

        final List<VoInventory> invAvailable = voFulfilmentService.getFilteredInventory(2L, "<2010-01-01", 10);
        assertNotNull(invAvailable);
        assertFalse(invAvailable.isEmpty());

    }

    @Test
    public void testInventoryCRUD() throws Exception {

        assertTrue(voFulfilmentService.getFilteredInventory(3L, "TESTCRUD", 10).isEmpty());

        final VoInventory inventory = new VoInventory();
        inventory.setSkuCode("TESTCRUD");
        inventory.setWarehouseCode("WAREHOUSE_3");
        inventory.setQuantity(BigDecimal.TEN);
        inventory.setAvailability(SkuWarehouse.AVAILABILITY_BACKORDER);
        inventory.setStepOrderQuantity(BigDecimal.TEN);

        final VoInventory created = voFulfilmentService.createInventory(inventory);
        assertTrue(created.getSkuWarehouseId() > 0L);

        assertFalse(voFulfilmentService.getFilteredInventory(3L, "TESTCRUD", 10).isEmpty());

        VoInventory afterCreated = voFulfilmentService.getInventoryById(created.getSkuWarehouseId());
        assertNotNull(afterCreated);
        assertTrue(new BigDecimal("10").compareTo(afterCreated.getQuantity()) == 0);

        created.setQuantity(new BigDecimal("1000"));

        final VoInventory updated = voFulfilmentService.updateInventory(created);
        assertTrue(new BigDecimal("1000").compareTo(updated.getQuantity()) == 0);

        assertFalse(voFulfilmentService.getFilteredInventory(3L, "TESTCRUD", 10).isEmpty());

        voFulfilmentService.removeInventory(updated.getSkuWarehouseId());

        VoInventory afterRemoved = voFulfilmentService.getInventoryById(created.getSkuWarehouseId());
        assertNotNull(afterRemoved);
        assertTrue(BigDecimal.ZERO.compareTo(afterRemoved.getQuantity()) == 0);

    }
}