package org.yes.cart.promotion.impl;

import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.domain.entity.PromotionCoupon;
import org.yes.cart.promotion.*;
import org.yes.cart.service.domain.PromotionCouponService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.shoppingcart.impl.TotalImpl;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: denispavlov
 * Date: 05/06/2014
 * Time: 15:37
 */
public class PromotionCouponContextImpl implements PromotionCouponContext {

    private final PromotionCouponService promotionCouponService;
    private final PromotionConditionParser promotionConditionParser;
    private final Map<String, PromotionAction> promotionActionMap;
    private final PromotionApplicationStrategy strategy;

    public PromotionCouponContextImpl(final PromotionCouponService promotionCouponService,
                                      final PromotionConditionParser promotionConditionParser,
                                      final Map<String, PromotionAction> promotionActionMap,
                                      final PromotionApplicationStrategy strategy) {
        this.promotionCouponService = promotionCouponService;
        this.promotionConditionParser = promotionConditionParser;
        this.promotionActionMap = promotionActionMap;
        this.strategy = strategy;
    }

    /** {@inheritDoc} */
    public Total applyCouponPromo(final Customer customer, final ShoppingCart cart, final Total orderTotal) {

        final List<String> coupons = cart.getCoupons();

        if (!coupons.isEmpty()) {

            final Map<String, Object> context = new HashMap<String, Object>() {{
                put(PromotionCondition.VAR_REGISTERED, customer != null);
                put(PromotionCondition.VAR_CUSTOMER, customer);
                put(PromotionCondition.VAR_CUSTOMER_TAGS, getCustomerTags(customer));
                put(PromotionCondition.VAR_CART, cart);
                put(PromotionCondition.VAR_CART_ITEM_TOTAL, orderTotal);
                put(PromotionCondition.VAR_TMP_TOTAL, new TotalImpl().add(orderTotal));
            }};

            final List<List<PromoTriplet>> buckets = createPromotionBuckets(cart, coupons);

            applyPromotions(buckets, context);

            return (Total) context.get(PromotionCondition.VAR_TMP_TOTAL);

        }

        return new TotalImpl().add(orderTotal);
    }

    private List<List<PromoTriplet>> createPromotionBuckets(final ShoppingCart cart, final List<String> coupons) {

        final List<List<PromoTriplet>> buckets = new ArrayList<List<PromoTriplet>>();
        buckets.add(new ArrayList<PromoTriplet>());  // 0th can be combined

        for (final String code : coupons) {

            final PromotionCoupon promotionCoupon = promotionCouponService.findValidPromotionCoupon(code, cart.getCustomerEmail());
            if (promotionCoupon != null) {

                final Promotion promotion = promotionCoupon.getPromotion();

                if (cart.getShoppingContext().getShopCode().equals(promotion.getShopCode())
                        && cart.getCurrencyCode().equals(promotion.getCurrency())) {

                    final PromotionAction action = promotionActionMap.get(promotion.getPromoAction());
                    if (action != null) {

                        final PromotionCondition condition = promotionConditionParser.parse(promotion);

                        final PromoTriplet promo = new PromoTripletImpl(promotion, condition, action);
                        if (promotion.isCanBeCombined()) {
                            buckets.get(0).add(promo);
                        } else {
                            buckets.add(Arrays.asList(promo));
                        }

                    } else {
                        ShopCodeContext.getLog(this).warn(
                                "No action mapping for promotion: {}, type: {}, action {}",
                                new Object[] { promotion.getCode(), promotion.getPromoType(), promotion.getPromoAction() });
                    }
                }
            }

        }
        return buckets;
    }


    private List<String> getCustomerTags(Customer customer) {
        if (customer != null && customer.getTag() != null) {
            return Arrays.asList(customer.getTag().split(" "));
        }
        return Collections.emptyList();
    }


    private void applyPromotions(final List<List<PromoTriplet>> promoBuckets,
                                 final Map<String, Object> context) {

        strategy.applyPromotions(promoBuckets, context);

    }


}
