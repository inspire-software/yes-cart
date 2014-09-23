package org.yes.cart.promotion.impl;

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.domain.entity.PromotionCoupon;
import org.yes.cart.promotion.PromoTriplet;
import org.yes.cart.promotion.PromotionApplicationStrategy;
import org.yes.cart.promotion.PromotionCondition;
import org.yes.cart.service.domain.PromotionCouponService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: denispavlov
 * Date: 06/06/2014
 * Time: 09:02
 */
public class BestValuePromotionApplicationStrategyImpl implements PromotionApplicationStrategy {


    private final PromotionCouponService promotionCouponService;

    public BestValuePromotionApplicationStrategyImpl(final PromotionCouponService promotionCouponService) {
        this.promotionCouponService = promotionCouponService;
    }

    /**
     * CPOINT - best deal is the industry standard since provide customer with lowest
     *          price possible. However there are use cases when we want alternative
     *          promotion strategy - e.g. by priority.
     *
     * {@inheritDoc}
     */
    @Override
    public void applyPromotions(final List<List<PromoTriplet>> promoBuckets, final Map<String, Object> context) {

        List<PromoTriplet> bestValue = null;
        BigDecimal bestDiscountValue = BigDecimal.ZERO;

        final Map<Long, PromotionCoupon> validCoupons = loadCoupons(context);

        for (final List<PromoTriplet> promoBucket : promoBuckets) {

            try {
                if (promoBucket.isEmpty()) {
                    continue;
                }

                final List<PromoTriplet> applicable = new ArrayList<PromoTriplet>(promoBucket.size());

                BigDecimal discountValue = BigDecimal.ZERO;

                for (final PromoTriplet promo : promoBucket) {

                    context.put(PromotionCondition.VAR_ACTION_CONTEXT, promo.getPromotion().getPromoActionContext());
                    context.put(PromotionCondition.VAR_PROMOTION, promo.getPromotion());

                    boolean eligible = true;

                    if (promo.getPromotion().isCouponTriggered()) {
                        // Only allow coupon promotion if we have a valid coupon
                        eligible = validCoupons.containsKey(promo.getPromotion().getPromotionId());
                    }

                    if (eligible) {

                        // Eligibility condition may be heavy, so we check it after coupons
                        eligible = promo.getCondition().isEligible(context);

                        if (eligible) {

                            final BigDecimal pdisc = promo.getAction().testDiscountValue(context);
                            if (MoneyUtils.isFirstBiggerThanSecond(pdisc, BigDecimal.ZERO)) {
                                // only if we get some discount from test this promo qualifies to be applicable
                                applicable.add(promo);
                                // cumulative discount value is total value in percent relative to sale price
                                discountValue = discountValue.add(pdisc);
                            }
                        }
                    }

                }

                if (!applicable.isEmpty() && MoneyUtils.isFirstBiggerThanSecond(discountValue, bestDiscountValue)) {

                    bestDiscountValue = discountValue;
                    bestValue = applicable;

                }
            } catch (Exception exp) {
                ShopCodeContext.getLog(this).error("Unable to apply promotions {}", promoBucket);
                ShopCodeContext.getLog(this).error("Unable to apply promotions", exp);
            }
        }

        if (CollectionUtils.isNotEmpty(bestValue)) {

            for (final PromoTriplet promo : bestValue) {

                context.put(PromotionCondition.VAR_ACTION_CONTEXT, promo.getPromotion().getPromoActionContext());
                context.put(PromotionCondition.VAR_PROMOTION, promo.getPromotion());
                context.put(PromotionCondition.VAR_PROMOTION_CODE, getPromotionCode(promo, validCoupons.get(promo.getPromotion().getPromotionId())));

                promo.getAction().perform(context);

            }

        }
    }

    private String getPromotionCode(final PromoTriplet promo, final PromotionCoupon promotionCoupon) {
        if (promo.getPromotion().isCouponTriggered()) {
            return promo.getPromotion().getCode() + ":" + promotionCoupon.getCode();
        }
        return promo.getPromotion().getCode();
    }

    private Map<Long, PromotionCoupon> loadCoupons(final Map<String, Object> context) {

        final ShoppingCart cart = (ShoppingCart) context.get(PromotionCondition.VAR_CART);

        if (cart == null) {
            return Collections.emptyMap();
        }

        final List<String> couponCodes = cart.getCoupons();

        if (couponCodes.isEmpty()) {
            return Collections.emptyMap();
        }

        final Map<Long, PromotionCoupon> map = new HashMap<Long, PromotionCoupon>();
        for (final String couponCode : couponCodes) {

            final PromotionCoupon coupon = promotionCouponService.findValidPromotionCoupon(couponCode, cart.getCustomerEmail());
            if (coupon != null) {
                map.put(coupon.getPromotion().getPromotionId(), coupon);
            }

        }
        return Collections.unmodifiableMap(map);
    }
}
