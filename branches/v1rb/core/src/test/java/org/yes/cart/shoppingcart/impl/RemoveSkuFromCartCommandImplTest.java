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
public class RemoveSkuFromCartCommandImplTest extends BaseCoreDBTestCase {

    @Test
    public void testExecute() {
        ShoppingCart shoppingCart = new ShoppingCartImpl();
        new ChangeCurrencyEventCommandImpl(ctx(), singletonMap(ChangeCurrencyEventCommandImpl.CMD_KEY, "EUR"))
                .execute(shoppingCart);
        new SetShopCartCommandImpl(ctx(), singletonMap(SetShopCartCommandImpl.CMD_KEY, 10))
                .execute(shoppingCart);
        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), shoppingCart.getCartSubTotal());
        Map<String, String> params = new HashMap<String, String>();
        params.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST3");
        params.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "3");
        new SetSkuQuantityToCartEventCommandImpl(ctx(), params)
                .execute(shoppingCart);
        assertTrue("Expected 21.00 but was " + shoppingCart.getCartSubTotal(), (new BigDecimal("21.00")).equals(shoppingCart.getCartSubTotal()));
        params = new HashMap<String, String>();
        params.put(RemoveSkuFromCartCommandImpl.CMD_KEY, "CC_TEST3");
        new RemoveSkuFromCartCommandImpl(ctx(), params)
                .execute(shoppingCart);
        assertTrue("Expected 15.98 but was " + shoppingCart.getCartSubTotal(), (new BigDecimal("15.98")).equals(shoppingCart.getCartSubTotal()));
        assertEquals(2, shoppingCart.getCartItemsCount());
    }
}
