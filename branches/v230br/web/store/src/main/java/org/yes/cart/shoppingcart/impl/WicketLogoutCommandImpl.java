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

import org.apache.wicket.Application;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.Map;

/**
 * Wicket logout command that invokes Wicket session invalidation.
 *
 * User: denispavlov
 * Date: 27/08/2014
 * Time: 00:21
 */
public class WicketLogoutCommandImpl extends LogoutCommandImpl {

    private static final long serialVersionUID = 20101026L;

    public WicketLogoutCommandImpl(final ShoppingCartCommandRegistry registry) {
        super(registry);
    }

    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        super.execute(shoppingCart, parameters);
        if (shoppingCart.getLogonState() == ShoppingCart.NOT_LOGGED && parameters.containsKey(getCmdKey())) {
            final IAuthenticationStrategy strategy = Application.get().getSecuritySettings().getAuthenticationStrategy();
            strategy.remove();
            AuthenticatedWebSession.get().signOut();
        }
    }
}
