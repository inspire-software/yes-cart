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

import javax.persistence.*;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
@Entity
@Table(name = "TCUSTOMER"
)
public class CustomerEntity implements org.yes.cart.domain.entity.Customer, java.io.Serializable {


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



    @Column(name = "EMAIL", unique = true, nullable = false)
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "FIRSTNAME", nullable = false, length = 128)
    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Column(name = "LASTNAME", nullable = false, length = 128)
    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Column(name = "MIDDLENAME", length = 128)
    public String getMiddlename() {
        return this.middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    @Column(name = "PASSWORD", nullable = false)
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    public Collection<CustomerOrder> getOrders() {
        return this.orders;
    }

    public void setOrders(Collection<CustomerOrder> orders) {
        this.orders = orders;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false, updatable = false)
    public Collection<CustomerWishList> getWishList() {
        return this.wishList;
    }

    public void setWishList(Collection<CustomerWishList> wishList) {
        this.wishList = wishList;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "customer")
    public Collection<AttrValueCustomer> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Collection<AttrValueCustomer> attributes) {
        this.attributes = attributes;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "customer")
    public Collection<Address> getAddress() {
        return this.address;
    }

    public void setAddress(Collection<Address> address) {
        this.address = address;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "customer")
    public Collection<CustomerShop> getShops() {
        return this.shops;
    }

    public void setShops(Collection<CustomerShop> shops) {
        this.shops = shops;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_TIMESTAMP")
    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_TIMESTAMP")
    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Column(name = "CREATED_BY", length = 64)
    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "UPDATED_BY", length = 64)
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "GUID", unique = true, nullable = false, length = 36)
    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }


    // The following is extra code specified in the hbm.xml files


    private long customerId;


    //@GenericGenerator(name="generator", strategy="native", parameters={@Parameter(name="column", value="value"), @Parameter(name="table", value="HIBERNATE_UNIQUE_KEYS")})

    @Id
    @GeneratedValue
    /*(generator="generator")*/
    @Column(name = "CUSTOMER_ID", nullable = false)
    public long getCustomerId() {
        return this.customerId;
    }


    @Transient
    public long getId() {
        return this.customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    @Transient
    public Set<AttrValueCustomer> getAttributesByCode(final String attributeCode) {
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

    @Transient
    public AttrValueCustomer getAttributeByCode(String attributeCode) {
        if (this.attributes != null) {
            for (AttrValueCustomer attrValue : this.attributes) {
                if (attrValue.getAttribute().getCode().equals(attributeCode)) {
                    return attrValue;
                }
            }
        }
        return null;
    }

    @Transient
    public List<Address> getAddresses(final String addressType) {
        final List<Address> rez = new ArrayList<Address>();
        for (Address addr : address) {
            if (addressType.equals(addr.getAddressType())) {
                rez.add(addr);
            }
        }
        return rez;
    }

    @Transient
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


    // end of extra code specified in the hbm.xml files

}


