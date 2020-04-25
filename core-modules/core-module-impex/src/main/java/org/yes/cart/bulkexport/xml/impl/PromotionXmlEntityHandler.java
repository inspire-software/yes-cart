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

package org.yes.cart.bulkexport.xml.impl;

import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.PromotionCouponService;

import java.io.OutputStreamWriter;

/**
 * User: denispavlov
 * Date: 04/11/2018
 * Time: 14:30
 */
public class PromotionXmlEntityHandler extends AbstractXmlEntityHandler<Promotion> {

    private PromotionCouponService promotionCouponService;

    private final PromotionCouponXmlEntityHandler couponHandler = new PromotionCouponXmlEntityHandler();

    public PromotionXmlEntityHandler() {
        super("promotions");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, Promotion> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer) throws Exception {

        handleInternal(tagPromotion(null, tuple.getData()), writer, statusListener);

    }

    Tag tagPromotion(final Tag parent, final Promotion promotion) {

        final Tag promoTag = tag(parent, "promotion")
                .attr("id", promotion.getPromotionId())
                .attr("guid", promotion.getGuid())
                .attr("code", promotion.getCode())
                .attr("shop", promotion.getShopCode())
                .attr("currency", promotion.getCurrency())
                .attr("rank", promotion.getRank())
                .tagCdata("name", promotion.getName())
                .tagI18n("display-name", promotion.getDisplayName())
                .tagCdata("description", promotion.getDescription())
                .tagI18n("display-description", promotion.getDisplayDescription())
                .tagList("tags", "tag", promotion.getTag(), ' ')
                .tag("availability")
                    .attr("disabled", !promotion.isEnabled())
                    .tagTime("available-from", promotion.getEnabledFrom())
                    .tagTime("available-to", promotion.getEnabledTo())
                .end()
                .tag("configuration")
                    .attr("action", promotion.getPromoAction())
                    .attr("action-context", promotion.getPromoActionContext())
                    .attr("type", promotion.getPromoType())
                    .attr("coupon-triggered", promotion.isCouponTriggered())
                    .attr("can-be-combined", promotion.isCanBeCombined())
                    .cdata(promotion.getEligibilityCondition())
                .end();

        if (promotion.isCouponTriggered()) {

            final Tag couponsTag = promoTag.tag("coupons");

            promotionCouponService.findByCriteriaIterator(" where e.promotion.promotionId = ?1", new Object[] { promotion.getPromotionId() }, (coupon) -> {

                couponHandler.tagPromotionCoupon(couponsTag, coupon);

                return true; // retrieve all
            });

            couponsTag.end();

        }

        return promoTag
                .tagTime(promotion)
                .end();

    }

    /**
     * Spring IoC.
     *
     * @param promotionCouponService coupon service
     */
    public void setPromotionCouponService(final PromotionCouponService promotionCouponService) {
        this.promotionCouponService = promotionCouponService;
    }

    /**
     * Spring IoC.
     *
     * @param prettyPrint set pretty print mode (new lines and indents)
     */
    @Override
    public void setPrettyPrint(final boolean prettyPrint) {
        super.setPrettyPrint(prettyPrint);
        this.couponHandler.setPrettyPrint(prettyPrint);
    }

}
