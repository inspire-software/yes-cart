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
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * User: denispavlov
 * Date: 12/12/2014
 * Time: 13:10
 */
public class SetOrderMessageCommandImplTest extends BaseCoreDBTestCase {
    /**
     * Test command.
     */
    @Test
    public void testExecute() {

        MutableShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        assertNull(shoppingCart.getOrderInfo().getPaymentGatewayLabel());

        Map params = new HashMap();

        params.put(SetPaymentGatewayLabelCommandImpl.CMD_SETORDERMSG, "message");

        commands.execute(shoppingCart, params);
        assertEquals("message", shoppingCart.getOrderInfo().getOrderMessage());

        params.put(SetPaymentGatewayLabelCommandImpl.CMD_SETORDERMSG, "qwerty");

        commands.execute(shoppingCart, params);
        assertEquals("qwerty", shoppingCart.getOrderInfo().getOrderMessage());

        params.put(SetPaymentGatewayLabelCommandImpl.CMD_SETORDERMSG, null);

        commands.execute(shoppingCart, params);
        assertNull(shoppingCart.getOrderInfo().getOrderMessage());

    }
}
