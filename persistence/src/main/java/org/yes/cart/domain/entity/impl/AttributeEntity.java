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


import org.yes.cart.domain.entity.AttributeGroup;
import org.yes.cart.domain.entity.Etype;

import java.time.Instant;
import java.util.UUID;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class AttributeEntity implements org.yes.cart.domain.entity.Attribute, java.io.Serializable {

    private long attributeId;
    private long version;

    private String code;
    private boolean mandatory;
    private boolean secure;
    private boolean allowduplicate;
    private boolean allowfailover;
    private boolean store;
    private boolean search;
    private boolean primary;
    private boolean navigation;
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
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public AttributeEntity() {
    }


    @Override
    public boolean isMandatory() {
        return this.mandatory;
    }

    @Override
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public void setSecure(final boolean secure) {
        this.secure = secure;
    }

    @Override
    public boolean isAllowduplicate() {
        return this.allowduplicate;
    }

    @Override
    public void setAllowduplicate(boolean allowduplicate) {
        this.allowduplicate = allowduplicate;
    }

    @Override
    public boolean isAllowfailover() {
        return this.allowfailover;
    }

    @Override
    public void setAllowfailover(boolean allowfailover) {
        this.allowfailover = allowfailover;
    }

    @Override
    public String getVal() {
        return this.val;
    }

    @Override
    public void setVal(String val) {
        this.val = val;
    }

    @Override
    public String getRegexp() {
        return this.regexp;
    }

    @Override
    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    @Override
    public String getValidationFailedMessage() {
        return this.validationFailedMessage;
    }

    @Override
    public void setValidationFailedMessage(String validationFailedMessage) {
        this.validationFailedMessage = validationFailedMessage;
    }

    @Override
    public int getRank() {
        return this.rank;
    }

    @Override
    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String getChoiceData() {
        return this.choiceData;
    }

    @Override
    public void setChoiceData(String choiceData) {
        this.choiceData = choiceData;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Etype getEtype() {
        return this.etype;
    }

    @Override
    public void setEtype(Etype etype) {
        this.etype = etype;
    }

    @Override
    public AttributeGroup getAttributeGroup() {
        return this.attributeGroup;
    }

    @Override
    public void setAttributeGroup(AttributeGroup attributeGroup) {
        this.attributeGroup = attributeGroup;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public long getAttributeId() {
        return this.attributeId;
    }

    @Override
    public long getId() {
        return this.attributeId;
    }


    @Override
    public void setAttributeId(long attributeId) {
        this.attributeId = attributeId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean isStore() {
        return store;
    }

    @Override
    public void setStore(final boolean store) {
        this.store = store;
    }

    @Override
    public boolean isSearch() {
        return search;
    }

    @Override
    public void setSearch(final boolean search) {
        this.search = search;
    }

    @Override
    public boolean isPrimary() {
        return primary;
    }

    @Override
    public void setPrimary(final boolean primary) {
        this.primary = primary;
    }

    @Override
    public boolean isNavigation() {
        return navigation;
    }

    @Override
    public void setNavigation(final boolean navigation) {
        this.navigation = navigation;
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

    @Override
    public  org.yes.cart.domain.entity.Attribute copy() throws CloneNotSupportedException {
        return this.clone();

    }

    @Override
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
                this.getCode(),
                this.isStore(),
                this.isSearch(),
                this.isPrimary(),
                this.isNavigation()
        );
    }


    public AttributeEntity(boolean mandatory, boolean allowduplicate, boolean allowfailover, String val,
                           String regexp, String validationFailedMessage, int rank, String choiceData, String name,
                           String displayName, String description, Etype etype, AttributeGroup attributeGroup,
                           Instant createdTimestamp, Instant updatedTimestamp, String createdBy, String updatedBy,
                           String guid, long attributeId, String code, boolean store, boolean search, boolean primary, boolean navigation) {
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
        this.store = store;
        this.search = search;
        this.primary = primary;
        this.navigation = navigation;
    }

}


