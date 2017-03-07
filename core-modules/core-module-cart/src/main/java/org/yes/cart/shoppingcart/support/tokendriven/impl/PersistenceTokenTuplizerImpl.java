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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.support.CartDetuplizationException;
import org.yes.cart.shoppingcart.support.CartTuplizationException;
import org.yes.cart.shoppingcart.support.CartTuplizer;
import org.yes.cart.shoppingcart.support.tokendriven.CartRepository;
import org.yes.cart.util.ShopCodeContext;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 21/08/2014
 * Time: 23:14
 */
public class PersistenceTokenTuplizerImpl implements CartTuplizer<Map, Map> {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceTokenTuplizerImpl.class);

    private final TuplizerSetting tuplizerSetting;

    private final CartRepository cartRepository;


    /**
     * Default Constructor.
     *
     * @param cartRepository       cart repository
     * @param expirySeconds        seconds after which cookie expires
     */
    public PersistenceTokenTuplizerImpl(final CartRepository cartRepository,
                                        final int expirySeconds) {
        this.cartRepository = cartRepository;

        this.tuplizerSetting = new TuplizerSetting(expirySeconds);

        if (this.tuplizerSetting.expiry == null) {
            final String errMsg = "persistence tuplizer misconfiguration, expiry is null";
            LOG.error(errMsg);
            throw new RuntimeException(errMsg);
        }

    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final Map httpServletRequest) {
        return httpServletRequest != null && httpServletRequest.containsKey(tuplizerSetting.key);
    }

    /** {@inheritDoc} */
    @Override
    public ShoppingCart detuplize(final Map httpServletRequest) throws CartDetuplizationException {

        Object token = httpServletRequest.get(tuplizerSetting.key);
        if (token instanceof String) {

            return cartRepository.getShoppingCart((String) token);

        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void tuplize(final Map httpServletRequest, final Map httpServletResponse, final ShoppingCart shoppingCart) throws CartTuplizationException {

        if (httpServletResponse == null) {
            throw new RuntimeException("target must not be null");
        }
        final String token = this.cartRepository.storeShoppingCart(shoppingCart);
        httpServletResponse.put(tuplizerSetting.key, token);
        httpServletResponse.put(tuplizerSetting.key + ".expiry", tuplizerSetting.expiry);

    }


    /**
     * Convenience class for meta data of object.
     */
    private class TuplizerSetting {

        private final String key = "token";
        private final Integer expiry;

        TuplizerSetting(final Integer expiry) {
            this.expiry = expiry;
        }
    }


}
