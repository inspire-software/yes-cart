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
     * Get product id.
     * @return product id.
     */
    @Override
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
     * Get the {@link Brand} of product.
     *
     * NOTE: this is search index value, which lower case
     *
     * @return {@link Brand} of product.
     */
    String getBrand();

    /**
     * Set {@link Brand} of product.
     *
     * NOTE: this is search index value, which lower case
     *
     * @param brand {@link Brand} of product.
     */
    void setBrand(String brand);


    /**
     * Get multi SKU flag.
     *
     * @return true if this product has multiple SKU
     */
    boolean isMultisku();

    /**
     * Get product default sku code.
     * @return product default sku code.
     */
    String getDefaultSkuCode();

    /**
     * Set product default sku code.
     * @param defaultSkuCode product default sku code.
     */
    void setDefaultSkuCode(String defaultSkuCode);

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
     * @param locale locale.
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
     * Set default image.
     * @param defaultImage default image.
     */
    void setDefaultImage(String defaultImage);


    /**
     * Get product name by specified locale.
     * @param locale given locale
     * @return product name.
     */
    String getType(final String locale);

    /**
     * Get product type.
     * @return product type.
     */
    String getType();

    /**
     * Set product type.
     * @param type product type.
     */
    void setType(String type);

    /**
     * Get product type.
     * @return product type.
     */
    String getDisplayType();

    /**
     * Set product type.
     * @param type product type.
     */
    void setDisplayType(String type);


    /**
     * Is this product type service.
     *
     * @return true is this product service
     */
    boolean isService();

    /**
     * Set this product type as service. For example - Gift wrap
     *
     * @param service service flag to set.
     */
    void setService(boolean service);

    /**
     * Is this product type ensemble.
     *
     * @return true if ensemble
     */
    boolean isEnsemble();

    /**
     * Set product type to ensemble
     *
     * @param ensemble true is ensemble
     */
    void setEnsemble(boolean ensemble);

    /**
     * Is this product type can be shipped
     *
     * @return true if product shippable
     */
    boolean isShippable();


    /**
     * Set product type to shippable.
     *
     * @param shippable true if shippable
     */
    void setShippable(boolean shippable);

    /**
     * Is product digital.
     *
     * @return true if product digital.
     */
    boolean isDigital();

    /**
     * Set digital flag to product.
     *
     * @param digital flag to set
     */
    void setDigital(boolean digital);

    /**
     * Is digital product downloadable ?
     *
     * @return true in case if digital product can be downloaded.
     */
    boolean isDownloadable();

    /**
     * Set downloadable flag.
     *
     * @param downloadable flag to set.
     */
    void setDownloadable(boolean downloadable);


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
     * Set SKUs defined for this product.
     *
     * @param baseSkus list of SKU
     */
    void setBaseSkus(Map<Long, ProductSkuSearchResultDTO> baseSkus);

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
     * Stored attributes container.
     *
     * @param attributes stored attributes
     */
    void setAttributes(StoredAttributesDTO attributes);


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
    ProductSearchResultDTO copy();

}
