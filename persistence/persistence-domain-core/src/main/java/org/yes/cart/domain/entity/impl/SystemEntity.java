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
package org.yes.cart.domain.entity.impl;


import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueSystem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class SystemEntity implements org.yes.cart.domain.entity.System, java.io.Serializable {

    private long systemId;
    private long version;

    private String code;
    private String name;
    private String description;
    private Map<String, AttrValueSystem> attributes = new HashMap<>(0);
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public SystemEntity() {
    }



    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public Map<String, AttrValueSystem> getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(final Map<String, AttrValueSystem> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }

    @Override
    public long getSystemId() {
        return this.systemId;
    }

    @Override
    public long getId() {
        return this.systemId;
    }

    @Override
    public void setSystemId(final long systemId) {
        this.systemId = systemId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }


    @Override
    public Collection<AttrValueSystem> getAttributesByCode(final String attributeCode) {
        final Collection<AttrValueSystem> result = new ArrayList<>();
        if (attributeCode != null && this.attributes != null) {
            for (AttrValueSystem attrValue : this.attributes.values()) {
                if (attributeCode.equals(attrValue.getAttributeCode())) {
                    result.add(attrValue);
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, AttrValue> getAllAttributesAsMap() {
        final Map<String, AttrValue> rez = new HashMap<>();
        if (this.attributes != null) {
            for (AttrValue attrValue : this.attributes.values()) {
                if (attrValue != null && attrValue.getAttributeCode() != null) {
                    rez.put(attrValue.getAttributeCode(), attrValue);
                }
            }
        }
        return rez;
    }

    @Override
    public AttrValueSystem getAttributeByCode(final String attributeCode) {
        if (attributeCode != null) {
            if (this.attributes != null) {
                for (AttrValueSystem attrValue : this.attributes.values()) {
                    if (attributeCode.equals(attrValue.getAttributeCode())) {
                        return attrValue;
                    }
                }
            }
        }
        return null;
    }



    @Override
    public String getAttributeValueByCode(final String attributeCode) {
        final AttrValue val = getAttributeByCode(attributeCode);
        return val != null ? val.getVal() : null;
    }


    @Override
    public boolean isAttributeValueByCodeTrue(final String attributeCode) {
        final AttrValue val = getAttributeByCode(attributeCode);
        return val != null && Boolean.valueOf(val.getVal());
    }



    @Override
    public Collection<AttrValue> getAllAttributes() {
        return new ArrayList<>(attributes.values());
    }

}


