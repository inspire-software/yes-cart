/*
 * Copyright 2009 Inspire-Software.com
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
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

        assertEquals(MoneyUtils.ZERO, shoppingCart.getTotal().getSubTotal());
        Map<String, String> params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST1");
        params.put(ShoppingCartCommand.CMD_P_QTY, "5");

        commands.execute(shoppingCart, (Map) params);
        assertEquals("90.00", shoppingCart.getTotal().getListSubTotal().toPlainString());
        assertEquals("90.00", shoppingCart.getTotal().getSubTotal().toPlainString());

        params.clear();
        params.put(ShoppingCartCommand.CMD_SETPRICE, "CC_TEST1");
        params.put(ShoppingCartCommand.CMD_SETPRICE_P_PRICE, "15.00");
        params.put(ShoppingCartCommand.CMD_SETPRICE_P_AUTH, "CC0001");

        commands.execute(shoppingCart, (Map) params);
        assertEquals("90.00", shoppingCart.getTotal().getListSubTotal().toPlainString());
        assertEquals("75.00", shoppingCart.getTotal().getSubTotal().toPlainString());

        params.clear();
        params.put(ShoppingCartCommand.CMD_SETPRICE, "CC_TEST1");
        params.put(ShoppingCartCommand.CMD_SETPRICE_P_PRICE, "30.00");
        params.put(ShoppingCartCommand.CMD_SETPRICE_P_AUTH, "CC0001");

        commands.execute(shoppingCart, (Map) params);
        assertEquals("150.00", shoppingCart.getTotal().getListSubTotal().toPlainString());
        assertEquals("150.00", shoppingCart.getTotal().getSubTotal().toPlainString());

    }
}