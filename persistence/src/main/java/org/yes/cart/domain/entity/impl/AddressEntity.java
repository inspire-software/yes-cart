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


import org.yes.cart.domain.entity.Customer;

import java.time.Instant;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class AddressEntity implements org.yes.cart.domain.entity.Address, java.io.Serializable {

    private long addressId;
    private long version;

    private String city;
    private String postcode;
    private String addrline1;
    private String addrline2;
    private String addressType;
    private String countryCode;
    private String stateCode;
    private String phone1;
    private String phone2;
    private String mobile1;
    private String mobile2;
    private String salutation;
    private String firstname;
    private String lastname;
    private String middlename;
    private String email1;
    private String email2;
    private String custom0;
    private String custom1;
    private String custom2;
    private String custom3;
    private String custom4;
    private String custom5;
    private String custom6;
    private String custom7;
    private String custom8;
    private String custom9;
    private boolean defaultAddress;
    private Customer customer;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public AddressEntity() {
    }

    @Override
    public String getCity() {
        return this.city;
    }

    @Override
    public void setCity(final String city) {
        this.city = city;
    }

    @Override
    public String getPostcode() {
        return this.postcode;
    }

    @Override
    public void setPostcode(final String postcode) {
        this.postcode = postcode;
    }

    @Override
    public String getAddrline1() {
        return this.addrline1;
    }

    @Override
    public void setAddrline1(final String addrline1) {
        this.addrline1 = addrline1;
    }

    @Override
    public String getAddrline2() {
        return this.addrline2;
    }

    @Override
    public void setAddrline2(final String addrline2) {
        this.addrline2 = addrline2;
    }

    @Override
    public String getAddressType() {
        return this.addressType;
    }

    @Override
    public void setAddressType(final String addressType) {
        this.addressType = addressType;
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
    public String getStateCode() {
        return this.stateCode;
    }

    @Override
    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    @Override
    public String getPhone1() {
        return this.phone1;
    }

    @Override
    public void setPhone1(final String phone1) {
        this.phone1 = phone1;
    }

    @Override
    public String getPhone2() {
        return phone2;
    }

    @Override
    public void setPhone2(final String phone2) {
        this.phone2 = phone2;
    }

    @Override
    public String getMobile1() {
        return mobile1;
    }

    @Override
    public void setMobile1(final String mobile1) {
        this.mobile1 = mobile1;
    }

    @Override
    public String getMobile2() {
        return mobile2;
    }

    @Override
    public void setMobile2(final String mobile2) {
        this.mobile2 = mobile2;
    }

    @Override
    public String getFirstname() {
        return this.firstname;
    }

    @Override
    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    @Override
    public String getLastname() {
        return this.lastname;
    }

    @Override
    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String getMiddlename() {
        return this.middlename;
    }

    @Override
    public void setMiddlename(final String middlename) {
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
    public String getEmail1() {
        return email1;
    }

    @Override
    public void setEmail1(final String email1) {
        this.email1 = email1;
    }

    @Override
    public String getEmail2() {
        return email2;
    }

    @Override
    public void setEmail2(final String email2) {
        this.email2 = email2;
    }

    @Override
    public String getCustom0() {
        return custom0;
    }

    @Override
    public void setCustom0(final String custom0) {
        this.custom0 = custom0;
    }

    @Override
    public String getCustom1() {
        return custom1;
    }

    @Override
    public void setCustom1(final String custom1) {
        this.custom1 = custom1;
    }

    @Override
    public String getCustom2() {
        return custom2;
    }

    @Override
    public void setCustom2(final String custom2) {
        this.custom2 = custom2;
    }

    @Override
    public String getCustom3() {
        return custom3;
    }

    @Override
    public void setCustom3(final String custom3) {
        this.custom3 = custom3;
    }

    @Override
    public String getCustom4() {
        return custom4;
    }

    @Override
    public void setCustom4(final String custom4) {
        this.custom4 = custom4;
    }

    @Override
    public String getCustom5() {
        return custom5;
    }

    @Override
    public void setCustom5(final String custom5) {
        this.custom5 = custom5;
    }

    @Override
    public String getCustom6() {
        return custom6;
    }

    @Override
    public void setCustom6(final String custom6) {
        this.custom6 = custom6;
    }

    @Override
    public String getCustom7() {
        return custom7;
    }

    @Override
    public void setCustom7(final String custom7) {
        this.custom7 = custom7;
    }

    @Override
    public String getCustom8() {
        return custom8;
    }

    @Override
    public void setCustom8(final String custom8) {
        this.custom8 = custom8;
    }

    @Override
    public String getCustom9() {
        return custom9;
    }

    @Override
    public void setCustom9(final String custom9) {
        this.custom9 = custom9;
    }

    @Override
    public boolean isDefaultAddress() {
        return this.defaultAddress;
    }

    @Override
    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    @Override
    public Customer getCustomer() {
        return this.customer;
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
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
    public long getAddressId() {
        return this.addressId;
    }

    @Override
    public long getId() {
        return this.addressId;
    }

    @Override
    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


