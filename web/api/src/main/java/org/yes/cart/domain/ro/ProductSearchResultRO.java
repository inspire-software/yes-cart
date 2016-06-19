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
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.dto.matcher.impl.NoopMatcher;
import org.yes.cart.domain.ro.xml.impl.I18nMapAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 22/03/2015
 * Time: 15:29
 */
@Dto
@XmlRootElement(name = "product-result")
public class ProductSearchResultRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    @DtoField(readOnly = true)
    private long id;
    @DtoField(readOnly = true)
    private String code;
    @DtoField(readOnly = true)
    private String manufacturerCode;
    @DtoField(readOnly = true)
    private boolean multisku;
    @DtoField(readOnly = true)
    private String defaultSkuCode;
    @DtoField(readOnly = true)
    private String name;
    @DtoField(readOnly = true, converter = "i18nStringConverter")
    private Map<String, String> displayName;
    @DtoField(readOnly = true)
    private String description;
    @DtoField(readOnly = true, converter = "i18nStringConverter")
    private Map<String, String> displayDescription;
    @DtoField(readOnly = true)
    private String tag;
    @DtoField(readOnly = true)
    private String brand;
    @DtoField(readOnly = true)
    private Date availablefrom;
    @DtoField(readOnly = true)
    private Date availableto;
    @DtoField(readOnly = true)
    private int availability;
    private ProductAvailabilityModelRO productAvailabilityModel;
    private SkuPriceRO price;
    @DtoField(readOnly = true)
    private String defaultImage;
    @DtoField(readOnly = true)
    private Boolean featured;
    @DtoField(readOnly = true)
    private BigDecimal minOrderQuantity;
    @DtoField(readOnly = true)
    private BigDecimal maxOrderQuantity;
    @DtoField(readOnly = true)
    private BigDecimal stepOrderQuantity;

    @DtoCollection(
            value = "skus",
            dtoBeanKey = "org.yes.cart.domain.ro.ProductSkuSearchResultRO",
            entityGenericType = ProductSkuSearchResultDTO.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private List<ProductSkuSearchResultRO> skus;

    @DtoField(readOnly = true, converter = "storedAttributesConverter")
    private List<ProductSearchResultAttributeRO> attributes;

    @DtoField(readOnly = true)
    private Date createdTimestamp;
    @DtoField(readOnly = true)
    private Date updatedTimestamp;


    @XmlElement(name = "default-image")
    public String getDefaultImage() {
        if (multisku && skus != null && !skus.isEmpty()) {
            // if this is multi SKU and we have relevancy list - use it
            return skus.get(0).getDefaultImage();
        }
        return defaultImage;
    }

    public void setDefaultImage(final String defaultImage) {
        this.defaultImage = defaultImage;
    }

    @XmlElement(name = "product-availability")
    public ProductAvailabilityModelRO getProductAvailabilityModel() {
        return productAvailabilityModel;
    }

    public void setProductAvailabilityModel(final ProductAvailabilityModelRO productAvailabilityModel) {
        this.productAvailabilityModel = productAvailabilityModel;
    }

    public Date getAvailableto() {
        return availableto;
    }

    public void setAvailableto(final Date availableto) {
        this.availableto = availableto;
    }

    public Date getAvailablefrom() {
        return availablefrom;
    }

    public void setAvailablefrom(final Date availablefrom) {
        this.availablefrom = availablefrom;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(final int availability) {
        this.availability = availability;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
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

    public boolean isMultisku() {
        return multisku;
    }

    public void setMultisku(final boolean multisku) {
        this.multisku = multisku;
    }

    @XmlElement(name = "default-sku-code")
    public String getDefaultSkuCode() {
        if (multisku && skus != null && !skus.isEmpty()) {
            // if this is multi SKU and we have relevancy list - use it
            return skus.get(0).getCode();
        }
        return defaultSkuCode;
    }

    public void setDefaultSkuCode(final String defaultSkuCode) {
        this.defaultSkuCode = defaultSkuCode;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-names")
    public Map<String, String> getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final Map<String, String> displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
    
    

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }


    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-descriptions")
    public Map<String, String> getDisplayDescription() {
        return displayDescription;
    }

    public void setDisplayDescription(final Map<String, String> displayDescription) {
        
        this.displayDescription = displayDescription;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(final String brand) {
        this.brand = brand;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(final Boolean featured) {
        this.featured = featured;
    }

    @XmlElement(name = "min-order-quantity")
    public BigDecimal getMinOrderQuantity() {
        return minOrderQuantity;
    }

    public void setMinOrderQuantity(final BigDecimal minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    @XmlElement(name = "max-order-quantity")
    public BigDecimal getMaxOrderQuantity() {
        return maxOrderQuantity;
    }

    public void setMaxOrderQuantity(final BigDecimal maxOrderQuantity) {
        this.maxOrderQuantity = maxOrderQuantity;
    }

    @XmlElement(name = "step-order-quantity")
    public BigDecimal getStepOrderQuantity() {
        return stepOrderQuantity;
    }

    public void setStepOrderQuantity(final BigDecimal stepOrderQuantity) {
        this.stepOrderQuantity = stepOrderQuantity;
    }

    @XmlElementWrapper(name = "skus")
    @XmlElement(name = "sku")
    public List<ProductSkuSearchResultRO> getSkus() {
        return skus;
    }

    public void setSkus(final List<ProductSkuSearchResultRO> skus) {
        this.skus = skus;
    }

    public SkuPriceRO getPrice() {
        return price;
    }

    public void setPrice(final SkuPriceRO price) {
        this.price = price;
    }

    @XmlElementWrapper(name = "attributes")
    @XmlElement(name = "attribute")
    public List<ProductSearchResultAttributeRO> getAttributes() {
        return attributes;
    }

    public void setAttributes(final List<ProductSearchResultAttributeRO> attributes) {
        this.attributes = attributes;
    }

    @XmlElement(name = "created-timestamp")
    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(final Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @XmlElement(name = "updated-timestamp")
    public Date getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(final Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }
}
