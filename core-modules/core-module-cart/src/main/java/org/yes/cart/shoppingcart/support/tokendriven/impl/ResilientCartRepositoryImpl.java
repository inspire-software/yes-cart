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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.StaleStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.ConcurrencyFailureException;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShoppingCartState;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.ShoppingCartStateService;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.support.tokendriven.CartRepository;
import org.yes.cart.shoppingcart.support.tokendriven.CartUpdateProcessor;
import org.yes.cart.shoppingcart.support.tokendriven.ShoppingCartStateSerDes;
import org.yes.cart.utils.ShopCodeContext;

/**
 * User: denispavlov
 * Date: 21/08/2014
 * Time: 20:11
 */
public class ResilientCartRepositoryImpl implements CartRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ResilientCartRepositoryImpl.class);

    private final Cache CART_CACHE;

    private final int sessionExpiryInSeconds;
    private final ShoppingCartStateService shoppingCartStateService;
    private final ShopService shopService;
    private final CartUpdateProcessor cartUpdateProcessor;
    private final ShoppingCartStateSerDes shoppingCartStateSerDes;
    private final TaskExecutor taskExecutor;

    public ResilientCartRepositoryImpl(final ShoppingCartStateService shoppingCartStateService,
                                       final ShopService shopService,
                                       final CartUpdateProcessor cartUpdateProcessor,
                                       final ShoppingCartStateSerDes shoppingCartStateSerDes,
                                       final int sessionExpiryInSeconds,
                                       final CacheManager cacheManager,
                                       final TaskExecutor taskExecutor) {

        this.shoppingCartStateService = shoppingCartStateService;
        this.shopService = shopService;
        this.cartUpdateProcessor = cartUpdateProcessor;
        this.shoppingCartStateSerDes = shoppingCartStateSerDes;
        this.sessionExpiryInSeconds = sessionExpiryInSeconds;
        this.taskExecutor = taskExecutor;
        CART_CACHE = cacheManager.getCache("web.shoppingCart");

    }

    public ResilientCartRepositoryImpl(final ShoppingCartStateService shoppingCartStateService,
                                       final ShopService shopService,
                                       final CartUpdateProcessor cartUpdateProcessor,
                                       final ShoppingCartStateSerDes shoppingCartStateSerDes,
                                       final int sessionExpiryInSeconds,
                                       final CacheManager cacheManager) {

        this(shoppingCartStateService, shopService, cartUpdateProcessor, shoppingCartStateSerDes, sessionExpiryInSeconds, cacheManager, null);
    }

    /** {@inheritDoc} */
    @Override
    public ShoppingCart createCart() {

        return shoppingCartStateSerDes.createState();

    }

    /** {@inheritDoc} */
    @Override
    public ShoppingCart getShoppingCart(final String token) {

        if (StringUtils.isBlank(token)) {
            return null;
        }

        // Try cache
        final ShoppingCart cachedCart = getFromCache(token);
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
                    cartUpdateProcessor.invalidateShoppingCart(cachedCart);
                    storeAsynchronously(cachedCart);
                    return cachedCart;
                } else {
                    // Otherwise if we cannot invalidate - remove from cache and try db
                    evictFromCache(token);
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
                        cartUpdateProcessor.invalidateShoppingCart(dbCart);
                        storeAsynchronously(dbCart);
                    } else {
                        // Otherwise if we cannot invalidate - remove the whole cart
                        evictFromCache(dbCart.getGuid());
                        return null;
                    }

                }
                // Update cache
                putToCache(dbCart.getGuid(), dbCart);
                return dbCart;

            }

        }

        return null;
    }

    private boolean determineIfLoginInvalidationRequired(final ShoppingCartState state, final long shopId) {

        if (state.getUpdatedTimestamp() != null) {
            return determineIfLoginInvalidationRequired(state.getUpdatedTimestamp().toEpochMilli(), shopId);
        }
        return determineIfLoginInvalidationRequired(state.getCreatedTimestamp().toEpochMilli(), shopId);

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

    private ShoppingCart getFromCache(final String token) {

        final Cache.ValueWrapper wrapper = CART_CACHE.get(token);

        if (wrapper != null) {
            return shoppingCartStateSerDes.restoreState((byte[]) wrapper.get());
        }
        return null;

    }

    private void putToCache(final String token, final ShoppingCart cart) {

        CART_CACHE.put(token, shoppingCartStateSerDes.saveState(cart));

    }

    private void evictFromCache(final String token) {

        CART_CACHE.evict(token);

    }

    /** {@inheritDoc} */
    @Override
    public String storeShoppingCart(final ShoppingCart shoppingCart) {

        if (shoppingCart.isModified()) {

            putToCache(shoppingCart.getGuid(), shoppingCart);

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
        return () -> {

            if (sccCode != null) {
                ShopCodeContext.setShopCode(sccCode);
                ShopCodeContext.setShopId(sccId);
            }

            try {

                // Update process potentially can merge the cart with other stored states (e.g. when user logs in)
                cartUpdateProcessor.updateShoppingCart(shoppingCart);
                // So we re-save it in cache
                putToCache(shoppingCart.getGuid(), shoppingCart);

            } catch (ConcurrencyFailureException cexp) {

                LOG.warn("Unable to persist cart state for {}, caused by concurrency update", shoppingCart.getGuid());

            } catch (StaleStateException sexp) {

                LOG.warn("Unable to persist cart state for {}, caused by stale state", shoppingCart.getGuid());

            } catch (Exception exp) {

                LOG.error("Unable to persist cart state for " + shoppingCart.getGuid(), exp);

            } finally {
                if (sccCode != null) {
                    ShopCodeContext.clear();
                }
            }

        };
    }

    /** {@inheritDoc} */
    @Override
    public void evictShoppingCart(final ShoppingCart shoppingCart) {

        evictFromCache(shoppingCart.getGuid());
        final ShoppingCartState state = shoppingCartStateService.findByGuid(shoppingCart.getGuid());
        if (state != null) {
            shoppingCartStateService.delete(state);
        }

    }

}
