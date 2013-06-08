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
import org.yes.cart.domain.entity.ProductSku;

import javax.persistence.*;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
@Entity
@Table(name = "TCUSTOMERWISHLIST"
)
public class CustomerWishListEntity implements org.yes.cart.domain.entity.CustomerWishList, java.io.Serializable {


    private ProductSku skus;
    private Customer customer;
    private String wlType;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public CustomerWishListEntity() {
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SKU_ID", nullable = false)
    public ProductSku getSkus() {
        return this.skus;
    }

    public void setSkus(ProductSku skus) {
        this.skus = skus;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Column(name = "WL_TYPE", length = 1)
    public String getWlType() {
        return this.wlType;
    }

    public void setWlType(String wlType) {
        this.wlType = wlType;
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


    private long customerwishlistId;

    //@GenericGenerator(name="generator", strategy="native", parameters={@Parameter(name="column", value="value"), @Parameter(name="table", value="HIBERNATE_UNIQUE_KEYS")})

    @Id
    @GeneratedValue
    /*(generator="generator")*/
    @Column(name = "CUSTOMERWISHLIST_ID", nullable = false)
    public long getCustomerwishlistId() {
        return this.customerwishlistId;
    }


    @Transient
    public long getId() {
        return this.customerwishlistId;
    }

    public void setCustomerwishlistId(long customerwishlistId) {
        this.customerwishlistId = customerwishlistId;
    }


    // end of extra code specified in the hbm.xml files

}


