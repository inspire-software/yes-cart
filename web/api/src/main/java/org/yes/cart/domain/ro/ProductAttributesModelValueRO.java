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
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.ro.xml.impl.I18nMapAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 24/05/2020
 * Time: 08:40
 */
@Dto
@XmlRootElement(name = "attribute-value")
public class ProductAttributesModelValueRO implements Serializable {

    private static final long serialVersionUID = 20200524L;

    @DtoField(readOnly = true)
    private String code;

    @DtoField(readOnly = true)
    private String val;

    @DtoField(readOnly = true)
    private Map<String, String> displayVals;

    @XmlAttribute(name = "attribute-code")
    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
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
}
