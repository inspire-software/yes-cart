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
import org.yes.cart.util.MoneyUtils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 23/03/2015
 * Time: 14:31
 */
@Dto
@XmlRootElement(name = "price")
public class SkuPriceRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    @DtoField(readOnly = true)
    private String currency;
    private String symbol;
    private String symbolPosition;
    @DtoField(readOnly = true)
    private BigDecimal quantity;
    @DtoField(readOnly = true)
    private BigDecimal regularPrice;
    @DtoField(readOnly = true)
    private BigDecimal salePrice;

    @DtoField(readOnly = true)
    private boolean taxInfoEnabled;
    @DtoField(readOnly = true)
    private boolean taxInfoUseNet;
    @DtoField(readOnly = true)
    private boolean taxInfoShowAmount;

    @DtoField(readOnly = true)
    private String priceTaxCode;
    @DtoField(readOnly = true)
    private BigDecimal priceTaxRate;
    @DtoField(readOnly = true)
    private boolean priceTaxExclusive;
    @DtoField(readOnly = true)
    private BigDecimal priceTax;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(final String symbol) {
        this.symbol = symbol;
    }

    @XmlElement(name = "symbol-position")
    public String getSymbolPosition() {
        return symbolPosition;
    }

    public void setSymbolPosition(final String symbolPosition) {
        this.symbolPosition = symbolPosition;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    @XmlElement(name = "regular-price")
    public BigDecimal getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(final BigDecimal regularPrice) {
        this.regularPrice = regularPrice;
    }

    @XmlElement(name = "sale-price")
    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(final BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getDiscount() {
        if (salePrice != null && MoneyUtils.isFirstBiggerThanSecond(regularPrice, salePrice)) {
            return MoneyUtils.getDiscountDisplayValue(regularPrice, salePrice);
        }
        return null;
    }

    @XmlAttribute(name = "tax-info-enabled")
    public boolean isTaxInfoEnabled() {
        return taxInfoEnabled;
    }

    public void setTaxInfoEnabled(final boolean taxInfoEnabled) {
        this.taxInfoEnabled = taxInfoEnabled;
    }

    @XmlAttribute(name = "tax-info-use-net")
    public boolean isTaxInfoUseNet() {
        return taxInfoUseNet;
    }

    public void setTaxInfoUseNet(final boolean taxInfoUseNet) {
        this.taxInfoUseNet = taxInfoUseNet;
    }

    @XmlAttribute(name = "tax-info-show-amount")
    public boolean isTaxInfoShowAmount() {
        return taxInfoShowAmount;
    }

    public void setTaxInfoShowAmount(final boolean taxInfoShowAmount) {
        this.taxInfoShowAmount = taxInfoShowAmount;
    }

    @XmlElement(name = "price-tax-code")
    public String getPriceTaxCode() {
        return priceTaxCode;
    }

    public void setPriceTaxCode(final String priceTaxCode) {
        this.priceTaxCode = priceTaxCode;
    }

    @XmlElement(name = "price-tax-rate")
    public BigDecimal getPriceTaxRate() {
        return priceTaxRate;
    }

    public void setPriceTaxRate(final BigDecimal priceTaxRate) {
        this.priceTaxRate = priceTaxRate;
    }

    @XmlElement(name = "price-tax-exclusive")
    public boolean isPriceTaxExclusive() {
        return priceTaxExclusive;
    }

    public void setPriceTaxExclusive(final boolean priceTaxExclusive) {
        this.priceTaxExclusive = priceTaxExclusive;
    }

    @XmlElement(name = "price-tax")
    public BigDecimal getPriceTax() {
        return priceTax;
    }

    public void setPriceTax(final BigDecimal priceTax) {
        this.priceTax = priceTax;
    }
}
