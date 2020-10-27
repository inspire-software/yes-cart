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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.PromotionCoupon;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-10-19
 * Time: 11:03 PM
 */
public interface PromotionCouponService extends GenericService<PromotionCoupon> {

    /**
     * Get all coupons for given promotion.
     *
     * @param couponCode coupon code
     *
     * @return coupon
     */
    PromotionCoupon findByCode(String couponCode);

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
     * @param customerLogin customer login
     *
     * @return coupon code entity or null
     */
    PromotionCoupon findValidPromotionCoupon(final String coupon, final String customerLogin);

    /**
     * Update coupon usage count by counting number of usage records.
     *
     * @param promotionCoupon coupon to update usage for
     * @param offset number of usages to add in addition to count (if usage is being updated
     *               before usage records persisted)
     */
    void updateUsage(PromotionCoupon promotionCoupon, int offset);

    /**
     * Remove all coupons by promotion ID
     *
     * @param promotionId promo ID
     */
    void removeAll(long promotionId);



    /**
     * Find promotion coupons by given search criteria. Search will be performed using like operation.
     *
     * @param start             start
     * @param offset            page size
     * @param sort              optional sort property
     * @param sortDescending    optional sort property direction
     * @param filter            optional filters (e.g. name, guid)
     *
     * @return list.
     */
    List<PromotionCoupon> findPromotionCoupons(int start,
                                               int offset,
                                               String sort,
                                               boolean sortDescending,
                                               Map<String, List> filter);

    /**
     * Find promotion coupons by given search criteria. Search will be performed using like operation.
     *
     * @param filter            optional filters (e.g. name, guid)
     *
     * @return count
     */
    int findPromotionCouponCount(Map<String, List> filter);




}
