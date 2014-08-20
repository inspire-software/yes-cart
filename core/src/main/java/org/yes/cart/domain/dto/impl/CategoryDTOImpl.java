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
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.AttrValueCategoryDTO;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.dto.matcher.impl.IdentifiableMatcher;
import org.yes.cart.domain.entity.AttrValueCategory;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class CategoryDTOImpl implements CategoryDTO {

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
            dtoBeanKey = "org.yes.cart.domain.dto.AttrValueCategoryDTO",
            entityGenericType = AttrValueCategory.class,
            entityCollectionClass = HashSet.class,
            dtoCollectionClass = HashSet.class,
            dtoToEntityMatcher = IdentifiableMatcher.class,
            readOnly = true
    )
    private Set<AttrValueCategoryDTO> attributes;


    private List<CategoryDTO> children;

    /**
     * {@inheritDoc}
     */
    public String getProductTypeName() {
        return productTypeName;
    }

    /**
     * {@inheritDoc}
     */
    public void setProductTypeName(final String productTypeName) {
        this.productTypeName = productTypeName;
    }

    /**
     * {@inheritDoc}
     */
    public List<CategoryDTO> getChildren() {
        return children;
    }

    /**
     * {@inheritDoc}
     */
    public void setChildren(final List<CategoryDTO> children) {
        this.children = children;
    }

    /**
     * {@inheritDoc}
     */
    public long getCategoryId() {
        return categoryId;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return categoryId;
    }

    /**
     * {@inheritDoc}
     */
    public void setCategoryId(final long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * {@inheritDoc}
     */
    public long getParentId() {
        return parentId;
    }

    /**
     * {@inheritDoc}
     */
    public void setParentId(final long parentId) {
        this.parentId = parentId;
    }

    /**
     * {@inheritDoc}
     */
    public int getRank() {
        return rank;
    }

    /**
     * {@inheritDoc}
     */
    public void setRank(final int rank) {
        this.rank = rank;
    }

    /**
     * {@inheritDoc}
     */
    public Long getProductTypeId() {
        return productTypeId;
    }

    /**
     * {@inheritDoc}
     */
    public void setProductTypeId(final Long productTypeId) {
        this.productTypeId = productTypeId;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    public String getUitemplate() {
        return uitemplate;
    }

    /**
     * {@inheritDoc}
     */
    public void setUitemplate(final String uitemplate) {
        this.uitemplate = uitemplate;
    }

    /**
     * {@inheritDoc}
     */
    public Date getAvailablefrom() {
        return availablefrom;
    }

    /**
     * {@inheritDoc}
     */
    public void setAvailablefrom(final Date availablefrom) {
        this.availablefrom = availablefrom;
    }

    /**
     * {@inheritDoc}
     */
    public Date getAvailableto() {
        return availableto;
    }

    /**
     * {@inheritDoc}
     */
    public void setAvailableto(final Date availableto) {
        this.availableto = availableto;
    }



    /**
     * {@inheritDoc}
     */
    public Boolean getNavigationByAttributes() {
        return navigationByAttributes;
    }

    /**
     * {@inheritDoc}
     */
    public void setNavigationByAttributes(final Boolean navigationByAttributes) {
        this.navigationByAttributes = navigationByAttributes;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean getNavigationByBrand() {
        return navigationByBrand;
    }

    /**
     * {@inheritDoc}
     */
    public void setNavigationByBrand(final Boolean navigationByBrand) {
        this.navigationByBrand = navigationByBrand;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean getNavigationByPrice() {
        return navigationByPrice;
    }

    /**
     * {@inheritDoc}
     */
    public void setNavigationByPrice(final Boolean navigationByPrice) {
        this.navigationByPrice = navigationByPrice;
    }

    /**
     * {@inheritDoc}
     */
    public String getNavigationByPriceTiers() {
        return navigationByPriceTiers;
    }

    /**
     * {@inheritDoc}
     */
    public void setNavigationByPriceTiers(final String navigationByPriceTiers) {
        this.navigationByPriceTiers = navigationByPriceTiers;
    }

    /**
     * {@inheritDoc}
     */
    public Set<AttrValueCategoryDTO> getAttributes() {
        return attributes;
    }

    /**
     * {@inheritDoc}
     */
    public void setAttributes(final Set<AttrValueCategoryDTO> attributes) {
        this.attributes = attributes;
    }

    /**
     * {@inheritDoc}
     */
    public String getUri() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    public void setUri(final String uri) {
        this.uri = uri;
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return title;
    }

    /**
     * {@inheritDoc}
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getDisplayTitles() {
        return displayTitles;
    }

    /**
     * {@inheritDoc}
     */
    public void setDisplayTitles(final Map<String, String> displayTitles) {
        this.displayTitles = displayTitles;
    }

    /**
     * {@inheritDoc}
     */
    public String getMetakeywords() {
        return metakeywords;
    }

    /**
     * {@inheritDoc}
     */
    public void setMetakeywords(final String metakeywords) {
        this.metakeywords = metakeywords;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getDisplayMetakeywords() {
        return displayMetakeywords;
    }

    /**
     * {@inheritDoc}
     */
    public void setDisplayMetakeywords(final Map<String, String> displayMetakeywords) {
        this.displayMetakeywords = displayMetakeywords;
    }

    /**
     * {@inheritDoc}
     */
    public String getMetadescription() {
        return metadescription;
    }

    /**
     * {@inheritDoc}
     */
    public void setMetadescription(final String metadescription) {
        this.metadescription = metadescription;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getDisplayMetadescriptions() {
        return displayMetadescriptions;
    }

    /**
     * {@inheritDoc}
     */
    public void setDisplayMetadescriptions(final Map<String, String> displayMetadescriptions) {
        this.displayMetadescriptions = displayMetadescriptions;
    }

    @Override
    public String toString() {
        return "CategoryDTOImpl{" +
                "categoryId=" + categoryId +
                ", parentId=" + parentId +
                ", rank=" + rank +
                ", productTypeId=" + productTypeId +
                ", productTypeName='" + productTypeName + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", uitemplate='" + uitemplate + '\'' +
                ", availablefrom=" + availablefrom +
                ", availableto=" + availableto +
                ", navigationByAttributes=" + navigationByAttributes +
                ", navigationByBrand=" + navigationByBrand +
                ", navigationByPrice=" + navigationByPrice +
                ", navigationByPriceTiers='" + navigationByPriceTiers + '\'' +
                ", attribute=" + attributes +
                ", children=" + children +
                '}';
    }
}
