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

package org.yes.cart.promotion.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.promotion.PromotionCondition;
import org.yes.cart.promotion.PromotionConditionSupport;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingContext;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 13-10-28
 * Time: 9:46 AM
 */
public class GroovyPromotionConditionParserTest {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testParseGroovyConditionTrue() throws Exception {

        final ShoppingCart cart = mockery.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = mockery.mock(ShoppingContext.class, "cartCtx");

        mockery.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(10L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(1010L));
        }});

        final GroovyPromotionConditionParser parser = new GroovyPromotionConditionParser();

        final Class cl = parser.parseGroovyCondition(1L, "ABC#", "shoppingCart != null");

        assertEquals("PromotionABC_", cl.getSimpleName());

        final PromotionCondition condition = ((PromotionCondition) cl.newInstance());

        assertEquals(1L, condition.getPromotionId());
        assertEquals("ABC#", condition.getPromotionCode());

        final boolean result = condition.isEligible(new HashMap<String, Object>() {{
            put("shoppingCart", cart);
        }});

        assertTrue(result);

    }

    @Test
    public void testParseGroovyConditionFalse() throws Exception {

        final ShoppingCart cart = mockery.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = mockery.mock(ShoppingContext.class, "cartCtx");

        mockery.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(10L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(1010L));
        }});

        final GroovyPromotionConditionParser parser = new GroovyPromotionConditionParser();

        final Class cl = parser.parseGroovyCondition(1L, "ABC#", "shoppingCart != null; return false");

        assertEquals("PromotionABC_", cl.getSimpleName());

        final PromotionCondition condition = ((PromotionCondition) cl.newInstance());

        assertEquals(1L, condition.getPromotionId());
        assertEquals("ABC#", condition.getPromotionCode());

        final boolean result = condition.isEligible(new HashMap<String, Object>() {{
            put("shoppingCart", cart);
        }});

        assertFalse(result);

    }


    @Test
    public void testParseCache() throws Exception {

        final ShoppingCart cart = mockery.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = mockery.mock(ShoppingContext.class, "cartCtx");
        final Promotion promotion = mockery.mock(Promotion.class, "promotion");

        mockery.checking(new Expectations() {{
            allowing(promotion).getPromotionId(); will(returnValue(1L));
            allowing(promotion).getCode(); will(returnValue("ABC#"));
            allowing(promotion).getEligibilityCondition(); will(returnValue("shoppingCart != null"));
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(10L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(1010L));
        }});

        final GroovyPromotionConditionParser parser = new GroovyPromotionConditionParser();

        final PromotionCondition condition = parser.parse(promotion);

        assertFalse(condition instanceof NullPromotionCondition);
        assertEquals("PromotionABC_", condition.getClass().getSimpleName());
        assertEquals(1L, condition.getPromotionId());
        assertEquals("ABC#", condition.getPromotionCode());

        final boolean result = condition.isEligible(new HashMap<String, Object>() {{
            put("shoppingCart", cart);
        }});

        assertTrue(result);

    }

    @Test
    public void testBrokenCondition() throws Exception {

        final Promotion promotion = mockery.mock(Promotion.class, "promotion");

        mockery.checking(new Expectations() {{
            allowing(promotion).getPromotionId(); will(returnValue(1L));
            allowing(promotion).getCode(); will(returnValue("ABC#"));
            allowing(promotion).getEligibilityCondition(); will(returnValue("#?/*"));
        }});

        GroovyPromotionConditionParser parser = new GroovyPromotionConditionParser();

        final PromotionCondition condition = parser.parse(promotion);

        assertTrue(condition instanceof NullPromotionCondition);
        assertEquals(1L, condition.getPromotionId());
        assertEquals("ABC#", condition.getPromotionCode());

        final boolean result = condition.isEligible(null);

        assertFalse(result);


    }

    @Test
    public void testFunctionInjection() throws Exception {

        final PromotionConditionSupport promotionConditionSupport = mockery.mock(PromotionConditionSupport.class, "promotionConditionSupport");

        final ShoppingCart cart = mockery.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = mockery.mock(ShoppingContext.class, "cartCtx");
        final CartItem cartItem = mockery.mock(CartItem.class, "cartItem");
        final Promotion promotion = mockery.mock(Promotion.class, "promotion");
        final Product product = mockery.mock(Product.class, "product");

        mockery.checking(new Expectations() {{
            allowing(promotion).getPromotionId(); will(returnValue(1L));
            allowing(promotion).getCode(); will(returnValue("ABC#"));
            allowing(promotion).getEligibilityCondition(); will(returnValue("product(SKU)?.featured"));
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(10L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(1010L));
            allowing(cartItem).getProductSkuCode(); will(returnValue("SKU001"));
            allowing(promotionConditionSupport).getProductBySkuCode("SKU001"); will(returnValue(product));
            allowing(product).getFeatured(); will(returnValue(true));
        }});

        GroovyPromotionConditionParser parser = new GroovyPromotionConditionParser();

        final PromotionCondition condition = parser.parse(promotion);

        assertFalse(condition instanceof NullPromotionCondition);
        assertEquals("PromotionABC_", condition.getClass().getSimpleName());
        assertEquals(1L, condition.getPromotionId());
        assertEquals("ABC#", condition.getPromotionCode());

        final boolean result = condition.isEligible(new HashMap<String, Object>() {{
            put("shoppingCart", cart);
            put("shoppingCartItem", cartItem);
            put("conditionSupport", promotionConditionSupport);
        }});

        assertTrue(result);



    }
}
