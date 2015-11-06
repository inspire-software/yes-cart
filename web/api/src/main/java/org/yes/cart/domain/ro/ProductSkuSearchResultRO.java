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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 22/03/2015
 * Time: 15:29
 */
@Dto
@XmlRootElement(name = "sku-result")
public class ProductSkuSearchResultRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    @DtoField(readOnly = true)
    private long id;
    @DtoField(readOnly = true)
    private long productId;
    @DtoField(readOnly = true)
    private String code;
    @DtoField(readOnly = true)
    private String manufacturerCode;
    @DtoField(readOnly = true)
    private String name;
    @DtoField(value = "displayName", converter = "i18nStringConverter", readOnly = true)
    private Map<String, String> displayName;
    @DtoField(readOnly = true)
    private String defaultImage;

    @DtoField(readOnly = true, converter = "storedAttributesConverter")
    private List<ProductSearchResultAttributeRO> attributes;

    @XmlElement(name = "default-image")
    public String getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(final String defaultImage) {
        this.defaultImage = defaultImage;
    }


    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    @XmlElement(name = "product-id")
    public long getProductId() {
        return productId;
    }

    public void setProductId(final long productId) {
        this.productId = productId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    @XmlElement(name = "manufacturer-code")
    public String getManufacturerCode() {
        return manufacturerCode;
    }

    public void setManufacturerCode(final String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-names")
    public Map<String, String> getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final Map<String, String> displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @XmlElementWrapper(name = "attributes")
    @XmlElement(name = "attribute")
    public List<ProductSearchResultAttributeRO> getAttributes() {
        return attributes;
    }

    public void setAttributes(final List<ProductSearchResultAttributeRO> attributes) {
        this.attributes = attributes;
    }

}
