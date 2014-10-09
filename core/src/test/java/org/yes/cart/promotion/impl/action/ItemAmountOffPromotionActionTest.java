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
public class ItemAmountOffPromotionActionTest extends BaseCoreDBTestCase {

    @Test
    public void testSingleItemAmountPromotion() throws Exception {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);

        ShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        // basic init
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETSHOP, 10));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR"));

        // create amount promotion
        final Promotion amount80 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amount80.setCode("CC_TEST_80");
        amount80.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amount80.setCurrency("EUR");
        amount80.setName("80 off on CC_TEST items");
        amount80.setPromoType(Promotion.TYPE_ITEM);
        amount80.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amount80.setEligibilityCondition("['CC_TEST4', 'CC_TEST5'].contains(shoppingCartItem.productSkuCode)");
        amount80.setPromoActionContext("80");
        amount80.setEnabled(true);

        promotionService.create(amount80);


        // add qualifying items
        Map<String, String> param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST4");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "2.00");
        commands.execute(shoppingCart, (Map) param);

        assertEquals(1, shoppingCart.getCartItemList().size());

        final CartItem cc_test4 = shoppingCart.getCartItemList().get(0);

        assertEquals("CC_TEST4", cc_test4.getProductSkuCode());
        assertTrue(cc_test4.isPromoApplied());
        assertEquals("CC_TEST_80", cc_test4.getAppliedPromo());
        assertEquals("2.00", cc_test4.getQty().toString());
        assertEquals("123.00", cc_test4.getListPrice().toString());
        assertEquals("123.00", cc_test4.getSalePrice().toString());
        assertEquals("43.00", cc_test4.getPrice().toString());

        assertEquals("246.00", shoppingCart.getTotal().getListSubTotal().toString());
        assertEquals("86.00", shoppingCart.getTotal().getSubTotal().toString());

        // clean test
        promotionService.delete(amount80);

    }

    @Test
    public void testSingleItemAmountPromotionWithNonEligible() throws Exception {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);

        ShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        // basic init
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETSHOP, 10));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR"));

        // create amount promotion
        final Promotion amount200 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amount200.setCode("CC_TEST_200");
        amount200.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amount200.setCurrency("EUR");
        amount200.setName("200 off on CC_TEST items");
        amount200.setPromoType(Promotion.TYPE_ITEM);
        amount200.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amount200.setEligibilityCondition("['CC_TEST4', 'CC_TEST5'].contains(shoppingCartItem.productSkuCode)");
        amount200.setPromoActionContext("200");
        amount200.setEnabled(true);

        promotionService.create(amount200);


        // add qualifying items
        Map<String, String> paramQ = new HashMap<String, String>();
        paramQ.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST4");
        paramQ.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "2.00");
        commands.execute(shoppingCart, (Map) paramQ);

        // add non-qualifying items
        Map<String, String> paramNQ = new HashMap<String, String>();
        paramNQ.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST6");
        paramNQ.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) paramNQ);

        assertEquals(2, shoppingCart.getCartItemList().size());

        final CartItem cc_test4 = shoppingCart.getCartItemList().get(0);

        assertEquals("CC_TEST4", cc_test4.getProductSkuCode());
        assertTrue(cc_test4.isPromoApplied());
        assertEquals("CC_TEST_200", cc_test4.getAppliedPromo());
        assertEquals("2.00", cc_test4.getQty().toString());
        assertEquals("123.00", cc_test4.getListPrice().toString());
        assertEquals("123.00", cc_test4.getSalePrice().toString());
        assertEquals("0.00", cc_test4.getPrice().toString());

        final CartItem cc_test6 = shoppingCart.getCartItemList().get(1);

        assertEquals("CC_TEST6", cc_test6.getProductSkuCode());
        assertFalse(cc_test6.isPromoApplied());
        assertNull(cc_test6.getAppliedPromo());
        assertEquals("1.00", cc_test6.getQty().toString());
        assertEquals("55.17", cc_test6.getListPrice().toString());
        assertEquals("55.17", cc_test6.getSalePrice().toString());
        assertEquals("55.17", cc_test6.getPrice().toString());

        assertEquals("301.17", shoppingCart.getTotal().getListSubTotal().toString());
        assertEquals("55.17", shoppingCart.getTotal().getSubTotal().toString());

        // clean test
        promotionService.delete(amount200);

    }

    @Test
    public void testMultiItemAmountPromotion() throws Exception {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);

        ShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        // basic init
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETSHOP, 10));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR"));

        // create discount promotion
        final Promotion amount80 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amount80.setCode("CC_TEST_80");
        amount80.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amount80.setCurrency("EUR");
        amount80.setName("80 off on CC_TEST items");
        amount80.setPromoType(Promotion.TYPE_ITEM);
        amount80.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amount80.setEligibilityCondition("['CC_TEST4', 'CC_TEST5'].contains(shoppingCartItem.productSkuCode)");
        amount80.setPromoActionContext("80");
        amount80.setCanBeCombined(true);
        amount80.setEnabled(true);

        promotionService.create(amount80);

        final Promotion amount15 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amount15.setCode("ALL_15");
        amount15.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amount15.setCurrency("EUR");
        amount15.setName("15 off on all");
        amount15.setPromoType(Promotion.TYPE_ITEM);
        amount15.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amount15.setEligibilityCondition("true");
        amount15.setPromoActionContext("15");
        amount15.setCanBeCombined(true);
        amount15.setEnabled(true);

        promotionService.create(amount15);


        // add qualifying items
        Map<String, String> paramQ = new HashMap<String, String>();
        paramQ.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST4");
        paramQ.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "2.00");
        commands.execute(shoppingCart, (Map) paramQ);

        // add non-qualifying items
        Map<String, String> paramNQ = new HashMap<String, String>();
        paramNQ.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST6");
        paramNQ.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) paramNQ);

        assertEquals(2, shoppingCart.getCartItemList().size());

        final CartItem cc_test4 = shoppingCart.getCartItemList().get(0);

        assertEquals("CC_TEST4", cc_test4.getProductSkuCode());
        assertTrue(cc_test4.isPromoApplied());
        assertEquals("CC_TEST_80,ALL_15", cc_test4.getAppliedPromo());
        assertEquals("2.00", cc_test4.getQty().toString());
        assertEquals("123.00", cc_test4.getListPrice().toString());
        assertEquals("123.00", cc_test4.getSalePrice().toString());
        assertEquals("28.00", cc_test4.getPrice().toString()); // 123 - 80 - 15

        final CartItem cc_test6 = shoppingCart.getCartItemList().get(1);

        assertEquals("CC_TEST6", cc_test6.getProductSkuCode());
        assertTrue(cc_test6.isPromoApplied());
        assertEquals("ALL_15", cc_test6.getAppliedPromo());
        assertEquals("1.00", cc_test6.getQty().toString());
        assertEquals("55.17", cc_test6.getListPrice().toString());
        assertEquals("55.17", cc_test6.getSalePrice().toString());
        assertEquals("40.17", cc_test6.getPrice().toString());

        assertEquals("301.17", shoppingCart.getTotal().getListSubTotal().toString());
        assertEquals("96.17", shoppingCart.getTotal().getSubTotal().toString());

        // clean test
        promotionService.delete(amount80);
        promotionService.delete(amount15);

    }

    @Test
    public void testBestDealPromotion() throws Exception {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);

        ShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        // basic init
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETSHOP, 10));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR"));

        // create combined discount promotion 80off + 15off
        final Promotion amount80 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amount80.setCode("CC_TEST_80");
        amount80.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amount80.setCurrency("EUR");
        amount80.setName("80 off on CC_TEST items");
        amount80.setPromoType(Promotion.TYPE_ITEM);
        amount80.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amount80.setEligibilityCondition("['CC_TEST4', 'CC_TEST5'].contains(shoppingCartItem.productSkuCode)");
        amount80.setPromoActionContext("80");
        amount80.setCanBeCombined(true);
        amount80.setEnabled(true);

        promotionService.create(amount80);

        final Promotion amount15 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amount15.setCode("ALL_15");
        amount15.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amount15.setCurrency("EUR");
        amount15.setName("15 off on all");
        amount15.setPromoType(Promotion.TYPE_ITEM);
        amount15.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amount15.setEligibilityCondition("true");
        amount15.setPromoActionContext("15");
        amount15.setCanBeCombined(true);
        amount15.setEnabled(true);

        promotionService.create(amount15);

        // separate deal for 50
        final Promotion amount50 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amount50.setCode("ALL_50");
        amount50.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amount50.setCurrency("EUR");
        amount50.setName("50 off on all");
        amount50.setPromoType(Promotion.TYPE_ITEM);
        amount50.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amount50.setEligibilityCondition("true");
        amount50.setPromoActionContext("50");
        amount50.setCanBeCombined(false);
        amount50.setEnabled(true);

        promotionService.create(amount50);


        // add qualifying items
        Map<String, String> paramQ = new HashMap<String, String>();
        paramQ.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST4");
        paramQ.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "2.00");
        commands.execute(shoppingCart, (Map) paramQ);

        // add non-qualifying items
        Map<String, String> paramNQ = new HashMap<String, String>();
        paramNQ.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST6");
        paramNQ.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) paramNQ);

        assertEquals(2, shoppingCart.getCartItemList().size());

        final CartItem cc_test4 = shoppingCart.getCartItemList().get(0);

        assertEquals("CC_TEST4", cc_test4.getProductSkuCode());
        assertTrue(cc_test4.isPromoApplied());
        assertEquals("CC_TEST_80,ALL_15", cc_test4.getAppliedPromo());
        assertEquals("2.00", cc_test4.getQty().toString());
        assertEquals("123.00", cc_test4.getListPrice().toString());
        assertEquals("123.00", cc_test4.getSalePrice().toString());
        assertEquals("28.00", cc_test4.getPrice().toString()); // Best deal: 123 - 80 - 15

        final CartItem cc_test6 = shoppingCart.getCartItemList().get(1);

        assertEquals("CC_TEST6", cc_test6.getProductSkuCode());
        assertTrue(cc_test6.isPromoApplied());
        assertEquals("ALL_50", cc_test6.getAppliedPromo());
        assertEquals("1.00", cc_test6.getQty().toString());
        assertEquals("55.17", cc_test6.getListPrice().toString());
        assertEquals("55.17", cc_test6.getSalePrice().toString());
        assertEquals("5.17", cc_test6.getPrice().toString()); // Best deal: 55.17 - 50

        assertEquals("301.17", shoppingCart.getTotal().getListSubTotal().toString());
        assertEquals("61.17", shoppingCart.getTotal().getSubTotal().toString());

        // clean test
        promotionService.delete(amount80);
        promotionService.delete(amount50);
        promotionService.delete(amount15);

    }
}
