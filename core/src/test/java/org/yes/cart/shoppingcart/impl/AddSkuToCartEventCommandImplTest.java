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
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AddSkuToCartEventCommandImplTest extends BaseCoreDBTestCase {

    @Test
    public void testExecute() {
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

        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 19.99 actual value " + shoppingCart.getTotal().getSubTotal(), (new BigDecimal("19.99")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);

        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 39.98", (new BigDecimal("39.98")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);

        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 57.00", (new BigDecimal("57.00")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);

        params.put(ShoppingCartCommand.CMD_P_QTY, "7");
        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 180.00", (new BigDecimal("180.00")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);

        params.put(ShoppingCartCommand.CMD_P_QTY, "zzzz"); // if invalid use 1
        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 198.00", (new BigDecimal("198.00")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);


    }


    @Test
    public void testExecuteSkuCodeOnly() {
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
        assertTrue("Expected 49.99 actual value " + shoppingCart.getTotal().getSubTotal(), (new BigDecimal("49.99")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);

        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 99.98", (new BigDecimal("99.98")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);

        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 149.97", (new BigDecimal("149.97")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);

        params.put(ShoppingCartCommand.CMD_P_QTY, "7");
        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 499.90", (new BigDecimal("499.90")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);

        params.put(ShoppingCartCommand.CMD_P_QTY, "zzzz"); // if invalid use 1
        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 549.89", (new BigDecimal("549.89")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);

        params.put(ShoppingCartCommand.CMD_P_QTY, "0.3"); // if no prod model round up to int
        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 599.88", (new BigDecimal("599.88")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);

        params.put(ShoppingCartCommand.CMD_ADDTOCART, "NOPROD-SKU-NONEXISTENT");
        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 599.88", (new BigDecimal("599.88")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);

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
        assertTrue("Expected 49.99 actual value " + shoppingCart.getTotal().getSubTotal(), (new BigDecimal("49.99")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);
        assertEquals(1, shoppingCart.getCartItemsCount());
        assertEquals(1, shoppingCart.getCartItemList().size());

        params.clear();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "001_CFG01");
        params.put(ShoppingCartCommand.CMD_P_ITEM_GROUP, "001_CFG01-A");
        params.put("MATERIAL", "001_CFG_OPT1_A");

        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 20049.99 actual value " + shoppingCart.getTotal().getSubTotal(), (new BigDecimal("20049.99")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);
        assertEquals(2, shoppingCart.getCartItemsCount());
        assertEquals(3, shoppingCart.getCartItemList().size());

        params.clear();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "001_CFG01");
        params.put(ShoppingCartCommand.CMD_P_ITEM_GROUP, "001_CFG01-B");
        params.put("MATERIAL", "001_CFG_OPT1_B");

        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 45049.99 actual value " + shoppingCart.getTotal().getSubTotal(), (new BigDecimal("45049.99")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);
        assertEquals(3, shoppingCart.getCartItemsCount());
        assertEquals(5, shoppingCart.getCartItemList().size());


        // Same group - no change
        params.clear();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "001_CFG01");
        params.put(ShoppingCartCommand.CMD_P_ITEM_GROUP, "001_CFG01-A");
        params.put("MATERIAL", "001_CFG_OPT1_A");

        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 45049.99 actual value " + shoppingCart.getTotal().getSubTotal(), (new BigDecimal("45049.99")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);
        assertEquals(3, shoppingCart.getCartItemsCount());
        assertEquals(5, shoppingCart.getCartItemList().size());

        // Auto generated group - new item
        params.clear();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "001_CFG01");
        params.put("MATERIAL", "001_CFG_OPT1_A");

        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 65049.99 actual value " + shoppingCart.getTotal().getSubTotal(), (new BigDecimal("65049.99")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);
        assertEquals(4, shoppingCart.getCartItemsCount());
        assertEquals(7, shoppingCart.getCartItemList().size());

    }
}