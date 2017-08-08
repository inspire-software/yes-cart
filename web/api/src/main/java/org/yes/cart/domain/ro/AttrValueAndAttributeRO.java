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
import org.codehaus.jackson.annotate.JsonIgnore;
import org.yes.cart.domain.ro.xml.impl.I18nMapAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 21/07/2017
 * Time: 12:40
 */
@Dto
@XmlRootElement(name = "attribute")
public class AttrValueAndAttributeRO implements Serializable {

    private static final long serialVersionUID = 20150301L;


    @DtoField(value = "attrvalueId", readOnly = true)
    private long attrvalueId;

    @DtoField(value = "val", readOnly = true)
    private String val;

    @DtoField(value = "displayVal", converter = "i18nStringConverter", readOnly = true)
    private Map<String, String> displayVals;

    @DtoField(value = "attributeCode", converter = "attributeCodeConverter", readOnly = true)
    private AttributeRO attribute;


    @XmlAttribute(name = "attrvalue-id")
    public long getAttrvalueId() {
        return attrvalueId;
    }

    public void setAttrvalueId(final long attrvalueId) {
        this.attrvalueId = attrvalueId;
    }

    public String getVal() {
        return val;
    }

    public void setVal(final String val) {
        this.val = val;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-vals")
    public Map<String, String> getDisplayVals() {
        return displayVals;
    }

    public void setDisplayVals(final Map<String, String> displayVals) {
        this.displayVals = displayVals;
    }

    @JsonIgnore
    @XmlTransient
    public AttributeRO getAttribute() {
        return attribute;
    }

    public void setAttribute(final AttributeRO attribute) {
        this.attribute = attribute;
    }

    private void initAttribute() {
        if (this.attribute == null) {
            this.attribute = new AttributeRO();
        }
    }

    @XmlAttribute(name = "attribute-id")
    public long getAttributeId() {
        initAttribute();
        return attribute.getAttributeId();
    }

    public void setAttributeId(final long attributeId) {
        initAttribute();
        this.attribute.setAttributeId(attributeId);
    }

    @XmlAttribute(name = "attribute-code")
    public String getAttributeCode() {
        initAttribute();
        return attribute.getCode();
    }

    public void setAttributeCode(final String attributeCode) {
        initAttribute();
        this.attribute.setCode(attributeCode);
    }

    @XmlElement(name = "attribute-name")
    public String getAttributeName() {
        initAttribute();
        return attribute.getName();
    }

    public void setAttributeName(final String attributeName) {
        initAttribute();
        this.attribute.setName(attributeName);
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "attribute-display-names")
    public Map<String, String> getAttributeDisplayNames() {
        initAttribute();
        return attribute.getDisplayNames();
    }

    public void setAttributeDisplayNames(final Map<String, String> attributeDisplayNames) {
        initAttribute();
        this.attribute.setDisplayNames(attributeDisplayNames);
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "attribute-display-choices")
    public Map<String, String> getAttributeDisplayChoices() {
        initAttribute();
        return attribute.getChoiceData();
    }

    public void setAttributeDisplayChoices(final Map<String, String> attributeDisplayChoices) {
        initAttribute();
        this.attribute.setChoiceData(attributeDisplayChoices);
    }
}
