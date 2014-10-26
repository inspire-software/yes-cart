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
import com.inspiresoftware.lib.dto.geda.annotations.DtoParent;
import org.yes.cart.domain.dto.*;
import org.yes.cart.domain.dto.matcher.impl.IdentifiableMatcher;
import org.yes.cart.domain.entity.AttrValueProduct;
import org.yes.cart.domain.entity.ProductCategory;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class ProductDTOImpl implements ProductDTO {

    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "productId", readOnly = true)
    private long productId;

    @DtoField(value = "code")
    private String code;

    @DtoField(value = "tag")
    private String tag;

    @DtoField(value = "availablefrom")
    private Date availablefrom;

    @DtoField(value = "availableto")
    private Date availableto;

    @DtoField(value = "availability")
    private int availability;

    @DtoField(value = "brand",
            dtoBeanKey = "org.yes.cart.domain.dto.BrandDTO",
            entityBeanKeys = "org.yes.cart.domain.entity.Brand")
    @DtoParent(value = "brandId", retriever = "brandDTO2Brand")
    private BrandDTO brandDTO;


    @DtoField(value = "producttype",
            dtoBeanKey = "org.yes.cart.domain.dto.ProductTypeDTO",
            entityBeanKeys = "org.yes.cart.domain.entity.ProductType")
    @DtoParent(value = "producttypeId", retriever = "productTypeDTO2ProductType")
    private ProductTypeDTO productTypeDTO;

    @DtoCollection(
            value = "productCategory",
            dtoBeanKey = "org.yes.cart.domain.dto.ProductCategoryDTO",
            entityGenericType = ProductCategory.class,
            entityCollectionClass = HashSet.class,
            dtoCollectionClass = HashSet.class,
            dtoToEntityMatcher = IdentifiableMatcher.class,
            readOnly = true
    )
    private Set<ProductCategoryDTO> productCategoryDTOs;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "displayName", converter = "i18nStringConverter")
    private Map<String, String> displayNames;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "featured")
    private Boolean featured;

    @DtoField(value = "minOrderQuantity")
    private BigDecimal minOrderQuantity;

    @DtoField(value = "maxOrderQuantity")
    private BigDecimal maxOrderQuantity;

    @DtoField(value = "stepOrderQuantity")
    private BigDecimal stepOrderQuantity;


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


    @DtoCollection(
            value = "attributes",
            dtoBeanKey = "org.yes.cart.domain.dto.AttrValueProductDTO",
            entityGenericType = AttrValueProduct.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = IdentifiableMatcher.class,
            readOnly = true
    )
    private Collection<AttrValueProductDTO> attributes;

    /**
     * {@inheritDoc}
     */
    public long getProductId() {
        return productId;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return productId;
    }

    /**
     * {@inheritDoc}
     */
    public void setProductId(final long productId) {
        this.productId = productId;
    }

    /**
     * {@inheritDoc}
     */
    public String getCode() {
        return code;
    }

    /**
     * {@inheritDoc}
     */
    public void setCode(final String code) {
        this.code = code;
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
    public int getAvailability() {
        return availability;
    }

    /**
     * {@inheritDoc}
     */
    public void setAvailability(final int availability) {
        this.availability = availability;
    }

    /**
     * {@inheritDoc}
     */
    public BrandDTO getBrandDTO() {
        return brandDTO;
    }

    /**
     * {@inheritDoc}
     */
    public void setBrandDTO(final BrandDTO brandDTO) {
        this.brandDTO = brandDTO;
    }

    /**
     * {@inheritDoc}
     */
    public ProductTypeDTO getProductTypeDTO() {
        return productTypeDTO;
    }

    /**
     * {@inheritDoc}
     */
    public void setProductTypeDTO(final ProductTypeDTO productTypeDTO) {
        this.productTypeDTO = productTypeDTO;
    }

    /**
     * {@inheritDoc}
     */
    public Set<ProductCategoryDTO> getProductCategoryDTOs() {
        return productCategoryDTOs;
    }

    /**
     * {@inheritDoc}
     */
    public void setProductCategoryDTOs(final Set<ProductCategoryDTO> productCategoryDTOs) {
        this.productCategoryDTOs = productCategoryDTOs;
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
    public Boolean getFeatured() {
        return featured;
    }

    /**
     * {@inheritDoc}
     */
    public void setFeatured(final Boolean featured) {
        this.featured = featured;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getMinOrderQuantity() {
        return minOrderQuantity;
    }

    /**
     * {@inheritDoc}
     */
    public void setMinOrderQuantity(final BigDecimal minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getMaxOrderQuantity() {
        return maxOrderQuantity;
    }

    /**
     * {@inheritDoc}
     */
    public void setMaxOrderQuantity(final BigDecimal maxOrderQuantity) {
        this.maxOrderQuantity = maxOrderQuantity;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getStepOrderQuantity() {
        return stepOrderQuantity;
    }

    /**
     * {@inheritDoc}
     */
    public void setStepOrderQuantity(final BigDecimal stepOrderQuantity) {
        this.stepOrderQuantity = stepOrderQuantity;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<AttrValueProductDTO> getAttributes() {
        return attributes;
    }

    /**
     * {@inheritDoc}
     */
    public void setAttributes(final Collection<AttrValueProductDTO> attributes) {
        this.attributes = attributes;
    }

    /**
     * {@inheritDoc}
     */
    public String getTag() {
        return tag;
    }

    /**
     * {@inheritDoc}
     */
    public void setTag(final String tag) {
        this.tag = tag;
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
        return "ProductDTOImpl{" +
                "productId=" + productId +
                ", code='" + code + '\'' +
                ", tag='" + tag + '\'' +
                ", availablefrom=" + availablefrom +
                ", availableto=" + availableto +
                ", availability=" + availability +
                ", brandDTO=" + brandDTO +
                ", productTypeDTO=" + productTypeDTO +
                ", productCategoryDTOs=" + productCategoryDTOs +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", featured=" + featured +
                ", attribute=" + attributes +
                '}';
    }
}
