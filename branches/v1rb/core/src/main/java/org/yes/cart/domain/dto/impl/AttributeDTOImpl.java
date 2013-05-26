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

    @DtoField(value = "mandatory")
    private boolean mandatory;

    @DtoField(value = "val")
    private String val;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "etype.etypeId", readOnly=true)
    private long etypeId;

    @DtoField(value = "etype.businesstype", readOnly=true)
    private String etypeName;


    @DtoField(value = "attributeGroup.attributegroupId", readOnly=true)
    private long attributegroupId;

    @DtoField(value = "allowduplicate")
    private boolean allowduplicate;

    @DtoField(value = "allowfailover")
    private boolean allowfailover;


    @DtoField(value = "regexp")
    private String regexp;

    @DtoField(value = "validationFailedMessage")
    private String validationFailedMessage;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "choiceData")
    private String choiceData;

    @DtoField(value = "displayName", converter = "i18nStringConverter")
    private Map<String, String> displayNames;

    /** {@inheritDoc} */
    public int getRank() {
        return rank;
    }

    /** {@inheritDoc} */
    public void setRank(final int rank) {
        this.rank = rank;
    }

    /** {@inheritDoc} */
    public String getChoiceData() {
        return choiceData;
    }

    /** {@inheritDoc} */
    public void setChoiceData(final String choiceData) {
        this.choiceData = choiceData;
    }

    /** {@inheritDoc} */
    public String getValidationFailedMessage() {
        return validationFailedMessage;
    }

    /** {@inheritDoc} */
    public void setValidationFailedMessage(final String validationFailedMessage) {
        this.validationFailedMessage = validationFailedMessage;
    }

    /** {@inheritDoc} */
    public long getAttributegroupId() {
        return attributegroupId;
    }

    /** {@inheritDoc} */
    public void setAttributegroupId(final long attributegroupId) {
        this.attributegroupId = attributegroupId;
    }

    /** {@inheritDoc} */
    public long getAttributeId() {
        return this.attributeId;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return attributeId;
    }

    /** {@inheritDoc} */
    public void setAttributeId(final long attributeId) {
        this.attributeId = attributeId;
    }

    /** {@inheritDoc} */
    public String getCode() {
        return code;
    }

    /** {@inheritDoc} */
    public void setCode(final String code) {
        this.code = code;
    }

    /** {@inheritDoc} */
    public boolean isMandatory() {
        return mandatory;
    }

    /** {@inheritDoc} */
    public void setMandatory(final boolean mandatory) {
        this.mandatory = mandatory;
    }

    /** {@inheritDoc} */
    public String getVal() {
        return val;
    }

    /** {@inheritDoc} */
    public void setVal(final String val) {
        this.val = val;
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    /** {@inheritDoc} */
    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    /** {@inheritDoc} */
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    public void setDescription(final String description) {
        this.description = description;
    }

    public long getEtypeId() {
        return etypeId;
    }

    /** {@inheritDoc} */
    public void setEtypeId(final long etypeId) {
        this.etypeId = etypeId;
    }

    /** {@inheritDoc} */
    public String getEtypeName() {
        return etypeName;
    }

    /** {@inheritDoc} */
    public void setEtypeName(final String etypeName) {
        this.etypeName = etypeName;
    }

    /** {@inheritDoc} */
    public boolean isAllowduplicate() {
        return allowduplicate;
    }

    /** {@inheritDoc} */
    public void setAllowduplicate(final boolean allowduplicate) {
        this.allowduplicate = allowduplicate;
    }

    /** {@inheritDoc} */
    public boolean isAllowfailover() {
        return allowfailover;
    }

    /** {@inheritDoc} */
    public void setAllowfailover(final boolean allowfailover) {
        this.allowfailover = allowfailover;
    }

    /** {@inheritDoc} */
    public String getRegexp() {
        return regexp;
    }

    /** {@inheritDoc} */
    public void setRegexp(final String regexp) {
        this.regexp = regexp;
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
                ", etypeId=" + etypeId +
                ", etypeName='" + etypeName + '\'' +
                ", attributegroupId=" + attributegroupId +
                ", allowduplicate=" + allowduplicate +
                ", allowfailover=" + allowfailover +
                ", regexp='" + regexp + '\'' +
                ", validationFailedMessage='" + validationFailedMessage + '\'' +
                ", rank=" + rank +
                ", choiceData='" + choiceData + '\'' +
                '}';
    }
}
