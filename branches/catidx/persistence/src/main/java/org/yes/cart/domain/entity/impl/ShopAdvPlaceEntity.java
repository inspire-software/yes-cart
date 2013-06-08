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


import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopAdvPlaceRule;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
@Entity
@Table(name = "TSHOPADVPLACE"
)
public class ShopAdvPlaceEntity implements org.yes.cart.domain.entity.ShopAdvPlace, java.io.Serializable {


    private String name;
    private String description;
    private Shop shop;
    private Set<ShopAdvPlaceRule> shopAdvPlaceRules = new HashSet<ShopAdvPlaceRule>(0);
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ShopAdvPlaceEntity() {
    }




    @Column(name = "NAME", nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DESCRIPTION", length = 4000)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHOP_ID", nullable = false)
    public Shop getShop() {
        return this.shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shopAdvPlace")
    public Set<ShopAdvPlaceRule> getShopAdvPlaceRules() {
        return this.shopAdvPlaceRules;
    }

    public void setShopAdvPlaceRules(Set<ShopAdvPlaceRule> shopAdvPlaceRules) {
        this.shopAdvPlaceRules = shopAdvPlaceRules;
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


    private long shopadvplaceId;

    //@GenericGenerator(name="generator", strategy="native", parameters={@Parameter(name="column", value="value"), @Parameter(name="table", value="HIBERNATE_UNIQUE_KEYS")})

    @Id
    @GeneratedValue
    /*(generator="generator")*/
    @Column(name = "SHOPADVPLACE_ID", nullable = false)
    public long getShopadvplaceId() {
        return this.shopadvplaceId;
    }


    @Transient
    public long getId() {
        return this.shopadvplaceId;
    }

    public void setShopadvplaceId(long shopadvplaceId) {
        this.shopadvplaceId = shopadvplaceId;
    }


    // end of extra code specified in the hbm.xml files

}


