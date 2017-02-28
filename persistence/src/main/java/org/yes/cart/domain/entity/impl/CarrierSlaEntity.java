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


import org.yes.cart.domain.entity.Carrier;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class CarrierSlaEntity implements org.yes.cart.domain.entity.CarrierSla, java.io.Serializable {

    private long carrierslaId;
    private long version;

    private String name;
    private String displayName;
    private String description;
    private String displayDescription;
    private Integer maxDays;
    private Integer minDays;
    private String excludeWeekDays;
    private String excludeDates;
    private boolean guaranteed;
    private String slaType;
    private String script;
    private String supportedPaymentGateways;
    private List<String> supportedPaymentGatewaysAsList;
    private String supportedFulfilmentCentres;
    private List<String> supportedFulfilmentCentresAsList;
    private boolean billingAddressNotRequired;
    private boolean deliveryAddressNotRequired;
    private String externalRef;
    private Carrier carrier;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public CarrierSlaEntity() {
    }




    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayDescription() {
        return displayDescription;
    }

    public void setDisplayDescription(final String displayDescription) {
        this.displayDescription = displayDescription;
    }

    public Integer getMaxDays() {
        return this.maxDays;
    }

    public void setMaxDays(Integer maxDays) {
        this.maxDays = maxDays;
    }

    public Integer getMinDays() {
        return minDays;
    }

    public void setMinDays(final Integer minDays) {
        this.minDays = minDays;
    }

    public String getExcludeWeekDays() {
        return excludeWeekDays;
    }

    public void setExcludeWeekDays(final String excludeWeekDays) {
        this.excludeWeekDays = excludeWeekDays;
    }

    public String getExcludeDates() {
        return excludeDates;
    }

    public void setExcludeDates(final String excludeDates) {
        this.excludeDates = excludeDates;
    }

    public boolean isGuaranteed() {
        return guaranteed;
    }

    public void setGuaranteed(final boolean guaranteed) {
        this.guaranteed = guaranteed;
    }

    public void setSupportedPaymentGatewaysAsList(final List<String> supportedPaymentGatewaysAsList) {
        this.supportedPaymentGatewaysAsList = supportedPaymentGatewaysAsList;
    }

    public void setSupportedFulfilmentCentresAsList(final List<String> supportedFulfilmentCentresAsList) {
        this.supportedFulfilmentCentresAsList = supportedFulfilmentCentresAsList;
    }

    public String getSlaType() {
        return this.slaType;
    }

    public void setSlaType(String slaType) {
        this.slaType = slaType;
    }

    public String getScript() {
        return this.script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getSupportedPaymentGateways() {
        return supportedPaymentGateways;
    }

    public void setSupportedPaymentGateways(final String supportedPaymentGateways) {
        this.supportedPaymentGateways = supportedPaymentGateways;
        this.supportedPaymentGatewaysAsList = null;
    }

    public List<String> getSupportedPaymentGatewaysAsList() {
        if (supportedPaymentGatewaysAsList == null) {
            if (supportedPaymentGateways != null) {
                supportedPaymentGatewaysAsList = Arrays.asList(supportedPaymentGateways.split(","));
            } else {
                supportedPaymentGatewaysAsList = Collections.emptyList();
            }

        }
        return supportedPaymentGatewaysAsList;
    }

    public String getSupportedFulfilmentCentres() {
        return supportedFulfilmentCentres;
    }

    public void setSupportedFulfilmentCentres(final String supportedFulfilmentCentres) {
        this.supportedFulfilmentCentres = supportedFulfilmentCentres;
        this.supportedFulfilmentCentresAsList = null;
    }

    public List<String> getSupportedFulfilmentCentresAsList() {
        if (supportedFulfilmentCentresAsList == null) {
            if (supportedFulfilmentCentres != null) {
                supportedFulfilmentCentresAsList = Arrays.asList(supportedFulfilmentCentres.split(","));
            } else {
                supportedFulfilmentCentresAsList = Collections.emptyList();
            }

        }
        return supportedFulfilmentCentresAsList;
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

    public String getExternalRef() {
        return externalRef;
    }

    public void setExternalRef(final String externalRef) {
        this.externalRef = externalRef;
    }

    public Carrier getCarrier() {
        return this.carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public long getCarrierslaId() {
        return this.carrierslaId;
    }


    public long getId() {
        return this.carrierslaId;
    }

    public void setCarrierslaId(long carrierslaId) {
        this.carrierslaId = carrierslaId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


