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

import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.bulkimport.xml.internal.PromotionCouponType;
import org.yes.cart.bulkimport.xml.internal.PromotionType;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.domain.entity.PromotionCoupon;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.PromotionService;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class PromotionXmlEntityHandler extends AbstractXmlEntityHandler<PromotionType, Promotion> implements XmlEntityImportHandler<PromotionType, Promotion> {

    private PromotionService promotionService;

    private XmlEntityImportHandler<PromotionCouponType, PromotionCoupon> promotionCouponTypeXmlEntityImportHandler;

    public PromotionXmlEntityHandler() {
        super("promotion");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final Promotion promotion) {
        this.promotionService.delete(promotion);
        this.promotionService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final Promotion domain, final PromotionType xmlType, final EntityImportModeType mode) {

        domain.setTag(processTags(xmlType.getTags(), domain.getTag()));
        domain.setName(xmlType.getName());
        domain.setDescription(xmlType.getDescription());
        domain.setDisplayName(processI18n(xmlType.getDisplayName(), domain.getDisplayName()));
        domain.setDisplayDescription(processI18n(xmlType.getDisplayDescription(), domain.getDisplayDescription()));

        if (xmlType.getRank() != null) {
            domain.setRank(xmlType.getRank());
        }

        if (xmlType.getAvailability() != null) {
            domain.setEnabled(!xmlType.getAvailability().isDisabled());
            domain.setEnabledFrom(processLDT(xmlType.getAvailability().getAvailableFrom()));
            domain.setEnabledTo(processLDT(xmlType.getAvailability().getAvailableTo()));
        }

        if (xmlType.getConfiguration() != null) {
            domain.setPromoType(xmlType.getConfiguration().getType());
            domain.setPromoAction(xmlType.getConfiguration().getAction());
            domain.setPromoActionContext(xmlType.getConfiguration().getActionContext());
            domain.setEligibilityCondition(xmlType.getConfiguration().getValue());
            domain.setCouponTriggered(xmlType.getConfiguration().isCouponTriggered());
            domain.setCanBeCombined(xmlType.getConfiguration().isCanBeCombined());
        }

        if (domain.getPromotionId() == 0L) {
            this.promotionService.create(domain);
        } else {
            this.promotionService.update(domain);
        }
        this.promotionService.getGenericDao().flush();
        this.promotionService.getGenericDao().evict(domain);

        if (xmlType.getCoupons() != null) {
            for (final PromotionCouponType xmlCouponType : xmlType.getCoupons().getPromotionCoupon()) {

                xmlCouponType.setPromotion(domain.getCode());
                promotionCouponTypeXmlEntityImportHandler.handle(statusListener, null, (ImpExTuple) new XmlImportTupleImpl(xmlCouponType.getCode(), xmlCouponType), null, null);

            }
        }

    }

    @Override
    protected Promotion getOrCreate(final JobStatusListener statusListener, final PromotionType xmlType) {
        Promotion promotion = this.promotionService.findSingleByCriteria(" where e.code = ?1", xmlType.getCode());
        if (promotion != null) {
            return promotion;
        }
        promotion = this.promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        promotion.setGuid(xmlType.getCode());
        promotion.setCode(xmlType.getCode());
        promotion.setShopCode(xmlType.getShop());
        promotion.setCurrency(xmlType.getCurrency());
        return promotion;
    }

    @Override
    protected EntityImportModeType determineImportMode(final PromotionType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Promotion domain) {
        return domain.getPromotionId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param promotionService promotion service
     */
    public void setPromotionService(final PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    /**
     * Spring IoC.
     *
     * @param promotionCouponTypeXmlEntityImportHandler handler
     */
    public void setPromotionCouponTypeXmlEntityImportHandler(final XmlEntityImportHandler<PromotionCouponType, PromotionCoupon> promotionCouponTypeXmlEntityImportHandler) {
        this.promotionCouponTypeXmlEntityImportHandler = promotionCouponTypeXmlEntityImportHandler;
    }
}
