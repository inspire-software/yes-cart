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
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.service.order.impl.DeliveryBucketImpl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
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
    private Date deliveryEstimatedMin;
    private Date deliveryEstimatedMax;
    private Date deliveryGuaranteed;
    private Date deliveryConfirmed;
    private BigDecimal deliveredQuantity;

    private String b2bRemarks;

    private String supplierInvoiceNo;
    private Date supplierInvoiceDate;

    private CustomerOrderDelivery delivery;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    private CustomerOrderDetAttributesImpl storedAttributes = new CustomerOrderDetAttributesImpl();

    public CustomerOrderDeliveryDetEntity() {
    }


    public BigDecimal getQty() {
        return this.qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
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

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(final String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getDeliveryGroup() {
        return getDelivery().getDeliveryGroup();
    }

    public DeliveryBucket getDeliveryBucket() {
        final String supplierCode = this.supplierCode == null ? "" : this.supplierCode;
        return new DeliveryBucketImpl(getDeliveryGroup(), supplierCode, getDelivery().getDeliveryNum());
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
        return this.deliveredQuantity != null && this.deliveredQuantity.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isDeliveryDifferent() {
        return this.deliveredQuantity != null && this.qty != null && this.deliveredQuantity.compareTo(this.qty) != 0;
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

    public CustomerOrderDelivery getDelivery() {
        return this.delivery;
    }

    public void setDelivery(CustomerOrderDelivery delivery) {
        this.delivery = delivery;
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

    public long getCustomerOrderDeliveryDetId() {
        return this.customerOrderDeliveryDetId;
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


    public String getStoredAttributesInternal() {
        return storedAttributes != null ? storedAttributes.toString() : null;
    }

    public void setStoredAttributesInternal(final String storedAttributesInternal) {
        this.storedAttributes = new CustomerOrderDetAttributesImpl(storedAttributesInternal);
    }

    public Pair<String, String> getValue(final String code) {
        return storedAttributes != null ? storedAttributes.getValue(code) : null;
    }

    public void putValue(final String code, final String value, final String displayValue) {
        if (storedAttributes == null) {
            storedAttributes = new CustomerOrderDetAttributesImpl();
        }
        storedAttributes.putValue(code, value, displayValue);
    }

    public Map<String, Pair<String, String>> getAllValues() {
        return storedAttributes != null ? storedAttributes.getAllValues() : Collections.<String, Pair<String, String>>emptyMap();
    }

    public void setAllValues(final Map<String, Pair<String, String>> allValues) {
        this.storedAttributes = new CustomerOrderDetAttributesImpl(allValues);
    }

    public long getId() {
        return this.customerOrderDeliveryDetId;
    }

    public void setCustomerOrderDeliveryDetId(long customerOrderDeliveryDetId) {
        this.customerOrderDeliveryDetId = customerOrderDeliveryDetId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


