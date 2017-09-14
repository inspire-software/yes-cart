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

import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.dto.StoredAttributesDTO;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.math.BigDecimal;
import java.util.Date;
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
    private String tag;
    private String brand;
    private Date availablefrom;
    private Date availableto;
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

    private Date createdTimestamp;
    private Date updatedTimestamp;


    /** {@inheritDoc} */
    public String getDefaultImage() {
        if (multisku && skus != null && !skus.isEmpty()) {
            // if this is multi SKU and we have relevancy list - use it
            return skus.get(0).getDefaultImage();
        }
        return defaultImage;
    }

    /** {@inheritDoc} */
    public void setDefaultImage(final String defaultImage) {
        this.defaultImage = defaultImage;
    }

    /** {@inheritDoc} */
    public Map<String, BigDecimal> getQtyOnWarehouse(final long shopId) {
        return qtyOnWarehouse != null ? qtyOnWarehouse.get(shopId) : null;
    }

    // JSON serialization accessor
    public Map<Long, Map<String, BigDecimal>> getQtyOnWarehouse() {
        return qtyOnWarehouse;
    }

    /** {@inheritDoc} */
    public void setQtyOnWarehouse(final Map<Long, Map<String, BigDecimal>> qtyOnWarehouse) {
        this.qtyOnWarehouse = qtyOnWarehouse;
    }

    /** {@inheritDoc} */
    public Date getAvailableto() {
        return availableto;
    }

    /** {@inheritDoc} */
    public void setAvailableto(final Date availableto) {
        this.availableto = availableto;
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
    public int getAvailability() {
        return availability;
    }

    /** {@inheritDoc} */
    public void setAvailability(final int availability) {
        this.availability = availability;
    }

    /** {@inheritDoc} */
    public long getId() {
        return id;
    }

    /** {@inheritDoc} */
    public void setId(final long id) {
        this.id = id;
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
    public String getManufacturerCode() {
        return manufacturerCode;
    }

    /** {@inheritDoc} */
    public void setManufacturerCode(final String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    /** {@inheritDoc} */
    public String getFulfilmentCentreCode() {
        return fulfilmentCentreCode;
    }

    /** {@inheritDoc} */
    public void setFulfilmentCentreCode(final String fulfilmentCentreCode) {
        this.fulfilmentCentreCode = fulfilmentCentreCode;
    }

    /** {@inheritDoc} */
    public boolean isMultisku() {
        return multisku;
    }

    /** {@inheritDoc} */
    public void setMultisku(final boolean multisku) {
        this.multisku = multisku;
    }

    /** {@inheritDoc} */
    public String getDefaultSkuCode() {
        if (multisku && skus != null && !skus.isEmpty()) {
            // if this is multi SKU and we have relevancy list - use it
            return skus.get(0).getCode();
        }
        return defaultSkuCode;
    }

    /** {@inheritDoc} */
    public void setDefaultSkuCode(final String defaultSkuCode) {
        this.defaultSkuCode = defaultSkuCode;
    }

    /** {@inheritDoc} */
    public String getDisplayName() {
        return displayName;
    }

    /** {@inheritDoc} */
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
        this.i18NModelName = new StringI18NModel(this.displayName);
    }


    /** {@inheritDoc} */
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
    public String getDisplayDescription() {
        return displayDescription;
    }

    /** {@inheritDoc} */
    public void setDisplayDescription(final String displayDescription) {
        
        this.displayDescription = displayDescription;

        i18NModelDescription = new StringI18NModel(this.displayDescription);
    }

    /** {@inheritDoc} */
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
    public String getType() {
        return type;
    }

    /** {@inheritDoc} */
    public void setType(final String type) {
        this.type = type;
    }

    /** {@inheritDoc} */
    public String getDisplayType() {
        return displayType;
    }

    /** {@inheritDoc} */
    public void setDisplayType(final String displayType) {

        this.displayType = displayType;

        i18NModelType = new StringI18NModel(this.displayType);

    }

    /** {@inheritDoc} */
    public String getTag() {
        return tag;
    }

    /** {@inheritDoc} */
    public void setTag(final String tag) {
        this.tag = tag;
    }

    /** {@inheritDoc} */
    public String getBrand() {
        return brand;
    }

    /** {@inheritDoc} */
    public void setBrand(final String brand) {
        this.brand = brand;
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
    public BigDecimal getMinOrderQuantity() {
        return minOrderQuantity;
    }

    /** {@inheritDoc} */
    public void setMinOrderQuantity(final BigDecimal minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    /** {@inheritDoc} */
    public BigDecimal getMaxOrderQuantity() {
        return maxOrderQuantity;
    }

    /** {@inheritDoc} */
    public void setMaxOrderQuantity(final BigDecimal maxOrderQuantity) {
        this.maxOrderQuantity = maxOrderQuantity;
    }

    /** {@inheritDoc} */
    public BigDecimal getStepOrderQuantity() {
        return stepOrderQuantity;
    }

    /** {@inheritDoc} */
    public void setStepOrderQuantity(final BigDecimal stepOrderQuantity) {
        this.stepOrderQuantity = stepOrderQuantity;
    }

    /** {@inheritDoc} */
    public List<ProductSkuSearchResultDTO> getSkus() {
        return skus;
    }

    /** {@inheritDoc} */
    public void setSkus(final List<ProductSkuSearchResultDTO> skus) {
        this.skus = skus;
    }

    /** {@inheritDoc} */
    public StoredAttributesDTO getAttributes() {
        return attributes;
    }

    /** {@inheritDoc} */
    public void setAttributes(final StoredAttributesDTO attributes) {
        this.attributes = attributes;
    }

    /** {@inheritDoc} */
    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    /** {@inheritDoc} */
    public void setCreatedTimestamp(final Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /** {@inheritDoc} */
    public Date getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    /** {@inheritDoc} */
    public void setUpdatedTimestamp(final Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    /** {@inheritDoc} */
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
