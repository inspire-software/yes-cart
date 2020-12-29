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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.misc.MutablePair;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13-10-22
 * Time: 5:37 PM
 */
@Dto
public class VoPromotion {

    @DtoField(value = "promotionId", readOnly = true)
    private long promotionId;

    @DtoField(value = "shopCode")
    private String shopCode;
    @DtoField(value = "currency")
    private String currency;
    @DtoField(value = "code")
    private String code;
    @DtoField(value = "promoType")
    private String promoType;
    @DtoField(value = "promoAction")
    private String promoAction;

    @DtoField(value = "eligibilityCondition")
    private String eligibilityCondition;

    @DtoField(value = "promoActionContext")
    private String promoActionContext;

    @DtoField(value = "tag")
    private String tag;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "name")
    private String name;
    @DtoField(value = "description")
    private String description;

    @DtoField(value = "displayNames", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayNames;

    @DtoField(value = "displayDescriptions", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayDescriptions;

    @DtoField(value = "couponTriggered")
    private boolean couponTriggered;
    @DtoField(value = "canBeCombined")
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

    public long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(final long promotionId) {
        this.promotionId = promotionId;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getPromoType() {
        return promoType;
    }

    public void setPromoType(final String promoType) {
        this.promoType = promoType;
    }

    public String getPromoAction() {
        return promoAction;
    }

    public void setPromoAction(final String promoAction) {
        this.promoAction = promoAction;
    }

    public String getEligibilityCondition() {
        return eligibilityCondition;
    }

    public void setEligibilityCondition(final String eligibilityCondition) {
        this.eligibilityCondition = eligibilityCondition;
    }

    public String getPromoActionContext() {
        return promoActionContext;
    }

    public void setPromoActionContext(final String promoActionContext) {
        this.promoActionContext = promoActionContext;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<MutablePair<String, String>> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(final List<MutablePair<String, String>> displayNames) {
        this.displayNames = displayNames;
    }

    public List<MutablePair<String, String>> getDisplayDescriptions() {
        return displayDescriptions;
    }

    public void setDisplayDescriptions(final List<MutablePair<String, String>> displayDescriptions) {
        this.displayDescriptions = displayDescriptions;
    }

    public boolean isCouponTriggered() {
        return couponTriggered;
    }

    public void setCouponTriggered(final boolean couponTriggered) {
        this.couponTriggered = couponTriggered;
    }

    public boolean isCanBeCombined() {
        return canBeCombined;
    }

    public void setCanBeCombined(final boolean canBeCombined) {
        this.canBeCombined = canBeCombined;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getEnabledFrom() {
        return enabledFrom;
    }

    public void setEnabledFrom(final LocalDateTime enabledFrom) {
        this.enabledFrom = enabledFrom;
    }

    public LocalDateTime getEnabledTo() {
        return enabledTo;
    }

    public void setEnabledTo(final LocalDateTime enabledTo) {
        this.enabledTo = enabledTo;
    }

    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
