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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.PromotionDTO;

import java.time.Instant;
import java.time.LocalDateTime;
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
    @DtoField(value = "displayName", converter = "i18nModelConverter")
    private Map<String, String> displayNames;
    @DtoField(value = "displayDescription", converter = "i18nModelConverter")
    private Map<String, String> displayDescriptions;

    @DtoField(value = "couponTriggered", readOnly = true)
    private boolean couponTriggered;
    @DtoField(value = "canBeCombined", readOnly = true)
    private boolean canBeCombined;
    @DtoField(value = "enabled")
    private boolean enabled;
    @DtoField(value = "enabledFrom")
    private LocalDateTime enabledFrom;
    @DtoField(value = "enabledTo")
    private LocalDateTime enabledTo;


    @DtoField(readOnly = true)
    private Instant createdTimestamp;
    @DtoField(readOnly = true)
    private Instant updatedTimestamp;
    @DtoField(readOnly = true)
    private String createdBy;
    @DtoField(readOnly = true)
    private String updatedBy;

    /** {@inheritDoc} */
    @Override
    public long getId() {
        return promotionId;
    }

    /** {@inheritDoc} */
    @Override
    public long getPromotionId() {
        return promotionId;
    }

    /** {@inheritDoc} */
    @Override
    public void setPromotionId(final long promotionId) {
        this.promotionId = promotionId;
    }

    /** {@inheritDoc} */
    @Override
    public String getShopCode() {
        return shopCode;
    }

    /** {@inheritDoc} */
    @Override
    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    /** {@inheritDoc} */
    @Override
    public String getCurrency() {
        return currency;
    }

    /** {@inheritDoc} */
    @Override
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    /** {@inheritDoc} */
    @Override
    public String getCode() {
        return code;
    }

    /** {@inheritDoc} */
    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    /** {@inheritDoc} */
    @Override
    public String getPromoType() {
        return promoType;
    }

    /** {@inheritDoc} */
    @Override
    public void setPromoType(final String promoType) {
        this.promoType = promoType;
    }

    /** {@inheritDoc} */
    @Override
    public String getPromoAction() {
        return promoAction;
    }

    /** {@inheritDoc} */
    @Override
    public void setPromoAction(final String promoAction) {
        this.promoAction = promoAction;
    }

    /** {@inheritDoc} */
    @Override
    public String getEligibilityCondition() {
        return eligibilityCondition;
    }

    /** {@inheritDoc} */
    @Override
    public void setEligibilityCondition(final String eligibilityCondition) {
        this.eligibilityCondition = eligibilityCondition;
    }

    /** {@inheritDoc} */
    @Override
    public String getPromoActionContext() {
        return promoActionContext;
    }

    /** {@inheritDoc} */
    @Override
    public void setPromoActionContext(final String promoActionContext) {
        this.promoActionContext = promoActionContext;
    }

    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return tag;
    }

    /** {@inheritDoc} */
    @Override
    public void setTag(final String tag) {
        this.tag = tag;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    /** {@inheritDoc} */
    @Override
    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getDisplayDescriptions() {
        return displayDescriptions;
    }

    /** {@inheritDoc} */
    @Override
    public void setDisplayDescriptions(final Map<String, String> displayDescriptions) {
        this.displayDescriptions = displayDescriptions;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCouponTriggered() {
        return couponTriggered;
    }

    /** {@inheritDoc} */
    @Override
    public void setCouponTriggered(final boolean couponTriggered) {
        this.couponTriggered = couponTriggered;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCanBeCombined() {
        return canBeCombined;
    }

    /** {@inheritDoc} */
    @Override
    public void setCanBeCombined(final boolean canBeCombined) {
        this.canBeCombined = canBeCombined;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /** {@inheritDoc} */
    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /** {@inheritDoc} */
    @Override
    public LocalDateTime getEnabledFrom() {
        return enabledFrom;
    }

    /** {@inheritDoc} */
    @Override
    public void setEnabledFrom(final LocalDateTime enabledFrom) {
        this.enabledFrom = enabledFrom;
    }

    /** {@inheritDoc} */
    @Override
    public LocalDateTime getEnabledTo() {
        return enabledTo;
    }

    /** {@inheritDoc} */
    @Override
    public void setEnabledTo(final LocalDateTime enabledTo) {
        this.enabledTo = enabledTo;
    }

    /** {@inheritDoc} */
    @Override
    public int getRank() {
        return rank;
    }

    /** {@inheritDoc} */
    @Override
    public void setRank(final int rank) {
        this.rank = rank;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    /** {@inheritDoc} */
    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

}
