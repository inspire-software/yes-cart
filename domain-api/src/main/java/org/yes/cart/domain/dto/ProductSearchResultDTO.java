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

import java.math.BigDecimal;

/**
 * Represent product from search result. The whole entity usage id overhead.
 */
public interface ProductSearchResultDTO {

    /**
     * Get product id.
     * @return product id.
     */
    long getId();

    /**
     * Set product id.
     * @param id product id.
     */
    void setId(long id);

    /**
     * Get product code.
     * @return product code.
     */
    String getCode();

    /**
     * Set product code.
     * @param code product code.
     */
    void setCode(String code);

    /**
     * Get product name by specified locale.
     * @param locale givem locale
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
     * Get product description.
     * @return
     */
    String getDescription();

    /**
     * Set product description.
     * @param description product description.
     */
    void setDescription(String description);

    /**
     * Set producr availalility. See ProductEntity fields for more details.
     * @return product availability.
     */
    int getAvailability();

    /**
     * Set product availability.
     * @param availability product availability.
     */
    void setAvailability(int availability);

    /**
     * Get availabale quantity on warehouses.
     * @return available qty on all warehouses.
     */
    BigDecimal getQtyOnWarehouse();

    /**
     * Set available qty on all warehouses.
     * @param qty quantity.
     */
    void setQtyOnWarehouse(BigDecimal qty);

    /**
     * Get first available available sku code. In case of multisku products some of particular sku may be unavailable.
     * @return first available sku code.
     */
    String getFirstAvailableSkuCode();

    /**
     * Set first available sku code.
     * @param firstAvailableSkuCode first available sku code.
     */
    void setFirstAvailableSkuCode(String firstAvailableSkuCode);

    /**
     * Get price of  first available sku code.
     * @return price of  first available sku code.
     */
    BigDecimal getFirstAvailableSkuQuantity();

    /**
     * Set price of  first available sku code.
     * @param firstAvailableSkuQuantity price of  first available sku code.
     */
    void setFirstAvailableSkuQuantity(BigDecimal firstAvailableSkuQuantity);

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


}
