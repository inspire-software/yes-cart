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

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SetSkuQuantityToCartEventCommandImplTest extends BaseCoreDBTestCase {

    @Test
    public void testExecute() {
        MutableShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        commands.execute(shoppingCart, new HashMap<String, Object>() {{
            put(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR");
            put(ShoppingCartCommand.CMD_CHANGELOCALE, "en");
            put(ShoppingCartCommand.CMD_SETSHOP, "10");
        }});

        assertEquals(MoneyUtils.ZERO, shoppingCart.getTotal().getSubTotal());
        Map<String, String> params = new HashMap<>();
        params.put(SetSkuQuantityToCartEventCommandImpl.CMD_ADDTOCART, "CC_TEST4");
        params.put(ShoppingCartCommand.CMD_P_QTY, "1");
        commands.execute(shoppingCart, (Map) params);

        assertEquals("123.00", shoppingCart.getTotal().getSubTotal().toPlainString());

        params.clear();
        params.put(SetSkuQuantityToCartEventCommandImpl.CMD_SETQTYSKU, "CC_TEST4");
        params.put(ShoppingCartCommand.CMD_P_QTY, "5");
        commands.execute(shoppingCart, (Map) params);

        assertEquals("499.95", shoppingCart.getTotal().getSubTotal().toPlainString());

    }


    @Test
    public void testExecuteProductWithOptions() throws Exception {

        MutableShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETSHOP, 10));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR"));

        assertEquals(MoneyUtils.ZERO, shoppingCart.getTotal().getSubTotal());
        Map<String, String> params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "NOPROD-SKU");

        commands.execute(shoppingCart, (Map) params);

        params.clear();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "001_CFG01");
        params.put(ShoppingCartCommand.CMD_P_ITEM_GROUP, "001_CFG01-A");
        params.put("MATERIAL", "001_CFG_OPT1_A");

        commands.execute(shoppingCart, (Map) params);

        params.clear();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "001_CFG01");
        params.put(ShoppingCartCommand.CMD_P_ITEM_GROUP, "001_CFG01-B");
        params.put("MATERIAL", "001_CFG_OPT1_B");

        commands.execute(shoppingCart, (Map) params);

        assertEquals("45049.99", shoppingCart.getTotal().getSubTotal().toPlainString());
        assertEquals(3, shoppingCart.getCartItemsCount());
        assertEquals(5, shoppingCart.getCartItemList().size());

        params.clear();
        params.put(ShoppingCartCommand.CMD_SETQTYSKU, "001_CFG01");
        params.put(ShoppingCartCommand.CMD_P_ITEM_GROUP, "001_CFG01-B");
        params.put(ShoppingCartCommand.CMD_P_QTY, "4");
        commands.execute(shoppingCart, (Map) params);

        assertEquals("120049.99", shoppingCart.getTotal().getSubTotal().toPlainString());
        assertEquals(6, shoppingCart.getCartItemsCount());
        assertEquals(5, shoppingCart.getCartItemList().size());


    }

}
