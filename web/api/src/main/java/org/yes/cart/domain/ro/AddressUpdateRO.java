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

import org.yes.cart.domain.ro.xml.impl.StringMapAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Map;

/**
 * Date: 13/06/2020
 */
@XmlRootElement(name = "address-update")
public class AddressUpdateRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    private Long addressId;
    private String addressName;
    private String addressType;
    private Map<String, String> custom;

    @XmlJavaTypeAdapter(StringMapAdapter.class)
    @XmlElement(name = "custom")
    public Map<String, String> getCustom() {
        return custom;
    }

    public void setCustom(final Map<String, String> custom) {
        this.custom = custom;
    }

    @XmlAttribute(name = "address-id")
    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(final Long addressId) {
        this.addressId = addressId;
    }

    @XmlAttribute(name = "address-type")
    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(final String addressType) {
        this.addressType = addressType;
    }

    @XmlAttribute(name = "address-name")
    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(final String addressName) {
        this.addressName = addressName;
    }
}
