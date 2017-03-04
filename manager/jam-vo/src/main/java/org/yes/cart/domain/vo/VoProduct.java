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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.ProductCategoryDTO;
import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.matcher.VoProductCategoryMatcher;
import org.yes.cart.domain.vo.matcher.VoProductSkuMatcher;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: denispavlov
 */
@Dto
public class VoProduct {

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

    @DtoField(value = "tag")
    private String tag;

    @DtoField(value = "availablefrom")
    private Date availablefrom;

    @DtoField(value = "availableto")
    private Date availableto;

    @DtoField(value = "availability")
    private int availability;

    @DtoField(value = "brandDTO",
            dtoBeanKey = "VoBrand",
            entityBeanKeys = "org.yes.cart.dto.BrandDTO",
            readOnly = true)
    private VoBrand brand;


    @DtoField(value = "productTypeDTO",
            dtoBeanKey = "VoProductTypeInfo",
            entityBeanKeys = "org.yes.cart.dto.ProductTypeDTO",
            readOnly = true)
    private VoProductTypeInfo productType;

    @DtoCollection(
            value = "productCategoryDTOs",
            dtoBeanKey = "VoProductCategory",
            entityGenericType = ProductCategoryDTO.class,
            entityCollectionClass = HashSet.class,
            dtoCollectionClass = HashSet.class,
            dtoToEntityMatcher = VoProductCategoryMatcher.class,
            readOnly = true
    )
    private Set<VoProductCategory> productCategories;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "displayNames", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayNames;

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


    @DtoField(value = "uri")
    private String uri;

    @DtoField(value = "title")
    private String title;

    @DtoField(value = "metakeywords")
    private String metakeywords;

    @DtoField(value = "metadescription")
    private String metadescription;

    @DtoField(value = "displayTitles", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayTitles;

    @DtoField(value = "displayMetakeywords", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayMetakeywords;

    @DtoField(value = "displayMetadescriptions", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayMetadescriptions;


    @DtoCollection(
            value = "sku",
            dtoBeanKey = "VoProductSku",
            entityGenericType = ProductSkuDTO.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = VoProductSkuMatcher.class,
            readOnly = true
    )
    private List<VoProductSku> sku;


    public long getProductId() {
        return productId;
    }

    public void setProductId(final long productId) {
        this.productId = productId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(final String guid) {
        this.guid = guid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getManufacturerCode() {
        return manufacturerCode;
    }

    public void setManufacturerCode(final String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    public String getManufacturerPartCode() {
        return manufacturerPartCode;
    }

    public void setManufacturerPartCode(final String manufacturerPartCode) {
        this.manufacturerPartCode = manufacturerPartCode;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(final String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierCatalogCode() {
        return supplierCatalogCode;
    }

    public void setSupplierCatalogCode(final String supplierCatalogCode) {
        this.supplierCatalogCode = supplierCatalogCode;
    }

    public String getPimCode() {
        return pimCode;
    }

    public void setPimCode(final String pimCode) {
        this.pimCode = pimCode;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
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

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(final int availability) {
        this.availability = availability;
    }

    public VoBrand getBrand() {
        return brand;
    }

    public void setBrand(final VoBrand brand) {
        this.brand = brand;
    }

    public VoProductTypeInfo getProductType() {
        return productType;
    }

    public void setProductType(final VoProductTypeInfo productType) {
        this.productType = productType;
    }

    public Set<VoProductCategory> getProductCategories() {
        return productCategories;
    }

    public void setProductCategories(final Set<VoProductCategory> productCategories) {
        this.productCategories = productCategories;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<MutablePair<String, String>> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(final List<MutablePair<String, String>> displayNames) {
        this.displayNames = displayNames;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(final Boolean featured) {
        this.featured = featured;
    }

    public BigDecimal getMinOrderQuantity() {
        return minOrderQuantity;
    }

    public void setMinOrderQuantity(final BigDecimal minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    public BigDecimal getMaxOrderQuantity() {
        return maxOrderQuantity;
    }

    public void setMaxOrderQuantity(final BigDecimal maxOrderQuantity) {
        this.maxOrderQuantity = maxOrderQuantity;
    }

    public BigDecimal getStepOrderQuantity() {
        return stepOrderQuantity;
    }

    public void setStepOrderQuantity(final BigDecimal stepOrderQuantity) {
        this.stepOrderQuantity = stepOrderQuantity;
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

    public String getMetakeywords() {
        return metakeywords;
    }

    public void setMetakeywords(final String metakeywords) {
        this.metakeywords = metakeywords;
    }

    public String getMetadescription() {
        return metadescription;
    }

    public void setMetadescription(final String metadescription) {
        this.metadescription = metadescription;
    }

    public List<MutablePair<String, String>> getDisplayTitles() {
        return displayTitles;
    }

    public void setDisplayTitles(final List<MutablePair<String, String>> displayTitles) {
        this.displayTitles = displayTitles;
    }

    public List<MutablePair<String, String>> getDisplayMetakeywords() {
        return displayMetakeywords;
    }

    public void setDisplayMetakeywords(final List<MutablePair<String, String>> displayMetakeywords) {
        this.displayMetakeywords = displayMetakeywords;
    }

    public List<MutablePair<String, String>> getDisplayMetadescriptions() {
        return displayMetadescriptions;
    }

    public void setDisplayMetadescriptions(final List<MutablePair<String, String>> displayMetadescriptions) {
        this.displayMetadescriptions = displayMetadescriptions;
    }

    public List<VoProductSku> getSku() {
        return sku;
    }

    public void setSku(final List<VoProductSku> sku) {
        this.sku = sku;
    }
}
