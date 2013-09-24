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


import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.System;

import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class AttrValueEntitySystem implements org.yes.cart.domain.entity.AttrValueSystem, java.io.Serializable {

    private long attrvalueId;

    private String val;
    private String displayVal;
    private Attribute attribute;
    private System system;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public AttrValueEntitySystem() {
    }




    public String getVal() {
        return this.val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getDisplayVal() {
        return this.displayVal;
    }

    public void setDisplayVal(String displayVal) {
        this.displayVal = displayVal;
    }

    public Attribute getAttribute() {
        return this.attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public System getSystem() {
        return this.system;
    }

    public void setSystem(System system) {
        this.system = system;
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

    public long getAttrvalueId() {
        return this.attrvalueId;
    }

    public long getId() {
        return this.attrvalueId;
    }


    public void setAttrvalueId(long attrvalueId) {
        this.attrvalueId = attrvalueId;
    }

}


