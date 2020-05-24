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


import org.yes.cart.domain.entity.CarrierShop;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

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
    private String displayNameInternal;
    private I18NModel displayName;
    private String description;
    private String displayDescriptionInternal;
    private I18NModel displayDescription;
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
    public void setName(final String name) {
        this.name = name;
    }

    public String getDisplayNameInternal() {
        return displayNameInternal;
    }

    public void setDisplayNameInternal(final String displayNameInternal) {
        this.displayNameInternal = displayNameInternal;
        this.displayName = new StringI18NModel(displayNameInternal);
    }

    @Override
    public I18NModel getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(final I18NModel displayName) {
        this.displayName = displayName;
        this.displayNameInternal = displayName != null ? displayName.toString() : null;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDisplayDescriptionInternal() {
        return displayDescriptionInternal;
    }

    public void setDisplayDescriptionInternal(final String displayDescriptionInternal) {
        this.displayDescriptionInternal = displayDescriptionInternal;
        this.displayDescription = new StringI18NModel(displayDescriptionInternal);
    }

    @Override
    public I18NModel getDisplayDescription() {
        return this.displayDescription;
    }

    @Override
    public void setDisplayDescription(final I18NModel displayDescription) {
        this.displayDescription = displayDescription;
        this.displayDescriptionInternal = displayDescription != null ? displayDescription.toString() : null;
    }

    @Override
    public boolean isWorldwide() {
        return this.worldwide;
    }

    @Override
    public void setWorldwide(final boolean worldwide) {
        this.worldwide = worldwide;
    }

    @Override
    public boolean isCountry() {
        return this.country;
    }

    @Override
    public void setCountry(final boolean country) {
        this.country = country;
    }

    @Override
    public boolean isState() {
        return this.state;
    }

    @Override
    public void setState(final boolean state) {
        this.state = state;
    }

    @Override
    public boolean isLocal() {
        return this.local;
    }

    @Override
    public void setLocal(final boolean local) {
        this.local = local;
    }

    @Override
    public Collection<CarrierSla> getCarrierSla() {
        return this.carrierSla;
    }

    @Override
    public void setCarrierSla(final Collection<CarrierSla> carrierSla) {
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
    public long getCarrierId() {
        return this.carrierId;
    }

    @Override
    public long getId() {
        return this.carrierId;
    }

    @Override
    public void setCarrierId(final long carrierId) {
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


