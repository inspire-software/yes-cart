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

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "currency")
    private String currency;

    @DtoField(value = "maxDays")
    private Integer maxDays;

    @DtoField(value = "slaType")
    private String slaType;

    @DtoField(value = "price")
    private BigDecimal price;

    @DtoField(value = "percent")
    private BigDecimal percent;

    @DtoField(value = "script")
    private String script;

    @DtoField(value = "priceNotLess")
    private BigDecimal priceNotLess;

    @DtoField(value = "percentNotLess")
    private BigDecimal percentNotLess;

    @DtoField(value = "costNotLess")
    private BigDecimal costNotLess;


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

    @DtoField(value = "billingAddressNotRequired")
    private boolean billingAddressNotRequired;

    @DtoField(value = "deliveryAddressNotRequired")
    private boolean deliveryAddressNotRequired;

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
    public String getCurrency() {
        return currency;
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrency(final String currency) {
        this.currency = currency;
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
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * {@inheritDoc}
     */
    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getPercent() {
        return percent;
    }

    /**
     * {@inheritDoc}
     */
    public void setPercent(final BigDecimal percent) {
        this.percent = percent;
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
    public BigDecimal getPriceNotLess() {
        return priceNotLess;
    }

    /**
     * {@inheritDoc}
     */
    public void setPriceNotLess(final BigDecimal priceNotLess) {
        this.priceNotLess = priceNotLess;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getPercentNotLess() {
        return percentNotLess;
    }

    /**
     * {@inheritDoc}
     */
    public void setPercentNotLess(final BigDecimal percentNotLess) {
        this.percentNotLess = percentNotLess;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getCostNotLess() {
        return costNotLess;
    }

    /**
     * {@inheritDoc}
     */
    public void setCostNotLess(final BigDecimal costNotLess) {
        this.costNotLess = costNotLess;
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
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", currency='" + currency + '\'' +
                ", maxDays=" + maxDays +
                ", slaType='" + slaType + '\'' +
                ", price=" + price +
                ", percent=" + percent +
                ", script='" + script + '\'' +
                ", priceNotLess=" + priceNotLess +
                ", percentNotLess=" + percentNotLess +
                ", costNotLess=" + costNotLess +
                ", carrierId=" + carrierId +
                '}';
    }
}
