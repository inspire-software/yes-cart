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

package org.yes.cart.service.order.impl;

import org.yes.cart.domain.entity.*;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 18/05/2018
 * Time: 20:11
 */
public class CustomerNameAdapter implements Customer {

    private final CustomerOrder customerOrder;

    public CustomerNameAdapter(final CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    @Override
    public long getCustomerId() {
        return 0;
    }

    @Override
    public void setCustomerId(final long customerId) {

    }

    @Override
    public String getPublicKey() {
        return null;
    }

    @Override
    public void setPublicKey(final String publicKey) {

    }

    @Override
    public String getCustomerType() {
        return null;
    }

    @Override
    public void setCustomerType(final String customerType) {

    }

    @Override
    public String getPricingPolicy() {
        return null;
    }

    @Override
    public void setPricingPolicy(final String pricingPolicy) {

    }

    @Override
    public boolean isGuest() {
        return false;
    }

    @Override
    public void setGuest(final boolean guest) {

    }

    @Override
    public String getGuestEmail() {
        return null;
    }

    @Override
    public void setGuestEmail(final String email) {

    }

    @Override
    public String getContactEmail() {
        return customerOrder.getEmail();
    }

    @Override
    public Collection<CustomerOrder> getOrders() {
        return null;
    }

    @Override
    public void setOrders(final Collection<CustomerOrder> orders) {

    }

    @Override
    public Collection<CustomerWishList> getWishList() {
        return null;
    }

    @Override
    public void setWishList(final Collection<CustomerWishList> wishList) {

    }

    @Override
    public Collection<AttrValueCustomer> getAttributes() {
        return null;
    }

    @Override
    public Collection<AttrValueCustomer> getAttributesByCode(final String attributeCode) {
        return null;
    }

    @Override
    public AttrValueCustomer getAttributeByCode(final String attributeCode) {
        return null;
    }

    @Override
    public List<Address> getAddresses(final String addressType) {
        return null;
    }

    @Override
    public Address getDefaultAddress(final String addressType) {
        return null;
    }

    @Override
    public void setAttributes(final Collection<AttrValueCustomer> attribute) {

    }

    @Override
    public Collection<Address> getAddress() {
        return null;
    }

    @Override
    public void setAddress(final Collection<Address> address) {

    }

    @Override
    public Collection<CustomerShop> getShops() {
        return null;
    }

    @Override
    public void setShops(final Collection<CustomerShop> shops) {

    }

    @Override
    public Instant getCreatedTimestamp() {
        return null;
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {

    }

    @Override
    public Instant getUpdatedTimestamp() {
        return null;
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {

    }

    @Override
    public String getCreatedBy() {
        return null;
    }

    @Override
    public void setCreatedBy(final String createdBy) {

    }

    @Override
    public String getUpdatedBy() {
        return null;
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {

    }

    @Override
    public String getGuid() {
        return null;
    }

    @Override
    public void setGuid(final String guid) {

    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public String getEmail() {
        return customerOrder.getEmail();
    }

    @Override
    public void setEmail(final String email) {

    }

    @Override
    public String getFirstname() {
        return customerOrder.getFirstname();
    }

    @Override
    public void setFirstname(final String firstname) {

    }

    @Override
    public String getLastname() {
        return customerOrder.getLastname();
    }

    @Override
    public void setLastname(final String lastname) {

    }

    @Override
    public String getMiddlename() {
        return customerOrder.getMiddlename();
    }

    @Override
    public void setMiddlename(final String middlename) {

    }

    @Override
    public String getSalutation() {
        return customerOrder.getSalutation();
    }

    @Override
    public void setSalutation(final String salutation) {

    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public void setPassword(final String password) {

    }

    @Override
    public Instant getPasswordExpiry() {
        return null;
    }

    @Override
    public void setPasswordExpiry(final Instant passwordExpiry) {

    }

    @Override
    public String getAuthToken() {
        return null;
    }

    @Override
    public void setAuthToken(final String authToken) {

    }

    @Override
    public Instant getAuthTokenExpiry() {
        return null;
    }

    @Override
    public void setAuthTokenExpiry(final Instant authTokenExpiry) {

    }

    @Override
    public String getTag() {
        return null;
    }

    @Override
    public void setTag(final String tag) {

    }

    @Override
    public String getCompanyName1() {
        return null;
    }

    @Override
    public void setCompanyName1(final String companyName1) {

    }

    @Override
    public String getCompanyName2() {
        return null;
    }

    @Override
    public void setCompanyName2(final String companyName2) {

    }

    @Override
    public String getCompanyDepartment() {
        return null;
    }

    @Override
    public void setCompanyDepartment(final String companyDepartment) {

    }

    @Override
    public Collection<AttrValue> getAllAttributes() {
        return null;
    }

    @Override
    public Map<String, AttrValue> getAllAttributesAsMap() {
        return null;
    }

    @Override
    public String getAttributeValueByCode(final String attributeCode) {
        return null;
    }

    @Override
    public boolean isAttributeValueByCodeTrue(final String attributeCode) {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(final String name) {

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void setDescription(final String description) {

    }

    @Override
    public long getVersion() {
        return 0;
    }
}
