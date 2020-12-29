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


import org.yes.cart.domain.entity.ShoppingCartState;

import java.time.Instant;

public class ShoppingCartStateEntity implements ShoppingCartState, java.io.Serializable {

    private long shoppingCartStateId;
    private long version;

    private Boolean empty = Boolean.TRUE;
    private String customerLogin;
    private long shopId;
    private String ordernum;
    private byte[] state;

    private Boolean managed = Boolean.TRUE;

    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ShoppingCartStateEntity() {
    }

    @Override
    public String getCustomerLogin() {
        return customerLogin;
    }

    @Override
    public void setCustomerLogin(final String customerLogin) {
        this.customerLogin = customerLogin;
    }

    @Override
    public long getShopId() {
        return shopId;
    }

    @Override
    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    @Override
    public String getOrdernum() {
        return ordernum;
    }

    @Override
    public void setOrdernum(final String ordernum) {
        this.ordernum = ordernum;
    }

    @Override
    public Boolean getEmpty() {
        return empty;
    }

    @Override
    public void setEmpty(final Boolean empty) {
        this.empty = empty;
    }

    @Override
    public Boolean getManaged() {
        return managed;
    }

    @Override
    public void setManaged(final Boolean managed) {
        this.managed = managed;
    }

    @Override
    public byte[] getState() {
        return state;
    }

    @Override
    public void setState(final byte[] state) {
        this.state = state;
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
    public long getShoppingCartStateId() {
        return shoppingCartStateId;
    }

    @Override
    public void setShoppingCartStateId(final long shoppingCartStateId) {
        this.shoppingCartStateId = shoppingCartStateId;
    }

    @Override
    public long getId() {
        return this.shoppingCartStateId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return this.getGuid();
    }

}


