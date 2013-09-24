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


import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;
import org.yes.cart.domain.entity.AttributeGroup;
import org.yes.cart.domain.entity.Etype;

import java.util.Date;
import java.util.UUID;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
@Indexed(index = "luceneindex/attribute")
public class AttributeEntity implements org.yes.cart.domain.entity.Attribute, java.io.Serializable {


    private long attributeId;

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


    public boolean isMandatory() {
        return this.mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isAllowduplicate() {
        return this.allowduplicate;
    }

    public void setAllowduplicate(boolean allowduplicate) {
        this.allowduplicate = allowduplicate;
    }

    public boolean isAllowfailover() {
        return this.allowfailover;
    }

    public void setAllowfailover(boolean allowfailover) {
        this.allowfailover = allowfailover;
    }

    public String getVal() {
        return this.val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getRegexp() {
        return this.regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public String getValidationFailedMessage() {
        return this.validationFailedMessage;
    }

    public void setValidationFailedMessage(String validationFailedMessage) {
        this.validationFailedMessage = validationFailedMessage;
    }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getChoiceData() {
        return this.choiceData;
    }

    public void setChoiceData(String choiceData) {
        this.choiceData = choiceData;
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Etype getEtype() {
        return this.etype;
    }

    public void setEtype(Etype etype) {
        this.etype = etype;
    }

    public AttributeGroup getAttributeGroup() {
        return this.attributeGroup;
    }

    public void setAttributeGroup(AttributeGroup attributeGroup) {
        this.attributeGroup = attributeGroup;
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

    @DocumentId
    public long getAttributeId() {
        return this.attributeId;
    }

    public long getId() {
        return this.attributeId;
    }


    public void setAttributeId(long attributeId) {
        this.attributeId = attributeId;
    }

    private String code;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public  org.yes.cart.domain.entity.Attribute copy() throws CloneNotSupportedException {
        return this.clone();

    }

    protected AttributeEntity clone() throws CloneNotSupportedException {
        return new AttributeEntity(
                this.isMandatory(),
                this.isAllowduplicate(),
                this.isAllowfailover(),
                this.getVal(),
                this.getRegexp(),
                this.getValidationFailedMessage(),
                this.getRank(),
                this.getChoiceData(),
                this.getName(),
                this.getDisplayName(),
                this.getDescription(),
                this.getEtype(),
                this.getAttributeGroup(),
                this.getCreatedTimestamp(),
                this.getUpdatedTimestamp(),
                this.getCreatedBy(),
                this.getUpdatedBy(),
                UUID.randomUUID().toString(),
                this.getAttributeId(),
                this.getCode()
        );
    }


    public AttributeEntity(boolean mandatory, boolean allowduplicate, boolean allowfailover, String val,
                           String regexp, String validationFailedMessage, int rank, String choiceData, String name,
                           String displayName, String description, Etype etype, AttributeGroup attributeGroup,
                           Date createdTimestamp, Date updatedTimestamp, String createdBy, String updatedBy,
                           String guid, long attributeId, String code) {
        this.mandatory = mandatory;
        this.allowduplicate = allowduplicate;
        this.allowfailover = allowfailover;
        this.val = val;
        this.regexp = regexp;
        this.validationFailedMessage = validationFailedMessage;
        this.rank = rank;
        this.choiceData = choiceData;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.etype = etype;
        this.attributeGroup = attributeGroup;
        this.createdTimestamp = createdTimestamp;
        this.updatedTimestamp = updatedTimestamp;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.guid = guid;
        this.attributeId = attributeId;
        this.code = code;
    }

}


