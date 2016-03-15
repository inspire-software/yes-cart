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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
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
import org.yes.cart.shoppingcart.ShoppingContext;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;
import org.yes.cart.shoppingcart.support.tokendriven.CartUpdateProcessor;
import org.yes.cart.shoppingcart.support.tokendriven.impl.ResilientCartRepositoryImpl;

import java.io.*;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 22/08/2014
 * Time: 14:22
 */
public class ResilientCartRepositoryImplTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testGetShoppingCartNullOrEmptyToken() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor);

        assertNull(repo.getShoppingCart(null));
        assertNull(repo.getShoppingCart("  "));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromCacheLoggedInActive() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final Cache.ValueWrapper wrapper = context.mock(Cache.ValueWrapper.class, "wrapper");
        final ShoppingCart cachedCart = context.mock(ShoppingCart.class, "cachedCart");
        final ShoppingContext cachedCartCtx = context.mock(ShoppingContext.class, "cachedCartCtx");
        final Shop shop = context.mock(Shop.class, "shop");

        final Date modified = new Date();

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-CACHE"); will(returnValue(wrapper));
            one(wrapper).get(); will(returnValue(cachedCart));
            allowing(cachedCart).getLogonState(); will(returnValue(ShoppingCart.LOGGED_IN));
            allowing(cachedCart).getModifiedTimestamp(); will(returnValue(modified.getTime() - 118000L));
            allowing(cachedCart).getShoppingContext(); will(returnValue(cachedCartCtx));
            allowing(cachedCartCtx).getShopId(); will(returnValue(111L));
            one(shopService).getById(111L); will(returnValue(shop));
            one(shop).getAttributeValueByCode(AttributeNamesKeys.Shop.CART_SESSION_EXPIRY_SECONDS); will(returnValue("120"));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor);

        assertSame(cachedCart, repo.getShoppingCart("IN-CACHE"));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromCacheLoggedOutActive() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final Cache.ValueWrapper wrapper = context.mock(Cache.ValueWrapper.class, "wrapper");
        final ShoppingCart cachedCart = context.mock(ShoppingCart.class, "cachedCart");

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-CACHE"); will(returnValue(wrapper));
            one(wrapper).get(); will(returnValue(cachedCart));
            allowing(cachedCart).getLogonState(); will(returnValue(ShoppingCart.SESSION_EXPIRED));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor);

        assertSame(cachedCart, repo.getShoppingCart("IN-CACHE"));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromCacheLoggedInActiveNoSetting() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final Cache.ValueWrapper wrapper = context.mock(Cache.ValueWrapper.class, "wrapper");
        final ShoppingCart cachedCart = context.mock(ShoppingCart.class, "cachedCart");
        final ShoppingContext cachedCartCtx = context.mock(ShoppingContext.class, "cachedCartCtx");
        final Shop shop = context.mock(Shop.class, "shop");

        final Date modified = new Date();

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-CACHE"); will(returnValue(wrapper));
            one(wrapper).get(); will(returnValue(cachedCart));
            allowing(cachedCart).getLogonState(); will(returnValue(ShoppingCart.LOGGED_IN));
            allowing(cachedCart).getModifiedTimestamp(); will(returnValue(modified.getTime() - 58000L));
            allowing(cachedCart).getShoppingContext(); will(returnValue(cachedCartCtx));
            allowing(cachedCartCtx).getShopId(); will(returnValue(111L));
            one(shopService).getById(111L); will(returnValue(shop));
            one(shop).getAttributeValueByCode(AttributeNamesKeys.Shop.CART_SESSION_EXPIRY_SECONDS); will(returnValue(null));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor);

        assertSame(cachedCart, repo.getShoppingCart("IN-CACHE"));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromCacheLoggedOutActiveNoSetting() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final Cache.ValueWrapper wrapper = context.mock(Cache.ValueWrapper.class, "wrapper");
        final ShoppingCart cachedCart = context.mock(ShoppingCart.class, "cachedCart");

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-CACHE"); will(returnValue(wrapper));
            one(wrapper).get(); will(returnValue(cachedCart));
            allowing(cachedCart).getLogonState(); will(returnValue(ShoppingCart.SESSION_EXPIRED));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor);

        assertSame(cachedCart, repo.getShoppingCart("IN-CACHE"));

        context.assertIsSatisfied();

    }


    @Test
    public void testGetShoppingCartFromCacheLoggedInInactive() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final Cache.ValueWrapper wrapper = context.mock(Cache.ValueWrapper.class, "wrapper");
        final ShoppingCart cachedCart = context.mock(ShoppingCart.class, "cachedCart");
        final ShoppingContext cachedCartCtx = context.mock(ShoppingContext.class, "cachedCartCtx");
        final Shop shop = context.mock(Shop.class, "shop");

        final Date modified = new Date();

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-CACHE"); will(returnValue(wrapper));
            one(wrapper).get(); will(returnValue(cachedCart));
            allowing(cachedCart).getLogonState(); will(returnValue(ShoppingCart.LOGGED_IN));
            allowing(cachedCart).getModifiedTimestamp(); will(returnValue(modified.getTime() - 121000L));
            allowing(cachedCart).getShoppingContext(); will(returnValue(cachedCartCtx));
            allowing(cachedCartCtx).getShopId(); will(returnValue(111L));
            one(shopService).getById(111L); will(returnValue(shop));
            one(shop).getAttributeValueByCode(AttributeNamesKeys.Shop.CART_SESSION_EXPIRY_SECONDS); will(returnValue("120"));
            one(cartCache).evict("IN-CACHE");  // Important to evict this state from cache if it is expired
            one(shoppingCartStateService).findByGuid("IN-CACHE"); will(returnValue(null)); // Just return null for this case
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor);

        assertNull(repo.getShoppingCart("IN-CACHE"));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromCacheLoggedOutInactive() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final Cache.ValueWrapper wrapper = context.mock(Cache.ValueWrapper.class, "wrapper");
        final ShoppingCart cachedCart = context.mock(ShoppingCart.class, "cachedCart");

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-CACHE"); will(returnValue(wrapper));
            one(wrapper).get(); will(returnValue(cachedCart));
            allowing(cachedCart).getLogonState(); will(returnValue(ShoppingCart.SESSION_EXPIRED));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor);

        assertEquals(cachedCart, repo.getShoppingCart("IN-CACHE"));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromCacheLoggedInInactiveMutable() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final Cache.ValueWrapper wrapper = context.mock(Cache.ValueWrapper.class, "wrapper");

        final Date modified = new Date();
        final ShoppingCartImpl cachedCart = new ShoppingCartImpl() {
            @Override
            public long getModifiedTimestamp() {
                return modified.getTime() - 121000L;
            }

            @Override
            public int getLogonState() {
                return ShoppingCart.LOGGED_IN;
            }
        };
        cachedCart.getShoppingContext().setCustomerEmail("bob@doe.com");
        cachedCart.getShoppingContext().setShopId(111L);
        final Shop shop = context.mock(Shop.class, "shop");

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-CACHE"); will(returnValue(wrapper));
            one(wrapper).get(); will(returnValue(cachedCart));
            one(shopService).getById(111L); will(returnValue(shop));
            one(shop).getAttributeValueByCode(AttributeNamesKeys.Shop.CART_SESSION_EXPIRY_SECONDS); will(returnValue("120"));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor) {
            @Override
            void storeAsynchronously(final ShoppingCart shoppingCart) {
                // If this cache is stale just update the state
                assertEquals(cachedCart.getGuid(), shoppingCart.getGuid());
                assertNull(shoppingCart.getCustomerEmail());
            }
        };

        assertEquals(cachedCart, repo.getShoppingCart("IN-CACHE"));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromCacheLoggedOutInactiveMutable() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final Cache.ValueWrapper wrapper = context.mock(Cache.ValueWrapper.class, "wrapper");

        final Date modified = new Date();
        final ShoppingCartImpl cachedCart = new ShoppingCartImpl() {
            @Override
            public long getModifiedTimestamp() {
                return modified.getTime() - 121000L;
            }

            @Override
            public int getLogonState() {
                return ShoppingCart.SESSION_EXPIRED;
            }
        };
        cachedCart.getShoppingContext().setCustomerEmail("bob@doe.com");
        cachedCart.getShoppingContext().setShopId(111L);

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-CACHE"); will(returnValue(wrapper));
            one(wrapper).get(); will(returnValue(cachedCart));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor) {
            @Override
            void storeAsynchronously(final ShoppingCart shoppingCart) {
                // If this cache is stale just update the state
                assertEquals(cachedCart.getGuid(), shoppingCart.getGuid());
                assertNull(shoppingCart.getCustomerEmail());
            }
        };

        assertEquals(cachedCart, repo.getShoppingCart("IN-CACHE"));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromCacheLoggedInInactiveNoSetting() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final Cache.ValueWrapper wrapper = context.mock(Cache.ValueWrapper.class, "wrapper");
        final ShoppingCart cachedCart = context.mock(ShoppingCart.class, "cachedCart");
        final ShoppingContext cachedCartCtx = context.mock(ShoppingContext.class, "cachedCartCtx");
        final Shop shop = context.mock(Shop.class, "shop");

        final Date modified = new Date();

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-CACHE"); will(returnValue(wrapper));
            one(wrapper).get(); will(returnValue(cachedCart));
            allowing(cachedCart).getLogonState(); will(returnValue(ShoppingCart.LOGGED_IN));
            allowing(cachedCart).getModifiedTimestamp(); will(returnValue(modified.getTime() - 61000L));
            allowing(cachedCart).getShoppingContext(); will(returnValue(cachedCartCtx));
            allowing(cachedCartCtx).getShopId(); will(returnValue(111L));
            one(shopService).getById(111L); will(returnValue(shop));
            one(shop).getAttributeValueByCode(AttributeNamesKeys.Shop.CART_SESSION_EXPIRY_SECONDS); will(returnValue(null));
            one(cartCache).evict("IN-CACHE");  // Important to evict this state from cache if it is expired
            one(shoppingCartStateService).findByGuid("IN-CACHE"); will(returnValue(null)); // Just return null for this case
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor);

        assertNull(repo.getShoppingCart("IN-CACHE"));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromCacheLoggedOutInactiveNoSetting() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final Cache.ValueWrapper wrapper = context.mock(Cache.ValueWrapper.class, "wrapper");
        final ShoppingCart cachedCart = context.mock(ShoppingCart.class, "cachedCart");

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-CACHE"); will(returnValue(wrapper));
            one(wrapper).get(); will(returnValue(cachedCart));
            allowing(cachedCart).getLogonState(); will(returnValue(ShoppingCart.SESSION_EXPIRED));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor);

        assertEquals(cachedCart, repo.getShoppingCart("IN-CACHE"));

        context.assertIsSatisfied();

    }


    @Test
    public void testGetShoppingCartNotInCacheNotInDb() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("MISSING"); will(returnValue(null));
            one(shoppingCartStateService).findByGuid("MISSING"); will(returnValue(null));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor);

        assertNull(repo.getShoppingCart("MISSING"));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromDbLoggedInActive() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final ShoppingCartState cartState = context.mock(ShoppingCartState.class, "cartState");
        final Shop shop = context.mock(Shop.class, "shop");

        final MutableShoppingCart cart = new ShoppingCartImpl();
        cart.getShoppingContext().setCustomerEmail("bob@doe.com");
        cart.getShoppingContext().setCustomerName("bob@doe.com");
        cart.getShoppingContext().setCustomerShops(Arrays.asList("SHOP10"));
        cart.getShoppingContext().setShopId(111L);
        cart.getShoppingContext().setShopCode("SHOP10");
        assertEquals(cart.getLogonState(), ShoppingCart.LOGGED_IN);
        final byte[] bytes = serializeCart(cart);
        final ShoppingCart restored = deserializeCart(bytes);

        final Date stillActive = new Date(System.currentTimeMillis() - 118000L);

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-DB-ACTIVE"); will(returnValue(null));
            one(shoppingCartStateService).findByGuid("IN-DB-ACTIVE"); will(returnValue(cartState));
            allowing(cartState).getUpdatedTimestamp(); will(returnValue(stillActive));
            one(cartState).getState(); will(returnValue(bytes));
            one(cartUpdateProcessor).restoreState(bytes); will(returnValue(restored));
            one(shopService).getById(111L); will(returnValue(shop));
            one(shop).getAttributeValueByCode(AttributeNamesKeys.Shop.CART_SESSION_EXPIRY_SECONDS); will(returnValue("120"));
            one(cartCache).put(with(equal(cart.getGuid())), with(any(ShoppingCart.class)));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor);

        final ShoppingCart newCart = repo.getShoppingCart("IN-DB-ACTIVE");
        assertNotNull(newCart);
        assertEquals(newCart.getGuid(), cart.getGuid());
        assertEquals(newCart.getCustomerEmail(), cart.getCustomerEmail());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromDbLoggedOutActive() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final ShoppingCartState cartState = context.mock(ShoppingCartState.class, "cartState");

        final MutableShoppingCart cart = new ShoppingCartImpl();
        cart.getShoppingContext().setCustomerEmail(null);
        cart.getShoppingContext().setCustomerName("bob@doe.com");
        cart.getShoppingContext().setCustomerShops(Arrays.asList("SHOP10"));
        cart.getShoppingContext().setShopId(111L);
        cart.getShoppingContext().setShopCode("SHOP10");
        cart.getShoppingContext().setShopId(111L);
        assertEquals(cart.getLogonState(), ShoppingCart.SESSION_EXPIRED);
        final byte[] bytes = serializeCart(cart);
        final ShoppingCart restored = deserializeCart(bytes);

        final Date stillActive = new Date(System.currentTimeMillis() - 118000L);

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-DB-ACTIVE"); will(returnValue(null));
            one(shoppingCartStateService).findByGuid("IN-DB-ACTIVE"); will(returnValue(cartState));
            allowing(cartState).getUpdatedTimestamp(); will(returnValue(stillActive));
            one(cartState).getState(); will(returnValue(bytes));
            one(cartUpdateProcessor).restoreState(bytes); will(returnValue(restored));
            one(cartCache).put(with(equal(cart.getGuid())), with(any(ShoppingCart.class)));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor);

        final ShoppingCart newCart = repo.getShoppingCart("IN-DB-ACTIVE");
        assertNotNull(newCart);
        assertEquals(newCart.getGuid(), cart.getGuid());
        assertEquals(newCart.getCustomerEmail(), cart.getCustomerEmail());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromDbLoggedInActiveNoSetting() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final ShoppingCartState cartState = context.mock(ShoppingCartState.class, "cartState");
        final Shop shop = context.mock(Shop.class, "shop");

        final MutableShoppingCart cart = new ShoppingCartImpl();
        cart.getShoppingContext().setCustomerEmail("bob@doe.com");
        cart.getShoppingContext().setCustomerName("bob@doe.com");
        cart.getShoppingContext().setCustomerShops(Arrays.asList("SHOP10"));
        cart.getShoppingContext().setShopId(111L);
        cart.getShoppingContext().setShopCode("SHOP10");
        assertEquals(cart.getLogonState(), ShoppingCart.LOGGED_IN);
        final byte[] bytes = serializeCart(cart);
        final ShoppingCart restored = deserializeCart(bytes);

        final Date stillActive = new Date(System.currentTimeMillis() - 58000L);

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-DB-ACTIVE"); will(returnValue(null));
            one(shoppingCartStateService).findByGuid("IN-DB-ACTIVE"); will(returnValue(cartState));
            allowing(cartState).getUpdatedTimestamp(); will(returnValue(stillActive));
            one(cartState).getState(); will(returnValue(bytes));
            one(cartUpdateProcessor).restoreState(bytes); will(returnValue(restored));
            one(shopService).getById(111L); will(returnValue(shop));
            one(shop).getAttributeValueByCode(AttributeNamesKeys.Shop.CART_SESSION_EXPIRY_SECONDS); will(returnValue(null));
            one(cartCache).put(with(equal(cart.getGuid())), with(any(ShoppingCart.class)));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor);

        final ShoppingCart newCart = repo.getShoppingCart("IN-DB-ACTIVE");
        assertNotNull(newCart);
        assertEquals(newCart.getGuid(), cart.getGuid());
        assertEquals(newCart.getCustomerEmail(), cart.getCustomerEmail());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromDbLoggedOutActiveNoSetting() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final ShoppingCartState cartState = context.mock(ShoppingCartState.class, "cartState");

        final MutableShoppingCart cart = new ShoppingCartImpl();
        cart.getShoppingContext().setCustomerEmail(null);
        cart.getShoppingContext().setCustomerName("bob@doe.com");
        cart.getShoppingContext().setCustomerShops(Arrays.asList("SHOP10"));
        cart.getShoppingContext().setShopId(111L);
        cart.getShoppingContext().setShopCode("SHOP10");
        assertEquals(cart.getLogonState(), ShoppingCart.SESSION_EXPIRED);
        final byte[] bytes = serializeCart(cart);
        final ShoppingCart restored = deserializeCart(bytes);

        final Date stillActive = new Date(System.currentTimeMillis() - 58000L);

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-DB-ACTIVE"); will(returnValue(null));
            one(shoppingCartStateService).findByGuid("IN-DB-ACTIVE"); will(returnValue(cartState));
            allowing(cartState).getUpdatedTimestamp(); will(returnValue(stillActive));
            one(cartState).getState(); will(returnValue(bytes));
            one(cartUpdateProcessor).restoreState(bytes); will(returnValue(restored));
            one(cartCache).put(with(equal(cart.getGuid())), with(any(ShoppingCart.class)));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor);

        final ShoppingCart newCart = repo.getShoppingCart("IN-DB-ACTIVE");
        assertNotNull(newCart);
        assertEquals(newCart.getGuid(), cart.getGuid());
        assertEquals(newCart.getCustomerEmail(), cart.getCustomerEmail());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromDbLoggedInInactive() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final ShoppingCartState cartState = context.mock(ShoppingCartState.class, "cartState");
        final Shop shop = context.mock(Shop.class, "shop");

        final MutableShoppingCart cart = new ShoppingCartImpl();
        cart.getShoppingContext().setCustomerEmail("bob@doe.com");
        cart.getShoppingContext().setCustomerName("bob@doe.com");
        cart.getShoppingContext().setCustomerShops(Arrays.asList("SHOP10"));
        cart.getShoppingContext().setShopId(111L);
        cart.getShoppingContext().setShopCode("SHOP10");
        assertEquals(cart.getLogonState(), ShoppingCart.LOGGED_IN);
        final byte[] bytes = serializeCart(cart);
        final ShoppingCart restored = deserializeCart(bytes);

        final Date stillActive = new Date(System.currentTimeMillis() - 121000L);

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-DB-INACTIVE"); will(returnValue(null));
            one(shoppingCartStateService).findByGuid("IN-DB-INACTIVE"); will(returnValue(cartState));
            allowing(cartState).getUpdatedTimestamp(); will(returnValue(stillActive));
            one(cartState).getState(); will(returnValue(bytes));
            one(cartUpdateProcessor).restoreState(bytes); will(returnValue(restored));
            one(shopService).getById(111L); will(returnValue(shop));
            one(shop).getAttributeValueByCode(AttributeNamesKeys.Shop.CART_SESSION_EXPIRY_SECONDS); will(returnValue("120"));
            one(cartCache).put(with(equal(cart.getGuid())), with(any(ShoppingCart.class)));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor) {
            @Override
            void storeAsynchronously(final ShoppingCart shoppingCart) {
                assertEquals(cart.getGuid(), shoppingCart.getGuid());
                assertNull(shoppingCart.getCustomerEmail());
            }
        };

        final ShoppingCart newCart = repo.getShoppingCart("IN-DB-INACTIVE");
        assertNotNull(newCart);
        assertEquals(newCart.getGuid(), cart.getGuid());
        assertNull(newCart.getCustomerEmail());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromDbLoggedOutInactive() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final ShoppingCartState cartState = context.mock(ShoppingCartState.class, "cartState");

        final MutableShoppingCart cart = new ShoppingCartImpl();
        cart.getShoppingContext().setCustomerEmail(null);
        cart.getShoppingContext().setCustomerName("bob@doe.com");
        cart.getShoppingContext().setCustomerShops(Arrays.asList("SHOP10"));
        cart.getShoppingContext().setShopId(111L);
        cart.getShoppingContext().setShopCode("SHOP10");
        assertEquals(cart.getLogonState(), ShoppingCart.SESSION_EXPIRED);
        final byte[] bytes = serializeCart(cart);
        final ShoppingCart restored = deserializeCart(bytes);

        final Date stillActive = new Date(System.currentTimeMillis() - 121000L);

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-DB-INACTIVE"); will(returnValue(null));
            one(shoppingCartStateService).findByGuid("IN-DB-INACTIVE"); will(returnValue(cartState));
            allowing(cartState).getUpdatedTimestamp(); will(returnValue(stillActive));
            one(cartState).getState(); will(returnValue(bytes));
            one(cartUpdateProcessor).restoreState(bytes); will(returnValue(restored));
            one(cartCache).put(with(equal(cart.getGuid())), with(any(ShoppingCart.class)));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor) {
            @Override
            void storeAsynchronously(final ShoppingCart shoppingCart) {
                assertEquals(cart.getGuid(), shoppingCart.getGuid());
                assertNull(shoppingCart.getCustomerEmail());
            }
        };

        final ShoppingCart newCart = repo.getShoppingCart("IN-DB-INACTIVE");
        assertNotNull(newCart);
        assertEquals(newCart.getGuid(), cart.getGuid());
        assertNull(newCart.getCustomerEmail());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromDbLoggedInInactiveNoSetting() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final ShoppingCartState cartState = context.mock(ShoppingCartState.class, "cartState");
        final Shop shop = context.mock(Shop.class, "shop");

        final MutableShoppingCart cart = new ShoppingCartImpl();
        cart.getShoppingContext().setCustomerEmail("bob@doe.com");
        cart.getShoppingContext().setCustomerName("bob@doe.com");
        cart.getShoppingContext().setCustomerShops(Arrays.asList("SHOP10"));
        cart.getShoppingContext().setShopId(111L);
        cart.getShoppingContext().setShopCode("SHOP10");
        assertEquals(cart.getLogonState(), ShoppingCart.LOGGED_IN);
        final byte[] bytes = serializeCart(cart);
        final ShoppingCart restored = deserializeCart(bytes);

        final Date stillActive = new Date(System.currentTimeMillis() - 61000L);

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-DB-INACTIVE"); will(returnValue(null));
            one(shoppingCartStateService).findByGuid("IN-DB-INACTIVE"); will(returnValue(cartState));
            allowing(cartState).getUpdatedTimestamp(); will(returnValue(stillActive));
            one(cartState).getState(); will(returnValue(bytes));
            one(cartUpdateProcessor).restoreState(bytes); will(returnValue(restored));
            one(shopService).getById(111L); will(returnValue(shop));
            one(shop).getAttributeValueByCode(AttributeNamesKeys.Shop.CART_SESSION_EXPIRY_SECONDS); will(returnValue(null));
            one(cartCache).put(with(equal(cart.getGuid())), with(any(ShoppingCart.class)));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor) {
            @Override
            void storeAsynchronously(final ShoppingCart shoppingCart) {
                assertEquals(cart.getGuid(), shoppingCart.getGuid());
                assertNull(shoppingCart.getCustomerEmail());
            }
        };

        final ShoppingCart newCart = repo.getShoppingCart("IN-DB-INACTIVE");
        assertNotNull(newCart);
        assertEquals(newCart.getGuid(), cart.getGuid());
        assertNull(newCart.getCustomerEmail());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromDbLoggedOutInactiveNoSetting() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final ShoppingCartState cartState = context.mock(ShoppingCartState.class, "cartState");
        final Shop shop = context.mock(Shop.class, "shop");

        final MutableShoppingCart cart = new ShoppingCartImpl();
        cart.getShoppingContext().setCustomerEmail(null);
        cart.getShoppingContext().setCustomerName("bob@doe.com");
        cart.getShoppingContext().setCustomerShops(Arrays.asList("SHOP10"));
        cart.getShoppingContext().setShopId(111L);
        cart.getShoppingContext().setShopCode("SHOP10");
        assertEquals(cart.getLogonState(), ShoppingCart.SESSION_EXPIRED);
        final byte[] bytes = serializeCart(cart);
        final ShoppingCart restored = deserializeCart(bytes);

        final Date stillActive = new Date(System.currentTimeMillis() - 61000L);

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-DB-INACTIVE"); will(returnValue(null));
            one(shoppingCartStateService).findByGuid("IN-DB-INACTIVE"); will(returnValue(cartState));
            allowing(cartState).getUpdatedTimestamp(); will(returnValue(stillActive));
            one(cartState).getState(); will(returnValue(bytes));
            one(cartUpdateProcessor).restoreState(bytes); will(returnValue(restored));
            one(cartCache).put(with(equal(cart.getGuid())), with(any(ShoppingCart.class)));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor) {
            @Override
            void storeAsynchronously(final ShoppingCart shoppingCart) {
                assertEquals(cart.getGuid(), shoppingCart.getGuid());
                assertNull(shoppingCart.getCustomerEmail());
            }
        };

        final ShoppingCart newCart = repo.getShoppingCart("IN-DB-INACTIVE");
        assertNotNull(newCart);
        assertEquals(newCart.getGuid(), cart.getGuid());
        assertNull(newCart.getCustomerEmail());

        context.assertIsSatisfied();

    }


    private byte[] serializeCart(final ShoppingCart cart) {

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ObjectOutputStream objectOutputStream = null;
        try {

            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(cart);
            objectOutputStream.flush();
            objectOutputStream.close();

        } catch (Throwable ioe) {
            fail("unable to serialize");
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                byteArrayOutputStream.close();
            } catch (IOException e) {

            }
        }
        return byteArrayOutputStream.toByteArray();
    }


    private ShoppingCart deserializeCart(final byte[] bytes) {


        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = null;
        try {

            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            final ShoppingCart shoppingCart = (ShoppingCart) objectInputStream.readObject();
            return shoppingCart;

        } catch (Exception exception) {
            return null;
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                byteArrayInputStream.close();
            } catch (IOException ioe) { // leave this one silent as we have the object.
            }

        }

    }

    @Test
    public void testStoreShoppingCartNonModified() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");

        final MutableShoppingCart cart = new ShoppingCartImpl();
        cart.getShoppingContext().setCustomerEmail("bob@doe.com");

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor) {
            @Override
            void storeAsynchronously(final ShoppingCart shoppingCart) {
                fail();
            }
        };

        assertFalse(cart.isModified());
        assertEquals(cart.getGuid(), repo.storeShoppingCart(cart));

        context.assertIsSatisfied();


    }

    @Test
    public void testStoreShoppingCartIsModified() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");

        final MutableShoppingCart cart = new ShoppingCartImpl();
        cart.getShoppingContext().setCustomerEmail("bob@doe.com");

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).put(with(equal(cart.getGuid())), with(any(ShoppingCart.class)));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, shopService, cartUpdateProcessor, 60, cacheManager, taskExecutor) {
            @Override
            void storeAsynchronously(final ShoppingCart shoppingCart) {
                assertSame(cart, shoppingCart);
            }
        };

        cart.markDirty();
        assertTrue(cart.isModified());
        assertEquals(cart.getGuid(), repo.storeShoppingCart(cart));

        context.assertIsSatisfied();


    }

}
