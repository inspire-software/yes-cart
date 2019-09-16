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
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.ro.xml.impl.I18nMapAdapter;
import org.yes.cart.domain.ro.xml.impl.LocalDateTimeAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 22/03/2015
 * Time: 15:29
 */
@Dto
@XmlRootElement(name = "sku-result")
public class ProductSkuSearchResultRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    @DtoField(readOnly = true)
    private long id;
    @DtoField(readOnly = true)
    private long productId;
    @DtoField(readOnly = true)
    private String code;
    @DtoField(readOnly = true)
    private String manufacturerCode;
    @DtoField(readOnly = true)
    private String fulfilmentCentreCode;
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
    private LocalDateTime availablefrom;
    @DtoField(readOnly = true)
    private LocalDateTime availableto;
    @DtoField(readOnly = true)
    private LocalDateTime releaseDate;
    @DtoField(readOnly = true)
    private int availability;
    private ProductAvailabilityModelRO skuAvailabilityModel;
    private ProductQuantityModelRO skuQuantityModel;
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

    @DtoField(readOnly = true, converter = "storedAttributesConverter")
    private List<ProductSearchResultAttributeRO> attributes;

    @XmlElement(name = "default-image")
    public String getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(final String defaultImage) {
        this.defaultImage = defaultImage;
    }

    @XmlElement(name = "sku-availability")
    public ProductAvailabilityModelRO getSkuAvailabilityModel() {
        return skuAvailabilityModel;
    }

    public void setSkuAvailabilityModel(final ProductAvailabilityModelRO skuAvailabilityModel) {
        this.skuAvailabilityModel = skuAvailabilityModel;
    }

    @XmlElement(name = "sku-quantity")
    public ProductQuantityModelRO getSkuQuantityModel() {
        return skuQuantityModel;
    }

    public void setSkuQuantityModel(final ProductQuantityModelRO skuQuantityModel) {
        this.skuQuantityModel = skuQuantityModel;
    }

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    public LocalDateTime getAvailableto() {
        return availableto;
    }

    public void setAvailableto(final LocalDateTime availableto) {
        this.availableto = availableto;
    }

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    public LocalDateTime getAvailablefrom() {
        return availablefrom;
    }

    public void setAvailablefrom(final LocalDateTime availablefrom) {
        this.availablefrom = availablefrom;
    }

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(final LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
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

    @XmlElement(name = "supplier-code")
    public String getFulfilmentCentreCode() {
        return fulfilmentCentreCode;
    }

    public void setFulfilmentCentreCode(final String fulfilmentCentreCode) {
        this.fulfilmentCentreCode = fulfilmentCentreCode;
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

}
