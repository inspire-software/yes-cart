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
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/6/11
 * Time: 8:30 PM
 */
public class SetPaymentGatewayLabelCommandImplTest extends BaseCoreDBTestCase {
    /**
     * Test command.
     */
    @Test
    public void testExecute() {

        ShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        assertNull(shoppingCart.getOrderInfo().getPaymentGatewayLabel());

        Map params = new HashMap();

        params.put(SetPaymentGatewayLabelCommandImpl.CMD_SETPGLABEL, "label");

        commands.execute(shoppingCart, params);
        assertEquals("label", shoppingCart.getOrderInfo().getPaymentGatewayLabel());

        params.put(SetPaymentGatewayLabelCommandImpl.CMD_SETPGLABEL, "qwerty");

        commands.execute(shoppingCart, params);
        assertEquals("qwerty", shoppingCart.getOrderInfo().getPaymentGatewayLabel());

    }
}
