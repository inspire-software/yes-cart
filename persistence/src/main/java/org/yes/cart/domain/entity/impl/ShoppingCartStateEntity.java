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


import org.yes.cart.domain.entity.ShoppingCartState;

import java.util.Date;

public class ShoppingCartStateEntity implements ShoppingCartState, java.io.Serializable {

    private long shoppingCartStateId;
    private long version;

    private Boolean empty = Boolean.TRUE;
    private String customerEmail;
    private String ordernum;
    private byte[] state;

    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ShoppingCartStateEntity() {
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(final String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(final String ordernum) {
        this.ordernum = ordernum;
    }

    public Boolean getEmpty() {
        return empty;
    }

    public void setEmpty(final Boolean empty) {
        this.empty = empty;
    }

    public byte[] getState() {
        return state;
    }

    public void setState(final byte[] state) {
        this.state = state;
    }

    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }


    public long getShoppingCartStateId() {
        return shoppingCartStateId;
    }

    public void setShoppingCartStateId(final long shoppingCartStateId) {
        this.shoppingCartStateId = shoppingCartStateId;
    }

    public long getId() {
        return this.shoppingCartStateId;
    }

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


