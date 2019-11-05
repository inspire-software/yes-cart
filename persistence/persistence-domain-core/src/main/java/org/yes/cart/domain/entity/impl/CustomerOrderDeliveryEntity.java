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
package org.yes.cart.domain.entity.impl;


import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class CustomerOrderDeliveryEntity implements org.yes.cart.domain.entity.CustomerOrderDelivery, java.io.Serializable {

    private long customerOrderDeliveryId;
    private long version;

    private String deliveryNum;
    private String refNo;

    private BigDecimal price;
    private BigDecimal listPrice;

    private BigDecimal netPrice;
    private BigDecimal grossPrice;
    private BigDecimal taxRate;
    private String taxCode;
    private boolean taxExclusiveOfPrice;

    private boolean promoApplied;
    private String appliedPromo;

    private String deliveryStatus;

    private String deliveryRemarks;
    private LocalDateTime deliveryEstimatedMin;
    private LocalDateTime deliveryEstimatedMax;
    private LocalDateTime deliveryGuaranteed;
    private LocalDateTime deliveryConfirmed;

    private LocalDateTime requestedDeliveryDate;

    private String eligibleForExport;
    private boolean blockExport;
    private Instant lastExportDate;
    private String lastExportStatus;
    private String lastExportDeliveryStatus;

    private Collection<CustomerOrderDeliveryDet> detail = new ArrayList<>(0);
    private CarrierSla carrierSla;
    private CustomerOrder customerOrder;
    private String deliveryGroup;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public CustomerOrderDeliveryEntity() {
    }



    @Override
    public String getDeliveryNum() {
        return this.deliveryNum;
    }

    @Override
    public void setDeliveryNum(final String deliveryNum) {
        this.deliveryNum = deliveryNum;
    }

    @Override
    public String getRefNo() {
        return this.refNo;
    }

    @Override
    public void setRefNo(final String refNo) {
        this.refNo = refNo;
    }

    @Override
    public BigDecimal getPrice() {
        return this.price;
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
    }

    @Override
    public BigDecimal getGrossPrice() {
        return grossPrice;
    }

    @Override
    public void setGrossPrice(final BigDecimal grossPrice) {
        this.grossPrice = grossPrice;
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
    public String getDeliveryStatus() {
        return this.deliveryStatus;
    }

    @Override
    public void setDeliveryStatus(final String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
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
        return deliveryConfirmed;
    }

    @Override
    public void setDeliveryConfirmed(final LocalDateTime deliveryConfirmed) {
        this.deliveryConfirmed = deliveryConfirmed;
    }

    @Override
    public LocalDateTime getRequestedDeliveryDate() {
        return requestedDeliveryDate;
    }

    @Override
    public void setRequestedDeliveryDate(final LocalDateTime requestedDeliveryDate) {
        this.requestedDeliveryDate = requestedDeliveryDate;
    }

    @Override
    public String getEligibleForExport() {
        return eligibleForExport;
    }

    @Override
    public void setEligibleForExport(final String eligibleForExport) {
        this.eligibleForExport = eligibleForExport;
    }

    @Override
    public boolean isBlockExport() {
        return blockExport;
    }

    @Override
    public void setBlockExport(final boolean blockExport) {
        this.blockExport = blockExport;
    }

    @Override
    public Instant getLastExportDate() {
        return lastExportDate;
    }

    @Override
    public void setLastExportDate(final Instant lastExportDate) {
        this.lastExportDate = lastExportDate;
    }

    @Override
    public String getLastExportStatus() {
        return lastExportStatus;
    }

    @Override
    public void setLastExportStatus(final String lastExportStatus) {
        this.lastExportStatus = lastExportStatus;
    }

    @Override
    public String getLastExportDeliveryStatus() {
        return lastExportDeliveryStatus;
    }

    @Override
    public void setLastExportDeliveryStatus(final String lastExportDeliveryStatus) {
        this.lastExportDeliveryStatus = lastExportDeliveryStatus;
    }

    @Override
    public Collection<CustomerOrderDeliveryDet> getDetail() {
        return this.detail;
    }

    @Override
    public void setDetail(final Collection<CustomerOrderDeliveryDet> detail) {
        this.detail = detail;
    }

    @Override
    public CarrierSla getCarrierSla() {
        return this.carrierSla;
    }

    @Override
    public void setCarrierSla(final CarrierSla carrierSla) {
        this.carrierSla = carrierSla;
    }

    @Override
    public CustomerOrder getCustomerOrder() {
        return this.customerOrder;
    }

    @Override
    public void setCustomerOrder(final CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    @Override
    public String getDeliveryGroup() {
        return this.deliveryGroup;
    }

    @Override
    public void setDeliveryGroup(final String deliveryGroup) {
        this.deliveryGroup = deliveryGroup;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }

    @Override
    public long getCustomerOrderDeliveryId() {
        return this.customerOrderDeliveryId;
    }

    @Override
    public long getId() {
        return this.customerOrderDeliveryId;
    }

    @Override
    public void setCustomerOrderDeliveryId(final long customerOrderDeliveryId) {
        this.customerOrderDeliveryId = customerOrderDeliveryId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


