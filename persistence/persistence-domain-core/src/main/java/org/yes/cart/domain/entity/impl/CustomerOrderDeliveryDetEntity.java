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


import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.service.order.impl.DeliveryBucketImpl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class CustomerOrderDeliveryDetEntity implements org.yes.cart.domain.entity.CustomerOrderDeliveryDet, java.io.Serializable {

    private long customerOrderDeliveryDetId;
    private long version;

    private BigDecimal qty;
    private BigDecimal price;
    private BigDecimal salePrice;
    private BigDecimal listPrice;

    private BigDecimal netPrice;
    private BigDecimal grossPrice;
    private BigDecimal taxRate;
    private String taxCode;
    private boolean taxExclusiveOfPrice;

    private boolean gift;
    private boolean promoApplied;
    private boolean fixedPrice;
    private String appliedPromo;

    private String productSkuCode;
    private String productName;
    private String supplierCode;

    private String deliveryRemarks;
    private LocalDateTime deliveryEstimatedMin;
    private LocalDateTime deliveryEstimatedMax;
    private LocalDateTime deliveryGuaranteed;
    private LocalDateTime deliveryConfirmed;
    private BigDecimal deliveredQuantity;

    private String b2bRemarks;

    private String supplierInvoiceNo;
    private LocalDate supplierInvoiceDate;

    private CustomerOrderDelivery delivery;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    private CustomerOrderDetAttributesImpl storedAttributes = new CustomerOrderDetAttributesImpl();

    public CustomerOrderDeliveryDetEntity() {
    }


    @Override
    public BigDecimal getQty() {
        return this.qty;
    }

    @Override
    public void setQty(final BigDecimal qty) {
        this.qty = qty;
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
    public String getProductSkuCode() {
        return productSkuCode;
    }

    @Override
    public void setProductSkuCode(final String productSkuCode) {
        this.productSkuCode = productSkuCode;
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public void setProductName(final String productName) {
        this.productName = productName;
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
        return getDelivery().getDeliveryGroup();
    }

    @Override
    public DeliveryBucket getDeliveryBucket() {
        final String supplierCode = this.supplierCode == null ? "" : this.supplierCode;
        return new DeliveryBucketImpl(getDeliveryGroup(), supplierCode, getDelivery().getDeliveryNum());
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
    public BigDecimal getDeliveredQuantity() {
        return deliveredQuantity;
    }

    @Override
    public void setDeliveredQuantity(final BigDecimal deliveredQuantity) {
        this.deliveredQuantity = deliveredQuantity;
    }

    @Override
    public boolean isDeliveryRejected() {
        return this.deliveredQuantity != null && this.deliveredQuantity.compareTo(BigDecimal.ZERO) == 0;
    }

    @Override
    public boolean isDeliveryDifferent() {
        return this.deliveredQuantity != null && this.qty != null && this.deliveredQuantity.compareTo(this.qty) != 0;
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
        return supplierInvoiceNo;
    }

    @Override
    public void setSupplierInvoiceNo(final String supplierInvoiceNo) {
        this.supplierInvoiceNo = supplierInvoiceNo;
    }

    @Override
    public LocalDate getSupplierInvoiceDate() {
        return supplierInvoiceDate;
    }

    @Override
    public void setSupplierInvoiceDate(final LocalDate supplierInvoiceDate) {
        this.supplierInvoiceDate = supplierInvoiceDate;
    }

    @Override
    public CustomerOrderDelivery getDelivery() {
        return this.delivery;
    }

    @Override
    public void setDelivery(final CustomerOrderDelivery delivery) {
        this.delivery = delivery;
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
    public long getCustomerOrderDeliveryDetId() {
        return this.customerOrderDeliveryDetId;
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
    public BigDecimal getListPrice() {
        return listPrice;
    }

    @Override
    public void setListPrice(final BigDecimal listPrice) {
        this.listPrice = listPrice;
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
    public boolean isFixedPrice() {
        return fixedPrice;
    }

    @Override
    public void setFixedPrice(final boolean fixedPrice) {
        this.fixedPrice = fixedPrice;
    }

    @Override
    public String getAppliedPromo() {
        return appliedPromo;
    }

    @Override
    public void setAppliedPromo(final String appliedPromo) {
        this.appliedPromo = appliedPromo;
    }


    public String getStoredAttributesInternal() {
        return storedAttributes != null ? storedAttributes.toString() : null;
    }

    public void setStoredAttributesInternal(final String storedAttributesInternal) {
        this.storedAttributes = new CustomerOrderDetAttributesImpl(storedAttributesInternal);
    }

    @Override
    public Pair<String, I18NModel> getValue(final String code) {
        return storedAttributes != null ? storedAttributes.getValue(code) : null;
    }

    @Override
    public void putValue(final String code, final String value, final I18NModel displayValue) {
        if (storedAttributes == null) {
            storedAttributes = new CustomerOrderDetAttributesImpl();
        }
        storedAttributes.putValue(code, value, displayValue);
    }

    @Override
    public Map<String, Pair<String, I18NModel>> getAllValues() {
        return storedAttributes != null ? storedAttributes.getAllValues() : Collections.emptyMap();
    }

    @Override
    public void setAllValues(final Map<String, Pair<String, I18NModel>> allValues) {
        this.storedAttributes = new CustomerOrderDetAttributesImpl(allValues);
    }

    @Override
    public long getId() {
        return this.customerOrderDeliveryDetId;
    }

    @Override
    public void setCustomerOrderDeliveryDetId(final long customerOrderDeliveryDetId) {
        this.customerOrderDeliveryDetId = customerOrderDeliveryDetId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


