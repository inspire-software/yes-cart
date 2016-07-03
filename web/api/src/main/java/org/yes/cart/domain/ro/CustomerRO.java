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

package org.yes.cart.domain.ro;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.matcher.impl.NoopMatcher;
import org.yes.cart.domain.entity.AttrValueCustomer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 30/03/2015
 * Time: 07:54
 */
@Dto
@XmlRootElement(name = "customer")
public class CustomerRO implements Serializable {

    private static final long serialVersionUID = 20150301L;


    @DtoField(value = "customerId", readOnly = true)
    private long customerId;

    @DtoField(value = "customerType", readOnly = true)
    private String customerType;

    @DtoField(value = "pricingPolicy", readOnly = true)
    private String pricingPolicy;

    @DtoField(value = "email")
    private String email;

    @DtoField(value = "salutation", readOnly = true)
    private String salutation;

    @DtoField(value = "firstname")
    private String firstname;

    @DtoField(value = "lastname")
    private String lastname;

    @DtoField(value = "middlename")
    private String middlename;

    @DtoField(value = "tag")
    private String tag;

//    @DtoCollection(
//            value="attributes",
//            dtoBeanKey="org.yes.cart.domain.ro.AttrValueCustomerRO",
//            entityGenericType = AttrValueCustomer.class,
//            entityCollectionClass = HashSet.class,
//            dtoCollectionClass = ArrayList.class,
//            dtoToEntityMatcher = NoopMatcher.class,
//            readOnly = true
//    )
    // NOT ALL customer attributes must be visible
    private List<AttrValueCustomerRO> attributes;

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final long customerId) {
        this.customerId = customerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @XmlElement(name = "customer-type")
    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(final String customerType) {
        this.customerType = customerType;
    }

    @XmlElement(name = "pricing-policy")
    public String getPricingPolicy() {
        return pricingPolicy;
    }

    public void setPricingPolicy(final String pricingPolicy) {
        this.pricingPolicy = pricingPolicy;
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

    @XmlElementWrapper(name = "attribute-values")
    @XmlElement(name = "attribute-value")
    public List<AttrValueCustomerRO> getAttributes() {
        return attributes;
    }

    public void setAttributes(final List<AttrValueCustomerRO> attributes) {
        this.attributes = attributes;
    }
}
