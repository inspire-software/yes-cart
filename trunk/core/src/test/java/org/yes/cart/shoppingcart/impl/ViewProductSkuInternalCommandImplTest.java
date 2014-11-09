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

package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 16/01/2014
 * Time: 18:33
 */
public class ViewProductSkuInternalCommandImplTest extends BaseCoreDBTestCase {


    @Test
    public void testExecute() {

        final Customer customer = createCustomer();

        MutableShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        assertNull(shoppingCart.getShoppingContext().getLatestViewedSkus());

        // Test adding single sku

        Map<String, String> params = new HashMap<String, String>();
        params.put(ShoppingCartCommand.CMD_INTERNAL_VIEWSKU, "1");
        commands.execute(shoppingCart, (Map) params);

        List<String> skus = shoppingCart.getShoppingContext().getLatestViewedSkus();
        assertNotNull(skus);
        assertEquals(1, skus.size());
        assertEquals("1", skus.get(0));

        // Test adding duplicate sku 1 and 9 others to have full 10 items

        for (int i = 1; i <= 10; i++) {

            params.put(ShoppingCartCommand.CMD_INTERNAL_VIEWSKU, String.valueOf(i));
            commands.execute(shoppingCart, (Map) params);

        }

        skus = shoppingCart.getShoppingContext().getLatestViewedSkus();
        assertNotNull(skus);
        assertEquals(10, skus.size());
        for (int i = 1; i <= 10; i++) {

            assertEquals(String.valueOf(i), skus.get(i - 1));

        }

        // Test adding 11th item
        params.put(ShoppingCartCommand.CMD_INTERNAL_VIEWSKU, "11");
        commands.execute(shoppingCart, (Map) params);

        skus = shoppingCart.getShoppingContext().getLatestViewedSkus();
        assertNotNull(skus);
        assertEquals(10, skus.size());
        for (int i = 1; i <= 10; i++) {

            assertEquals(String.valueOf(i + 1), skus.get(i - 1));

        }



    }


}
