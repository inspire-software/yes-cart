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
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.codehaus.jackson.annotate.JsonProperty;
import org.yes.cart.domain.dto.matcher.impl.IdentifiableMatcher;
import org.yes.cart.domain.entity.AttrValueCategory;
import org.yes.cart.domain.ro.xml.impl.I18nMapAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.*;

/**
 * User: denispavlov
 * Date: 19/08/2014
 * Time: 23:44
 */
@Dto
@XmlRootElement(name = "category")
public class CategoryRO implements Serializable {

    private static final long serialVersionUID = 20100717L;

    @DtoField(value = "categoryId", readOnly = true)
    private long categoryId;

    @DtoField(value = "parentId")
    private long parentId;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "productType.producttypeId", readOnly = true)
    private Long productTypeId;

    @DtoField(value = "productType.name", readOnly = true)
    private String productTypeName;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "displayName", converter = "i18nStringConverter")
    private Map<String, String> displayNames;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "uitemplate")
    private String uitemplate;

    @DtoField(value = "availablefrom")
    private Date availablefrom;

    @DtoField(value = "availableto")
    private Date availableto;

    @DtoField(value = "seo.uri", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String uri;

    @DtoField(value = "seo.title", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String title;

    @DtoField(value = "seo.metakeywords", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String metakeywords;

    @DtoField(value = "seo.metadescription", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String metadescription;

    @DtoField(value = "seo.displayTitle", converter = "i18nStringConverter")
    private Map<String, String> displayTitles;

    @DtoField(value = "seo.displayMetakeywords", converter = "i18nStringConverter")
    private Map<String, String> displayMetakeywords;

    @DtoField(value = "seo.displayMetadescription", converter = "i18nStringConverter")
    private Map<String, String> displayMetadescriptions;


    @DtoField(value = "navigationByAttributes")
    private Boolean navigationByAttributes;

    @DtoField(value = "navigationByBrand")
    private Boolean navigationByBrand;

    @DtoField(value = "navigationByPrice")
    private Boolean navigationByPrice;

    @DtoField(value = "navigationByPriceTiers")
    private String navigationByPriceTiers;


    @DtoCollection(
            value = "attributes",
            dtoBeanKey = "org.yes.cart.domain.ro.AttrValueCategoryRO",
            entityGenericType = AttrValueCategory.class,
            entityCollectionClass = HashSet.class,
            dtoCollectionClass = HashSet.class,
            dtoToEntityMatcher = IdentifiableMatcher.class,
            readOnly = true
    )
    private Set<AttrValueCategoryRO> attributes;

    private List<BreadcrumbRO> breadcrumbs = Collections.EMPTY_LIST;

    @XmlElement(name = "product-type-name")
    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(final String productTypeName) {
        this.productTypeName = productTypeName;
    }

    @XmlAttribute(name = "category-id")
    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(final long categoryId) {
        this.categoryId = categoryId;
    }

    @XmlAttribute(name = "parent-id")
    public long getParentId() {
        return parentId;
    }

    public void setParentId(final long parentId) {
        this.parentId = parentId;
    }

    @XmlElementWrapper(name = "breadcrumbs")
    @XmlElement(name = "breadcrumb")
    public List<BreadcrumbRO> getBreadcrumbs() {
        return breadcrumbs;
    }

    public void setBreadcrumbs(final List<BreadcrumbRO> breadcrumbs) {
        this.breadcrumbs = breadcrumbs;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    @XmlAttribute(name = "product-type-id")
    public Long getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(final Long productTypeId) {
        this.productTypeId = productTypeId;
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

    public String getUitemplate() {
        return uitemplate;
    }

    public void setUitemplate(final String uitemplate) {
        this.uitemplate = uitemplate;
    }

    public Date getAvailablefrom() {
        return availablefrom;
    }

    public void setAvailablefrom(final Date availablefrom) {
        this.availablefrom = availablefrom;
    }

    public Date getAvailableto() {
        return availableto;
    }

    public void setAvailableto(final Date availableto) {
        this.availableto = availableto;
    }


    @XmlAttribute(name = "navigation-by-attributes")
    public Boolean getNavigationByAttributes() {
        return navigationByAttributes;
    }

    public void setNavigationByAttributes(final Boolean navigationByAttributes) {
        this.navigationByAttributes = navigationByAttributes;
    }

    @XmlAttribute(name = "navigation-by-brand")
    public Boolean getNavigationByBrand() {
        return navigationByBrand;
    }

    public void setNavigationByBrand(final Boolean navigationByBrand) {
        this.navigationByBrand = navigationByBrand;
    }

    @XmlAttribute(name = "navigation-by-price")
    public Boolean getNavigationByPrice() {
        return navigationByPrice;
    }

    public void setNavigationByPrice(final Boolean navigationByPrice) {
        this.navigationByPrice = navigationByPrice;
    }

    @XmlElement(name = "navigation-by-price-tiers")
    public String getNavigationByPriceTiers() {
        return navigationByPriceTiers;
    }

    public void setNavigationByPriceTiers(final String navigationByPriceTiers) {
        this.navigationByPriceTiers = navigationByPriceTiers;
    }

    @XmlElement(name = "attribute-value")
    public Set<AttrValueCategoryRO> getAttributes() {
        return attributes;
    }

    public void setAttributes(final Set<AttrValueCategoryRO> attributes) {
        this.attributes = attributes;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-titles")
    public Map<String, String> getDisplayTitles() {
        return displayTitles;
    }

    public void setDisplayTitles(final Map<String, String> displayTitles) {
        this.displayTitles = displayTitles;
    }

    public String getMetakeywords() {
        return metakeywords;
    }

    public void setMetakeywords(final String metakeywords) {
        this.metakeywords = metakeywords;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-metakeywords")
    public Map<String, String> getDisplayMetakeywords() {
        return displayMetakeywords;
    }

    public void setDisplayMetakeywords(final Map<String, String> displayMetakeywords) {
        this.displayMetakeywords = displayMetakeywords;
    }

    public String getMetadescription() {
        return metadescription;
    }

    public void setMetadescription(final String metadescription) {
        this.metadescription = metadescription;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-metadescription")
    public Map<String, String> getDisplayMetadescriptions() {
        return displayMetadescriptions;
    }

    public void setDisplayMetadescriptions(final Map<String, String> displayMetadescriptions) {
        this.displayMetadescriptions = displayMetadescriptions;
    }


}
