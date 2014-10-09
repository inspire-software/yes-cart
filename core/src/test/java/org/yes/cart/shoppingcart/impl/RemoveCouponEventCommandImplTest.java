package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.service.domain.PromotionCouponService;
import org.yes.cart.service.domain.PromotionService;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 06/06/2014
 * Time: 17:47
 */
public class RemoveCouponEventCommandImplTest extends BaseCoreDBTestCase {



    @Test
    public void testExecute() {

        ShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETSHOP, 10));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR"));

        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), shoppingCart.getTotal().getSubTotal());

        final String couponCodeValid = "CMDTEST-REM001";
        createCoupon(couponCodeValid, shoppingCart.getShoppingContext().getShopCode(), shoppingCart.getCurrencyCode());

        Map<String, String> params = new HashMap<String, String>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST1");
        commands.execute(shoppingCart, (Map) params);
        assertTrue("Expected 19.99 actual value " + shoppingCart.getTotal().getSubTotal(), (new BigDecimal("19.99")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);

        params.remove(ShoppingCartCommand.CMD_ADDTOCART);
        params.put(ShoppingCartCommand.CMD_ADDCOUPON, couponCodeValid);
        commands.execute(shoppingCart, (Map) params);
        assertEquals(1, shoppingCart.getCoupons().size());
        assertEquals(1, shoppingCart.getAppliedCoupons().size());
        assertTrue(shoppingCart.getAppliedCoupons().contains(couponCodeValid));
        assertTrue(shoppingCart.getTotal().getAppliedOrderPromo().contains("PROMO-" + couponCodeValid));
        assertTrue("Expected 17.99 actual value " + shoppingCart.getTotal().getSubTotal(), (new BigDecimal("17.99")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);

        params.remove(ShoppingCartCommand.CMD_ADDCOUPON);
        params.put(ShoppingCartCommand.CMD_REMOVECOUPON, couponCodeValid);
        commands.execute(shoppingCart, (Map) params);
        assertTrue(shoppingCart.getCoupons().isEmpty());
        assertTrue(shoppingCart.getAppliedCoupons().isEmpty());
        assertTrue("Expected 19.99 actual value " + shoppingCart.getTotal().getSubTotal(), (new BigDecimal("19.99")).compareTo(shoppingCart.getTotal().getSubTotal()) == 0);
        assertNull(shoppingCart.getTotal().getAppliedOrderPromo());

    }

    private void createCoupon(final String code, final String shopCode, final String currencyCode) {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);
        final PromotionCouponService promotionCouponService = ctx().getBean("promotionCouponService", PromotionCouponService.class);


        Promotion couponPromotion = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        couponPromotion.setCode("PROMO-" + code);
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
