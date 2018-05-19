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


import org.yes.cart.domain.entity.*;

import java.time.Instant;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class CustomerEntity implements org.yes.cart.domain.entity.Customer, java.io.Serializable {

    private long customerId;
    private long version;

    private String email;
    private String guestEmail;
    private boolean guest;
    private String salutation;
    private String firstname;
    private String lastname;
    private String middlename;
    private String password;
    private Instant passwordExpiry;
    private String authToken;
    private Instant authTokenExpiry;
    private String tag;
    private String publicKey;
    private String customerType;
    private String pricingPolicy;

    private Collection<CustomerOrder> orders = new ArrayList<>(0);
    private Collection<CustomerWishList> wishList = new ArrayList<>(0);
    private Collection<AttrValueCustomer> attributes = new ArrayList<>(0);
    private Collection<Address> address = new ArrayList<>(0);
    private Collection<CustomerShop> shops = new ArrayList<>(0);
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public CustomerEntity() {
    }



    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getGuestEmail() {
        return guestEmail;
    }

    @Override
    public void setGuestEmail(final String guestEmail) {
        this.guestEmail = guestEmail;
    }

    @Override
    public boolean isGuest() {
        return guest;
    }

    @Override
    public void setGuest(final boolean guest) {
        this.guest = guest;
    }

    @Override
    public String getContactEmail() {
        if (isGuest()) {
            return getGuestEmail();
        }
        return getEmail();
    }

    @Override
    public String getFirstname() {
        return this.firstname;
    }

    @Override
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Override
    public String getLastname() {
        return this.lastname;
    }

    @Override
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String getMiddlename() {
        return this.middlename;
    }

    @Override
    public void setMiddlename(String middlename) {
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
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Instant getPasswordExpiry() {
        return passwordExpiry;
    }

    @Override
    public void setPasswordExpiry(final Instant passwordExpiry) {
        this.passwordExpiry = passwordExpiry;
    }

    @Override
    public String getAuthToken() {
        return authToken;
    }

    @Override
    public void setAuthToken(final String authToken) {
        this.authToken = authToken;
    }

    @Override
    public Instant getAuthTokenExpiry() {
        return authTokenExpiry;
    }

    @Override
    public void setAuthTokenExpiry(final Instant authTokenExpiry) {
        this.authTokenExpiry = authTokenExpiry;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String getPublicKey() {
        return publicKey;
    }

    @Override
    public void setPublicKey(final String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public String getCustomerType() {
        return customerType;
    }

    @Override
    public void setCustomerType(final String customerType) {
        this.customerType = customerType;
    }

    @Override
    public String getPricingPolicy() {
        return pricingPolicy;
    }

    @Override
    public void setPricingPolicy(final String pricingPolicy) {
        this.pricingPolicy = pricingPolicy;
    }

    @Override
    public Collection<CustomerOrder> getOrders() {
        return this.orders;
    }

    @Override
    public void setOrders(Collection<CustomerOrder> orders) {
        this.orders = orders;
    }

    @Override
    public Collection<CustomerWishList> getWishList() {
        return this.wishList;
    }

    @Override
    public void setWishList(Collection<CustomerWishList> wishList) {
        this.wishList = wishList;
    }

    @Override
    public Collection<AttrValueCustomer> getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(Collection<AttrValueCustomer> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Collection<Address> getAddress() {
        return this.address;
    }

    @Override
    public void setAddress(Collection<Address> address) {
        this.address = address;
    }

    @Override
    public Collection<CustomerShop> getShops() {
        return this.shops;
    }

    @Override
    public void setShops(Collection<CustomerShop> shops) {
        this.shops = shops;
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
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public long getCustomerId() {
        return this.customerId;
    }

    @Override
    public long getId() {
        return this.customerId;
    }

    @Override
    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    @Override
    public Set<AttrValueCustomer> getAttributesByCode(final String attributeCode) {
        if (attributeCode == null) {
            return null;
        }
        final Set<AttrValueCustomer> result = new HashSet<>();
        if (this.attributes != null) {
            for (AttrValueCustomer attrValue : this.attributes) {
                if (attributeCode.equals(attrValue.getAttributeCode())) {
                    result.add(attrValue);
                }
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    @Override
    public AttrValueCustomer getAttributeByCode(String attributeCode) {
        if (attributeCode == null) {
            return null;
        }
        if (this.attributes != null) {
            for (AttrValueCustomer attrValue : this.attributes) {
                if (attributeCode.equals(attrValue.getAttributeCode())) {
                    return attrValue;
                }
            }
        }
        return null;
    }

    @Override
    public List<Address> getAddresses(final String addressType) {
        final List<Address> rez = new ArrayList<>();
        for (Address addr : address) {
            if (addressType.equals(addr.getAddressType())) {
                rez.add(addr);
            }
        }
        return rez;
    }

    @Override
    public Address getDefaultAddress(final String addressType) {
        for (Address addr : address) {
            if (addressType.equals(addr.getAddressType()) && addr.isDefaultAddress()) {
                return addr;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "CustomerEntity{" +
                "email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", middlename='" + middlename + '\'' +
                ", guid='" + guid + '\'' +
                ", customerId=" + customerId +
                '}';
    }

}


