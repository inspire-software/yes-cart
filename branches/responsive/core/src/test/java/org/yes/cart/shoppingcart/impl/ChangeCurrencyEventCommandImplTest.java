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
import org.yes.cart.constants.Constants;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ChangeCurrencyEventCommandImplTest extends BaseCoreDBTestCase {

    @Test
    public void testExecute() {
        ShoppingCart shoppingCart = new ShoppingCartImpl();
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        commands.execute(shoppingCart,
                (Map) Collections.singletonMap(ShoppingCartCommand.CMD_SETSHOP, "10"));
        commands.execute(shoppingCart,
                (Map) Collections.singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR"));
        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), shoppingCart.getCartSubTotal());
        for (int i = 0; i < 3; i++) {
            commands.execute(shoppingCart,
                    (Map) Collections.singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST1"));
        }

        assertTrue("Expected 57.00 Actual " + shoppingCart.getCartSubTotal(), (new BigDecimal("57.00")).equals(shoppingCart.getCartSubTotal()));
        commands.execute(shoppingCart,
                (Map) Collections.singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "USD"));
        assertEquals("USD", shoppingCart.getCurrencyCode());
        assertTrue("Expected 570.03", (new BigDecimal("570.03")).equals(shoppingCart.getCartSubTotal()));
    }
}