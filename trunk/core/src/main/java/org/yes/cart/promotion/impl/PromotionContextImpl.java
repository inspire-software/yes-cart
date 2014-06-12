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

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.promotion.*;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.shoppingcart.impl.TotalImpl;

import java.util.*;

/**
 * User: denispavlov
 * Date: 13-10-29
 * Time: 8:48 AM
 */
public class PromotionContextImpl implements PromotionContext {

    private final String shopCode;
    private final PromotionApplicationStrategy strategy;
    private final Date timestamp = new Date();

    private final Map<String, List<List<PromoTriplet>>> promotionBuckets = new HashMap<String, List<List<PromoTriplet>>>();
    private final Map<String, PromoTriplet> promotionByCode = new HashMap<String, PromoTriplet>();

    public PromotionContextImpl(final String shopCode, final PromotionApplicationStrategy strategy) {
        this.shopCode = shopCode;
        this.strategy = strategy;
    }

    /**
     * Add promotion triplet to this context.
     *
     * @param promotion original promotion object
     * @param condition precompiled condition
     * @param action action to be performed
     */
    public void addPromotion(final Promotion promotion,
                             final PromotionCondition condition,
                             final PromotionAction action) {

        final PromoTriplet promo = new PromoTripletImpl(promotion, condition, action);

        promotionByCode.put(promotion.getCode(), promo);

        List<List<PromoTriplet>> buckets = promotionBuckets.get(promotion.getPromoType());

        if (buckets == null) {
            buckets = new ArrayList<List<PromoTriplet>>();
            buckets.add(new ArrayList<PromoTriplet>()); // 0th can be combined
            promotionBuckets.put(promotion.getPromoType(), buckets);
        }

        if (promotion.isCanBeCombined()) {
            buckets.get(0).add(promo);
        } else {
            buckets.add(Arrays.asList(promo));
        }
    }

    /** {@inheritDoc} */
    public String getShopCode() {
        return shopCode;
    }

    /** {@inheritDoc} */
    public Date getTimestamp() {
        return timestamp;
    }

    private List<String> getCustomerTags(Customer customer) {
        if (customer != null && customer.getTag() != null) {
            return Arrays.asList(customer.getTag().split(" "));
        }
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    public void applyItemPromo(final Customer customer, final ShoppingCart cart) {

        cart.removeItemPromotions(); // remove all gifts and promo prices

        final List<List<PromoTriplet>> itemPromoBuckets = promotionBuckets.get(Promotion.TYPE_ITEM);

        if (CollectionUtils.isEmpty(itemPromoBuckets)) {
            return;
        }

        for (final CartItem item : cart.getCartItemList()) {

            final Map<String, Object> context = new HashMap<String, Object>() {{
                put(PromotionCondition.VAR_REGISTERED, customer != null);
                put(PromotionCondition.VAR_CUSTOMER, customer);
                put(PromotionCondition.VAR_CUSTOMER_TAGS, getCustomerTags(customer));
                put(PromotionCondition.VAR_CART, cart);
                put(PromotionCondition.VAR_CART_ITEM, item);
            }};

            applyPromotions(itemPromoBuckets, context);

        }

    }

    /** {@inheritDoc} */
    public Total applyOrderPromo(final Customer customer, final ShoppingCart cart, final Total itemTotal) {

        final List<List<PromoTriplet>> orderPromoBuckets = promotionBuckets.get(Promotion.TYPE_ORDER);

        if (CollectionUtils.isEmpty(orderPromoBuckets)) {
            return new TotalImpl().add(itemTotal);
        }

        final Map<String, Object> context = new HashMap<String, Object>() {{
            put(PromotionCondition.VAR_REGISTERED, customer != null);
            put(PromotionCondition.VAR_CUSTOMER, customer);
            put(PromotionCondition.VAR_CUSTOMER_TAGS, getCustomerTags(customer));
            put(PromotionCondition.VAR_CART, cart);
            put(PromotionCondition.VAR_CART_ITEM_TOTAL, itemTotal);
            put(PromotionCondition.VAR_TMP_TOTAL, new TotalImpl().add(itemTotal));
        }};

        applyPromotions(orderPromoBuckets, context);

        return (Total) context.get(PromotionCondition.VAR_TMP_TOTAL);

    }

    /** {@inheritDoc} */
    public Total applyShippingPromo(final Customer customer, final ShoppingCart cart, final Total orderTotal) {

        final List<List<PromoTriplet>> orderPromoBuckets = promotionBuckets.get(Promotion.TYPE_SHIPPING);

        if (CollectionUtils.isEmpty(orderPromoBuckets)) {
            return new TotalImpl().add(orderTotal);
        }

        final Map<String, Object> context = new HashMap<String, Object>() {{
            put(PromotionCondition.VAR_REGISTERED, customer != null);
            put(PromotionCondition.VAR_CUSTOMER, customer);
            put(PromotionCondition.VAR_CUSTOMER_TAGS, getCustomerTags(customer));
            put(PromotionCondition.VAR_CART, cart);
            put(PromotionCondition.VAR_CART_ORDER_TOTAL, orderTotal);
            put(PromotionCondition.VAR_TMP_TOTAL, new TotalImpl().add(orderTotal));
        }};

        applyPromotions(orderPromoBuckets, context);

        return (Total) context.get(PromotionCondition.VAR_TMP_TOTAL);
    }

    /** {@inheritDoc} */
    public void applyCustomerPromo(final Customer customer, final ShoppingCart cart) {

        if (customer == null) {
            return;
        }

        final List<List<PromoTriplet>> tagPromoBuckets = promotionBuckets.get(Promotion.TYPE_CUSTOMER_TAG);

        if (CollectionUtils.isEmpty(tagPromoBuckets)) {
            return;
        }

        final Map<String, Object> context = new HashMap<String, Object>() {{
            put(PromotionCondition.VAR_CUSTOMER, customer);
            put(PromotionCondition.VAR_CUSTOMER_TAGS, getCustomerTags(customer));
            put(PromotionCondition.VAR_CART, cart);
        }};

        applyPromotions(tagPromoBuckets, context);

    }

    private void applyPromotions(final List<List<PromoTriplet>> promoBuckets,
                                 final Map<String, Object> context) {

        strategy.applyPromotions(promoBuckets, context);

    }

}
