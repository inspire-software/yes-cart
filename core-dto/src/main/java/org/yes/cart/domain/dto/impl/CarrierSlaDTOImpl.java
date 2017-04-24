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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.CarrierSlaDTO;

import java.math.BigDecimal;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class CarrierSlaDTOImpl implements CarrierSlaDTO {

    @DtoField(value = "carrierslaId", readOnly = true)
    private long carrierslaId;

    @DtoField(value = "guid")
    private String code;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "maxDays")
    private Integer maxDays;

    @DtoField(value = "minDays")
    private Integer minDays;

    @DtoField(value = "excludeWeekDays")
    private String excludeWeekDays;

    @DtoField(value = "excludeDates")
    private String excludeDates;

    @DtoField(value = "guaranteed")
    private boolean guaranteed;

    @DtoField(value = "namedDay")
    private boolean namedDay;

    @DtoField(value = "excludeCustomerTypes")
    private String excludeCustomerTypes;

    @DtoField(value = "slaType")
    private String slaType;

    @DtoField(value = "script")
    private String script;

    @DtoField(
            value = "carrier",
            converter = "carrierId2Carrier",
            entityBeanKeys = "org.yes.cart.domain.entity.Carrier"
    )
    private long carrierId;

    @DtoField(value = "displayName", converter = "i18nStringConverter")
    private Map<String, String> displayNames;

    @DtoField(value = "displayDescription", converter = "i18nStringConverter")
    private Map<String, String> displayDescriptions;

    @DtoField(value = "supportedPaymentGateways")
    private String supportedPaymentGateways;

    @DtoField(value = "supportedFulfilmentCentres")
    private String supportedFulfilmentCentres;

    @DtoField(value = "billingAddressNotRequired")
    private boolean billingAddressNotRequired;

    @DtoField(value = "deliveryAddressNotRequired")
    private boolean deliveryAddressNotRequired;

    @DtoField(value = "externalRef")
    private String externalRef;

    /** {@inheritDoc} */
    public long getCarrierslaId() {
        return carrierslaId;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return carrierslaId;
    }

    /**
     * {@inheritDoc}
     */
    public void setCarrierslaId(final long carrierslaId) {
        this.carrierslaId = carrierslaId;
    }

    /**
     * {@inheritDoc}
     */
    public String getCode() {
        return code;
    }

    /**
     * {@inheritDoc}
     */
    public void setCode(final String code) {
        this.code = code;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    /**
     * {@inheritDoc}
     */
    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getDisplayDescriptions() {
        return displayDescriptions;
    }

    /**
     * {@inheritDoc}
     */
    public void setDisplayDescriptions(final Map<String, String> displayDescriptions) {
        this.displayDescriptions = displayDescriptions;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getMaxDays() {
        return maxDays;
    }

    /**
     * {@inheritDoc}
     */
    public void setMaxDays(final Integer maxDays) {
        this.maxDays = maxDays;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getMinDays() {
        return minDays;
    }

    /**
     * {@inheritDoc}
     */
    public void setMinDays(final Integer minDays) {
        this.minDays = minDays;
    }

    /**
     * {@inheritDoc}
     */
    public String getExcludeWeekDays() {
        return excludeWeekDays;
    }

    /**
     * {@inheritDoc}
     */
    public void setExcludeWeekDays(final String excludeWeekDays) {
        this.excludeWeekDays = excludeWeekDays;
    }

    /**
     * {@inheritDoc}
     */
    public String getExcludeDates() {
        return excludeDates;
    }

    /**
     * {@inheritDoc}
     */
    public void setExcludeDates(final String excludeDates) {
        this.excludeDates = excludeDates;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isGuaranteed() {
        return guaranteed;
    }

    /**
     * {@inheritDoc}
     */
    public void setGuaranteed(final boolean guaranteed) {
        this.guaranteed = guaranteed;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNamedDay() {
        return namedDay;
    }

    /**
     * {@inheritDoc}
     */
    public void setNamedDay(final boolean namedDay) {
        this.namedDay = namedDay;
    }

    /**
     * {@inheritDoc}
     */
    public String getExcludeCustomerTypes() {
        return excludeCustomerTypes;
    }

    /**
     * {@inheritDoc}
     */
    public void setExcludeCustomerTypes(final String excludeCustomerTypes) {
        this.excludeCustomerTypes = excludeCustomerTypes;
    }

    /**
     * {@inheritDoc}
     */
    public String getSlaType() {
        return slaType;
    }

    /**
     * {@inheritDoc}
     */
    public void setSlaType(final String slaType) {
        this.slaType = slaType;
    }

    /**
     * {@inheritDoc}
     */
    public String getScript() {
        return script;
    }

    /**
     * {@inheritDoc}
     */
    public void setScript(final String script) {
        this.script = script;
    }

    /**
     * {@inheritDoc}
     */
    public String getSupportedPaymentGateways() {
        return supportedPaymentGateways;
    }

    /**
     * {@inheritDoc}
     */
    public void setSupportedPaymentGateways(final String supportedPaymentGateways) {
        this.supportedPaymentGateways = supportedPaymentGateways;
    }

    /**
     * {@inheritDoc}
     */
    public String getSupportedFulfilmentCentres() {
        return supportedFulfilmentCentres;
    }

    /**
     * {@inheritDoc}
     */
    public void setSupportedFulfilmentCentres(final String supportedFulfilmentCentres) {
        this.supportedFulfilmentCentres = supportedFulfilmentCentres;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBillingAddressNotRequired() {
        return billingAddressNotRequired;
    }

    /**
     * {@inheritDoc}
     */
    public void setBillingAddressNotRequired(final boolean billingAddressNotRequired) {
        this.billingAddressNotRequired = billingAddressNotRequired;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDeliveryAddressNotRequired() {
        return deliveryAddressNotRequired;
    }

    /**
     * {@inheritDoc}
     */
    public void setDeliveryAddressNotRequired(final boolean deliveryAddressNotRequired) {
        this.deliveryAddressNotRequired = deliveryAddressNotRequired;
    }

    /**
     * {@inheritDoc}
     */
    public String getExternalRef() {
        return externalRef;
    }

    /**
     * {@inheritDoc}
     */
    public void setExternalRef(final String externalRef) {
        this.externalRef = externalRef;
    }

    /**
     * {@inheritDoc}
     */
    public long getCarrierId() {
        return carrierId;
    }

    /**
     * {@inheritDoc}
     */
    public void setCarrierId(final long carrierId) {
        this.carrierId = carrierId;
    }

    @Override
    public String toString() {
        return "CarrierSlaDTOImpl{" +
                "carrierslaId=" + carrierslaId +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", maxDays=" + maxDays +
                ", slaType='" + slaType + '\'' +
                ", script='" + script + '\'' +
                ", carrierId=" + carrierId +
                '}';
    }
}
