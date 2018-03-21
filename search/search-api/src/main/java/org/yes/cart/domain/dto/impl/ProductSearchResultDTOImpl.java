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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.dto.StoredAttributesDTO;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Implement search result dto item.
 * User: igor
 * Date: 01.06.13
 * Time: 13:15
 */
public class ProductSearchResultDTOImpl implements ProductSearchResultDTO {

    private long id;
    private String code;
    private String manufacturerCode;
    private String fulfilmentCentreCode;
    private boolean multisku;
    private String defaultSkuCode;
    private String name;
    private String displayName;
    private String description;
    private String displayDescription;
    private String type;
    private String displayType;
    private int typeMask;
    private String tag;
    private String brand;
    private LocalDateTime availablefrom;
    private LocalDateTime availableto;
    private int availability;
    private Map<Long, Map<String, BigDecimal>> qtyOnWarehouse;
    private String defaultImage;
    private Boolean featured;
    private BigDecimal minOrderQuantity;
    private BigDecimal maxOrderQuantity;
    private BigDecimal stepOrderQuantity;

    // This is dependent of FT search, so not part of the copy()
    private List<ProductSkuSearchResultDTO> skus;

    private I18NModel i18NModelName;
    private I18NModel i18NModelDescription;
    private I18NModel i18NModelType;

    private StoredAttributesDTO attributes;

    private Instant createdTimestamp;
    private Instant updatedTimestamp;


    /** {@inheritDoc} */
    @Override
    public String getDefaultImage() {
        if (multisku && skus != null && !skus.isEmpty()) {
            // if this is multi SKU and we have relevancy list - use it
            return skus.get(0).getDefaultImage();
        }
        return defaultImage;
    }

    /** {@inheritDoc} */
    @Override
    public void setDefaultImage(final String defaultImage) {
        this.defaultImage = defaultImage;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, BigDecimal> getQtyOnWarehouse(final long shopId) {
        return qtyOnWarehouse != null ? qtyOnWarehouse.get(shopId) : null;
    }

    // JSON serialization accessor
    public Map<Long, Map<String, BigDecimal>> getQtyOnWarehouse() {
        return qtyOnWarehouse;
    }

    /** {@inheritDoc} */
    @Override
    public void setQtyOnWarehouse(final Map<Long, Map<String, BigDecimal>> qtyOnWarehouse) {
        this.qtyOnWarehouse = qtyOnWarehouse;
    }

    /** {@inheritDoc} */
    @Override
    public LocalDateTime getAvailableto() {
        return availableto;
    }

    /** {@inheritDoc} */
    @Override
    public void setAvailableto(final LocalDateTime availableto) {
        this.availableto = availableto;
    }

    /** {@inheritDoc} */
    @Override
    public LocalDateTime getAvailablefrom() {
        return availablefrom;
    }

    /** {@inheritDoc} */
    @Override
    public void setAvailablefrom(final LocalDateTime availablefrom) {
        this.availablefrom = availablefrom;
    }

    /** {@inheritDoc} */
    @Override
    public int getAvailability() {
        return availability;
    }

    /** {@inheritDoc} */
    @Override
    public void setAvailability(final int availability) {
        this.availability = availability;
    }

    /** {@inheritDoc} */
    @Override
    public long getId() {
        return id;
    }

    /** {@inheritDoc} */
    @Override
    public void setId(final long id) {
        this.id = id;
    }

    /** {@inheritDoc} */
    @Override
    public String getCode() {
        return code;
    }

    /** {@inheritDoc} */
    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    /** {@inheritDoc} */
    @Override
    public String getManufacturerCode() {
        return manufacturerCode;
    }

    /** {@inheritDoc} */
    @Override
    public void setManufacturerCode(final String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    /** {@inheritDoc} */
    @Override
    public String getFulfilmentCentreCode() {
        return fulfilmentCentreCode;
    }

    /** {@inheritDoc} */
    @Override
    public void setFulfilmentCentreCode(final String fulfilmentCentreCode) {
        this.fulfilmentCentreCode = fulfilmentCentreCode;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMultisku() {
        return multisku;
    }

    /** {@inheritDoc} */
    @Override
    public void setMultisku(final boolean multisku) {
        this.multisku = multisku;
    }

    /** {@inheritDoc} */
    @Override
    public String getDefaultSkuCode() {
        if (multisku && skus != null && !skus.isEmpty()) {
            // if this is multi SKU and we have relevancy list - use it
            return skus.get(0).getCode();
        }
        return defaultSkuCode;
    }

    /** {@inheritDoc} */
    @Override
    public void setDefaultSkuCode(final String defaultSkuCode) {
        this.defaultSkuCode = defaultSkuCode;
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return displayName;
    }

    /** {@inheritDoc} */
    @Override
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
        this.i18NModelName = new StringI18NModel(this.displayName);
    }


    /** {@inheritDoc} */
    @Override
    public String getName(final String locale) {
        if (this.i18NModelName != null) {
            final String name = this.i18NModelName.getValue(locale);
            if (name != null) {
                return name;
            }
        }
        return this.name;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public void setName(final String name) {
        this.name = name;
    }
    
    

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription(final String locale) {
        if (this.i18NModelDescription != null) {
            final String desc = this.i18NModelDescription.getValue(locale);
            if (desc != null) {
                return desc;
            }
        }
        return this.description;
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayDescription() {
        return displayDescription;
    }

    /** {@inheritDoc} */
    @Override
    public void setDisplayDescription(final String displayDescription) {
        
        this.displayDescription = displayDescription;

        i18NModelDescription = new StringI18NModel(this.displayDescription);
    }

    /** {@inheritDoc} */
    @Override
    public String getType(final String locale) {
        if (this.i18NModelType != null) {
            final String type = this.i18NModelType.getValue(locale);
            if (type != null) {
                return type;
            }
        }
        return this.type;
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return type;
    }

    /** {@inheritDoc} */
    @Override
    public void setType(final String type) {
        this.type = type;
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayType() {
        return displayType;
    }

    /** {@inheritDoc} */
    @Override
    public void setDisplayType(final String displayType) {

        this.displayType = displayType;

        i18NModelType = new StringI18NModel(this.displayType);

    }

    // JSON accessor
    public int getTypeMask() {
        return typeMask;
    }

    public void setTypeMask(final int typeMask) {
        this.typeMask = typeMask;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public boolean isService() {
        return (this.typeMask & 2) != 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setService(final boolean service) {
        this.typeMask = service ? this.typeMask | 2 : this.typeMask & ~ 2;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public boolean isEnsemble() {
        return (this.typeMask & 4) != 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setEnsemble(final boolean ensemble) {
        this.typeMask = ensemble ? this.typeMask | 4 : this.typeMask & ~ 4;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public boolean isShippable() {
        return (this.typeMask & 8) != 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setShippable(final boolean shippable) {
        this.typeMask = shippable ? this.typeMask | 8 : this.typeMask & ~ 8;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public boolean isDigital() {
        return (this.typeMask & 16) != 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setDigital(final boolean digital) {
        this.typeMask = digital ? this.typeMask | 16 : this.typeMask & ~ 16;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public boolean isDownloadable() {
        return (this.typeMask & 32) != 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setDownloadable(final boolean downloadable) {
        this.typeMask = downloadable ? this.typeMask | 32 : this.typeMask & ~ 32;
    }

    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return tag;
    }

    /** {@inheritDoc} */
    @Override
    public void setTag(final String tag) {
        this.tag = tag;
    }

    /** {@inheritDoc} */
    @Override
    public String getBrand() {
        return brand;
    }

    /** {@inheritDoc} */
    @Override
    public void setBrand(final String brand) {
        this.brand = brand;
    }

    /** {@inheritDoc} */
    @Override
    public Boolean getFeatured() {
        return featured;
    }

    /** {@inheritDoc} */
    @Override
    public void setFeatured(final Boolean featured) {
        this.featured = featured;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getMinOrderQuantity() {
        return minOrderQuantity;
    }

    /** {@inheritDoc} */
    @Override
    public void setMinOrderQuantity(final BigDecimal minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getMaxOrderQuantity() {
        return maxOrderQuantity;
    }

    /** {@inheritDoc} */
    @Override
    public void setMaxOrderQuantity(final BigDecimal maxOrderQuantity) {
        this.maxOrderQuantity = maxOrderQuantity;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getStepOrderQuantity() {
        return stepOrderQuantity;
    }

    /** {@inheritDoc} */
    @Override
    public void setStepOrderQuantity(final BigDecimal stepOrderQuantity) {
        this.stepOrderQuantity = stepOrderQuantity;
    }

    /** {@inheritDoc} */
    @Override
    public List<ProductSkuSearchResultDTO> getSkus() {
        return skus;
    }

    /** {@inheritDoc} */
    @Override
    public void setSkus(final List<ProductSkuSearchResultDTO> skus) {
        this.skus = skus;
    }

    /** {@inheritDoc} */
    @Override
    public StoredAttributesDTO getAttributes() {
        return attributes;
    }

    /** {@inheritDoc} */
    @Override
    public void setAttributes(final StoredAttributesDTO attributes) {
        this.attributes = attributes;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public ProductSearchResultDTO copy() {
        /*
            DO NOT copy skus as this must be set through FT search - we need them to be
            sorted by relevancy
         */
        final ProductSearchResultDTOImpl copy = new ProductSearchResultDTOImpl();
        copy.setId(this.id);
        copy.setCode(this.code);
        copy.setManufacturerCode(this.manufacturerCode);
        copy.setFulfilmentCentreCode(this.fulfilmentCentreCode);
        copy.setMultisku(this.multisku);
        copy.setDefaultSkuCode(this.defaultSkuCode);
        copy.setName(this.name);
        copy.setDisplayName(this.displayName);
        copy.setDescription(this.description);
        copy.setDisplayDescription(this.displayDescription);
        copy.setType(this.type);
        copy.setDisplayType(this.displayType);
        copy.setTypeMask(this.typeMask);
        copy.setTag(this.tag);
        copy.setBrand(this.brand);
        copy.setAvailablefrom(this.availablefrom);
        copy.setAvailableto(this.availableto);
        copy.setAvailability(this.availability);
        copy.setQtyOnWarehouse(this.qtyOnWarehouse);
        copy.setDefaultImage(this.defaultImage);
        copy.setFeatured(this.featured);
        copy.setMaxOrderQuantity(this.maxOrderQuantity);
        copy.setMinOrderQuantity(this.minOrderQuantity);
        copy.setStepOrderQuantity(this.stepOrderQuantity);
        copy.setCreatedTimestamp(this.createdTimestamp);
        copy.setUpdatedTimestamp(this.updatedTimestamp);
        if (this.attributes != null) {
            copy.setAttributes(new StoredAttributesDTOImpl(this.attributes.toString()));
        }
        return copy;
    }
}
