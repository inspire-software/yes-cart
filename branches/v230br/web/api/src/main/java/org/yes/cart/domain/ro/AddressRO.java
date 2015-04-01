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

package org.yes.cart.domain.ro;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * User: denispavlov
 * Date: 28/03/2015
 * Time: 17:26
 */
@Dto
@XmlRootElement(name = "address")
public class AddressRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    @DtoField(value = "addressId", readOnly = true)
    private long addressId;

    @DtoField(value = "city", readOnly = true)
    private String city;

    @DtoField(value = "postcode", readOnly = true)
    private String postcode;

    @DtoField(value = "addrline1", readOnly = true)
    private String addrline1;

    @DtoField(value = "addrline2", readOnly = true)
    private String addrline2;

    @DtoField(value = "addressType", readOnly = true)
    private String addressType;

    @DtoField(value = "countryCode", readOnly = true)
    private String countryCode;
    private String countryName;
    private String countryLocalName;

    @DtoField(value = "stateCode", readOnly = true)
    private String stateCode;
    private String stateName;
    private String stateLocalName;

    @DtoField(value = "firstname", readOnly = true)
    private String firstname;

    @DtoField(value = "lastname", readOnly = true)
    private String lastname;

    @DtoField(value = "middlename", readOnly = true)
    private String middlename;

    @DtoField(value = "defaultAddress", readOnly = true)
    private boolean defaultAddress;

    @DtoField(value = "phoneList", readOnly = true)
    private String phoneList;

    @DtoField(value = "customer.customerId", readOnly = true)
    private long customerId;


    @XmlAttribute(name = "address-id")
    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(final long addressId) {
        this.addressId = addressId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(final String postcode) {
        this.postcode = postcode;
    }

    public String getAddrline1() {
        return addrline1;
    }

    public void setAddrline1(final String addrline1) {
        this.addrline1 = addrline1;
    }

    public String getAddrline2() {
        return addrline2;
    }

    public void setAddrline2(final String addrline2) {
        this.addrline2 = addrline2;
    }

    @XmlAttribute(name = "address-type")
    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(final String addressType) {
        this.addressType = addressType;
    }

    @XmlElement(name = "country-code")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    @XmlElement(name = "country-name")
    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(final String countryName) {
        this.countryName = countryName;
    }

    @XmlElement(name = "country-local-name")
    public String getCountryLocalName() {
        return countryLocalName;
    }

    public void setCountryLocalName(final String countryLocalName) {
        this.countryLocalName = countryLocalName;
    }

    @XmlElement(name = "state-code")
    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    @XmlElement(name = "state-name")
    public String getStateName() {
        return stateName;
    }

    public void setStateName(final String stateName) {
        this.stateName = stateName;
    }

    @XmlElement(name = "state-local-name")
    public String getStateLocalName() {
        return stateLocalName;
    }

    public void setStateLocalName(final String stateLocalName) {
        this.stateLocalName = stateLocalName;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(final String middlename) {
        this.middlename = middlename;
    }

    @XmlAttribute(name = "default-address")
    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(final boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    @XmlElement(name = "phone-list")
    public String getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(final String phoneList) {
        this.phoneList = phoneList;
    }

    @XmlAttribute(name = "customer-id")
    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final long customerId) {
        this.customerId = customerId;
    }
}
