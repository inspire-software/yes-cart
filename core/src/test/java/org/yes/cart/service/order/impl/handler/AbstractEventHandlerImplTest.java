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

package org.yes.cart.service.order.impl.handler;

import org.springframework.context.ApplicationContext;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.impl.*;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class AbstractEventHandlerImplTest extends BaseCoreDBTestCase {

    /**
     * Simple card with two sku, three items, standard availability, one payment
     *
     * @param context spring context
     * @return cart
     */
    protected ShoppingCart getStdCard(final ApplicationContext context, final String customerEmail) {
        ShoppingCart shoppingCart = getEmptyCart(customerEmail);
        Map<String, String> param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST1");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "2.00");
        new SetSkuQuantityToCartEventCommandImpl(context, param)
                .execute(shoppingCart);
        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST2");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "1.00");
        new SetSkuQuantityToCartEventCommandImpl(context, param)
                .execute(shoppingCart);
        return shoppingCart;
    }

    protected ShoppingCart getEmptyCart(final String customerEmail) {
        ShoppingCart shoppingCart = new ShoppingCartImpl();
        Map<String, String> params = new HashMap<String, String>();
        params.put(LoginCommandImpl.EMAIL, customerEmail);
        params.put(LoginCommandImpl.NAME, "John Doe");
        new SetShopCartCommandImpl(ctx(), singletonMap(SetShopCartCommandImpl.CMD_KEY, 10))
                .execute(shoppingCart);
        new ChangeCurrencyEventCommandImpl(ctx(), singletonMap(ChangeCurrencyEventCommandImpl.CMD_KEY, "USD"))
                .execute(shoppingCart);
        new LoginCommandImpl(null, params)
                .execute(shoppingCart);
        new SetCarrierSlaCartCommandImpl(null, singletonMap(SetCarrierSlaCartCommandImpl.CMD_KEY, "1"))
                .execute(shoppingCart);
        return shoppingCart;
    }
}
