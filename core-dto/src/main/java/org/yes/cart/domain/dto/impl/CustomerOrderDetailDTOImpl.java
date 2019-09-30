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
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDetailDTO;
import org.yes.cart.domain.dto.CustomerOrderDetailDTO;
import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Customer order detail DTO interface implementation.
 */
@Dto
public class CustomerOrderDetailDTOImpl implements CustomerOrderDeliveryDetailDTO, CustomerOrderDetailDTO {

    private static final long serialVersionUID = 20120812L;

    @DtoField(value = "customerOrderDetId", readOnly = true)
    private long customerOrderDeliveryDetId;

    @DtoField(value = "productSkuCode", readOnly = true)
    private String skuCode;

    @DtoField(value = "productName", readOnly = true)
    private String skuName;


    @DtoField(value = "qty", readOnly = true)
    private BigDecimal qty;
    @DtoField(readOnly = true)
    private String supplierCode;
    @DtoField(readOnly = true)
    private String deliveryGroup;

    @DtoField(readOnly = true)
    private String deliveryRemarks;
    @DtoField(readOnly = true)
    private LocalDateTime deliveryEstimatedMin;
    @DtoField(readOnly = true)
    private LocalDateTime deliveryEstimatedMax;
    @DtoField(readOnly = true)
    private LocalDateTime deliveryGuaranteed;

    @DtoField(readOnly = true)
    private String b2bRemarks;

    @DtoField(value = "price", readOnly = true)
    private BigDecimal price;

    @DtoField(value = "listPrice", readOnly = true)
    private BigDecimal listPrice;
    @DtoField(value = "salePrice", readOnly = true)
    private BigDecimal salePrice;
    @DtoField(value = "gift", readOnly = true)
    private boolean gift;
    @DtoField(value = "promoApplied", readOnly = true)
    private boolean promoApplied;
    @DtoField(value = "appliedPromo", readOnly = true)
    private String appliedPromo;

    @DtoField(value = "netPrice", readOnly = true)
    private BigDecimal netPrice;
    @DtoField(value = "grossPrice", readOnly = true)
    private BigDecimal grossPrice;
    @DtoField(value = "taxRate", readOnly = true)
    private BigDecimal taxRate;
    @DtoField(value = "taxCode", readOnly = true)
    private String taxCode;
    @DtoField(value = "taxExclusiveOfPrice", readOnly = true)
    private boolean taxExclusiveOfPrice;

    private BigDecimal lineTotal;
    private BigDecimal lineTotalGross;
    private BigDecimal lineTotalNet;

    private BigDecimal lineTax;


    private String deliveryNum;

    private String deliveryStatusLabel;

    @DtoField(value = "allValues", readOnly = true, converter = "customValuesMapConverter")
    private Map<String, Pair<String, Map<String, String>>> allValues;

    @Override
    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    @Override
    public void setLineTotal(final BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    @Override
    public BigDecimal getLineTax() {
        return lineTax;
    }

    @Override
    public void setLineTax(final BigDecimal lineTax) {
        this.lineTax = lineTax;
    }
    @Override
    public BigDecimal getLineTotalGross() {
        return lineTotalGross;
    }

    @Override
    public void setLineTotalGross(final BigDecimal lineTotalGross) {
        this.lineTotalGross = lineTotalGross;
    }

    @Override
    public BigDecimal getLineTotalNet() {
        return lineTotalNet;
    }

    @Override
    public void setLineTotalNet(final BigDecimal lineTotalNet) {
        this.lineTotalNet = lineTotalNet;
    }

    private void calculateLineTotal() {

        if (qty != null && netPrice != null && grossPrice != null) {
            lineTotal = qty.multiply(grossPrice).setScale(Constants.MONEY_SCALE, RoundingMode.HALF_UP);
            lineTotalGross = qty.multiply(grossPrice).setScale(Constants.MONEY_SCALE, RoundingMode.HALF_UP);
            lineTotalNet = qty.multiply(netPrice).setScale(Constants.MONEY_SCALE, RoundingMode.HALF_UP);
            lineTax = lineTotalGross.subtract(lineTotalNet);
        }

    }

    @Override
    public long getId() {
        return customerOrderDeliveryDetId;
    }

    @Override
    public long getCustomerOrderDeliveryDetId() {
        return customerOrderDeliveryDetId;
    }

    @Override
    public void setCustomerOrderDeliveryDetId(final long customerOrderDeliveryDetId) {
        this.customerOrderDeliveryDetId = customerOrderDeliveryDetId;
    }

    @Override
    public long getCustomerOrderDetId() {
        return customerOrderDeliveryDetId;
    }

    @Override
    public void setCustomerOrderDetId(final long customerOrderDetId) {
        this.customerOrderDeliveryDetId = customerOrderDetId;
    }

    @Override
    public String getSkuCode() {
        return skuCode;
    }

    @Override
    public void setSkuCode(final String skuCode) {
        this.skuCode = skuCode;
    }

    @Override
    public String getSkuName() {
        return skuName;
    }

    @Override
    public void setSkuName(final String skuName) {
        this.skuName = skuName;
    }


    @Override
    public String getSupplierCode() {
        return supplierCode;
    }

    @Override
    public void setSupplierCode(final String supplierCode) {
        this.supplierCode = supplierCode;
    }

    @Override
    public String getDeliveryGroup() {
        return deliveryGroup;
    }

    @Override
    public void setDeliveryGroup(final String deliveryGroup) {
        this.deliveryGroup = deliveryGroup;
    }

    @Override
    public String getDeliveryRemarks() {
        return deliveryRemarks;
    }

    @Override
    public void setDeliveryRemarks(final String deliveryRemarks) {
        this.deliveryRemarks = deliveryRemarks;
    }

    @Override
    public LocalDateTime getDeliveryEstimatedMin() {
        return deliveryEstimatedMin;
    }

    @Override
    public void setDeliveryEstimatedMin(final LocalDateTime deliveryEstimatedMin) {
        this.deliveryEstimatedMin = deliveryEstimatedMin;
    }

    @Override
    public LocalDateTime getDeliveryEstimatedMax() {
        return deliveryEstimatedMax;
    }

    @Override
    public void setDeliveryEstimatedMax(final LocalDateTime deliveryEstimatedMax) {
        this.deliveryEstimatedMax = deliveryEstimatedMax;
    }

    @Override
    public LocalDateTime getDeliveryGuaranteed() {
        return deliveryGuaranteed;
    }

    @Override
    public void setDeliveryGuaranteed(final LocalDateTime deliveryGuaranteed) {
        this.deliveryGuaranteed = deliveryGuaranteed;
    }

    @Override
    public LocalDateTime getDeliveryConfirmed() {
        return null;
    }

    @Override
    public void setDeliveryConfirmed(final LocalDateTime delivered) {

    }

    @Override
    public BigDecimal getDeliveredQuantity() {
        return null;
    }

    @Override
    public void setDeliveredQuantity(final BigDecimal deliveredQuantity) {

    }

    @Override
    public boolean isDeliveryRejected() {
        return false;
    }

    @Override
    public void setDeliveryRejected(final boolean deliveryRejected) {

    }

    @Override
    public boolean isDeliveryDifferent() {
        return false;
    }

    @Override
    public void setDeliveryDifferent(final boolean deliveryDifferent) {

    }

    @Override
    public String getB2bRemarks() {
        return b2bRemarks;
    }

    @Override
    public void setB2bRemarks(final String b2bRemarks) {
        this.b2bRemarks = b2bRemarks;
    }

    @Override
    public String getSupplierInvoiceNo() {
        return null;
    }

    @Override
    public void setSupplierInvoiceNo(final String invoiceNo) {

    }

    @Override
    public LocalDate getSupplierInvoiceDate() {
        return null;
    }

    @Override
    public void setSupplierInvoiceDate(final LocalDate supplierInvoiceDate) {

    }

    @Override
    public BigDecimal getQty() {
        return qty;
    }

    @Override
    public void setQty(final BigDecimal qty) {
        this.qty = qty;
        calculateLineTotal();
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    @Override
    public BigDecimal getNetPrice() {
        return netPrice;
    }

    @Override
    public void setNetPrice(final BigDecimal netPrice) {
        this.netPrice = netPrice;
        calculateLineTotal();
    }

    @Override
    public BigDecimal getGrossPrice() {
        return grossPrice;
    }

    @Override
    public void setGrossPrice(final BigDecimal grossPrice) {
        this.grossPrice = grossPrice;
        calculateLineTotal();
    }

    @Override
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    @Override
    public void setTaxRate(final BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    @Override
    public String getTaxCode() {
        return taxCode;
    }

    @Override
    public void setTaxCode(final String taxCode) {
        this.taxCode = taxCode;
    }

    @Override
    public boolean isTaxExclusiveOfPrice() {
        return taxExclusiveOfPrice;
    }

    @Override
    public void setTaxExclusiveOfPrice(final boolean taxExclusiveOfPrice) {
        this.taxExclusiveOfPrice = taxExclusiveOfPrice;
    }

    @Override
    public BigDecimal getListPrice() {
        return listPrice;
    }

    @Override
    public void setListPrice(final BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    @Override
    public BigDecimal getSalePrice() {
        return salePrice;
    }

    @Override
    public void setSalePrice(final BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    @Override
    public boolean isGift() {
        return gift;
    }

    @Override
    public void setGift(final boolean gift) {
        this.gift = gift;
    }

    @Override
    public boolean isPromoApplied() {
        return promoApplied;
    }

    @Override
    public void setPromoApplied(final boolean promoApplied) {
        this.promoApplied = promoApplied;
    }

    @Override
    public String getAppliedPromo() {
        return appliedPromo;
    }

    @Override
    public void setAppliedPromo(final String appliedPromo) {
        this.appliedPromo = appliedPromo;
    }

    @Override
    public String getDeliveryNum() {
        return deliveryNum;
    }

    @Override
    public void setDeliveryNum(final String deliveryNum) {
        this.deliveryNum = deliveryNum;
    }

    @Override
    public String getDeliveryStatusLabel() {
        return deliveryStatusLabel;
    }

    @Override
    public void setDeliveryStatusLabel(final String deliveryStatusLabel) {
        this.deliveryStatusLabel = deliveryStatusLabel;
    }

    @Override
    public Map<String, Pair<String, Map<String, String>>> getAllValues() {
        return allValues;
    }

    @Override
    public void setAllValues(final Map<String, Pair<String, Map<String, String>>> allValues) {
        this.allValues = allValues;
    }

    public String toString() {
        return "CustomerOrderDeliveryDetailDTOImpl{" +
                "customerorderdeliveryId=" + customerOrderDeliveryDetId +
                ", skuCode='" + skuCode + '\'' +
                ", skuName='" + skuName + '\'' +
                ", qty=" + qty +
                ", price=" + price +
                ", netPrice=" + netPrice +
                ", grossPrice=" + grossPrice +
                ", listPrice=" + listPrice +
                ", deliveryNum='" + deliveryNum + '\'' +
                ", deliveryStatusLabel='" + deliveryStatusLabel + '\'' +
                '}';
    }
}
