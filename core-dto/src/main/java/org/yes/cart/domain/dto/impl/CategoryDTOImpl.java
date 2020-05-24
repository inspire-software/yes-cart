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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.AttrValueCategoryDTO;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.dto.matcher.impl.IdentifiableMatcher;
import org.yes.cart.domain.entity.AttrValueCategory;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private String parentName;

    @DtoField(value = "linkToId")
    private Long linkToId;
    private String linkToName;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "productType.producttypeId", readOnly = true)
    private Long productTypeId;

    @DtoField(value = "productType.name", readOnly = true)
    private String productTypeName;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "guid")
    private String guid;

    @DtoField(value = "displayName", converter = "i18nModelConverter")
    private Map<String, String> displayNames;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "uitemplate")
    private String uitemplate;

    @DtoField(value = "disabled")
    private boolean disabled;

    @DtoField(value = "availablefrom")
    private LocalDateTime availablefrom;

    @DtoField(value = "availableto")
    private LocalDateTime availableto;

    @DtoField(value = "seo.uri", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String uri;

    @DtoField(value = "seo.title", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String title;

    @DtoField(value = "seo.metakeywords", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String metakeywords;

    @DtoField(value = "seo.metadescription", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String metadescription;

    @DtoField(value = "seo.displayTitle", converter = "i18nModelConverter")
    private Map<String, String> displayTitles;

    @DtoField(value = "seo.displayMetakeywords", converter = "i18nModelConverter")
    private Map<String, String> displayMetakeywords;

    @DtoField(value = "seo.displayMetadescription", converter = "i18nModelConverter")
    private Map<String, String> displayMetadescriptions;


    @DtoField(value = "navigationByAttributes")
    private Boolean navigationByAttributes;

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
    @Override
    public String getProductTypeName() {
        return productTypeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProductTypeName(final String productTypeName) {
        this.productTypeName = productTypeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CategoryDTO> getChildren() {
        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setChildren(final List<CategoryDTO> children) {
        this.children = children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCategoryId() {
        return categoryId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return categoryId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCategoryId(final long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getParentId() {
        return parentId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParentId(final long parentId) {
        this.parentId = parentId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParentName() {
        return parentName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParentName(final String parentName) {
        this.parentName = parentName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getLinkToId() {
        return linkToId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLinkToId(final Long linkToId) {
        this.linkToId = linkToId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLinkToName() {
        return linkToName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLinkToName(final String linkToName) {
        this.linkToName = linkToName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRank() {
        return rank;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRank(final int rank) {
        this.rank = rank;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getProductTypeId() {
        return productTypeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProductTypeId(final Long productTypeId) {
        this.productTypeId = productTypeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGuid() {
        return guid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    /** {@inheritDoc} */
    @Override
    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUitemplate() {
        return uitemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUitemplate(final String uitemplate) {
        this.uitemplate = uitemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getAvailablefrom() {
        return availablefrom;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAvailablefrom(final LocalDateTime availablefrom) {
        this.availablefrom = availablefrom;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getAvailableto() {
        return availableto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAvailableto(final LocalDateTime availableto) {
        this.availableto = availableto;
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getNavigationByAttributes() {
        return navigationByAttributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNavigationByAttributes(final Boolean navigationByAttributes) {
        this.navigationByAttributes = navigationByAttributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getNavigationByPrice() {
        return navigationByPrice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNavigationByPrice(final Boolean navigationByPrice) {
        this.navigationByPrice = navigationByPrice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNavigationByPriceTiers() {
        return navigationByPriceTiers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNavigationByPriceTiers(final String navigationByPriceTiers) {
        this.navigationByPriceTiers = navigationByPriceTiers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<AttrValueCategoryDTO> getAttributes() {
        return attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAttributes(final Set<AttrValueCategoryDTO> attributes) {
        this.attributes = attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUri() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUri(final String uri) {
        this.uri = uri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getDisplayTitles() {
        return displayTitles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayTitles(final Map<String, String> displayTitles) {
        this.displayTitles = displayTitles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMetakeywords() {
        return metakeywords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMetakeywords(final String metakeywords) {
        this.metakeywords = metakeywords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getDisplayMetakeywords() {
        return displayMetakeywords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayMetakeywords(final Map<String, String> displayMetakeywords) {
        this.displayMetakeywords = displayMetakeywords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMetadescription() {
        return metadescription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMetadescription(final String metadescription) {
        this.metadescription = metadescription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getDisplayMetadescriptions() {
        return displayMetadescriptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
                ", navigationByPrice=" + navigationByPrice +
                ", navigationByPriceTiers='" + navigationByPriceTiers + '\'' +
                ", attribute=" + attributes +
                ", children=" + children +
                '}';
    }
}
