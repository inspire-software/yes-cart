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

package org.yes.cart.domain.ro;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.ro.xml.impl.I18nMapAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Map;

@Dto
@XmlRootElement(name = "attribute")
public class AttributeRO implements Serializable {

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

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    @XmlElement(name = "choice-data")
    public String getChoiceData() {
        return choiceData;
    }

    public void setChoiceData(final String choiceData) {
        this.choiceData = choiceData;
    }

    @XmlElement(name = "validation-failed-message")
    public String getValidationFailedMessage() {
        return validationFailedMessage;
    }

    public void setValidationFailedMessage(final String validationFailedMessage) {
        this.validationFailedMessage = validationFailedMessage;
    }

    @XmlAttribute(name = "attributegroup-id")
    public long getAttributegroupId() {
        return attributegroupId;
    }

    public void setAttributegroupId(final long attributegroupId) {
        this.attributegroupId = attributegroupId;
    }

    @XmlAttribute(name = "attribute-id")
    public long getAttributeId() {
        return this.attributeId;
    }

    public void setAttributeId(final long attributeId) {
        this.attributeId = attributeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(final boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getVal() {
        return val;
    }

    public void setVal(final String val) {
        this.val = val;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-names")
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @XmlElement(name = "etype-id")
    public long getEtypeId() {
        return etypeId;
    }

    public void setEtypeId(final long etypeId) {
        this.etypeId = etypeId;
    }

    @XmlElement(name = "etype-name")
    public String getEtypeName() {
        return etypeName;
    }

    public void setEtypeName(final String etypeName) {
        this.etypeName = etypeName;
    }

    public boolean isAllowduplicate() {
        return allowduplicate;
    }

    public void setAllowduplicate(final boolean allowduplicate) {
        this.allowduplicate = allowduplicate;
    }

    public boolean isAllowfailover() {
        return allowfailover;
    }

    public void setAllowfailover(final boolean allowfailover) {
        this.allowfailover = allowfailover;
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(final String regexp) {
        this.regexp = regexp;
    }

    @Override
    public String toString() {
        return "AttributeROImpl{" +
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
