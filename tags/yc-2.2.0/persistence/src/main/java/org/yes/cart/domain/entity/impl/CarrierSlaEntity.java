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


import org.yes.cart.domain.entity.Carrier;

import java.math.BigDecimal;
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
    private String currency;
    private Integer maxDays;
    private String slaType;
    private BigDecimal price;
    private BigDecimal percent;
    private String script;
    private BigDecimal priceNotLess;
    private BigDecimal percentNotLess;
    private BigDecimal costNotLess;
    private String supportedPaymentGateways;
    private List<String> supportedPaymentGatewaysAsList;
    private boolean billingAddressNotRequired;
    private boolean deliveryAddressNotRequired;
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

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getMaxDays() {
        return this.maxDays;
    }

    public void setMaxDays(Integer maxDays) {
        this.maxDays = maxDays;
    }

    public String getSlaType() {
        return this.slaType;
    }

    public void setSlaType(String slaType) {
        this.slaType = slaType;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPercent() {
        return this.percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    public String getScript() {
        return this.script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public BigDecimal getPriceNotLess() {
        return this.priceNotLess;
    }

    public void setPriceNotLess(BigDecimal priceNotLess) {
        this.priceNotLess = priceNotLess;
    }

    public BigDecimal getPercentNotLess() {
        return this.percentNotLess;
    }

    public void setPercentNotLess(BigDecimal percentNotLess) {
        this.percentNotLess = percentNotLess;
    }

    public BigDecimal getCostNotLess() {
        return this.costNotLess;
    }

    public void setCostNotLess(BigDecimal costNotLess) {
        this.costNotLess = costNotLess;
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


