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
import org.yes.cart.domain.entity.PromotionCoupon;

import java.util.Date;

/**
 * User: denispavlov
 * Date: 13-10-18
 * Time: 5:06 PM
 */
public class PromotionCouponEntity implements PromotionCoupon, java.io.Serializable {

    private long promotioncouponId;
    private long version;

    private String code;
    private Promotion promotion;

    private int usageLimit;
    private int usageLimitPerCustomer;
    private int usageCount;

    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;

    public long getId() {
        return promotioncouponId;
    }

    public long getPromotioncouponId() {
        return promotioncouponId;
    }

    public void setPromotioncouponId(final long promotioncouponId) {
        this.promotioncouponId = promotioncouponId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(final Promotion promotion) {
        this.promotion = promotion;
    }

    public int getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(final int usageLimit) {
        this.usageLimit = usageLimit;
    }

    public int getUsageLimitPerCustomer() {
        return usageLimitPerCustomer;
    }

    public void setUsageLimitPerCustomer(final int usageLimitPerCustomer) {
        this.usageLimitPerCustomer = usageLimitPerCustomer;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(final int usageCount) {
        this.usageCount = usageCount;
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
        return code;
    }

    public void setGuid(final String guid) {
        this.code = guid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "PromotionCouponEntity{" +
                "promotioncouponId=" + promotioncouponId +
                ", version=" + version +
                ", code='" + code + '\'' +
                ", usageLimit=" + usageLimit +
                ", usageLimitPerCustomer=" + usageLimitPerCustomer +
                ", usageCount=" + usageCount +
                ", createdTimestamp=" + createdTimestamp +
                ", updatedTimestamp=" + updatedTimestamp +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}
