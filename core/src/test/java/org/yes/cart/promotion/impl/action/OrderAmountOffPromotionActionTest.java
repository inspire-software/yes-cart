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
 * Date: 04/11/2013
 * Time: 07:56
 */
public class OrderAmountOffPromotionActionTest extends BaseCoreDBTestCase {


    @Test
    public void testSingleItemDiscountPromotion() throws Exception {

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
        final Promotion amount50 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amount50.setCode("ORDER_50");
        amount50.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amount50.setCurrency("EUR");
        amount50.setName("50 off on orders over 200");
        amount50.setPromoType(Promotion.TYPE_ORDER);
        amount50.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amount50.setEligibilityCondition("shoppingCartItemTotal.priceSubTotal > 200.00");
        amount50.setPromoActionContext("50");
        amount50.setEnabled(true);

        promotionService.create(amount50);


        // add qualifying items
        Map<String, String> param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST4");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "2.00");
        commands.execute(shoppingCart, (Map) param);

        assertEquals(1, shoppingCart.getCartItemList().size());

        final CartItem cc_test4 = shoppingCart.getCartItemList().get(0);

        assertEquals("CC_TEST4", cc_test4.getProductSkuCode());
        assertFalse(cc_test4.isPromoApplied());
        assertNull(cc_test4.getAppliedPromo());
        assertEquals("2.00", cc_test4.getQty().toString());
        assertEquals("123.00", cc_test4.getListPrice().toString());
        assertEquals("123.00", cc_test4.getSalePrice().toString());
        assertEquals("123.00", cc_test4.getPrice().toString());

        assertEquals("246.00", shoppingCart.getTotal().getListSubTotal().toString());
        assertEquals("196.00", shoppingCart.getTotal().getSubTotal().toString());
        assertTrue(shoppingCart.getTotal().isOrderPromoApplied());
        assertEquals("ORDER_50", shoppingCart.getTotal().getAppliedOrderPromo());

        // clean test
        promotionService.delete(amount50);

    }

    @Test
    public void testSingleItemDiscountPromotionWithNonEligible() throws Exception {

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
        final Promotion amount50 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amount50.setCode("ORDER_50");
        amount50.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amount50.setCurrency("EUR");
        amount50.setName("50 off on orders over 200");
        amount50.setPromoType(Promotion.TYPE_ORDER);
        amount50.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amount50.setEligibilityCondition("shoppingCartItemTotal.priceSubTotal > 200.00");
        amount50.setPromoActionContext("50");
        amount50.setEnabled(true);

        promotionService.create(amount50);


        // add qualifying items
        Map<String, String> paramQ = new HashMap<String, String>();
        paramQ.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST4");
        paramQ.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) paramQ);

        // add non-qualifying items
        Map<String, String> paramNQ = new HashMap<String, String>();
        paramNQ.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST6");
        paramNQ.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) paramNQ);

        assertEquals(2, shoppingCart.getCartItemList().size());

        final CartItem cc_test4 = shoppingCart.getCartItemList().get(0);

        assertEquals("CC_TEST4", cc_test4.getProductSkuCode());
        assertFalse(cc_test4.isPromoApplied());
        assertNull(cc_test4.getAppliedPromo());
        assertEquals("1.00", cc_test4.getQty().toString());
        assertEquals("123.00", cc_test4.getListPrice().toString());
        assertEquals("123.00", cc_test4.getSalePrice().toString());
        assertEquals("123.00", cc_test4.getPrice().toString());

        final CartItem cc_test6 = shoppingCart.getCartItemList().get(1);

        assertEquals("CC_TEST6", cc_test6.getProductSkuCode());
        assertFalse(cc_test6.isPromoApplied());
        assertNull(cc_test6.getAppliedPromo());
        assertEquals("1.00", cc_test6.getQty().toString());
        assertEquals("55.17", cc_test6.getListPrice().toString());
        assertEquals("55.17", cc_test6.getSalePrice().toString());
        assertEquals("55.17", cc_test6.getPrice().toString());

        assertEquals("178.17", shoppingCart.getTotal().getListSubTotal().toString());
        assertEquals("178.17", shoppingCart.getTotal().getSubTotal().toString());
        assertFalse(shoppingCart.getTotal().isOrderPromoApplied());
        assertNull(shoppingCart.getTotal().getAppliedOrderPromo());


        // clean test
        promotionService.delete(amount50);

    }

    @Test
    public void testMultiItemDiscountPromotion() throws Exception {

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
        final Promotion amount50 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amount50.setCode("ORDER_50");
        amount50.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amount50.setCurrency("EUR");
        amount50.setName("50 off on orders over 200");
        amount50.setPromoType(Promotion.TYPE_ORDER);
        amount50.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amount50.setEligibilityCondition("shoppingCartItemTotal.priceSubTotal > 200.00");
        amount50.setPromoActionContext("50");
        amount50.setCanBeCombined(true);
        amount50.setEnabled(true);

        promotionService.create(amount50);

        final Promotion amount10 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amount10.setCode("ALL_10");
        amount10.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amount10.setCurrency("EUR");
        amount10.setName("10 off on all");
        amount10.setPromoType(Promotion.TYPE_ORDER);
        amount10.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amount10.setEligibilityCondition("true");
        amount10.setPromoActionContext("10");
        amount10.setCanBeCombined(true);
        amount10.setEnabled(true);

        promotionService.create(amount10);


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
        assertFalse(cc_test4.isPromoApplied());
        assertNull(cc_test4.getAppliedPromo());
        assertEquals("2.00", cc_test4.getQty().toString());
        assertEquals("123.00", cc_test4.getListPrice().toString());
        assertEquals("123.00", cc_test4.getSalePrice().toString());
        assertEquals("123.00", cc_test4.getPrice().toString());

        final CartItem cc_test6 = shoppingCart.getCartItemList().get(1);

        assertEquals("CC_TEST6", cc_test6.getProductSkuCode());
        assertFalse(cc_test6.isPromoApplied());
        assertNull(cc_test6.getAppliedPromo());
        assertEquals("1.00", cc_test6.getQty().toString());
        assertEquals("55.17", cc_test6.getListPrice().toString());
        assertEquals("55.17", cc_test6.getSalePrice().toString());
        assertEquals("55.17", cc_test6.getPrice().toString());

        assertEquals("301.17", shoppingCart.getTotal().getListSubTotal().toString());
        assertEquals("241.17", shoppingCart.getTotal().getSubTotal().toString());
        assertTrue(shoppingCart.getTotal().isOrderPromoApplied());
        assertEquals("ALL_10,ORDER_50", shoppingCart.getTotal().getAppliedOrderPromo());

        // clean test
        promotionService.delete(amount50);
        promotionService.delete(amount10);

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

        // create discount promotion -50
        final Promotion amount50 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amount50.setCode("ORDER_50");
        amount50.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amount50.setCurrency("EUR");
        amount50.setName("50 off on orders over 200");
        amount50.setPromoType(Promotion.TYPE_ORDER);
        amount50.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amount50.setEligibilityCondition("shoppingCartItemTotal.priceSubTotal > 200.00");
        amount50.setPromoActionContext("50");
        amount50.setCanBeCombined(true);
        amount50.setEnabled(true);

        promotionService.create(amount50);

        // create discount promotion -10
        final Promotion amount10 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amount10.setCode("ALL_10");
        amount10.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amount10.setCurrency("EUR");
        amount10.setName("10 off on all");
        amount10.setPromoType(Promotion.TYPE_ORDER);
        amount10.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amount10.setEligibilityCondition("true");
        amount10.setPromoActionContext("10");
        amount10.setCanBeCombined(false);
        amount10.setEnabled(true);

        promotionService.create(amount10);


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
        assertFalse(cc_test4.isPromoApplied());
        assertNull(cc_test4.getAppliedPromo());
        assertEquals("2.00", cc_test4.getQty().toString());
        assertEquals("123.00", cc_test4.getListPrice().toString());
        assertEquals("123.00", cc_test4.getSalePrice().toString());
        assertEquals("123.00", cc_test4.getPrice().toString());

        final CartItem cc_test6 = shoppingCart.getCartItemList().get(1);

        assertEquals("CC_TEST6", cc_test6.getProductSkuCode());
        assertFalse(cc_test6.isPromoApplied());
        assertNull(cc_test6.getAppliedPromo());
        assertEquals("1.00", cc_test6.getQty().toString());
        assertEquals("55.17", cc_test6.getListPrice().toString());
        assertEquals("55.17", cc_test6.getSalePrice().toString());
        assertEquals("55.17", cc_test6.getPrice().toString());

        assertEquals("301.17", shoppingCart.getTotal().getListSubTotal().toString());
        assertEquals("251.17", shoppingCart.getTotal().getSubTotal().toString());
        assertTrue(shoppingCart.getTotal().isOrderPromoApplied());
        assertEquals("ORDER_50", shoppingCart.getTotal().getAppliedOrderPromo());

        // clean test
        promotionService.delete(amount50);
        promotionService.delete(amount10);

    }

}
