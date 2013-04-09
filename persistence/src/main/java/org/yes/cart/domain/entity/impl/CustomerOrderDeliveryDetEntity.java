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

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
@Entity
@Table(name = "TCUSTOMERORDERDELIVERYDET"
)
public class CustomerOrderDeliveryDetEntity implements org.yes.cart.domain.entity.CustomerOrderDeliveryDet, java.io.Serializable {


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


    @Column(name = "QTY", nullable = false)
    public BigDecimal getQty() {
        return this.qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    @Column(name = "PRICE", nullable = false)
    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SKU_ID", nullable = false)
    public ProductSku getSku() {
        return this.sku;
    }

    public void setSku(ProductSku sku) {
        this.sku = sku;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMERORDERDELIVERY_ID", nullable = false)
    public CustomerOrderDelivery getDelivery() {
        return this.delivery;
    }

    public void setDelivery(CustomerOrderDelivery delivery) {
        this.delivery = delivery;
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


    private long customerOrderDeliveryDetId;

    //@GenericGenerator(name="generator", strategy="native", parameters={@Parameter(name="column", value="value"), @Parameter(name="table", value="HIBERNATE_UNIQUE_KEYS")})
    @Id
    @GeneratedValue
    /*(generator="generator")*/

    @Column(name = "CUSTOMERORDERDELIVERYDET_ID", nullable = false)
    public long getCustomerOrderDeliveryDetId() {
        return this.customerOrderDeliveryDetId;
    }

    /**
     * Get list / catalog price.
     * @return price
     */
    @Column(name = "LIST_PRICE", nullable = false)
    public BigDecimal getListPrice() {
        return listPrice;
    }

    /**
     * Set list (regular/catalog) price.
     * @param listPrice to set.
     */
    public void setListPrice(final BigDecimal listPrice) {
        this.listPrice = listPrice;
    }


    @Transient
    public long getId() {
        return this.customerOrderDeliveryDetId;
    }

    public void setCustomerOrderDeliveryDetId(long customerOrderDeliveryDetId) {
        this.customerOrderDeliveryDetId = customerOrderDeliveryDetId;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getProductSkuCode() {
        return this.sku == null ? null : this.sku.getCode();
    }


    // end of extra code specified in the hbm.xml files

}


