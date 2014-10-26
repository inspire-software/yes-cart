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

package org.yes.cart.promotion.impl;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.service.domain.PromotionService;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: denispavlov
 * Date: 13-10-29
 * Time: 7:44 PM
 */
public class PromotionContextImplTest extends BaseCoreDBTestCase {

    /**
     * Verify that all promotions combined together work
     */
    @Test
    public void testComplexMixedPromotion() throws Exception {


        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);
        final List<Promotion> promotions = new ArrayList<Promotion>();

        ShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        // basic init
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETSHOP, 10));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR"));

        generatePromotionsMix(promotionService, promotions, shoppingCart);


        // add qualifying items
        Map<String, String> param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST4");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "2.00");
        commands.execute(shoppingCart, (Map) param);

        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETCARRIERSLA, "4")); // 10 EUR


        assertEquals(2, shoppingCart.getCartItemList().size());

        final CartItem cc_test4 = shoppingCart.getCartItemList().get(0);

        assertEquals("CC_TEST4", cc_test4.getProductSkuCode());
        assertTrue(cc_test4.isPromoApplied());
        assertEquals("CC_TEST_10,CC_TEST_10%,CC_TEST_6_GIFT", cc_test4.getAppliedPromo());
        assertEquals("2", cc_test4.getQty().toString());
        assertEquals("123.00", cc_test4.getListPrice().toString());
        assertEquals("123.00", cc_test4.getSalePrice().toString());
        assertEquals("100.70", cc_test4.getPrice().toString()); // -10 -10%


        final CartItem cc_test6 = shoppingCart.getCartItemList().get(1);

        assertEquals("CC_TEST6", cc_test6.getProductSkuCode());
        assertTrue(cc_test6.isPromoApplied());
        assertTrue(cc_test6.isGift());
        assertEquals("CC_TEST_6_GIFT,ORDER_6_GIFT", cc_test6.getAppliedPromo());
        assertEquals("3.00", cc_test6.getQty().toString());
        assertEquals("55.17", cc_test6.getListPrice().toString());
        assertEquals("55.17", cc_test6.getSalePrice().toString());
        assertEquals("0.00", cc_test6.getPrice().toString());


        assertEquals("411.51", shoppingCart.getTotal().getListSubTotal().toString());
        assertEquals("201.40", shoppingCart.getTotal().getPriceSubTotal().toString());
        assertTrue(shoppingCart.getTotal().isOrderPromoApplied());
        assertEquals("ORDER_5%,ORDER_6_GIFT,ORDER_15", shoppingCart.getTotal().getAppliedOrderPromo());
        assertEquals("176.33", shoppingCart.getTotal().getSubTotal().toString());
        assertTrue(shoppingCart.getTotal().isDeliveryPromoApplied());
        assertEquals("SHIP_50%", shoppingCart.getTotal().getAppliedDeliveryPromo());
        assertEquals("10.00", shoppingCart.getTotal().getDeliveryListCost().toString());
        assertEquals("5.00", shoppingCart.getTotal().getDeliveryCost().toString());
        assertEquals("181.33", shoppingCart.getTotal().getTotal().toString());


        // add many qualifying items to get better value promotions
        Map<String, String> param2 = new HashMap<String, String>();
        param2.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST4");
        param2.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "20.00");
        commands.execute(shoppingCart, (Map) param2);


        assertEquals(2, shoppingCart.getCartItemList().size());

        final CartItem cc_test4_2 = shoppingCart.getCartItemList().get(0);

        assertEquals("CC_TEST4", cc_test4_2.getProductSkuCode());
        assertTrue(cc_test4_2.isPromoApplied());
        assertEquals("CC_TEST_10,CC_TEST_10%,CC_TEST_6_GIFT", cc_test4_2.getAppliedPromo());
        assertEquals("20", cc_test4_2.getQty().toString());
        assertEquals("99.99", cc_test4_2.getListPrice().toString());
        assertEquals("99.99", cc_test4_2.getSalePrice().toString());
        assertEquals("79.99", cc_test4_2.getPrice().toString()); // -10 -10%


        final CartItem cc_test6_2 = shoppingCart.getCartItemList().get(1);

        assertEquals("CC_TEST6", cc_test6_2.getProductSkuCode());
        assertTrue(cc_test6_2.isPromoApplied());
        assertTrue(cc_test6_2.isGift());
        assertEquals("CC_TEST_6_GIFT,ORDER_6_GIFT", cc_test6_2.getAppliedPromo());
        assertEquals("21.00", cc_test6_2.getQty().toString());
        assertEquals("55.17", cc_test6_2.getListPrice().toString());
        assertEquals("55.17", cc_test6_2.getSalePrice().toString());
        assertEquals("0.00", cc_test6_2.getPrice().toString());


        assertEquals("3158.37", shoppingCart.getTotal().getListSubTotal().toString());
        assertEquals("1599.80", shoppingCart.getTotal().getPriceSubTotal().toString());
        assertTrue(shoppingCart.getTotal().isOrderPromoApplied());
        assertEquals("ORDER_5%,ORDER_25%,ORDER_6_GIFT,ORDER_15", shoppingCart.getTotal().getAppliedOrderPromo());
        assertEquals("1104.86", shoppingCart.getTotal().getSubTotal().toString());
        assertTrue(shoppingCart.getTotal().isDeliveryPromoApplied());
        assertEquals("SHIP_100%,SHIP_50%", shoppingCart.getTotal().getAppliedDeliveryPromo());
        assertEquals("10.00", shoppingCart.getTotal().getDeliveryListCost().toString());
        assertEquals("0.00", shoppingCart.getTotal().getDeliveryCost().toString());
        assertEquals("1104.86", shoppingCart.getTotal().getTotal().toString());


        // clean test
        for (final Promotion promotion : promotions) {
            promotionService.delete(promotion);
        }


    }

    private void generatePromotionsMix(final PromotionService promotionService,
                                       final List<Promotion> promotions,
                                       final ShoppingCart shoppingCart) {
        // create amount promotion
        final Promotion amount10 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        amount10.setCode("CC_TEST_10");
        amount10.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        amount10.setCurrency("EUR");
        amount10.setName("10 off on CC_TEST items");
        amount10.setRank(0);
        amount10.setPromoType(Promotion.TYPE_ITEM);
        amount10.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        amount10.setEligibilityCondition("['CC_TEST4', 'CC_TEST5'].contains(shoppingCartItem.productSkuCode)");
        amount10.setPromoActionContext("10");
        amount10.setCanBeCombined(true);
        amount10.setEnabled(true);

        promotions.add(promotionService.create(amount10));

        final Promotion discount10 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        discount10.setCode("CC_TEST_10%");
        discount10.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        discount10.setCurrency("EUR");
        discount10.setName("10% off on CC_TEST items");
        discount10.setRank(1);
        discount10.setPromoType(Promotion.TYPE_ITEM);
        discount10.setPromoAction(Promotion.ACTION_PERCENT_DISCOUNT);
        discount10.setEligibilityCondition("['CC_TEST4', 'CC_TEST5'].contains(shoppingCartItem.productSkuCode)");
        discount10.setPromoActionContext("10");
        discount10.setCanBeCombined(true);
        discount10.setEnabled(true);

        promotions.add(promotionService.create(discount10));

        final Promotion gift6 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        gift6.setCode("CC_TEST_6_GIFT");
        gift6.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        gift6.setCurrency("EUR");
        gift6.setName("Gift for CC_TEST items");
        gift6.setRank(3);
        gift6.setPromoType(Promotion.TYPE_ITEM);
        gift6.setPromoAction(Promotion.ACTION_GIFT);
        gift6.setEligibilityCondition("['CC_TEST4', 'CC_TEST5'].contains(shoppingCartItem.productSkuCode)");
        gift6.setPromoActionContext("CC_TEST6");
        gift6.setCanBeCombined(true);
        gift6.setEnabled(true);

        promotions.add(promotionService.create(gift6));

        final Promotion orderDiscount5 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        orderDiscount5.setCode("ORDER_5%");
        orderDiscount5.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        orderDiscount5.setCurrency("EUR");
        orderDiscount5.setName("5% off orders more than 100");
        orderDiscount5.setRank(0);
        orderDiscount5.setPromoType(Promotion.TYPE_ORDER);
        orderDiscount5.setPromoAction(Promotion.ACTION_PERCENT_DISCOUNT);
        orderDiscount5.setEligibilityCondition("shoppingCartItemTotal.priceSubTotal > 100.00");
        orderDiscount5.setPromoActionContext("5");
        orderDiscount5.setCanBeCombined(true);
        orderDiscount5.setEnabled(true);

        promotions.add(promotionService.create(orderDiscount5));

        final Promotion orderDiscount25 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        orderDiscount25.setCode("ORDER_25%");
        orderDiscount25.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        orderDiscount25.setCurrency("EUR");
        orderDiscount25.setName("25% off orders more than 500");
        orderDiscount25.setRank(0);
        orderDiscount25.setPromoType(Promotion.TYPE_ORDER);
        orderDiscount25.setPromoAction(Promotion.ACTION_PERCENT_DISCOUNT);
        orderDiscount25.setEligibilityCondition("shoppingCartItemTotal.priceSubTotal > 500.00");
        orderDiscount25.setPromoActionContext("25");
        orderDiscount25.setCanBeCombined(true);
        orderDiscount25.setEnabled(true);

        promotions.add(promotionService.create(orderDiscount25));


        final Promotion orderAmount5 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        orderAmount5.setCode("ORDER_15");
        orderAmount5.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        orderAmount5.setCurrency("EUR");
        orderAmount5.setName("15 off orders more than 100");
        orderAmount5.setRank(1);
        orderAmount5.setPromoType(Promotion.TYPE_ORDER);
        orderAmount5.setPromoAction(Promotion.ACTION_FIXED_AMOUNT_OFF);
        orderAmount5.setEligibilityCondition("shoppingCartItemTotal.priceSubTotal > 100.00");
        orderAmount5.setPromoActionContext("15");
        orderAmount5.setCanBeCombined(true);
        orderAmount5.setEnabled(true);

        promotions.add(promotionService.create(orderAmount5));

        final Promotion orderGift6 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        orderGift6.setCode("ORDER_6_GIFT");
        orderGift6.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        orderGift6.setCurrency("EUR");
        orderGift6.setName("Gift orders more than 100");
        orderGift6.setRank(2);
        orderGift6.setPromoType(Promotion.TYPE_ORDER);
        orderGift6.setPromoAction(Promotion.ACTION_GIFT);
        orderGift6.setEligibilityCondition("shoppingCartItemTotal.priceSubTotal > 100.00");
        orderGift6.setPromoActionContext("CC_TEST6");
        orderGift6.setCanBeCombined(true);
        orderGift6.setEnabled(true);

        promotions.add(promotionService.create(orderGift6));

        final Promotion ship50 = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        ship50.setCode("SHIP_50%");
        ship50.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        ship50.setCurrency("EUR");
        ship50.setName("50% off shipping for orders more than 100");
        ship50.setRank(2);
        ship50.setPromoType(Promotion.TYPE_SHIPPING);
        ship50.setPromoAction(Promotion.ACTION_PERCENT_DISCOUNT);
        ship50.setEligibilityCondition("shoppingCartOrderTotal.subTotal > 100.00");
        ship50.setPromoActionContext("50");
        ship50.setCanBeCombined(true);
        ship50.setEnabled(true);

        promotions.add(promotionService.create(ship50));

        final Promotion shipFree = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        shipFree.setCode("SHIP_100%");
        shipFree.setShopCode(shoppingCart.getShoppingContext().getShopCode());
        shipFree.setCurrency("EUR");
        shipFree.setName("Free shipping for orders more than 500");
        shipFree.setRank(3);
        shipFree.setPromoType(Promotion.TYPE_SHIPPING);
        shipFree.setPromoAction(Promotion.ACTION_PERCENT_DISCOUNT);
        shipFree.setEligibilityCondition("shoppingCartOrderTotal.subTotal > 500.00");
        shipFree.setPromoActionContext("100");
        shipFree.setCanBeCombined(true);
        shipFree.setEnabled(true);

        promotions.add(promotionService.create(shipFree));
    }

}
