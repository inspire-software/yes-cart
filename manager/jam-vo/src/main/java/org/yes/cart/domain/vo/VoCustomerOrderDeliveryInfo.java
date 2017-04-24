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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * User: denispavlov
 * Date: 31/08/2016
 * Time: 19:15
 */
@Dto
public class VoCustomerOrderDeliveryInfo {

    @DtoField(value = "customerOrderDeliveryId", readOnly = true)
    private long customerOrderDeliveryId;

    @DtoField(value = "deliveryNum", readOnly = true)
    private String deliveryNum;

    @DtoField(value = "refNo", readOnly = true)
    private String refNo;

    @DtoField(value = "deliveryStatus", readOnly = true)
    private String deliveryStatus;
    private List<String> deliveryStatusNextOptions = Collections.emptyList();

    @DtoField(value = "carrierSlaName", readOnly = true)
    private String carrierSlaName;

    @DtoField(value = "carrierName", readOnly = true)
    private String carrierName;

    @DtoField(value = "ordernum", readOnly = true)
    private String ordernum;

    @DtoField(value = "shippingAddress", readOnly = true)
    private String shippingAddress;

    @DtoField(value = "billingAddress", readOnly = true)
    private String billingAddress;

    @DtoField(value = "currency", readOnly = true)
    private String currency;

    @DtoField(value = "shopName", readOnly = true)
    private String shopName;

    @DtoField(value = "pgLabel", readOnly = true)
    private String pgLabel;

    @DtoField(value = "supportCaptureMore", readOnly = true)
    private boolean supportCaptureMore;

    @DtoField(value = "supportCaptureLess", readOnly = true)
    private boolean supportCaptureLess;

    @DtoField(value = "deliveryGroup", readOnly = true)
    private String deliveryGroup;

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

    @DtoField(value = "requestedDeliveryDate", readOnly = true)
    private Date requestedDeliveryDate;

    @DtoField(value = "price", readOnly = true)
    private BigDecimal price;
    @DtoField(value = "listPrice", readOnly = true)
    private BigDecimal listPrice;
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

    @DtoField(value = "eligibleForExport", readOnly = true)
    private String eligibleForExport;
    @DtoField(value = "blockExport", readOnly = true)
    private boolean blockExport;
    @DtoField(value = "lastExportDate", readOnly = true)
    private Date lastExportDate;
    @DtoField(value = "lastExportStatus", readOnly = true)
    private String lastExportStatus;
    @DtoField(value = "lastExportDeliveryStatus", readOnly = true)
    private String lastExportDeliveryStatus;


    public long getCustomerOrderDeliveryId() {
        return customerOrderDeliveryId;
    }

    public void setCustomerOrderDeliveryId(final long customerOrderDeliveryId) {
        this.customerOrderDeliveryId = customerOrderDeliveryId;
    }

    public String getDeliveryNum() {
        return deliveryNum;
    }

    public void setDeliveryNum(final String deliveryNum) {
        this.deliveryNum = deliveryNum;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(final String refNo) {
        this.refNo = refNo;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(final String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public List<String> getDeliveryStatusNextOptions() {
        return deliveryStatusNextOptions;
    }

    public void setDeliveryStatusNextOptions(final List<String> deliveryStatusNextOptions) {
        this.deliveryStatusNextOptions = deliveryStatusNextOptions;
    }

    public String getCarrierSlaName() {
        return carrierSlaName;
    }

    public void setCarrierSlaName(final String carrierSlaName) {
        this.carrierSlaName = carrierSlaName;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(final String carrierName) {
        this.carrierName = carrierName;
    }

    public String getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(final String ordernum) {
        this.ordernum = ordernum;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(final String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(final String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(final String shopName) {
        this.shopName = shopName;
    }

    public String getPgLabel() {
        return pgLabel;
    }

    public void setPgLabel(final String pgLabel) {
        this.pgLabel = pgLabel;
    }

    public boolean isSupportCaptureMore() {
        return supportCaptureMore;
    }

    public void setSupportCaptureMore(final boolean supportCaptureMore) {
        this.supportCaptureMore = supportCaptureMore;
    }

    public boolean isSupportCaptureLess() {
        return supportCaptureLess;
    }

    public void setSupportCaptureLess(final boolean supportCaptureLess) {
        this.supportCaptureLess = supportCaptureLess;
    }

    public String getDeliveryGroup() {
        return deliveryGroup;
    }

    public void setDeliveryGroup(final String deliveryGroup) {
        this.deliveryGroup = deliveryGroup;
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

    public Date getRequestedDeliveryDate() {
        return requestedDeliveryDate;
    }

    public void setRequestedDeliveryDate(final Date requestedDeliveryDate) {
        this.requestedDeliveryDate = requestedDeliveryDate;
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

    public String getEligibleForExport() {
        return eligibleForExport;
    }

    public void setEligibleForExport(final String eligibleForExport) {
        this.eligibleForExport = eligibleForExport;
    }

    public boolean isBlockExport() {
        return blockExport;
    }

    public void setBlockExport(final boolean blockExport) {
        this.blockExport = blockExport;
    }

    public Date getLastExportDate() {
        return lastExportDate;
    }

    public void setLastExportDate(final Date lastExportDate) {
        this.lastExportDate = lastExportDate;
    }

    public String getLastExportStatus() {
        return lastExportStatus;
    }

    public void setLastExportStatus(final String lastExportStatus) {
        this.lastExportStatus = lastExportStatus;
    }

    public String getLastExportDeliveryStatus() {
        return lastExportDeliveryStatus;
    }

    public void setLastExportDeliveryStatus(final String lastExportDeliveryStatus) {
        this.lastExportDeliveryStatus = lastExportDeliveryStatus;
    }
}
