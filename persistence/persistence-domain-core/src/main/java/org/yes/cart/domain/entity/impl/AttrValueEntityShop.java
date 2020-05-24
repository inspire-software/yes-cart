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


import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.time.Instant;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class AttrValueEntityShop implements org.yes.cart.domain.entity.AttrValueShop, java.io.Serializable {

    private long attrvalueId;
    private long version;

    private Shop shop;
    private String val;
    private String indexedVal;
    private String displayValInternal;
    private I18NModel displayVal;
    private String attributeCode;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public AttrValueEntityShop() {
    }

    @Override
    public Shop getShop() {
        return this.shop;
    }

    @Override
    public void setShop(final Shop shop) {
        this.shop = shop;
    }

    @Override
    public String getVal() {
        return this.val;
    }

    @Override
    public void setVal(final String val) {
        this.val = val;
    }

    @Override
    public String getIndexedVal() {
        return indexedVal;
    }

    @Override
    public void setIndexedVal(final String indexedVal) {
        this.indexedVal = indexedVal;
    }

    public String getDisplayValInternal() {
        return displayValInternal;
    }

    public void setDisplayValInternal(final String displayValInternal) {
        this.displayValInternal = displayValInternal;
        this.displayVal = new StringI18NModel(displayValInternal);
    }

    @Override
    public I18NModel getDisplayVal() {
        return this.displayVal;
    }

    @Override
    public void setDisplayVal(final I18NModel displayVal) {
        this.displayVal = displayVal;
        this.displayValInternal = displayVal != null ? displayVal.toString() : null;
    }

    @Override
    public String getAttributeCode() {
        return attributeCode;
    }

    @Override
    public void setAttributeCode(final String attributeCode) {
        this.attributeCode = attributeCode;
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
    public long getAttrvalueId() {
        return this.attrvalueId;
    }

    @Override
    public long getId() {
        return this.attrvalueId;
    }


    @Override
    public void setAttrvalueId(final long attrvalueId) {
        this.attrvalueId = attrvalueId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


