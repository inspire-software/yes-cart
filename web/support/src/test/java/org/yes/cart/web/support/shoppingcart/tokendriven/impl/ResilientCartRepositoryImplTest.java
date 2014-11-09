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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.domain.entity.ShoppingCartState;
import org.yes.cart.service.domain.ShoppingCartStateService;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;
import org.yes.cart.web.support.shoppingcart.tokendriven.CartUpdateProcessor;

import java.io.*;
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
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, cartUpdateProcessor, 60, cacheManager, taskExecutor);

        assertNull(repo.getShoppingCart(null));
        assertNull(repo.getShoppingCart("  "));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromCache() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final Cache.ValueWrapper wrapper = context.mock(Cache.ValueWrapper.class, "wrapper");
        final ShoppingCart cachedCart = context.mock(ShoppingCart.class, "cachedCart");

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart");
            will(returnValue(cartCache));
            one(cartCache).get("IN-CACHE");
            will(returnValue(wrapper));
            one(wrapper).get();
            will(returnValue(cachedCart));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, cartUpdateProcessor, 60, cacheManager, taskExecutor);

        assertSame(cachedCart, repo.getShoppingCart("IN-CACHE"));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartNotInCacheNotInDb() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart");
            will(returnValue(cartCache));
            one(cartCache).get("MISSING");
            will(returnValue(null));
            one(shoppingCartStateService).findByGuid("MISSING");
            will(returnValue(null));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, cartUpdateProcessor, 60, cacheManager, taskExecutor);

        assertNull(repo.getShoppingCart("MISSING"));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromDbActive() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final ShoppingCartState cartState = context.mock(ShoppingCartState.class, "cartState");

        final MutableShoppingCart cart = new ShoppingCartImpl();
        cart.getShoppingContext().setCustomerEmail("bob@doe.com");
        final byte[] bytes = serializeCart(cart);
        final ShoppingCart restored = deserializeCart(bytes);

        final Date stillActive = new Date();

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
            one(cartCache).get("IN-DB-ACTIVE"); will(returnValue(null));
            one(shoppingCartStateService).findByGuid("IN-DB-ACTIVE"); will(returnValue(cartState));
            allowing(cartState).getUpdatedTimestamp(); will(returnValue(stillActive));
            one(cartState).getState(); will(returnValue(bytes));
            one(cartUpdateProcessor).restoreState(bytes); will(returnValue(restored));
            one(cartCache).put(with(equal(cart.getGuid())), with(any(ShoppingCart.class)));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, cartUpdateProcessor, 60, cacheManager, taskExecutor);

        final ShoppingCart newCart = repo.getShoppingCart("IN-DB-ACTIVE");
        assertNotNull(newCart);
        assertEquals(newCart.getGuid(), cart.getGuid());
        assertEquals(newCart.getCustomerEmail(), cart.getCustomerEmail());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShoppingCartFromDbInactive() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = context.mock(ShoppingCartStateService.class, "shoppingCartStateService");
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");
        final ShoppingCartState cartState = context.mock(ShoppingCartState.class, "cartState");

        final MutableShoppingCart cart = new ShoppingCartImpl();
        cart.getShoppingContext().setCustomerEmail("bob@doe.com");
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

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, cartUpdateProcessor, 60, cacheManager, taskExecutor) {
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
        final CartUpdateProcessor cartUpdateProcessor = context.mock(CartUpdateProcessor.class, "cartUpdateProcessor");
        final TaskExecutor taskExecutor = context.mock(TaskExecutor.class, "taskExecutor");
        final CacheManager cacheManager = context.mock(CacheManager.class, "cacheManager");
        final Cache cartCache = context.mock(Cache.class, "cartCache");

        final MutableShoppingCart cart = new ShoppingCartImpl();
        cart.getShoppingContext().setCustomerEmail("bob@doe.com");

        context.checking(new Expectations() {{
            one(cacheManager).getCache("web.shoppingCart"); will(returnValue(cartCache));
        }});

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, cartUpdateProcessor, 60, cacheManager, taskExecutor) {
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

        final ResilientCartRepositoryImpl repo = new ResilientCartRepositoryImpl(shoppingCartStateService, cartUpdateProcessor, 60, cacheManager, taskExecutor) {
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
