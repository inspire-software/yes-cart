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
 * Time: 15:15
 */
@Dto
@XmlRootElement(name = "country")
public class CountryRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    @DtoField(value = "countryId", readOnly = true)
    private long countryId;

    @DtoField(value = "countryCode", readOnly = true)
    private String countryCode;

    @DtoField(value = "isoCode", readOnly = true)
    private String isoCode;

    @DtoField(value = "name", readOnly = true)
    private String name;

    @DtoField(value = "displayName", readOnly = true)
    private String displayName;

    @XmlAttribute(name = "country-id")
    public long getCountryId() {
        return countryId;
    }

    public void setCountryId(final long countryId) {
        this.countryId = countryId;
    }

    @XmlAttribute(name = "country-code")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    @XmlAttribute(name = "country-iso")
    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(final String isoCode) {
        this.isoCode = isoCode;
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
