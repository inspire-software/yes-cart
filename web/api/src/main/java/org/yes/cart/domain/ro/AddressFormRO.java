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

package org.yes.cart.domain.ro;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * Date: 13/06/2020
 */
@XmlRootElement(name = "address-form")
public class AddressFormRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    private String addressType;
    private List<AttrValueAndAttributeRO> custom;

    @XmlElementWrapper(name = "custom")
    @XmlElement(name = "attribute")
    public List<AttrValueAndAttributeRO> getCustom() {
        return custom;
    }

    public void setCustom(final List<AttrValueAndAttributeRO> custom) {
        this.custom = custom;
    }

    @XmlAttribute(name = "address-type")
    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(final String addressType) {
        this.addressType = addressType;
    }

}
