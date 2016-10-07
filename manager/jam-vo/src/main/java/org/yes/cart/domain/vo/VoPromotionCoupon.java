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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

/**
 * User: denispavlov
 * Date: 13-10-22
 * Time: 5:37 PM
 */
@Dto
public class VoPromotionCoupon {

    @DtoField(value = "promotioncouponId", readOnly = true)
    private long promotioncouponId;

    @DtoField(value = "promotionId")
    private long promotionId;

    @DtoField(value = "code")
    private String code;
    @DtoField(value = "usageLimit")
    private int usageLimit;
    @DtoField(value = "usageLimitPerCustomer")
    private int usageLimitPerCustomer;
    @DtoField(value = "usageCount")
    private int usageCount;

    public long getPromotioncouponId() {
        return promotioncouponId;
    }

    public void setPromotioncouponId(final long promotioncouponId) {
        this.promotioncouponId = promotioncouponId;
    }

    public long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(final long promotionId) {
        this.promotionId = promotionId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
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
}
