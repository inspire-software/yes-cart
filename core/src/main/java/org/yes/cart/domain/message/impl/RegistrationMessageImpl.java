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

package org.yes.cart.domain.message.impl;


import org.yes.cart.domain.message.RegistrationMessage;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class RegistrationMessageImpl implements RegistrationMessage {

    private static final long serialVersionUID = 20100824L;

    private long   shopId = 0L;
    private String locale;
    private String shopCode;
    private String shopName;
    private String shopMailFrom;
    private Set<String> shopUrl;


    private String email;
    private String salutation;
    private String firstname;
    private String lastname;
    private String middlename;
    private String companyName1;
    private String companyName2;
    private String companyDepartment;
    private String password;
    private String authToken;
    private List<String> mailTemplatePathChain;
    private String templateName;

    private Map<String, Object> additionalData;


    /** {@inheritDoc} */
    @Override
    public String getTemplateName() {
        return templateName;
    }

    /** {@inheritDoc} */
    @Override
    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getMailTemplatePathChain() {
        return mailTemplatePathChain;
    }

    /** {@inheritDoc} */
    @Override
    public void setMailTemplatePathChain(final List<String> mailTemplatePathChain) {
        this.mailTemplatePathChain = mailTemplatePathChain;
    }

    /** {@inheritDoc} */
    @Override
    public String getShopMailFrom() {
        return shopMailFrom;
    }

    /** {@inheritDoc} */
    @Override
    public void setShopMailFrom(final String shopMailFrom) {
        this.shopMailFrom = shopMailFrom;
    }

    /** {@inheritDoc} */
    @Override
    public String getEmail() {
        return email;
    }

    /** {@inheritDoc} */
    @Override
    public void setEmail(final String email) {
        this.email = email;
    }

    /** {@inheritDoc} */
    @Override
    public long getShopId() {
        return shopId;
    }

    /** {@inheritDoc} */
    @Override
    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    /** {@inheritDoc} */
    @Override
    public String getPassword() {
        return password;
    }

    /** {@inheritDoc} */
    @Override
    public void setPassword(final String password) {
        this.password = password;
    }

    /** {@inheritDoc} */
    @Override
    public String getAuthToken() {
        return authToken;
    }

    /** {@inheritDoc} */
    @Override
    public void setAuthToken(final String authToken) {
        this.authToken = authToken;
    }

    /** {@inheritDoc} */
    @Override
    public String getLocale() {
        return locale;
    }

    /** {@inheritDoc} */
    @Override
    public void setLocale(final String locale) {
        this.locale = locale;
    }

    /** {@inheritDoc} */
    @Override
    public String getShopCode() {
        return shopCode;
    }

    /** {@inheritDoc} */
    @Override
    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    /** {@inheritDoc} */
    @Override
    public String getShopName() {
        return shopName;
    }

    /** {@inheritDoc} */
    @Override
    public void setShopName(final String shopName) {
        this.shopName = shopName;
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getShopUrl() {
        return shopUrl;
    }

    /** {@inheritDoc} */
    @Override
    public void setShopUrl(final Set<String> shopUrl) {
        this.shopUrl = shopUrl;
    }

    /** {@inheritDoc} */
    @Override
    public String getFirstname() {
        return firstname;
    }

    /** {@inheritDoc} */
    @Override
    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    /** {@inheritDoc} */
    @Override
    public String getLastname() {
        return lastname;
    }

    /** {@inheritDoc} */
    @Override
    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    /** {@inheritDoc} */
    @Override
    public String getSalutation() {
        return salutation;
    }

    /** {@inheritDoc} */
    @Override
    public void setSalutation(final String salutation) {
        this.salutation = salutation;
    }

    /** {@inheritDoc} */
    @Override
    public String getMiddlename() {
        return middlename;
    }

    /** {@inheritDoc} */
    @Override
    public void setMiddlename(final String middlename) {
        this.middlename = middlename;
    }

    /** {@inheritDoc} */
    @Override
    public String getCompanyName1() {
        return companyName1;
    }

    /** {@inheritDoc} */
    @Override
    public void setCompanyName1(final String companyName1) {
        this.companyName1 = companyName1;
    }

    /** {@inheritDoc} */
    @Override
    public String getCompanyName2() {
        return companyName2;
    }

    /** {@inheritDoc} */
    @Override
    public void setCompanyName2(final String companyName2) {
        this.companyName2 = companyName2;
    }

    /** {@inheritDoc} */
    @Override
    public String getCompanyDepartment() {
        return companyDepartment;
    }

    /** {@inheritDoc} */
    @Override
    public void setCompanyDepartment(final String companyDepartment) {
        this.companyDepartment = companyDepartment;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    /** {@inheritDoc} */
    @Override
    public void setAdditionalData(final Map<String, Object> additionalData) {
        this.additionalData = additionalData;
    }

    @Override
    public String toString() {
        return "RegistrationMessageImpl{" +
                "shopCode='" + shopCode + '\'' +
                ", email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", pathToTemplateFolder='" + mailTemplatePathChain + '\'' +
                ", templateName='" + templateName + '\'' +
                '}';
    }
}
