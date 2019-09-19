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
import java.util.Map;

/**
 * Represent product from search result. The whole entity usage id overhead.
 */
public interface ProductSkuSearchResultDTO extends Identifiable {

    /**
     * Get product SKU id.
     * @return product SKU id.
     */
    @Override
    long getId();

    /**
     * Set product id.
     * @param id product id.
     */
    void setId(long id);

    /**
     * Get product SKU id.
     * @return product SKU id.
     */
    long getProductId();

    /**
     * Set product id.
     * @param id product id.
     */
    void setProductId(long id);

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
     * Get the non unique product code.
     *
     * @return product code.
     */
    String getManufacturerCode();

    /**
     * Manufacturer non unique product code.
     * Limitation code must not contains underscore
     *
     * @param code manufacturer code
     */
    void setManufacturerCode(String code);

    /**
     * Fulfilment center for given search result.
     *
     * @return fulfilment centre code for this result
     */
    String getFulfilmentCentreCode();

    /**
     * Fulfilment center for given search result.
     *
     * @param code fulfilment centre code for this result
     */
    void setFulfilmentCentreCode(String code);

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
     * Set space separated product tags.
     *
     * @param tag space separated product tags.
     */
    void setTag(String tag);

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
     * @return product description.
     */
    String getDescription();

    /**
     * Set product description.
     * @param description product description.
     */
    void setDescription(String description);



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
     * Get localized description.
     *
     * @param locale locale.
     *
     * @return localized description
     */
    String getDescription(String locale);


    /**
     * Get localized raw value
     * @return localized raw value
     */
    String getDisplayDescription();

    /**
     * Set raw localized value for description.
     *
     * @param displayDescription localized raw value
     */
    void setDisplayDescription(String displayDescription);


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
     * Stored attributes container.
     *
     * @return stored attributes
     */
    StoredAttributesDTO getAttributes();

    /**
     * Stored attributes container.
     *
     * @param attributes stored attributes
     */
    void setAttributes(StoredAttributesDTO attributes);


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
     * Set start of product availability.
     *
     * @param availablefrom start of product availability.
     */
    void setAvailablefrom(LocalDateTime availablefrom);

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
     * Set end of product availability.
     *
     * @param availableto end of product availability.
     */
    void setAvailableto(LocalDateTime availableto);

    /**
     * Get product release date.
     * Null - product has no release date (alrady available).
     *
     * @return product release date.
     */
    LocalDateTime getReleaseDate();

    /**
     * Set product release date.
     *
     * @param releaseDate when product is to be released.
     */
    void setReleaseDate(LocalDateTime releaseDate);

    /**
     * Set product availability. See ProductEntity fields for more details.
     * @return product availability.
     */
    int getAvailability();

    /**
     * Set product availability.
     * @param availability product availability.
     */
    void setAvailability(int availability);

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
     * @return available qty on all warehouses.
     */
    BigDecimal getQtyOnWarehouse(long shop);

    /**
     * Set available qty on all warehouses.
     * @param qty quantity.
     */
    void setQtyOnWarehouse(Map<Long, BigDecimal> qty);


    /**
     * Get the featured flag for product.
     *
     * @return set featured flag.
     */
    boolean isFeatured();

    /**
     * Set product featured flag.
     *
     * @param featured featured flag.
     */
    void setFeatured(boolean featured);


    /**
     * Get minimal quantity for order. E.g. 5.0 means customer can only buy 5 or more.
     *
     * @return minimal order quantity
     */
    BigDecimal getMinOrderQuantity();

    /**
     * @param minOrderQuantity minimal quantity for order.
     */
    void setMinOrderQuantity(BigDecimal minOrderQuantity);

    /**
     * Get maximum quantity for order. E.g. 5.0 means customer can only buy up to 5
     *
     * @return maximum order quantity
     */
    BigDecimal getMaxOrderQuantity();

    /**
     * @param maxOrderQuantity maximum quantity for order.
     */
    void setMaxOrderQuantity(BigDecimal maxOrderQuantity);

    /**
     * Get step quantity for order. E.g. 5.0 means customer can only buy in batches of 5 - 5, 10, 15
     * but not say 11.
     *
     * @return step order quantity
     */
    BigDecimal getStepOrderQuantity();

    /**
     * @param stepOrderQuantity step quantity for order.
     */
    void setStepOrderQuantity(BigDecimal stepOrderQuantity);


    /**
     * @return created timestamp.
     */
    Instant getCreatedTimestamp();

    /**
     * @param createdTimestamp set created timestamp.
     */
    void setCreatedTimestamp(Instant createdTimestamp);

    /**
     * @return updated timestamp.
     */
    Instant getUpdatedTimestamp();

    /**
     * @param updatedTimestamp set updated timestamp.
     */
    void setUpdatedTimestamp(Instant updatedTimestamp);

    /**
     * Creates copy of this object
     *
     * @return copy
     */
    ProductSkuSearchResultDTO copy();

}
