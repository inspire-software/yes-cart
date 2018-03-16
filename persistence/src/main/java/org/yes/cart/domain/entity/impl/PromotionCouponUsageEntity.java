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

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.PromotionCoupon;
import org.yes.cart.domain.entity.PromotionCouponUsage;

import java.time.Instant;

/**
 * User: denispavlov
 * Date: 13-10-18
 * Time: 5:06 PM
 */
public class PromotionCouponUsageEntity implements PromotionCouponUsage, java.io.Serializable {

    private long promotioncouponusageId;
    private long version;

    private PromotionCoupon coupon;
    private String customerEmail;
    private CustomerOrder customerOrder;

    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;

    @Override
    public long getId() {
        return promotioncouponusageId;
    }

    @Override
    public long getPromotioncouponusageId() {
        return promotioncouponusageId;
    }

    @Override
    public void setPromotioncouponusageId(final long promotioncouponusageId) {
        this.promotioncouponusageId = promotioncouponusageId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    @Override
    public PromotionCoupon getCoupon() {
        return coupon;
    }

    @Override
    public void setCoupon(final PromotionCoupon coupon) {
        this.coupon = coupon;
    }

    @Override
    public String getCustomerEmail() {
        return customerEmail;
    }

    @Override
    public void setCustomerEmail(final String customerEmail) {
        this.customerEmail = customerEmail;
    }

    @Override
    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    @Override
    public void setCustomerOrder(final CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
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
        return String.valueOf(promotioncouponusageId);
    }

    @Override
    public void setGuid(final String guid) {
        // nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "PromotionCouponUsageEntity{" +
                "promotioncouponusageId=" + promotioncouponusageId +
                ", version=" + version +
                ", customerEmail='" + customerEmail + '\'' +
                ", createdTimestamp=" + createdTimestamp +
                ", updatedTimestamp=" + updatedTimestamp +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}
