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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.PromotionCoupon;

import java.util.List;

/**
 * User: denispavlov
 * Date: 13-10-19
 * Time: 11:03 PM
 */
public interface PromotionCouponService extends GenericService<PromotionCoupon> {

    /**
     * Get all coupons for given promotion.
     *
     * @param promotionId promotion PK
     *
     * @return list of coupons
     */
    List<PromotionCoupon> findByPromotionId(Long promotionId);

    /**
     * Create named coupon for given promotion with limits provided.
     *
     * @param promotionId promotion PK
     * @param couponCode coupon code
     * @param limit total usage limit
     * @param limitPerUser usage limit per customer
     */
    void create(Long promotionId, String couponCode, int limit, int limitPerUser);

    /**
     * Create random coupon codes for given promotion with limits provided.
     *
     * @param promotionId promotion PK
     * @param couponCount number of coupons to generate
     * @param limit total usage limit
     * @param limitPerUser usage limit per customer
     */
    void create(Long promotionId, int couponCount, int limit, int limitPerUser);

    /**
     * Get valid (enabled with usage capacity remaining) promotion coupon.
     *
     * @param coupon coupon code
     * @param customerEmail customer email
     *
     * @return coupon code entity or null
     */
    PromotionCoupon findValidPromotionCoupon(final String coupon, final String customerEmail);

    /**
     * Update coupon usage count by counting number of usage records.
     *
     * @param promotionCoupon coupon to update usage for
     * @param offset number of usages to add in addition to count (if usage is being updated
     *               before usage records persisted)
     */
    void updateUsage(PromotionCoupon promotionCoupon, int offset);

}
