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

package org.yes.cart.domain.entity.impl;


import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueBrand;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class BrandEntity implements org.yes.cart.domain.entity.Brand, java.io.Serializable {

    private long brandId;
    private long version;

    private String name;
    private String description;
    private Collection<AttrValueBrand> attributes = new ArrayList<AttrValueBrand>(0);
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public BrandEntity() {
    }



    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<AttrValueBrand> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Collection<AttrValueBrand> attributes) {
        this.attributes = attributes;
    }

    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public long getBrandId() {
        return this.brandId;
    }

    public long getId() {
        return this.brandId;
    }

    public void setBrandId(long brandId) {
        this.brandId = brandId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    public Collection<AttrValue> getAllAttributes() {
        return new ArrayList<AttrValue>(attributes);
    }

    public Map<String, AttrValue> getAllAttributesAsMap() {
        final Map<String, AttrValue> rez = new HashMap<String, AttrValue>();
        for (AttrValue attrValue : getAllAttributes()) {
            if (attrValue != null && attrValue.getAttribute() != null) {
                rez.put(attrValue.getAttribute().getCode(), attrValue);
            }
        }
        return rez;
    }

    public Collection getAttributesByCode(final String attributeCode) {
        final Collection<AttrValueBrand> result = new ArrayList<AttrValueBrand>();
        if (attributeCode != null && this.attributes != null) {
            for (AttrValueBrand attrValue : this.attributes) {
                if (attrValue.getAttribute().getCode().equals(attributeCode)) {
                    result.add(attrValue);
                }
            }
        }
        return result;
    }

    public AttrValue getAttributeByCode(final String attributeCode) {
        if (attributeCode == null) {
            return null;
        }
        if (this.attributes != null) {
            for (AttrValueBrand attrValue : this.attributes) {
                if (attrValue.getAttribute() != null && attributeCode.equals(attrValue.getAttribute().getCode())) {
                    return attrValue;
                }
            }
        }
        return null;
    }


    public String getAttributeValueByCode(final String attributeCode) {
        final AttrValue val = getAttributeByCode(attributeCode);
        return val != null ? val.getVal() : null;
    }

    public boolean isAttributeValueByCodeTrue(final String attributeCode) {
        final AttrValue val = getAttributeByCode(attributeCode);
        return val != null && Boolean.valueOf(val.getVal());
    }



}


