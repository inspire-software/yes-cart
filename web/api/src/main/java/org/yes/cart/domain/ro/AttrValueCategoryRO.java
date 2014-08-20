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
public class AttrValueCategoryRO implements Serializable {

    private static final long serialVersionUID = 20100717L;

    @DtoField(value = "attrvalueId", readOnly = true)
    private long attrvalueId;

    @DtoField(value = "val")
    private String val;

    @DtoField(value = "displayVal", converter = "i18nStringConverter")
    private Map<String, String> displayVals;

    @DtoField(value = "attribute.attributeId", readOnly = true)
    private long attributeId;

    @DtoField(value = "attribute.name", readOnly = true)
    private String attributeName;

    @DtoField(value = "attribute.displayName", converter = "i18nStringConverter", readOnly = true)
    private Map<String, String> attributeDisplayNames;

    @DtoField(value = "category.categoryId", readOnly = true)
    private long categoryId;


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

    @XmlAttribute(name = "attribute-id")
    public long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(final long attributeId) {
        this.attributeId = attributeId;
    }

    @XmlElement(name = "attribute-name")
    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(final String attributeName) {
        this.attributeName = attributeName;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "attribute-display-vals")
    public Map<String, String> getAttributeDisplayNames() {
        return attributeDisplayNames;
    }

    public void setAttributeDisplayNames(final Map<String, String> attributeDisplayNames) {
        this.attributeDisplayNames = attributeDisplayNames;
    }

    @XmlAttribute(name = "category-id")
    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(final long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "AttrValueCategoryROImpl{" +
                "attrvalueId=" + attrvalueId +
                ", val='" + val + '\'' +
                ", attributeId=" + attributeId +
                ", categoryId=" + categoryId +
                '}';
    }
}
