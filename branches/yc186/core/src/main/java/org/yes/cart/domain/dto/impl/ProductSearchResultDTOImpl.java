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

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Collection;

/**
 * Implement search result dto item.
 * User: igor
 * Date: 01.06.13
 * Time: 13:15
 */
public class ProductSearchResultDTOImpl implements ProductSearchResultDTO {

    private long id;
    private String code;
    private String name;
    private String description;

    private int availability;

    private BigDecimal qtyOnWarehouse;

    private String firstAvailableSkuCode;
    private BigDecimal firstAvailableSkuQuantity;

    private I18NModel i18NModel;
    private String defaultImage;

    public String getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(String defaultImage) {
        this.defaultImage = defaultImage;
    }

    public String getFirstAvailableSkuCode() {
        return firstAvailableSkuCode;
    }

    public void setFirstAvailableSkuCode(String firstAvailableSkuCode) {
        this.firstAvailableSkuCode = firstAvailableSkuCode;
    }

    public BigDecimal getFirstAvailableSkuQuantity() {
        return firstAvailableSkuQuantity;
    }

    public void setFirstAvailableSkuQuantity(BigDecimal firstAvailableSkuQuantity) {
        this.firstAvailableSkuQuantity = firstAvailableSkuQuantity;
    }

    public BigDecimal getQtyOnWarehouse() {
        return qtyOnWarehouse;
    }

    public void setQtyOnWarehouse(BigDecimal qtyOnWarehouse) {
        this.qtyOnWarehouse = qtyOnWarehouse;
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

    public String getName(final String locale) {
        return (String) ObjectUtils.defaultIfNull(this.i18NModel.getValue(locale), name);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
        this.i18NModel = new StringI18NModel(name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
