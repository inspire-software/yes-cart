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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.AttrValueCustomerDTO;
import org.yes.cart.domain.dto.CustomerDTO;
import org.yes.cart.domain.dto.matcher.impl.IdentifiableMatcher;
import org.yes.cart.domain.entity.AttrValueCustomer;

import java.util.HashSet;
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

    @DtoField(value = "email")
    private String email;

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
                ", attribute=" + attributes +
                '}';
    }
}
