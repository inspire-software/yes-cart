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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.CustomerOrderDTO;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class CustomerOrderDTOImpl implements CustomerOrderDTO {

    private static final long serialVersionUID = 20110325L;

    @DtoField(value = "customerorderId", readOnly = true)
    private long customerorderId;

    @DtoField(value = "ordernum", readOnly = true)
    private String ordernum;

    @DtoField(value = "pgLabel", readOnly = true)
    private String pgLabel;

    @DtoField(value = "billingAddress", readOnly = true)
    private String billingAddress;

    @DtoField(value = "shippingAddress", readOnly = true)
    private String shippingAddress;

    @DtoField(value = "cartGuid", readOnly = true)
    private String cartGuid;

    @DtoField(value = "currency", readOnly = true)
    private String currency;

    @DtoField(value = "orderMessage", readOnly = true)
    private String orderMessage;

    @DtoField(value = "orderStatus", readOnly = true)
    private String orderStatus;

    @DtoField(value = "multipleShipmentOption", readOnly = true)
    private boolean multipleShipmentOption;

    @DtoField(value = "orderTimestamp", readOnly = true)
    private LocalDateTime orderTimestamp;

    @DtoField(value = "orderIp", readOnly = true)
    private String orderIp;

    @DtoField(value = "email", readOnly = true)
    private String email;

    @DtoField(value = "salutation")
    private String salutation;

    @DtoField(value = "firstname", readOnly = true)
    private String firstname;

    @DtoField(value = "lastname", readOnly = true)
    private String lastname;

    @DtoField(value = "middlename", readOnly = true)
    private String middlename;
    
    @DtoField(value = "customer.customerId", readOnly = true)
    private long customerId;

    @DtoField(value = "shop.shopId", readOnly = true)
    private long shopId;

    @DtoField(value = "shop.code", readOnly = true)
    private String code;


    private BigDecimal amount = MoneyUtils.ZERO;

    @DtoField(value = "orderTotal", readOnly = true)
    private BigDecimal orderTotal = MoneyUtils.ZERO;
    @DtoField(value = "orderTotalTax", readOnly = true)
    private BigDecimal orderTotalTax = MoneyUtils.ZERO;
    @DtoField(value = "orderGrossTotal", readOnly = true)
    private BigDecimal orderGrossTotal = MoneyUtils.ZERO;
    @DtoField(value = "orderNetTotal", readOnly = true)
    private BigDecimal orderNetTotal = MoneyUtils.ZERO;

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
    @DtoField(value = "appliedPromo", readOnly = true)
    private String appliedPromo;

    @DtoField(value = "eligibleForExport", readOnly = true)
    private String eligibleForExport;
    @DtoField(value = "blockExport", readOnly = true)
    private boolean blockExport;
    @DtoField(value = "lastExportDate", readOnly = true)
    private Instant lastExportDate;
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
    private LocalDateTime b2bApprovedDate;
    @DtoField(value = "b2bRemarks", readOnly = true)
    private String b2bRemarks;

    @DtoField(value = "requestedDeliveryDate", readOnly = true)
    private LocalDateTime requestedDeliveryDate;

    @DtoField(value = "allValues", readOnly = true, converter = "customValuesMapConverter")
    private Map<String, Pair<String, Map<String, String>>> allValues;


    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getOrderTotal() {
        return orderTotal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOrderTotal(final BigDecimal orderTotal) {
        this.orderTotal = orderTotal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getOrderTotalTax() {
        return orderTotalTax;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOrderTotalTax(final BigDecimal orderTotalTax) {
        this.orderTotalTax = orderTotalTax;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getOrderGrossTotal() {
        return orderGrossTotal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOrderGrossTotal(final BigDecimal orderGrossTotal) {
        this.orderGrossTotal = orderGrossTotal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getOrderNetTotal() {
        return orderNetTotal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOrderNetTotal(final BigDecimal orderNetTotal) {
        this.orderNetTotal = orderNetTotal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getNetPrice() {
        return netPrice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNetPrice(final BigDecimal netPrice) {
        this.netPrice = netPrice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getGrossPrice() {
        return grossPrice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGrossPrice(final BigDecimal grossPrice) {
        this.grossPrice = grossPrice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getListPrice() {
        return listPrice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setListPrice(final BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPromoApplied() {
        return promoApplied;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPromoApplied(final boolean promoApplied) {
        this.promoApplied = promoApplied;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAppliedPromo() {
        return appliedPromo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAppliedPromo(final String appliedPromo) {
        this.appliedPromo = appliedPromo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCustomerorderId() {
        return customerorderId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return customerorderId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustomerorderId(final long customerorderId) {
        this.customerorderId = customerorderId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOrdernum() {
        return ordernum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOrdernum(final String ordernum) {
        this.ordernum = ordernum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPgLabel() {
        return pgLabel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPgLabel(final String pgLabel) {
        this.pgLabel = pgLabel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBillingAddress() {
        return billingAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBillingAddress(final String billingAddress) {
        this.billingAddress = billingAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShippingAddress() {
        return shippingAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShippingAddress(final String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCartGuid() {
        return cartGuid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCartGuid(final String cartGuid) {
        this.cartGuid = cartGuid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCurrency() {
        return currency;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOrderMessage() {
        return orderMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOrderMessage(final String orderMessage) {
        this.orderMessage = orderMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOrderStatus() {
        return orderStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOrderStatus(final String orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMultipleShipmentOption() {
        return multipleShipmentOption;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMultipleShipmentOption(final boolean multipleShipmentOption) {
        this.multipleShipmentOption = multipleShipmentOption;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getOrderTimestamp() {
        return orderTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOrderTimestamp(final LocalDateTime orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOrderIp() {
        return orderIp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOrderIp(final String orderIp) {
        this.orderIp = orderIp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEmail() {
        return email;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFirstname() {
        return firstname;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLastname() {
        return lastname;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMiddlename() {
        return middlename;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMiddlename(final String middlename) {
        this.middlename = middlename;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSalutation() {
        return salutation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSalutation(final String salutation) {
        this.salutation = salutation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEligibleForExport() {
        return eligibleForExport;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEligibleForExport(final String eligibleForExport) {
        this.eligibleForExport = eligibleForExport;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBlockExport() {
        return blockExport;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBlockExport(final boolean blockExport) {
        this.blockExport = blockExport;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instant getLastExportDate() {
        return lastExportDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLastExportDate(final Instant lastExportDate) {
        this.lastExportDate = lastExportDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLastExportStatus() {
        return lastExportStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLastExportStatus(final String lastExportStatus) {
        this.lastExportStatus = lastExportStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLastExportOrderStatus() {
        return lastExportOrderStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLastExportOrderStatus(final String lastExportOrderStatus) {
        this.lastExportOrderStatus = lastExportOrderStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getB2bRef() {
        return b2bRef;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setB2bRef(final String b2bRef) {
        this.b2bRef = b2bRef;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getB2bEmployeeId() {
        return b2bEmployeeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setB2bEmployeeId(final String b2bEmployeeId) {
        this.b2bEmployeeId = b2bEmployeeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getB2bChargeId() {
        return b2bChargeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setB2bChargeId(final String b2bChargeId) {
        this.b2bChargeId = b2bChargeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isB2bRequireApprove() {
        return b2bRequireApprove;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setB2bRequireApprove(final boolean b2bRequireApprove) {
        this.b2bRequireApprove = b2bRequireApprove;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getB2bApprovedBy() {
        return b2bApprovedBy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setB2bApprovedBy(final String b2bApprovedBy) {
        this.b2bApprovedBy = b2bApprovedBy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getB2bApprovedDate() {
        return b2bApprovedDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setB2bApprovedDate(final LocalDateTime b2bApprovedDate) {
        this.b2bApprovedDate = b2bApprovedDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getB2bRemarks() {
        return b2bRemarks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setB2bRemarks(final String b2bRemarks) {
        this.b2bRemarks = b2bRemarks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getRequestedDeliveryDate() {
        return requestedDeliveryDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRequestedDeliveryDate(final LocalDateTime requestedDeliveryDate) {
        this.requestedDeliveryDate = requestedDeliveryDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Pair<String, Map<String, String>>> getAllValues() {
        return allValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAllValues(final Map<String, Pair<String, Map<String, String>>> allValues) {
        this.allValues = allValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCustomerId() {
        return customerId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustomerId(final long customerId) {
        this.customerId = customerId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getShopId() {
        return shopId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    /**
     * {@inheritDoc}
     */ @Override
    public String getCode() {
        return code;
    }

    /**
     * {@inheritDoc}
     */ @Override
    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "CustomerOrderDTOImpl{" +
                "customerorderId=" + customerorderId +
                ", ordernum='" + ordernum + '\'' +
                ", pgLabel='" + pgLabel + '\'' +
                ", billingAddress='" + billingAddress + '\'' +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", cartGuid='" + cartGuid + '\'' +
                ", currency='" + currency + '\'' +
                ", orderMessage='" + orderMessage + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", multipleShipmentOption=" + multipleShipmentOption +
                ", orderTimestamp=" + orderTimestamp +
                ", email='" + email + '\'' +
                ", salutation='" + salutation + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", middlename='" + middlename + '\'' +
                ", customerId=" + customerId +
                ", shopId=" + shopId +
                ", code='" + code + '\'' +
                ", amount=" + amount +
                '}';
    }
}
