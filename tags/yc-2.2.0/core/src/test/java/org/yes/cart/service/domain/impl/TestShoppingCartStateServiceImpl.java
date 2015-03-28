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

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.ShoppingCartState;
import org.yes.cart.service.domain.ShoppingCartStateService;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 22/08/2014
 * Time: 14:02
 */
public class TestShoppingCartStateServiceImpl extends BaseCoreDBTestCase {

    private ShoppingCartStateService shoppingCartStateService;


    @Before
    public void setUp()  {
        shoppingCartStateService = (ShoppingCartStateService) ctx().getBean(ServiceSpringKeys.SHOPPING_CART_STATE_SERVICE);
        super.setUp();
    }


    @Test
    public void testFindByGuidOrEmail() {
        List<ShoppingCartState> carts;
        assertNull(shoppingCartStateService.findByGuid("CART-001"));
        carts = shoppingCartStateService.findByCustomerEmail("bob@doe.com");
        assertNotNull(carts);
        assertTrue(carts.isEmpty());

        final ShoppingCartState scs = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
        scs.setGuid("CART-001");
        scs.setCustomerEmail("bob@doe.com");
        scs.setState("State".getBytes());
        shoppingCartStateService.create(scs);

        assertNotNull(shoppingCartStateService.findByGuid("CART-001"));
        carts = shoppingCartStateService.findByCustomerEmail("bob@doe.com");
        assertNotNull(carts);
        assertEquals(carts.size(), 1);

        shoppingCartStateService.delete(scs);

        assertNull(shoppingCartStateService.findByGuid("CART-001"));
        carts = shoppingCartStateService.findByCustomerEmail("bob@doe.com");
        assertNotNull(carts);
        assertTrue(carts.isEmpty());
    }

    @Test
    public void testFindByModificationPrior() {

        final ShoppingCartState scs = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
        scs.setGuid("CART-001");
        scs.setCustomerEmail("bob@doe.com");
        scs.setState("State".getBytes());
        shoppingCartStateService.create(scs);

        final Date tenSecondsAfterCreation = new Date(System.currentTimeMillis() + 10000L);
        final Date tenSecondsBeforeCreation = new Date(tenSecondsAfterCreation.getTime() - 20000L);

        List<ShoppingCartState> carts;
        carts = shoppingCartStateService.findByModificationPrior(tenSecondsBeforeCreation);
        assertNotNull(carts);
        assertTrue(carts.isEmpty());

        carts = shoppingCartStateService.findByModificationPrior(tenSecondsAfterCreation);
        assertNotNull(carts);
        assertEquals(carts.size(), 1);

        shoppingCartStateService.delete(shoppingCartStateService.findByGuid("CART-001"));

    }

}
