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

package org.yes.cart.domain.entity.impl;


import org.yes.cart.domain.entity.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

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

    private String currency;
    private BigDecimal price;
    private BigDecimal listPrice;

    private boolean promoApplied;
    private String appliedPromo;

    private String locale;
    private String orderMessage;
    private String orderStatus;
    private Customer customer;
    private Shop shop;
    private Collection<CustomerOrderDet> orderDetail = new ArrayList<CustomerOrderDet>(0);
    private Collection<CustomerOrderDelivery> delivery = new ArrayList<CustomerOrderDelivery>(0);
    private Collection<PromotionCouponUsage> coupons = new ArrayList<PromotionCouponUsage>(0);
    private String billingAddress;
    private String shippingAddress;
    private boolean multipleShipmentOption;
    private Date orderTimestamp;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

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

    public String getShippingAddress() {
        return this.shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
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


