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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.CustomerShopDTO;
import org.yes.cart.domain.vo.matcher.NoopMatcher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * User: denispavlov
 */

@Dto
public class VoCustomerInfo {

    @DtoField(value = "customerId", readOnly = true)
    private long customerId;

    @DtoField(value = "login")
    private String login;

    @DtoField(value = "email")
    private String email;

    @DtoField(value = "phone")
    private String phone;

    @DtoField(value = "guest")
    private boolean guest;

    @DtoField(value = "shop")
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
            value="assignedShops",
            dtoBeanKey="VoCustomerShopLink",
            entityGenericType = CustomerShopDTO.class,
            entityCollectionClass = HashSet.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private List<VoCustomerShopLink> customerShops = new ArrayList<>();


    @DtoField(readOnly = true)
    private Instant createdTimestamp;
    @DtoField(readOnly = true)
    private Instant updatedTimestamp;
    @DtoField(readOnly = true)
    private String createdBy;
    @DtoField(readOnly = true)
    private String updatedBy;

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final long customerId) {
        this.customerId = customerId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public boolean isGuest() {
        return guest;
    }

    public void setGuest(final boolean guest) {
        this.guest = guest;
    }

    public boolean isShop() {
        return shop;
    }

    public void setShop(final boolean shop) {
        this.shop = shop;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(final String salutation) {
        this.salutation = salutation;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(final String middlename) {
        this.middlename = middlename;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(final String customerType) {
        this.customerType = customerType;
    }

    public String getPricingPolicy() {
        return pricingPolicy;
    }

    public void setPricingPolicy(final String pricingPolicy) {
        this.pricingPolicy = pricingPolicy;
    }

    public String getCompanyName1() {
        return companyName1;
    }

    public void setCompanyName1(final String companyName1) {
        this.companyName1 = companyName1;
    }

    public String getCompanyName2() {
        return companyName2;
    }

    public void setCompanyName2(final String companyName2) {
        this.companyName2 = companyName2;
    }

    public String getCompanyDepartment() {
        return companyDepartment;
    }

    public void setCompanyDepartment(final String companyDepartment) {
        this.companyDepartment = companyDepartment;
    }

    public List<VoCustomerShopLink> getCustomerShops() {
        return customerShops;
    }

    public void setCustomerShops(final List<VoCustomerShopLink> customerShops) {
        this.customerShops = customerShops;
    }

    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
