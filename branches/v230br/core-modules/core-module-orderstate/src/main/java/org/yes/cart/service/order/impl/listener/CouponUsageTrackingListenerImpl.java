/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.order.impl.listener;

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.PromotionCouponUsage;
import org.yes.cart.service.domain.PromotionCouponService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderStateTransitionListener;

/**
 * User: denispavlov
 * Date: 21/04/2015
 * Time: 18:29
 */
public class CouponUsageTrackingListenerImpl implements OrderStateTransitionListener {

    private final PromotionCouponService promotionCouponService;
    private int usageOffset = 1;

    public CouponUsageTrackingListenerImpl(final PromotionCouponService promotionCouponService) {
        this.promotionCouponService = promotionCouponService;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onEvent(final OrderEvent orderEvent) {

        final CustomerOrder customerOrder = orderEvent.getCustomerOrder();

        if (CollectionUtils.isNotEmpty(customerOrder.getCoupons())) {
            // We can iterate over usages as they are not lazy BUT coupon we need to get fresh in this session
            for (final PromotionCouponUsage usage : customerOrder.getCoupons()) {

                promotionCouponService.updateUsage(
                        promotionCouponService.findById(usage.getCoupon().getPromotioncouponId()),
                        usageOffset);

            }

        }

        return true;
    }

    /**
     * Usage offset (e.g. 1 indicates coupon used, -1 indicates coupon usage revoked)
     *
     * @param usageOffset usage offset
     */
    public void setUsageOffset(final int usageOffset) {
        this.usageOffset = usageOffset;
    }
}
