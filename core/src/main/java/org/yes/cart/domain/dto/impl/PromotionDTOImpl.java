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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.PromotionDTO;

import java.util.Date;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-10-22
 * Time: 5:37 PM
 */
@Dto
public class PromotionDTOImpl implements PromotionDTO {

    @DtoField(value = "promotionId", readOnly = true)
    private long promotionId;

    @DtoField(value = "shopCode", readOnly = true)
    private String shopCode;
    @DtoField(value = "currency", readOnly = true)
    private String currency;
    @DtoField(value = "code", readOnly = true)
    private String code;
    @DtoField(value = "promoType", readOnly = true)
    private String promoType;
    @DtoField(value = "promoAction", readOnly = true)
    private String promoAction;

    @DtoField(value = "eligibilityCondition", readOnly = true)
    private String eligibilityCondition;

    @DtoField(value = "promoActionContext", readOnly = true)
    private String promoActionContext;

    @DtoField(value = "tag")
    private String tag;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "name")
    private String name;
    @DtoField(value = "description")
    private String description;
    @DtoField(value = "displayName", converter = "i18nStringConverter")
    private Map<String, String> displayNames;
    @DtoField(value = "displayDescription", converter = "i18nStringConverter")
    private Map<String, String> displayDescriptions;

    @DtoField(value = "couponTriggered", readOnly = true)
    private boolean couponTriggered;
    @DtoField(value = "canBeCombined", readOnly = true)
    private boolean canBeCombined;
    @DtoField(value = "enabled")
    private boolean enabled;
    @DtoField(value = "enabledFrom")
    private Date enabledFrom;
    @DtoField(value = "enabledTo")
    private Date enabledTo;

    /** {@inheritDoc} */
    public long getId() {
        return promotionId;
    }

    /** {@inheritDoc} */
    public long getPromotionId() {
        return promotionId;
    }

    /** {@inheritDoc} */
    public void setPromotionId(final long promotionId) {
        this.promotionId = promotionId;
    }

    /** {@inheritDoc} */
    public String getShopCode() {
        return shopCode;
    }

    /** {@inheritDoc} */
    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    /** {@inheritDoc} */
    public String getCurrency() {
        return currency;
    }

    /** {@inheritDoc} */
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    /** {@inheritDoc} */
    public String getCode() {
        return code;
    }

    /** {@inheritDoc} */
    public void setCode(final String code) {
        this.code = code;
    }

    /** {@inheritDoc} */
    public String getPromoType() {
        return promoType;
    }

    /** {@inheritDoc} */
    public void setPromoType(final String promoType) {
        this.promoType = promoType;
    }

    /** {@inheritDoc} */
    public String getPromoAction() {
        return promoAction;
    }

    /** {@inheritDoc} */
    public void setPromoAction(final String promoAction) {
        this.promoAction = promoAction;
    }

    /** {@inheritDoc} */
    public String getEligibilityCondition() {
        return eligibilityCondition;
    }

    /** {@inheritDoc} */
    public void setEligibilityCondition(final String eligibilityCondition) {
        this.eligibilityCondition = eligibilityCondition;
    }

    /** {@inheritDoc} */
    public String getPromoActionContext() {
        return promoActionContext;
    }

    /** {@inheritDoc} */
    public void setPromoActionContext(final String promoActionContext) {
        this.promoActionContext = promoActionContext;
    }

    /** {@inheritDoc} */
    public String getTag() {
        return tag;
    }

    /** {@inheritDoc} */
    public void setTag(final String tag) {
        this.tag = tag;
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    /** {@inheritDoc} */
    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    /** {@inheritDoc} */
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    public void setDescription(final String description) {
        this.description = description;
    }

    /** {@inheritDoc} */
    public Map<String, String> getDisplayDescriptions() {
        return displayDescriptions;
    }

    /** {@inheritDoc} */
    public void setDisplayDescriptions(final Map<String, String> displayDescriptions) {
        this.displayDescriptions = displayDescriptions;
    }

    /** {@inheritDoc} */
    public boolean isCouponTriggered() {
        return couponTriggered;
    }

    /** {@inheritDoc} */
    public void setCouponTriggered(final boolean couponTriggered) {
        this.couponTriggered = couponTriggered;
    }

    /** {@inheritDoc} */
    public boolean isCanBeCombined() {
        return canBeCombined;
    }

    /** {@inheritDoc} */
    public void setCanBeCombined(final boolean canBeCombined) {
        this.canBeCombined = canBeCombined;
    }

    /** {@inheritDoc} */
    public boolean isEnabled() {
        return enabled;
    }

    /** {@inheritDoc} */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /** {@inheritDoc} */
    public Date getEnabledFrom() {
        return enabledFrom;
    }

    /** {@inheritDoc} */
    public void setEnabledFrom(final Date enabledFrom) {
        this.enabledFrom = enabledFrom;
    }

    /** {@inheritDoc} */
    public Date getEnabledTo() {
        return enabledTo;
    }

    /** {@inheritDoc} */
    public void setEnabledTo(final Date enabledTo) {
        this.enabledTo = enabledTo;
    }

    /** {@inheritDoc} */
    public int getRank() {
        return rank;
    }

    /** {@inheritDoc} */
    public void setRank(final int rank) {
        this.rank = rank;
    }
}
