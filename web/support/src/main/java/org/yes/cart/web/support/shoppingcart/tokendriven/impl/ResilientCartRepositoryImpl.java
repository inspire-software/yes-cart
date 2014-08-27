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

package org.yes.cart.web.support.shoppingcart.tokendriven.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.domain.entity.ShoppingCartState;
import org.yes.cart.service.domain.ShoppingCartStateService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.shoppingcart.tokendriven.CartRepository;
import org.yes.cart.web.support.shoppingcart.tokendriven.CartUpdateProcessor;

/**
 * User: denispavlov
 * Date: 21/08/2014
 * Time: 20:11
 */
public class ResilientCartRepositoryImpl implements CartRepository {

    private final Cache CART_CACHE;

    private final int sessionExpiryInSeconds;
    private final ShoppingCartStateService shoppingCartStateService;
    private final CartUpdateProcessor cartUpdateProcessor;
    private final TaskExecutor taskExecutor;

    public ResilientCartRepositoryImpl(final ShoppingCartStateService shoppingCartStateService,
                                       final CartUpdateProcessor cartUpdateProcessor,
                                       final int sessionExpiryInSeconds,
                                       final CacheManager cacheManager,
                                       final TaskExecutor taskExecutor) {

        this.shoppingCartStateService = shoppingCartStateService;
        this.cartUpdateProcessor = cartUpdateProcessor;
        this.sessionExpiryInSeconds = sessionExpiryInSeconds;
        this.taskExecutor = taskExecutor;
        CART_CACHE = cacheManager.getCache("web.shoppingCart");

    }

    public ResilientCartRepositoryImpl(final ShoppingCartStateService shoppingCartStateService,
                                       final CartUpdateProcessor cartUpdateProcessor,
                                       final int sessionExpiryInSeconds,
                                       final CacheManager cacheManager) {

        this(shoppingCartStateService, cartUpdateProcessor, sessionExpiryInSeconds, cacheManager, null);
    }

    /** {@inheritDoc} */
    @Override
    public ShoppingCart getShoppingCart(final String token) {

        if (StringUtils.isBlank(token)) {
            return null;
        }

        final ShoppingCart cachedCart = getFromValueWrapper(CART_CACHE.get(token));
        if (cachedCart != null) {
            return cachedCart;
        }

        final ShoppingCartState state = shoppingCartStateService.findByGuid(token);
        if (state != null) {

            final ShoppingCart dbCart = cartUpdateProcessor.restoreState(state.getState());
            if (dbCart == null) {
                // invalid byte data - remove this
                shoppingCartStateService.delete(state);
            } else {

                final boolean invalidateLogin = determineIfLoginInvalidationRequired(state);
                if (invalidateLogin) {
                    dbCart.getShoppingContext().setCustomerEmail(null);
                    storeAsynchronously(dbCart);
                }
                CART_CACHE.put(dbCart.getGuid(), dbCart);
                return dbCart;

            }

        }

        return null;
    }

    private boolean determineIfLoginInvalidationRequired(final ShoppingCartState state) {
        final boolean invalidateLogin;
        if (state.getUpdatedTimestamp() != null) {
            invalidateLogin = state.getUpdatedTimestamp().getTime() + sessionExpiryInSeconds * 1000 < System.currentTimeMillis();
        } else {
            invalidateLogin = state.getCreatedTimestamp().getTime() + sessionExpiryInSeconds * 1000 < System.currentTimeMillis();
        }
        return invalidateLogin;
    }


    private ShoppingCart getFromValueWrapper(final Cache.ValueWrapper wrapper) {
        if (wrapper != null) {
            return (ShoppingCart) wrapper.get();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String storeShoppingCart(final ShoppingCart shoppingCart) {

        if (shoppingCart.isModified()) {

            CART_CACHE.put(shoppingCart.getGuid(), shoppingCart);

            storeAsynchronously(shoppingCart);

        }

        return shoppingCart.getGuid();

    }

    void storeAsynchronously(final ShoppingCart shoppingCart) {

        if (taskExecutor == null) {
            createRunnable(shoppingCart, null, null).run();
        } else {
            taskExecutor.execute(createRunnable(shoppingCart, ShopCodeContext.getShopCode(), ShopCodeContext.getShopId()));
        }
    }

    private Runnable createRunnable(final ShoppingCart shoppingCart, final String sccCode, final Long sccId) {
        return new Runnable() {
            @Override
            public void run() {

                if (sccCode != null) {
                    ShopCodeContext.setShopCode(sccCode);
                    ShopCodeContext.setShopId(sccId);
                }

                try {

                    // Update process potentially can merge the cart with other stored states (e.g. when user logs in)
                    cartUpdateProcessor.updateShoppingCart(shoppingCart);
                    // So we re-save it in cache
                    CART_CACHE.put(shoppingCart.getGuid(), shoppingCart);

                } finally {
                     ShopCodeContext.clear();
                }

            }
        };
    }

    /** {@inheritDoc} */
    @Override
    public void evictShoppingCart(final ShoppingCart shoppingCart) {

        CART_CACHE.evict(shoppingCart.getGuid());
        final ShoppingCartState state = shoppingCartStateService.findByGuid(shoppingCart.getGuid());
        if (state != null) {
            shoppingCartStateService.delete(state);
        }

    }

}
