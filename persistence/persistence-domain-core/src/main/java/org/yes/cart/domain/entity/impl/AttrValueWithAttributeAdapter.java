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
import org.yes.cart.domain.entity.AttrValueWithAttribute;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.i18n.I18NModel;

import java.time.Instant;

/**
 * User: denispavlov
 * Instant: 21/07/2017
 * Time: 10:33
 */
public class AttrValueWithAttributeAdapter implements AttrValueWithAttribute {

    private final AttrValue value;
    private Attribute attribute;

    public AttrValueWithAttributeAdapter(final AttrValue value, final Attribute attribute) {
        this.value = value;
        this.attribute = attribute;
        if (value.getAttributeCode() == null) {
            this.value.setAttributeCode(attribute.getCode());
        }
    }

    @Override
    public Attribute getAttribute() {
        return attribute;
    }

    @Override
    public void setAttribute(final Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public long getAttrvalueId() {
        return value.getAttrvalueId();
    }

    @Override
    public void setAttrvalueId(final long attrvalueId) {
        value.setAttrvalueId(attrvalueId);
    }

    @Override
    public String getVal() {
        return value.getVal();
    }

    @Override
    public void setVal(final String val) {
        value.setVal(val);
    }

    @Override
    public String getIndexedVal() {
        return value.getIndexedVal();
    }

    @Override
    public void setIndexedVal(final String val) {
        value.setIndexedVal(val);
    }

    @Override
    public I18NModel getDisplayVal() {
        return value.getDisplayVal();
    }

    @Override
    public void setDisplayVal(final I18NModel displayVal) {
        value.setDisplayVal(displayVal);
    }

    @Override
    public String getAttributeCode() {
        return value.getAttributeCode();
    }

    @Override
    public void setAttributeCode(final String attributeCode) {
        value.setAttributeCode(attributeCode);
    }

    @Override
    public Instant getCreatedTimestamp() {
        return value.getCreatedTimestamp();
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        value.setCreatedTimestamp(createdTimestamp);
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return value.getUpdatedTimestamp();
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        value.setUpdatedTimestamp(updatedTimestamp);
    }

    @Override
    public String getCreatedBy() {
        return value.getCreatedBy();
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        value.setCreatedBy(createdBy);
    }

    @Override
    public String getUpdatedBy() {
        return value.getUpdatedBy();
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        value.setUpdatedBy(updatedBy);
    }

    @Override
    public long getId() {
        return value.getId();
    }

    @Override
    public String getGuid() {
        return value.getGuid();
    }

    @Override
    public void setGuid(final String guid) {
        value.setGuid(guid);
    }

    @Override
    public long getVersion() {
        return value.getVersion();
    }
}
