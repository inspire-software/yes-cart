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

package org.yes.cart.shoppingcart.support.tokendriven.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.yes.cart.shoppingcart.ShoppingCart;

/**
 * User: denispavlov
 * Date: 21/04/2015
 * Time: 13:28
 */
public class ShoppingCartStateSerializerJacksonImplTest extends AbstractShoppingCartStateSerializerTest {



    @Test
    public void testSaveRestore() throws Exception {

        final ShoppingCartStateSerializerJacksonImpl serializer = new ShoppingCartStateSerializerJacksonImpl();

        final ShoppingCart cart = createFilledCart();
        assertFilledCart(cart, cart.getGuid());

        byte[] data = serializer.saveState(cart);
        final ShoppingCart restored = serializer.restoreState(data);

        assertFilledCart(restored, cart.getGuid());

    }


    /**
     * This test produces the following results on Mac OSX 2.4GHz Core 2 Duo:
     *
     * == Jackson JSON Serializable implementation: ======================================
     *
     * Serializing 10000 carts took ~11654ms (size of cart is ~22.2Kb)
     * Deserializing 10000 carts took ~9047ms
     * Total to and back for 10000 carts took ~20701ms (~2.07ms per cart)
     *
     * Conclusion: to and back with 100 items is around ~2ms
     *
     * @throws Exception
     */
    @Ignore("This is performance test for manual checks only")
    @Test
    public void testSerializationPerformance() throws Exception {

        serializationPerformanceRoutine(new ShoppingCartStateSerializerJacksonImpl(), 10000, 100, 5, 5);

    }


}
