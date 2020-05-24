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


import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.Shop;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class ShopTopSellerEntity implements org.yes.cart.domain.entity.ShopTopSeller, java.io.Serializable {

    private long shopTopsellerId;
    private long version;

    private Shop shop;
    private Product product;
    private BigDecimal counter;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ShopTopSellerEntity() {
    }



    @Override
    public Shop getShop() {
        return this.shop;
    }

    @Override
    public void setShop(final Shop shop) {
        this.shop = shop;
    }

    @Override
    public Product getProduct() {
        return this.product;
    }

    @Override
    public void setProduct(final Product product) {
        this.product = product;
    }

    @Override
    public BigDecimal getCounter() {
        return this.counter;
    }

    @Override
    public void setCounter(final BigDecimal counter) {
        this.counter = counter;
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
    public long getShopTopsellerId() {
        return shopTopsellerId;
    }

    @Override
    public void setShopTopsellerId(final long shopTopsellerId) {
        this.shopTopsellerId = shopTopsellerId;
    }

    @Override
    public long getId() {
        return this.shopTopsellerId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


