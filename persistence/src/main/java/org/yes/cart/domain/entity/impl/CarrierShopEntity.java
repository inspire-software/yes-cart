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


import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.domain.entity.CarrierShop;
import org.yes.cart.domain.entity.Shop;

import java.util.Date;

public class CarrierShopEntity implements CarrierShop, java.io.Serializable {

    private long carrierShopId;
    private long version;

    private Carrier carrier;
    private Shop shop;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public CarrierShopEntity() {
    }


    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(final Carrier carrier) {
        this.carrier = carrier;
    }

    public Shop getShop() {
        return this.shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
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

    public long getCarrierShopId() {
        return this.carrierShopId;
    }


    public long getId() {
        return this.carrierShopId;
    }

    public void setCarrierShopId(long carrierShopId) {
        this.carrierShopId = carrierShopId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


