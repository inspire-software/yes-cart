/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.ShoppingCartState;
import org.yes.cart.service.domain.ShoppingCartStateService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 22/08/2014
 * Time: 14:02
 */
public class TestShoppingCartStateServiceImpl extends BaseCoreDBTestCase {

    private ShoppingCartStateService shoppingCartStateService;


    @Override
    @Before
    public void setUp()  {
        shoppingCartStateService = (ShoppingCartStateService) ctx().getBean(ServiceSpringKeys.SHOPPING_CART_STATE_SERVICE);
        super.setUp();
    }


    @Test
    public void testFindByGuidOrEmail() {

        final String uuid = UUID.randomUUID().toString();

        List<ShoppingCartState> carts;
        assertNull(shoppingCartStateService.findByGuid(uuid));
        carts = shoppingCartStateService.findByCustomerEmail("bob@doe.com");
        assertNotNull(carts);
        assertTrue(carts.isEmpty());

        final ShoppingCartState scs = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
        scs.setGuid(uuid);
        scs.setCustomerEmail("bob@doe.com");
        scs.setState("State".getBytes());
        shoppingCartStateService.create(scs);

        assertNotNull(shoppingCartStateService.findByGuid(uuid));
        carts = shoppingCartStateService.findByCustomerEmail("bob@doe.com");
        assertNotNull(carts);
        assertEquals(carts.size(), 1);

        shoppingCartStateService.delete(scs);

        assertNull(shoppingCartStateService.findByGuid(uuid));
        carts = shoppingCartStateService.findByCustomerEmail("bob@doe.com");
        assertNotNull(carts);
        assertTrue(carts.isEmpty());
    }

    @Test
    public void testFindByModificationPrior() {

        final String uuid = UUID.randomUUID().toString();

        final ShoppingCartState scs = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
        scs.setGuid(uuid);
        scs.setCustomerEmail("bob@doe.com");
        scs.setEmpty(false);
        scs.setState("State".getBytes());
        shoppingCartStateService.create(scs);

        final Instant tenSecondsAfterCreation = Instant.now().plusSeconds(10L);
        final Instant tenSecondsBeforeCreation = tenSecondsAfterCreation.plusSeconds(-20L);

        getTxReadOnly().execute(status -> {
            List<ShoppingCartState> carts = shoppingCartStateService.findByCriteria(
                    " where e.updatedTimestamp < ?1 OR e.updatedTimestamp IS NULL",
                    tenSecondsBeforeCreation
            );
            assertNotNull(carts);
            assertTrue(carts.isEmpty());
            return null;
        });

        getTxReadOnly().execute(status -> {
            List<ShoppingCartState> carts = shoppingCartStateService.findByCriteria(
                    " where e.updatedTimestamp < ?1 OR e.updatedTimestamp IS NULL",
                    tenSecondsAfterCreation
            );
            assertNotNull(carts);
            assertEquals(1, carts.size()); // one
            return null;
        });

        shoppingCartStateService.delete(shoppingCartStateService.findByGuid(uuid));

    }

    @Test
    public void testFindByModificationPriorEmptyAnonymousFalse() {

        final String uuid = UUID.randomUUID().toString();

        final ShoppingCartState scs = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
        scs.setGuid(uuid);
        scs.setCustomerEmail("bob@doe.com");
        scs.setEmpty(true);
        scs.setState("State".getBytes());
        shoppingCartStateService.create(scs);

        final Instant tenSecondsAfterCreation = Instant.now().plusSeconds(10L);
        final Instant tenSecondsBeforeCreation = tenSecondsAfterCreation.plusSeconds(-20L);

        getTxReadOnly().execute(status -> {
            List<ShoppingCartState> carts = shoppingCartStateService.findByCriteria(
                    " where e.empty = ?2 AND e.customerEmail IS NULL AND (e.updatedTimestamp < ?1 OR e.updatedTimestamp IS NULL)",
                    tenSecondsBeforeCreation, true
            );
            assertNotNull(carts);
            assertTrue(carts.isEmpty());
            return null;
        });

        getTxReadOnly().execute(status -> {
            List<ShoppingCartState> carts = shoppingCartStateService.findByCriteria(
                    " where e.empty = ?2 AND e.customerEmail IS NULL AND (e.updatedTimestamp < ?1 OR e.updatedTimestamp IS NULL)",
                    tenSecondsAfterCreation, true
            );
            assertNotNull(carts);
            assertTrue(carts.isEmpty());
            return null;
        });

        shoppingCartStateService.delete(shoppingCartStateService.findByGuid(uuid));

    }

    @Test
    public void testFindByModificationPriorEmptyAnonymousTrue() {

        final String uuid = UUID.randomUUID().toString();

        final ShoppingCartState scs = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
        scs.setGuid(uuid);
        scs.setState("State".getBytes());
        shoppingCartStateService.create(scs);

        final Instant tenSecondsAfterCreation = Instant.now().plusSeconds(10L);
        final Instant tenSecondsBeforeCreation = tenSecondsAfterCreation.plusSeconds(-20L);

        getTxReadOnly().execute(status -> {
            List<ShoppingCartState> carts = shoppingCartStateService.findByCriteria(
                    " where e.empty = ?2 AND e.customerEmail IS NULL AND (e.updatedTimestamp < ?1 OR e.updatedTimestamp IS NULL)",
                    tenSecondsBeforeCreation, true
            );
            assertNotNull(carts);
            assertTrue(carts.isEmpty());
            return null;
        });

        getTxReadOnly().execute(status -> {
            List<ShoppingCartState> carts = shoppingCartStateService.findByCriteria(
                    " where e.empty = ?2 AND e.customerEmail IS NULL AND (e.updatedTimestamp < ?1 OR e.updatedTimestamp IS NULL)",
                    tenSecondsAfterCreation, true
            );
            assertNotNull(carts);
            assertEquals(1, carts.size());  // one
            return null;
        });

        shoppingCartStateService.delete(shoppingCartStateService.findByGuid(uuid));

    }

}
