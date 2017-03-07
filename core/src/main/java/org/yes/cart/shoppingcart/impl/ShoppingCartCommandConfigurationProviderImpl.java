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
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommandConfigurationProvider;
import org.yes.cart.shoppingcart.ShoppingCartCommandConfigurationVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 24/01/2017
 * Time: 07:37
 */
public class ShoppingCartCommandConfigurationProviderImpl implements ShoppingCartCommandConfigurationProvider<MutableShoppingCart> {

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartCommandConfigurationProviderImpl.class);

    private final Map<String, ShoppingCartCommandConfigurationVisitor<MutableShoppingCart>> providers =
            new HashMap<String, ShoppingCartCommandConfigurationVisitor<MutableShoppingCart>>();

    public ShoppingCartCommandConfigurationProviderImpl(final Set<ShoppingCartCommandConfigurationVisitor<MutableShoppingCart>> providers) {
        for (final ShoppingCartCommandConfigurationVisitor<MutableShoppingCart> provider : providers) {
            this.providers.put(provider.getId(), provider);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ShoppingCartCommandConfigurationVisitor<MutableShoppingCart> provide(final String visitorId) {
        final ShoppingCartCommandConfigurationVisitor<MutableShoppingCart> provider = this.providers.get(visitorId);
        if (provider == null) {
            return newNoop(visitorId);
        }
        return provider;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getVisitorIds() {
        return this.providers.keySet();
    }

    private ShoppingCartCommandConfigurationVisitor<MutableShoppingCart> newNoop(final String id) {
        return new ShoppingCartCommandConfigurationVisitor<MutableShoppingCart>() {
            /**
             * {@inheritDoc}
             */
            public String getId() {
                return "NOOP";
            }

            /**
             * {@inheritDoc}
             */
            public void visit(final MutableShoppingCart cart, final Object ... args) {
                LOG.error("Unknown ShoppingCartCommandConfigurationVisitor {} using NOOP", id);
            }
        };
    }

}
