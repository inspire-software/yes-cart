/*
 * Copyright 2009 Inspire-Software.com
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

import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.dto.StoredAttributesDTO;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 04/12/2014
 * Time: 00:03
 */
public class ProductSkuSearchResultDTOImpl implements ProductSkuSearchResultDTO {

    private long id;
    private long productId;
    private String code;
    private String manufacturerCode;
    private String fulfilmentCentreCode;
    private String name;
    private StringI18NModel displayName;
    private String description;
    private StringI18NModel displayDescription;
    private String defaultImage;

    private StoredAttributesDTO attributes;

    private Instant createdTimestamp;
    private Instant updatedTimestamp;

    private LocalDateTime availablefrom;
    private LocalDateTime availableto;
    private LocalDateTime releaseDate;
    private int availability;
    private Map<Long, BigDecimal> qtyOnWarehouse;

    private LocalDateTime restockDate;
    private StringI18NModel restockNotes;

    private boolean featured;
    private BigDecimal minOrderQuantity;
    private BigDecimal maxOrderQuantity;
    private BigDecimal stepOrderQuantity;
    private String tag;


    /** {@inheritDoc} */
    @Override
    public String getDefaultImage() {
        return defaultImage;
    }

    // JSON serialization accessor
    public void setDefaultImage(final String defaultImage) {
        this.defaultImage = defaultImage;
    }


    /** {@inheritDoc} */
    @Override
    public long getId() {
        return id;
    }

    // JSON serialization accessor
    public void setId(final long id) {
        this.id = id;
    }

    /** {@inheritDoc} */
    @Override
    public long getProductId() {
        return productId;
    }

    // JSON serialization accessor
    public void setProductId(final long productId) {
        this.productId = productId;
    }

    /** {@inheritDoc} */
    @Override
    public String getCode() {
        return code;
    }

    // JSON serialization accessor
    public void setCode(final String code) {
        this.code = code;
    }

    /** {@inheritDoc} */
    @Override
    public String getManufacturerCode() {
        return manufacturerCode;
    }

    // JSON serialization accessor
    public void setManufacturerCode(final String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    /** {@inheritDoc} */
    @Override
    public String getFulfilmentCentreCode() {
        return fulfilmentCentreCode;
    }

    // JSON serialization accessor
    public void setFulfilmentCentreCode(final String fulfilmentCentreCode) {
        this.fulfilmentCentreCode = fulfilmentCentreCode;
    }

    /** {@inheritDoc} */
    @Override
    public StringI18NModel getDisplayName() {
        return displayName;
    }

    // JSON serialization accessor
    public void setDisplayName(final StringI18NModel displayName) {
        this.displayName = displayName;
    }


    /** {@inheritDoc} */
    @Override
    public String getName(final String locale) {
        if (this.displayName != null) {
            final String name = this.displayName.getValue(locale);
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

    // JSON accessor
    public void setName(final String name) {
        this.name = name;
    }



    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return description;
    }

    // JSON accessor
    public void setDescription(final String description) {
        this.description = description;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription(final String locale) {
        if (this.displayDescription != null) {
            final String desc = this.displayDescription.getValue(locale);
            if (desc != null) {
                return desc;
            }
        }
        return this.description;
    }


    /** {@inheritDoc} */
    @Override
    public StringI18NModel getDisplayDescription() {
        return displayDescription;
    }

    // JSON accessor
    public void setDisplayDescription(final StringI18NModel displayDescription) {

        this.displayDescription = displayDescription;

    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getQtyOnWarehouse(final long shopId) {
        return qtyOnWarehouse != null ? qtyOnWarehouse.get(shopId) : null;
    }

    // JSON serialization accessor
    public Map<Long, BigDecimal> getQtyOnWarehouse() {
        return qtyOnWarehouse;
    }

    // JSON accessor
    public void setQtyOnWarehouse(final Map<Long, BigDecimal> qtyOnWarehouse) {
        this.qtyOnWarehouse = qtyOnWarehouse;
    }

    /** {@inheritDoc} */
    @Override
    public LocalDateTime getAvailableto() {
        return availableto;
    }

    // JSON accessor
    public void setAvailableto(final LocalDateTime availableto) {
        this.availableto = availableto;
    }

    /** {@inheritDoc} */
    @Override
    public LocalDateTime getAvailablefrom() {
        return availablefrom;
    }

    // JSON accessor
    public void setAvailablefrom(final LocalDateTime availablefrom) {
        this.availablefrom = availablefrom;
    }

    /** {@inheritDoc} */
    @Override
    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    // JSON accessor
    public void setReleaseDate(final LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    /** {@inheritDoc} */
    @Override
    public int getAvailability() {
        return availability;
    }

    // JSON accessor
    public void setAvailability(final int availability) {
        this.availability = availability;
    }

    /** {@inheritDoc} */
    @Override
    public LocalDateTime getRestockDate() {
        return restockDate;
    }

    // JSON serialization accessor
    public void setRestockDate(final LocalDateTime restockDate) {
        this.restockDate = restockDate;
    }

    // JSON serialization accessor
    public StringI18NModel getRestockNotes() {
        return restockNotes;
    }

    // JSON serialization accessor
    public void setRestockNotes(final StringI18NModel restockNotes) {
        this.restockNotes = restockNotes;
    }

    /** {@inheritDoc} */
    @Override
    public String getRestockNote(final String locale) {
        if (this.restockNotes != null) {
            return this.restockNotes.getValue(locale);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFeatured() {
        return featured;
    }

    // JSON accessor
    public void setFeatured(final boolean featured) {
        this.featured = featured;
    }

    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return tag;
    }

    // JSON accessor
    public void setTag(final String tag) {
        this.tag = tag;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getMinOrderQuantity() {
        return minOrderQuantity;
    }

    // JSON accessor
    public void setMinOrderQuantity(final BigDecimal minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getMaxOrderQuantity() {
        return maxOrderQuantity;
    }

    // JSON accessor
    public void setMaxOrderQuantity(final BigDecimal maxOrderQuantity) {
        this.maxOrderQuantity = maxOrderQuantity;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getStepOrderQuantity() {
        return stepOrderQuantity;
    }

    // JSON accessor
    public void setStepOrderQuantity(final BigDecimal stepOrderQuantity) {
        this.stepOrderQuantity = stepOrderQuantity;
    }

    /** {@inheritDoc} */
    @Override
    public StoredAttributesDTO getAttributes() {
        return attributes;
    }

    // JSON accessor
    public void setAttributes(final StoredAttributesDTO attributes) {
        this.attributes = attributes;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    // JSON accessor
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    // JSON accessor
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public ProductSkuSearchResultDTOImpl copy() {

        final ProductSkuSearchResultDTOImpl copy = new ProductSkuSearchResultDTOImpl();
        copy.setId(this.id);
        copy.setProductId(this.productId);
        copy.setCode(this.code);
        copy.setManufacturerCode(this.manufacturerCode);
        copy.setFulfilmentCentreCode(this.fulfilmentCentreCode);
        copy.setName(this.name);
        if (this.displayName != null) {
            copy.setDisplayName(this.displayName.copy());
        }
        copy.setDescription(this.description);
        if (this.displayDescription != null) {
            copy.setDisplayDescription(this.displayDescription.copy());
        }
        copy.setDefaultImage(this.defaultImage);
        copy.setCreatedTimestamp(this.createdTimestamp);
        copy.setUpdatedTimestamp(this.updatedTimestamp);
        copy.setAvailablefrom(this.availablefrom);
        copy.setAvailableto(this.availableto);
        copy.setReleaseDate(this.releaseDate);
        copy.setRestockDate(this.restockDate);
        if (this.restockNotes != null) {
            copy.setRestockNotes(this.restockNotes.copy());
        }
        copy.setAvailability(this.availability);
        if (this.qtyOnWarehouse != null) {
            copy.setQtyOnWarehouse(new HashMap<>(this.qtyOnWarehouse));
        }
        copy.setFeatured(this.featured);
        copy.setMinOrderQuantity(this.minOrderQuantity);
        copy.setMaxOrderQuantity(this.maxOrderQuantity);
        copy.setStepOrderQuantity(this.stepOrderQuantity);
        copy.setTag(this.tag);
        if (this.attributes != null) {
            copy.setAttributes(new StoredAttributesDTOImpl(this.attributes.toString()));
        }
        return copy;
    }

    @Override
    public String toString() {
        return "ProductSkuSearchResultDTOImpl{" +
                "id=" + id +
                ", productId=" + productId +
                ", code='" + code + '\'' +
                ", fulfilmentCentreCode='" + fulfilmentCentreCode + '\'' +
                ", availablefrom=" + availablefrom +
                ", availableto=" + availableto +
                ", releaseDate=" + releaseDate +
                ", availability=" + availability +
                '}';
    }
}
