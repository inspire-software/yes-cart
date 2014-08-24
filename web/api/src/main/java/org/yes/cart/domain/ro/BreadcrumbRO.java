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
import java.util.Map;

/**
 * User: denispavlov
 * Date: 24/08/2014
 * Time: 19:31
 */
@Dto
@XmlRootElement(name = "breadcrumb")
public class BreadcrumbRO {

    @DtoField(value = "categoryId", readOnly = true)
    private long categoryId;

    @DtoField(value = "seo.uri", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String uri;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "displayName", converter = "i18nStringConverter")
    private Map<String, String> displayNames;

    public long getCategoryId() {
        return categoryId;
    }

    @XmlAttribute(name = "category-id")
    public void setCategoryId(final long categoryId) {
        this.categoryId = categoryId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
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

}
