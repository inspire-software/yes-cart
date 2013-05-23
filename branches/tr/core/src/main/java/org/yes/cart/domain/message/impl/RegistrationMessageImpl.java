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

package org.yes.cart.domain.message.impl;


import org.yes.cart.domain.message.RegistrationMessage;

import java.util.Set;

/**
 * {@inheritDoc}
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class RegistrationMessageImpl implements RegistrationMessage {

    private static final long serialVersionUID = 20100824L;

    private long    shopId = 0L;
    private String  shopCode;
    private String  shopName;
    private String  shopMailFrom;
    private Set<String> shopUrl;


    private String email;
    private String firstname;
    private String lastname;
    private String password;
    private String pathToTemplateFolder;
    private String templateName;


    /** {@inheritDoc} */
    public String getTemplateName() {
        return templateName;
    }

    /** {@inheritDoc} */
    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }

    /** {@inheritDoc} */
    public String getPathToTemplateFolder() {
        return pathToTemplateFolder;
    }

    /** {@inheritDoc} */
    public void setPathToTemplateFolder(final String pathToTemplateFolder) {
        this.pathToTemplateFolder = pathToTemplateFolder;
    }

    /** {@inheritDoc} */
    public String getShopMailFrom() {
        return shopMailFrom;
    }

    /** {@inheritDoc} */
    public void setShopMailFrom(final String shopMailFrom) {
        this.shopMailFrom = shopMailFrom;
    }

    /** {@inheritDoc} */
    public String getEmail() {
        return email;
    }

    /** {@inheritDoc} */
    public void setEmail(final String email) {
        this.email = email;
    }

    /** {@inheritDoc} */
    public long getShopId() {
        return shopId;
    }

    /** {@inheritDoc} */
    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    /** {@inheritDoc} */
    public String getPassword() {
        return password;
    }

    /** {@inheritDoc} */
    public void setPassword(final String password) {
        this.password = password;
    }

    /** {@inheritDoc} */
    public String getShopCode() {
        return shopCode;
    }

    /** {@inheritDoc} */
    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    /** {@inheritDoc} */
    public String getShopName() {
        return shopName;
    }

    /** {@inheritDoc} */
    public void setShopName(final String shopName) {
        this.shopName = shopName;
    }

    /** {@inheritDoc} */
    public Set<String> getShopUrl() {
        return shopUrl;
    }

    /** {@inheritDoc} */
    public void setShopUrl(final Set<String> shopUrl) {
        this.shopUrl = shopUrl;
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

    @Override
    public String toString() {
        return "RegistrationMessageImpl{" +
                "shopCode='" + shopCode + '\'' +
                ", email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", pathToTemplateFolder='" + pathToTemplateFolder + '\'' +
                ", templateName='" + templateName + '\'' +
                '}';
    }
}
