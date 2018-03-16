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
    @Override
    public long getCarrierslaId() {
        return carrierslaId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return carrierslaId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCarrierslaId(final long carrierslaId) {
        this.carrierslaId = carrierslaId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getDisplayDescriptions() {
        return displayDescriptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayDescriptions(final Map<String, String> displayDescriptions) {
        this.displayDescriptions = displayDescriptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getMaxDays() {
        return maxDays;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxDays(final Integer maxDays) {
        this.maxDays = maxDays;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getMinDays() {
        return minDays;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMinDays(final Integer minDays) {
        this.minDays = minDays;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExcludeWeekDays() {
        return excludeWeekDays;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExcludeWeekDays(final String excludeWeekDays) {
        this.excludeWeekDays = excludeWeekDays;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExcludeDates() {
        return excludeDates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExcludeDates(final String excludeDates) {
        this.excludeDates = excludeDates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGuaranteed() {
        return guaranteed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGuaranteed(final boolean guaranteed) {
        this.guaranteed = guaranteed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNamedDay() {
        return namedDay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNamedDay(final boolean namedDay) {
        this.namedDay = namedDay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExcludeCustomerTypes() {
        return excludeCustomerTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExcludeCustomerTypes(final String excludeCustomerTypes) {
        this.excludeCustomerTypes = excludeCustomerTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSlaType() {
        return slaType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSlaType(final String slaType) {
        this.slaType = slaType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getScript() {
        return script;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScript(final String script) {
        this.script = script;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSupportedPaymentGateways() {
        return supportedPaymentGateways;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSupportedPaymentGateways(final String supportedPaymentGateways) {
        this.supportedPaymentGateways = supportedPaymentGateways;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSupportedFulfilmentCentres() {
        return supportedFulfilmentCentres;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSupportedFulfilmentCentres(final String supportedFulfilmentCentres) {
        this.supportedFulfilmentCentres = supportedFulfilmentCentres;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBillingAddressNotRequired() {
        return billingAddressNotRequired;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBillingAddressNotRequired(final boolean billingAddressNotRequired) {
        this.billingAddressNotRequired = billingAddressNotRequired;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDeliveryAddressNotRequired() {
        return deliveryAddressNotRequired;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDeliveryAddressNotRequired(final boolean deliveryAddressNotRequired) {
        this.deliveryAddressNotRequired = deliveryAddressNotRequired;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExternalRef() {
        return externalRef;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExternalRef(final String externalRef) {
        this.externalRef = externalRef;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCarrierId() {
        return carrierId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
