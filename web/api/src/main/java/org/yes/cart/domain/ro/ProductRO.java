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

package org.yes.cart.domain.ro;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.matcher.impl.NoopMatcher;
import org.yes.cart.domain.entity.AttrValueProduct;
import org.yes.cart.domain.entity.ProductOption;
import org.yes.cart.domain.ro.xml.impl.I18nMapAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 24/03/2015
 * Time: 10:44
 */
@Dto
@XmlRootElement(name = "product")
public class ProductRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    @DtoField(value = "productId", readOnly = true)
    private long productId;

    @DtoField(value = "code", readOnly = true)
    private String code;

    @DtoField(value = "manufacturerCode", readOnly = true)
    private String manufacturerCode;

    @DtoField(value = "tag", readOnly = true)
    private String tag;

    private String uitemplate;
    private String uitemplateFallback;

    private ProductAvailabilityModelRO productAvailabilityModel;

    private SkuPriceRO price;

    @DtoField(value = "brand.brandId", readOnly = true)
    private long brandId;

    // @DtoField(value = "brand.name", readOnly = true) <- Brand is lazy
    private String brandName;

    @DtoField(value = "producttype.producttypeId", readOnly = true)
    private long productTypeId;

    @DtoField(value = "producttype.name", readOnly = true)
    private String productTypeName;

    @DtoField(value = "producttype.displayName", converter = "i18nModelConverter", readOnly = true)
    private Map<String, String> productTypeDisplayNames;

    @DtoField(value = "producttype.service", readOnly = true)
    private boolean service;
    @DtoField(value = "producttype.shippable", readOnly = true)
    private boolean shippable;
    @DtoField(value = "producttype.digital", readOnly = true)
    private boolean digital;
    @DtoField(value = "producttype.downloadable", readOnly = true)
    private boolean downloadable;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "displayName", converter = "i18nModelConverter", readOnly = true)
    private Map<String, String> displayNames;

    @DtoField(value = "description", readOnly = true)
    private String description;

    @DtoField(value = "seo.uri", entityBeanKeys = "org.yes.cart.domain.entity.Seo", readOnly = true)
    private String uri;

    @DtoField(value = "seo.title", entityBeanKeys = "org.yes.cart.domain.entity.Seo", readOnly = true)
    private String title;

    @DtoField(value = "seo.metakeywords", entityBeanKeys = "org.yes.cart.domain.entity.Seo", readOnly = true)
    private String metakeywords;

    @DtoField(value = "seo.metadescription", entityBeanKeys = "org.yes.cart.domain.entity.Seo", readOnly = true)
    private String metadescription;

    @DtoField(value = "seo.displayTitle", converter = "i18nModelConverter", readOnly = true)
    private Map<String, String> displayTitles;

    @DtoField(value = "seo.displayMetakeywords", converter = "i18nModelConverter", readOnly = true)
    private Map<String, String> displayMetakeywords;

    @DtoField(value = "seo.displayMetadescription", converter = "i18nModelConverter", readOnly = true)
    private Map<String, String> displayMetadescriptions;


    @DtoCollection(
            value = "attributes",
            dtoBeanKey = "org.yes.cart.domain.ro.AttrValueProductRO",
            entityGenericType = AttrValueProduct.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private Collection<AttrValueProductRO> attributes;

    @DtoField(value = "options.configurable", readOnly = true)
    private boolean configurable;

    @DtoCollection(
            value = "options.configurationOption",
            dtoBeanKey = "org.yes.cart.domain.ro.ProductOptionRO",
            entityGenericType = ProductOption.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private Collection<ProductOptionRO> options;

    @DtoField(value = "notSoldSeparately", readOnly = true)
    private boolean notSoldSeparately;

    private List<ProductSkuRO> skus;

    @XmlElement(name = "product-id")
    public long getProductId() {
        return productId;
    }

    public void setProductId(final long productId) {
        this.productId = productId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    @XmlElement(name = "manufacturer-code")
    public String getManufacturerCode() {
        return manufacturerCode;
    }

    public void setManufacturerCode(final String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    public String getUitemplate() {
        return uitemplate;
    }

    public void setUitemplate(final String uitemplate) {
        this.uitemplate = uitemplate;
    }

    @XmlElement(name = "uitemplate-fallback")
    public String getUitemplateFallback() {
        return uitemplateFallback;
    }

    public void setUitemplateFallback(final String uitemplateFallback) {
        this.uitemplateFallback = uitemplateFallback;
    }

    @XmlElement(name = "brand-id")
    public long getBrandId() {
        return brandId;
    }

    public void setBrandId(final long brandId) {
        this.brandId = brandId;
    }

    @XmlElement(name = "brand-name")
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(final String brandName) {
        this.brandName = brandName;
    }

    @XmlElement(name = "product-type-id")
    public long getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(final long productTypeId) {
        this.productTypeId = productTypeId;
    }

    @XmlElement(name = "product-type-name")
    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(final String productTypeName) {
        this.productTypeName = productTypeName;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "product-type-display-names")
    public Map<String, String> getProductTypeDisplayNames() {
        return productTypeDisplayNames;
    }

    public void setProductTypeDisplayNames(final Map<String, String> productTypeDisplayNames) {
        this.productTypeDisplayNames = productTypeDisplayNames;
    }


    @XmlAttribute(name = "type-service")
    public boolean isService() {
        return service;
    }

    public void setService(final boolean service) {
        this.service = service;
    }

    @XmlAttribute(name = "type-configurable")
    public boolean isConfigurable() {
        return configurable;
    }

    public void setConfigurable(final boolean configurable) {
        this.configurable = configurable;
    }

    @XmlAttribute(name = "type-shippable")
    public boolean isShippable() {
        return shippable;
    }

    public void setShippable(final boolean shippable) {
        this.shippable = shippable;
    }

    @XmlAttribute(name = "type-digital")
    public boolean isDigital() {
        return digital;
    }

    public void setDigital(final boolean digital) {
        this.digital = digital;
    }

    @XmlAttribute(name = "type-downloadable")
    public boolean isDownloadable() {
        return downloadable;
    }

    public void setDownloadable(final boolean downloadable) {
        this.downloadable = downloadable;
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

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-titles")
    public Map<String, String> getDisplayTitles() {
        return displayTitles;
    }

    public void setDisplayTitles(final Map<String, String> displayTitles) {
        this.displayTitles = displayTitles;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-metakeywords")
    public Map<String, String> getDisplayMetakeywords() {
        return displayMetakeywords;
    }

    public void setDisplayMetakeywords(final Map<String, String> displayMetakeywords) {
        this.displayMetakeywords = displayMetakeywords;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-metadescription")
    public Map<String, String> getDisplayMetadescriptions() {
        return displayMetadescriptions;
    }

    public void setDisplayMetadescriptions(final Map<String, String> displayMetadescriptions) {
        this.displayMetadescriptions = displayMetadescriptions;
    }

    @XmlElementWrapper(name = "attribute-values")
    @XmlElement(name = "attribute-value")
    public Collection<AttrValueProductRO> getAttributes() {
        return attributes;
    }

    public void setAttributes(final Collection<AttrValueProductRO> attributes) {
        this.attributes = attributes;
    }

    @XmlElementWrapper(name = "product-options")
    @XmlElement(name = "product-option")
    public Collection<ProductOptionRO> getOptions() {
        return options;
    }

    public void setOptions(final Collection<ProductOptionRO> options) {
        this.options = options;
    }

    @XmlElement(name = "not-sold-separately")
    public boolean isNotSoldSeparately() {
        return notSoldSeparately;
    }

    public void setNotSoldSeparately(final boolean notSoldSeparately) {
        this.notSoldSeparately = notSoldSeparately;
    }

    @XmlElement(name = "product-availability")
    public ProductAvailabilityModelRO getProductAvailabilityModel() {
        return productAvailabilityModel;
    }

    public void setProductAvailabilityModel(final ProductAvailabilityModelRO productAvailabilityModel) {
        this.productAvailabilityModel = productAvailabilityModel;
    }


    public SkuPriceRO getPrice() {
        return price;
    }

    public void setPrice(final SkuPriceRO price) {
        this.price = price;
    }

    @XmlElementWrapper(name = "skus")
    @XmlElement(name = "sku")
    public List<ProductSkuRO> getSkus() {
        return skus;
    }

    public void setSkus(final List<ProductSkuRO> skus) {
        this.skus = skus;
    }
}
