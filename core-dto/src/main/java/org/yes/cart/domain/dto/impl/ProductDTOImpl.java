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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import com.inspiresoftware.lib.dto.geda.annotations.DtoParent;
import org.yes.cart.domain.dto.*;
import org.yes.cart.domain.dto.matcher.impl.IdentifiableMatcher;
import org.yes.cart.domain.entity.AttrValueProduct;
import org.yes.cart.domain.entity.ProductCategory;
import org.yes.cart.domain.entity.ProductSku;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
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

    @DtoField(value = "guid")
    private String guid;

    @DtoField(value = "code")
    private String code;

    @DtoField(value = "manufacturerCode")
    private String manufacturerCode;

    @DtoField(value = "manufacturerPartCode")
    private String manufacturerPartCode;

    @DtoField(value = "supplierCode")
    private String supplierCode;

    @DtoField(value = "supplierCatalogCode")
    private String supplierCatalogCode;

    @DtoField(value = "pimCode")
    private String pimCode;

    @DtoField(value = "pimDisabled")
    private boolean pimDisabled;

    @DtoField(value = "pimOutdated")
    private boolean pimOutdated;

    @DtoField(value = "pimUpdated", readOnly = true)
    private Instant pimUpdated;

    @DtoField(value = "tag")
    private String tag;

    @DtoField(value = "availablefrom")
    private LocalDateTime availablefrom;

    @DtoField(value = "availableto")
    private LocalDateTime availableto;

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

    @DtoCollection(
            value = "sku",
            dtoBeanKey = "org.yes.cart.domain.dto.ProductSkuDTO",
            entityGenericType = ProductSku.class,
            entityCollectionClass = HashSet.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = IdentifiableMatcher.class,
            readOnly = true
    )
    private List<ProductSkuDTO> sku;

    /**
     * {@inheritDoc}
     */
    @Override
    public long getProductId() {
        return productId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return productId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProductId(final long productId) {
        this.productId = productId;
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
    public String getCode() {
        return code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getManufacturerCode() {
        return manufacturerCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setManufacturerCode(final String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getManufacturerPartCode() {
        return manufacturerPartCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setManufacturerPartCode(final String manufacturerPartCode) {
        this.manufacturerPartCode = manufacturerPartCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSupplierCode() {
        return supplierCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSupplierCode(final String supplierCode) {
        this.supplierCode = supplierCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSupplierCatalogCode() {
        return supplierCatalogCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSupplierCatalogCode(final String supplierCatalogCode) {
        this.supplierCatalogCode = supplierCatalogCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPimCode() {
        return pimCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPimCode(final String pimCode) {
        this.pimCode = pimCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getPimDisabled() {
        return pimDisabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPimDisabled(final boolean pimDisabled) {
        this.pimDisabled = pimDisabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getPimOutdated() {
        return pimOutdated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPimOutdated(final boolean pimOutdated) {
        this.pimOutdated = pimOutdated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instant getPimUpdated() {
        return pimUpdated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPimUpdated(final Instant pimUpdated) {
        this.pimUpdated = pimUpdated;
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
    public int getAvailability() {
        return availability;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAvailability(final int availability) {
        this.availability = availability;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BrandDTO getBrandDTO() {
        return brandDTO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBrandDTO(final BrandDTO brandDTO) {
        this.brandDTO = brandDTO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductTypeDTO getProductTypeDTO() {
        return productTypeDTO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProductTypeDTO(final ProductTypeDTO productTypeDTO) {
        this.productTypeDTO = productTypeDTO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ProductCategoryDTO> getProductCategoryDTOs() {
        return productCategoryDTOs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProductCategoryDTOs(final Set<ProductCategoryDTO> productCategoryDTOs) {
        this.productCategoryDTOs = productCategoryDTOs;
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
    public Boolean getFeatured() {
        return featured;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFeatured(final Boolean featured) {
        this.featured = featured;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getMinOrderQuantity() {
        return minOrderQuantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMinOrderQuantity(final BigDecimal minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getMaxOrderQuantity() {
        return maxOrderQuantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxOrderQuantity(final BigDecimal maxOrderQuantity) {
        this.maxOrderQuantity = maxOrderQuantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getStepOrderQuantity() {
        return stepOrderQuantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStepOrderQuantity(final BigDecimal stepOrderQuantity) {
        this.stepOrderQuantity = stepOrderQuantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<AttrValueProductDTO> getAttributes() {
        return attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAttributes(final Collection<AttrValueProductDTO> attributes) {
        this.attributes = attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTag() {
        return tag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTag(final String tag) {
        this.tag = tag;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductSkuDTO> getSku() {
        return sku;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSku(final List<ProductSkuDTO> sku) {
        this.sku = sku;
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
