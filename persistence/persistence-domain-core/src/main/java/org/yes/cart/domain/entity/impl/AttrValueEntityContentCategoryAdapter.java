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

import org.yes.cart.domain.entity.AttrValueCategory;
import org.yes.cart.domain.entity.AttrValueContent;
import org.yes.cart.domain.entity.Content;
import org.yes.cart.domain.i18n.I18NModel;

import java.time.Instant;

/**
 * User: denispavlov
 * Date: 20/04/2019
 * Time: 19:28
 */
public class AttrValueEntityContentCategoryAdapter implements AttrValueContent {

    private final AttrValueCategory attrValueCategory;

    public AttrValueEntityContentCategoryAdapter(final AttrValueCategory attrValueCategory) {
        this.attrValueCategory = attrValueCategory;
    }

    public AttrValueCategory getAttrValueCategory() {
        return attrValueCategory;
    }

    @Override
    public Content getContent() {
        if (attrValueCategory.getCategory() != null) {
            return new ContentCategoryAdapter(attrValueCategory.getCategory());
        }
        return null;
    }

    @Override
    public void setContent(final Content content) {
        if (content instanceof ContentCategoryAdapter) {
            this.attrValueCategory.setCategory(((ContentCategoryAdapter) content).getCategory());
        } else {
            throw new UnsupportedOperationException("This type of AV only supports ContentCategoryAdapter as content");
        }
    }

    @Override
    public long getAttrvalueId() {
        return attrValueCategory.getAttrvalueId();
    }

    @Override
    public void setAttrvalueId(final long attrvalueId) {
        attrValueCategory.setAttrvalueId(attrvalueId);
    }

    @Override
    public String getVal() {
        return attrValueCategory.getVal();
    }

    @Override
    public void setVal(final String val) {
        attrValueCategory.setVal(val);
    }

    @Override
    public String getIndexedVal() {
        return attrValueCategory.getIndexedVal();
    }

    @Override
    public void setIndexedVal(final String val) {
        attrValueCategory.setIndexedVal(val);
    }

    @Override
    public I18NModel getDisplayVal() {
        return attrValueCategory.getDisplayVal();
    }

    @Override
    public void setDisplayVal(final I18NModel displayVal) {
        attrValueCategory.setDisplayVal(displayVal);
    }

    @Override
    public String getAttributeCode() {
        return attrValueCategory.getAttributeCode();
    }

    @Override
    public void setAttributeCode(final String attributeCode) {
        attrValueCategory.setAttributeCode(attributeCode);
    }

    @Override
    public Instant getCreatedTimestamp() {
        return attrValueCategory.getCreatedTimestamp();
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        attrValueCategory.setCreatedTimestamp(createdTimestamp);
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return attrValueCategory.getUpdatedTimestamp();
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        attrValueCategory.setUpdatedTimestamp(updatedTimestamp);
    }

    @Override
    public String getCreatedBy() {
        return attrValueCategory.getCreatedBy();
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        attrValueCategory.setCreatedBy(createdBy);
    }

    @Override
    public String getUpdatedBy() {
        return attrValueCategory.getUpdatedBy();
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        attrValueCategory.setUpdatedBy(updatedBy);
    }

    @Override
    public long getId() {
        return attrValueCategory.getId();
    }

    @Override
    public String getGuid() {
        return attrValueCategory.getGuid();
    }

    @Override
    public void setGuid(final String guid) {
        attrValueCategory.setGuid(guid);
    }

    @Override
    public long getVersion() {
        return attrValueCategory.getVersion();
    }
}
