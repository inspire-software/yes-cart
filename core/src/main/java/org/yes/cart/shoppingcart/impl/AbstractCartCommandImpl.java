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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.shoppingcart.*;

import java.util.Arrays;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 13:16
 */
public abstract class AbstractCartCommandImpl implements ConfigurableShoppingCartCommand {

    private int priority = 0;

    private ShoppingCartCommandConfigurationProvider configurationProvider;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     */
    protected AbstractCartCommandImpl(final ShoppingCartCommandRegistry registry) {
        registry.registerCommand(this);
    }

    /**
     * {@inheritDoc}
     */
    public void configure(final ShoppingCartCommandConfigurationProvider provider) {
        this.configurationProvider = provider;
    }

    /**
     * {@inheritDoc}
     */
    public final void execute(final ShoppingCart shoppingCart, final Map<String, Object> parameters) {
        // OOTB we only have mutable cart
        execute((MutableShoppingCart) shoppingCart, parameters);
    }

    /**
     * Internal hook to switch to mutable cart.
     *
     * @param shoppingCart mutable cart
     * @param parameters   parameters
     */
    public abstract void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters);

    /**
     * Recalculate shopping cart.
     *
     * @param shoppingCart current cart
     */
    protected void recalculate(final MutableShoppingCart shoppingCart) {
        shoppingCart.recalculate();
        setCheckoutOptions(shoppingCart);
    }

    /**
     * Mark shopping cart dirty and thus eligible for persistence.
     *
     * @param shoppingCart current cart
     */
    protected void markDirty(final MutableShoppingCart shoppingCart) {
        shoppingCart.markDirty();
    }

    /**
     * Set customer tax options.
     *
     * @param cart             current cart
     * @param showTaxOption    optional flag to show tax or not
     * @param showNetOption    optional flag to display net or gross prices
     * @param showAmountOption optional flag to show amount ot percent of tax
     */
    protected void setTaxOptions(final MutableShoppingCart cart,
                                 final Boolean showTaxOption,
                                 final Boolean showNetOption,
                                 final Boolean showAmountOption) {

        this.configurationProvider.provide("TAX").visit(cart, showTaxOption, showNetOption, showAmountOption);

    }

    /**
     * Set default customer information that contributes to order info object.
     *
     * @param cart             current cart
     */
    protected void setCustomerOptions(final MutableShoppingCart cart) {

        this.configurationProvider.provide("CUSTOMERTYPE").visit(cart);
        this.configurationProvider.provide("CUSTOMER").visit(cart);
        this.configurationProvider.provide("CHECKOUT").visit(cart);

    }

    /**
     * Set default customer information that contributes to order info object.
     *
     * @param cart             current cart
     */
    protected void setCheckoutOptions(final MutableShoppingCart cart) {

        this.configurationProvider.provide("CHECKOUT").visit(cart);

    }

    /**
     * Set default customer address information if possible.
     *
     * Since customers could have defaults set to country which is not supported by shop billing/delivery address
     * a check is made for compatibility first.
     *
     * @param cart             current cart
     */
    protected void setDefaultAddressesIfPossible(final MutableShoppingCart cart) {

        this.configurationProvider.provide("DEFAULTADDRESS").visit(cart);

    }



    /** {@inheritDoc} */
    public int getPriority() {
        return priority;
    }

    /**
     * IoC priority for command.
     *
     * @param priority see {@link #getPriority()}
     */
    public void setPriority(final int priority) {
        this.priority = priority;
    }

}
