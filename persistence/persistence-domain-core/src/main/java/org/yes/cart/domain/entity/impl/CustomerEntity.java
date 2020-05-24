/*
 * Copyright 2009 Inspire-Software.com
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


import org.apache.commons.lang.StringUtils;
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
    private String companyName1;
    private String companyName2;
    private String companyDepartment;

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
    public void setEmail(final String email) {
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
    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    @Override
    public String getLastname() {
        return this.lastname;
    }

    @Override
    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String getMiddlename() {
        return this.middlename;
    }

    @Override
    public void setMiddlename(final String middlename) {
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
    public void setPassword(final String password) {
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
    public void setTag(final String tag) {
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
    public String getCompanyName1() {
        return companyName1;
    }

    @Override
    public void setCompanyName1(final String companyName1) {
        this.companyName1 = companyName1;
    }

    @Override
    public String getCompanyName2() {
        return companyName2;
    }

    @Override
    public void setCompanyName2(final String companyName2) {
        this.companyName2 = companyName2;
    }

    @Override
    public String getCompanyDepartment() {
        return companyDepartment;
    }

    @Override
    public void setCompanyDepartment(final String companyDepartment) {
        this.companyDepartment = companyDepartment;
    }

    @Override
    public Collection<CustomerOrder> getOrders() {
        return this.orders;
    }

    @Override
    public void setOrders(final Collection<CustomerOrder> orders) {
        this.orders = orders;
    }

    @Override
    public Collection<CustomerWishList> getWishList() {
        return this.wishList;
    }

    @Override
    public void setWishList(final Collection<CustomerWishList> wishList) {
        this.wishList = wishList;
    }

    @Override
    public Collection<AttrValueCustomer> getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(final Collection<AttrValueCustomer> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Collection<Address> getAddress() {
        return this.address;
    }

    @Override
    public void setAddress(final Collection<Address> address) {
        this.address = address;
    }

    @Override
    public Collection<CustomerShop> getShops() {
        return this.shops;
    }

    @Override
    public void setShops(final Collection<CustomerShop> shops) {
        this.shops = shops;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(final String guid) {
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
    public void setCustomerId(final long customerId) {
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
    public Map<String, AttrValue> getAllAttributesAsMap() {
        final Map<String, AttrValue> rez = new HashMap<>();
        if (this.attributes != null) {
            for (AttrValue attrValue : this.attributes) {
                if (attrValue != null && attrValue.getAttributeCode() != null) {
                    rez.put(attrValue.getAttributeCode(), attrValue);
                }
            }
        }
        return rez;
    }

    @Override
    public AttrValueCustomer getAttributeByCode(final String attributeCode) {
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
    public String getAttributeValueByCode(final String attributeCode) {
        final AttrValue val = getAttributeByCode(attributeCode);
        return val != null ? val.getVal() : null;
    }


    @Override
    public boolean isAttributeValueByCodeTrue(final String attributeCode) {
        final AttrValue val = getAttributeByCode(attributeCode);
        return val != null && Boolean.valueOf(val.getVal());
    }



    @Override
    public Collection<AttrValue> getAllAttributes() {
        return new ArrayList<>(attributes);
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
    public String getName() {
        final StringBuilder name = new StringBuilder();
        if (StringUtils.isNotBlank(salutation)) {
            name.append(salutation);
        }
        if (StringUtils.isNotBlank(firstname)) {
            if (name.length() > 0) {
                name.append(' ');
            }
            name.append(firstname);
        }
        if (StringUtils.isNotBlank(middlename)) {
            if (name.length() > 0) {
                name.append(' ');
            }
            name.append(middlename);
        }
        if (StringUtils.isNotBlank(lastname)) {
            if (name.length() > 0) {
                name.append(' ');
            }
            name.append(lastname);
        }
        return name.toString();
    }

    @Override
    public void setName(final String name) {
        // do nothing
    }

    @Override
    public String getDescription() {
        return email;
    }

    @Override
    public void setDescription(final String description) {
        // do nothing
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


