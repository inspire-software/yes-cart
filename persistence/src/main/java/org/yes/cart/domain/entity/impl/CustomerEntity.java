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
package org.yes.cart.domain.entity.impl;


import org.yes.cart.domain.entity.*;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class CustomerEntity implements org.yes.cart.domain.entity.Customer, java.io.Serializable {

    private long customerId;

    private String email;
    private String firstname;
    private String lastname;
    private String middlename;
    private String password;
    private Collection<CustomerOrder> orders = new ArrayList<CustomerOrder>(0);
    private Collection<CustomerWishList> wishList = new ArrayList<CustomerWishList>(0);
    private Collection<AttrValueCustomer> attributes = new ArrayList<AttrValueCustomer>(0);
    private Collection<Address> address = new ArrayList<Address>(0);
    private Collection<CustomerShop> shops = new ArrayList<CustomerShop>(0);
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public CustomerEntity() {
    }



    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return this.middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<CustomerOrder> getOrders() {
        return this.orders;
    }

    public void setOrders(Collection<CustomerOrder> orders) {
        this.orders = orders;
    }

    public Collection<CustomerWishList> getWishList() {
        return this.wishList;
    }

    public void setWishList(Collection<CustomerWishList> wishList) {
        this.wishList = wishList;
    }

    public Collection<AttrValueCustomer> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Collection<AttrValueCustomer> attributes) {
        this.attributes = attributes;
    }

    public Collection<Address> getAddress() {
        return this.address;
    }

    public void setAddress(Collection<Address> address) {
        this.address = address;
    }

    public Collection<CustomerShop> getShops() {
        return this.shops;
    }

    public void setShops(Collection<CustomerShop> shops) {
        this.shops = shops;
    }

    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public long getCustomerId() {
        return this.customerId;
    }

    public long getId() {
        return this.customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public Set<AttrValueCustomer> getAttributesByCode(final String attributeCode) {
        if (attributeCode == null) {
            return null;
        }
        final Set<AttrValueCustomer> result = new HashSet<AttrValueCustomer>();
        if (this.attributes != null) {
            for (AttrValueCustomer attrValue : this.attributes) {
                if (attrValue.getAttribute().getCode().equals(attributeCode)) {
                    result.add(attrValue);
                }
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    public AttrValueCustomer getAttributeByCode(String attributeCode) {
        if (attributeCode == null) {
            return null;
        }
        if (this.attributes != null) {
            for (AttrValueCustomer attrValue : this.attributes) {
                if (attrValue.getAttribute().getCode().equals(attributeCode)) {
                    return attrValue;
                }
            }
        }
        return null;
    }

    public List<Address> getAddresses(final String addressType) {
        final List<Address> rez = new ArrayList<Address>();
        for (Address addr : address) {
            if (addressType.equals(addr.getAddressType())) {
                rez.add(addr);
            }
        }
        return rez;
    }

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


