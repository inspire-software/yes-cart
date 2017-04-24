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
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.misc.MutablePair;

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
public class VoCustomerOrderInfo {

    @DtoField(value = "customerorderId", readOnly = true)
    private long customerorderId;

    @DtoField(value = "ordernum", readOnly = true)
    private String ordernum;

    @DtoField(value = "pgLabel", readOnly = true)
    private String pgLabel;
    private String pgName;

    @DtoField(value = "billingAddress", readOnly = true)
    private String billingAddress;

    @DtoField(value = "shippingAddress", readOnly = true)
    private String shippingAddress;

    @DtoField(value = "cartGuid", readOnly = true)
    private String cartGuid;

    @DtoField(value = "currency", readOnly = true)
    private String currency;

    @DtoField(value = "orderMessage")
    private String orderMessage;

    @DtoField(value = "orderStatus", readOnly = true)
    private String orderStatus;
    private List<String> orderStatusNextOptions = Collections.emptyList();
    private String orderPaymentStatus;

    @DtoField(value = "multipleShipmentOption", readOnly = true)
    private boolean multipleShipmentOption;

    @DtoField(value = "orderTimestamp", readOnly = true)
    private Date orderTimestamp;

    @DtoField(value = "orderIp", readOnly = true)
    private String orderIp;

    @DtoField(value = "email", readOnly = true)
    private String email;

    @DtoField(value = "salutation", readOnly = true)
    private String salutation;

    @DtoField(value = "firstname", readOnly = true)
    private String firstname;

    @DtoField(value = "lastname", readOnly = true)
    private String lastname;

    @DtoField(value = "middlename", readOnly = true)
    private String middlename;
    
    @DtoField(value = "customerId", readOnly = true)
    private long customerId;

    @DtoField(value = "shopId", readOnly = true)
    private long shopId;

    @DtoField(value = "code", readOnly = true)
    private String code;

    @DtoField(value = "amount", readOnly = true)
    private BigDecimal orderPaymentBalance = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE);

    @DtoField(value = "orderTotal", readOnly = true)
    private BigDecimal orderTotal = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE);

    @DtoField(value = "price", readOnly = true)
    private BigDecimal price;
    @DtoField(value = "netPrice", readOnly = true)
    private BigDecimal netPrice;
    @DtoField(value = "grossPrice", readOnly = true)
    private BigDecimal grossPrice;
    @DtoField(value = "listPrice", readOnly = true)
    private BigDecimal listPrice;
    @DtoField(value = "promoApplied", readOnly = true)
    private boolean promoApplied;
    @DtoField(value = "appliedPromo", converter = "CSVToList", readOnly = true)
    private List<String> appliedPromo;

    @DtoField(value = "eligibleForExport", readOnly = true)
    private String eligibleForExport;
    @DtoField(value = "blockExport", readOnly = true)
    private boolean blockExport;
    @DtoField(value = "lastExportDate", readOnly = true)
    private Date lastExportDate;
    @DtoField(value = "lastExportStatus", readOnly = true)
    private String lastExportStatus;
    @DtoField(value = "lastExportOrderStatus", readOnly = true)
    private String lastExportOrderStatus;

    @DtoField(value = "b2bRef", readOnly = true)
    private String b2bRef;
    @DtoField(value = "b2bEmployeeId", readOnly = true)
    private String b2bEmployeeId;
    @DtoField(value = "b2bChargeId", readOnly = true)
    private String b2bChargeId;
    @DtoField(value = "b2bRequireApprove", readOnly = true)
    private boolean b2bRequireApprove;
    @DtoField(value = "b2bApprovedBy", readOnly = true)
    private String b2bApprovedBy;
    @DtoField(value = "b2bApprovedDate", readOnly = true)
    private Date b2bApprovedDate;
    @DtoField(value = "b2bRemarks", readOnly = true)
    private String b2bRemarks;

    @DtoField(value = "requestedDeliveryDate", readOnly = true)
    private Date requestedDeliveryDate;

    @DtoField(value = "allValues", converter = "PairMapToPairList", readOnly = true)
    private List<MutablePair<String, MutablePair<String, String>>> allValues;

    public long getCustomerorderId() {
        return customerorderId;
    }

    public void setCustomerorderId(final long customerorderId) {
        this.customerorderId = customerorderId;
    }

    public String getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(final String ordernum) {
        this.ordernum = ordernum;
    }

    public String getPgLabel() {
        return pgLabel;
    }

    public void setPgLabel(final String pgLabel) {
        this.pgLabel = pgLabel;
    }

    public String getPgName() {
        return pgName;
    }

    public void setPgName(final String pgName) {
        this.pgName = pgName;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(final String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(final String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getCartGuid() {
        return cartGuid;
    }

    public void setCartGuid(final String cartGuid) {
        this.cartGuid = cartGuid;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public String getOrderMessage() {
        return orderMessage;
    }

    public void setOrderMessage(final String orderMessage) {
        this.orderMessage = orderMessage;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(final String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<String> getOrderStatusNextOptions() {
        return orderStatusNextOptions;
    }

    public void setOrderStatusNextOptions(final List<String> orderStatusNextOptions) {
        this.orderStatusNextOptions = orderStatusNextOptions;
    }

    public String getOrderPaymentStatus() {
        return orderPaymentStatus;
    }

    public void setOrderPaymentStatus(final String orderPaymentStatus) {
        this.orderPaymentStatus = orderPaymentStatus;
    }

    public boolean isMultipleShipmentOption() {
        return multipleShipmentOption;
    }

    public void setMultipleShipmentOption(final boolean multipleShipmentOption) {
        this.multipleShipmentOption = multipleShipmentOption;
    }

    public Date getOrderTimestamp() {
        return orderTimestamp;
    }

    public void setOrderTimestamp(final Date orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

    public String getOrderIp() {
        return orderIp;
    }

    public void setOrderIp(final String orderIp) {
        this.orderIp = orderIp;
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

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final long customerId) {
        this.customerId = customerId;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public BigDecimal getOrderPaymentBalance() {
        return orderPaymentBalance;
    }

    public void setOrderPaymentBalance(final BigDecimal orderPaymentBalance) {
        this.orderPaymentBalance = orderPaymentBalance;
    }

    public BigDecimal getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(final BigDecimal orderTotal) {
        this.orderTotal = orderTotal;
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

    public List<String> getAppliedPromo() {
        return appliedPromo;
    }

    public void setAppliedPromo(final List<String> appliedPromo) {
        this.appliedPromo = appliedPromo;
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

    public List<MutablePair<String, MutablePair<String, String>>> getAllValues() {
        return allValues;
    }

    public void setAllValues(final List<MutablePair<String, MutablePair<String, String>>> allValues) {
        this.allValues = allValues;
    }
}
