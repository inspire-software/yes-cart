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

package org.yes.cart.shoppingcart.support.tokendriven.impl;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.impl.*;
import org.yes.cart.shoppingcart.support.tokendriven.ShoppingCartStateSerializer;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * User: denispavlov
 * Date: 21/04/2015
 * Time: 10:10
 */
public class ShoppingCartStateSerializerJacksonImpl implements ShoppingCartStateSerializer {

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartStateSerializerJacksonImpl.class);

    private final ObjectMapper mapper;

    public ShoppingCartStateSerializerJacksonImpl() {
        mapper = new ObjectMapper();

        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

        SimpleModule module = new SimpleModule("cart", new Version(2, 3, 0, null));
        module.addAbstractTypeMapping(Total.class, TotalImpl.class);
        module.addAbstractTypeMapping(MutableShoppingContext.class, ShoppingContextImpl.class);
        module.addAbstractTypeMapping(MutableOrderInfo.class, OrderInfoImpl.class);
        module.addAbstractTypeMapping(CartItem.class, CartItemImpl.class);

        mapper.registerModule(module);
    }

    /** {@inheritDoc} */
    @Override
    public ShoppingCart restoreState(final byte[] bytes) {

        try {
            return mapper.readValue(bytes, ShoppingCartImpl.class);
        } catch (IOException exception) {
            final String errMsg = "Unable to convert bytes assembled from tuple into object";
            LOG.error(errMsg, exception);
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
                    MessageFormat.format("Unable to serialize object {0}", shoppingCart),
                    ioe
            );
        }

        return null;
    }
}
