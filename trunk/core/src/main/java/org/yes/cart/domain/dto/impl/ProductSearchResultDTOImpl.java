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
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.math.BigDecimal;
import java.util.Date;
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

    private I18NModel i18NModelName;
    private I18NModel i18NModelDescription;


    /** {@inheritDoc} */
    public String getDefaultImage() {
        return defaultImage;
    }

    /** {@inheritDoc} */
    public void setDefaultImage(final String defaultImage) {
        this.defaultImage = defaultImage;
    }

    /** {@inheritDoc} */
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
    public String getDefaultSkuCode() {
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
    public ProductSearchResultDTO copy() {
        final ProductSearchResultDTOImpl copy = new ProductSearchResultDTOImpl();
        copy.setId(this.getId());
        copy.setCode(this.getCode());
        copy.setDefaultSkuCode(this.getDefaultSkuCode());
        copy.setName(this.getName());
        copy.setDisplayName(this.getDisplayName());
        copy.setDescription(this.getDescription());
        copy.setDisplayDescription(this.getDisplayDescription());
        copy.setAvailablefrom(this.getAvailablefrom());
        copy.setAvailableto(this.getAvailableto());
        copy.setAvailability(this.getAvailability());
        copy.setQtyOnWarehouse(this.getQtyOnWarehouse());
        copy.setDefaultImage(this.getDefaultImage());
        copy.setFeatured(this.getFeatured());
        return copy;
    }
}
