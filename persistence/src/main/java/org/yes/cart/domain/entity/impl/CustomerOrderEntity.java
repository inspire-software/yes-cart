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


import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.CustomerOrderDet;
import org.yes.cart.domain.entity.Shop;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
@Entity
@Table(name = "TCUSTOMERORDER"
)
public class CustomerOrderEntity implements org.yes.cart.domain.entity.CustomerOrder, java.io.Serializable {


    private String pgLabel;
    private String ordernum;
    private String cartGuid;
    private String currency;
    private String orderMessage;
    private String orderStatus;
    private Customer customer;
    private Shop shop;
    private Collection<CustomerOrderDet> orderDetail = new ArrayList<CustomerOrderDet>(0);
    private Collection<CustomerOrderDelivery> delivery = new ArrayList<CustomerOrderDelivery>(0);
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



    @Column(name = "PG_LABEL")
    public String getPgLabel() {
        return this.pgLabel;
    }

    public void setPgLabel(String pgLabel) {
        this.pgLabel = pgLabel;
    }

    @Column(name = "ORDERNUM")
    public String getOrdernum() {
        return this.ordernum;
    }

    public void setOrdernum(String ordernum) {
        this.ordernum = ordernum;
    }

    @Column(name = "CART_GUID", nullable = false, length = 36)
    public String getCartGuid() {
        return this.cartGuid;
    }

    public void setCartGuid(String cartGuid) {
        this.cartGuid = cartGuid;
    }

    @Column(name = "CURRENCY", nullable = false, length = 3)
    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name = "MESSAGE")
    public String getOrderMessage() {
        return this.orderMessage;
    }

    public void setOrderMessage(String orderMessage) {
        this.orderMessage = orderMessage;
    }

    @Column(name = "ORDERSTATUS", nullable = false, length = 64)
    public String getOrderStatus() {
        return this.orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CUSTOMER_ID")
    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SHOP_ID", nullable = false)
    public Shop getShop() {
        return this.shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "customerOrder")
    public Collection<CustomerOrderDet> getOrderDetail() {
        return this.orderDetail;
    }

    public void setOrderDetail(Collection<CustomerOrderDet> orderDetail) {
        this.orderDetail = orderDetail;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "customerOrder", orphanRemoval = true)
    public Collection<CustomerOrderDelivery> getDelivery() {
        return this.delivery;
    }

    public void setDelivery(Collection<CustomerOrderDelivery> delivery) {
        this.delivery = delivery;
    }

    @Column(name = "BILLING_ADDRESS")
    public String getBillingAddress() {
        return this.billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    @Column(name = "SHIPPING_ADDRESS")
    public String getShippingAddress() {
        return this.shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    @Column(name = "MULTIPLE_SHIPMENT")
    public boolean isMultipleShipmentOption() {
        return this.multipleShipmentOption;
    }

    public void setMultipleShipmentOption(boolean multipleShipmentOption) {
        this.multipleShipmentOption = multipleShipmentOption;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ORDER_TIMESTAMP", nullable = false)
    public Date getOrderTimestamp() {
        return this.orderTimestamp;
    }

    public void setOrderTimestamp(Date orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_TIMESTAMP")
    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_TIMESTAMP")
    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Column(name = "CREATED_BY", length = 64)
    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "UPDATED_BY", length = 64)
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "GUID", unique = true, nullable = false, length = 36)
    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }


    // The following is extra code specified in the hbm.xml files


    private long customerorderId;


    //@GenericGenerator(name="generator", strategy="native", parameters={@Parameter(name="column", value="value"), @Parameter(name="table", value="HIBERNATE_UNIQUE_KEYS")})

    @Id
    @GeneratedValue
    /*(generator="generator")*/
    @Column(name = "CUSTOMERORDER_ID", nullable = false)
    public long getCustomerorderId() {
        return this.customerorderId;
    }


    @Transient
    public long getId() {
        return this.customerorderId;
    }

    public void setCustomerorderId(long customerorderId) {
        this.customerorderId = customerorderId;
    }

    /**
     * {@inheritDoc}
     */
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
                ", customer=" + customer +
                ", orderStatus='" + orderStatus + '\'' +
                ", shop=" + shop +
                ", guid='" + guid + '\'' +
                ", multipleShipmentOption=" + multipleShipmentOption +
                '}';
    }


    // end of extra code specified in the hbm.xml files

}


