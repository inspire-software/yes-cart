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

import org.apache.wicket.Session;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.Locale;
import java.util.Map;

/**
 * Wicket change locale command invokes Wicket specific settings for current locale.
 *
 * User: denispavlov
 * Date: 27/08/2014
 * Time: 00:29
 */
public class WicketChangeLocaleCartCommandImpl extends ChangeLocaleCartCommandImpl {

    private static final long serialVersionUID = 20101026L;

    /**
     * Wicket command.
     *
     * @param registry shopping cart command registry
     */
    public WicketChangeLocaleCartCommandImpl(final ShoppingCartCommandRegistry registry) {
        super(registry);
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final ShoppingCart shoppingCart, final Map<String, Object> parameters) {
        // current locale may be null if it is new cart
        final String locale = shoppingCart.getCurrentLocale();
        super.execute(shoppingCart, parameters);
        if (parameters.containsKey(getCmdKey()) && !shoppingCart.getCurrentLocale().equals(locale)) {
            Session.get().setLocale(new Locale(shoppingCart.getCurrentLocale()));
        }
    }
}
