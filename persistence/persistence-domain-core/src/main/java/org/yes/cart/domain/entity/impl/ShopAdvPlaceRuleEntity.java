/*
 * Copyright 2009 Inspire-Software.com
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


import org.yes.cart.domain.entity.ShopAdvPlace;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class ShopAdvPlaceRuleEntity implements org.yes.cart.domain.entity.ShopAdvPlaceRule, java.io.Serializable {

    private long shopadvrulesId;
    private long version;

    private int rank;
    private String name;
    private String description;
    private LocalDateTime availablefrom;
    private LocalDateTime availableto;
    private String rule;
    private ShopAdvPlace shopAdvPlace;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ShopAdvPlaceRuleEntity() {
    }



    @Override
    public int getRank() {
        return this.rank;
    }

    @Override
    public void setRank(final int rank) {
        this.rank = rank;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public LocalDateTime getAvailablefrom() {
        return this.availablefrom;
    }

    @Override
    public void setAvailablefrom(final LocalDateTime availablefrom) {
        this.availablefrom = availablefrom;
    }

    @Override
    public LocalDateTime getAvailableto() {
        return this.availableto;
    }

    @Override
    public void setAvailableto(final LocalDateTime availableto) {
        this.availableto = availableto;
    }

    @Override
    public String getRule() {
        return this.rule;
    }

    @Override
    public void setRule(final String rule) {
        this.rule = rule;
    }

    @Override
    public ShopAdvPlace getShopAdvPlace() {
        return this.shopAdvPlace;
    }

    @Override
    public void setShopAdvPlace(final ShopAdvPlace shopAdvPlace) {
        this.shopAdvPlace = shopAdvPlace;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }

    @Override
    public long getShopadvrulesId() {
        return this.shopadvrulesId;
    }


    @Override
    public long getId() {
        return this.shopadvrulesId;
    }

    @Override
    public void setShopadvrulesId(final long shopadvrulesId) {
        this.shopadvrulesId = shopadvrulesId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


