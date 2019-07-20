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

package org.yes.cart.payment.dto.impl;

import org.yes.cart.payment.dto.PaymentAddress;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 13:24:24
 */
public class PaymentAddressImpl implements PaymentAddress {

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
    private String companyName1;
    private String companyName2;
    private String companyDepartment;
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


    /**
     * {@inheritDoc}
     */
    @Override
    public String getCity() {
        return city;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCity(final String city) {
        this.city = city;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPostcode() {
        return postcode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPostcode(final String postcode) {
        this.postcode = postcode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAddrline1() {
        return addrline1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddrline1(final String addrline1) {
        this.addrline1 = addrline1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAddrline2() {
        return addrline2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddrline2(final String addrline2) {
        this.addrline2 = addrline2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAddressType() {
        return addressType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddressType(final String addressType) {
        this.addressType = addressType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStateCode() {
        return stateCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPhone1() {
        return phone1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPhone1(final String phone1) {
        this.phone1 = phone1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPhone2() {
        return phone2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPhone2(final String phone2) {
        this.phone2 = phone2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMobile1() {
        return mobile1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMobile1(final String mobile1) {
        this.mobile1 = mobile1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMobile2() {
        return mobile2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMobile2(final String mobile2) {
        this.mobile2 = mobile2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFirstname() {
        return firstname;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLastname() {
        return lastname;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMiddlename() {
        return middlename;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMiddlename(final String middlename) {
        this.middlename = middlename;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSalutation() {
        return salutation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSalutation(final String salutation) {
        this.salutation = salutation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEmail1() {
        return email1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEmail1(final String email1) {
        this.email1 = email1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEmail2() {
        return email2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEmail2(final String email2) {
        this.email2 = email2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCompanyName1() {
        return companyName1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCompanyName1(final String companyName1) {
        this.companyName1 = companyName1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCompanyName2() {
        return companyName2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCompanyName2(final String companyName2) {
        this.companyName2 = companyName2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCompanyDepartment() {
        return companyDepartment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCompanyDepartment(final String companyDepartment) {
        this.companyDepartment = companyDepartment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustom0() {
        return custom0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustom0(final String custom0) {
        this.custom0 = custom0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustom1() {
        return custom1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustom1(final String custom1) {
        this.custom1 = custom1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustom2() {
        return custom2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustom2(final String custom2) {
        this.custom2 = custom2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustom3() {
        return custom3;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustom3(final String custom3) {
        this.custom3 = custom3;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustom4() {
        return custom4;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustom4(final String custom4) {
        this.custom4 = custom4;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustom5() {
        return custom5;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustom5(final String custom5) {
        this.custom5 = custom5;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustom6() {
        return custom6;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustom6(final String custom6) {
        this.custom6 = custom6;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustom7() {
        return custom7;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustom7(final String custom7) {
        this.custom7 = custom7;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustom8() {
        return custom8;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustom8(final String custom8) {
        this.custom8 = custom8;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustom9() {
        return custom9;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustom9(final String custom9) {
        this.custom9 = custom9;
    }

}
