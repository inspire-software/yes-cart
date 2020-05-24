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


import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;

import java.time.Instant;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class CustomerShopEntity implements org.yes.cart.domain.entity.CustomerShop, java.io.Serializable {

    private long customerShopId;
    private long version;

    private Customer customer;
    private Shop shop;
    private boolean disabled;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public CustomerShopEntity() {
    }


    @Override
    public Customer getCustomer() {
        return this.customer;
    }

    @Override
    public void setCustomer(final Customer customer) {
        this.customer = customer;
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
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

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
    public long getCustomerShopId() {
        return this.customerShopId;
    }


    public long getId() {
        return this.customerShopId;
    }

    @Override
    public void setCustomerShopId(final long customerShopId) {
        this.customerShopId = customerShopId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


