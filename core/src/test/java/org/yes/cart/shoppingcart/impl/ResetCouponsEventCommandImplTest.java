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

package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.service.domain.PromotionCouponService;
import org.yes.cart.service.domain.PromotionService;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.utils.MoneyUtils;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;

/**
 * User: inspiresoftware
 * Date: 01/03/2021
 * Time: 20:11
 */
public class ResetCouponsEventCommandImplTest extends BaseCoreDBTestCase {


    @Test
    public void testExecute() {

        MutableShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETSHOP, 10));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR"));

        assertEquals(MoneyUtils.ZERO, shoppingCart.getTotal().getSubTotal());

        final String couponCodeValid = "CMDTEST2-ADD001";
        createCoupon(couponCodeValid, shoppingCart.getShoppingContext().getShopCode(), shoppingCart.getCurrencyCode());

        final String couponCodeInvalid = "CMDTEST2-ADD002";
        createCoupon(couponCodeInvalid, "DIFFERENTSHOP", shoppingCart.getCurrencyCode());

        Map<String, String> params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST1");
        commands.execute(shoppingCart, (Map) params);
        assertEquals("19.99", shoppingCart.getTotal().getSubTotal().toPlainString());

        params.remove(ShoppingCartCommand.CMD_ADDTOCART);
        params.put(ShoppingCartCommand.CMD_RESETCOUPONS, "ZZZZZ1,ZZZZZ2,ZZZZZ3");
        commands.execute(shoppingCart, (Map) params);
        assertEquals(3, shoppingCart.getCoupons().size());
        assertTrue(shoppingCart.getAppliedCoupons().isEmpty());
        assertEquals("19.99", shoppingCart.getTotal().getSubTotal().toPlainString());

        params.put(ShoppingCartCommand.CMD_RESETCOUPONS, couponCodeValid);
        commands.execute(shoppingCart, (Map) params);
        assertEquals(1, shoppingCart.getCoupons().size());
        assertEquals(1, shoppingCart.getAppliedCoupons().size());
        assertTrue(shoppingCart.getAppliedCoupons().contains(couponCodeValid));
        assertEquals("17.99", shoppingCart.getTotal().getSubTotal().toPlainString());
        assertTrue(shoppingCart.getTotal().getAppliedOrderPromo().contains(couponCodeValid));

        params.put(ShoppingCartCommand.CMD_ADDCOUPON, couponCodeInvalid);
        commands.execute(shoppingCart, (Map) params);
        assertEquals(1, shoppingCart.getCoupons().size());
        assertEquals(1, shoppingCart.getAppliedCoupons().size());
        assertTrue(shoppingCart.getAppliedCoupons().contains(couponCodeValid));
        assertEquals("17.99", shoppingCart.getTotal().getSubTotal().toPlainString());
        assertTrue(shoppingCart.getTotal().getAppliedOrderPromo().contains("PROMO2-" + couponCodeValid));

    }

    private void createCoupon(final String code, final String shopCode, final String currencyCode) {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);
        final PromotionCouponService promotionCouponService = ctx().getBean("promotionCouponService", PromotionCouponService.class);


        Promotion couponPromotion = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        couponPromotion.setCode("PROMO2-" + code);
        couponPromotion.setCouponTriggered(true);
        couponPromotion.setCanBeCombined(false);
        couponPromotion.setEnabled(true);
        couponPromotion.setCurrency(currencyCode);
        couponPromotion.setShopCode(shopCode);
        couponPromotion.setName(code);
        couponPromotion.setEligibilityCondition("");
        couponPromotion.setPromoType(Promotion.TYPE_ORDER);
        couponPromotion.setPromoAction(Promotion.ACTION_PERCENT_DISCOUNT);
        couponPromotion.setPromoActionContext("10");

        promotionService.create(couponPromotion);

        promotionCouponService.create(couponPromotion.getPromotionId(), code, 1, 0);

    }


}