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


import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.ProductSku;

import java.math.BigDecimal;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class CustomerOrderDeliveryDetEntity implements org.yes.cart.domain.entity.CustomerOrderDeliveryDet, java.io.Serializable {

    private long customerOrderDeliveryDetId;

    private BigDecimal qty;
    private BigDecimal price;
    private BigDecimal listPrice;
    private ProductSku sku;
    private CustomerOrderDelivery delivery;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

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

    public ProductSku getSku() {
        return this.sku;
    }

    public void setSku(ProductSku sku) {
        this.sku = sku;
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

    public BigDecimal getListPrice() {
        return listPrice;
    }

    public void setListPrice(final BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    public long getId() {
        return this.customerOrderDeliveryDetId;
    }

    public void setCustomerOrderDeliveryDetId(long customerOrderDeliveryDetId) {
        this.customerOrderDeliveryDetId = customerOrderDeliveryDetId;
    }

    public String getProductSkuCode() {
        return this.sku == null ? null : this.sku.getCode();
    }

}


