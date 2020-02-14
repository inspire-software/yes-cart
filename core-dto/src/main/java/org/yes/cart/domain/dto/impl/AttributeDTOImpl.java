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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.AttributeDTO;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class AttributeDTOImpl implements AttributeDTO {

    private static final long serialVersionUID = 20100717L;

    @DtoField(value = "attributeId", readOnly = true)
    private long attributeId;
    

    @DtoField(value = "code")
    private String code;

    @DtoField(value = "secure")
    private boolean secure;

    @DtoField(value = "mandatory")
    private boolean mandatory;

    @DtoField(value = "val")
    private String val;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "etype")
    private String etype;


    @DtoField(value = "attributeGroup")
    private String attributegroup;

    @DtoField(value = "allowduplicate")
    private boolean allowduplicate;

    @DtoField(value = "allowfailover")
    private boolean allowfailover;


    @DtoField(value = "regexp")
    private String regexp;

    @DtoField(value = "validationFailedMessage", converter = "i18nModelConverter")
    private Map<String, String> validationFailedMessage;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "choiceData", converter = "i18nModelConverter")
    private Map<String, String> choiceData;

    @DtoField(value = "displayName", converter = "i18nModelConverter")
    private Map<String, String> displayNames;

    @DtoField(value = "store")
    private boolean store;

    @DtoField(value = "search")
    private boolean search;

    @DtoField(value = "primary")
    private boolean primary;

    @DtoField(value = "navigation")
    private boolean navigation;


    /** {@inheritDoc} */
    @Override
    public int getRank() {
        return rank;
    }

    /** {@inheritDoc} */
    @Override
    public void setRank(final int rank) {
        this.rank = rank;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getChoiceData() {
        return choiceData;
    }

    /** {@inheritDoc} */
    @Override
    public void setChoiceData(final Map<String, String> choiceData) {
        this.choiceData = choiceData;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getValidationFailedMessage() {
        return validationFailedMessage;
    }

    /** {@inheritDoc} */
    @Override
    public void setValidationFailedMessage(final Map<String, String> validationFailedMessage) {
        this.validationFailedMessage = validationFailedMessage;
    }

    /** {@inheritDoc} */
    @Override
    public String getAttributegroup() {
        return attributegroup;
    }

    /** {@inheritDoc} */
    @Override
    public void setAttributegroup(final String attributegroup) {
        this.attributegroup = attributegroup;
    }

    /** {@inheritDoc} */
    @Override
    public long getAttributeId() {
        return this.attributeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return attributeId;
    }

    /** {@inheritDoc} */
    @Override
    public void setAttributeId(final long attributeId) {
        this.attributeId = attributeId;
    }

    /** {@inheritDoc} */
    @Override
    public String getCode() {
        return code;
    }

    /** {@inheritDoc} */
    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSecure() {
        return secure;
    }

    /** {@inheritDoc} */
    @Override
    public void setSecure(final boolean secure) {
        this.secure = secure;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMandatory() {
        return mandatory;
    }

    /** {@inheritDoc} */
    @Override
    public void setMandatory(final boolean mandatory) {
        this.mandatory = mandatory;
    }

    /** {@inheritDoc} */
    @Override
    public String getVal() {
        return val;
    }

    /** {@inheritDoc} */
    @Override
    public void setVal(final String val) {
        this.val = val;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    /** {@inheritDoc} */
    @Override
    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public String getEtype() {
        return etype;
    }

    /** {@inheritDoc} */
    public void setEtype(final String etype) {
        this.etype = etype;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAllowduplicate() {
        return allowduplicate;
    }

    /** {@inheritDoc} */
    @Override
    public void setAllowduplicate(final boolean allowduplicate) {
        this.allowduplicate = allowduplicate;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAllowfailover() {
        return allowfailover;
    }

    /** {@inheritDoc} */
    @Override
    public void setAllowfailover(final boolean allowfailover) {
        this.allowfailover = allowfailover;
    }

    /** {@inheritDoc} */
    @Override
    public String getRegexp() {
        return regexp;
    }

    /** {@inheritDoc} */
    @Override
    public void setRegexp(final String regexp) {
        this.regexp = regexp;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isStore() {
        return store;
    }

    /** {@inheritDoc} */
    @Override
    public void setStore(final boolean store) {
        this.store = store;
    }

    @Override
    public boolean isSearch() {
        return search;
    }

    /** {@inheritDoc} */
    @Override
    public void setSearch(final boolean search) {
        this.search = search;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPrimary() {
        return primary;
    }

    /** {@inheritDoc} */
    @Override
    public void setPrimary(final boolean primary) {
        this.primary = primary;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isNavigation() {
        return navigation;
    }

    /** {@inheritDoc} */
    @Override
    public void setNavigation(final boolean navigation) {
        this.navigation = navigation;
    }

    @Override
    public String toString() {
        return "AttributeDTOImpl{" +
                "attributeId=" + attributeId +
                ", code='" + code + '\'' +
                ", mandatory=" + mandatory +
                ", val='" + val + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", etype=" + etype +
                ", attributegroup=" + attributegroup +
                ", allowduplicate=" + allowduplicate +
                ", allowfailover=" + allowfailover +
                ", regexp='" + regexp + '\'' +
                ", validationFailedMessage='" + validationFailedMessage + '\'' +
                ", rank=" + rank +
                ", choiceData='" + choiceData + '\'' +
                '}';
    }
}
