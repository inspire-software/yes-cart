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
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ExpireCartCommandImplTest extends BaseCoreDBTestCase {

    @Test
    public void testExecute() {
        ShoppingCart shoppingCart = new ShoppingCartImpl();
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put(ShoppingCartCommand.CMD_LOGIN_P_EMAIL, "test@test.com");
        params.put(ShoppingCartCommand.CMD_LOGIN_P_NAME, "John Doe");
        params.put(ShoppingCartCommand.CMD_LOGIN, ShoppingCartCommand.CMD_LOGIN);
        commands.execute(shoppingCart, (Map) params);
        assertNotNull(shoppingCart.getCustomerEmail());
        assertNotNull(shoppingCart.getCustomerName());

        commands.execute(shoppingCart,
                (Map) Collections.singletonMap(ShoppingCartCommand.CMD_EXPIRE, null));
        assertNull(shoppingCart.getCustomerEmail());
        assertNull(shoppingCart.getCustomerName());
    }
}
