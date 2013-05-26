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
        ShoppingCart shoppingCart = new ShoppingCartImpl();
        new ChangeCurrencyEventCommandImpl(ctx(), singletonMap(ChangeCurrencyEventCommandImpl.CMD_KEY, "EUR"))
                .execute(shoppingCart);
        new SetShopCartCommandImpl(ctx(), singletonMap(SetShopCartCommandImpl.CMD_KEY, 10))
                .execute(shoppingCart);
        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), shoppingCart.getCartSubTotal());
        Map<String, String> params = new HashMap<String, String>();
        params.put(AddSkuToCartEventCommandImpl.CMD_KEY, "CC_TEST1");
        AddSkuToCartEventCommandImpl command;
        command = new AddSkuToCartEventCommandImpl(ctx(), params);
        command.execute(shoppingCart);
        assertTrue("Expected 19.99 actual value " + shoppingCart.getCartSubTotal(), (new BigDecimal("19.99")).compareTo(shoppingCart.getCartSubTotal()) == 0);
        command = new AddSkuToCartEventCommandImpl(ctx(), params);
        command.execute(shoppingCart);
        assertTrue("Expected 39.98", (new BigDecimal("39.98")).compareTo(shoppingCart.getCartSubTotal()) == 0);
        command = new AddSkuToCartEventCommandImpl(ctx(), params);
        command.execute(shoppingCart);
        assertTrue("Expected 57.00", (new BigDecimal("57.00")).compareTo(shoppingCart.getCartSubTotal()) == 0);

        params.put(AddSkuToCartEventCommandImpl.CMD_QTY_KEY, "7");
        command = new AddSkuToCartEventCommandImpl(ctx(), params);
        command.execute(shoppingCart);
        assertTrue("Expected 180.00", (new BigDecimal("180.00")).compareTo(shoppingCart.getCartSubTotal()) == 0);

        params.put(AddSkuToCartEventCommandImpl.CMD_QTY_KEY, "zzzz"); // if invalid use 1
        command = new AddSkuToCartEventCommandImpl(ctx(), params);
        command.execute(shoppingCart);
        assertTrue("Expected 198.00", (new BigDecimal("198.00")).compareTo(shoppingCart.getCartSubTotal()) == 0);


    }
}