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
import org.yes.cart.domain.entity.Warehouse;

import java.time.Instant;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class ShopWarehouseEntity implements org.yes.cart.domain.entity.ShopWarehouse, java.io.Serializable {

    private long shopWarehouseId;
    private long version;

    private Shop shop;
    private Warehouse warehouse;
    private Integer rank;
    private boolean disabled;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ShopWarehouseEntity() {
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
    public Warehouse getWarehouse() {
        return this.warehouse;
    }

    @Override
    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public Integer getRank() {
        if (this.rank == null) {
            this.rank = 0;
        }
        return this.rank;
    }

    @Override
    public void setRank(Integer rank) {
        this.rank = rank;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
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
    public long getShopWarehouseId() {
        return shopWarehouseId;
    }

    @Override
    public long getId() {
        return this.shopWarehouseId;
    }

    @Override
    public void setShopWarehouseId(long shopWarehouseId) {
        this.shopWarehouseId = shopWarehouseId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


