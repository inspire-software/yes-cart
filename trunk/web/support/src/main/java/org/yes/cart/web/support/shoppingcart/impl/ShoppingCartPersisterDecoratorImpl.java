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

package org.yes.cart.web.support.shoppingcart.impl;

import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.support.shoppingcart.ShoppingCartPersister;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: denispavlov
 * Date: 22/08/2014
 * Time: 00:25
 */
public class ShoppingCartPersisterDecoratorImpl implements ShoppingCartPersister {

    private final ShoppingCartPersister shoppingCartPersister;

    public ShoppingCartPersisterDecoratorImpl(final ShoppingCartPersister shoppingCartPersister) {
        this.shoppingCartPersister = shoppingCartPersister;
    }

    /** {@inheritDoc} */
    @Override
    public void persistShoppingCart(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final ShoppingCart shoppingCart) {
        shoppingCartPersister.persistShoppingCart(httpServletRequest, httpServletResponse, shoppingCart);
    }

}
