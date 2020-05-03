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

import org.yes.cart.domain.entity.Brand;
import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.domain.i18n.I18NModel;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Represent product from search result. The whole entity usage id overhead.
 */
public interface ProductSearchResultDTO extends Identifiable {

    /**
     * Get product code.
     * @return product code.
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
     * Get the {@link Brand} of product.
     *
     * NOTE: this is search index value, which lower case
     *
     * @return {@link Brand} of product.
     */
    String getBrand();

    /**
     * Get multi SKU flag.
     *
     * @return true if this product has multiple SKU
     */
    boolean isMultisku();

    /**
     * Get product default sku code.
     *
     * @return product default sku code.
     */
    String getDefaultSkuCode();

    /**
     * Get product name by specified locale.
     *
     * @param locale given locale
     *
     * @return product name.
     */
    String getName(final String locale);

    /**
     * Get product name.
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
     * Null - product has no release date (already available).
     *
     * @return product release date.
     */
    LocalDateTime getReleaseDate();

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
     * Set product availability. See ProductEntity fields for more details.
     * @return product availability.
     */
    int getAvailability();

    /**
     * Get available quantity on warehouses.
     * @return available qty on all warehouses.
     */
    Map<String, BigDecimal> getQtyOnWarehouse(long shop);

    /**
     * Get default image, under IMAGE0 attribute.
     * @return default image.
     */
    String getDefaultImage();

    /**
     * Get product type by specified locale.
     *
     * @param locale given locale
     *
     * @return product type.
     */
    String getType(final String locale);

    /**
     * Get product type.
     *
     * @return product type.
     */
    String getType();

    /**
     * Get product type.
     *
     * @return product type.
     */
    I18NModel getDisplayType();

    /**
     * Is this product type service.
     *
     * @return true is this product service
     */
    boolean isService();

    /**
     * Is this product configurable.
     *
     * @return true if configurable
     */
    boolean isConfigurable();

    /**
     * Is this product cannot be sold separately.
     *
     * @return true if not sold separately
     */
    boolean isNotSoldSeparately();

    /**
     * Is this product type can be shipped
     *
     * @return true if product shippable
     */
    boolean isShippable();

    /**
     * Is product digital.
     *
     * @return true if product digital.
     */
    boolean isDigital();

    /**
     * Is digital product downloadable ?
     *
     * @return true in case if digital product can be downloaded.
     */
    boolean isDownloadable();


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
     * Get SKUs defined for this product.
     *
     * @return list of SKU
     */
    ProductSkuSearchResultDTO getBaseSku(long skuId);

    /**
     * Get SKUs defined for this product.
     *
     * @return SKUs
     */
    Map<Long, ProductSkuSearchResultDTO> getBaseSkus();

    /**
     * Get SKUs sorted by search priority from the FT search.
     *
     * @return list of SKU
     */
    List<ProductSkuSearchResultDTO> getSearchSkus();

    /**
     * Set SKUs sorted by search priority from the FT search.
     *
     * @param searchSkus list of SKU
     */
    void setSearchSkus(List<ProductSkuSearchResultDTO> searchSkus);

    /**
     * Stored attributes container.
     *
     * @return stored attributes
     */
    StoredAttributesDTO getAttributes();


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
    ProductSearchResultDTO copy();

}
