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

package org.yes.cart.bulkimport.xml.impl;

import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.bulkimport.xml.internal.PromotionCouponType;
import org.yes.cart.domain.entity.PromotionCoupon;
import org.yes.cart.service.domain.PromotionCouponService;
import org.yes.cart.service.domain.PromotionService;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class PromotionCouponXmlEntityHandler extends AbstractXmlEntityHandler<PromotionCouponType, PromotionCoupon> implements XmlEntityImportHandler<PromotionCouponType, PromotionCoupon> {

    private PromotionService promotionService;
    private PromotionCouponService promotionCouponService;

    public PromotionCouponXmlEntityHandler() {
        super("promotion-coupon");
    }

    @Override
    protected void delete(final PromotionCoupon promotion) {
        this.promotionCouponService.delete(promotion);
        this.promotionCouponService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final PromotionCoupon domain, final PromotionCouponType xmlType, final EntityImportModeType mode) {

        if (xmlType.getUsageCount() != null) {
            domain.setUsageCount(xmlType.getUsageCount());
        }

        if (xmlType.getConfiguration() != null) {
            domain.setUsageLimit(xmlType.getConfiguration().getMaxLimit());
            domain.setUsageLimitPerCustomer(xmlType.getConfiguration().getCustomerLimit());
        }

        if (domain.getPromotioncouponId() == 0L) {
            this.promotionCouponService.create(domain);
        } else {
            this.promotionCouponService.update(domain);
        }
        this.promotionCouponService.getGenericDao().flush();
        this.promotionCouponService.getGenericDao().evict(domain);

    }

    @Override
    protected PromotionCoupon getOrCreate(final PromotionCouponType xmlType) {
        PromotionCoupon coupon = this.promotionCouponService.findSingleByCriteria(" where e.code = ?1", xmlType.getCode());
        if (coupon != null) {
            return coupon;
        }
        coupon = this.promotionCouponService.getGenericDao().getEntityFactory().getByIface(PromotionCoupon.class);
        coupon.setGuid(xmlType.getCode());
        coupon.setCode(xmlType.getCode());
        coupon.setUsageLimit(1);
        coupon.setUsageLimitPerCustomer(1);
        coupon.setUsageCount(0);
        coupon.setPromotion(promotionService.findSingleByCriteria(" where e.code = ?1", xmlType.getPromotion()));
        return coupon;
    }

    @Override
    protected EntityImportModeType determineImportMode(final PromotionCouponType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final PromotionCoupon domain) {
        return domain.getPromotioncouponId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param promotionCouponService promotion coupon service
     */
    public void setPromotionCouponService(final PromotionCouponService promotionCouponService) {
        this.promotionCouponService = promotionCouponService;
    }

    /**
     * Spring IoC.
     *
     * @param promotionService promotion service
     */
    public void setPromotionService(final PromotionService promotionService) {
        this.promotionService = promotionService;
    }
}
