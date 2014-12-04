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

import org.apache.commons.lang.ObjectUtils;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
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
    private boolean multisku;
    private String defaultSkuCode;
    private String name;
    private String displayName;
    private String description;
    private String displayDescription;
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
        return (String) ObjectUtils.defaultIfNull(
                this.i18NModelName.getValue(locale),
                name
        );
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
        return (String) ObjectUtils.defaultIfNull(
                this.i18NModelDescription.getValue(locale),
                description
        );
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
    public ProductSearchResultDTO copy() {
        /*
            DO NOT copy skus as this must be set through FT search - we need them to be
            sorted by relevancy
         */
        final ProductSearchResultDTOImpl copy = new ProductSearchResultDTOImpl();
        copy.setId(this.id);
        copy.setCode(this.code);
        copy.setMultisku(this.multisku);
        copy.setDefaultSkuCode(this.defaultSkuCode);
        copy.setName(this.name);
        copy.setDisplayName(this.displayName);
        copy.setDescription(this.description);
        copy.setDisplayDescription(this.displayDescription);
        copy.setAvailablefrom(this.availablefrom);
        copy.setAvailableto(this.availableto);
        copy.setAvailability(this.availability);
        copy.setQtyOnWarehouse(this.qtyOnWarehouse);
        copy.setDefaultImage(this.defaultImage);
        copy.setFeatured(this.featured);
        copy.setMaxOrderQuantity(this.maxOrderQuantity);
        copy.setMinOrderQuantity(this.minOrderQuantity);
        copy.setStepOrderQuantity(this.stepOrderQuantity);
        return copy;
    }
}
