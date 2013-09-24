/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class ProdTypeAttributeViewGroupEntity implements org.yes.cart.domain.entity.ProdTypeAttributeViewGroup, java.io.Serializable {

    private long prodTypeAttributeViewGroupId;

    private ProductType producttype;
    private String attrCodeList;
    private int rank;
    private String name;
    private String displayName;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ProdTypeAttributeViewGroupEntity() {
    }


    public ProductType getProducttype() {
        return this.producttype;
    }

    public void setProducttype(ProductType producttype) {
        this.producttype = producttype;
    }

    public String getAttrCodeList() {
        return this.attrCodeList;
    }

    public void setAttrCodeList(String attrCodeList) {
        this.attrCodeList = attrCodeList;
    }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public long getProdTypeAttributeViewGroupId() {
        return this.prodTypeAttributeViewGroupId;
    }

    public long getId() {
        return this.prodTypeAttributeViewGroupId;
    }

    public void setProdTypeAttributeViewGroupId(long prodTypeAttributeViewGroupId) {
        this.prodTypeAttributeViewGroupId = prodTypeAttributeViewGroupId;
    }

}


