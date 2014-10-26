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

package org.yes.cart.promotion.impl.action;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.service.domain.PromotionService;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 13-10-30
 * Time: 11:31 PM
 */
public class OrderGiftPromotionActionTest extends BaseCoreDBTestCase {

    @Test
    public void testSingleExactItemGiftPromotion() throws Exception {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);

        ShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        // basic init
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETSHOP, 10));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR"));

        // create gift promotion
        final Promotion gift6 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        gift6.setCode("ORDER_GIFT");
        gift6.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        gift6.setCurrency("EUR");
        gift6.setName("Gift for orders over 200");
        gift6.setPromoType(Promotion.TYPE_ORDER);
        gift6.setPromoAction(Promotion.ACTION_GIFT);
        gift6.setEligibilityCondition("shoppingCartItemTotal.priceSubTotal > 200.00");
        gift6.setPromoActionContext("CC_TEST6 = 200"); // one gift for every 200 spent
        gift6.setEnabled(true);

        promotionService.create(gift6);


        // add qualifying items
        Map<String, String> param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST4");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "5.00");
        commands.execute(shoppingCart, (Map) param);

        assertEquals(2, shoppingCart.getCartItemList().size());

        final CartItem cc_test4 = shoppingCart.getCartItemList().get(0);

        assertEquals("CC_TEST4", cc_test4.getProductSkuCode());
        assertFalse(cc_test4.isPromoApplied());
        assertNull(cc_test4.getAppliedPromo());
        assertEquals("5", cc_test4.getQty().toString());
        assertEquals("99.99", cc_test4.getListPrice().toString());
        assertEquals("99.99", cc_test4.getSalePrice().toString());
        assertEquals("99.99", cc_test4.getPrice().toString());

        final CartItem cc_test6 = shoppingCart.getCartItemList().get(1);

        assertEquals("CC_TEST6", cc_test6.getProductSkuCode());
        assertTrue(cc_test6.isPromoApplied());
        assertTrue(cc_test6.isGift());
        assertEquals("ORDER_GIFT", cc_test6.getAppliedPromo());
        assertEquals("2.00", cc_test6.getQty().toString());
        assertEquals("55.17", cc_test6.getListPrice().toString());
        assertEquals("55.17", cc_test6.getSalePrice().toString());
        assertEquals("0.00", cc_test6.getPrice().toString());

        assertEquals("610.29", shoppingCart.getTotal().getListSubTotal().toString());
        assertEquals("499.95", shoppingCart.getTotal().getSubTotal().toString());
        assertTrue(shoppingCart.getTotal().isOrderPromoApplied());
        assertEquals("ORDER_GIFT", shoppingCart.getTotal().getAppliedOrderPromo());

        // clean test
        promotionService.delete(gift6);

    }

    @Test
    public void testSingleApproxItemGiftPromotion() throws Exception {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);

        ShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        // basic init
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETSHOP, 10));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR"));

        // create gift promotion
        final Promotion gift6 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        gift6.setCode("ORDER_GIFT");
        gift6.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        gift6.setCurrency("EUR");
        gift6.setName("Gift for orders over 200");
        gift6.setPromoType(Promotion.TYPE_ORDER);
        gift6.setPromoAction(Promotion.ACTION_GIFT);
        gift6.setEligibilityCondition("shoppingCartItemTotal.priceSubTotal > 200.00");
        gift6.setPromoActionContext("CC_TEST6 ~ 200"); // one gift for every 200 spent
        gift6.setEnabled(true);

        promotionService.create(gift6);


        // add qualifying items
        Map<String, String> param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST4");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "5.00");
        commands.execute(shoppingCart, (Map) param);

        assertEquals(2, shoppingCart.getCartItemList().size());

        final CartItem cc_test4 = shoppingCart.getCartItemList().get(0);

        assertEquals("CC_TEST4", cc_test4.getProductSkuCode());
        assertFalse(cc_test4.isPromoApplied());
        assertNull(cc_test4.getAppliedPromo());
        assertEquals("5", cc_test4.getQty().toString());
        assertEquals("99.99", cc_test4.getListPrice().toString());
        assertEquals("99.99", cc_test4.getSalePrice().toString());
        assertEquals("99.99", cc_test4.getPrice().toString());

        final CartItem cc_test6 = shoppingCart.getCartItemList().get(1);

        assertEquals("CC_TEST6", cc_test6.getProductSkuCode());
        assertTrue(cc_test6.isPromoApplied());
        assertTrue(cc_test6.isGift());
        assertEquals("ORDER_GIFT", cc_test6.getAppliedPromo());
        assertEquals("3.00", cc_test6.getQty().toString());
        assertEquals("55.17", cc_test6.getListPrice().toString());
        assertEquals("55.17", cc_test6.getSalePrice().toString());
        assertEquals("0.00", cc_test6.getPrice().toString());

        assertEquals("665.46", shoppingCart.getTotal().getListSubTotal().toString());
        assertEquals("499.95", shoppingCart.getTotal().getSubTotal().toString());
        assertTrue(shoppingCart.getTotal().isOrderPromoApplied());
        assertEquals("ORDER_GIFT", shoppingCart.getTotal().getAppliedOrderPromo());

        // clean test
        promotionService.delete(gift6);

    }

    @Test
    public void testSingleItemGiftPromotionNotEnoughQty() throws Exception {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);

        ShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        // basic init
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETSHOP, 10));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR"));

        // create gift promotion
        final Promotion gift6 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        gift6.setCode("ORDER_GIFT");
        gift6.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        gift6.setCurrency("EUR");
        gift6.setName("Gift for orders over 200");
        gift6.setPromoType(Promotion.TYPE_ORDER);
        gift6.setPromoAction(Promotion.ACTION_GIFT);
        gift6.setEligibilityCondition("shoppingCartItemTotal.priceSubTotal > 200.00");
        gift6.setPromoActionContext("CC_TEST6"); // one gift for every amount
        gift6.setEnabled(true);

        promotionService.create(gift6);


        // add qualifying items
        Map<String, String> param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST4");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) param);

        assertEquals(1, shoppingCart.getCartItemList().size());

        final CartItem cc_test4 = shoppingCart.getCartItemList().get(0);

        assertEquals("CC_TEST4", cc_test4.getProductSkuCode());
        assertFalse(cc_test4.isPromoApplied());
        assertNull(cc_test4.getAppliedPromo());
        assertEquals("1", cc_test4.getQty().toString());
        assertEquals("123.00", cc_test4.getListPrice().toString());
        assertEquals("123.00", cc_test4.getSalePrice().toString());
        assertEquals("123.00", cc_test4.getPrice().toString());

        assertEquals("123.00", shoppingCart.getTotal().getListSubTotal().toString());
        assertEquals("123.00", shoppingCart.getTotal().getSubTotal().toString());
        assertFalse(shoppingCart.getTotal().isOrderPromoApplied());
        assertNull(shoppingCart.getTotal().getAppliedOrderPromo());

        // clean test
        promotionService.delete(gift6);

    }

}
