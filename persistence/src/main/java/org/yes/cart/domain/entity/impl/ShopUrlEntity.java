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

import java.time.Instant;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class ShopUrlEntity implements org.yes.cart.domain.entity.ShopUrl, java.io.Serializable {

    private long storeUrlId;
    private long version;

    private String url;
    private String themeChain;
    private boolean primary;
    private Shop shop;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ShopUrlEntity() {
    }


    public ShopUrlEntity(String url, Shop shop, String guid) {
        this.url = url;
        this.shop = shop;
        this.guid = guid;
    }

    public ShopUrlEntity(String url, Shop shop, Instant createdTimestamp, Instant updatedTimestamp, String createdBy, String updatedBy, String guid) {
        this.url = url;
        this.shop = shop;
        this.createdTimestamp = createdTimestamp;
        this.updatedTimestamp = updatedTimestamp;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.guid = guid;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getThemeChain() {
        return themeChain;
    }

    @Override
    public void setThemeChain(final String themeChain) {
        this.themeChain = themeChain;
    }

    @Override
    public boolean isPrimary() {
        return primary;
    }

    @Override
    public void setPrimary(final boolean primary) {
        this.primary = primary;
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
    public long getStoreUrlId() {
        return this.storeUrlId;
    }


    @Override
    public long getId() {
        return this.storeUrlId;
    }

    @Override
    public void setStoreUrlId(long storeUrlId) {
        this.storeUrlId = storeUrlId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


