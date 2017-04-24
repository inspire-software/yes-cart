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


import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class CustomerOrderEntity implements org.yes.cart.domain.entity.CustomerOrder, java.io.Serializable {

    private long customerorderId;
    private long version;

    private String pgLabel;
    private String ordernum;
    private String cartGuid;

    private String email;
    private String salutation;
    private String firstname;
    private String lastname;
    private String middlename;

    private String currency;
    private BigDecimal price;
    private BigDecimal listPrice;

    private BigDecimal netPrice;
    private BigDecimal grossPrice;

    private boolean promoApplied;
    private String appliedPromo;

    private String locale;
    private String orderMessage;

    private String orderStatus;

    private String eligibleForExport;
    private boolean blockExport;
    private Date lastExportDate;
    private String lastExportStatus;
    private String lastExportOrderStatus;

    private String b2bRef;
    private String b2bEmployeeId;
    private String b2bChargeId;
    private boolean b2bRequireApprove;
    private String b2bApprovedBy;
    private Date b2bApprovedDate;
    private String b2bRemarks;

    private Date requestedDeliveryDate;

    private Customer customer;
    private Shop shop;
    private Collection<CustomerOrderDet> orderDetail = new ArrayList<CustomerOrderDet>(0);
    private Collection<CustomerOrderDelivery> delivery = new ArrayList<CustomerOrderDelivery>(0);
    private Collection<PromotionCouponUsage> coupons = new ArrayList<PromotionCouponUsage>(0);
    private String billingAddress;
    private String shippingAddress;
    private Address billingAddressDetails;
    private Address shippingAddressDetails;
    private boolean multipleShipmentOption;
    private Date orderTimestamp;
    private String orderIp;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    private CustomerOrderDetAttributesImpl storedAttributes = new CustomerOrderDetAttributesImpl();

    public CustomerOrderEntity() {
    }


    public String getPgLabel() {
        return this.pgLabel;
    }

    public void setPgLabel(String pgLabel) {
        this.pgLabel = pgLabel;
    }

    public String getOrdernum() {
        return this.ordernum;
    }

    public void setOrdernum(String ordernum) {
        this.ordernum = ordernum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(final String salutation) {
        this.salutation = salutation;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(final String middlename) {
        this.middlename = middlename;
    }

    public String getCartGuid() {
        return this.cartGuid;
    }

    public void setCartGuid(String cartGuid) {
        this.cartGuid = cartGuid;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(final String locale) {
        this.locale = locale;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOrderMessage() {
        return this.orderMessage;
    }

    public void setOrderMessage(String orderMessage) {
        this.orderMessage = orderMessage;
    }

    public String getOrderStatus() {
        return this.orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
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

    public String getLastExportOrderStatus() {
        return lastExportOrderStatus;
    }

    public void setLastExportOrderStatus(final String lastExportOrderStatus) {
        this.lastExportOrderStatus = lastExportOrderStatus;
    }

    public String getB2bRef() {
        return b2bRef;
    }

    public void setB2bRef(final String b2bRef) {
        this.b2bRef = b2bRef;
    }

    public String getB2bEmployeeId() {
        return b2bEmployeeId;
    }

    public void setB2bEmployeeId(final String b2bEmployeeId) {
        this.b2bEmployeeId = b2bEmployeeId;
    }

    public String getB2bChargeId() {
        return b2bChargeId;
    }

    public void setB2bChargeId(final String b2bChargeId) {
        this.b2bChargeId = b2bChargeId;
    }

    public boolean isB2bRequireApprove() {
        return b2bRequireApprove;
    }

    public void setB2bRequireApprove(final boolean b2bRequireApprove) {
        this.b2bRequireApprove = b2bRequireApprove;
    }

    public String getB2bApprovedBy() {
        return b2bApprovedBy;
    }

    public void setB2bApprovedBy(final String b2bApprovedBy) {
        this.b2bApprovedBy = b2bApprovedBy;
    }

    public Date getB2bApprovedDate() {
        return b2bApprovedDate;
    }

    public void setB2bApprovedDate(final Date b2bApprovedDate) {
        this.b2bApprovedDate = b2bApprovedDate;
    }

    public String getB2bRemarks() {
        return b2bRemarks;
    }

    public void setB2bRemarks(final String b2bRemarks) {
        this.b2bRemarks = b2bRemarks;
    }

    public Date getRequestedDeliveryDate() {
        return requestedDeliveryDate;
    }

    public void setRequestedDeliveryDate(final Date requestedDeliveryDate) {
        this.requestedDeliveryDate = requestedDeliveryDate;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Shop getShop() {
        return this.shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Collection<PromotionCouponUsage> getCoupons() {
        return coupons;
    }

    public void setCoupons(Collection<PromotionCouponUsage> coupons) {
        this.coupons = coupons;
    }

    public Collection<CustomerOrderDet> getOrderDetail() {
        return this.orderDetail;
    }

    public void setOrderDetail(Collection<CustomerOrderDet> orderDetail) {
        this.orderDetail = orderDetail;
    }

    public Collection<CustomerOrderDelivery> getDelivery() {
        return this.delivery;
    }

    public void setDelivery(Collection<CustomerOrderDelivery> delivery) {
        this.delivery = delivery;
    }

    public String getBillingAddress() {
        return this.billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public Address getBillingAddressDetails() {
        return billingAddressDetails;
    }

    public void setBillingAddressDetails(final Address billingAddressDetails) {
        this.billingAddressDetails = billingAddressDetails;
    }

    public String getShippingAddress() {
        return this.shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Address getShippingAddressDetails() {
        return shippingAddressDetails;
    }

    public void setShippingAddressDetails(final Address shippingAddressDetails) {
        this.shippingAddressDetails = shippingAddressDetails;
    }

    public boolean isMultipleShipmentOption() {
        return this.multipleShipmentOption;
    }

    public void setMultipleShipmentOption(boolean multipleShipmentOption) {
        this.multipleShipmentOption = multipleShipmentOption;
    }

    public Date getOrderTimestamp() {
        return this.orderTimestamp;
    }

    public void setOrderTimestamp(Date orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

    public String getOrderIp() {
        return orderIp;
    }

    public void setOrderIp(final String orderIp) {
        this.orderIp = orderIp;
    }

    public BigDecimal getOrderTotal() {
        BigDecimal total = getGrossPrice();
        // deliveries are eagerly fetched so there should not be a lazy init issue
        for (final CustomerOrderDelivery delivery : getDelivery()) {
            total = total.add(delivery.getGrossPrice());
        }
        return total;
    }

    public BigDecimal getOrderTotalTax() {
        BigDecimal totalTax = getGrossPrice().subtract(getNetPrice());
        // deliveries are eagerly fetched so there should not be a lazy init issue
        for (final CustomerOrderDelivery delivery : getDelivery()) {
            totalTax = totalTax.add(delivery.getGrossPrice().subtract(delivery.getNetPrice()));
        }
        return totalTax;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
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

    public long getCustomerorderId() {
        return this.customerorderId;
    }


    public long getId() {
        return this.customerorderId;
    }

    public void setCustomerorderId(long customerorderId) {
        this.customerorderId = customerorderId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    public CustomerOrderDelivery getCustomerOrderDelivery(final String deliveryNumber) {
        if (deliveryNumber != null) {
            for (CustomerOrderDelivery delivery : getDelivery()) {
                if (deliveryNumber.equals(delivery.getDeliveryNum())) {
                    return delivery;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "CustomerOrderEntity{" +
                "pgLabel='" + pgLabel + '\'' +
                ", ordernum='" + ordernum + '\'' +
                ", cartGuid='" + cartGuid + '\'' +
                ", currency='" + currency + '\'' +
                ", price='" + price + '\'' +
                ", salePrice='" + listPrice + '\'' +
                ", promoApplied='" + promoApplied + '\'' +
                ", appliedPromo='" + appliedPromo + '\'' +
                ", customer=" + customer +
                ", orderStatus='" + orderStatus + '\'' +
                ", shop=" + shop +
                ", guid='" + guid + '\'' +
                ", multipleShipmentOption=" + multipleShipmentOption +
                '}';
    }

}


