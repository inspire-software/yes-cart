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
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * User: denispavlov
 * Date: 30/03/2015
 * Time: 15:17
 */
@Dto
@XmlRootElement(name = "state")
public class StateRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    @DtoField(value = "stateId", readOnly = true)
    private long stateId;

    @DtoField(value = "countryCode", readOnly = true)
    private String countryCode;

    @DtoField(value = "stateCode", readOnly = true)
    private String stateCode;

    @DtoField(value = "name", readOnly = true)
    private String name;

    @DtoField(value = "displayName", readOnly = true)
    private String displayName;

    @XmlAttribute(name = "state-id")
    public long getStateId() {
        return stateId;
    }

    public void setStateId(final long stateId) {
        this.stateId = stateId;
    }

    @XmlAttribute(name = "country-code")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    @XmlAttribute(name = "state-code")
    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @XmlElement(name = "display-name")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
}
