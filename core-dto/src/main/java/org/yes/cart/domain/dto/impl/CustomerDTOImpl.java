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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.AttrValueCustomerDTO;
import org.yes.cart.domain.dto.CustomerDTO;
import org.yes.cart.domain.dto.CustomerShopDTO;
import org.yes.cart.domain.dto.matcher.impl.IdentifiableMatcher;
import org.yes.cart.domain.dto.matcher.impl.NoopMatcher;
import org.yes.cart.domain.entity.AttrValueCustomer;
import org.yes.cart.domain.entity.CustomerShop;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */

@Dto
public class CustomerDTOImpl implements CustomerDTO {

    @DtoField(value = "customerId", readOnly = true)
    private long customerId;

    @DtoField(value = "login")
    private String login;

    @DtoField(value = "email")
    private String email;

    @DtoField(value = "phone")
    private String phone;

    @DtoField(value = "guest", readOnly = true)
    private boolean guest;

    @DtoField(value = "shop", readOnly = true)
    private boolean shop;

    @DtoField(value = "salutation")
    private String salutation;

    @DtoField(value = "firstname")
    private String firstname;

    @DtoField(value = "lastname")
    private String lastname;

    @DtoField(value = "middlename")
    private String middlename;

    @DtoField(value = "tag")
    private String tag;

    @DtoField(value = "customerType")
    private String customerType;

    @DtoField(value = "pricingPolicy")
    private String pricingPolicy;

    @DtoField(value = "companyName1")
    private String companyName1;

    @DtoField(value = "companyName2")
    private String companyName2;

    @DtoField(value = "companyDepartment")
    private String companyDepartment;

    @DtoCollection(
            value="shops",
            dtoBeanKey="org.yes.cart.domain.dto.CustomerShopDTO",
            entityGenericType = CustomerShop.class,
            entityCollectionClass = HashSet.class,
            dtoCollectionClass = HashSet.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private Set<CustomerShopDTO> assignedShops;

    @DtoCollection(
            value="attributes",
            dtoBeanKey="org.yes.cart.domain.dto.AttrValueCustomerDTO",
            entityGenericType = AttrValueCustomer.class,
            entityCollectionClass = HashSet.class,
            dtoCollectionClass = HashSet.class,
            dtoToEntityMatcher = IdentifiableMatcher.class,
            readOnly = true
            )
    private Set<AttrValueCustomerDTO> attributes;


    @DtoField(readOnly = true)
    private Instant createdTimestamp;
    @DtoField(readOnly = true)
    private Instant updatedTimestamp;
    @DtoField(readOnly = true)
    private String createdBy;
    @DtoField(readOnly = true)
    private String updatedBy;


    /** {@inheritDoc} */
    @Override
    public long getCustomerId() {
        return customerId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return customerId;
    }

    /** {@inheritDoc} */
    @Override
    public void setCustomerId(final long customerId) {
        this.customerId = customerId;
    }

    /** {@inheritDoc} */
    @Override
    public String getLogin() {
        return login;
    }

    /** {@inheritDoc} */
    @Override
    public void setLogin(final String login) {
        this.login = login;
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
    public String getPhone() {
        return phone;
    }

    /** {@inheritDoc} */
    @Override
    public void setPhone(final String phone) {
        this.phone = phone;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isGuest() {
        return guest;
    }

    /** {@inheritDoc} */
    @Override
    public void setGuest(final boolean guest) {
        this.guest = guest;
    }

    @Override
    public boolean isShop() {
        return shop;
    }

    @Override
    public void setShop(final boolean shop) {
        this.shop = shop;
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
    public Set<AttrValueCustomerDTO> getAttributes() {
        return attributes;
    }

    /** {@inheritDoc} */
    @Override
    public void setAttributes(final Set<AttrValueCustomerDTO> attributes) {
        this.attributes = attributes;
    }

    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return tag;
    }

    /** {@inheritDoc} */
    @Override
    public void setTag(final String tag) {
        this.tag = tag;
    }

    /** {@inheritDoc} */
    @Override
    public String getCustomerType() {
        return customerType;
    }

    /** {@inheritDoc} */
    @Override
    public void setCustomerType(final String customerType) {
        this.customerType = customerType;
    }

    /** {@inheritDoc} */
    @Override
    public String getPricingPolicy() {
        return pricingPolicy;
    }

    /** {@inheritDoc} */
    @Override
    public void setPricingPolicy(final String pricingPolicy) {
        this.pricingPolicy = pricingPolicy;
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
    public Set<CustomerShopDTO> getAssignedShops() {
        return assignedShops;
    }

    /** {@inheritDoc} */
    @Override
    public void setAssignedShops(final Set<CustomerShopDTO> assignedShops) {
        this.assignedShops = assignedShops;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    /** {@inheritDoc} */
    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "CustomerDTOImpl{" +
                "customerId=" + customerId +
                ", email='" + email + '\'' +
                ", salutation='" + salutation + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", middlename='" + middlename + '\'' +
                ", tag='" + tag + '\'' +
                ", customerType='" + customerType + '\'' +
                ", pricingPolicy='" + pricingPolicy + '\'' +
                ", companyName1='" + companyName1 + '\'' +
                ", companyName2='" + companyName2 + '\'' +
                ", companyDepartment='" + companyDepartment + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
