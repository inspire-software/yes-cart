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


import org.yes.cart.domain.entity.ManagerShop;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class ManagerEntity implements org.yes.cart.domain.entity.Manager, java.io.Serializable {

    private long managerId;
    private long version;

    private Collection<ManagerShop> shops = new ArrayList<>(0);

    private String email;
    private String salutation;
    private String firstname;
    private String lastname;
    private String middlename;
    private String password;
    private String authToken;
    private boolean enabled;
    private Instant authTokenExpiry;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ManagerEntity() {
    }



    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getFirstname() {
        return this.firstname;
    }

    @Override
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Override
    public String getLastname() {
        return this.lastname;
    }

    @Override
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String getMiddlename() {
        return this.middlename;
    }

    @Override
    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    @Override
    public String getSalutation() {
        return salutation;
    }

    @Override
    public void setSalutation(final String salutation) {
        this.salutation = salutation;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getAuthToken() {
        return authToken;
    }

    @Override
    public void setAuthToken(final String authToken) {
        this.authToken = authToken;
    }

    @Override
    public Instant getAuthTokenExpiry() {
        return authTokenExpiry;
    }

    @Override
    public void setAuthTokenExpiry(final Instant authTokenExpiry) {
        this.authTokenExpiry = authTokenExpiry;
    }

    @Override
    public boolean getEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
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
    public long getManagerId() {
        return this.managerId;
    }

    @Override
    public long getId() {
        return this.managerId;
    }

    @Override
    public void setManagerId(long managerId) {
        this.managerId = managerId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    @Override
    public Collection<ManagerShop> getShops() {
        return this.shops;
    }

    @Override
    public void setShops(Collection<ManagerShop> shops) {
        this.shops = shops;
    }
}


