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

package org.yes.cart.promotion.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.promotion.*;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.impl.TotalImpl;
import org.yes.cart.utils.TimeContext;

import java.time.Instant;
import java.util.*;

/**
 * User: denispavlov
 * Date: 13-10-29
 * Time: 8:48 AM
 */
public class PromotionContextImpl implements PromotionContext {

    private final String shopCode;
    private final String currency;
    private final PromotionApplicationStrategy strategy;
    private final PromotionConditionSupport conditionSupport;
    private final PricingPolicyProvider pricingPolicyProvider;
    private final Instant timestamp = TimeContext.getTime();

    private final Map<String, List<List<PromoTriplet>>> promotionBuckets = new HashMap<>();

    PromotionContextImpl(final String shopCode,
                         final String currency,
                         final PromotionApplicationStrategy strategy,
                         final PromotionConditionSupport conditionSupport,
                         final PricingPolicyProvider pricingPolicyProvider) {
        this.shopCode = shopCode;
        this.currency = currency;
        this.strategy = strategy;
        this.conditionSupport = conditionSupport;
        this.pricingPolicyProvider = pricingPolicyProvider;
    }

    /**
     * Add promotion triplet to this context.
     *
     * @param promotion original promotion object
     * @param condition precompiled condition
     * @param action action to be performed
     */
    void addPromotion(final Promotion promotion,
                      final PromotionCondition condition,
                      final PromotionAction action) {

        final PromoTriplet promo = new PromoTripletImpl(promotion, condition, action);

        List<List<PromoTriplet>> buckets = promotionBuckets.get(promotion.getPromoType());

        if (buckets == null) {
            buckets = new ArrayList<>();
            buckets.add(new ArrayList<>()); // 0th can be combined
            promotionBuckets.put(promotion.getPromoType(), buckets);
        }

        if (promotion.isCanBeCombined()) {
            buckets.get(0).add(promo);
        } else {
            buckets.add(Collections.singletonList(promo));
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getShopCode() {
        return shopCode;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    private List<String> getCustomerTags(Customer customer) {
        if (customer != null && StringUtils.isNotBlank(customer.getTag())) {
            return Arrays.asList(StringUtils.split(customer.getTag(), ' '));
        }
        return Collections.emptyList();
    }

    private String getCustomerType(final ShoppingCart shoppingCart, final Customer customer) {
        if (shoppingCart != null) {
            return shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_CUSTOMER_TYPE);
        } else if (customer != null) {
            return customer.getCustomerType();
        }
        return null;
    }

    private List<String> getCustomerPricingPolicies(final ShoppingCart shoppingCart, final Customer customer) {

        if (shoppingCart != null) {
            final PricingPolicyProvider.PricingPolicy pricingPolicy = pricingPolicyProvider.determinePricingPolicy(
                    shoppingCart.getShoppingContext().getShopCode(), shoppingCart.getCurrencyCode(), shoppingCart.getCustomerLogin(),
                    shoppingCart.getShoppingContext().getCountryCode(),
                    shoppingCart.getShoppingContext().getStateCode()
            );

            if (pricingPolicy.getID() != null) {
                return Collections.singletonList(pricingPolicy.getID());
            }
        } else if (customer != null) {
            if (StringUtils.isNotEmpty(customer.getPricingPolicy())) {
                return Collections.singletonList(customer.getPricingPolicy());
            }
        }
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    public void applyItemPromo(final Customer customer, final MutableShoppingCart cart) {

        cart.removeItemPromotions(); // remove all gifts and promo prices

        final List<List<PromoTriplet>> itemPromoBuckets = promotionBuckets.get(Promotion.TYPE_ITEM);

        if (CollectionUtils.isEmpty(itemPromoBuckets) || cart.isPromotionsDisabled()) {
            return;
        }

        final Map<String, Object> context = new HashMap<>();
        context.put(PromotionCondition.VAR_CONDITION_SUPPORT, this.conditionSupport);
        context.put(PromotionCondition.VAR_REGISTERED, customer != null && !customer.isGuest());
        context.put(PromotionCondition.VAR_CUSTOMER, customer);
        context.put(PromotionCondition.VAR_CUSTOMER_TAGS, getCustomerTags(customer));
        context.put(PromotionCondition.VAR_CUSTOMER_TYPE, getCustomerType(cart, customer));
        context.put(PromotionCondition.VAR_CUSTOMER_PRICING_POLICY, getCustomerPricingPolicies(cart, customer));
        context.put(PromotionCondition.VAR_CART, cart);

        for (final CartItem item : cart.getCartItemList()) {

            if (!item.isFixedPrice()) { // Offers do not participate in promotions

                context.put(PromotionCondition.VAR_CART_ITEM, item);

                applyPromotions(itemPromoBuckets, context);

            }

        }

    }

    /** {@inheritDoc} */
    @Override
    public Total applyOrderPromo(final Customer customer, final MutableShoppingCart cart, final Total itemTotal) {

        final List<List<PromoTriplet>> orderPromoBuckets = promotionBuckets.get(Promotion.TYPE_ORDER);

        if (CollectionUtils.isEmpty(orderPromoBuckets) || cart.isPromotionsDisabled()) {
            return new TotalImpl().add(itemTotal);
        }

        final Map<String, Object> context = new HashMap<>();
        context.put(PromotionCondition.VAR_CONDITION_SUPPORT, this.conditionSupport);
        context.put(PromotionCondition.VAR_REGISTERED, customer != null && !customer.isGuest());
        context.put(PromotionCondition.VAR_CUSTOMER, customer);
        context.put(PromotionCondition.VAR_CUSTOMER_TAGS, getCustomerTags(customer));
        context.put(PromotionCondition.VAR_CUSTOMER_TYPE, getCustomerType(cart, customer));
        context.put(PromotionCondition.VAR_CUSTOMER_PRICING_POLICY, getCustomerPricingPolicies(cart, customer));
        context.put(PromotionCondition.VAR_CART, cart);
        context.put(PromotionCondition.VAR_CART_ITEM_TOTAL, itemTotal);
        context.put(PromotionCondition.VAR_TMP_TOTAL, new TotalImpl().add(itemTotal));

        applyPromotions(orderPromoBuckets, context);

        return (Total) context.get(PromotionCondition.VAR_TMP_TOTAL);

    }

    /** {@inheritDoc} */
    @Override
    public void applyShippingPromo(final Customer customer, final MutableShoppingCart cart, final Total orderTotal) {

        final List<List<PromoTriplet>> orderPromoBuckets = promotionBuckets.get(Promotion.TYPE_SHIPPING);

        if (CollectionUtils.isEmpty(orderPromoBuckets) || cart.isPromotionsDisabled()) {
            return;
        }

        final Map<String, Object> context = new HashMap<>();
        context.put(PromotionCondition.VAR_CONDITION_SUPPORT, this.conditionSupport);
        context.put(PromotionCondition.VAR_REGISTERED, customer != null && !customer.isGuest());
        context.put(PromotionCondition.VAR_CUSTOMER, customer);
        context.put(PromotionCondition.VAR_CUSTOMER_TAGS, getCustomerTags(customer));
        context.put(PromotionCondition.VAR_CUSTOMER_TYPE, getCustomerType(cart, customer));
        context.put(PromotionCondition.VAR_CUSTOMER_PRICING_POLICY, getCustomerPricingPolicies(cart, customer));
        context.put(PromotionCondition.VAR_CART, cart);
        context.put(PromotionCondition.VAR_CART_ORDER_TOTAL, orderTotal);

        for (final CartItem shipping : cart.getShippingList()) {

            context.put(PromotionCondition.VAR_SHIPPING, shipping);

            applyPromotions(orderPromoBuckets, context);

        }

    }

    /** {@inheritDoc} */
    @Override
    public void applyCustomerPromo(final Customer customer, final MutableShoppingCart cart) {

        if (customer == null) {
            return;
        }

        final List<List<PromoTriplet>> tagPromoBuckets = promotionBuckets.get(Promotion.TYPE_CUSTOMER_TAG);

        if (CollectionUtils.isEmpty(tagPromoBuckets)) {
            return;
        }

        final Map<String, Object> context = new HashMap<>();
        context.put(PromotionCondition.VAR_CONDITION_SUPPORT, this.conditionSupport);
        context.put(PromotionCondition.VAR_CUSTOMER, customer);
        context.put(PromotionCondition.VAR_CUSTOMER_TAGS, getCustomerTags(customer));
        context.put(PromotionCondition.VAR_CUSTOMER_TYPE, getCustomerType(cart, customer));
        context.put(PromotionCondition.VAR_CUSTOMER_PRICING_POLICY, getCustomerPricingPolicies(cart, customer));
        context.put(PromotionCondition.VAR_CART, cart);

        applyPromotions(tagPromoBuckets, context);

    }

    private void applyPromotions(final List<List<PromoTriplet>> promoBuckets,
                                 final Map<String, Object> context) {

        strategy.applyPromotions(promoBuckets, context);

    }

}
