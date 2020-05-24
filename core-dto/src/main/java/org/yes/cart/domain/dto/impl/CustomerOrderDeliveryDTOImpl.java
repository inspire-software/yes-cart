/*
 * Copyright 2009 Inspire-Software.com
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
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDTO;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDetailDTO;
import org.yes.cart.domain.dto.matcher.impl.IdentifiableMatcher;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * Dto to represent delivery from order.
 * @see org.yes.cart.domain.entity.CustomerOrderDelivery for more details.
 *
 * User: iazarny@yahoo.com
 * Date: 8/17/12
 * Time: 7:20 AM
 */
@Dto
public class CustomerOrderDeliveryDTOImpl implements CustomerOrderDeliveryDTO {

    private static final long serialVersionUID = 20120817L;

    @DtoField(value = "customerOrderDeliveryId", readOnly = true)
    private long customerOrderDeliveryId;

    @DtoField(value = "deliveryNum", readOnly = true)
    private String deliveryNum;

    @DtoField(value = "refNo", readOnly = true)
    private String refNo;

    @DtoField(value = "deliveryStatus", readOnly = true)
    private String deliveryStatus;

    @DtoField(value = "carrierSla.name", readOnly = true)
    private String carrierSlaName;

    @DtoField(value = "carrierSla.carrier.name", readOnly = true)
    private String carrierName;

    @DtoField(value = "customerOrder.ordernum", readOnly = true)
    private String ordernum;

    @DtoField(value = "customerOrder.shippingAddress", readOnly = true)
    private String shippingAddress;

    @DtoField(value = "customerOrder.billingAddress", readOnly = true)
    private String billingAddress;

    @DtoField(value = "customerOrder.currency", readOnly = true)
    private String currency;

    @DtoField(value = "customerOrder.shop.name", readOnly = true)
    private String shopName;

    @DtoField(value = "customerOrder.pgLabel", readOnly = true)
    private String pgLabel;

    private boolean supportCaptureMore;

    private boolean supportCaptureLess;

    @DtoField(value = "deliveryGroup", readOnly = true)
    private String deliveryGroup;

    @DtoField(value = "deliveryRemarks", readOnly = true)
    private String deliveryRemarks;
    @DtoField(value = "deliveryEstimatedMin", readOnly = true)
    private LocalDateTime deliveryEstimatedMin;
    @DtoField(value = "deliveryEstimatedMax", readOnly = true)
    private LocalDateTime deliveryEstimatedMax;
    @DtoField(value = "deliveryGuaranteed", readOnly = true)
    private LocalDateTime deliveryGuaranteed;
    @DtoField(value = "deliveryConfirmed", readOnly = true)
    private LocalDateTime deliveryConfirmed;


    @DtoField(value = "price", readOnly = true)
    private BigDecimal price;
    @DtoField(value = "listPrice", readOnly = true)
    private BigDecimal listPrice;
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

    @DtoCollection(
            value = "detail",
            dtoBeanKey = "org.yes.cart.domain.dto.CustomerOrderDeliveryDetailDTO",
            entityGenericType =  CustomerOrderDeliveryDet.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = IdentifiableMatcher.class,
            readOnly = true
    )
    private Collection<CustomerOrderDeliveryDetailDTO> detail;

    @DtoField(value = "eligibleForExport", readOnly = true)
    private String eligibleForExport;
    @DtoField(value = "blockExport", readOnly = true)
    private boolean blockExport;
    @DtoField(value = "lastExportDate", readOnly = true)
    private Instant lastExportDate;
    @DtoField(value = "lastExportStatus", readOnly = true)
    private String lastExportStatus;
    @DtoField(value = "lastExportDeliveryStatus", readOnly = true)
    private String lastExportDeliveryStatus;

    @DtoField(value = "requestedDeliveryDate", readOnly = true)
    private LocalDateTime requestedDeliveryDate;

    /** {@inheritDoc} */
    @Override
    public String getShippingAddress() {
        return shippingAddress;
    }

    /** {@inheritDoc} */
    @Override
    public void setShippingAddress(final String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    /** {@inheritDoc} */
    @Override
    public String getBillingAddress() {
        return billingAddress;
    }

    /** {@inheritDoc} */
    @Override
    public void setBillingAddress(final String billingAddress) {
        this.billingAddress = billingAddress;
    }

    /** {@inheritDoc} */
    @Override
    public String getCurrency() {
        return currency;
    }

    /** {@inheritDoc} */
    @Override
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    /** {@inheritDoc} */
    @Override
    public String getShopName() {
        return shopName;
    }

    /** {@inheritDoc} */
    @Override
    public void setShopName(final String shopName) {
        this.shopName = shopName;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<CustomerOrderDeliveryDetailDTO> getDetail() {
        return detail;
    }

    /** {@inheritDoc} */
    @Override
    public void setDetail(final Collection<CustomerOrderDeliveryDetailDTO> detail) {
        this.detail = detail;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getPrice() {
        return price;
    }

    /** {@inheritDoc} */
    @Override
    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getListPrice() {
        return listPrice;
    }

    /** {@inheritDoc} */
    @Override
    public void setListPrice(final BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getNetPrice() {
        return netPrice;
    }

    /** {@inheritDoc} */
    @Override
    public void setNetPrice(final BigDecimal netPrice) {
        this.netPrice = netPrice;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getGrossPrice() {
        return grossPrice;
    }

    /** {@inheritDoc} */
    @Override
    public void setGrossPrice(final BigDecimal grossPrice) {
        this.grossPrice = grossPrice;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    /** {@inheritDoc} */
    @Override
    public void setTaxRate(final BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    /** {@inheritDoc} */
    @Override
    public String getTaxCode() {
        return taxCode;
    }

    /** {@inheritDoc} */
    @Override
    public void setTaxCode(final String taxCode) {
        this.taxCode = taxCode;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTaxExclusiveOfPrice() {
        return taxExclusiveOfPrice;
    }

    /** {@inheritDoc} */
    @Override
    public void setTaxExclusiveOfPrice(final boolean taxExclusiveOfPrice) {
        this.taxExclusiveOfPrice = taxExclusiveOfPrice;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPromoApplied() {
        return promoApplied;
    }

    /** {@inheritDoc} */
    @Override
    public void setPromoApplied(final boolean promoApplied) {
        this.promoApplied = promoApplied;
    }

    /** {@inheritDoc} */
    @Override
    public String getAppliedPromo() {
        return appliedPromo;
    }

    /** {@inheritDoc} */
    @Override
    public void setAppliedPromo(final String appliedPromo) {
        this.appliedPromo = appliedPromo;
    }

    /** {@inheritDoc} */
    @Override
    public long getCustomerOrderDeliveryId() {
        return customerOrderDeliveryId;
    }

    /** {@inheritDoc} */
    @Override
    public void setCustomerOrderDeliveryId(final long customerOrderDeliveryId) {
        this.customerOrderDeliveryId = customerOrderDeliveryId;
    }

    /** {@inheritDoc} */
    @Override
    public String getDeliveryNum() {
        return deliveryNum;
    }

    /** {@inheritDoc} */
    @Override
    public void setDeliveryNum(final String deliveryNum) {
        this.deliveryNum = deliveryNum;
    }

    /** {@inheritDoc} */
    @Override
    public String getRefNo() {
        return refNo;
    }

    /** {@inheritDoc} */
    @Override
    public void setRefNo(final String refNo) {
        this.refNo = refNo;
    }

    /** {@inheritDoc} */
    @Override
    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    /** {@inheritDoc} */
    @Override
    public void setDeliveryStatus(final String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    /** {@inheritDoc} */
    @Override
    public String getEligibleForExport() {
        return eligibleForExport;
    }

    /** {@inheritDoc} */
    @Override
    public void setEligibleForExport(final String eligibleForExport) {
        this.eligibleForExport = eligibleForExport;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isBlockExport() {
        return blockExport;
    }

    /** {@inheritDoc} */
    @Override
    public void setBlockExport(final boolean blockExport) {
        this.blockExport = blockExport;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getLastExportDate() {
        return lastExportDate;
    }

    /** {@inheritDoc} */
    @Override
    public void setLastExportDate(final Instant lastExportDate) {
        this.lastExportDate = lastExportDate;
    }

    /** {@inheritDoc} */
    @Override
    public String getLastExportStatus() {
        return lastExportStatus;
    }

    /** {@inheritDoc} */
    @Override
    public void setLastExportStatus(final String lastExportStatus) {
        this.lastExportStatus = lastExportStatus;
    }

    /** {@inheritDoc} */
    @Override
    public String getLastExportDeliveryStatus() {
        return lastExportDeliveryStatus;
    }

    /** {@inheritDoc} */
    @Override
    public void setLastExportDeliveryStatus(final String lastExportDeliveryStatus) {
        this.lastExportDeliveryStatus = lastExportDeliveryStatus;
    }

    /** {@inheritDoc} */
    @Override
    public String getCarrierSlaName() {
        return carrierSlaName;
    }

    /** {@inheritDoc} */
    @Override
    public void setCarrierSlaName(final String carrierSlaName) {
        this.carrierSlaName = carrierSlaName;
    }

    /** {@inheritDoc} */
    @Override
    public String getCarrierName() {
        return carrierName;
    }

    /** {@inheritDoc} */
    @Override
    public void setCarrierName(final String carrierName) {
        this.carrierName = carrierName;
    }

    /** {@inheritDoc} */
    @Override
    public String getOrdernum() {
        return ordernum;
    }

    /** {@inheritDoc} */
    @Override
    public void setOrdernum(final String ordernum) {
        this.ordernum = ordernum;
    }

    /** {@inheritDoc} */
    @Override
    public String getDeliveryGroup() {
        return deliveryGroup;
    }

    /** {@inheritDoc} */
    @Override
    public void setDeliveryGroup(final String deliveryGroup) {
        this.deliveryGroup = deliveryGroup;
    }

    /** {@inheritDoc} */
    @Override
    public String getDeliveryRemarks() {
        return deliveryRemarks;
    }

    /** {@inheritDoc} */
    @Override
    public void setDeliveryRemarks(final String deliveryRemarks) {
        this.deliveryRemarks = deliveryRemarks;
    }

    /** {@inheritDoc} */
    @Override
    public LocalDateTime getDeliveryEstimatedMin() {
        return deliveryEstimatedMin;
    }

    /** {@inheritDoc} */
    @Override
    public void setDeliveryEstimatedMin(final LocalDateTime deliveryEstimatedMin) {
        this.deliveryEstimatedMin = deliveryEstimatedMin;
    }

    /** {@inheritDoc} */
    @Override
    public LocalDateTime getDeliveryEstimatedMax() {
        return deliveryEstimatedMax;
    }

    /** {@inheritDoc} */
    @Override
    public void setDeliveryEstimatedMax(final LocalDateTime deliveryEstimatedMax) {
        this.deliveryEstimatedMax = deliveryEstimatedMax;
    }

    /** {@inheritDoc} */
    @Override
    public LocalDateTime getDeliveryGuaranteed() {
        return deliveryGuaranteed;
    }

    /** {@inheritDoc} */
    @Override
    public void setDeliveryGuaranteed(final LocalDateTime deliveryGuaranteed) {
        this.deliveryGuaranteed = deliveryGuaranteed;
    }

    /** {@inheritDoc} */
    @Override
    public LocalDateTime getDeliveryConfirmed() {
        return deliveryConfirmed;
    }

    /** {@inheritDoc} */
    @Override
    public void setDeliveryConfirmed(final LocalDateTime deliveryConfirmed) {
        this.deliveryConfirmed = deliveryConfirmed;
    }

    /** {@inheritDoc} */
    @Override
    public LocalDateTime getRequestedDeliveryDate() {
        return requestedDeliveryDate;
    }

    /** {@inheritDoc} */
    @Override
    public void setRequestedDeliveryDate(final LocalDateTime requestedDeliveryDate) {
        this.requestedDeliveryDate = requestedDeliveryDate;
    }

    /** {@inheritDoc} */
    @Override
    public long getId() {
        return customerOrderDeliveryId;
    }

    /** {@inheritDoc} */
    @Override
    public String getPgLabel() {
        return pgLabel;
    }

    /** {@inheritDoc} */
    @Override
    public void setPgLabel(final String pgLabel) {
        this.pgLabel = pgLabel;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSupportCaptureMore() {
        return supportCaptureMore;
    }

    /** {@inheritDoc} */
    @Override
    public void setSupportCaptureMore(final boolean supportCaptureMore) {
        this.supportCaptureMore = supportCaptureMore;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSupportCaptureLess() {
        return supportCaptureLess;
    }

    /** {@inheritDoc} */
    @Override
    public void setSupportCaptureLess(final boolean supportCaptureLess) {
        this.supportCaptureLess = supportCaptureLess;
    }
}
