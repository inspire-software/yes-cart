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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.domain.i18n.I18NModel;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Represent product from search result. The whole entity usage id overhead.
 */
public interface ProductSkuSearchResultDTO extends Identifiable {

    /**
     * Get product SKU id.
     *
     * @return product SKU id.
     */
    @Override
    long getId();

    /**
     * Get product SKU id.
     *
     * @return product SKU id.
     */
    long getProductId();

    /**
     * Get product SKU code.
     *
     * @return product SKU code.
     */
    String getCode();

    /**
     * Get the non unique product code.
     *
     * @return product code.
     */
    String getManufacturerCode();

    /**
     * Fulfilment center for given search result.
     *
     * @return fulfilment centre code for this result
     */
    String getFulfilmentCentreCode();

    /**
     * Get the space separated product tags. For example
     * sale specialoffer, newarrival etc.
     *
     * This tags should not be shown to customer, just for query navigation.
     *
     * @return space separated product tags
     */
    String getTag();

    /**
     * Get product name by specified locale.
     *
     * @param locale given locale
     *
     * @return product name.
     */
    String getName(String locale);

    /**
     * Get product name.
     *
     * @return product name.
     */
    String getName();

    /**
     * Get display name, which is hold localization failover.
     *
     * @return display name.
     */
    I18NModel getDisplayName();

    /**
     * Get localized description.
     *
     * @param locale locale.
     *
     * @return localized description
     */
    String getDescription(String locale);

    /**
     * Get product description.
     *
     * @return product description.
     */
    String getDescription();

    /**
     * Get localized raw value
     *
     * @return localized raw value
     */
    I18NModel getDisplayDescription();


    /**
     * Get default image, under IMAGE0 attribute.
     * @return default image.
     */
    String getDefaultImage();

    /**
     * Stored attributes container.
     *
     * @return stored attributes
     */
    StoredAttributesDTO getAttributes();


    /**
     * Get start of product availability.
     * Null - product has not start date, means no limitation.
     *
     * Note that search result does not have isDisabled() because it would not be part of the index if it was
     *
     * @return start of product availability.
     */
    LocalDateTime getAvailablefrom();

    /**
     * Get end of product availability.
     * Null - product has not end date, means no limitation.
     *
     * Note that search result does not have isDisabled() because it would not be part of the index if it was
     *
     * @return end of product availability.
     */
    LocalDateTime getAvailableto();

    /**
     * Get product release date.
     * Null - product has no release date (alrady available).
     *
     * @return product release date.
     */
    LocalDateTime getReleaseDate();

    /**
     * Set product availability. See ProductEntity fields for more details.
     *
     * @return product availability.
     */
    int getAvailability();

    /**
     * Get restock date.
     *
     * @return product restock date.
     */
    LocalDateTime getRestockDate();

    /**
     * Get restock note.
     *
     * @param locale locale
     *
     * @return restock note
     */
    String getRestockNote(String locale);

    /**
     * Get restock notes.
     *
     * @return restock notes
     */
    I18NModel getRestockNotes();

    /**
     * Get available quantity on warehouses.
     *
     * @return available qty on all warehouses.
     */
    BigDecimal getQtyOnWarehouse(long shop);


    /**
     * Get the featured flag for product.
     *
     * @return set featured flag.
     */
    boolean isFeatured();


    /**
     * Get minimal quantity for order. E.g. 5.0 means customer can only buy 5 or more.
     *
     * @return minimal order quantity
     */
    BigDecimal getMinOrderQuantity();

    /**
     * Get maximum quantity for order. E.g. 5.0 means customer can only buy up to 5
     *
     * @return maximum order quantity
     */
    BigDecimal getMaxOrderQuantity();

    /**
     * Get step quantity for order. E.g. 5.0 means customer can only buy in batches of 5 - 5, 10, 15
     * but not say 11.
     *
     * @return step order quantity
     */
    BigDecimal getStepOrderQuantity();


    /**
     * @return created timestamp.
     */
    Instant getCreatedTimestamp();

    /**
     * @return updated timestamp.
     */
    Instant getUpdatedTimestamp();

    /**
     * Creates copy of this object
     *
     * @return copy
     */
    ProductSkuSearchResultDTO copy();

}
