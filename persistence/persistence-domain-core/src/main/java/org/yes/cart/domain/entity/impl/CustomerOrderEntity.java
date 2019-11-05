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
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

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
    private Instant lastExportDate;
    private String lastExportStatus;
    private String lastExportOrderStatus;

    private String b2bRef;
    private String b2bEmployeeId;
    private String b2bChargeId;
    private boolean b2bRequireApprove;
    private String b2bApprovedBy;
    private LocalDateTime b2bApprovedDate;
    private String b2bRemarks;

    private LocalDateTime requestedDeliveryDate;

    private Customer customer;
    private Shop shop;
    private Collection<CustomerOrderDet> orderDetail = new ArrayList<>(0);
    private Collection<CustomerOrderDelivery> delivery = new ArrayList<>(0);
    private Collection<PromotionCouponUsage> coupons = new ArrayList<>(0);
    private String billingAddress;
    private String shippingAddress;
    private Address billingAddressDetails;
    private Address shippingAddressDetails;
    private boolean multipleShipmentOption;
    private LocalDateTime orderTimestamp;
    private String orderIp;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    private CustomerOrderDetAttributesImpl storedAttributes = new CustomerOrderDetAttributesImpl();

    public CustomerOrderEntity() {
    }


    @Override
    public String getPgLabel() {
        return this.pgLabel;
    }

    @Override
    public void setPgLabel(final String pgLabel) {
        this.pgLabel = pgLabel;
    }

    @Override
    public String getOrdernum() {
        return this.ordernum;
    }

    @Override
    public void setOrdernum(final String ordernum) {
        this.ordernum = ordernum;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(final String email) {
        this.email = email;
    }

    @Override
    public String getSalutation() {
        return salutation;
    }

    @Override
    public void setSalutation(final String salutation) {
        this.salutation = salutation;
    }

    @Override
    public String getFirstname() {
        return firstname;
    }

    @Override
    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    @Override
    public String getLastname() {
        return lastname;
    }

    @Override
    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String getMiddlename() {
        return middlename;
    }

    @Override
    public void setMiddlename(final String middlename) {
        this.middlename = middlename;
    }

    @Override
    public String getCartGuid() {
        return this.cartGuid;
    }

    @Override
    public void setCartGuid(final String cartGuid) {
        this.cartGuid = cartGuid;
    }

    @Override
    public String getLocale() {
        return locale;
    }

    @Override
    public void setLocale(final String locale) {
        this.locale = locale;
    }

    @Override
    public String getCurrency() {
        return this.currency;
    }

    @Override
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    @Override
    public String getOrderMessage() {
        return this.orderMessage;
    }

    @Override
    public void setOrderMessage(final String orderMessage) {
        this.orderMessage = orderMessage;
    }

    @Override
    public String getOrderStatus() {
        return this.orderStatus;
    }

    @Override
    public void setOrderStatus(final String orderStatus) {
        this.orderStatus = orderStatus;
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
    public String getLastExportOrderStatus() {
        return lastExportOrderStatus;
    }

    @Override
    public void setLastExportOrderStatus(final String lastExportOrderStatus) {
        this.lastExportOrderStatus = lastExportOrderStatus;
    }

    @Override
    public String getB2bRef() {
        return b2bRef;
    }

    @Override
    public void setB2bRef(final String b2bRef) {
        this.b2bRef = b2bRef;
    }

    @Override
    public String getB2bEmployeeId() {
        return b2bEmployeeId;
    }

    @Override
    public void setB2bEmployeeId(final String b2bEmployeeId) {
        this.b2bEmployeeId = b2bEmployeeId;
    }

    @Override
    public String getB2bChargeId() {
        return b2bChargeId;
    }

    @Override
    public void setB2bChargeId(final String b2bChargeId) {
        this.b2bChargeId = b2bChargeId;
    }

    @Override
    public boolean isB2bRequireApprove() {
        return b2bRequireApprove;
    }

    @Override
    public void setB2bRequireApprove(final boolean b2bRequireApprove) {
        this.b2bRequireApprove = b2bRequireApprove;
    }

    @Override
    public String getB2bApprovedBy() {
        return b2bApprovedBy;
    }

    @Override
    public void setB2bApprovedBy(final String b2bApprovedBy) {
        this.b2bApprovedBy = b2bApprovedBy;
    }

    @Override
    public LocalDateTime getB2bApprovedDate() {
        return b2bApprovedDate;
    }

    @Override
    public void setB2bApprovedDate(final LocalDateTime b2bApprovedDate) {
        this.b2bApprovedDate = b2bApprovedDate;
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
    public LocalDateTime getRequestedDeliveryDate() {
        return requestedDeliveryDate;
    }

    @Override
    public void setRequestedDeliveryDate(final LocalDateTime requestedDeliveryDate) {
        this.requestedDeliveryDate = requestedDeliveryDate;
    }

    @Override
    public Customer getCustomer() {
        return this.customer;
    }

    @Override
    public void setCustomer(final Customer customer) {
        this.customer = customer;
    }

    @Override
    public Shop getShop() {
        return this.shop;
    }

    @Override
    public void setShop(final Shop shop) {
        this.shop = shop;
    }

    @Override
    public Collection<PromotionCouponUsage> getCoupons() {
        return coupons;
    }

    @Override
    public void setCoupons(final Collection<PromotionCouponUsage> coupons) {
        this.coupons = coupons;
    }

    @Override
    public Collection<CustomerOrderDet> getOrderDetail() {
        return this.orderDetail;
    }

    @Override
    public void setOrderDetail(final Collection<CustomerOrderDet> orderDetail) {
        this.orderDetail = orderDetail;
    }

    @Override
    public Collection<CustomerOrderDelivery> getDelivery() {
        return this.delivery;
    }

    @Override
    public void setDelivery(final Collection<CustomerOrderDelivery> delivery) {
        this.delivery = delivery;
    }

    @Override
    public String getBillingAddress() {
        return this.billingAddress;
    }

    @Override
    public void setBillingAddress(final String billingAddress) {
        this.billingAddress = billingAddress;
    }

    @Override
    public Address getBillingAddressDetails() {
        return billingAddressDetails;
    }

    @Override
    public void setBillingAddressDetails(final Address billingAddressDetails) {
        this.billingAddressDetails = billingAddressDetails;
    }

    @Override
    public String getShippingAddress() {
        return this.shippingAddress;
    }

    @Override
    public void setShippingAddress(final String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    @Override
    public Address getShippingAddressDetails() {
        return shippingAddressDetails;
    }

    @Override
    public void setShippingAddressDetails(final Address shippingAddressDetails) {
        this.shippingAddressDetails = shippingAddressDetails;
    }

    @Override
    public boolean isMultipleShipmentOption() {
        return this.multipleShipmentOption;
    }

    @Override
    public void setMultipleShipmentOption(final boolean multipleShipmentOption) {
        this.multipleShipmentOption = multipleShipmentOption;
    }

    @Override
    public LocalDateTime getOrderTimestamp() {
        return this.orderTimestamp;
    }

    @Override
    public void setOrderTimestamp(final LocalDateTime orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

    @Override
    public String getOrderIp() {
        return orderIp;
    }

    @Override
    public void setOrderIp(final String orderIp) {
        this.orderIp = orderIp;
    }

    @Override
    public BigDecimal getOrderTotal() {
        BigDecimal total = getGrossPrice();
        // deliveries are eagerly fetched so there should not be a lazy init issue
        for (final CustomerOrderDelivery delivery : getDelivery()) {
            total = total.add(delivery.getGrossPrice());
        }
        return total;
    }

    @Override
    public BigDecimal getOrderGrossTotal() {
        return getOrderTotal();
    }

    @Override
    public BigDecimal getOrderNetTotal() {
        BigDecimal total = getNetPrice();
        // deliveries are eagerly fetched so there should not be a lazy init issue
        for (final CustomerOrderDelivery delivery : getDelivery()) {
            total = total.add(delivery.getNetPrice());
        }
        return total;
    }

    @Override
    public BigDecimal getOrderTotalTax() {
        BigDecimal totalTax = getGrossPrice().subtract(getNetPrice());
        // deliveries are eagerly fetched so there should not be a lazy init issue
        for (final CustomerOrderDelivery delivery : getDelivery()) {
            totalTax = totalTax.add(delivery.getGrossPrice().subtract(delivery.getNetPrice()));
        }
        return totalTax;
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
    public long getCustomerorderId() {
        return this.customerorderId;
    }


    @Override
    public long getId() {
        return this.customerorderId;
    }

    @Override
    public void setCustomerorderId(final long customerorderId) {
        this.customerorderId = customerorderId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    @Override
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


