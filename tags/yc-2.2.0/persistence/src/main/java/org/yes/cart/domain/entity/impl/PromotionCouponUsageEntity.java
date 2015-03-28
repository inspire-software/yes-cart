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

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.PromotionCoupon;
import org.yes.cart.domain.entity.PromotionCouponUsage;

import java.util.Date;

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

    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;

    public long getId() {
        return promotioncouponusageId;
    }

    public long getPromotioncouponusageId() {
        return promotioncouponusageId;
    }

    public void setPromotioncouponusageId(final long promotioncouponusageId) {
        this.promotioncouponusageId = promotioncouponusageId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    public PromotionCoupon getCoupon() {
        return coupon;
    }

    public void setCoupon(final PromotionCoupon coupon) {
        this.coupon = coupon;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(final String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(final CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
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
        return String.valueOf(promotioncouponusageId);
    }

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
