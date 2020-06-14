/*
 * Copyright 2009 Inspire-Software.com
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
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.matcher.impl.NoopMatcher;
import org.yes.cart.domain.entity.ProductAttributesModelValue;
import org.yes.cart.domain.ro.xml.impl.I18nMapAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 24/05/2020
 * Time: 08:44
 */
@Dto
@XmlRootElement(name = "attribute")
public class ProductAttributesModelAttributeRO implements Serializable {

    private static final long serialVersionUID = 20200524L;

    @DtoField(readOnly = true)
    private String code;

    @DtoField(readOnly = true)
    private boolean multivalue;

    @DtoField(readOnly = true)
    private Map<String, String> displayNames;

    @DtoCollection(
            value = "values",
            dtoBeanKey = "org.yes.cart.domain.ro.ProductAttributesModelValueRO",
            entityGenericType = ProductAttributesModelValue.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private List<ProductAttributesModelValueRO> values;

    @XmlAttribute(name = "attribute-code")
    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public boolean isMultivalue() {
        return multivalue;
    }

    public void setMultivalue(final boolean multivalue) {
        this.multivalue = multivalue;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "attribute-display-names")
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    @XmlElementWrapper(name = "attribute-values")
    @XmlElement(name = "attribute-value")
    public List<ProductAttributesModelValueRO> getValues() {
        return values;
    }

    public void setValues(final List<ProductAttributesModelValueRO> values) {
        this.values = values;
    }
}
