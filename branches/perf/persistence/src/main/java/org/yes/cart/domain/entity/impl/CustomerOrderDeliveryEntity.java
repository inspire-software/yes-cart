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


import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
@Entity
@Table(name = "TCUSTOMERORDERDELIVERY"
)
public class CustomerOrderDeliveryEntity implements org.yes.cart.domain.entity.CustomerOrderDelivery, java.io.Serializable {


    private String deliveryNum;
    private String refNo;
    private BigDecimal price;
    private String deliveryStatus;
    private Collection<CustomerOrderDeliveryDet> detail = new ArrayList<CustomerOrderDeliveryDet>(0);
    private CarrierSla carrierSla;
    private CustomerOrder customerOrder;
    private String deliveryGroup;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public CustomerOrderDeliveryEntity() {
    }



    @Column(name = "DELIVERYNUM")
    public String getDeliveryNum() {
        return this.deliveryNum;
    }

    public void setDeliveryNum(String deliveryNum) {
        this.deliveryNum = deliveryNum;
    }

    @Column(name = "REF_NO")
    public String getRefNo() {
        return this.refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    @Column(name = "PRICE", nullable = false)
    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Column(name = "DELIVERYSTATUS", nullable = false, length = 64)
    public String getDeliveryStatus() {
        return this.deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "delivery")
    public Collection<CustomerOrderDeliveryDet> getDetail() {
        return this.detail;
    }

    public void setDetail(Collection<CustomerOrderDeliveryDet> detail) {
        this.detail = detail;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CARRIERSLA_ID")
    public CarrierSla getCarrierSla() {
        return this.carrierSla;
    }

    public void setCarrierSla(CarrierSla carrierSla) {
        this.carrierSla = carrierSla;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMERORDER_ID", nullable = false)
    public CustomerOrder getCustomerOrder() {
        return this.customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    @Column(name = "DELIVERY_GROUP", nullable = false, length = 16)
    public String getDeliveryGroup() {
        return this.deliveryGroup;
    }

    public void setDeliveryGroup(String deliveryGroup) {
        this.deliveryGroup = deliveryGroup;
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


    private long customerOrderDeliveryId;


    //@GenericGenerator(name="generator", strategy="native", parameters={@Parameter(name="column", value="value"), @Parameter(name="table", value="HIBERNATE_UNIQUE_KEYS")})
    @Id
    @GeneratedValue
    /*(generator="generator")*/

    @Column(name = "CUSTOMERORDERDELIVERY_ID", nullable = false)
    public long getCustomerOrderDeliveryId() {
        return this.customerOrderDeliveryId;
    }


    @Transient
    public long getId() {
        return this.customerOrderDeliveryId;
    }

    public void setCustomerOrderDeliveryId(long customerOrderDeliveryId) {
        this.customerOrderDeliveryId = customerOrderDeliveryId;
    }


    // end of extra code specified in the hbm.xml files

}


