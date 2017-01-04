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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

@Dto
@XmlRootElement(name = "cart-item")
public class CartItemRO implements Serializable {

    private static final long serialVersionUID = 20100116L;

    private static final BigDecimal DEFAULT_QUANTITY = BigDecimal.ONE; // do not simplify this, because of min quantity pair, triple , etc.

    @DtoField(readOnly = true)
    private String productSkuCode;

    @DtoField(value = "qty", readOnly = true)
    private BigDecimal quantity = DEFAULT_QUANTITY;
    @DtoField(readOnly = true)
    private String supplierCode;
    @DtoField(readOnly = true)
    private String deliveryGroup;

    @DtoField(readOnly = true)
    private BigDecimal price = BigDecimal.ZERO;
    @DtoField(readOnly = true)
    private BigDecimal salePrice = BigDecimal.ZERO;
    @DtoField(readOnly = true)
    private BigDecimal listPrice = BigDecimal.ZERO;

    @DtoField(readOnly = true)
    private BigDecimal netPrice = BigDecimal.ZERO;
    @DtoField(readOnly = true)
    private BigDecimal grossPrice = BigDecimal.ZERO;
    @DtoField(readOnly = true)
    private BigDecimal taxRate = BigDecimal.ZERO;
    @DtoField(readOnly = true)
    private String taxCode;
    @DtoField(readOnly = true)
    private boolean taxExclusiveOfPrice;

    @DtoField(readOnly = true)
    private boolean gift;
    @DtoField(readOnly = true)
    private boolean promoApplied;
    @DtoField(readOnly = true)
    private boolean fixedPrice;
    @DtoField(readOnly = true)
    private String appliedPromo;

    private SkuPriceRO unitPricing;
    private SkuPriceRO totalPricing;

    @XmlAttribute(name = "product-sku-code")
    public String getProductSkuCode() {
        return productSkuCode;
    }

    public void setProductSkuCode(final String productSkuCode) {
        this.productSkuCode = productSkuCode;
    }

    @XmlAttribute(name = "quantity")
    public BigDecimal getQuantity() {
        return new BigDecimal(quantity.toString());
    }

    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    @XmlAttribute(name = "supplier-code")
    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(final String supplierCode) {
        this.supplierCode = supplierCode;
    }

    @XmlAttribute(name = "delivery-group")
    public String getDeliveryGroup() {
        return deliveryGroup;
    }

    public void setDeliveryGroup(final String deliveryGroup) {
        this.deliveryGroup = deliveryGroup;
    }


    @XmlAttribute(name = "price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    @XmlAttribute(name = "net-price")
    public BigDecimal getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(final BigDecimal netPrice) {
        this.netPrice = netPrice;
    }

    @XmlAttribute(name = "gross-price")
    public BigDecimal getGrossPrice() {
        return grossPrice;
    }

    public void setGrossPrice(final BigDecimal grossPrice) {
        this.grossPrice = grossPrice;
    }

    @XmlAttribute(name = "tax-code")
    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(final String taxCode) {
        this.taxCode = taxCode;
    }

    @XmlAttribute(name = "tax-rate")
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(final BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    @XmlAttribute(name = "tax-exclusive-of-price")
    public boolean isTaxExclusiveOfPrice() {
        return taxExclusiveOfPrice;
    }

    public void setTaxExclusiveOfPrice(final boolean taxExclusiveOfPrice) {
        this.taxExclusiveOfPrice = taxExclusiveOfPrice;
    }

    @XmlAttribute(name = "list-price")
    public BigDecimal getListPrice() {
        return listPrice;
    }

    public void setListPrice(final BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    @XmlAttribute(name = "sale-price")
    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(final BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    @XmlAttribute(name = "gift")
    public boolean isGift() {
        return gift;
    }

    public void setGift(final boolean gift) {
        this.gift = gift;
    }

    @XmlAttribute(name = "fixed-price")
    public boolean isFixedPrice() {
        return fixedPrice;
    }

    public void setFixedPrice(final boolean fixedPrice) {
        this.fixedPrice = fixedPrice;
    }

    @XmlAttribute(name = "promo-applied")
    public boolean isPromoApplied() {
        return promoApplied;
    }

    public void setPromoApplied(final boolean promoApplied) {
        this.promoApplied = promoApplied;
    }

    @XmlElement(name = "applied-promo")
    public String getAppliedPromo() {
        return appliedPromo;
    }

    public void setAppliedPromo(final String appliedPromo) {
        this.appliedPromo = appliedPromo;
    }

    @XmlElement(name = "unit-pricing")
    public SkuPriceRO getUnitPricing() {
        return unitPricing;
    }

    public void setUnitPricing(final SkuPriceRO unitPricing) {
        this.unitPricing = unitPricing;
    }

    @XmlElement(name = "total-pricing")
    public SkuPriceRO getTotalPricing() {
        return totalPricing;
    }

    public void setTotalPricing(final SkuPriceRO totalPricing) {
        this.totalPricing = totalPricing;
    }

    @Override
    public String toString() {
        return "CartItemRO{" +
                "productSkuCode='" + productSkuCode + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
