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

package org.yes.cart.domain.ro;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.matcher.impl.NoopMatcher;
import org.yes.cart.domain.entity.CustomerOrderDet;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: denispavlov
 * Date: 02/04/2015
 * Time: 14:00
 */
@Dto
@XmlRootElement(name = "order")
public class OrderRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

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
    private String symbol;
    private String symbolPosition;

    @DtoField(value = "orderMessage", readOnly = true)
    private String orderMessage;

    @DtoField(value = "orderStatus", readOnly = true)
    private String orderStatus;

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

    @DtoField(value = "customer.customerId", readOnly = true)
    private long customerId;

    @DtoField(value = "shop.shopId", readOnly = true)
    private long shopId;

    @DtoField(value = "shop.code", readOnly = true)
    private String code;

    private CartTotalRO total;

    private List<DeliveryRO> deliveries = new ArrayList<DeliveryRO>(3);

    @DtoCollection(
            value = "orderDetail",
            dtoBeanKey = "org.yes.cart.domain.ro.OrderItemRO",
            entityGenericType = CustomerOrderDet.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private List<OrderItemRO> orderItems = new ArrayList<OrderItemRO>();

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

    @XmlAttribute(name = "customer-order-id")
    public long getCustomerorderId() {
        return customerorderId;
    }

    public void setCustomerorderId(final long customerorderId) {
        this.customerorderId = customerorderId;
    }

    @XmlAttribute(name = "ordernum")
    public String getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(final String ordernum) {
        this.ordernum = ordernum;
    }

    @XmlAttribute(name = "pg-label")
    public String getPgLabel() {
        return pgLabel;
    }

    public void setPgLabel(final String pgLabel) {
        this.pgLabel = pgLabel;
    }

    @XmlElement(name = "billing-address")
    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(final String billingAddress) {
        this.billingAddress = billingAddress;
    }

    @XmlElement(name = "shipping-address")
    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(final String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    @XmlAttribute(name = "cart-guid")
    public String getCartGuid() {
        return cartGuid;
    }

    public void setCartGuid(final String cartGuid) {
        this.cartGuid = cartGuid;
    }

    @XmlAttribute(name = "currency")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    @XmlAttribute(name = "symbol")
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(final String symbol) {
        this.symbol = symbol;
    }

    @XmlAttribute(name = "symbol-position")
    public String getSymbolPosition() {
        return symbolPosition;
    }

    public void setSymbolPosition(final String symbolPosition) {
        this.symbolPosition = symbolPosition;
    }

    @XmlElement(name = "order-message")
    public String getOrderMessage() {
        return orderMessage;
    }

    public void setOrderMessage(final String orderMessage) {
        this.orderMessage = orderMessage;
    }

    @XmlAttribute(name = "order-status")
    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(final String orderStatus) {
        this.orderStatus = orderStatus;
    }

    @XmlAttribute(name = "multiple-shipment-option")
    public boolean isMultipleShipmentOption() {
        return multipleShipmentOption;
    }

    public void setMultipleShipmentOption(final boolean multipleShipmentOption) {
        this.multipleShipmentOption = multipleShipmentOption;
    }

    @XmlAttribute(name = "order-timestamp")
    public Date getOrderTimestamp() {
        return orderTimestamp;
    }

    public void setOrderTimestamp(final Date orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

    @XmlAttribute(name = "order-ip")
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

    @XmlAttribute(name = "customer-id")
    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final long customerId) {
        this.customerId = customerId;
    }

    @XmlAttribute(name = "shop-id")
    public long getShopId() {
        return shopId;
    }

    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    @XmlAttribute(name = "shop-code")
    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    @XmlElement(name = "b2b-ref")
    public String getB2bRef() {
        return b2bRef;
    }

    public void setB2bRef(final String b2bRef) {
        this.b2bRef = b2bRef;
    }

    @XmlElement(name = "b2b-employee-id")
    public String getB2bEmployeeId() {
        return b2bEmployeeId;
    }

    public void setB2bEmployeeId(final String b2bEmployeeId) {
        this.b2bEmployeeId = b2bEmployeeId;
    }

    @XmlElement(name = "b2b-charge-id")
    public String getB2bChargeId() {
        return b2bChargeId;
    }

    public void setB2bChargeId(final String b2bChargeId) {
        this.b2bChargeId = b2bChargeId;
    }

    @XmlElement(name = "b2b-require-approve")
    public boolean isB2bRequireApprove() {
        return b2bRequireApprove;
    }

    public void setB2bRequireApprove(final boolean b2bRequireApprove) {
        this.b2bRequireApprove = b2bRequireApprove;
    }

    @XmlElement(name = "b2b-approved-by")
    public String getB2bApprovedBy() {
        return b2bApprovedBy;
    }

    public void setB2bApprovedBy(final String b2bApprovedBy) {
        this.b2bApprovedBy = b2bApprovedBy;
    }

    @XmlElement(name = "b2b-approved-date")
    public Date getB2bApprovedDate() {
        return b2bApprovedDate;
    }

    public void setB2bApprovedDate(final Date b2bApprovedDate) {
        this.b2bApprovedDate = b2bApprovedDate;
    }

    @XmlElement(name = "b2b-remarks")
    public String getB2bRemarks() {
        return b2bRemarks;
    }

    public void setB2bRemarks(final String b2bRemarks) {
        this.b2bRemarks = b2bRemarks;
    }

    @XmlElement(name = "requested-delivery-date")
    public Date getRequestedDeliveryDate() {
        return requestedDeliveryDate;
    }

    public void setRequestedDeliveryDate(final Date requestedDeliveryDate) {
        this.requestedDeliveryDate = requestedDeliveryDate;
    }

    public CartTotalRO getTotal() {
        return total;
    }

    public void setTotal(final CartTotalRO total) {
        this.total = total;
    }

    @XmlElementWrapper(name = "order-items")
    @XmlElement(name = "order-item")
    public List<OrderItemRO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(final List<OrderItemRO> orderItems) {
        this.orderItems = orderItems;
    }

    @XmlElementWrapper(name = "deliveries")
    @XmlElement(name = "delivery")
    public List<DeliveryRO> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(final List<DeliveryRO> deliveries) {
        this.deliveries = deliveries;
    }
}
