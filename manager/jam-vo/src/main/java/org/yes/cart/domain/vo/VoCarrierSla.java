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
import org.yes.cart.domain.misc.MutablePair;

import java.util.List;

/**
 * User: denispavlov
 * Date: 28/07/2016
 * Time: 17:54
 */
@Dto
public class VoCarrierSla {

    @DtoField(value = "carrierslaId", readOnly = true)
    private long carrierslaId;

    @DtoField(value = "carrierId", readOnly = true)
    private long carrierId;

    @DtoField(value = "code")
    private String code;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "maxDays")
    private Integer maxDays;

    @DtoField(value = "slaType")
    private String slaType;

    @DtoField(value = "script")
    private String script;

    @DtoField(value = "displayNames", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayNames;

    @DtoField(value = "displayDescriptions", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayDescriptions;

    @DtoField(value = "supportedPaymentGateways", converter = "CSVToList")
    private List<String> supportedPaymentGateways;

    @DtoField(value = "supportedFulfilmentCentres", converter = "CSVToList")
    private List<String> supportedFulfilmentCentres;

    @DtoField(value = "billingAddressNotRequired")
    private boolean billingAddressNotRequired;

    @DtoField(value = "deliveryAddressNotRequired")
    private boolean deliveryAddressNotRequired;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Integer getMaxDays() {
        return maxDays;
    }

    public void setMaxDays(final Integer maxDays) {
        this.maxDays = maxDays;
    }

    public String getSlaType() {
        return slaType;
    }

    public void setSlaType(final String slaType) {
        this.slaType = slaType;
    }

    public String getScript() {
        return script;
    }

    public void setScript(final String script) {
        this.script = script;
    }

    public long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(final long carrierId) {
        this.carrierId = carrierId;
    }

    public List<MutablePair<String, String>> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(final List<MutablePair<String, String>> displayNames) {
        this.displayNames = displayNames;
    }

    public List<MutablePair<String, String>> getDisplayDescriptions() {
        return displayDescriptions;
    }

    public void setDisplayDescriptions(final List<MutablePair<String, String>> displayDescriptions) {
        this.displayDescriptions = displayDescriptions;
    }

    public List<String> getSupportedPaymentGateways() {
        return supportedPaymentGateways;
    }

    public void setSupportedPaymentGateways(final List<String> supportedPaymentGateways) {
        this.supportedPaymentGateways = supportedPaymentGateways;
    }

    public List<String> getSupportedFulfilmentCentres() {
        return supportedFulfilmentCentres;
    }

    public void setSupportedFulfilmentCentres(final List<String> supportedFulfilmentCentres) {
        this.supportedFulfilmentCentres = supportedFulfilmentCentres;
    }

    public boolean isBillingAddressNotRequired() {
        return billingAddressNotRequired;
    }

    public void setBillingAddressNotRequired(final boolean billingAddressNotRequired) {
        this.billingAddressNotRequired = billingAddressNotRequired;
    }

    public boolean isDeliveryAddressNotRequired() {
        return deliveryAddressNotRequired;
    }

    public void setDeliveryAddressNotRequired(final boolean deliveryAddressNotRequired) {
        this.deliveryAddressNotRequired = deliveryAddressNotRequired;
    }

}
