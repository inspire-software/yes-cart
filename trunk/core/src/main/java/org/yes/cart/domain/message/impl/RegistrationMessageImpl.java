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
    private boolean newPerson;

    /** {@inheritDoc} */
    public String getShopMailFrom() {
        return shopMailFrom;
    }

    /** {@inheritDoc} */
    public void setShopMailFrom(final String shopMailFrom) {
        this.shopMailFrom = shopMailFrom;
    }

    /** {@inheritDoc} */
    public boolean isNewPerson() {
        return newPerson;
    }

    /** {@inheritDoc} */
    public void setNewPerson(final boolean newPerson) {
        this.newPerson = newPerson;
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
                "shopId=" + shopId +
                ", shopCode='" + shopCode + '\'' +
                ", shopName='" + shopName + '\'' +
                ", shopMailFrom='" + shopMailFrom + '\'' +
                ", shopUrl=" + shopUrl +
                ", email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", newPerson=" + newPerson +
                '}';
    }
}
