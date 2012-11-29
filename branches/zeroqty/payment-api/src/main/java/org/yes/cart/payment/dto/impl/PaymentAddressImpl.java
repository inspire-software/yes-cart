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
    private String phoneList;
    private String firstname;
    private String lastname;
    private String middlename;


    /**
     * {@inheritDoc}
     */
    public String getCity() {
        return city;
    }

    /**
     * {@inheritDoc}
     */
    public void setCity(final String city) {
        this.city = city;
    }

    /**
     * {@inheritDoc}
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * {@inheritDoc}
     */
    public void setPostcode(final String postcode) {
        this.postcode = postcode;
    }

    /**
     * {@inheritDoc}
     */
    public String getAddrline1() {
        return addrline1;
    }

    /**
     * {@inheritDoc}
     */
    public void setAddrline1(final String addrline1) {
        this.addrline1 = addrline1;
    }

    /**
     * {@inheritDoc}
     */
    public String getAddrline2() {
        return addrline2;
    }

    /**
     * {@inheritDoc}
     */
    public void setAddrline2(final String addrline2) {
        this.addrline2 = addrline2;
    }

    /**
     * {@inheritDoc}
     */
    public String getAddressType() {
        return addressType;
    }

    /**
     * {@inheritDoc}
     */
    public void setAddressType(final String addressType) {
        this.addressType = addressType;
    }

    /**
     * {@inheritDoc}
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * {@inheritDoc}
     */
    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * {@inheritDoc}
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * {@inheritDoc}
     */
    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    /**
     * {@inheritDoc}
     */
    public String getPhoneList() {
        return phoneList;
    }

    /**
     * {@inheritDoc}
     */
    public void setPhoneList(final String phoneList) {
        this.phoneList = phoneList;
    }

    /**
     * {@inheritDoc}
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * {@inheritDoc}
     */
    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    /**
     * {@inheritDoc}
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * {@inheritDoc}
     */
    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    /**
     * {@inheritDoc}
     */
    public String getMiddlename() {
        return middlename;
    }

    /**
     * {@inheritDoc}
     */
    public void setMiddlename(final String middlename) {
        this.middlename = middlename;
    }
}
