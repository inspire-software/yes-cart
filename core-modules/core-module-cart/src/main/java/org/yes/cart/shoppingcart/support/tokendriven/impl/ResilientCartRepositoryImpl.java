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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShoppingCartState;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.ShoppingCartStateService;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.shoppingcart.support.tokendriven.CartRepository;
import org.yes.cart.shoppingcart.support.tokendriven.CartUpdateProcessor;

/**
 * User: denispavlov
 * Date: 21/08/2014
 * Time: 20:11
 */
public class ResilientCartRepositoryImpl implements CartRepository {

    private final Cache CART_CACHE;

    private final int sessionExpiryInSeconds;
    private final ShoppingCartStateService shoppingCartStateService;
    private final ShopService shopService;
    private final CartUpdateProcessor cartUpdateProcessor;
    private final TaskExecutor taskExecutor;

    public ResilientCartRepositoryImpl(final ShoppingCartStateService shoppingCartStateService,
                                       final ShopService shopService,
                                       final CartUpdateProcessor cartUpdateProcessor,
                                       final int sessionExpiryInSeconds,
                                       final CacheManager cacheManager,
                                       final TaskExecutor taskExecutor) {

        this.shoppingCartStateService = shoppingCartStateService;
        this.shopService = shopService;
        this.cartUpdateProcessor = cartUpdateProcessor;
        this.sessionExpiryInSeconds = sessionExpiryInSeconds;
        this.taskExecutor = taskExecutor;
        CART_CACHE = cacheManager.getCache("web.shoppingCart");

    }

    public ResilientCartRepositoryImpl(final ShoppingCartStateService shoppingCartStateService,
                                       final ShopService shopService,
                                       final CartUpdateProcessor cartUpdateProcessor,
                                       final int sessionExpiryInSeconds,
                                       final CacheManager cacheManager) {

        this(shoppingCartStateService, shopService, cartUpdateProcessor, sessionExpiryInSeconds, cacheManager, null);
    }

    /** {@inheritDoc} */
    @Override
    public ShoppingCart getShoppingCart(final String token) {

        if (StringUtils.isBlank(token)) {
            return null;
        }

        // Try cache
        final ShoppingCart cachedCart = getFromValueWrapper(CART_CACHE.get(token));
        if (cachedCart != null) {

            final boolean invalidateLogin =
                    cachedCart.getLogonState() == ShoppingCart.LOGGED_IN &&
                    determineIfLoginInvalidationRequired(
                        cachedCart.getModifiedTimestamp(),
                        cachedCart.getShoppingContext().getShopId()
                    );

            if (invalidateLogin) {

                if (cachedCart instanceof MutableShoppingCart) {
                    // if need to invalidate and it is mutable - then invalidate
                    invalidateLogin((MutableShoppingCart) cachedCart);
                    storeAsynchronously(cachedCart);
                    return cachedCart;
                } else {
                    // Otherwise if we cannot invalidate - remove from cache and try db
                    CART_CACHE.evict(token);
                }

            } else {
                // Cached cart is still good to use
                return cachedCart;

            }
        }

        // Try DB
        final ShoppingCartState state = shoppingCartStateService.findByGuid(token);
        if (state != null) {

            final ShoppingCart dbCart = cartUpdateProcessor.restoreState(state.getState());
            if (dbCart == null) {

                // invalid byte data - remove this
                shoppingCartStateService.delete(state);

            } else {

                final boolean invalidateLogin =
                        dbCart.getLogonState() == ShoppingCart.LOGGED_IN &&
                        determineIfLoginInvalidationRequired(
                            state,
                            dbCart.getShoppingContext().getShopId()
                        );

                if (invalidateLogin) {

                    if (dbCart instanceof MutableShoppingCart) {
                        // if need to invalidate and it is mutable - then invalidate
                        invalidateLogin((MutableShoppingCart) dbCart);
                        storeAsynchronously(dbCart);
                    } else {
                        // Otherwise if we cannot invalidate - remove the whole cart
                        CART_CACHE.evict(dbCart.getGuid());
                        return null;
                    }

                }
                // Update cache
                CART_CACHE.put(dbCart.getGuid(), dbCart);
                return dbCart;

            }

        }

        return null;
    }

    private boolean determineIfLoginInvalidationRequired(final ShoppingCartState state, final long shopId) {

        if (state.getUpdatedTimestamp() != null) {
            return determineIfLoginInvalidationRequired(state.getUpdatedTimestamp().getTime(), shopId);
        }
        return determineIfLoginInvalidationRequired(state.getCreatedTimestamp().getTime(), shopId);

    }

    private boolean determineIfLoginInvalidationRequired(final long lastModified, final long shopId) {

        int expiry = determineExpiryInSeconds(shopId);

        return lastModified + expiry * 1000 < System.currentTimeMillis();

    }

    private int determineExpiryInSeconds(final long shopId) {

        int expiry = this.sessionExpiryInSeconds;

        final Shop shop = shopService.getById(shopId);
        if (shop != null) {
            final String av = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.CART_SESSION_EXPIRY_SECONDS);
            if (StringUtils.isNotBlank(av)) {
                expiry = NumberUtils.toInt(av, this.sessionExpiryInSeconds);
            }
        }

        return expiry;

    }


    private ShoppingCart getFromValueWrapper(final Cache.ValueWrapper wrapper) {

        if (wrapper != null) {
            return (ShoppingCart) wrapper.get();
        }
        return null;

    }

    private void invalidateLogin(final MutableShoppingCart shoppingCart) {
        shoppingCart.getShoppingContext().setCustomerEmail(null);
        shoppingCart.getShoppingContext().setCustomerShopId(shoppingCart.getShoppingContext().getShopId());
        shoppingCart.getShoppingContext().setCustomerShopCode(shoppingCart.getShoppingContext().getShopCode());
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
            taskExecutor.execute(createRunnable(shoppingCart,
                    shoppingCart.getShoppingContext().getShopCode(),
                    shoppingCart.getShoppingContext().getShopId()));
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
                    if (sccCode != null) {
                        ShopCodeContext.clear();
                    }
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
