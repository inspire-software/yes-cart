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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * User: denispavlov
 * Date: 31/08/2016
 * Time: 19:15
 */
@Dto
public class VoCustomerOrderLine {

    @DtoField(value = "customerOrderDeliveryDetId", readOnly = true)
    private long customerOrderDeliveryDetId;

    @DtoField(value = "skuCode", readOnly = true)
    private String skuCode;

    @DtoField(value = "skuName", readOnly = true)
    private String skuName;


    @DtoField(value = "qty", readOnly = true)
    private BigDecimal qty;
    @DtoField(value = "supplierCode", readOnly = true)
    private String supplierCode;

    @DtoField(value = "deliveryRemarks", readOnly = true)
    private String deliveryRemarks;
    @DtoField(value = "deliveryEstimatedMin", readOnly = true)
    private Date deliveryEstimatedMin;
    @DtoField(value = "deliveryEstimatedMax", readOnly = true)
    private Date deliveryEstimatedMax;
    @DtoField(value = "deliveryGuaranteed", readOnly = true)
    private Date deliveryGuaranteed;
    @DtoField(value = "deliveryConfirmed", readOnly = true)
    private Date deliveryConfirmed;
    @DtoField(value = "deliveredQuantity", readOnly = true)
    private BigDecimal deliveredQuantity;
    @DtoField(value = "deliveryRejected", readOnly = true)
    private boolean deliveryRejected;
    @DtoField(value = "deliveryDifferent", readOnly = true)
    private boolean deliveryDifferent;

    @DtoField(value = "b2bRemarks", readOnly = true)
    private String b2bRemarks;

    @DtoField(value = "supplierInvoiceNo", readOnly = true)
    private String supplierInvoiceNo;
    @DtoField(value = "supplierInvoiceDate", readOnly = true)
    private Date supplierInvoiceDate;

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
    @DtoField(value = "appliedPromo", converter = "CSVToList", readOnly = true)
    private List<String> appliedPromo;

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

    @DtoField(value = "lineTotal", readOnly = true)
    private BigDecimal lineTotal;

    @DtoField(value = "lineTax", readOnly = true)
    private BigDecimal lineTax;

    @DtoField(value = "deliveryNum", readOnly = true)
    private String deliveryNum;

    @DtoField(value = "deliveryStatusLabel", readOnly = true)
    private String deliveryStatusLabel;

    @DtoField(value = "allValues", converter = "PairMapToPairList", readOnly = true)
    private List<MutablePair<String, MutablePair<String, String>>> allValues;

    public long getCustomerOrderDeliveryDetId() {
        return customerOrderDeliveryDetId;
    }

    public void setCustomerOrderDeliveryDetId(final long customerOrderDeliveryDetId) {
        this.customerOrderDeliveryDetId = customerOrderDeliveryDetId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(final String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(final String skuName) {
        this.skuName = skuName;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(final BigDecimal qty) {
        this.qty = qty;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(final String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getDeliveryRemarks() {
        return deliveryRemarks;
    }

    public void setDeliveryRemarks(final String deliveryRemarks) {
        this.deliveryRemarks = deliveryRemarks;
    }

    public Date getDeliveryEstimatedMin() {
        return deliveryEstimatedMin;
    }

    public void setDeliveryEstimatedMin(final Date deliveryEstimatedMin) {
        this.deliveryEstimatedMin = deliveryEstimatedMin;
    }

    public Date getDeliveryEstimatedMax() {
        return deliveryEstimatedMax;
    }

    public void setDeliveryEstimatedMax(final Date deliveryEstimatedMax) {
        this.deliveryEstimatedMax = deliveryEstimatedMax;
    }

    public Date getDeliveryGuaranteed() {
        return deliveryGuaranteed;
    }

    public void setDeliveryGuaranteed(final Date deliveryGuaranteed) {
        this.deliveryGuaranteed = deliveryGuaranteed;
    }

    public Date getDeliveryConfirmed() {
        return deliveryConfirmed;
    }

    public void setDeliveryConfirmed(final Date deliveryConfirmed) {
        this.deliveryConfirmed = deliveryConfirmed;
    }

    public BigDecimal getDeliveredQuantity() {
        return deliveredQuantity;
    }

    public void setDeliveredQuantity(final BigDecimal deliveredQuantity) {
        this.deliveredQuantity = deliveredQuantity;
    }

    public boolean isDeliveryRejected() {
        return deliveryRejected;
    }

    public void setDeliveryRejected(final boolean deliveryRejected) {
        this.deliveryRejected = deliveryRejected;
    }

    public boolean isDeliveryDifferent() {
        return deliveryDifferent;
    }

    public void setDeliveryDifferent(final boolean deliveryDifferent) {
        this.deliveryDifferent = deliveryDifferent;
    }

    public String getB2bRemarks() {
        return b2bRemarks;
    }

    public void setB2bRemarks(final String b2bRemarks) {
        this.b2bRemarks = b2bRemarks;
    }

    public String getSupplierInvoiceNo() {
        return supplierInvoiceNo;
    }

    public void setSupplierInvoiceNo(final String supplierInvoiceNo) {
        this.supplierInvoiceNo = supplierInvoiceNo;
    }

    public Date getSupplierInvoiceDate() {
        return supplierInvoiceDate;
    }

    public void setSupplierInvoiceDate(final Date supplierInvoiceDate) {
        this.supplierInvoiceDate = supplierInvoiceDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getListPrice() {
        return listPrice;
    }

    public void setListPrice(final BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(final BigDecimal salePrice) {
        this.salePrice = salePrice;
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

    public List<String> getAppliedPromo() {
        return appliedPromo;
    }

    public void setAppliedPromo(final List<String> appliedPromo) {
        this.appliedPromo = appliedPromo;
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

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(final BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public BigDecimal getLineTax() {
        return lineTax;
    }

    public void setLineTax(final BigDecimal lineTax) {
        this.lineTax = lineTax;
    }

    public String getDeliveryNum() {
        return deliveryNum;
    }

    public void setDeliveryNum(final String deliveryNum) {
        this.deliveryNum = deliveryNum;
    }

    public String getDeliveryStatusLabel() {
        return deliveryStatusLabel;
    }

    public void setDeliveryStatusLabel(final String deliveryStatusLabel) {
        this.deliveryStatusLabel = deliveryStatusLabel;
    }

    public List<MutablePair<String, MutablePair<String, String>>> getAllValues() {
        return allValues;
    }

    public void setAllValues(final List<MutablePair<String, MutablePair<String, String>>> allValues) {
        this.allValues = allValues;
    }
}
