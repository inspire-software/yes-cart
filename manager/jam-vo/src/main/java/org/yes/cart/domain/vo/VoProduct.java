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
import org.yes.cart.domain.dto.ProductOptionDTO;
import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.matcher.NoopMatcher;
import org.yes.cart.domain.vo.matcher.VoProductCategoryMatcher;
import org.yes.cart.domain.vo.matcher.VoProductSkuMatcher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @DtoField(value = "pimDisabled")
    private boolean pimDisabled;

    @DtoField(value = "pimOutdated")
    private boolean pimOutdated;

    @DtoField(value = "pimUpdated")
    private Instant pimUpdated;

    @DtoField(value = "tag")
    private String tag;

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

    @DtoField(value = "notSoldSeparately")
    private boolean notSoldSeparately;

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

    @DtoField(value = "configurable")
    private boolean configurable;

    @DtoCollection(
            value = "configurationOptions",
            dtoBeanKey = "VoProductOption",
            entityGenericType = ProductOptionDTO.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private List<VoProductOption> configurationOptions;


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

    public boolean isPimDisabled() {
        return pimDisabled;
    }

    public void setPimDisabled(final boolean pimDisabled) {
        this.pimDisabled = pimDisabled;
    }

    public boolean isPimOutdated() {
        return pimOutdated;
    }

    public void setPimOutdated(final boolean pimOutdated) {
        this.pimOutdated = pimOutdated;
    }

    public Instant getPimUpdated() {
        return pimUpdated;
    }

    public void setPimUpdated(final Instant pimUpdated) {
        this.pimUpdated = pimUpdated;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
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

    public boolean isNotSoldSeparately() {
        return notSoldSeparately;
    }

    public void setNotSoldSeparately(final boolean notSoldSeparately) {
        this.notSoldSeparately = notSoldSeparately;
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

    public boolean isConfigurable() {
        return configurable;
    }

    public void setConfigurable(final boolean configurable) {
        this.configurable = configurable;
    }

    public List<VoProductOption> getConfigurationOptions() {
        return configurationOptions;
    }

    public void setConfigurationOptions(final List<VoProductOption> configurationOptions) {
        this.configurationOptions = configurationOptions;
    }
}
