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
// Generated Oct 26, 2012 9:09:11 AM by Hibernate Tools 3.2.2.GA


import org.yes.cart.domain.entity.ShopAdvPlace;

import javax.persistence.*;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
@Entity
@Table(name = "TSHOPADVRULES"
)
public class ShopAdvPlaceRuleEntity implements org.yes.cart.domain.entity.ShopAdvPlaceRule, java.io.Serializable {


    private int rank;
    private String name;
    private String description;
    private Date availablefrom;
    private Date availableto;
    private String rule;
    private ShopAdvPlace shopAdvPlace;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ShopAdvPlaceRuleEntity() {
    }




    @Column(name = "rank")
    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "AVAILABLEFROM")
    public Date getAvailablefrom() {
        return this.availablefrom;
    }

    public void setAvailablefrom(Date availablefrom) {
        this.availablefrom = availablefrom;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "AVAILABLETO")
    public Date getAvailableto() {
        return this.availableto;
    }

    public void setAvailableto(Date availableto) {
        this.availableto = availableto;
    }

    @Column(name = "RULE", length = 4000)
    public String getRule() {
        return this.rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHOPADVPLACE_ID", nullable = false)
    public ShopAdvPlace getShopAdvPlace() {
        return this.shopAdvPlace;
    }

    public void setShopAdvPlace(ShopAdvPlace shopAdvPlace) {
        this.shopAdvPlace = shopAdvPlace;
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


    private long shopadvrulesId;

    //@GenericGenerator(name="generator", strategy="native", parameters={@Parameter(name="column", value="value"), @Parameter(name="table", value="HIBERNATE_UNIQUE_KEYS")})

    @Id
    @GeneratedValue
    /*(generator="generator")*/
    @Column(name = "STOREADVRULES_ID", nullable = false)
    public long getShopadvrulesId() {
        return this.shopadvrulesId;
    }


    @Transient
    public long getId() {
        return this.shopadvrulesId;
    }

    public void setShopadvrulesId(long shopadvrulesId) {
        this.shopadvrulesId = shopadvrulesId;
    }


    // end of extra code specified in the hbm.xml files

}


