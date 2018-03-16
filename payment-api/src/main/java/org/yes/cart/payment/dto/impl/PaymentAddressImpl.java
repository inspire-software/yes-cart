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
    private String firstname;
    private String lastname;
    private String middlename;


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
}
