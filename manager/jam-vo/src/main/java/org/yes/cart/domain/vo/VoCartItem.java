/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 10/04/2018
 * Time: 19:55
 */
@Dto
public class VoCartItem {

    private static final BigDecimal DEFAULT_QUANTITY = BigDecimal.ONE; // do not simplify this, because of min quantity pair, triple , etc.

    @DtoField(readOnly = true)
    private String productSkuCode;

    @DtoField(readOnly = true)
    private String productName;

    @DtoField(value = "qty", readOnly = true)
    private BigDecimal quantity = DEFAULT_QUANTITY;
    @DtoField(readOnly = true)
    private String supplierCode;
    @DtoField(readOnly = true)
    private String deliveryGroup;
    @DtoField(readOnly = true)
    private String itemGroup;

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
    private boolean notSoldSeparately;
    @DtoField(readOnly = true)
    private boolean configurable;

    @DtoField(readOnly = true)
    private boolean gift;
    @DtoField(readOnly = true)
    private boolean promoApplied;
    @DtoField(readOnly = true)
    private boolean fixedPrice;
    @DtoField(readOnly = true)
    private String appliedPromo;

    public String getProductSkuCode() {
        return productSkuCode;
    }

    public void setProductSkuCode(final String productSkuCode) {
        this.productSkuCode = productSkuCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(final String productName) {
        this.productName = productName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(final String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getDeliveryGroup() {
        return deliveryGroup;
    }

    public void setDeliveryGroup(final String deliveryGroup) {
        this.deliveryGroup = deliveryGroup;
    }

    public String getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(final String itemGroup) {
        this.itemGroup = itemGroup;
    }

    public boolean isNotSoldSeparately() {
        return notSoldSeparately;
    }

    public void setNotSoldSeparately(final boolean notSoldSeparately) {
        this.notSoldSeparately = notSoldSeparately;
    }

    public boolean isConfigurable() {
        return configurable;
    }

    public void setConfigurable(final boolean configurable) {
        this.configurable = configurable;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(final BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getListPrice() {
        return listPrice;
    }

    public void setListPrice(final BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    public BigDecimal getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(final BigDecimal netPrice) {
        this.netPrice = netPrice;
    }

    public BigDecimal getGrossPrice() {
        return grossPrice;
    }

    public void setGrossPrice(final BigDecimal grossPrice) {
        this.grossPrice = grossPrice;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(final BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(final String taxCode) {
        this.taxCode = taxCode;
    }

    public boolean isTaxExclusiveOfPrice() {
        return taxExclusiveOfPrice;
    }

    public void setTaxExclusiveOfPrice(final boolean taxExclusiveOfPrice) {
        this.taxExclusiveOfPrice = taxExclusiveOfPrice;
    }

    public boolean isGift() {
        return gift;
    }

    public void setGift(final boolean gift) {
        this.gift = gift;
    }

    public boolean isPromoApplied() {
        return promoApplied;
    }

    public void setPromoApplied(final boolean promoApplied) {
        this.promoApplied = promoApplied;
    }

    public boolean isFixedPrice() {
        return fixedPrice;
    }

    public void setFixedPrice(final boolean fixedPrice) {
        this.fixedPrice = fixedPrice;
    }

    public String getAppliedPromo() {
        return appliedPromo;
    }

    public void setAppliedPromo(final String appliedPromo) {
        this.appliedPromo = appliedPromo;
    }

}
