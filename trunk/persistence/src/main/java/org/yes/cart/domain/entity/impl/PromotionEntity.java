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

package org.yes.cart.domain.entity.impl;

import org.yes.cart.domain.entity.Promotion;

import java.util.Date;

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

    private String name;
    private String displayName;
    private String description;
    private String displayDescription;

    private boolean canBeCombined;
    private boolean enabled;
    private Date enabledFrom;
    private Date enabledTo;

    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public long getId() {
        return promotionId;
    }

    public long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(final long promotionId) {
        this.promotionId = promotionId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
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

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDisplayDescription() {
        return displayDescription;
    }

    public void setDisplayDescription(final String displayDescription) {
        this.displayDescription = displayDescription;
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

    public Date getEnabledFrom() {
        return enabledFrom;
    }

    public void setEnabledFrom(final Date enabledFrom) {
        this.enabledFrom = enabledFrom;
    }

    public Date getEnabledTo() {
        return enabledTo;
    }

    public void setEnabledTo(final Date enabledTo) {
        this.enabledTo = enabledTo;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(final Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(final Date updatedTimestamp) {
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

    public String getGuid() {
        return guid;
    }

    public void setGuid(final String guid) {
        this.guid = guid;
    }
}
