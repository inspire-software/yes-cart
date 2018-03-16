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


import org.yes.cart.domain.entity.CarrierShop;
import org.yes.cart.domain.entity.CarrierSla;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Instant: 27 0ct 2012
 * Time: 9:10 AM
 */
public class CarrierEntity implements org.yes.cart.domain.entity.Carrier, java.io.Serializable {

    private long carrierId;
    private long version;

    private String name;
    private String displayName;
    private String description;
    private String displayDescription;
    private boolean worldwide;
    private boolean country;
    private boolean state;
    private boolean local;
    private Collection<CarrierSla> carrierSla = new ArrayList<>(0);
    private Collection<CarrierShop> shops = new ArrayList<>(0);
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public CarrierEntity() {
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
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
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
    public String getDisplayDescription() {
        return displayDescription;
    }

    @Override
    public void setDisplayDescription(final String displayDescription) {
        this.displayDescription = displayDescription;
    }

    @Override
    public boolean isWorldwide() {
        return this.worldwide;
    }

    @Override
    public void setWorldwide(boolean worldwide) {
        this.worldwide = worldwide;
    }

    @Override
    public boolean isCountry() {
        return this.country;
    }

    @Override
    public void setCountry(boolean country) {
        this.country = country;
    }

    @Override
    public boolean isState() {
        return this.state;
    }

    @Override
    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public boolean isLocal() {
        return this.local;
    }

    @Override
    public void setLocal(boolean local) {
        this.local = local;
    }

    @Override
    public Collection<CarrierSla> getCarrierSla() {
        return this.carrierSla;
    }

    @Override
    public void setCarrierSla(Collection<CarrierSla> carrierSla) {
        this.carrierSla = carrierSla;
    }

    @Override
    public Collection<CarrierShop> getShops() {
        return shops;
    }

    @Override
    public void setShops(final Collection<CarrierShop> shops) {
        this.shops = shops;
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
    public long getCarrierId() {
        return this.carrierId;
    }

    @Override
    public long getId() {
        return this.carrierId;
    }

    @Override
    public void setCarrierId(long carrierId) {
        this.carrierId = carrierId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


