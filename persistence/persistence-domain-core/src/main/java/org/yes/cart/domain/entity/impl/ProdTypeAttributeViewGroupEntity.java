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


import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.time.Instant;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class ProdTypeAttributeViewGroupEntity implements org.yes.cart.domain.entity.ProdTypeAttributeViewGroup, java.io.Serializable {

    private long prodTypeAttributeViewGroupId;
    private long version;

    private ProductType producttype;
    private String attrCodeList;
    private int rank;
    private String name;
    private String displayNameInternal;
    private I18NModel displayName;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ProdTypeAttributeViewGroupEntity() {
    }


    @Override
    public ProductType getProducttype() {
        return this.producttype;
    }

    @Override
    public void setProducttype(final ProductType producttype) {
        this.producttype = producttype;
    }

    @Override
    public String getAttrCodeList() {
        return this.attrCodeList;
    }

    @Override
    public void setAttrCodeList(final String attrCodeList) {
        this.attrCodeList = attrCodeList;
    }

    @Override
    public int getRank() {
        return this.rank;
    }

    @Override
    public void setRank(final int rank) {
        this.rank = rank;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    public String getDisplayNameInternal() {
        return displayNameInternal;
    }

    public void setDisplayNameInternal(final String displayNameInternal) {
        this.displayNameInternal = displayNameInternal;
        this.displayName = new StringI18NModel(displayNameInternal);
    }

    @Override
    public I18NModel getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(final I18NModel displayName) {
        this.displayName = displayName;
        this.displayNameInternal = displayName != null ? displayName.toString() : null;
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
    public long getProdTypeAttributeViewGroupId() {
        return this.prodTypeAttributeViewGroupId;
    }

    @Override
    public long getId() {
        return this.prodTypeAttributeViewGroupId;
    }

    @Override
    public void setProdTypeAttributeViewGroupId(final long prodTypeAttributeViewGroupId) {
        this.prodTypeAttributeViewGroupId = prodTypeAttributeViewGroupId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


