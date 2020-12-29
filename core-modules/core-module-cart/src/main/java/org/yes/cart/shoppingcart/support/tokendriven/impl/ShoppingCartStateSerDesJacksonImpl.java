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

package org.yes.cart.shoppingcart.support.tokendriven.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.impl.*;
import org.yes.cart.shoppingcart.support.tokendriven.ShoppingCartStateSerDes;
import org.yes.cart.utils.impl.JsonAdapterUtils;

import java.io.IOException;

/**
 * User: denispavlov
 * Date: 21/04/2015
 * Time: 10:10
 */
public class ShoppingCartStateSerDesJacksonImpl implements ShoppingCartStateSerDes {

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartStateSerDesJacksonImpl.class);

    private final ObjectMapper mapper;

    private final AmountCalculationStrategy amountCalculationStrategy;

    public ShoppingCartStateSerDesJacksonImpl(final AmountCalculationStrategy amountCalculationStrategy) {

        mapper = new ObjectMapper();

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        SimpleModule module = new SimpleModule("cart", JsonAdapterUtils.VERSION);
        module.addAbstractTypeMapping(Total.class, TotalImpl.class);
        module.addAbstractTypeMapping(MutableShoppingContext.class, ShoppingContextImpl.class);
        module.addAbstractTypeMapping(MutableOrderInfo.class, OrderInfoImpl.class);
        module.addAbstractTypeMapping(CartItem.class, CartItemImpl.class);

        mapper.registerModule(module);

        this.amountCalculationStrategy = amountCalculationStrategy;

    }

    /** {@inheritDoc} */
    @Override
    public ShoppingCart createState() {
        final MutableShoppingCart cart = new ShoppingCartImpl();
        cart.initialise(amountCalculationStrategy);
        return cart;
    }

    /** {@inheritDoc} */
    @Override
    public ShoppingCart restoreState(final byte[] bytes) {

        try {
            final MutableShoppingCart cart = mapper.readValue(bytes, ShoppingCartImpl.class);
            cart.initialise(amountCalculationStrategy);
            return cart;
        } catch (IOException exception) {
            LOG.error("Unable to convert bytes assembled from tuple into object: " + exception.getMessage(), exception);
            return null;
        }

    }

    /** {@inheritDoc} */
    @Override
    public byte[] saveState(final ShoppingCart shoppingCart) {

        try {
            return mapper.writeValueAsBytes(shoppingCart);
        } catch (IOException ioe) {
            LOG.error(
                    "Unable to serialize object: " + shoppingCart,
                    ioe
            );
        }

        return null;
    }
}
