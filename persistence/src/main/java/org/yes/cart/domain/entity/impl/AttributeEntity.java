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


import org.hibernate.search.annotations.Indexed;
import org.yes.cart.domain.entity.AttributeGroup;
import org.yes.cart.domain.entity.Etype;

import javax.persistence.*;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
@Indexed(index = "luceneindex/attribute")
@Entity
@Table(name = "TATTRIBUTE"
)
public class AttributeEntity implements org.yes.cart.domain.entity.Attribute, java.io.Serializable {


    private boolean mandatory;
    private boolean allowduplicate;
    private boolean allowfailover;
    private String val;
    private String regexp;
    private String validationFailedMessage;
    private int rank;
    private String choiceData;
    private String name;
    private String displayName;
    private String description;
    private Etype etype;
    private AttributeGroup attributeGroup;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public AttributeEntity() {
    }


    @Column(name = "MANDATORY", nullable = false, length = 1)
    public boolean isMandatory() {
        return this.mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    @Column(name = "ALLOWDUPLICATE", nullable = false, length = 1)
    public boolean isAllowduplicate() {
        return this.allowduplicate;
    }

    public void setAllowduplicate(boolean allowduplicate) {
        this.allowduplicate = allowduplicate;
    }

    @Column(name = "ALLOWFAILOVER", nullable = false, length = 1)
    public boolean isAllowfailover() {
        return this.allowfailover;
    }

    public void setAllowfailover(boolean allowfailover) {
        this.allowfailover = allowfailover;
    }

    @Column(name = "VAL", length = 4000)
    public String getVal() {
        return this.val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    @Column(name = "REXP", length = 4000)
    public String getRegexp() {
        return this.regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    @Column(name = "V_FAILED_MSG", length = 4000)
    public String getValidationFailedMessage() {
        return this.validationFailedMessage;
    }

    public void setValidationFailedMessage(String validationFailedMessage) {
        this.validationFailedMessage = validationFailedMessage;
    }

    @Column(name = "RANK")
    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Column(name = "CHOICES", length = 4000)
    public String getChoiceData() {
        return this.choiceData;
    }

    public void setChoiceData(String choiceData) {
        this.choiceData = choiceData;
    }

    @Column(name = "NAME", nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DISPLAYNAME", length = 4000)
    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Column(name = "DESCRIPTION", length = 4000)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ETYPE_ID", nullable = false)
    public Etype getEtype() {
        return this.etype;
    }

    public void setEtype(Etype etype) {
        this.etype = etype;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTRIBUTEGROUP_ID", nullable = false)
    public AttributeGroup getAttributeGroup() {
        return this.attributeGroup;
    }

    public void setAttributeGroup(AttributeGroup attributeGroup) {
        this.attributeGroup = attributeGroup;
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


    private long attributeId;

    //@GenericGenerator(name="generator", strategy="native", parameters={@Parameter(name="column", value="value"), @Parameter(name="table", value="HIBERNATE_UNIQUE_KEYS")})

    @Id
    @GeneratedValue
    /*(generator="generator")*/
    @Column(name = "ATTRIBUTE_ID", nullable = false)
    public long getAttributeId() {
        return this.attributeId;
    }

    @Transient
    public long getId() {
        return this.attributeId;
    }


    public void setAttributeId(long attributeId) {
        this.attributeId = attributeId;
    }

    private String code;

    @Column(name = "CODE", length = 255, nullable = false)
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributeEntity that = (AttributeEntity) o;
        if (attributeId != that.attributeId) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return (int) (attributeId ^ (attributeId >>> 32));
    }


    // end of extra code specified in the hbm.xml files

}


