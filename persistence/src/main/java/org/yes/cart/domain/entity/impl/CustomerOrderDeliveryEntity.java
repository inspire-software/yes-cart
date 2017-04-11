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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

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
    private Date deliveryEstimatedMin;
    private Date deliveryEstimatedMax;
    private Date deliveryGuaranteed;
    private Date deliveryConfirmed;

    private Date requestedDeliveryDate;

    private String eligibleForExport;
    private boolean blockExport;
    private Date lastExportDate;
    private String lastExportStatus;
    private String lastExportDeliveryStatus;

    private Collection<CustomerOrderDeliveryDet> detail = new ArrayList<CustomerOrderDeliveryDet>(0);
    private CarrierSla carrierSla;
    private CustomerOrder customerOrder;
    private String deliveryGroup;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public CustomerOrderDeliveryEntity() {
    }



    public String getDeliveryNum() {
        return this.deliveryNum;
    }

    public void setDeliveryNum(String deliveryNum) {
        this.deliveryNum = deliveryNum;
    }

    public String getRefNo() {
        return this.refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    public String getAppliedPromo() {
        return appliedPromo;
    }

    public void setAppliedPromo(final String appliedPromo) {
        this.appliedPromo = appliedPromo;
    }

    public String getDeliveryStatus() {
        return this.deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
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

    public Collection<CustomerOrderDeliveryDet> getDetail() {
        return this.detail;
    }

    public void setDetail(Collection<CustomerOrderDeliveryDet> detail) {
        this.detail = detail;
    }

    public CarrierSla getCarrierSla() {
        return this.carrierSla;
    }

    public void setCarrierSla(CarrierSla carrierSla) {
        this.carrierSla = carrierSla;
    }

    public CustomerOrder getCustomerOrder() {
        return this.customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    public String getDeliveryGroup() {
        return this.deliveryGroup;
    }

    public void setDeliveryGroup(String deliveryGroup) {
        this.deliveryGroup = deliveryGroup;
    }

    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public long getCustomerOrderDeliveryId() {
        return this.customerOrderDeliveryId;
    }

    public long getId() {
        return this.customerOrderDeliveryId;
    }

    public void setCustomerOrderDeliveryId(long customerOrderDeliveryId) {
        this.customerOrderDeliveryId = customerOrderDeliveryId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


