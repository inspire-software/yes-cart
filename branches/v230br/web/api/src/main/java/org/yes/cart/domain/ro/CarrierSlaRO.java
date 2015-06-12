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

package org.yes.cart.domain.ro;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.ro.xml.impl.I18nMapAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 28/03/2015
 * Time: 16:20
 */
@Dto
@XmlRootElement(name = "sla")
public class CarrierSlaRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    @DtoField(value = "carrierslaId", readOnly = true)
    private long carrierslaId;

    @DtoField(value = "name", readOnly = true)
    private String name;

    @DtoField(value = "description", readOnly = true)
    private String description;

    @DtoField(value = "currency", readOnly = true)
    private String currency;

    @DtoField(value = "maxDays", readOnly = true)
    private Integer maxDays;

    @DtoField(value = "slaType", readOnly = true)
    private String slaType;

    @DtoField(value = "price", readOnly = true)
    private BigDecimal price;

    @DtoField(value = "percent", readOnly = true)
    private BigDecimal percent;

    @DtoField(value = "script", readOnly = true)
    private String script;

    @DtoField(value = "priceNotLess", readOnly = true)
    private BigDecimal priceNotLess;

    @DtoField(value = "percentNotLess", readOnly = true)
    private BigDecimal percentNotLess;

    @DtoField(value = "costNotLess", readOnly = true)
    private BigDecimal costNotLess;

    @DtoField(value = "carrier.carrierId", readOnly = true)
    private long carrierId;

    @DtoField(value = "displayName", converter = "i18nStringConverter", readOnly = true)
    private Map<String, String> displayNames;

    @DtoField(value = "displayDescription", converter = "i18nStringConverter", readOnly = true)
    private Map<String, String> displayDescriptions;

    @DtoField(value = "supportedPaymentGateways", readOnly = true)
    private String supportedPaymentGateways;

    @DtoField(value = "billingAddressNotRequired", readOnly = true)
    private boolean billingAddressNotRequired;

    @DtoField(value = "deliveryAddressNotRequired", readOnly = true)
    private boolean deliveryAddressNotRequired;

    @XmlAttribute(name = "carriersla-id")
    public long getCarrierslaId() {
        return carrierslaId;
    }

    public void setCarrierslaId(final long carrierslaId) {
        this.carrierslaId = carrierslaId;
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

    @XmlAttribute(name = "currency")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    @XmlAttribute(name = "max-days")
    public Integer getMaxDays() {
        return maxDays;
    }

    public void setMaxDays(final Integer maxDays) {
        this.maxDays = maxDays;
    }

    @XmlAttribute(name = "sla-type")
    public String getSlaType() {
        return slaType;
    }

    public void setSlaType(final String slaType) {
        this.slaType = slaType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(final BigDecimal percent) {
        this.percent = percent;
    }

    public String getScript() {
        return script;
    }

    public void setScript(final String script) {
        this.script = script;
    }

    public BigDecimal getPriceNotLess() {
        return priceNotLess;
    }

    public void setPriceNotLess(final BigDecimal priceNotLess) {
        this.priceNotLess = priceNotLess;
    }

    public BigDecimal getPercentNotLess() {
        return percentNotLess;
    }

    public void setPercentNotLess(final BigDecimal percentNotLess) {
        this.percentNotLess = percentNotLess;
    }

    public BigDecimal getCostNotLess() {
        return costNotLess;
    }

    public void setCostNotLess(final BigDecimal costNotLess) {
        this.costNotLess = costNotLess;
    }

    @XmlAttribute(name = "carrier-id")
    public long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(final long carrierId) {
        this.carrierId = carrierId;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-names")
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-descriptions")
    public Map<String, String> getDisplayDescriptions() {
        return displayDescriptions;
    }

    public void setDisplayDescriptions(final Map<String, String> displayDescriptions) {
        this.displayDescriptions = displayDescriptions;
    }

    @XmlElement(name = "supported-payment-gateways")
    public String getSupportedPaymentGateways() {
        return supportedPaymentGateways;
    }

    public void setSupportedPaymentGateways(final String supportedPaymentGateways) {
        this.supportedPaymentGateways = supportedPaymentGateways;
    }

    @XmlAttribute(name = "billing-address-not-required")
    public boolean isBillingAddressNotRequired() {
        return billingAddressNotRequired;
    }

    public void setBillingAddressNotRequired(final boolean billingAddressNotRequired) {
        this.billingAddressNotRequired = billingAddressNotRequired;
    }

    @XmlAttribute(name = "delivery-address-not-required")
    public boolean isDeliveryAddressNotRequired() {
        return deliveryAddressNotRequired;
    }

    public void setDeliveryAddressNotRequired(final boolean deliveryAddressNotRequired) {
        this.deliveryAddressNotRequired = deliveryAddressNotRequired;
    }
}
