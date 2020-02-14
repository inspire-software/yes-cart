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


import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

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
    private String validationFailedMessageInternal;
    private I18NModel validationFailedMessage;
    private int rank;
    private String choiceDataInternal;
    private I18NModel choiceData;
    private String name;
    private String displayNameInternal;
    private I18NModel displayName;
    private String description;
    private String etype;
    private String attributeGroup;
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
    public void setMandatory(final boolean mandatory) {
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
    public void setAllowduplicate(final boolean allowduplicate) {
        this.allowduplicate = allowduplicate;
    }

    @Override
    public boolean isAllowfailover() {
        return this.allowfailover;
    }

    @Override
    public void setAllowfailover(final boolean allowfailover) {
        this.allowfailover = allowfailover;
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
    public String getRegexp() {
        return this.regexp;
    }

    @Override
    public void setRegexp(final String regexp) {
        this.regexp = regexp;
    }

    public String getValidationFailedMessageInternal() {
        return validationFailedMessageInternal;
    }

    public void setValidationFailedMessageInternal(final String validationFailedMessageInternal) {
        this.validationFailedMessageInternal = validationFailedMessageInternal;
        this.validationFailedMessage = new StringI18NModel(validationFailedMessageInternal);
    }

    @Override
    public I18NModel getValidationFailedMessage() {
        return this.validationFailedMessage;
    }

    @Override
    public void setValidationFailedMessage(final I18NModel validationFailedMessage) {
        this.validationFailedMessage = validationFailedMessage;
        this.validationFailedMessageInternal = validationFailedMessage != null ? validationFailedMessage.toString() : null;
    }

    @Override
    public int getRank() {
        return this.rank;
    }

    @Override
    public void setRank(final int rank) {
        this.rank = rank;
    }

    public String getChoiceDataInternal() {
        return choiceDataInternal;
    }

    public void setChoiceDataInternal(final String choiceDataInternal) {
        this.choiceDataInternal = choiceDataInternal;
        this.choiceData = new StringI18NModel(choiceDataInternal);
    }

    @Override
    public I18NModel getChoiceData() {
        return this.choiceData;
    }

    @Override
    public void setChoiceData(final I18NModel choiceData) {
        this.choiceData = choiceData;
        this.choiceDataInternal = choiceData != null ? choiceData.toString() : null;
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
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public String getEtype() {
        return this.etype;
    }

    @Override
    public void setEtype(final String etype) {
        this.etype = etype;
    }

    @Override
    public String getAttributeGroup() {
        return this.attributeGroup;
    }

    @Override
    public void setAttributeGroup(final String attributeGroup) {
        this.attributeGroup = attributeGroup;
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
    public long getAttributeId() {
        return this.attributeId;
    }

    @Override
    public long getId() {
        return this.attributeId;
    }


    @Override
    public void setAttributeId(final long attributeId) {
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
    public void setCode(final String code) {
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
                this.getValidationFailedMessageInternal(),
                this.getRank(),
                this.getChoiceDataInternal(),
                this.getName(),
                this.getDisplayNameInternal(),
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


    public AttributeEntity(final boolean mandatory,
                           final boolean allowduplicate,
                           final boolean allowfailover,
                           final String val,
                           final String regexp,
                           final String validationFailedMessage,
                           final int rank,
                           final String choiceData,
                           final String name,
                           final String displayName,
                           final String description,
                           final String etype,
                           final String attributeGroup,
                           final Instant createdTimestamp,
                           final Instant updatedTimestamp,
                           final String createdBy,
                           final String updatedBy,
                           final String guid,
                           final long attributeId,
                           final String code,
                           final boolean store,
                           final boolean search,
                           final boolean primary,
                           final boolean navigation) {
        this.mandatory = mandatory;
        this.allowduplicate = allowduplicate;
        this.allowfailover = allowfailover;
        this.val = val;
        this.regexp = regexp;
        this.validationFailedMessageInternal = validationFailedMessage;
        this.validationFailedMessage = new StringI18NModel(validationFailedMessage);
        this.rank = rank;
        this.choiceDataInternal = choiceData;
        this.choiceData = new StringI18NModel(choiceData);
        this.name = name;
        this.displayNameInternal = displayName;
        this.displayName = new StringI18NModel(displayName);
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


