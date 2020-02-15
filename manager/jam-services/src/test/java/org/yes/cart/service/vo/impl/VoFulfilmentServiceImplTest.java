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
import org.yes.cart.domain.vo.*;
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

        VoSearchContext ctxFind = new VoSearchContext();
        ctxFind.setParameters(createSearchContextParams("filter", "warehouse"));
        ctxFind.setSize(10);

        final List<VoFulfilmentCentreInfo> fcs = voFulfilmentService.getFilteredFulfilmentCentres(ctxFind).getItems();
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

        VoSearchContext ctxFind = new VoSearchContext();
        ctxFind.setParameters(createSearchContextParams("filter", fulfilmentCentre.getCode()));
        ctxFind.setSize(10);

        assertTrue(voFulfilmentService.getFilteredFulfilmentCentres(ctxFind).getItems().stream().anyMatch(fc -> fc.getWarehouseId() == updated.getWarehouseId()));

        voFulfilmentService.removeFulfilmentCentre(updated.getWarehouseId());

        assertFalse(voFulfilmentService.getFilteredFulfilmentCentres(ctxFind).getItems().stream().anyMatch(fc -> fc.getWarehouseId() == updated.getWarehouseId()));

    }

    @Test
    public void testGetInventory() throws Exception {

        VoSearchContext ctxFindNone = new VoSearchContext();
        ctxFindNone.setParameters(createSearchContextParams(
                "filter", "XXXXX",
                "centreId", 3L
        ));
        ctxFindNone.setSize(10);

        final List<VoInventory> invNone = voFulfilmentService.getFilteredInventory(ctxFindNone).getItems();
        assertNotNull(invNone);
        assertTrue(invNone.isEmpty());

        VoSearchContext ctxFindName = new VoSearchContext();
        ctxFindName.setParameters(createSearchContextParams(
                "filter", "Ben",
                "centreId", 2L
        ));
        ctxFindName.setSize(10);

        final List<VoInventory> invByName = voFulfilmentService.getFilteredInventory(ctxFindName).getItems();
        assertNotNull(invByName);
        assertFalse(invByName.isEmpty());
        assertTrue(invByName.get(0).getSkuCode().contains("BENDER"));

        VoSearchContext ctxFindCode = new VoSearchContext();
        ctxFindCode.setParameters(createSearchContextParams(
                "filter", "!BENDER-ua",
                "centreId", 2L
        ));
        ctxFindCode.setSize(10);

        final List<VoInventory> invByCode = voFulfilmentService.getFilteredInventory(ctxFindCode).getItems();
        assertNotNull(invByCode);
        assertFalse(invByCode.isEmpty());
        assertEquals("BENDER-ua", invByCode.get(0).getSkuCode());

        VoSearchContext ctxFindReserved = new VoSearchContext();
        ctxFindReserved.setParameters(createSearchContextParams(
                "filter", "+1",
                "centreId", 2L
        ));
        ctxFindReserved.setSize(10);

        final List<VoInventory> invReserved = voFulfilmentService.getFilteredInventory(ctxFindReserved).getItems();
        assertNotNull(invReserved);
        assertFalse(invReserved.isEmpty());
        assertEquals("PRODUCT6", invReserved.get(0).getSkuCode());
        assertTrue(BigDecimal.ZERO.compareTo(invReserved.get(0).getReserved()) < 0);

        VoSearchContext ctxFindDate = new VoSearchContext();
        ctxFindDate.setParameters(createSearchContextParams(
                "filter", "2010-01-01<",
                "centreId", 2L
        ));
        ctxFindDate.setSize(10);

        final List<VoInventory> invAvailable = voFulfilmentService.getFilteredInventory(ctxFindDate).getItems();
        assertNotNull(invAvailable);
        assertFalse(invAvailable.isEmpty());

    }

    @Test
    public void testInventoryCRUD() throws Exception {

        VoSearchContext ctxFind = new VoSearchContext();
        ctxFind.setParameters(createSearchContextParams(
                "filter", "TESTCRUD",
                "centreId", 3L
        ));
        ctxFind.setSize(10);

        assertTrue(voFulfilmentService.getFilteredInventory(ctxFind).getItems().isEmpty());

        final VoInventory inventory = new VoInventory();
        inventory.setSkuCode("TESTCRUD");
        inventory.setWarehouseCode("WAREHOUSE_3");
        inventory.setQuantity(BigDecimal.TEN);
        inventory.setAvailability(SkuWarehouse.AVAILABILITY_BACKORDER);
        inventory.setStepOrderQuantity(BigDecimal.TEN);

        final VoInventory created = voFulfilmentService.createInventory(inventory);
        assertTrue(created.getSkuWarehouseId() > 0L);

        assertFalse(voFulfilmentService.getFilteredInventory(ctxFind).getItems().isEmpty());

        VoInventory afterCreated = voFulfilmentService.getInventoryById(created.getSkuWarehouseId());
        assertNotNull(afterCreated);
        assertTrue(new BigDecimal("10").compareTo(afterCreated.getQuantity()) == 0);

        created.setQuantity(new BigDecimal("1000"));

        final VoInventory updated = voFulfilmentService.updateInventory(created);
        assertTrue(new BigDecimal("1000").compareTo(updated.getQuantity()) == 0);

        assertFalse(voFulfilmentService.getFilteredInventory(ctxFind).getItems().isEmpty());

        voFulfilmentService.removeInventory(updated.getSkuWarehouseId());

        VoInventory afterRemoved = voFulfilmentService.getInventoryById(created.getSkuWarehouseId());
        assertNotNull(afterRemoved);
        assertTrue(BigDecimal.ZERO.compareTo(afterRemoved.getQuantity()) == 0);

    }
}