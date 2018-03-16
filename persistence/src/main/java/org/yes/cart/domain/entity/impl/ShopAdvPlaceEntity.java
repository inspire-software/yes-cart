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

package org.yes.cart.domain.entity.impl;


import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopAdvPlaceRule;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class ShopAdvPlaceEntity implements org.yes.cart.domain.entity.ShopAdvPlace, java.io.Serializable {

    private long shopadvplaceId;
    private long version;

    private String name;
    private String description;
    private Shop shop;
    private Set<ShopAdvPlaceRule> shopAdvPlaceRules = new HashSet<>(0);
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ShopAdvPlaceEntity() {
    }



    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Shop getShop() {
        return this.shop;
    }

    @Override
    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @Override
    public Set<ShopAdvPlaceRule> getShopAdvPlaceRules() {
        return this.shopAdvPlaceRules;
    }

    @Override
    public void setShopAdvPlaceRules(Set<ShopAdvPlaceRule> shopAdvPlaceRules) {
        this.shopAdvPlaceRules = shopAdvPlaceRules;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public long getShopadvplaceId() {
        return this.shopadvplaceId;
    }


    @Override
    public long getId() {
        return this.shopadvplaceId;
    }

    @Override
    public void setShopadvplaceId(long shopadvplaceId) {
        this.shopadvplaceId = shopadvplaceId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


