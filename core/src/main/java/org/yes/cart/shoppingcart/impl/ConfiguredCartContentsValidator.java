/*
 * Copyright 2009 Inspire-Software.com
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

import org.yes.cart.config.Configuration;
import org.yes.cart.config.ConfigurationContext;
import org.yes.cart.shoppingcart.CartContentsValidator;
import org.yes.cart.shoppingcart.CartValidityModel;
import org.yes.cart.shoppingcart.ShoppingCart;

/**
 * User: denispavlov
 * Date: 27/03/2018
 * Time: 21:05
 */
public class ConfiguredCartContentsValidator implements CartContentsValidator, Configuration {

    private CartContentsValidator validator;

    private ConfigurationContext cfgContext;

    /** {@inheritDoc} */
    @Override
    public CartValidityModel validate(final ShoppingCart cart) {
        return validator.validate(cart);
    }

    public void setValidator(final CartContentsValidator validator) {
        this.validator = validator;
    }

    /** {@inheritDoc} */
    @Override
    public ConfigurationContext getCfgContext() {
        return cfgContext;
    }

    public void setCfgContext(final ConfigurationContext cfgContext) {
        this.cfgContext = cfgContext;
    }
}
