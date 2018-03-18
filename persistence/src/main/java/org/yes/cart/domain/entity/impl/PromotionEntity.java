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

package org.yes.cart.domain.entity.impl;

import org.yes.cart.domain.entity.Promotion;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * User: denispavlov
 * Date: 13-10-18
 * Time: 5:06 PM
 */
public class PromotionEntity implements Promotion, java.io.Serializable {

    private long promotionId;
    private long version;

    private String shopCode;
    private String currency;
    private String code;
    private String promoType;
    private String promoAction;

    private String eligibilityCondition;

    private String promoActionContext;

    private String tag;

    private int rank;

    private String name;
    private String displayName;
    private String description;
    private String displayDescription;

    private boolean couponTriggered;
    private boolean canBeCombined;
    private boolean enabled;
    private LocalDateTime enabledFrom;
    private LocalDateTime enabledTo;

    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    @Override
    public long getId() {
        return promotionId;
    }

    @Override
    public long getPromotionId() {
        return promotionId;
    }

    @Override
    public void setPromotionId(final long promotionId) {
        this.promotionId = promotionId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    @Override
    public String getShopCode() {
        return shopCode;
    }

    @Override
    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    public String getPromoType() {
        return promoType;
    }

    @Override
    public void setPromoType(final String promoType) {
        this.promoType = promoType;
    }

    @Override
    public String getPromoAction() {
        return promoAction;
    }

    @Override
    public void setPromoAction(final String promoAction) {
        this.promoAction = promoAction;
    }

    @Override
    public String getEligibilityCondition() {
        return eligibilityCondition;
    }

    @Override
    public void setEligibilityCondition(final String eligibilityCondition) {
        this.eligibilityCondition = eligibilityCondition;
    }

    @Override
    public String getPromoActionContext() {
        return promoActionContext;
    }

    @Override
    public void setPromoActionContext(final String promoActionContext) {
        this.promoActionContext = promoActionContext;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public void setTag(final String tag) {
        this.tag = tag;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public String getDisplayDescription() {
        return displayDescription;
    }

    @Override
    public void setDisplayDescription(final String displayDescription) {
        this.displayDescription = displayDescription;
    }

    @Override
    public boolean isCouponTriggered() {
        return couponTriggered;
    }

    @Override
    public void setCouponTriggered(final boolean couponTriggered) {
        this.couponTriggered = couponTriggered;
    }

    @Override
    public boolean isCanBeCombined() {
        return canBeCombined;
    }

    @Override
    public void setCanBeCombined(final boolean canBeCombined) {
        this.canBeCombined = canBeCombined;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public LocalDateTime getEnabledFrom() {
        return enabledFrom;
    }

    @Override
    public void setEnabledFrom(final LocalDateTime enabledFrom) {
        this.enabledFrom = enabledFrom;
    }

    @Override
    public LocalDateTime getEnabledTo() {
        return enabledTo;
    }

    @Override
    public void setEnabledTo(final LocalDateTime enabledTo) {
        this.enabledTo = enabledTo;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return guid;
    }

    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }

    @Override
    public int getRank() {
        return rank;
    }

    @Override
    public void setRank(final int rank) {
        this.rank = rank;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "PromotionEntity{" +
                "shopCode='" + shopCode + '\'' +
                ", currency='" + currency + '\'' +
                ", code='" + code + '\'' +
                ", rank=" + rank +
                ", promoType='" + promoType + '\'' +
                ", promoAction='" + promoAction + '\'' +
                ", eligibilityCondition='" + eligibilityCondition + '\'' +
                ", promoActionContext='" + promoActionContext + '\'' +
                ", enabled=" + enabled +
                ", enabledFrom=" + enabledFrom +
                ", enabledTo=" + enabledTo +
                '}';
    }
}
