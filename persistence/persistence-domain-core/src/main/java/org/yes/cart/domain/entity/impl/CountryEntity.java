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


import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.time.Instant;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Instant: 27 0ct 2012
 * Time: 9:10 AM
 */
public class CountryEntity implements org.yes.cart.domain.entity.Country, java.io.Serializable {

    private long countryId;
    private long version;

    private String countryCode;
    private String isoCode;
    private String name;
    private String displayNameInternal;
    private I18NModel displayName;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public CountryEntity() {
    }




    @Override
    public String getCountryCode() {
        return this.countryCode;
    }

    @Override
    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public String getIsoCode() {
        return this.isoCode;
    }

    @Override
    public void setIsoCode(final String isoCode) {
        this.isoCode = isoCode;
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
    public long getCountryId() {
        return this.countryId;
    }

    @Override
    public long getId() {
        return this.countryId;
    }


    @Override
    public void setCountryId(final long countryId) {
        this.countryId = countryId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


