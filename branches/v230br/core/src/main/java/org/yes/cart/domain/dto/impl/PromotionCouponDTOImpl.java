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
import org.yes.cart.domain.dto.PromotionCouponDTO;

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

    /** {@inheritDoc} */
    public long getId() {
        return promotioncouponId;
    }

    /** {@inheritDoc} */
    public long getPromotioncouponId() {
        return promotioncouponId;
    }

    /** {@inheritDoc} */
    public void setPromotioncouponId(long promotioncouponId) {
        this.promotioncouponId = promotioncouponId;
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
    public String getCode() {
        return code;
    }

    /** {@inheritDoc} */
    public void setCode(final String code) {
        this.code = code;
    }

    /** {@inheritDoc} */
    public int getUsageLimit() {
        return usageLimit;
    }

    /** {@inheritDoc} */
    public void setUsageLimit(int usageLimit) {
        this.usageLimit = usageLimit;
    }

    /** {@inheritDoc} */
    public int getUsageLimitPerCustomer() {
        return usageLimitPerCustomer;
    }

    /** {@inheritDoc} */
    public void setUsageLimitPerCustomer(int usageLimitPerCustomer) {
        this.usageLimitPerCustomer = usageLimitPerCustomer;
    }

    /** {@inheritDoc} */
    public int getUsageCount() {
        return usageCount;
    }

    /** {@inheritDoc} */
    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

}
