/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.dto.CustomerOrderDTO;

import java.math.BigDecimal;
import java.util.Date;

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

    @DtoField(value = "ordernum")
    private String ordernum;

    @DtoField(value = "pgLabel")
    private String pgLabel;

    @DtoField(value = "billingAddress")
    private String billingAddress;

    @DtoField(value = "shippingAddress")
    private String shippingAddress;

    @DtoField(value = "cartGuid", readOnly = true)
    private String cartGuid;

    @DtoField(value = "currency", readOnly = true)
    private String currency;

    @DtoField(value = "orderMessage")
    private String orderMessage;

    @DtoField(value = "orderStatus")
    private String orderStatus;

    @DtoField(value = "multipleShipmentOption", readOnly = true)
    private boolean multipleShipmentOption;

    @DtoField(value = "orderTimestamp", readOnly = true)
    private Date orderTimestamp;
    

    @DtoField(value = "customer.email", readOnly = true)
    private String email;

    @DtoField(value = "customer.firstname", readOnly = true)
    private String firstname;

    @DtoField(value = "customer.lastname", readOnly = true)
    private String lastname;

    @DtoField(value = "customer.middlename", readOnly = true)
    private String middlename;
    
    @DtoField(value = "customer.customerId", readOnly = true)
    private long customerId;

    @DtoField(value = "shop.shopId", readOnly = true)
    private long shopId;

    @DtoField(value = "shop.code", readOnly = true)
    private String code;


    private BigDecimal amount = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE);


    /**
     * {@inheritDoc
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * {@inheritDoc
     */
    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * {@inheritDoc
     */
    public long getCustomerorderId() {
        return customerorderId;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return customerorderId;
    }

    /**
     * {@inheritDoc
     */
    public void setCustomerorderId(final long customerorderId) {
        this.customerorderId = customerorderId;
    }

    /**
     * {@inheritDoc
     */
    public String getOrdernum() {
        return ordernum;
    }

    /**
     * {@inheritDoc
     */
    public void setOrdernum(final String ordernum) {
        this.ordernum = ordernum;
    }

    /**
     * {@inheritDoc
     */
    public String getPgLabel() {
        return pgLabel;
    }

    /**
     * {@inheritDoc
     */
    public void setPgLabel(final String pgLabel) {
        this.pgLabel = pgLabel;
    }

    /**
     * {@inheritDoc
     */
    public String getBillingAddress() {
        return billingAddress;
    }

    /**
     * {@inheritDoc
     */
    public void setBillingAddress(final String billingAddress) {
        this.billingAddress = billingAddress;
    }

    /**
     * {@inheritDoc
     */
    public String getShippingAddress() {
        return shippingAddress;
    }

    /**
     * {@inheritDoc
     */
    public void setShippingAddress(final String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    /**
     * {@inheritDoc
     */
    public String getCartGuid() {
        return cartGuid;
    }

    /**
     * {@inheritDoc
     */
    public void setCartGuid(final String cartGuid) {
        this.cartGuid = cartGuid;
    }

    /**
     * {@inheritDoc
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * {@inheritDoc
     */
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    /**
     * {@inheritDoc
     */
    public String getOrderMessage() {
        return orderMessage;
    }

    /**
     * {@inheritDoc
     */
    public void setOrderMessage(final String orderMessage) {
        this.orderMessage = orderMessage;
    }

    /**
     * {@inheritDoc
     */
    public String getOrderStatus() {
        return orderStatus;
    }

    /**
     * {@inheritDoc
     */
    public void setOrderStatus(final String orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     * {@inheritDoc
     */
    public boolean isMultipleShipmentOption() {
        return multipleShipmentOption;
    }

    /**
     * {@inheritDoc
     */
    public void setMultipleShipmentOption(final boolean multipleShipmentOption) {
        this.multipleShipmentOption = multipleShipmentOption;
    }

    /**
     * {@inheritDoc
     */
    public Date getOrderTimestamp() {
        return orderTimestamp;
    }

    /**
     * {@inheritDoc
     */
    public void setOrderTimestamp(final Date orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

    /**
     * {@inheritDoc
     */
    public String getEmail() {
        return email;
    }

    /**
     * {@inheritDoc
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * {@inheritDoc
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * {@inheritDoc
     */
    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    /**
     * {@inheritDoc
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * {@inheritDoc
     */
    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    /**
     * {@inheritDoc
     */
    public String getMiddlename() {
        return middlename;
    }

    /**
     * {@inheritDoc
     */
    public void setMiddlename(final String middlename) {
        this.middlename = middlename;
    }

    /**
     * {@inheritDoc
     */
    public long getCustomerId() {
        return customerId;
    }

    /**
     * {@inheritDoc
     */
    public void setCustomerId(final long customerId) {
        this.customerId = customerId;
    }

    /**
     * {@inheritDoc
     */
    public long getShopId() {
        return shopId;
    }

    /**
     * {@inheritDoc
     */
    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    /**
     * {@inheritDoc
     */ public String getCode() {
        return code;
    }

    /**
     * {@inheritDoc
     */ public void setCode(final String code) {
        this.code = code;
    }
}
