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
 * Date: 28/07/2016
 * Time: 17:54
 */
@Dto
public class VoCarrierSlaInfo {

    @DtoField(value = "carrierslaId", readOnly = true)
    private long carrierslaId;

    @DtoField(value = "carrierId", readOnly = true)
    private long carrierId;

    @DtoField(value = "code")
    private String code;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "maxDays")
    private Integer maxDays;

    @DtoField(value = "minDays")
    private Integer minDays;

    @DtoField(value = "guaranteed")
    private boolean guaranteed;

    @DtoField(value = "namedDay")
    private boolean namedDay;

    @DtoField(value = "slaType")
    private String slaType;

    @DtoField(value = "externalRef")
    private String externalRef;

    public long getCarrierslaId() {
        return carrierslaId;
    }

    public void setCarrierslaId(final long carrierslaId) {
        this.carrierslaId = carrierslaId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getMaxDays() {
        return maxDays;
    }

    public void setMaxDays(final Integer maxDays) {
        this.maxDays = maxDays;
    }

    public Integer getMinDays() {
        return minDays;
    }

    public void setMinDays(final Integer minDays) {
        this.minDays = minDays;
    }

    public boolean isGuaranteed() {
        return guaranteed;
    }

    public void setGuaranteed(final boolean guaranteed) {
        this.guaranteed = guaranteed;
    }

    public boolean isNamedDay() {
        return namedDay;
    }

    public void setNamedDay(final boolean namedDay) {
        this.namedDay = namedDay;
    }

    public String getSlaType() {
        return slaType;
    }

    public void setSlaType(final String slaType) {
        this.slaType = slaType;
    }

    public long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(final long carrierId) {
        this.carrierId = carrierId;
    }

    public String getExternalRef() {
        return externalRef;
    }

    public void setExternalRef(final String externalRef) {
        this.externalRef = externalRef;
    }
}
