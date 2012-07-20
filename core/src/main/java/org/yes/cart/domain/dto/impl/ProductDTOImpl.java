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
import org.yes.cart.domain.dto.matcher.impl.AttrValueProductMatcher;
import org.yes.cart.domain.dto.matcher.impl.ProductCategoryMatcher;
import org.yes.cart.domain.entity.ProductCategory;
import org.yes.cart.domain.entity.impl.AttrValueEntityProduct;

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

    @DtoField(value = "availabletill")
    private Date availabletill;

    @DtoField(value = "availability")
    private int availability;

    @DtoField(value = "brand",
            dtoBeanKey = "org.yes.cart.domain.dto.BrandDTO",
            entityBeanKeys = "org.yes.cart.domain.entity.Brand")
    @DtoParent(value = "brandId", retriever = "brandDTO2Brand")
    private BrandDTO brandDTO;


    @DtoField(value = "producttype",
            dtoBeanKey =  "org.yes.cart.domain.dto.ProductTypeDTO",
            entityBeanKeys = "org.yes.cart.domain.entity.ProductType")
    @DtoParent(value = "producttypeId", retriever = "productTypeDTO2ProductType")
    private ProductTypeDTO productTypeDTO;

    @DtoCollection(
            value="productCategory",
            dtoBeanKey="org.yes.cart.domain.dto.ProductCategoryDTO",
            entityGenericType = ProductCategory.class,
            entityCollectionClass = HashSet.class,
            dtoCollectionClass = HashSet.class, 
            dtoToEntityMatcher = ProductCategoryMatcher.class,
            readOnly = true            
            )
    private Set<ProductCategoryDTO> productCategoryDTOs;


    @DtoField(value = "seo", dtoBeanKey = "org.yes.cart.domain.dto.SeoDTO", readOnly = true)
    private SeoDTO seoDTO;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "featured")
    private Boolean featured;



    @DtoCollection(
            value="attribute",
            dtoBeanKey="org.yes.cart.domain.dto.AttrValueProductDTO",
            entityGenericType = AttrValueEntityProduct.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = AttrValueProductMatcher.class,
            readOnly = true
            )
    private Collection<AttrValueProductDTO> attribute;

    /** {@inheritDoc} */
    public long getProductId() {
        return productId;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return productId;
    }

    /** {@inheritDoc} */
    public void setProductId(final long productId) {
        this.productId = productId;
    }

    /** {@inheritDoc} */
    public String getCode() {
        return code;
    }

    /** {@inheritDoc} */
    public void setCode(final String code) {
        this.code = code;
    }

    /** {@inheritDoc} */
    public Date getAvailablefrom() {
        return availablefrom;
    }

    /** {@inheritDoc} */
    public void setAvailablefrom(final Date availablefrom) {
        this.availablefrom = availablefrom;
    }

    /** {@inheritDoc} */
    public Date getAvailabletill() {
        return availabletill;
    }

    /** {@inheritDoc} */
    public void setAvailabletill(final Date availabletill) {
        this.availabletill = availabletill;
    }

    /** {@inheritDoc} */
    public int getAvailability() {
        return availability;
    }

    /** {@inheritDoc} */
    public void setAvailability(final int availability) {
        this.availability = availability;
    }

    /** {@inheritDoc} */
    public BrandDTO getBrandDTO() {
        return brandDTO;
    }

    /** {@inheritDoc} */
    public void setBrandDTO(final BrandDTO brandDTO) {
        this.brandDTO = brandDTO;
    }

    /** {@inheritDoc} */
    public ProductTypeDTO getProductTypeDTO() {
        return productTypeDTO;
    }

    /** {@inheritDoc} */
    public void setProductTypeDTO(final ProductTypeDTO productTypeDTO) {
        this.productTypeDTO = productTypeDTO;
    }

    /** {@inheritDoc} */
    public Set<ProductCategoryDTO> getProductCategoryDTOs() {
        return productCategoryDTOs;
    }

    /** {@inheritDoc} */
    public void setProductCategoryDTOs(final Set<ProductCategoryDTO> productCategoryDTOs) {
        this.productCategoryDTOs = productCategoryDTOs;
    }

    /** {@inheritDoc} */
    public SeoDTO getSeoDTO() {
        return seoDTO;
    }

    /** {@inheritDoc} */
    public void setSeoDTO(final SeoDTO seoDTO) {
        this.seoDTO = seoDTO;
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    public void setDescription(final String description) {
        this.description = description;
    }

    /** {@inheritDoc} */
    public Boolean getFeatured() {
        return featured;
    }

    /** {@inheritDoc} */
    public void setFeatured(final Boolean featured) {
        this.featured = featured;
    }

    /** {@inheritDoc} */
    public Collection<AttrValueProductDTO> getAttribute() {
        return attribute;
    }

    /** {@inheritDoc} */
    public void setAttribute(final Collection<AttrValueProductDTO> attribute) {
        this.attribute = attribute;
    }

    /** {@inheritDoc} */
    public String getTag() {
        return tag;
    }

    /** {@inheritDoc} */
    public void setTag(final String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "ProductDTOImpl{" +
                "productId=" + productId +
                ", code='" + code + '\'' +
                ", tag='" + tag + '\'' +
                ", availablefrom=" + availablefrom +
                ", availabletill=" + availabletill +
                ", availability=" + availability +
                ", brandDTO=" + brandDTO +
                ", productTypeDTO=" + productTypeDTO +
                ", productCategoryDTOs=" + productCategoryDTOs +
                ", seoDTO=" + seoDTO +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", featured=" + featured +
                ", attribute=" + attribute +
                '}';
    }
}
