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


import org.yes.cart.domain.entity.Customer;

import javax.persistence.*;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */

@Entity
@Table(name = "TADDRESS"
)
public class AddressEntity implements org.yes.cart.domain.entity.Address, java.io.Serializable {


    private String city;
    private String postcode;
    private String addrline1;
    private String addrline2;
    private String addressType;
    private String countryCode;
    private String stateCode;
    private String phoneList;
    private String firstname;
    private String lastname;
    private String middlename;
    private boolean defaultAddress;
    private Customer customer;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public AddressEntity() {
    }

    @Column(name = "CITY", nullable = false, length = 128)
    public String getCity() {
        return this.city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    @Column(name = "POSTCODE", length = 16)
    public String getPostcode() {
        return this.postcode;
    }

    public void setPostcode(final String postcode) {
        this.postcode = postcode;
    }

    @Column(name = "ADDRLINE1", nullable = false)
    public String getAddrline1() {
        return this.addrline1;
    }

    public void setAddrline1(final String addrline1) {
        this.addrline1 = addrline1;
    }

    @Column(name = "ADDRLINE2")
    public String getAddrline2() {
        return this.addrline2;
    }

    public void setAddrline2(final String addrline2) {
        this.addrline2 = addrline2;
    }

    @Column(name = "ADDRESS_TYPE", nullable = false, length = 1)
    public String getAddressType() {
        return this.addressType;
    }

    public void setAddressType(final String addressType) {
        this.addressType = addressType;
    }

    @Column(name = "COUNTRY_CODE", nullable = false, length = 64)
    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    @Column(name = "STATE_CODE", length = 64)
    public String getStateCode() {
        return this.stateCode;
    }

    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    @Column(name = "PHONES")
    public String getPhoneList() {
        return this.phoneList;
    }

    public void setPhoneList(final String phoneList) {
        this.phoneList = phoneList;
    }

    @Column(name = "FIRSTNAME", nullable = false, length = 128)
    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    @Column(name = "LASTNAME", nullable = false, length = 128)
    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    @Column(name = "MIDDLENAME", length = 128)
    public String getMiddlename() {
        return this.middlename;
    }

    public void setMiddlename(final String middlename) {
        this.middlename = middlename;
    }

    @Column(name = "DEFAULT_ADDR", length = 1)
    public boolean isDefaultAddress() {
        return this.defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_TIMESTAMP")
    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_TIMESTAMP")
    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Column(name = "CREATED_BY", length = 64)
    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "UPDATED_BY", length = 64)
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "GUID", unique = true, nullable = false, length = 36)
    public String getGuid() {
        return this.guid;
    }

    public void setGuid(final String guid) {
        this.guid = guid;
    }


    // The following is extra code specified in the hbm.xml files


    private long addressId;

    //@GenericGenerator(name="generator", strategy="native", parameters={@Parameter(name="column", value="value"), @Parameter(name="table", value="HIBERNATE_UNIQUE_KEYS")})

    @Id
    @GeneratedValue
    /*(generator="generator")*/
    @Column(name = "ADDRESS_ID", nullable = false)
    public long getAddressId() {
        return this.addressId;
    }

    @Transient
    public long getId() {
        return this.addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }


    // end of extra code specified in the hbm.xml files

}


