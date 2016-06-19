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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * User: denispavlov
 * Date: 21/08/2014
 * Time: 16:21
 */
@XmlRootElement(name = "register-form")
public class RegisterFormRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    private String email;
    private String customerType;
    private boolean customerTypeSupported;
    private List<AttrValueCustomerRO> custom;

    @XmlElementWrapper(name = "custom")
    @XmlElement(name = "attribute")
    public List<AttrValueCustomerRO> getCustom() {
        return custom;
    }

    public void setCustom(final List<AttrValueCustomerRO> custom) {
        this.custom = custom;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @XmlAttribute(name = "customer-type")
    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(final String customerType) {
        this.customerType = customerType;
    }

    @XmlAttribute(name = "customer-type-supported")
    public boolean isCustomerTypeSupported() {
        return customerTypeSupported;
    }

    public void setCustomerTypeSupported(final boolean customerTypeSupported) {
        this.customerTypeSupported = customerTypeSupported;
    }
}
