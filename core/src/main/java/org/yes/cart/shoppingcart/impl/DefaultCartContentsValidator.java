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

package org.yes.cart.shoppingcart.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.config.ConfigurationRegistry;
import org.yes.cart.shoppingcart.CartContentsValidator;
import org.yes.cart.shoppingcart.CartValidityModel;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 15/11/2017
 * Time: 13:59
 */
public class DefaultCartContentsValidator extends AbstractCartContentsValidatorImpl
        implements CartContentsValidator, ConfigurationRegistry<String, CartContentsValidator> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultCartContentsValidator.class);

    private final CartContentsValidator defaultValidator;
    private final Map<String, CartContentsValidator> customValidators = new HashMap<>();

    public DefaultCartContentsValidator(final CartContentsValidator defaultValidator) {
        this.defaultValidator = defaultValidator;
    }


    /** {@inheritDoc} */
    @Override
    public CartValidityModel validate(final ShoppingCart cart) {

        return getValidator(cart.getShoppingContext().getShopCode()).validate(cart);

    }


    protected CartContentsValidator getValidator(final String shopCode) {
        CartContentsValidator validator = customValidators.get(shopCode);
        if (validator == null) {
            validator = defaultValidator;
        }
        return validator;
    }


    /** {@inheritDoc} */
    @Override
    public boolean supports(final String cfgProperty, final Object configuration) {
        return configuration instanceof CartContentsValidator ||
                (configuration instanceof Class && CartContentsValidator.class.isAssignableFrom((Class<?>) configuration));

    }

    /** {@inheritDoc} */
    @Override
    public void register(final String shopCode, final CartContentsValidator validator) {

        if (validator != null) {
            LOG.debug("Custom shop settings for {} registering cart validator {}", shopCode, validator.getClass());
            customValidators.put(shopCode, validator);
        } else {
            LOG.debug("Custom shop settings for {} registering cart validator DEFAULT", shopCode);
            customValidators.remove(shopCode);
        }

    }
}
