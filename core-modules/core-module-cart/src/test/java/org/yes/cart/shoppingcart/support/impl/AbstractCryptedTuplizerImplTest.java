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

package org.yes.cart.shoppingcart.support.impl;

import org.junit.Test;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 15/03/2016
 * Time: 12:17
 */
public class AbstractCryptedTuplizerImplTest {

    @Test
    public void testCrypto() throws Exception {

        final ShoppingCartImpl cart = new ShoppingCartImpl();

        cart.addProductSkuToCart("s01","SKU001", "SKU001", BigDecimal.ONE);
        cart.setProductSkuPrice("s01","SKU001", new BigDecimal("9.99"), new BigDecimal("9.99"));

        final AbstractCryptedTuplizerImpl tup = new AbstractCryptedTuplizerImpl(
                "CHANGE_PASSWORD",
                "DES",
                "DES/ECB/PKCS5Padding"
        ) {

        };


        final String base64 = tup.toToken(cart);
        assertFalse("Splitting happens manually as it is media dependent", base64.contains("\n"));

        final ShoppingCart restored = tup.toObject(base64);

        assertNotNull(restored);
        assertEquals(1, restored.getCartItemList().size());

    }

}