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
import org.yes.cart.domain.dto.PromotionCouponDTO;

import java.time.Instant;

/**
 * User: denispavlov
 * Date: 13-10-22
 * Time: 5:37 PM
 */
@Dto
public class PromotionCouponDTOImpl implements PromotionCouponDTO {

    @DtoField(value = "promotioncouponId", readOnly = true)
    private long promotioncouponId;

    @DtoField(value = "promotion.promotionId", readOnly = true)
    private long promotionId;

    @DtoField(value = "code", readOnly = true)
    private String code;
    @DtoField(value = "usageLimit", readOnly = true)
    private int usageLimit;
    @DtoField(value = "usageLimitPerCustomer", readOnly = true)
    private int usageLimitPerCustomer;
    @DtoField(value = "usageCount", readOnly = true)
    private int usageCount;


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
        return promotioncouponId;
    }

    /** {@inheritDoc} */
    @Override
    public long getPromotioncouponId() {
        return promotioncouponId;
    }

    /** {@inheritDoc} */
    @Override
    public void setPromotioncouponId(long promotioncouponId) {
        this.promotioncouponId = promotioncouponId;
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
    public int getUsageLimit() {
        return usageLimit;
    }

    /** {@inheritDoc} */
    @Override
    public void setUsageLimit(int usageLimit) {
        this.usageLimit = usageLimit;
    }

    /** {@inheritDoc} */
    @Override
    public int getUsageLimitPerCustomer() {
        return usageLimitPerCustomer;
    }

    /** {@inheritDoc} */
    @Override
    public void setUsageLimitPerCustomer(int usageLimitPerCustomer) {
        this.usageLimitPerCustomer = usageLimitPerCustomer;
    }

    /** {@inheritDoc} */
    @Override
    public int getUsageCount() {
        return usageCount;
    }

    /** {@inheritDoc} */
    @Override
    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
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
