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
public class ShippingAmountOffPromotionActionTest extends BaseCoreDBTestCase {


    @Test
    public void testSingleItemDiscountPromotion() throws Exception {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);

        ShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        // basic init
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR"));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETSHOP, 10));

        // create discount promotion
        final Promotion amount5 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amount5.setCode("SHIP_5");
        amount5.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amount5.setCurrency("EUR");
        amount5.setName("5 off ship on orders over 200");
        amount5.setPromoType(Promotion.TYPE_SHIPPING);
        amount5.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amount5.setEligibilityCondition("shoppingCartOrderTotal.subTotal > 200.00");
        amount5.setPromoActionContext("5");
        amount5.setEnabled(true);

        promotionService.create(amount5);


        // add qualifying items
        Map<String, String> param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST4");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "2.00");
        commands.execute(shoppingCart, (Map) param);

        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETCARRIERSLA, "4")); // 10 EUR


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
        assertEquals("246.00", shoppingCart.getTotal().getSubTotal().toString());
        assertFalse(shoppingCart.getTotal().isOrderPromoApplied());
        assertNull(shoppingCart.getTotal().getAppliedOrderPromo());
        assertEquals("10.00", shoppingCart.getTotal().getDeliveryListCost().toString());
        assertEquals("5.00", shoppingCart.getTotal().getDeliveryCost().toString());
        assertTrue(shoppingCart.getTotal().isDeliveryPromoApplied());
        assertEquals("SHIP_5", shoppingCart.getTotal().getAppliedDeliveryPromo());

        // clean test
        promotionService.delete(amount5);

    }

    @Test
    public void testSingleItemDiscountPromotionWithNonEligible() throws Exception {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);

        ShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        // basic init
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR"));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETSHOP, 10));

        // create discount promotion
        final Promotion amount5 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amount5.setCode("SHIP_5");
        amount5.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amount5.setCurrency("EUR");
        amount5.setName("5 off ship on orders over 200");
        amount5.setPromoType(Promotion.TYPE_SHIPPING);
        amount5.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amount5.setEligibilityCondition("shoppingCartOrderTotal.subTotal > 200.00");
        amount5.setPromoActionContext("5");
        amount5.setEnabled(true);

        promotionService.create(amount5);


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


        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETCARRIERSLA, "4")); // 10 EUR


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
        assertEquals("10.00", shoppingCart.getTotal().getDeliveryListCost().toString());
        assertEquals("10.00", shoppingCart.getTotal().getDeliveryCost().toString());
        assertFalse(shoppingCart.getTotal().isDeliveryPromoApplied());
        assertNull(shoppingCart.getTotal().getAppliedDeliveryPromo());

        // clean test
        promotionService.delete(amount5);

    }

    @Test
    public void testMultiItemDiscountPromotion() throws Exception {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);

        ShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        // basic init
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR"));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETSHOP, 10));

        // create discount promotion
        final Promotion amount5 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amount5.setCode("SHIP_5");
        amount5.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amount5.setCurrency("EUR");
        amount5.setName("5 off ship on orders over 200");
        amount5.setPromoType(Promotion.TYPE_SHIPPING);
        amount5.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amount5.setEligibilityCondition("shoppingCartOrderTotal.subTotal > 200.00");
        amount5.setPromoActionContext("5");
        amount5.setCanBeCombined(true);
        amount5.setEnabled(true);

        promotionService.create(amount5);

        final Promotion amountAll5 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amountAll5.setCode("ALL_5");
        amountAll5.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amountAll5.setCurrency("EUR");
        amountAll5.setName("5 off ship on all");
        amountAll5.setPromoType(Promotion.TYPE_SHIPPING);
        amountAll5.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amountAll5.setEligibilityCondition("true");
        amountAll5.setPromoActionContext("5");
        amountAll5.setCanBeCombined(true);
        amountAll5.setEnabled(true);

        promotionService.create(amountAll5);


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


        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETCARRIERSLA, "4")); // 10 EUR


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
        assertEquals("301.17", shoppingCart.getTotal().getSubTotal().toString());
        assertFalse(shoppingCart.getTotal().isOrderPromoApplied());
        assertNull(shoppingCart.getTotal().getAppliedOrderPromo());
        assertEquals("10.00", shoppingCart.getTotal().getDeliveryListCost().toString());
        assertEquals("0.00", shoppingCart.getTotal().getDeliveryCost().toString());
        assertTrue(shoppingCart.getTotal().isDeliveryPromoApplied());
        assertEquals("ALL_5,SHIP_5", shoppingCart.getTotal().getAppliedDeliveryPromo());

        // clean test
        promotionService.delete(amount5);
        promotionService.delete(amountAll5);

    }

    @Test
    public void testBestDealPromotion() throws Exception {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);

        ShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        // basic init
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR"));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETSHOP, 10));

        // create discount promotion -5
        final Promotion amount5 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amount5.setCode("SHIP_5");
        amount5.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amount5.setCurrency("EUR");
        amount5.setName("5 off ship on orders over 200");
        amount5.setPromoType(Promotion.TYPE_SHIPPING);
        amount5.setPromoAction(Promotion.ACTION_PERCENT_DISCOUNT);
        amount5.setEligibilityCondition("shoppingCartOrderTotal.subTotal > 200.00");
        amount5.setPromoActionContext("5");
        amount5.setCanBeCombined(true);
        amount5.setEnabled(true);

        promotionService.create(amount5);

        // create discount promotion -8
        final Promotion amountAll8 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amountAll8.setCode("ALL_8");
        amountAll8.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amountAll8.setCurrency("EUR");
        amountAll8.setName("8 off ship on all");
        amountAll8.setPromoType(Promotion.TYPE_SHIPPING);
        amountAll8.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amountAll8.setEligibilityCondition("true");
        amountAll8.setPromoActionContext("8");
        amountAll8.setCanBeCombined(false);
        amountAll8.setEnabled(true);

        promotionService.create(amountAll8);


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


        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETCARRIERSLA, "4")); // 10 EUR


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
        assertEquals("301.17", shoppingCart.getTotal().getSubTotal().toString());
        assertFalse(shoppingCart.getTotal().isOrderPromoApplied());
        assertNull(shoppingCart.getTotal().getAppliedOrderPromo());
        assertEquals("10.00", shoppingCart.getTotal().getDeliveryListCost().toString());
        assertEquals("2.00", shoppingCart.getTotal().getDeliveryCost().toString());
        assertTrue(shoppingCart.getTotal().isDeliveryPromoApplied());
        assertEquals("ALL_8", shoppingCart.getTotal().getAppliedDeliveryPromo());

        // clean test
        promotionService.delete(amount5);
        promotionService.delete(amountAll8);

    }

}
