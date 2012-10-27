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
import org.yes.cart.domain.entity.Category;

import javax.persistence.*;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
@Entity
@Table(name = "TCATEGORYATTRVALUE"
)
public class AttrValueEntityCategory implements org.yes.cart.domain.entity.AttrValueCategory, java.io.Serializable {


    private String val;
    private String displayVal;
    private Category category;
    private Attribute attribute;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public AttrValueEntityCategory() {
    }



    @Column(name = "VAL", length = 4000)
    public String getVal() {
        return this.val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    @Column(name = "DISPLAYVAL", length = 4000)
    public String getDisplayVal() {
        return this.displayVal;
    }

    public void setDisplayVal(String displayVal) {
        this.displayVal = displayVal;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CODE", nullable = false)
    public Attribute getAttribute() {
        return this.attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_TIMESTAMP")
    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_TIMESTAMP")
    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Column(name = "CREATED_BY", length = 64)
    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "UPDATED_BY", length = 64)
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "GUID", unique = true, nullable = false, length = 36)
    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }


    // The following is extra code specified in the hbm.xml files


    private long attrvalueId;

    //@GenericGenerator(name="generator", strategy="native", parameters={@Parameter(name="column", value="value"),     @Parameter(name="table", value="HIBERNATE_UNIQUE_KEYS")})

    @Id
    @GeneratedValue
    /*(generator="generator")*/
    @Column(name = "ATTRVALUE_ID", nullable = false)
    public long getAttrvalueId() {
        return this.attrvalueId;
    }

    @Transient
    public long getId() {
        return this.attrvalueId;
    }


    public void setAttrvalueId(long attrvalueId) {
        this.attrvalueId = attrvalueId;
    }


    // end of extra code specified in the hbm.xml files

}


