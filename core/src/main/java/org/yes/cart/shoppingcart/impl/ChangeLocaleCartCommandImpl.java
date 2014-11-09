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

import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/25/11
 * Time: 11:37 AM
 */
public class ChangeLocaleCartCommandImpl  extends AbstractCartCommandImpl {

    private static final long serialVersionUID = 20110625L;

    private final LanguageService languageService;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     * @param languageService shop service
     */
    public ChangeLocaleCartCommandImpl(final ShoppingCartCommandRegistry registry,
                                       final LanguageService languageService) {
        super(registry);
        this.languageService = languageService;
    }

    /** {@inheritDoc} */
    public String getCmdKey() {
        return CMD_CHANGELOCALE;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {
            final String locale = (String) parameters.get(getCmdKey());
            if (locale != null && !locale.equals(shoppingCart.getCurrentLocale())) {
                final List<String> supported = languageService.getSupportedLanguages(shoppingCart.getShoppingContext().getShopCode());
                if (supported.contains(locale)) {
                    shoppingCart.setCurrentLocale(locale);
                    markDirty(shoppingCart);
                }
            }
        }
    }
}