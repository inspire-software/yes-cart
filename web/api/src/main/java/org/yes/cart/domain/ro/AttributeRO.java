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

    private static final long serialVersionUID = 20150301L;

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

    @DtoField(value = "etype", readOnly=true)
    private String etype;

    @DtoField(value = "attributeGroup", readOnly=true)
    private String attributegroup;

    @DtoField(value = "allowduplicate", readOnly=true)
    private boolean allowduplicate;

    @DtoField(value = "allowfailover", readOnly=true)
    private boolean allowfailover;


    @DtoField(value = "regexp", readOnly=true)
    private String regexp;

    @DtoField(value = "validationFailedMessage", converter = "i18nModelConverter", readOnly=true)
    private Map<String, String> validationFailedMessage;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "choiceData", converter = "i18nModelConverter", readOnly=true)
    private Map<String, String> choiceData;

    @DtoField(value = "displayName", converter = "i18nModelConverter", readOnly=true)
    private Map<String, String> displayNames;

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "choice-data")
    public Map<String, String> getChoiceData() {
        return choiceData;
    }

    public void setChoiceData(final Map<String, String> choiceData) {
        this.choiceData = choiceData;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "validation-failed-message")
    public Map<String, String> getValidationFailedMessage() {
        return validationFailedMessage;
    }

    public void setValidationFailedMessage(final Map<String, String> validationFailedMessage) {
        this.validationFailedMessage = validationFailedMessage;
    }

    public String getAttributegroup() {
        return attributegroup;
    }

    public void setAttributegroup(final String attributegroup) {
        this.attributegroup = attributegroup;
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

    public String getEtype() {
        return etype;
    }

    public void setEtype(final String etype) {
        this.etype = etype;
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
                ", etype='" + etype + '\'' +
                ", attributegroupId=" + attributegroup +
                ", allowduplicate=" + allowduplicate +
                ", allowfailover=" + allowfailover +
                ", regexp='" + regexp + '\'' +
                ", validationFailedMessage='" + validationFailedMessage + '\'' +
                ", rank=" + rank +
                ", choiceData='" + choiceData + '\'' +
                '}';
    }
}
