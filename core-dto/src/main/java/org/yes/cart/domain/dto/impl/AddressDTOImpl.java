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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.AddressDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class AddressDTOImpl implements AddressDTO {

    private static final long serialVersionUID = 20100717L;

    @DtoField(value = "addressId", readOnly = true)
    private long addressId;

    @DtoField(value = "city")
    private String city;

    @DtoField(value = "postcode")
    private String postcode;

    @DtoField(value = "addrline1")
    private String addrline1;

    @DtoField(value = "addrline2")
    private String addrline2;

    @DtoField(value = "addressType")
    private String addressType;

    @DtoField(value = "countryCode")
    private String countryCode;

    @DtoField(value = "stateCode")
    private String stateCode;


    @DtoField(value = "salutation")
    private String salutation;

    @DtoField(value = "firstname")
    private String firstname;

    @DtoField(value = "lastname")
    private String lastname;

    @DtoField(value = "middlename")
    private String middlename;

    @DtoField(value = "defaultAddress", readOnly = true)
    private boolean defaultAddress;

    @DtoField(value = "phone1")
    private String phone1;

    @DtoField(value = "phone2")
    private String phone2;

    @DtoField(value = "mobile1")
    private String mobile1;

    @DtoField(value = "mobile2")
    private String mobile2;

    @DtoField(value = "email1")
    private String email1;

    @DtoField(value = "email2")
    private String email2;

    @DtoField(value = "custom0")
    private String custom0;

    @DtoField(value = "custom1")
    private String custom1;

    @DtoField(value = "custom2")
    private String custom2;

    @DtoField(value = "custom3")
    private String custom3;

    @DtoField(value = "custom4")
    private String custom4;

    @DtoField(value = "custom5")
    private String custom5;

    @DtoField(value = "custom6")
    private String custom6;

    @DtoField(value = "custom7")
    private String custom7;

    @DtoField(value = "custom8")
    private String custom8;

    @DtoField(value = "custom9")
    private String custom9;

    @DtoField(value = "customer.customerId", readOnly = true)
    private long customerId;


    /** {@inheritDoc} */
    public String getPhone1() {
        return phone1;
    }

    /** {@inheritDoc} */
    public void setPhone1(final String phone1) {
        this.phone1 = phone1;
    }

    /** {@inheritDoc} */
    public String getPhone2() {
        return phone2;
    }

    /** {@inheritDoc} */
    public void setPhone2(final String phone2) {
        this.phone2 = phone2;
    }

    /** {@inheritDoc} */
    public String getMobile1() {
        return mobile1;
    }

    /** {@inheritDoc} */
    public void setMobile1(final String mobile1) {
        this.mobile1 = mobile1;
    }

    public String getMobile2() {
        return mobile2;
    }

    public void setMobile2(final String mobile2) {
        this.mobile2 = mobile2;
    }

    /** {@inheritDoc} */
    public String getEmail1() {
        return email1;
    }

    /** {@inheritDoc} */
    public void setEmail1(final String email1) {
        this.email1 = email1;
    }

    /** {@inheritDoc} */
    public String getEmail2() {
        return email2;
    }

    /** {@inheritDoc} */
    public void setEmail2(final String email2) {
        this.email2 = email2;
    }

    /** {@inheritDoc} */
    public String getCustom0() {
        return custom0;
    }

    /** {@inheritDoc} */
    public void setCustom0(final String custom0) {
        this.custom0 = custom0;
    }

    /** {@inheritDoc} */
    public String getCustom1() {
        return custom1;
    }

    /** {@inheritDoc} */
    public void setCustom1(final String custom1) {
        this.custom1 = custom1;
    }

    /** {@inheritDoc} */
    public String getCustom2() {
        return custom2;
    }

    /** {@inheritDoc} */
    public void setCustom2(final String custom2) {
        this.custom2 = custom2;
    }

    /** {@inheritDoc} */
    public String getCustom3() {
        return custom3;
    }

    /** {@inheritDoc} */
    public void setCustom3(final String custom3) {
        this.custom3 = custom3;
    }

    /** {@inheritDoc} */
    public String getCustom4() {
        return custom4;
    }

    /** {@inheritDoc} */
    public void setCustom4(final String custom4) {
        this.custom4 = custom4;
    }

    /** {@inheritDoc} */
    public String getCustom5() {
        return custom5;
    }

    /** {@inheritDoc} */
    public void setCustom5(final String custom5) {
        this.custom5 = custom5;
    }

    /** {@inheritDoc} */
    public String getCustom6() {
        return custom6;
    }

    /** {@inheritDoc} */
    public void setCustom6(final String custom6) {
        this.custom6 = custom6;
    }

    /** {@inheritDoc} */
    public String getCustom7() {
        return custom7;
    }

    /** {@inheritDoc} */
    public void setCustom7(final String custom7) {
        this.custom7 = custom7;
    }

    /** {@inheritDoc} */
    public String getCustom8() {
        return custom8;
    }

    /** {@inheritDoc} */
    public void setCustom8(final String custom8) {
        this.custom8 = custom8;
    }

    /** {@inheritDoc} */
    public String getCustom9() {
        return custom9;
    }

    /** {@inheritDoc} */
    public void setCustom9(final String custom9) {
        this.custom9 = custom9;
    }

    /** {@inheritDoc} */
    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    /** {@inheritDoc} */
    public void setDefaultAddress(final boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    /** {@inheritDoc} */
    public String getStateCode() {
        return stateCode;
    }

    /** {@inheritDoc} */
    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    /** {@inheritDoc} */
    public long getCustomerId() {
        return customerId;
    }

    /**
     * Set customer ID.
     *
     * @param customerId PK
     */
    public void setCustomerId(final long customerId) {
        this.customerId = customerId;
    }

    /** {@inheritDoc} */
    public long getAddressId() {
        return addressId;
    }

    /** {@inheritDoc} */
    public long getId() {
        return addressId;
    }


    /** {@inheritDoc} */
    public void setAddressId(final long addressId) {
        this.addressId = addressId;
    }

    /** {@inheritDoc} */
    public String getCity() {
        return city;
    }

    /** {@inheritDoc} */
    public void setCity(final String city) {
        this.city = city;
    }

    /** {@inheritDoc} */
    public String getPostcode() {
        return postcode;
    }

    /** {@inheritDoc} */
    public void setPostcode(final String postcode) {
        this.postcode = postcode;
    }

    /** {@inheritDoc} */
    public String getAddrline1() {
        return addrline1;
    }

    /** {@inheritDoc} */
    public void setAddrline1(final String addrline1) {
        this.addrline1 = addrline1;
    }

    /** {@inheritDoc} */
    public String getAddrline2() {
        return addrline2;
    }

    /** {@inheritDoc} */
    public void setAddrline2(final String addrline2) {
        this.addrline2 = addrline2;
    }

    /** {@inheritDoc} */
    public String getAddressType() {
        return addressType;
    }

    /** {@inheritDoc} */
    public void setAddressType(final String addressType) {
        this.addressType = addressType;
    }

    /** {@inheritDoc} */
    public String getCountryCode() {
        return countryCode;
    }

    /** {@inheritDoc} */
    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    /** {@inheritDoc} */
    public String getFirstname() {
        return firstname;
    }

    /** {@inheritDoc} */
    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    /** {@inheritDoc} */
    public String getLastname() {
        return lastname;
    }

    /** {@inheritDoc} */
    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    /** {@inheritDoc} */
    public String getMiddlename() {
        return middlename;
    }

    /** {@inheritDoc} */
    public void setMiddlename(final String middlename) {
        this.middlename = middlename;
    }

    /** {@inheritDoc} */
    public String getSalutation() {
        return salutation;
    }

    /** {@inheritDoc} */
    public void setSalutation(final String salutation) {
        this.salutation = salutation;
    }

    @Override
    public String toString() {
        return "AddressDTOImpl{" +
                "addressId=" + addressId +
                ", city='" + city + '\'' +
                ", postcode='" + postcode + '\'' +
                ", addrline1='" + addrline1 + '\'' +
                ", addrline2='" + addrline2 + '\'' +
                ", addressType='" + addressType + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", stateCode='" + stateCode + '\'' +
                ", salutation='" + salutation + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", middlename='" + middlename + '\'' +
                ", defaultAddress=" + defaultAddress +
                ", phone1='" + phone1 + '\'' +
                ", customerId=" + customerId +
                '}';
    }
}
