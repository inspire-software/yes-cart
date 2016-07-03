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

package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.Constants;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 11/03/2016
 * Time: 17:31
 */
public class SetSkuPriceEventCommandImplTest extends BaseCoreDBTestCase {

    @Test
    public void testExecute() throws Exception {

        MutableShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETSHOP, 10));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR"));

        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), shoppingCart.getTotal().getSubTotal());
        Map<String, String> params = new HashMap<String, String>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST1");
        params.put(ShoppingCartCommand.CMD_ADDTOCART_P_QTY, "5");

        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 90.00, was: " + shoppingCart.getTotal().getListSubTotal(), (new BigDecimal("90.00")).compareTo(shoppingCart.getTotal().getListSubTotal()) == 0);
        assertTrue("Expected 90.00, was: " + shoppingCart.getTotal().getSubTotal(), (new BigDecimal("90.00")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);

        params.clear();
        params.put(ShoppingCartCommand.CMD_SETPRICE, "CC_TEST1");
        params.put(ShoppingCartCommand.CMD_SETPRICE_P_PRICE, "15.00");
        params.put(ShoppingCartCommand.CMD_SETPRICE_P_AUTH, "CC0001");

        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 90.00, was: " + shoppingCart.getTotal().getListSubTotal(), (new BigDecimal("90.00")).compareTo(shoppingCart.getTotal().getListSubTotal()) == 0);
        assertTrue("Expected 75.00, was: " + shoppingCart.getTotal().getSubTotal(), (new BigDecimal("75.00")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);

        params.clear();
        params.put(ShoppingCartCommand.CMD_SETPRICE, "CC_TEST1");
        params.put(ShoppingCartCommand.CMD_SETPRICE_P_PRICE, "30.00");
        params.put(ShoppingCartCommand.CMD_SETPRICE_P_AUTH, "CC0001");

        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 150.00, was: " + shoppingCart.getTotal().getListSubTotal(), (new BigDecimal("150.00")).compareTo(shoppingCart.getTotal().getListSubTotal()) == 0);
        assertTrue("Expected 150.00, was: " + shoppingCart.getTotal().getSubTotal(), (new BigDecimal("150.00")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);

    }
}