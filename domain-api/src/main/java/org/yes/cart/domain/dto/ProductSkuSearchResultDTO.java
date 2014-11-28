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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Represent product from search result. The whole entity usage id overhead.
 */
public interface ProductSkuSearchResultDTO extends Identifiable {

    /**
     * Get product SKU id.
     * @return product SKU id.
     */
    long getId();

    /**
     * Set product id.
     * @param id product id.
     */
    void setId(long id);

    /**
     * Get product SKU code.
     * @return product SKU code.
     */
    String getCode();

    /**
     * Set product SKU code.
     * @param code product SKU code.
     */
    void setCode(String code);

    /**
     * Get product name by specified locale.
     * @param locale given locale
     * @return product name.
     */
    String getName(final String locale);

    /**
     * Get product name.
     * @return product name.
     */
    String getName();

    /**
     * Set product name.
     * @param name product name.
     */
    void setName(String name);

    /**
     * Get default image, under IMAGE0 attribute.
     * @return default image.
     */
    String getDefaultImage();

    /**
     * Set default image.
     * @param defaultImage default image.
     */
    void setDefaultImage(String defaultImage);

    /**
     * Get display name, which is hold localization failover.
     * @return display name.
     */
    String getDisplayName();

    /**
     * Set display name.
     * @param displayName display name.
     */
    void setDisplayName(String displayName);

    /**
     * Creates copy of this object
     *
     * @return copy
     */
    ProductSkuSearchResultDTO copy();

}
